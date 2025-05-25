package jmp.midi;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
import jlib.midi.MappedSequence;

public class JMPMidiReader {

    public static MappedSequence parseSmf(File file) throws Exception {
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            ByteBuffer header = ByteBuffer.allocate(14);
            channel.read(header);
            header.flip();

            byte[] id = new byte[4];
            header.get(id);
            if (!new String(id, "US-ASCII").equals("MThd"))
                throw new IOException("Not a valid MIDI file");

            header.getInt(); // skip headerLength
            int format = header.getShort() & 0xFFFF;
            int numTracks = header.getShort() & 0xFFFF;
            int division = header.getShort() & 0xFFFF;

            MappedSequence sequence = new MappedSequence(Sequence.PPQ, division);

            ExecutorService executor = Executors.newFixedThreadPool(4); // 並列数制限推奨
            @SuppressWarnings("unchecked")
            Future<List<MidiEvent>>[] futures = new Future[numTracks];

            for (int i = 0; i < numTracks; i++) {
                ByteBuffer trkHeader = ByteBuffer.allocate(8);
                channel.read(trkHeader);
                trkHeader.flip();

                trkHeader.get(id);
                if (!new String(id, "US-ASCII").equals("MTrk"))
                    throw new IOException("Track " + i + " missing MTrk");

                int trackLength = trkHeader.getInt();
                long pos = channel.position();

                MappedByteBuffer trackBuf = channel.map(FileChannel.MapMode.READ_ONLY, pos, trackLength);
                short trackIndex = (short) i;
                futures[i] = executor.submit(() -> parseTrackBuffer(trackBuf, trackIndex));

                channel.position(pos + trackLength);
            }

            executor.shutdown();

            for (Future<List<MidiEvent>> future : futures) {
                List<MidiEvent> events = future.get();
                Track track = sequence.createTrack();
                for (MidiEvent e : events)
                    track.add(e);
            }

            return sequence;
        }
    }

    private static List<MidiEvent> parseTrackBuffer(ByteBuffer buf, short trkIndex) throws Exception {
        List<MidiEvent> events = new ArrayList<>();
        int tick = 0;
        int lastStatus = 0;

        while (buf.hasRemaining()) {
            int delta = readVariableLength(buf);
            tick += delta;

            int statusByte = buf.get() & 0xFF;
            if (statusByte < 0x80) {
                if (lastStatus == 0) throw new IOException("Invalid running status");
                buf.position(buf.position() - 1);
                statusByte = lastStatus;
            } else {
                lastStatus = statusByte;
            }

            if (statusByte == 0xFF) {
                int type = buf.get() & 0xFF;
                int length = readVariableLength(buf);
                byte[] metaData = new byte[length];
                buf.get(metaData);
                MetaMessage meta = new MetaMessage();
                meta.setMessage(type, metaData, length);
                events.add(new MidiEvent(meta, tick));
            } else if (statusByte >= 0x80 && statusByte <= 0xEF) {
                int command = statusByte & 0xF0;
                int data1 = buf.get() & 0xFF;
                int data2 = (command != 0xC0 && command != 0xD0) ? (buf.get() & 0xFF) : 0;
                LightweightShortMessage sm = new LightweightShortMessage(
                        statusByte | (data1 << 8) | (data2 << 16), trkIndex);
                events.add(new MidiEvent(sm, tick));
            } else if (statusByte == 0xF0 || statusByte == 0xF7) {
                int length = readVariableLength(buf);
                byte[] sysexData = new byte[length];
                buf.get(sysexData);
                SysexMessage sx = new SysexMessage();
                sx.setMessage(statusByte, sysexData, length);
                events.add(new MidiEvent(sx, tick));
            } else {
                throw new IOException("Unknown status byte: " + statusByte);
            }
        }

        System.out.println("parsed track " + (trkIndex + 1));
        return events;
    }

    private static int readVariableLength(ByteBuffer buf) throws IOException {
        int value = 0;
        int b;
        do {
            if (!buf.hasRemaining()) throw new EOFException();
            b = buf.get() & 0xFF;
            value = (value << 7) | (b & 0x7F);
        } while ((b & 0x80) != 0);
        return value;
    }
} 
