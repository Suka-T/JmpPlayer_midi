package jmp.convert.musicxml;

import java.util.ArrayList;
import java.util.List;

public class MusicXMLPartList {

    private List<MusicXMLScorePart> scorepart = null;

    public MusicXMLPartList() {
        scorepart = new ArrayList<MusicXMLScorePart>();
    }

    public void addScorePart(MusicXMLScorePart element) {
        scorepart.add(element);
    }

    public MusicXMLScorePart getScorePart(String partID) {
        MusicXMLScorePart score = null;
        for (MusicXMLScorePart sp : scorepart) {
            if (partID.equals(sp.getPartID()) == true) {
                score = sp;
                break;
            }
        }
        return score;
    }

    public MusicXMLScorePart getScorePart(int index) {
        return scorepart.get(index);
    }

    public List<MusicXMLScorePart> getScorePart() {
        return scorepart;
    }

}
