package jlib.plugin;

import javax.sound.midi.MidiMessage;

import jlib.core.JMPCoreAccessor;
import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jlib.midi.MidiByte;
import jlib.midi.MidiUtility;

public abstract class JMidiPlugin extends JMPlugin implements IMidiEventListener {

    public JMidiPlugin() {
    }

    /**
     * MIDIイベントを受信するか判定する
     *
     * @return
     */
    protected boolean isCatchMidiEvent() {
        return true;
    };

    /**
     * ノートオンイベント受信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @param velocity
     *            ベロシティ
     * @param timeStamp
     *            タイムスタンプ
     * @param senderType
     *            MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
     */
    protected void noteOn(int channel, int midiNumber, int velocity, long timeStamp, short senderType) {
    };

    /**
     * ノートオフイベント受信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @param timeStamp
     *            タイムスタンプ
     * @param senderType
     *            MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
     */
    protected void noteOff(int channel, int midiNumber, long timeStamp, short senderType) {
    };

    /**
     * プログラムチェンジ受信
     *
     * @param channel
     *            チャンネル
     * @param programNumber
     *            プログラム番号
     * @param timeStamp
     *            タイムスタンプ
     * @param senderType
     *            MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
     */
    protected void programChange(int channel, int programNumber, long timeStamp, short senderType) {
    };

    /**
     * ピッチベンド受信
     *
     * @param channel
     *            チャンネル
     * @param pbValue
     *            ピッチベンド値
     * @param timeStamp
     *            タイムスタンプ
     * @param senderType
     *            MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
     */
    protected void pitchBend(int channel, int pbValue, long timeStamp, short senderType) {
    };

    /**
     * オールノートオフイベント受信
     *
     * @param channel
     * @param timeStamp
     * @param senderType
     */
    protected void allNoteOff(int channel, long timeStamp, short senderType) {
    };

    /**
     * オールサウンドオフイベント受信
     *
     * @param channel
     * @param timeStamp
     * @param senderType
     */
    protected void allSoundOff(int channel, long timeStamp, short senderType) {
    };

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        if (isCatchMidiEvent() == false) {
            return;
        }

        try {
            IMidiToolkit toolkit = JMPCoreAccessor.getSoundManager().getMidiToolkit();
            if (toolkit.isNoteOn(message) == true) {
                noteOn(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        MidiByte.getData1(message.getMessage(), message.getLength()), //
                        MidiByte.getData2(message.getMessage(), message.getLength()), //
                        timeStamp, //
                        senderType//
                );//
            }
            else if (toolkit.isNoteOff(message) == true) {
                noteOff(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        MidiByte.getData1(message.getMessage(), message.getLength()), //
                        timeStamp, //
                        senderType//
                );//
            }
            else if (toolkit.isProgramChange(message) == true) {
                programChange(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        MidiByte.getData1(message.getMessage(), message.getLength()), //
                        timeStamp, //
                        senderType//
                );//
            }
            else if (toolkit.isPitchBend(message) == true) {
                pitchBend(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        MidiUtility.convertPitchBendValue(message), //
                        timeStamp, //
                        senderType//
                );//
            }
            else if (toolkit.isAllNoteOff(message) == true) {
                allNoteOff(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        timeStamp, //
                        senderType//
                );//
            }
            else if (toolkit.isAllSoundOff(message) == true) {
                allSoundOff(//
                        MidiByte.getChannel(message.getMessage(), message.getLength()), //
                        timeStamp, //
                        senderType//
                );//
            }
        }
        catch (Exception e) {
        }
    }

}
