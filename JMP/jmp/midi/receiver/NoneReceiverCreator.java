package jmp.midi.receiver;

import javax.sound.midi.Receiver;

import jmp.midi.NullReceiver;

class NoneReceiverCreator extends ReceiverCreator {

    @Override
    public Receiver getReciever() {
        return new NullReceiver();
    }

}
