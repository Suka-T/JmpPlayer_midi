package jmp.convert.musicxml;

import jmp.util.JmpUtil;

public class MusicXMLTimeModification extends MusicXMLElement {

    private String actualNotes = "";
    private String normalNotes = "";

    public MusicXMLTimeModification() {
    }

    public String getActualNotes() {
        return actualNotes;
    }

    public int getActualNotesValue() {
        if (actualNotes.isEmpty() == true) {
            return 0;
        }
        return JmpUtil.toInt(actualNotes, 0);
    }

    public void setActualNotes(String actualNotes) {
        this.actualNotes = actualNotes;
    }

    public String getNormalNotes() {
        return normalNotes;
    }

    public int getNormalNotesValue() {
        if (normalNotes.isEmpty() == true) {
            return 0;
        }
        return JmpUtil.toInt(normalNotes, 0);
    }

    public void setNormalNotes(String normalNotes) {
        this.normalNotes = normalNotes;
    }

}
