package jlib.midi;

public class SignatureInfo {
    public static final String[] MAJOR_KEYS = { "Cb Maj", "Gb Maj", "Db Maj", "Ab Maj", "Eb Maj", "Bb Maj", "F Maj", "C Maj", "G Maj", "D Maj", "A Maj",
            "E Maj", "B Maj", "F# Maj", "C# Maj" };

    public static final String[] MINOR_KEYS = { "Ab Min", "Eb Min", "Bb Min", "F Min", "C Min", "G Min", "D Min", "A Min", "E Min", "B Min", "F# Min",
            "C# Min", "G# Min", "D# Min", "A# Min" };

    private int numerator = 4; // 分子
    private int denominator = 4; // 分母 (2^dd)
    private int midiClocks = 24; // 1拍あたりのMIDIクロック数
    private int notated32nd = 8; // 1小節の32分音符数

    private String accidental = ""; // 調号

    public SignatureInfo() {
    }

    public void init() {
        numerator = 4;
        denominator = 2;
        midiClocks = 24;
        notated32nd = 8;
        accidental = "";
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public int getMidiClocks() {
        return midiClocks;
    }

    public void setMidiClocks(int midiClocks) {
        this.midiClocks = midiClocks;
    }

    public int getNotated32nd() {
        return notated32nd;
    }

    public void setNotated32nd(int notated32nd) {
        this.notated32nd = notated32nd;
    }

    public String getAccidental() {
        return accidental;
    }

    public void setAccidental(String accidental) {
        this.accidental = accidental;
    }

}
