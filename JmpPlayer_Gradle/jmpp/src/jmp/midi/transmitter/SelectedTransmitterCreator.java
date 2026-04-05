package jmp.midi.transmitter;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import jmp.core.JMPCore;

class SelectedTransmitterCreator extends TransmitterCreator {
    private String selected = "";

    public SelectedTransmitterCreator(String selected) {
        this.selected = selected;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        MidiDevice dev = null;
        Transmitter rec = null;
        try {
            dev = JMPCore.getSoundManager().getMidiToolkit().getMidiDevice(selected);
            if (dev.isOpen() == false) {
                dev.open();
            }
            rec = dev.getTransmitter();
        }
        catch (Exception e) {
            NoneTransmitterCreator creator = new NoneTransmitterCreator();
            rec = creator.getTransmitter();
        }
        return rec;
    }

}
