package jmp.midi.receiver;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public abstract class ReceiverCreator {
    public abstract Receiver getReciever() throws MidiUnavailableException;
}
