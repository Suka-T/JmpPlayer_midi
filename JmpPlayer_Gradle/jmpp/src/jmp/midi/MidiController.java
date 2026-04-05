package jmp.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiController;
import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jmp.core.JMPCore;

public class MidiController implements IMidiController {

    protected short senderType = IMidiEventListener.SENDER_MIDI_OUT;

    public MidiController(short senderType) {
        this.senderType = senderType;
    }

    protected Receiver getReceiver() {
        Receiver rec = null;
        switch (this.senderType) {
            case IMidiEventListener.SENDER_MIDI_IN:
                Transmitter trans = JMPCore.getSoundManager().getCurrentTransmitter();
                if (trans != null) {
                    rec = trans.getReceiver();
                }
                break;
            case IMidiEventListener.SENDER_MIDI_OUT:
            default:
                rec = JMPCore.getSoundManager().getCurrentReciever();
                break;
        }
        return rec;
    }

    @Override
    public boolean sendMidiMessage(byte[] data, long timeStamp) {
        // MidiMessage sendMsg = null;
        MidiByteMessage byteMessage = new MidiByteMessage(data);
        // if (MidiByte.isChannelMessage(data, data.length) == true) {
        // try {
        // // 可能であればShortMessageに変換する
        // sendMsg = byteMessage.convertShortMessage();
        // }
        // catch (InvalidMidiDataException e) {
        // sendMsg = byteMessage;
        // }
        // }
        return sendMidiMessage(byteMessage, timeStamp);
    }

    @Override
    public boolean sendMidiMessage(MidiMessage msg, long timeStamp) {
        Receiver rec = getReceiver();
        if (rec == null) {
            return false;
        }
        rec.send(msg, timeStamp);
        return true;
    }

    @Override
    public boolean sendNoteOn(int channel, int midiNumber, int velocity, long timeStamp) throws InvalidMidiDataException {
        IMidiToolkit kit = JMPCore.getSoundManager().getMidiToolkit();
        MidiMessage sMes = kit.createNoteOnMessage(channel, midiNumber, velocity);
        return sendMidiMessage(sMes, timeStamp);
    }

    @Override
    public boolean sendNoteOff(int channel, int midiNumber, long timeStamp) throws InvalidMidiDataException {
        IMidiToolkit kit = JMPCore.getSoundManager().getMidiToolkit();
        MidiMessage sMes = kit.createNoteOffMessage(channel, midiNumber, 0);
        return sendMidiMessage(sMes, timeStamp);
    }

    @Override
    public boolean sendProgramChange(int channel, int programNumber, long timeStamp) throws InvalidMidiDataException {
        IMidiToolkit kit = JMPCore.getSoundManager().getMidiToolkit();
        MidiMessage sMes = kit.createProgramChangeMessage(channel, programNumber);
        return sendMidiMessage(sMes, timeStamp);
    }

}
