package jmp.convert.musicxml;

import java.util.ArrayList;
import java.util.List;

public class MusicXMLPart {

    private String id = "";
    private List<MusicXMLMeasure> measures = null;

    public MusicXMLPart(String id) {
        this.setId(id);
        measures = new ArrayList<MusicXMLMeasure>();
    }

    public void addMeasure(MusicXMLMeasure meas) {
        measures.add(meas);
    }

    public MusicXMLMeasure getMeasure(int index) {
        return measures.get(index);
    }

    public List<MusicXMLMeasure> getMeasures() {
        return measures;
    }

    public int measureSize() {
        return measures.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
