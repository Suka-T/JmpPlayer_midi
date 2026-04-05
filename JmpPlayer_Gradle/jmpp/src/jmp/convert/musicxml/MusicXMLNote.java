package jmp.convert.musicxml;

import jmp.util.JmpUtil;

public class MusicXMLNote extends MusicXMLElement {
    /* <rest> */
    private boolean isRest = false;

    /* <chord> */
    private boolean isChord = false;

    /* <pitch> */
    private String step = "";
    private String octave = "";
    private String alter = "";

    /* <durasion> */
    private String duration = "";

    private String voice = "";

    private String type = "";

    private String accidental = "";

    private String stem = "";
    private String staff = "";

    private String tieType = "";

    private MusicXMLNotations notations = null;
    private MusicXMLTimeModification timeModification = null;

    public MusicXMLNote() {
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getOctave() {
        return octave;
    }

    public int getOctaveInt() {
        return JmpUtil.toInt(getOctave(), 0);
    }

    public void setOctave(String octave) {
        this.octave = octave;
    }

    public String getDuration() {
        return duration;
    }

    public int getDurationInt() {
        return JmpUtil.toInt(getDuration(), 0);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isRest() {
        return isRest;
    }

    public void setRest(boolean isRest) {
        this.isRest = isRest;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccidental() {
        return accidental;
    }

    public void setAccidental(String accidental) {
        this.accidental = accidental;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getAlter() {
        return alter;
    }

    public int getAlterInt() {
        String alter = getAlter();
        if (alter.isEmpty() == true) {
            // alterが指定されていない場合は、accidentalの文字列解析を試みる
            int res = 0;
            String accidental = getAccidental();
            if (accidental.isEmpty() == false) {
                // res += Utility.getContainsStrCount(accidental, "sharp");
                // res -= Utility.getContainsStrCount(accidental, "flat");
            }
            return res;
        }
        return JmpUtil.toInt(alter, 0);
    }

    public void setAlter(String alter) {
        this.alter = alter;
    }

    public boolean isChord() {
        return isChord;
    }

    public void setChord(boolean isChord) {
        this.isChord = isChord;
    }

    public String getTieType() {
        return tieType;
    }

    public void setTieType(String tieType) {
        this.tieType = tieType;
    }

    public MusicXMLNotations getNotations() {
        return notations;
    }

    public void setNotations(MusicXMLNotations notations) {
        this.notations = notations;
    }

    public MusicXMLTimeModification getTimeModification() {
        return timeModification;
    }

    public void setTimeModification(MusicXMLTimeModification timeModification) {
        this.timeModification = timeModification;
    }

}
