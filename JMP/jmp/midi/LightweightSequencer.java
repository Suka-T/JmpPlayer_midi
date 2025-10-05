package jmp.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiEventListener;
import jlib.midi.MappedParseFunc;
import jlib.midi.MidiByte;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.SoundManager;

public class LightweightSequencer implements Sequencer {
    public static enum ESeqMode {
        Normal, TickOnly, NonSound;

        @Override
        public String toString() {
            switch (this) {
                case Normal:
                    return "Normal";
                case TickOnly:
                    return "TickOnly";
                case NonSound:
                    return "NonSound";
                default:
                    return "";
            }
        }
    }

    public class PackedLongList {
        private int capacity = 16;
        private long[] data;
        private int size;

        public PackedLongList() {
            data = null;
        }

        public void add(int trk, int statusByte, int data1, int data2) {
            int packedMsg = MidiShortMessagePool.packShortMsg(statusByte, data1, data2);
            long packedAll = MidiShortMessagePool.packTrackMsg(trk, packedMsg);
            add(packedAll);
        }

        public void add(long value) {
            if (data == null) {
                data = new long[capacity];
            }

            if (size == data.length) {
                long[] newData = new long[data.length * 2];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            }
            data[size++] = value;
        }

        public long get(int index) {
            return data[index];
        }

        public int size() {
            return size;
        }
    }

    static final double EXTRACT_MIDI_USAGE = 0.3;
    private float tempoBPM = 120.0f;
    private int resolution = 480;
    private long tickPosition = 0;
    private long blockTick = 1000;

    private Sequence sequence;
    private Thread playThread;
    private Thread dumpThread;
    private Thread extractThread;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean paused = new AtomicBoolean(false);
    private long lastTimeNs = System.nanoTime();
    private double tickRemainder = 0.0;
    private boolean isOpen = false;

    private static final int BUSY_WAIT_COUNT = 100;
    private boolean isMidioutDump = false;
    private int midioutDumpCnt = 0;

    private MidiShortMessagePool shortMsgPool = new MidiShortMessagePool(50);
    private TreeMap<Long, Float> tempoChanges = new TreeMap<>();
    private TreeMap<Long, List<MidiEvent>> metaSysMap = new TreeMap<>();
    private Map<Long, PackedLongList> eventMap1 = new TreeMap<>();
    private Map<Long, PackedLongList> eventMap2 = new TreeMap<>();
    private Map<Long, PackedLongList> currentEventMap = null;
    private Map<Long, PackedLongList> offEventMap = null;
    private List<MetaEventListener> metaEventListeners = new ArrayList<MetaEventListener>();

    private LwTransmitter lwTransmitter = new LwTransmitter();
    private MidiMessagePump midiMsgPump;
    private ExtractWorker extractWorker;
    private ESeqMode seqMode = ESeqMode.Normal;

    private boolean ignoreNotesValidOfMonitor = false;
    private int ignoreNotesLowestOfMonitor = 0;
    private int ignoreNotesHighestOfMonitor = 0;

    private boolean ignoreNotesValidOfAudio = false;
    private int ignoreNotesLowestOfAudio = 0;
    private int ignoreNotesHighestOfAudio = 0;

    // seek移動中はフラグを建てることで各スレッドを動作しない制御する
    private boolean seekingFlag = false;

    private boolean toInvalidFlag = false;

    // MIDI抽出のRAM使用率
    private double usageExtractRam = 0.25;

    // Sequenceを削除するフラグ
    public void toInvalid() {
        stop();

        toInvalidFlag = true;
    }

    protected void toInvalidProcess() {
        if (toInvalidFlag == true) {
            tickPosition = 0;
            tempoChanges.clear(); // TreeMap<Long, Float>
            metaSysMap.clear();
            eventMap1.clear();
            eventMap2.clear();
            if (midiMsgPump != null) {
                midiMsgPump.reset();
            }
            sequence = null;
            toInvalidFlag = false;

            System.gc();
        }
    }

    public LightweightSequencer(ESeqMode seqMode) {
        this.seqMode = seqMode;
    }

