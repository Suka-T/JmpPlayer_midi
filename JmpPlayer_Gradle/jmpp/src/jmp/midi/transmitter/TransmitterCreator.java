package jmp.midi.transmitter;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

public abstract class TransmitterCreator {
    public abstract Transmitter getTransmitter() throws MidiUnavailableException;
}
