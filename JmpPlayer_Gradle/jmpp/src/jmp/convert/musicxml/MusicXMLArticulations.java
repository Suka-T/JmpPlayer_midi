package jmp.convert.musicxml;

public class MusicXMLArticulations extends MusicXMLElement {

    private boolean isStaccato = false;

    public MusicXMLArticulations() {
    }

    public boolean isStaccato() {
        return isStaccato;
    }

    public void setStaccato(boolean isStaccato) {
        this.isStaccato = isStaccato;
    }

}
