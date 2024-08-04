package jmp.convert.musicxml;

import jmp.util.JmpUtil;

public class MusicXMLAttributes extends MusicXMLElement {

    private String divisions = "";
    private String fifths = "";
    private String mode = "";

    private String staves = "";

    /* <time> */
    private String beats = "";
    private String beatsType = "";

    public MusicXMLAttributes() {
    }

    public String getDivisions() {
        return divisions;
    }

    public void setDivisions(String divisions) {
        this.divisions = divisions;
    }

    public int getDivisionsInt() {
        String str = getDivisions();
        if (str == null || str.isEmpty()) {
            return 0;
        }
        return JmpUtil.toInt(str, 0);
    }

    public String getFifths() {
        return fifths;
    }

    public void setFifths(String fifths) {
        this.fifths = fifths;
    }

    public String getBeats() {
        return beats;
    }

    public void setBeats(String beats) {
        this.beats = beats;
    }

    public String getBeatsType() {
        return beatsType;
    }

    public void setBeatsType(String beatsType) {
        this.beatsType = beatsType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStaves() {
        return staves;
    }

    public void setStaves(String staves) {
        this.staves = staves;
    }

}