    public void setIgnoreNotesVelocityOfMonitor(int lowest, int highest) {
        ignoreNotesValidOfMonitor = true;
        ignoreNotesLowestOfMonitor = lowest;
        ignoreNotesHighestOfMonitor = highest;
        if (ignoreNotesLowestOfMonitor < 0) {
            ignoreNotesLowestOfMonitor = 0;
        }
        else if (ignoreNotesLowestOfMonitor > 127) {
            ignoreNotesLowestOfMonitor = 127;
        }
        if (ignoreNotesHighestOfMonitor < 0) {
            ignoreNotesHighestOfMonitor = 0;
        }
        else if (ignoreNotesHighestOfMonitor > 127) {
            ignoreNotesHighestOfMonitor = 127;
        }
        if ((ignoreNotesLowestOfMonitor > ignoreNotesHighestOfMonitor) || (ignoreNotesLowestOfMonitor == 0 && ignoreNotesHighestOfMonitor == 0)) {
            ignoreNotesLowestOfMonitor = 0;
            ignoreNotesHighestOfMonitor = 0;
            ignoreNotesValidOfMonitor = false;
        }
    }

    public boolean isValidIgnoreNotesOfMonitor() {
        return ignoreNotesValidOfMonitor;
    }

    public boolean isGhostNotesOfMonitor(int velocity) {
        if (ignoreNotesValidOfMonitor == false) {
            return false;
        }

        if (ignoreNotesLowestOfMonitor <= velocity && velocity <= ignoreNotesHighestOfMonitor) {
            return true;
        }
        return false;
    }

    public void setIgnoreNotesVelocityOfAudio(int lowest, int highest) {
        ignoreNotesValidOfAudio = true;
        ignoreNotesLowestOfAudio = lowest;
        ignoreNotesHighestOfAudio = highest;
        if (ignoreNotesLowestOfAudio < 0) {
            ignoreNotesLowestOfAudio = 0;
        }
        else if (ignoreNotesLowestOfAudio > 127) {
            ignoreNotesLowestOfAudio = 127;
        }
        if (ignoreNotesHighestOfAudio < 0) {
            ignoreNotesHighestOfAudio = 0;
        }
        else if (ignoreNotesHighestOfAudio > 127) {
            ignoreNotesHighestOfAudio = 127;
        }
        if ((ignoreNotesLowestOfAudio > ignoreNotesHighestOfAudio) || (ignoreNotesLowestOfAudio == 0 && ignoreNotesHighestOfAudio == 0)) {
            ignoreNotesLowestOfAudio = 0;
            ignoreNotesHighestOfAudio = 0;
            ignoreNotesValidOfAudio = false;
        }
    }

    public boolean isValidIgnoreNotesOfAudio() {
        return ignoreNotesValidOfAudio;
    }

    public boolean isGhostNotesOfAudio(int velocity) {
        if (ignoreNotesValidOfAudio == false) {
            return false;
        }

        if (ignoreNotesLowestOfAudio <= velocity && velocity <= ignoreNotesHighestOfAudio) {
            return true;
        }
        return false;
    }

    private long calcBlockTick(double usage) {

        // 1秒のtick数を1ブロックを最小にする
        long minimum = (long) getTickPerSecond((int) getTempoInBPM(), 15.0) * 2;

        // tickLengthを基準に使用RAM指定から求める
        long total = (long) ((double) sequence.getTickLength() * usage);
        if (total < minimum) {
            // 最小ブロック数
            total = minimum;
        }
        return total / 2; // Map2つ分
    }

    public double getTickPerSecond(int bpm, double second) {
        if (sequence == null) {
            return 10000;
        }

        int tempo = 60_000_000 / bpm;
        int ticksPerQuarterNote = sequence.getResolution();
        return (second * 1_000_000.0 * (double) ticksPerQuarterNote) / (double) tempo;
    }

    private void extractMidiEvent(Map<Long, PackedLongList> map, long startTick, long endTick, double usage) {
        MappedSequence seq = (MappedSequence) this.sequence;
        map.clear();

        System.out.println("extract events : " + startTick + "-" + endTick);

        for (short trkIndex = 0; trkIndex < seq.getNumTracks(); trkIndex++) {
            final short index = trkIndex;

            try {
                seq.parse(index, new MappedParseFunc(startTick, endTick) {

                    @Override
                    public void sysexMessage(int trk, long tick, int statusByte, byte[] sysexData, int length) {
                        // try {
                        // SysexMessage sysex = new SysexMessage(statusByte,
                        // sysexData, length);
                        // results.get(trk).events.computeIfAbsent(tick, k
                        // -> new ArrayList<>()).add(new MidiEvent(sysex,
                        // tick));
                        // }
                        // catch (InvalidMidiDataException e) {
                        // e.printStackTrace();
                        // }

                    }

                    @Override
                    public void shortMessage(int trk, long tick, int statusByte, int data1, int data2) {
                        if (map.get(tick) == null) {
                            map.put(tick, new PackedLongList());
                        }
                        map.get(tick).add(trk, statusByte, data1, data2);
                    }

                    @Override
                    public void metaMessage(int trk, long tick, int type, byte[] metaData, int length) {
                        // try {
                        // MetaMessage meta = new MetaMessage(type,
                        // metaData, length);
                        // results.get(trk).events.computeIfAbsent(tick, k
                        // -> new ArrayList<>()).add(new MidiEvent(meta,
                        // tick));
                        // }
                        // catch (InvalidMidiDataException e) {
                        // e.printStackTrace();
                        // }
                    }

                    @Override
                    public boolean interrupt() {
                        if (toInvalidFlag == true) {
                            return true;
                        }
                        return super.interrupt();
                    }
                });
            }
            catch (Throwable e) {
                JMPCore.getSystemManager().errorHandle(e);
            }
        }
    }

