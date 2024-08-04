package jmp.player;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiEventListener;
import jlib.midi.MidiByte;
import jlib.player.Player;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.midi.JMPSequencer;
import jmp.midi.ReceiverWrapper;
import jmp.midi.TransmitterWrapper;
import jmp.midi.receiver.ReceiverCreator;
import jmp.midi.receiver.ReceiverFactory;
import jmp.midi.transmitter.TransmitterCreator;
import jmp.midi.transmitter.TransmitterFactory;

public class MidiPlayer extends Player {

    public class FastMetaMessage extends MetaMessage {

        private int nativeType = 0;
        private byte[] nativeData = null;
        private int nativeLength = 0;

        public FastMetaMessage() {
            super();
            updateNative();
        }

        public FastMetaMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
            super(type, data, length);
            updateNative();
        }

        public FastMetaMessage(MetaMessage mes) throws InvalidMidiDataException {
            super(mes.getType(), mes.getData(), mes.getData().length);
            updateNative();
        }

        private void updateNative() {
            // ネイティブ変数に格納する
            nativeType = super.getType();
            nativeData = Arrays.copyOf(super.getData(), super.getLength());
            nativeLength = super.getLength();

        }

        @Override
        public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
            super.setMessage(type, data, length);
            updateNative();
        }

        @Override
        public int getType() {
            return nativeType;
        }

        @Override
        public byte[] getData() {
            return nativeData;
        }

        @Override
        public int getLength() {
            return nativeLength;
        }

        @Override
        public Object clone() {
            MetaMessage mmes = (MetaMessage) super.clone();
            FastMetaMessage msg = null;
            try {
                msg = new FastMetaMessage(mmes);
            }
            catch (InvalidMidiDataException e) {
                msg = null;
            }
            return msg;
        }
    }

    public class FastShortMessage extends ShortMessage {
        private int nativeChannel = 0;
        private int nativeCommand = 0;
        private int nativeData1 = 0;
        private int nativeData2 = 0;

        public FastShortMessage() throws InvalidMidiDataException {
            super();
            updateNative();
        }

        public FastShortMessage(int status) throws InvalidMidiDataException {
            super(status);
            updateNative();
        }

        public FastShortMessage(int status, int data1, int data2) throws InvalidMidiDataException {
            super(status, data1, data2);
            updateNative();
        }

        public FastShortMessage(ShortMessage sMes) throws InvalidMidiDataException {
            super(sMes.getStatus(), sMes.getData1(), sMes.getData2());
            updateNative();
        }

        private void updateNative() {
            // ネイティブ変数に格納する
            nativeChannel = super.getChannel();
            nativeCommand = super.getCommand();
            nativeData1 = super.getData1();
            nativeData2 = super.getData2();
        }

        @Override
        public void setMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
            super.setMessage(command, channel, data1, data2);
            updateNative();
        }

        @Override
        public void setMessage(int status) throws InvalidMidiDataException {
            super.setMessage(status);
            updateNative();
        }

        @Override
        public void setMessage(int status, int data1, int data2) throws InvalidMidiDataException {
            super.setMessage(status, data1, data2);
            updateNative();
        }

        @Override
        protected void setMessage(byte[] data, int length) throws InvalidMidiDataException {
            super.setMessage(data, length);
            updateNative();
        }

        @Override
        public int getChannel() {
            return nativeChannel;
        }

        @Override
        public int getCommand() {
            return nativeCommand;
        }

        @Override
        public int getData1() {
            return nativeData1;
        }

        @Override
        public int getData2() {
            return nativeData2;
        }

        @Override
        public Object clone() {
            ShortMessage smes = (ShortMessage) super.clone();
            FastShortMessage msg = null;
            try {
                msg = new FastShortMessage(smes);
            }
            catch (InvalidMidiDataException e) {
                msg = null;
            }
            return msg;
        }
    }

    public class MidiInfo extends Player.Info {
        // public static final String PLAYER_MIDI_INFO_KEY_BPM = "BPM";

        public MidiInfo() {
            super();
            // put(PLAYER_MIDI_INFO_KEY_BPM, "");
        }

        @Override
        public void update() {
            super.update();
            // put(PLAYER_MIDI_INFO_KEY_BPM, (sequencer != null) ?
            // String.valueOf(sequencer.getTempoInBPM()) : "0.0");
        }
    }

    // MIDI Synth
    private JMPSequencer sequencer;

    private Receiver currentReceiver = null;

    private TransmitterWrapper transmitterWrapper = null;
    private ReceiverWrapper receiverWrapper = null;

    public Receiver getCurrentReciver() {
        return currentReceiver;
    };

    private Transmitter currentTransmitter = null;

    public Transmitter getCurrentTransmitter() {
        return currentTransmitter;
    };

    public MidiPlayer() {
    }

    public JMPSequencer getSequencer() {
        return sequencer;
    }

    private String getEncText(byte[] data) {
        String charcode = JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_LYRIC_CHARCODE);
        String str = "";
        try {
            str = new String(data, charcode);
        }
        catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    @Override
    public boolean open() {
        boolean result = true;
        // Midiシーケンサー取得
        try {
            sequencer = new JMPSequencer(MidiSystem.getSequencer(false));
            if (sequencer.isOpen() == true) {
                sequencer.close();
            }

            /* MIDIOUT用ReceiverオブジェクトをSequencerに設定 */
            receiverWrapper = new ReceiverWrapper();
            getSequencer().getTransmitter().setReceiver(receiverWrapper);

            /* MIDIIN用TransmitterオブジェクトをSequencerに設定 */
            transmitterWrapper = new TransmitterWrapper();
            transmitterWrapper.setConnectedReceiver(receiverWrapper);

            currentReceiver = receiverWrapper;
            currentTransmitter = transmitterWrapper;

            sequencer.addMetaEventListener(new MetaEventListener() {

                @Override
                public void meta(MetaMessage meta) {
                    if (MidiByte.COPYRIGHT_NOTICE.type == meta.getType()) {
                        // System.out.println(getEncText(meta.getData()));
                    }
                    else if (MidiByte.TEXT.type == meta.getType()) {
                        // System.out.println(getEncText(meta.getData()));
                    }
                    else if (MidiByte.LYRICS.type == meta.getType()) {
                        JMPCore.getWindowManager().getMainWindow().setLyric(getEncText(meta.getData()));
                    }
                    else if (MidiByte.END_OF_TRACK.type == meta.getType()) {
                        JMPCore.getWindowManager().getMainWindow().setLyric("");
                    }

                    IMidiEventListener l = (IMidiEventListener) JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
                    if (l != null) {
                        l.catchMidiEvent(meta, 0, IMidiEventListener.SENDER_MIDI_OUT);
                    }
                }
            });

            sequencer.open();
        }
        catch (MidiUnavailableException e) {
            result = false;
            return result;
        }
        return result;
    }

    private static final String NO_CACHE = "NO_CACHE";
    private String cachedMidiOutName = NO_CACHE;
    private String cachedMidiInName = NO_CACHE;

    public boolean updateMidiOut(String name) {
        if (cachedMidiOutName.equals(NO_CACHE) == false && cachedMidiOutName.equals(name) == true) {
            // 同名は処理しない
            return false;
        }

        boolean state = false;

        Receiver receiver = null;
        try {
            receiverWrapper.close();

            /* Receiverインスタンス生成 */
            ReceiverFactory factory = new ReceiverFactory();
            ReceiverCreator creator = factory.create(name);
            receiver = creator.getReciever();

            receiverWrapper.changeAbsReceiver(receiver);
            currentReceiver = receiverWrapper;

            cachedMidiOutName = new String(name);

            state = true;
        }
        catch (MidiUnavailableException e) {
            JMPFlags.Log.cprintln("## Midi OUT err", true);
            JMPFlags.Log.cprintln(function.Error.getPrintStackTrace(e), true);

            JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, "");
            cachedMidiOutName = NO_CACHE;
        }
        return state;
    }

    public boolean updateMidiIn(String name) {
        if (cachedMidiInName.equals(NO_CACHE) == false && cachedMidiInName.equals(name) == true) {
            // 同名は処理しない
            return false;
        }

        boolean state = false;

        Transmitter transmitter = null;
        try {
            transmitterWrapper.close();

            /* Transmitterインスタンス生成 */
            TransmitterFactory factory = new TransmitterFactory();
            TransmitterCreator creator = factory.create(name);
            transmitter = creator.getTransmitter();

            transmitterWrapper.changeAbsTransmitter(transmitter);
            currentTransmitter = transmitterWrapper;

            cachedMidiInName = new String(name);

            state = true;
        }
        catch (MidiUnavailableException e) {
            JMPFlags.Log.cprintln("## Midi IN err", true);
            JMPFlags.Log.cprintln(function.Error.getPrintStackTrace(e), true);

            JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIIN, "");
            cachedMidiInName = NO_CACHE;
        }
        return state;
    }

    @Override
    public boolean close() {
        if (sequencer != null) {
            if (sequencer.isRunning() == true || sequencer.isRecording() == true) {
                // 念のためシーケンサーを停止
                sequencer.stop();
            }

            // トランスミッター・レシーバーの接続を解除
            for (Receiver rec : sequencer.getReceivers()) {
                rec.close();
            }
            for (Transmitter trans : sequencer.getTransmitters()) {
                trans.close();
            }
            if (sequencer.isOpen() == true) {
                sequencer.close();
            }
            // sequencer = null;
        }
        return true;
    }

    @Override
    public void play() {
        if (isValid() == false) {
            return;
        }
        if (sequencer.getTickPosition() >= sequencer.getTickLength()) {
            // 最初から再生
            sequencer.setTickPosition(0);
        }
        sequencer.start();
    }

    @Override
    public void stop() {
        if (isValid() == false) {
            return;
        }
        sequencer.stop();
    }

    @Override
    public boolean isRunnable() {
        if (isValid() == false) {
            return false;
        }
        return sequencer.isRunning();
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        sequencer.setTickPosition(pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return sequencer.getTickPosition();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return sequencer.getTickLength();
    }

    @Override
    public boolean isValid() {
        if (sequencer == null) {
            return false;
        }

        if (sequencer.getSequence() == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        int time = (int) (sequencer.getMicrosecondPosition() / 1000 / 1000);
        return time;
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        int time = (int) (sequencer.getMicrosecondLength() / 1000 / 1000);
        return time;
    }

    @Override
    public void setVolume(float volume) {
        if (isValid() == false) {
            return;
        }
    }

    @Override
    public float getVolume() {
        if (isValid() == false) {
            return 0;
        }
        return 0;
    }

    @Override
    public boolean loadFile(File file) throws InvalidMidiDataException, IOException {
        if (sequencer == null) {
            return false;
        }
        loadMidiFile(file);

        Player.Info info = createInfo();
        info.put(Player.Info.PLAYER_ABS_INFO_KEY_FILENAME, file.getName());
        info.update();
        setInfo(info);

        return true;
    }

    public Player.Info createInfo() {
        Player.Info info = new MidiInfo();
        if (sequencer == null) {
            info.update();
            return info;
        }

        Sequence sequence = sequencer.getSequence();
        if (sequence == null) {
            info.update();
            return info;
        }

        String sDivisionType = "UNKNOWN";
        float divisionType = sequence.getDivisionType();
        if (divisionType == Sequence.PPQ) {
            sDivisionType = "PPQ";
        }
        else if (divisionType == Sequence.SMPTE_24) {
            sDivisionType = "SMPTE_24";
        }
        else if (divisionType == Sequence.SMPTE_25) {
            sDivisionType = "SMPTE_25";
        }
        else if (divisionType == Sequence.SMPTE_30) {
            sDivisionType = "SMPTE_30";
        }
        else if (divisionType == Sequence.SMPTE_30DROP) {
            sDivisionType = "SMPTE_30DROP";
        }
        info.put("Division Type", sDivisionType);
        info.put("Resolution", String.valueOf(sequence.getResolution()));
        info.put("Tick Length", String.valueOf(sequence.getTickLength()));
        info.put("Tracks", String.valueOf(sequence.getTracks().length));

        info.update();
        return info;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        if (sequencer == null) {
            return false;
        }
        outputMidiFile(file.getPath());
        return true;
    }

    /**
     * MIDIファイルをシーケンサーにロードする
     *
     * @param file
     *            MIDIファイル
     * @throws IOException
     * @throws InvalidMidiDataException
     * @throws Exception
     */
    public void loadMidiFile(File file) throws InvalidMidiDataException, IOException {
        Sequence seq = JMPCore.getSoundManager().getMidiToolkit().readMidiFile(file);
        loadMidiSequence(seq);
    }

    /**
     * MIDIファイルをシーケンサーにロードする
     *
     * @param path
     *            MIDIファイルパス
     * @throws IOException
     * @throws InvalidMidiDataException
     * @throws Exception
     */
    public void loadMidiFile(String path) throws InvalidMidiDataException, IOException {
        File f = new File(path);
        if (f.exists() == true) {
            loadMidiFile(f);
        }
        return;
    }

    public void loadMidiSequence(Sequence seq) throws InvalidMidiDataException {
        if (JMPFlags.UseHispeedMidiMessage == true) {
            /* 高速ショートメッセージ使用 */
            Sequence newSeq = new Sequence(seq.getDivisionType(), seq.getResolution());
            Track[] tracks = seq.getTracks();
            for (int i = 0; i < tracks.length; i++) {
                Track t = newSeq.createTrack();
                for (int j = 0; j < tracks[i].size(); j++) {
                    MidiEvent newEvent = null;
                    MidiEvent orgEvent = tracks[i].get(j);
                    if (orgEvent.getMessage() instanceof ShortMessage) {
                        newEvent = new MidiEvent(new FastShortMessage((ShortMessage) orgEvent.getMessage()), orgEvent.getTick());
                    }
                    else if (orgEvent.getMessage() instanceof MetaMessage) {
                        newEvent = new MidiEvent(new FastMetaMessage((MetaMessage) orgEvent.getMessage()), orgEvent.getTick());
                    }
                    else {
                        newEvent = orgEvent;
                    }
                    t.add(newEvent);
                }
            }
            seq = newSeq;
        }
        getSequencer().setSequence(seq);
    }

    /**
     * MIDIファイル出力
     *
     * @param path
     *            MIDIファイルパス
     * @return
     * @throws IOException
     * @throws InvalidMidiDataException
     */
    public void outputMidiFile(String path) throws InvalidMidiDataException, IOException {
        outputMidiFile(1, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param format
     *            SMFフォーマット
     * @param path
     *            出力ファイルパス
     * @return
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public void outputMidiFile(int format, String path) throws InvalidMidiDataException, IOException {
        Sequence curSeq = getSequencer().getSequence();
        Sequence seq = new Sequence(curSeq.getDivisionType(), curSeq.getResolution(), curSeq.getTracks().length);
        for (int i = 0; i < curSeq.getTracks().length; i++) {
            Track srcTrack = curSeq.getTracks()[i];
            Track dstTrack = seq.getTracks()[i];
            for (int j = 0; j < srcTrack.size(); j++) {
                dstTrack.add(srcTrack.get(j));
            }
        }

        outputMidiFile(seq, format, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param sequence
     *            シーケンス
     * @param path
     *            ファイルパス
     * @return
     * @throws IOException
     */
    public void outputMidiFile(Sequence sequence, String path) throws IOException {
        outputMidiFile(sequence, 1, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param sequence
     *            シーケンス
     * @param format
     *            SMFフォーマット
     * @param path
     *            ファイルパス
     * @return
     * @throws IOException
     */
    public void outputMidiFile(Sequence sequence, int format, String path) throws IOException {
        File file = new File(path);
        MidiSystem.write(sequence, format, file);
        return;
    }
}
