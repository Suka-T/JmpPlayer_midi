package jmp.midi.receiver;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import jmp.core.JMPCore;
import jmp.midi.JMPBuiltinSynthMidiDevice;

public class AutoSelectSynthReceiverCreator extends ReceiverCreator {

    @Override
    public Receiver getReciever() {

        String recommendedReceiverName = JMPCore.getSoundManager().getMidiToolkit().getAutoSelectRecieverName();
        MidiDevice.Info[] infosOfRecv = JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(false, true);

        // デフォルト
        int defIndex = -1;
        for (int i = 0; i < infosOfRecv.length; i++) {
            if (infosOfRecv[i].getName().contains(recommendedReceiverName) == true) {
                defIndex = i;
                break;
            }
        }

        /* デフォルト使用 */
        Receiver reciever = null;
        if (defIndex != -1) {
            // "DEFAULT_RECVNAME"を優先的に使用
            try {
                MidiDevice outDev;
                outDev = JMPCore.getSoundManager().getMidiToolkit().getMidiDevice(infosOfRecv[defIndex]);
                if (outDev.isOpen() == false) {
                    outDev.open();
                }
                reciever = outDev.getReceiver();
            }
            catch (MidiUnavailableException e) {
                reciever = null;
            }
        }
        else {
            // SoundAPIの自動選択に従う
            try {
                reciever = MidiSystem.getReceiver();
            }
            catch (Exception e3) {
                reciever = null;
            }
        }

        // ない場合は内蔵シンセを採用する
        if (reciever == null) {
            try {
                reciever = JMPCore.getSoundManager().getMidiToolkit().getMidiDevice(JMPBuiltinSynthMidiDevice.INFO).getReceiver();
            }
            catch (MidiUnavailableException e) {
            }
        }

        // 本来ならありえないが、上記の処理でレシーバーがnullの場合は
        // Nullレシーバーを例外処理として返す
        if (reciever == null) {
            NoneReceiverCreator none = new NoneReceiverCreator();
            reciever = none.getReciever();
        }
        return reciever;
    }

}
