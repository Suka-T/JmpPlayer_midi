package jmp.midi;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import javax.sound.midi.Sequence;

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

            MappedSequence sequence = new MappedSequence(Sequence.PPQ, division, numTracks);

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
                sequence.addMap(trackBuf);

                channel.position(pos + trackLength);
            }
            return sequence;
        }
    }
} 
