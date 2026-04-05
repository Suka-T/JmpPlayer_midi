package jmp.midi.receiver;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import jmp.core.JMPCore;

class SelectedSynthReceiverCreator extends ReceiverCreator {

    private String selected = "";

    public SelectedSynthReceiverCreator(String selected) {
        this.selected = selected;
    }

    @Override
    public Receiver getReciever() throws MidiUnavailableException {
        MidiDevice dev = null;
        Receiver rec = null;
        try {
            dev = JMPCore.getSoundManager().getMidiToolkit().getMidiDevice(selected);
            if (dev.isOpen() == false) {
                dev.open();
            }
            rec = dev.getReceiver();
        }
        catch (Exception e) {
            // 指定したレシーバーが開けなかった
            NoneReceiverCreator cre = new NoneReceiverCreator();
            rec = cre.getReciever();
        }
        return rec;
    }

}
