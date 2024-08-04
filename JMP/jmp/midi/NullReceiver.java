package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

/**
 * プラグインのみにMIDIメッセージを転送する
 *
 * @author akkut
 *
 */
public class NullReceiver implements Receiver {

    public NullReceiver() {
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        /* 何もしない */
    }

    @Override
    public void close() {
        /* 何もしない */
    }

}
