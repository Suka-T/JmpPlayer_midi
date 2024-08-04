package jlib.midi;

import javax.sound.midi.MidiMessage;

public interface IMidiEventListener {

    public static short SENDER_MIDI_OUT = 0x00;
    public static short SENDER_MIDI_IN = 0x01;

    /**
     * Midiイベント受信
     *
     * @param message
     *            MIDIメッセージ
     * @param timeStamp
     *            タイムスタンプ
     */
    abstract void catchMidiEvent(MidiMessage message, long timeStamp, short senderType);
}
