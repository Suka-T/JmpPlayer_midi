package jmp.midi.receiver;

import jmp.core.SoundManager;

public class ReceiverFactory {
    public ReceiverFactory() {
    };

    public ReceiverCreator create(String name) {
        if (name.equals("") == true || name.isEmpty() == true) {
            return new AutoSelectSynthReceiverCreator();
        }
        else if (name.equals(SoundManager.NULL_RECEIVER_NAME) == true) {
            return new NoneReceiverCreator();
        }
        else {
            return new SelectedSynthReceiverCreator(name);
        }
    }
}
