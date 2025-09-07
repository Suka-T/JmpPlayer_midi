package jmp.midi;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
            sequence.setFile(file);
            return sequence;
        }
    }
}
