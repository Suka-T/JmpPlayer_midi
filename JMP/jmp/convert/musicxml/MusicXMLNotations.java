package jmp.convert.musicxml;

public class MusicXMLNotations extends MusicXMLElement {

    private MusicXMLArticulations articulations = null;
    private TiedType tied = TiedType.NONE;

    public MusicXMLNotations() {
    }

    public MusicXMLArticulations getArticulations() {
        return articulations;
    }

    public void setArticulations(MusicXMLArticulations articulations) {
        this.articulations = articulations;
    }

    public TiedType getTied() {
        return this.tied;
    }

    public void setTied(String tied) {
        if (tied.equalsIgnoreCase("start") == true) {
            this.tied = TiedType.START;
        }
        else if (tied.equalsIgnoreCase("stop") == true) {
            this.tied = TiedType.STOP;
        }
        else {
            this.tied = TiedType.NONE;
        }
    }

    public void setTied(TiedType isTied) {
        this.tied = TiedType.NONE;
    }

}
