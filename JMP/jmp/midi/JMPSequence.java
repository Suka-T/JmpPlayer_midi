package jmp.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class JMPSequence extends Sequence {

    public JMPSequence(float divisionType, int resolution) throws InvalidMidiDataException {
        super(divisionType, resolution);
    }

    public JMPSequence(float divisionType, int resolution, int numTracks) throws InvalidMidiDataException {
        super(divisionType, resolution, numTracks);
    }

}
