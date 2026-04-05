package jmp.midi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;

public class JMPMidiDataInfo {
    public static long DIVISION_DAT_TICK = 10000L;
    private int headerLength = 0;
    private int format = 0;
    private int numTracks = 0;
    private int division = 0;
    private int ppg = 0;
    private List<List<MidiEvent>> allEventLists = null;
    private File tempDir = null;

    public JMPMidiDataInfo(String tempDirPath, int division, int numTracks) {
        this.division = division;
        this.numTracks = numTracks;
        this.tempDir = new File(tempDirPath);
        if (this.tempDir.exists() == false) {
            this.tempDir.mkdirs();
        }

        allEventLists = new ArrayList<>(numTracks);
        for (int i = 0; i < numTracks; i++) {
            allEventLists.add(new ArrayList<MidiEvent>());
        }

    }

    public List<List<MidiEvent>> getAllEventLists() {
        return allEventLists;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public int getPpg() {
        return ppg;
    }

    public void setPpg(int ppg) {
        this.ppg = ppg;
    }
}
