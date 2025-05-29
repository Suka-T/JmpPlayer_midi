package jmp.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jlib.midi.LightweightShortMessage;
import jlib.midi.MappedParseFunc;
import jlib.midi.MappedSequence;
import jlib.midi.MidiByte;
import jmp.JMPFlags;

public class LightweightSequencer implements Sequencer {
	public static final int BLOCK_TICK = 20000;
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
	private TreeMap<Long, List<MidiEvent>> offEventMap = new TreeMap<>();
	
	private LwTransmitter lwTransmitter = new LwTransmitter();
	private MidiMessagePump midiMsgPump;
	private boolean isRenderOnly = false;

	// タスク定義：1トラックごとにテンポ & イベントを収集
	class TrackResult {
		final Map<Long, Float> tempoMap;
		final Map<Long, List<MidiEvent>> events;
		int notesCount = 0;
		int maxTick = 0;

		TrackResult(Map<Long, Float> tempoMap, Map<Long, List<MidiEvent>> events) {
			this.tempoMap = tempoMap;
			this.events = events;
		}
	}
	
	public LightweightSequencer(boolean isRenderOnly) {
		this.isRenderOnly = isRenderOnly;
	}

	private void extractMidiEvent(TreeMap<Long, List<MidiEvent>> map, long startTick, long endTick) {
		MappedSequence seq = (MappedSequence) this.sequence;
		map.clear();

		int coreCount = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(coreCount);

		List<TrackResult> results = new ArrayList<>();

		for (short trkIndex = 0; trkIndex < seq.getNumTracks(); trkIndex++) {
			final short index = trkIndex;
			Map<Long, List<MidiEvent>> localEvents = new HashMap<>();
			results.add(new TrackResult(null, localEvents));

			executor.submit(() -> {
				try {
					seq.parse(index, new MappedParseFunc(startTick, endTick) {

						@Override
						public void sysexMessage(int trk, long tick, int statusByte, byte[] sysexData, int length) {
							// TODO 自動生成されたメソッド・スタブ

						}

						@Override
						public void shortMessage(int trk, long tick, int statusByte, int data1, int data2) {
							LightweightShortMessage sm;
							try {
								sm = new LightweightShortMessage(
										statusByte | (data1 << 8) | (data2 << 16), (short) trk);
								results.get(trk).events.computeIfAbsent(tick, k -> new ArrayList<>())
										.add(new MidiEvent(sm, tick));
							} catch (InvalidMidiDataException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}

						@Override
						public void metaMessage(int trk, long tick, int type, byte[] metaData, int length) {
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		// 終了を待つ
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		// 結果をマージ
		for (TrackResult result : results) {
			// eventMap へ統合
			for (Map.Entry<Long, List<MidiEvent>> entry : result.events.entrySet()) {
				map
						.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
						.addAll(entry.getValue());
			}
		}
	}

	private void analyzeSequence(MappedSequence seq) {
		tempoChanges.clear(); // TreeMap<Long, Float>
		eventMap.clear(); // TreeMap<Long, List<MidiEvent>>
		offEventMap.clear();

		int coreCount = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(coreCount);

		List<TrackResult> results = new ArrayList<>();

		for (short trkIndex = 0; trkIndex < seq.getNumTracks(); trkIndex++) {
			final short index = trkIndex;
			Map<Long, Float> tempoMap = new HashMap<>();
			Map<Long, List<MidiEvent>> localEvents = new HashMap<>();
			results.add(new TrackResult(tempoMap, localEvents));

			executor.submit(() -> {
				try {
					seq.parse(index, new MappedParseFunc() {
						
						@Override
						public void calcTick(int trk, int tick) {
							if (results.get(trk).maxTick < tick) {
								results.get(trk).maxTick = tick;
							}
						}

						@Override
						public void sysexMessage(int trk, long tick, int statusByte, byte[] sysexData, int length) {
							// TODO 自動生成されたメソッド・スタブ

						}

						@Override
						public void shortMessage(int trk, long tick, int statusByte, int data1, int data2) {
							int command = statusByte & 0xF0;
							if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF)
									|| (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && data2 <= 0)) {
								results.get(trk).notesCount++;
							}
						}

						@Override
						public void metaMessage(int trk, long tick, int type, byte[] metaData, int length) {
							// テンポイベント検出
							if (type == 0x51) {
								if (length == 3) {
									int mpq = ((metaData[0] & 0xFF) << 16) |
											((metaData[1] & 0xFF) << 8) |
											(metaData[2] & 0xFF);
									float bpm = 60_000_000f / mpq;
									results.get(trk).tempoMap.put(tick, bpm);
								}
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

		// 終了を待つ
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		// 結果をマージ
		int maxTick = 0;
		int notesCount = 0;
		for (TrackResult result : results) {
			// tempoChanges へ統合
			tempoChanges.putAll(result.tempoMap);

			notesCount += result.notesCount;
			
			if (maxTick < result.maxTick) {
				maxTick = result.maxTick;
			}
		}
		seq.setTickLength(maxTick);
		seq.setNumOfNotes(notesCount);
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
		if (lwTransmitter.getReceiver() != null) {
			lwTransmitter.getReceiver().send(msg, timeStamp); // 即時送信
		}
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
		System.out.println("lwSequencer RUN");
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
		if (midiMsgPump != null) {
			midiMsgPump.reset();
		}
		allSoundOff();
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
		private int curBlockIndex = 0;
		private int oldBlockIndex = -1;

		MidiMessagePump() {
			curTickPosition = 0;
			nextTickPosition = 0;
			curBlockIndex = 0;
			oldBlockIndex = -1;
		}

		void reset() {
			curTickPosition = 0;
			nextTickPosition = 0;
			curBlockIndex = 0;
			oldBlockIndex = -1;
			waitFlag.set(true);
		}

		void nextTick(long tickPos) {
			nextTickPosition = tickPos;
			curBlockIndex = (int) (nextTickPosition / BLOCK_TICK);
			waitFlag.set(false);
		}

		@Override
		public void run() {
			long cur = 0;
			long next = 0;
			long t = 0;
			while (running.get()) {
				if (isRenderOnly == true) {
					// レンダリングモードは音声出力しない
					if (eventMap.isEmpty() == false) {
						eventMap.clear();
					}
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				if (waitFlag.get() == false) {
					cur = curTickPosition;
					next = nextTickPosition;
					
					if (curBlockIndex != oldBlockIndex) {
						System.out.println("extract event block: " + curBlockIndex);
						oldBlockIndex = curBlockIndex;
						eventMap.clear();
						extractMidiEvent(eventMap, BLOCK_TICK * curBlockIndex, (BLOCK_TICK * (curBlockIndex + 1)) - 1);
					}
					
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
	
	public void setRenderOnly(boolean b) {
		isRenderOnly = b;
	}
	
	public boolean isRenderOnly() {
		return isRenderOnly;
	}
} /* LightweightSequencer class end */