    private boolean analyzing = false;
    private long tempMaxTick = 0;
    private long tempNotesCount = 0;
    private long tempCurFinTrk = 0;
    private long tempFinTrk = 0;
    private long tempBlockTick = 200000;
    private long tempStartTick = 0;
    private long tempEndTick = 0;

    // Analyze進捗確認用の関数
    public boolean isProgressNowAnalyzing() {
        return analyzing;
    }

    public long getProgressFinTrackNum() {
        return tempFinTrk;
    }

    public long getProgressNotesCount() {
        return tempNotesCount;
    }

    public long getProgressReadTick() {
        return tempStartTick;
    }

    private class AnalyzeThreadResult {
        public long startTick = 0;
        public long endTick = 0;
        public long maxTick = 0;
        public long notesCount = 0;
        public boolean finTrackState[];
        public int numOfTrack = 0;
        public AnalyzeThreadResult(int numOfTrack) {
            this.numOfTrack = numOfTrack;
            finTrackState = new boolean[this.numOfTrack];
            init();
        }
        
        public void init() {
            startTick = 0;
            endTick = 0;
            maxTick = 0;
            notesCount = 0;
            for (int j = 0; j < this.numOfTrack; j++) {
                finTrackState[j] = false;
            }
        }
    }
    
    private void analyzeSequence(MappedSequence seq) {

        seekingFlag = true;
        if (midiMsgPump != null && extractWorker != null) {
            while (midiMsgPump.isWait() == false || extractWorker.isWait() == false) {
                try {
                    System.out.println("mp = " + midiMsgPump.isWait() + " ew = " + extractWorker.isWait());
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    JMPCore.getSystemManager().errorHandle(e);
                }
            }
        }

        if (midiMsgPump == null) {
            midiMsgPump = new MidiMessagePump();
        }
        if (extractWorker == null) {
            extractWorker = new ExtractWorker();
        }

        tempoChanges.clear(); // TreeMap<Long, Float>
        metaSysMap.clear();
        eventMap1.clear();
        eventMap2.clear();

        System.gc();

        if (midiMsgPump != null) {
            midiMsgPump.reset();
        }

        currentEventMap = eventMap1;
        offEventMap = eventMap2;

        tempMaxTick = 0;
        tempNotesCount = 0;
        tempBlockTick = 200000;
        tempStartTick = 0;
        tempEndTick = tempStartTick + tempBlockTick;
        tempFinTrk = 0;
        analyzing = true;

        tempCurFinTrk = 0;

        int threadCount = 6;
        AnalyzeThreadResult[] analyzeResults = new AnalyzeThreadResult[threadCount];
        for (int i = 0; i < threadCount; i++) {
            analyzeResults[i] = new AnalyzeThreadResult(seq.getNumTracks());
        }
        boolean threadFinTrackStateTotal[] = new boolean[seq.getNumTracks()];
        
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount);

        do {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                analyzeResults[i].init();
                analyzeResults[i].startTick = tempStartTick;
                analyzeResults[i].endTick = tempEndTick;

                tempStartTick = tempEndTick + 1;
                tempEndTick += tempBlockTick;
            }
            
            Object thMutex = new Object();

