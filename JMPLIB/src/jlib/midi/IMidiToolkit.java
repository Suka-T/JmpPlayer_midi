package jlib.midi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

public interface IMidiToolkit {

    /**
     * MIDIデバイスの一覧を取得
     *
     * @return
     */
    abstract List<MidiDevice> getMidiDevices();
    
    /**
     * Midiファイル解析 
     * 
     * @param file
     * @return
     * @throws Exception
     */
    abstract Sequence readMidiFile(File file) throws InvalidMidiDataException, IOException;

    /**
     * MIDIデバイス情報一覧取得
     *
     * @param incTransmitter
     * @param incReciever
     * @return
     */
    abstract MidiDevice.Info[] getMidiDeviceInfo(boolean incTransmitter, boolean incReciever);

    /**
     * MIDIデバイス取得
     *
     * @param info
     * @throws MidiUnavailableException
     * @return
     */
    abstract MidiDevice getMidiDevice(MidiDevice.Info info) throws MidiUnavailableException;

    /**
     * MIDIデバイス取得
     *
     * @param name
     * @return
     * @throws MidiUnavailableException
     */
    abstract MidiDevice getMidiDevice(String name) throws MidiUnavailableException;

    /**
     * ByteメッセージからMIDIメッセージオブジェクト生成
     *
     * @param data
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createByteMidiMessage(byte[] data);

    /**
     * SysExメッセージ(0xF0)からMIDIメッセージオブジェクト生成
     *
     * @param data
     * @param length
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createSysexBeginMessage(byte[] data, int length) throws InvalidMidiDataException;

    /**
     * SysExメッセージ(0xF7)からMIDIメッセージオブジェクト生成
     *
     * @param data
     * @param length
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createSysexEndMessage(byte[] data, int length) throws InvalidMidiDataException;

    /**
     * ShortメッセージからMIDIメッセージオブジェクト生成
     *
     * @param command
     * @param channel
     * @param data1
     * @param data2
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createShortMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException;

    /**
     * MetaメッセージからMIDIメッセージオブジェクト生成
     *
     * @param type
     * @param data
     * @param length
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createMetaMessage(int type, byte[] data, int length) throws InvalidMidiDataException;

    /**
     * MIDIメッセージからMIDIイベントオブジェクト生成
     *
     * @param mes
     * @param position
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createMidiEvent(MidiMessage mes, long position) throws InvalidMidiDataException;

    ////////////////////////////////////////////////////
    // 以下、よく使うメソッド群をDefault定義で実装
    //

    /**
     * ノートオンMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createNoteOnMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON, channel, midiNumber, velocity);
    }

    /**
     * ノートオンMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createNoteOnEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        return createMidiEvent(createNoteOnMessage(channel, midiNumber, velocity), position);
    }

    /**
     * NoteOnイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isNoteOn(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data2 = MidiByte.getData2(mes.getMessage(), mes.getLength());
        if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) && (data2 > 0)) {
            return true;
        }
        return false;
    }

    /**
     * ノートオフMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createNoteOffMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF, channel, midiNumber, velocity);
    }

    /**
     * ノートオフMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createNoteOffEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        return createMidiEvent(createNoteOffMessage(channel, midiNumber, velocity), position);
    }

    /**
     * NoteOffイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isNoteOff(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data2 = MidiByte.getData2(mes.getMessage(), mes.getLength());
        if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF) || (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && data2 <= 0)) {
            return true;
        }
        return false;
    }

    /**
     * プログラムチェンジMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param programNumber
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createProgramChangeMessage(int channel, int programNumber) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE, channel, programNumber, 0);
    }

    /**
     * プログラムチェンジMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param programNumber
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createProgramChangeEvent(long position, int channel, int programNumber) throws InvalidMidiDataException {
        return createMidiEvent(createProgramChangeMessage(channel, programNumber), position);
    }

    /**
     * ProgramChangeイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isProgramChange(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE) {
            return true;
        }
        return false;
    }

    /**
     * ピッチベンドMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param bend
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createPitchBendMessage(int channel, int bend) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND, channel, (bend & 0x00ef), (bend >> 7));
    }

    /**
     * ピッチベンドMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param bend
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createPitchBendEvent(long position, int channel, int bend) throws InvalidMidiDataException {
        return createMidiEvent(createPitchBendMessage(channel, bend), position);
    }

    /**
     * PitchBendイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isPitchBend(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND) {
            return true;
        }
        return false;
    }

    /**
     * オールノートオフMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createAllNoteOffMessage(int channel) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelMode.Fst.CONTROL_CHANGE, channel, MidiByte.Status.Channel.ChannelMode.Snd.ALL_NOTE_OFF, 0);
    }

    /**
     * オールノートオフMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createAllNoteOffEvent(long position, int channel) throws InvalidMidiDataException {
        return createMidiEvent(createAllNoteOffMessage(channel), position);
    }

    /**
     * オールノートオフイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isAllNoteOff(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data1 = MidiByte.getData1(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelMode.Fst.CONTROL_CHANGE && data1 == MidiByte.Status.Channel.ChannelMode.Snd.ALL_NOTE_OFF) {
            return true;
        }
        return false;
    }

    /**
     * オールサウンドオフMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createAllSoundOffMessage(int channel) throws InvalidMidiDataException {
        return createShortMessage(MidiByte.Status.Channel.ChannelMode.Fst.CONTROL_CHANGE, channel, MidiByte.Status.Channel.ChannelMode.Snd.ALL_SOUND_OFF, 0);
    }

    /**
     * オールサウンドオフMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createAllSoundOffEvent(long position, int channel) throws InvalidMidiDataException {
        return createMidiEvent(createAllSoundOffMessage(channel), position);
    }

    /**
     * オールサウンドオフイベントか判定
     *
     * @param mes
     * @return
     */
    default boolean isAllSoundOff(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data1 = MidiByte.getData1(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelMode.Fst.CONTROL_CHANGE && data1 == MidiByte.Status.Channel.ChannelMode.Snd.ALL_SOUND_OFF) {
            return true;
        }
        return false;
    }

    /**
     * テンポ変更MIDIメッセージオブジェクト生成
     *
     * @param bpm
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiMessage createTempoMessage(float bpm) throws InvalidMidiDataException {
        long mpq = Math.round(60000000f / bpm);
        byte[] data = new byte[3];
        data[0] = new Long(mpq / 0x10000).byteValue();
        data[1] = new Long((mpq / 0x100) % 0x100).byteValue();
        data[2] = new Long(mpq % 0x100).byteValue();
        return createMetaMessage(MidiByte.SET_TEMPO.type, data, data.length);
    }

    /**
     * テンポ変更MIDIイベントオブジェクト生成
     *
     * @param position
     * @param bpm
     * @return
     * @throws InvalidMidiDataException
     */
    default MidiEvent createTempoEvent(long position, float bpm) throws InvalidMidiDataException {
        return createMidiEvent(createTempoMessage(bpm), position);
    }
}
