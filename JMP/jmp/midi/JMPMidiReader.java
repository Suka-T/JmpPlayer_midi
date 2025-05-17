package jmp.midi;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import jlib.midi.LightweightShortMessage;

public class JMPMidiReader {

	public static Sequence parseSmf(File file) throws Exception {
		try (DataInputStream in = new DataInputStream(
				new BufferedInputStream(new FileInputStream(file), 256 * 1024))) {

			if (!readString(in, 4).equals("MThd"))
				throw new IOException("Not a valid MIDI file");

			int headerLength = in.readInt();
			int format = in.readUnsignedShort();
			int numTracks = in.readUnsignedShort();
			int division = in.readUnsignedShort();

			Sequence sequence = new Sequence(Sequence.PPQ, division);

			int coreCount = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(coreCount, r -> {
				Thread t = new Thread(r);
				t.setName("TrackParserThread");
				t.setPriority(Thread.NORM_PRIORITY - 1);
				return t;
			});

			@SuppressWarnings("unchecked")
			Future<List<MidiEvent>>[] futures = new Future[numTracks];

			for (int i = 0; i < numTracks; i++) {
				String chunkType = readString(in, 4);
				if (!chunkType.equals("MTrk"))
					throw new IOException("Track " + i + ": missing MTrk");

				int trackLength = in.readInt();
				if (trackLength <= 0 || trackLength > 100_000_000) {
					throw new IOException("Track " + i + " has invalid length: " + trackLength);
				}

				byte[] trackData = new byte[trackLength];
				in.readFully(trackData);

				final byte[] trackCopy = trackData;
				final short trackIndex = (short) i;

				futures[i] = executor.submit(() -> parseTrackData(trackCopy, trackIndex));
			}
			executor.shutdown();

			// 1. すべてのイベントリストを収集（並列結果の取得）
			List<List<MidiEvent>> allEventLists = new ArrayList<>(numTracks);
			for (int i = 0; i < numTracks; i++) {
				try {
					allEventLists.add(futures[i].get());
				} catch (Exception e) {
					System.err.println("Track " + i + " failed to parse: " + e.getMessage());
					e.printStackTrace();
					allEventLists.add(Collections.emptyList());
				}
			}
			
			System.out.println("build sequence...");

			// 2. Sequence に Track を追加（スレッドセーフ）
			for (List<MidiEvent> events : allEventLists) {
				Track track = sequence.createTrack();
				for (MidiEvent event : events) {
					track.add(event);
				}
			}
			System.out.println("complited");

			return sequence;
		}
	}

	private static List<MidiEvent> parseTrackData(byte[] data, short trkIndex) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		PushbackInputStream pbis = new PushbackInputStream(bais, 1);
		DataInputStream in = new DataInputStream(pbis);

		List<MidiEvent> events = new ArrayList<>();
		int tick = 0;
		int lastStatus = 0;

		while (in.available() > 0) {
			int delta = readVariableLength(pbis);
			tick += delta;

			int statusByte = in.readUnsignedByte();
			if (statusByte < 0x80) {
				if (lastStatus == 0)
					throw new IOException("Invalid running status");
				pbis.unread(statusByte);
				statusByte = lastStatus;
			} else {
				lastStatus = statusByte;
			}

			if (statusByte == 0xFF) {
				int type = in.readUnsignedByte();
				int length = readVariableLength(pbis);
				byte[] metaData = new byte[length];
				in.readFully(metaData);
				MetaMessage meta = new MetaMessage();
				meta.setMessage(type, metaData, length);
				events.add(new MidiEvent(meta, tick));
			} else if (0x80 <= statusByte && statusByte <= 0xEF) {
				int command = statusByte & 0xF0;
				int data1 = in.readUnsignedByte();
				int data2 = (command != 0xC0 && command != 0xD0) ? in.readUnsignedByte() : 0;
				//if (command == ShortMessage.NOTE_OFF || command == ShortMessage.NOTE_ON || command == ShortMessage.PITCH_BEND) {
				LightweightShortMessage sm = new LightweightShortMessage(statusByte | (data1 << 8) | (data2 << 16),
						trkIndex);
				events.add(new MidiEvent(sm, tick));
				//}
			} else if (statusByte == 0xF0 || statusByte == 0xF7) {
				int length = readVariableLength(pbis);
				byte[] sysexData = new byte[length];
				in.readFully(sysexData);
				SysexMessage sx = new SysexMessage();
				sx.setMessage(statusByte, sysexData, length);
				events.add(new MidiEvent(sx, tick));
			} else {
				throw new IOException("Unknown status byte: " + statusByte);
			}
		}

		System.out.println("parsed track" + (trkIndex + 1));
		return events;
	}

	private static int readVariableLength(InputStream in) throws IOException {
		int value = 0;
		int b;
		do {
			b = in.read();
			if (b == -1)
				throw new EOFException();
			value = (value << 7) | (b & 0x7F);
		} while ((b & 0x80) != 0);
		return value;
	}

	private static String readString(DataInputStream in, int length) throws IOException {
		byte[] buf = new byte[length];
		in.readFully(buf);
		return new String(buf, "US-ASCII");
	}
}
