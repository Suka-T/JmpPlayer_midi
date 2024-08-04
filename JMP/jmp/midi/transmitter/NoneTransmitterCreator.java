package jmp.midi.transmitter;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

class NoneTransmitterCreator extends TransmitterCreator {

    class NoneTransmitter implements Transmitter {

        @Override
        public void setReceiver(Receiver receiver) {
        }

        @Override
        public Receiver getReceiver() {
            return null;
        }

        @Override
        public void close() {
        }

    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        // Transmitter outTransmitter = null;
        // outTransmitter = MidiSystem.getTransmitter();
        // return outTransmitter;
        return new NoneTransmitter();
    }

}
