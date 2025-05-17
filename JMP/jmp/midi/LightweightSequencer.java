package jmp.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jmp.JMPFlags;

public class LightweightSequencer implements Sequencer {
	private float tempoBPM = 120.0f;
	private int resolution = 480;
	private long tickPosition = 0;

	private Sequence sequence;
	private Thread playThread;
	private Thread dumpThread;
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicBoolean paused = new AtomicBoolean(false);
	private long lastTimeNs = System.nanoTime();
	private double tickRemainder = 0.0;
	private boolean isOpen = false;

	private TreeMap<Long, Float> tempoChanges = new TreeMap<>();
	private TreeMap<Long, List<MidiEvent>> eventMap = new TreeMap<>();

	private LwTransmitter lwTransmitter = new LwTransmitter();
	private MidiMessagePump midiMsgPump;

	private void analyzeSequence(Sequence seq) {
		tempoChanges.clear(); // TreeMap<Long, Float>
		eventMap.clear(); // TreeMap<Long, List<MidiEvent>>

		Track[] tracks = seq.getTracks();
		if (tracks == null || tracks.length == 0)
			return;

		int coreCount = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(coreCount);

		// タスク定義：1トラックごとにテンポ & イベントを収集
		class TrackResult {
			final Map<Long, Float> tempoMap;
			final Map<Long, List<MidiEvent>> events;

			TrackResult(Map<Long, Float> tempoMap, Map<Long, List<MidiEvent>> events) {
				this.tempoMap = tempoMap;
				this.events = events;
			}
		}

		List<Future<TrackResult>> futures = new ArrayList<>();

		for (Track track : tracks) {
			futures.add(executor.submit(() -> {
				Map<Long, Float> tempoMap = new HashMap<>();
				Map<Long, List<MidiEvent>> localEvents = new HashMap<>();

				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage msg = event.getMessage();
					long tick = event.getTick();

					// イベント登録
					localEvents.computeIfAbsent(tick, k -> new ArrayList<>()).add(event);

					// テンポイベント検出
					if (msg instanceof MetaMessage mm && mm.getType() == 0x51) {
						byte[] data = mm.getData();
						if (data.length == 3) {
							int mpq = ((data[0] & 0xFF) << 16) |
									((data[1] & 0xFF) << 8) |
									(data[2] & 0xFF);
							float bpm = 60_000_000f / mpq;
							tempoMap.put(tick, bpm);
						}
					}
				}

				return new TrackResult(tempoMap, localEvents);
			}));
		}
		
		executor.shutdown();

		// 結果をマージ
		for (Future<TrackResult> future : futures) {
			try {
				TrackResult result = future.get();

				// tempoChanges へ統合
				tempoChanges.putAll(result.tempoMap);

				// eventMap へ統合
				for (Map.Entry<Long, List<MidiEvent>> entry : result.events.entrySet()) {
					eventMap
							.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
							.addAll(entry.getValue());
				}

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
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
			if (paused.get()) {
				try {
					Thread.sleep(10);
					continue;
				} catch (InterruptedException e) {
					break;
				}
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
					float newTempo = tempoChanges.get(tickPosition);
					setTempoInBPM(newTempo);

					// microPerQuarter を更新
					microPerQuarter = 60_000_000.0 / tempoBPM;
				}
				tickPosition++;
			}

			midiMsgPump.nextTick(tickPosition);

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} // スムーズなCPU制御
		}
	}

	protected void onTick(long tick) {

		// Note On / Off 処理
		List<MidiEvent> events = eventMap.get(tick);
		if (events != null && lwTransmitter.getReceiver() != null) {
			for (MidiEvent event : events) {

				MidiMessage msg = event.getMessage();
				sendMidiEvent(msg, -1); // 即時送信
			}
		}
	}

	private void sendMidiEvent(MidiMessage msg, int timeStamp) {
		lwTransmitter.getReceiver().send(msg, timeStamp); // 即時送信
	}

	private void allSoundOff() {
		// 全チャンネルに All Sound Off (CC#120) を送る
		for (int channel = 0; channel < 16; channel++) {
			try {
				ShortMessage cc = new ShortMessage();
				cc.setMessage(ShortMessage.CONTROL_CHANGE, channel, 120, 0);
				sendMidiEvent(cc, -1);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < 128; i++) {
				try {
					ShortMessage ss = new ShortMessage();
					ss.setMessage(ShortMessage.NOTE_OFF, channel, i, 0);
					sendMidiEvent(ss, -1);
				} catch (InvalidMidiDataException e) {
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
	}

	@Override
	public void close() {
		running.set(false);
		if (playThread != null) {
			try {
				playThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		playThread = null;
		isOpen = false;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public void start() {
		System.out.println("LightweitSequencer RUN");
		if (playThread != null) {
			if (paused.get()) {
				if (getTickLength() <= tickPosition) {
					lastTimeNs = System.nanoTime();
					tickRemainder = 0.0;
				}
				resume();
			}
			return;
		} else {
			playThread = new Thread(this::runLoop);
			playThread.start();

			dumpThread = new Thread(midiMsgPump);
			dumpThread.start();
			midiMsgPump.reset();

			running.set(true);
			paused.set(false);
			lastTimeNs = System.nanoTime();
			tickRemainder = 0.0;
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

	@Override
	public long getTickPosition() {
		return tickPosition;
	}

	@Override
	public void setTickPosition(long tick) {
		this.tickPosition = tick;
		if (0 >= this.tickPosition) {
			tempoBPM = 120.0f;
		}
		lastTimeNs = System.nanoTime();
		tickRemainder = 0.0;
	}

	@Override
	public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException {
	}

	@Override
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
		this.resolution = sequence.getResolution();
		analyzeSequence(sequence);
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
		return true;
	}

	@Override
	public void removeMetaEventListener(MetaEventListener listener) {
		unsupported();
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

	private class MidiMessagePump implements Runnable {
		private AtomicBoolean waitFlag = new AtomicBoolean(true);
		private long curTickPosition = 0;
		private long nextTickPosition = 0;

		MidiMessagePump() {
			curTickPosition = 0;
			nextTickPosition = 0;
		}

		void reset() {
			curTickPosition = 0;
			nextTickPosition = 0;
			waitFlag.set(true);
		}

		void nextTick(long tickPos) {
			nextTickPosition = tickPos;
			waitFlag.set(false);
		}

		@Override
		public void run() {
			long cur = 0;
			long next = 0;
			long t = 0;
			while (running.get()) {
				if (waitFlag.get() == false) {
					cur = curTickPosition;
					next = nextTickPosition;
					for (t = cur; t < next; t++) {
						onTick(t);
						if (t >= getTickLength()) {
							stop();
							break;
						}
					}
					curTickPosition = next;
					if (nextTickPosition == next) {
						waitFlag.set(true);
					}
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				} // スムーズなCPU制御
			}
		}
	}
} /* LightweightSequencer class end */
