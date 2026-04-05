package jlib.midi;

import javax.sound.midi.MidiMessage;

/**
 * MIDI解析系の補助メソッド群
 *
 * @author abs
 *
 */
public class MidiUtility {
    /**
     * PitchBendイベントをシフト量に変換する
     *
     * @param mes
     * @return
     */
    public static int convertPitchBendValue(MidiMessage mes) {
        int lsb = MidiByte.getData1(mes.getMessage(), mes.getLength());
        int msb = MidiByte.getData2(mes.getMessage(), mes.getLength());
        int pbValue = MidiByte.mergeLsbMsbValue(lsb, msb);
        return pbValue;
    }
}
