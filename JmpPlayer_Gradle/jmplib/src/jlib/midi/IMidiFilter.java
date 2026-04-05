package jlib.midi;

import javax.sound.midi.MidiMessage;

public interface IMidiFilter {
    default boolean filter(MidiMessage message, short senderType) {
        return true;
    }
}
