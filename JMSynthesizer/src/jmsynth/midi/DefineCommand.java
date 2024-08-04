package jmsynth.midi;

public class DefineCommand {
    static final int NOTE_OFF = 128;
    static final int NOTE_ON = 144;
    static final int POLYPHONIC_KEY_PRESSURE = 160;
    static final int CONTROL_CHANGE = 176;
    static final int PROGRAM_CHANGE = 192;
    static final int CHANNEL_PRESSURE = 208;
    static final int PITCH_BEND = 224;
    static final int SYSEX_BEGIN = 240;
    static final int MIDI_TIME_CODE = 241;
    static final int SONG_POSITION = 242;
    static final int SONG_SELECT = 243;
    static final int TUNE_SELECT = 246;
    static final int SYSEX_END = 247;
    static final int MIDI_CLOCK = 248;
    static final int START = 250;
    static final int CONTINUE = 251;
    static final int STOP = 252;
    static final int ACTIVE_SENSING = 254;
    static final int RESET = 255;
}