            for (int thId = 0; thId < threadCount; thId++) {
                final int exThId = thId;
                futures.add(executor.submit(() -> {
                    final int executorId = exThId;
                    for (short trkIndex = 0; trkIndex < seq.getNumTracks(); trkIndex++) {

                        try {
                            seq.parse(trkIndex, new MappedParseFunc(analyzeResults[executorId].startTick, analyzeResults[executorId].endTick) {

                                @Override
                                public void calcTick(int trk, int tick) {
                                    if (analyzeResults[executorId].maxTick < tick) {
                                        analyzeResults[executorId].maxTick = tick;
                                    }
                                }

                                @Override
                                public void sysexMessage(int trk, long tick, int statusByte, byte[] sysexData, int length) {
                                    synchronized (thMutex) {
                                        try {
                                            metaSysMap.computeIfAbsent(tick, k -> new ArrayList<>())
                                                    .add(new MidiEvent(new SysexMessage(statusByte, sysexData, length), tick));
                                        }
                                        catch (InvalidMidiDataException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }

                                @Override
                                public void shortMessage(int trk, long tick, int statusByte, int data1, int data2) {
                                    int command = statusByte & 0xF0;
                                    if (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && data2 > 0) {
                                        analyzeResults[executorId].notesCount++;
                                    }
                                }

                                @Override
                                public void metaMessage(int trk, long tick, int type, byte[] metaData, int length) {
                                    synchronized (thMutex) {
                                        // テンポイベント検出
                                        if (type == 0x51) {
                                            if (length == 3) {
                                                int mpq = ((metaData[0] & 0xFF) << 16) | ((metaData[1] & 0xFF) << 8) | (metaData[2] & 0xFF);
                                                float bpm = 60_000_000f / mpq;
                                                tempoChanges.put(tick, bpm);
                                            }
                                        }
    
                                        try {
                                            metaSysMap.computeIfAbsent(tick, k -> new ArrayList<>())
                                                    .add(new MidiEvent(new MetaMessage(type, metaData, length), tick));
                                        }
                                        catch (InvalidMidiDataException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void end() {
                                }
                                
                                @Override
                                public void endTrack(int trk) {
                                    analyzeResults[executorId].finTrackState[trk] = true;
                                }

                                @Override
                                public boolean interrupt() {
                                    if (toInvalidFlag == true) {
                                        return true;
                                    }
                                    return super.interrupt();
                                }
                            });
                        }
                        catch (Throwable e) {
                            JMPCore.getSystemManager().errorHandle(e);
                        }
                    }
                    return executorId;
                }));
            }

            // 終了を待つ
            // 集約処理
            for (int j = 0; j < seq.getNumTracks(); j++) {
                threadFinTrackStateTotal[j] = false;
            }
            for (Future<Integer> f : futures) {
                try {
                    int fid = f.get();
                    AnalyzeThreadResult thResult = analyzeResults[fid];
                    if (tempMaxTick < thResult.maxTick) {
                        tempMaxTick = thResult.maxTick;
                    }
                    tempNotesCount += thResult.notesCount;
                    
                    for (int j = 0; j<seq.getNumTracks(); j++) {
                        if (thResult.finTrackState[j] == true) {
                            threadFinTrackStateTotal[j] = true;
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            
            tempCurFinTrk = 0;
            for (int j = 0; j<seq.getNumTracks(); j++) {
                if (threadFinTrackStateTotal[j] == true) {
                    tempCurFinTrk++;
                }
            }
            tempFinTrk = tempCurFinTrk;
        }
        while (tempFinTrk < seq.getNumTracks() && toInvalidFlag == false);
        
        executor.shutdown();

        seq.setTickLength(tempMaxTick);
        seq.setNumOfNotes(tempNotesCount);

        long roundNoteCount = tempNotesCount;
        if (roundNoteCount == 0) {
            roundNoteCount = (seq.getFileSize() - 2000) / 8; // ファイルサイズからおおよそのノーツ数を計算
        }
        if (roundNoteCount < 20000) {
            blockTick = tempMaxTick;
        }
        else {
            blockTick = calcBlockTick(usageExtractRam);
        }

        analyzing = false;
        seekingFlag = false;
        midiMsgPump.initialRead();
    }

    // 再生処理スレッド
    private void runLoop() {
        long nowNs = 0;
        long deltaUs = 0;
        double microPerQuarter = 0.0;
        double ticksDelta = 0.0;
        long ticksToAdvance = 0;
        long i = 0;
        while (running.get()) {
            try {
                toInvalidProcess();

                if (paused.get()) {
                    Thread.sleep(10);
                    continue;
                }

                nowNs = System.nanoTime();
                deltaUs = (nowNs - lastTimeNs) / 1000;
                lastTimeNs = nowNs;

                // Δtick = Δμ秒 / (テンポ（μs/拍） / resolution)
                microPerQuarter = 60_000_000.0 / tempoBPM;
                ticksDelta = (double) (deltaUs * resolution) / microPerQuarter;

                tickRemainder += ticksDelta;
                ticksToAdvance = (long) tickRemainder;
                tickRemainder -= ticksToAdvance;

                for (i = 0; i < ticksToAdvance; i++) {
                    if (tempoChanges.containsKey(tickPosition)) {
                        setTempoInBPM(tempoChanges.get(tickPosition));

                        // microPerQuarter を更新
                        microPerQuarter = 60_000_000.0 / tempoBPM;
                    }

                    if (i >= getTickLength()) {
                        stop();
                        break;
                    }
                    tickPosition++;
                }

                midiMsgPump.nextTick(tickPosition);
                if (isMidioutDump == true) {
                    // busy waitを軽減
                    while (!midiMsgPump.isWait()) {
                        LockSupport.parkNanos(500_000); // 0.5ms sleep相当
                    }
                }
                Thread.sleep(1);
            }
            catch (Throwable e) {
                JMPCore.getSystemManager().errorHandle(e);
            }
        }
    }

    protected void onTick(long tick) {

        // Note On / Off 処理
        PackedLongList events = currentEventMap.get(tick);
        if (events != null && lwTransmitter.getReceiver() != null) {
            int i = 0;
            for (; i < events.size(); i++) {
                try {
                    LightweightShortMessage msg = shortMsgPool.borrow(events.get(i));
                    sendMidiEvent(msg, -1); // 即時送信
                    shortMsgPool.release(msg);
                }
                catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                }
            }

            // 処理済みの tick を削除
            currentEventMap.remove(tick);
        }

    }

    protected void onSysMeta(long tick) {
        // MetaMsg 処理
        List<MidiEvent> events = metaSysMap.get(tick);
        if (events != null && lwTransmitter.getReceiver() != null) {
            MidiMessage msg = null;
            for (MidiEvent event : events) {
                msg = event.getMessage();
                if (msg instanceof MetaMessage) {
                    sendMetaEvent((MetaMessage) msg, -1); // 即時送信
                }
                else if (msg instanceof SysexMessage) {
                    sendSysExEvent((SysexMessage) msg, -1); // 即時送信
                }
            }
        }
    }

    private void sendMidiEvent(MidiMessage msg, int timeStamp) {
        SoundManager sm = JMPCore.getSoundManager();
        IMidiEventListener notesMonitor = (IMidiEventListener) sm.getNotesMonitor();
        if (notesMonitor != null) {
            notesMonitor.catchMidiEvent(msg, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
        }

        if (seqMode != ESeqMode.NonSound) {
            if (msg instanceof LightweightShortMessage) {
                LightweightShortMessage lm = (LightweightShortMessage) msg;
                if (lm.getCommand() == ShortMessage.NOTE_ON && lm.getData2() > 0) {
                    if (isGhostNotesOfAudio(lm.getData2()) == true) {
                        return;
                    }
                }
            }

            if (isMidioutDump == true) {
                // 音声出力がビジーと判断し、NoteONをスキップする
                return;
            }
            lwTransmitter.getReceiver().send(msg, timeStamp); // 即時送信
        }
    }

    private void sendSysExEvent(SysexMessage msg, int timeStamp) {
        SoundManager sm = JMPCore.getSoundManager();
        IMidiEventListener notesMonitor = (IMidiEventListener) sm.getNotesMonitor();
        if (notesMonitor != null) {
            notesMonitor.catchMidiEvent(msg, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
        }
        lwTransmitter.getReceiver().send(msg, timeStamp); // 即時送信
    }

    private void sendMetaEvent(MetaMessage msg, int timeStamp) {
        SoundManager sm = JMPCore.getSoundManager();
        IMidiEventListener notesMonitor = (IMidiEventListener) sm.getNotesMonitor();
        if (notesMonitor != null) {
            notesMonitor.catchMidiEvent(msg, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
        }

        for (MetaEventListener ml : metaEventListeners) {
            ml.meta(msg);
        }
    }

    private void allSoundOff() {
        if (lwTransmitter.getReceiver() == null) {
            return;
        }

        // 全チャンネルに All Sound Off (CC#120) を送る
        for (int channel = 0; channel < 16; channel++) {
            try {
                ShortMessage cc = new ShortMessage();
                cc.setMessage(ShortMessage.CONTROL_CHANGE, channel, 120, 0);
                sendMidiEvent(cc, -1);
            }
            catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 128; i++) {
                try {
                    ShortMessage ss = new ShortMessage();
                    ss.setMessage(ShortMessage.NOTE_OFF, channel, i, 0);
                    sendMidiEvent(ss, -1);
                }
                catch (InvalidMidiDataException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void open() {
        isOpen = true;
        midiMsgPump = new MidiMessagePump();
        extractWorker = new ExtractWorker();
    }

    @Override
    public void close() {
        running.set(false);
        if (playThread != null) {
            try {
                playThread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (dumpThread != null) {
            try {
                dumpThread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (extractThread != null) {
            try {
                extractThread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        playThread = null;
        dumpThread = null;
        extractThread = null;
        isOpen = false;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void start() {
        System.out.print("lwSequencer RUN ");
        System.out.println(getSeqMode().toString());
        isMidioutDump = false;
        midioutDumpCnt = 0;
        if (playThread != null) {
            if (paused.get()) {
                if (getTickLength() <= tickPosition) {
                    lastTimeNs = System.nanoTime();
                    tickRemainder = 0.0;
                }
                resume();
            }
            return;
        }
        else {
            playThread = new Thread(this::runLoop);
            playThread.setPriority(Thread.MAX_PRIORITY - 1);

            dumpThread = new Thread(midiMsgPump);
            dumpThread.setPriority(Thread.MAX_PRIORITY - 2);

            extractThread = new Thread(extractWorker);
            extractThread.setPriority(Thread.MAX_PRIORITY - 2);

            running.set(true);
            paused.set(false);
            lastTimeNs = System.nanoTime();
            tickRemainder = 0.0;

            playThread.start();
            dumpThread.start();
            midiMsgPump.reset();
            extractThread.start();
        }
    }

    @Override
    public void stop() {
        pause();

        allSoundOff();
    }

    @Override
    public void setTempoInBPM(float bpm) {
        this.tempoBPM = bpm;
    }

    @Override
    public float getTempoInBPM() {
        return tempoBPM;
    }

    public float getFirstTempoInBPM() {
        float bpm = 120.0f;
        if (tempoChanges.isEmpty() == true) {
            bpm = tempoChanges.get(tempoChanges.firstKey());
        }
        return bpm == 0.0f ? 120.0f : bpm;
    }

    @Override
    public long getTickPosition() {
        return tickPosition;
    }

    @Override
    public void setTickPosition(long tick) {
        seekingFlag = true;
        if (midiMsgPump != null) {
            midiMsgPump.reset(tick);
            midiMsgPump.initialRead();
        }

        this.tickPosition = tick;
        if (0 >= this.tickPosition) {
            tempoBPM = 120.0f;
        }
        lastTimeNs = System.nanoTime();
        tickRemainder = 0.0;

        allSoundOff();

        seekingFlag = false;
    }

    @Override
    public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException {
    }

    @Override
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
        this.resolution = sequence.getResolution();
        analyzeSequence((MappedSequence) sequence);
        this.tickPosition = 0;
    }

    @Override
    public Sequence getSequence() {
        return sequence;
    }

    @Override
    public boolean isRunning() {
        return running.get() && !paused.get();
    }

    // 一時停止用（Sequencer標準には pause/resume 無いので独自メソッド）
    public void pause() {
        if (running.get() && !paused.get()) {
            paused.set(true);
        }
    }

    public void resume() {
        if (running.get() && paused.get()) {
            paused.set(false);
            lastTimeNs = System.nanoTime();
            tickRemainder = 0.0;
        }
    }

    public long getMicrosecondsFromTick(long tick) {
        long totalMicroseconds = 0;
        long previousTick = 0;
        float currentTempoBPM = 120.0f; // 初期テンポ
        double currentMicroPerQuarter = 60_000_000.0 / currentTempoBPM;
        if (JMPFlags.NowLoadingFlag == true) {
            return 0;
        }

        for (Map.Entry<Long, Float> entry : tempoChanges.entrySet()) {
            long changeTick = entry.getKey();

            if (changeTick >= tick)
                break;

            long deltaTick = changeTick - previousTick;

            // μs = Δtick × μs/拍 ÷ resolution
            totalMicroseconds += (long) ((deltaTick * currentMicroPerQuarter) / resolution);

            // テンポを更新
            currentTempoBPM = entry.getValue();
            currentMicroPerQuarter = 60_000_000.0 / currentTempoBPM;
            previousTick = changeTick;
        }

        // 最後の区間の μs を追加
        long remainingTick = tick - previousTick;
        totalMicroseconds += (long) ((remainingTick * currentMicroPerQuarter) / resolution);

        return totalMicroseconds;
    }

    @Override
    public void setMicrosecondPosition(long microseconds) {
        unsupported();
    }

    @Override
    public long getMicrosecondPosition() {
        return getMicrosecondsFromTick(getTickPosition());
    }

    @Override
    public long getMicrosecondLength() {
        return getMicrosecondsFromTick(getTickLength());
    }

    // 以下のメソッドは未対応（必要に応じて実装）
    @Override
    public void recordEnable(Track track, int channel) {
        unsupported();
    }

    @Override
    public void recordDisable(Track track) {
        unsupported();
    }

    @Override
    public boolean getTrackMute(int track) {
        unsupported();
        return false;
    }

    @Override
    public void setTrackMute(int track, boolean mute) {
        unsupported();
    }

    @Override
    public boolean getTrackSolo(int track) {
        unsupported();
        return false;
    }

    @Override
    public void setTrackSolo(int track, boolean solo) {
        unsupported();
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public void startRecording() {
        unsupported();
    }

    @Override
    public void stopRecording() {
        unsupported();
    }

    @Override
    public void setLoopStartPoint(long tick) {
        unsupported();
    }

    @Override
    public long getLoopStartPoint() {
        return 0;
    }

    @Override
    public void setLoopEndPoint(long tick) {
        unsupported();
    }

    @Override
    public long getLoopEndPoint() {
        return 0;
    }

    @Override
    public void setLoopCount(int count) {
        unsupported();
    }

    @Override
    public int getLoopCount() {
        return 0;
    }

    @Override
    public void setMasterSyncMode(SyncMode mode) {
        unsupported();
    }

    @Override
    public SyncMode getMasterSyncMode() {
        return null;
    }

    @Override
    public SyncMode[] getMasterSyncModes() {
        return new SyncMode[0];
    }

    @Override
    public void setSlaveSyncMode(SyncMode mode) {
        unsupported();
    }

    @Override
    public SyncMode getSlaveSyncMode() {
        return null;
    }

    @Override
    public SyncMode[] getSlaveSyncModes() {
        return new SyncMode[0];
    }

    @Override
    public void setTempoInMPQ(float mpq) {
        unsupported();
    }

    @Override
    public float getTempoInMPQ() {
        return 0;
    }

    public int getResolution() {
        return resolution;
    }

    @Override
    public long getTickLength() {
        return sequence != null ? sequence.getTickLength() : 0;
    }

    @Override
    public boolean addMetaEventListener(MetaEventListener listener) {
        return metaEventListeners.add(listener);
    }

    @Override
    public void removeMetaEventListener(MetaEventListener listener) {
        metaEventListeners.remove(listener);
    }

    @Override
    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers) {
        unsupported();
        return new int[0];
    }

    @Override
    public int[] removeControllerEventListener(ControllerEventListener listener, int[] controllers) {
        unsupported();
        return new int[0];
    }

    private void unsupported() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Info getDeviceInfo() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public int getMaxReceivers() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public int getMaxTransmitters() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<Receiver> getReceivers() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        return lwTransmitter;
    }

    @Override
    public List<Transmitter> getTransmitters() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public void setTempoFactor(float factor) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public float getTempoFactor() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    private class LwTransmitter implements Transmitter {

        Receiver lwReceiver = null;

        private LwTransmitter() {

        }

        @Override
        public void setReceiver(Receiver receiver) {
            lwReceiver = receiver;
        }

        @Override
        public Receiver getReceiver() {
            return lwReceiver;
        }

        @Override
        public void close() {
            lwReceiver.close();
        }
    }

    private class ExtractWorker implements Runnable {
        private AtomicBoolean waitFlag = new AtomicBoolean(true);
        private long readBlockIndex = 0;

        ExtractWorker() {
            readBlockIndex = 0;
        }

        public boolean isWait() {
            return this.waitFlag.get();
        }

        public void read(long readIndex) {
            readBlockIndex = readIndex;
            waitFlag.set(false);
        }

        public void waitForRead() {
            while (waitFlag.get() == false) {
                try {
                    Thread.sleep(1); // waitFlagがfalseの間は待機
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    if (seqMode == ESeqMode.TickOnly) {
                        // Tickモードは音声出力しない
                        if (offEventMap.isEmpty() == false) {
                            offEventMap.clear();
                        }
                        Thread.sleep(100);
                        continue;
                    }

                    if (waitFlag.get() == false) {
                        offEventMap.clear();

                        long startTick = blockTick * readBlockIndex;
                        long endTick = (blockTick * (readBlockIndex + 1)) - 1;
                        if (startTick < 0) {
                            startTick = 0;
                        }
                        else if (getTickLength() < startTick) {
                            startTick = getTickLength();
                        }
                        if (endTick < 0) {
                            endTick = 0;
                        }
                        else if (getTickLength() < endTick) {
                            endTick = getTickLength();
                        }

                        if (endTick >= startTick) {
                            // conflict args
                            extractMidiEvent(offEventMap, startTick, endTick, EXTRACT_MIDI_USAGE);
                        }

                        waitFlag.set(true);
                    }
                    Thread.sleep(1);
                }
                catch (Throwable e) {
                    JMPCore.getSystemManager().errorHandle(e);
                }
            }
        }
    }

    private class MidiMessagePump implements Runnable {
        private AtomicBoolean waitFlag = new AtomicBoolean(true);
        private long curTickPosition = 0;
        private long nextTickPosition = 0;
        private long curBlockIndex = -1;

        MidiMessagePump() {
            curTickPosition = 0;
            nextTickPosition = 0;
            curBlockIndex = -1;
        }

        public boolean isWait() {
            return this.waitFlag.get();
        }

        void reset() {
            reset(0);
        }

        void reset(long tick) {
            if (seqMode != ESeqMode.TickOnly) {
                while (!waitFlag.get()) {
                    try {
                        Thread.sleep(1); // waitFlagがfalseの間は待機
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            waitFlag.set(true);
            curBlockIndex = -1;
            curTickPosition = tick;
            nextTickPosition = tick;
        }

        void nextTick(long tickPos) {
            nextTickPosition = tickPos;
            if (seqMode != ESeqMode.TickOnly) {
                if (waitFlag.get() == false) {
                    midioutDumpCnt++;
                    if (midioutDumpCnt >= BUSY_WAIT_COUNT) {
                        // 音声出力がビジーと判断
                        isMidioutDump = true;
                        System.out.println("!! midiout busy !!");
                    }
                }
                else {
                    midioutDumpCnt = 0;
                    isMidioutDump = false;
                }
            }
            waitFlag.set(false);
        }

        void initialRead() {
            if (curBlockIndex == -1) {
                curBlockIndex = 0;
                currentEventMap = eventMap1;
                offEventMap = eventMap2;
                currentEventMap.clear();

                long startTick = blockTick * curBlockIndex;
                long endTick = blockTick * (curBlockIndex + 1) - 1;
                if (startTick < 0) {
                    startTick = 0;
                }
                else if (getTickLength() < startTick) {
                    startTick = getTickLength();
                }
                if (endTick < 0) {
                    endTick = 0;
                }
                else if (getTickLength() < endTick) {
                    endTick = getTickLength();
                }

                if (seqMode != ESeqMode.TickOnly) {
                    if (endTick > startTick) {
                        // conflict args
                        extractMidiEvent(currentEventMap, startTick, endTick, EXTRACT_MIDI_USAGE);
                    }
                    extractWorker.read(curBlockIndex + 1);
                }
            }
        }

        @Override
        public void run() {
            long cur = 0;
            long next = 0;
            long t = 0;
            long blockIndex = -1;
            while (running.get()) {
                try {
                    if (seekingFlag == false) {
                        if (curBlockIndex != -1) {
                            if (curTickPosition != nextTickPosition) {
                                waitFlag.set(false);
                            }
                        }

                        if (waitFlag.get() == false) {
                            cur = curTickPosition;
                            next = nextTickPosition;

                            for (t = cur; t < next; t++) {
                                blockIndex = t / blockTick;
                                if (curBlockIndex == -1) {
                                    curBlockIndex = blockIndex;
                                    initialRead();
                                }
                                else {
                                    if (seqMode != ESeqMode.TickOnly) {
                                        if (blockIndex != curBlockIndex) {
                                            curBlockIndex = blockIndex;
                                            extractWorker.waitForRead();

                                            Map<Long, PackedLongList> temp = currentEventMap;
                                            currentEventMap = offEventMap;
                                            offEventMap = temp;
                                            extractWorker.read(curBlockIndex + 1);
                                        }
                                    }
                                }

                                onSysMeta(t);
                                if (seqMode != ESeqMode.TickOnly) {
                                    onTick(t);
                                }
                            }
                            curTickPosition = next;
                            if (nextTickPosition == next) {
                                waitFlag.set(true);
                            }
                        }
                    }
                    else {
                        // シーク中のリクエストは破棄
                        waitFlag.set(true);
                    }

                    // 過負荷を防ぐため、最小限のSleep
                    Thread.sleep(1);
                }
                catch (Throwable e) {
                    JMPCore.getSystemManager().errorHandle(e);
                }
            }
        }
    }

    public ESeqMode getSeqMode() {
        return seqMode;
    }

    public void setSeqMode(ESeqMode seqMode) {
        this.seqMode = seqMode;
    }

    public double getUsageExtractRam() {
        return usageExtractRam;
    }

    public void setUsageExtractRam(double usageExtractRam) {
        this.usageExtractRam = usageExtractRam;
    }
} /* LightweightSequencer class end */
