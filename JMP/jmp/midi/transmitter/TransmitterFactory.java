package jmp.midi.transmitter;

public class TransmitterFactory {
    public TransmitterFactory() {
    };

    public TransmitterCreator create(String name) {
        if (name.equals("") == true || name.isEmpty() == true) {
            return new NoneTransmitterCreator();
        }
        else {
            return new SelectedTransmitterCreator(name);
        }
    }
}
