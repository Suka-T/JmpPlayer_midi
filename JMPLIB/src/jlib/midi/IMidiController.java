package jlib.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;

public interface IMidiController {

    /**
     * Midiメッセージ送信
     *
     * @param data
     * @param timeStamp
     * @return
     */
    abstract boolean sendMidiMessage(final byte[] data, long timeStamp);

    /**
     * Midiメッセージ送信
     *
     * @param msg
     * @param timeStamp
     * @return
     */
    abstract boolean sendMidiMessage(MidiMessage msg, long timeStamp);

    /**
     * ノートオンイベント送信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @param velocity
     *            ベロシティ
     * @throws InvalidMidiDataException
     */
    abstract boolean sendNoteOn(int channel, int midiNumber, int velocity, long timeStamp) throws InvalidMidiDataException;

    /**
     * ノートオフイベント送信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @throws InvalidMidiDataException
     */
    abstract boolean sendNoteOff(int channel, int midiNumber, long timeStamp) throws InvalidMidiDataException;

    /**
     * プログラムチェンジ送信
     *
     * @param channel
     *            チャンネル
     * @param programNumber
     *            プログラム番号
     * @throws InvalidMidiDataException
     */
    abstract boolean sendProgramChange(int channel, int programNumber, long timeStamp) throws InvalidMidiDataException;
}
