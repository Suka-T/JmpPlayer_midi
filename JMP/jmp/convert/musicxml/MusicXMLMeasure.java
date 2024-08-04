package jmp.convert.musicxml;

import java.util.ArrayList;
import java.util.List;

public class MusicXMLMeasure extends MusicXMLElement {

    private int number = -1;

    private List<MusicXMLElement> elements = null;

    public MusicXMLMeasure(int number) {
        this.number = number;

        elements = new ArrayList<MusicXMLElement>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addElement(MusicXMLElement element) {
        elements.add(element);
    }

    public MusicXMLElement getElement(int index) {
        return elements.get(index);
    }

    public List<MusicXMLElement> getElements() {
        return elements;
    }

    public int elementSize() {
        return elements.size();
    }

}
