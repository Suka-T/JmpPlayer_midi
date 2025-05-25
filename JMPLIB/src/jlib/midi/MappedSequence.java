package jlib.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class MappedSequence extends Sequence {

	public MappedSequence(float divisionType, int resolution) throws InvalidMidiDataException {
		super(divisionType, resolution);
	}

	public MappedSequence(float divisionType, int resolution, int numTracks) throws InvalidMidiDataException {
		super(divisionType, resolution, numTracks);
	}

}
