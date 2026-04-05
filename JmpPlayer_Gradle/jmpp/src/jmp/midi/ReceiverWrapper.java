package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import jlib.midi.IMidiEventListener;
import jmp.core.JMPCore;

/**
 * MIDI_OUT用Receiverのラッパークラス
 *
 * @author akkut
 *
 */
public class ReceiverWrapper implements Receiver {

    private Receiver abstractReciever = null;

    public ReceiverWrapper() {
    };

    public ReceiverWrapper(Receiver abstractReciever) {
        this.abstractReciever = abstractReciever;
    };

    public void changeAbsReceiver(Receiver reciever) {
        this.abstractReciever = reciever;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (JMPCore.getSoundManager().executeMidiFilter(message, IMidiEventListener.SENDER_MIDI_OUT) == false) {
            return;
        }

        if (abstractReciever != null) {
            abstractReciever.send(message, timeStamp);
        }

        JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
    }

    @Override
    public void close() {
        if (abstractReciever != null) {
            abstractReciever.close();
        }
    }

}
