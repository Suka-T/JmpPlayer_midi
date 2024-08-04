package jmp.convert.musicxml;

import jmp.util.JmpUtil;

public class MusicXMLDirection extends MusicXMLElement {

    private String tempo = "120.0";

    public MusicXMLDirection() {

    }

    public String getTempo() {
        return tempo;
    }

    public float getTempoDouble() {
        return JmpUtil.toFloat(getTempo(), 120.0f);
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}
