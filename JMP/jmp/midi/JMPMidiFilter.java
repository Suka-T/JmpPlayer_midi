package jmp.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jlib.midi.IMidiFilter;
import jlib.midi.IMidiToolkit;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;
import jmp.core.SoundManager;

public class JMPMidiFilter implements IMidiFilter {
    public JMPMidiFilter() {
    }

    public boolean filter(MidiMessage message, short senderType) {
        SoundManager sm = JMPCore.getSoundManager();
        IMidiToolkit toolkit = sm.getMidiToolkit();
        int transpose = sm.getTranspose();
        if (transpose != 0) {
            if (toolkit.isNoteOn(message) == true || toolkit.isNoteOff(message) == true) {
                byte[] data = message.getMessage();
                int length = message.getLength();

                int channel = MidiByte.getChannel(data, length);
                if (channel == 9) {
                    // ドラムトラックは対象外
                    return true;
                }

                int status = message.getStatus();
                int data1 = MidiByte.getData1(data, length);
                int data2 = MidiByte.getData2(data, length);

                data1 += transpose;

                /* TODO instanceofを指定して処理を分岐するのはよろしくない 【改善策はないか...】 */
                if (message instanceof ShortMessage) {
                    try {
                        ((ShortMessage) message).setMessage(status, data1, data2);
                    }
                    catch (InvalidMidiDataException e) {
                    }
                }
                else if (message instanceof MidiByteMessage) {
                    MidiByteMessage bMes = (MidiByteMessage) message;
                    bMes.changeByte(0, status);
                    bMes.changeByte(1, data1);
                    bMes.changeByte(2, data2);
                }
            }
        }
        return true;
    }
}
