package jmp.midi;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiFilter;
import jlib.midi.IMidiUnit;
import jlib.midi.MappedParseFunc;
import jmp.midi.LightweightSequencer.ESeqMode;
import jmp.player.MidiPlayer;

public class MidiUnit implements IMidiUnit {

    private MidiPlayer midiPlayer;

    private List<IMidiFilter> filters = null;

    public MidiUnit(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
        this.filters = new LinkedList<IMidiFilter>();
    }

    public MidiPlayer getMidiPlayer() {
        return midiPlayer;
    }

    public void addFilter(IMidiFilter f) {
        filters.add(f);
    }

    public void removeFilter(IMidiFilter f) {
        filters.remove(f);
    }

    public boolean filter(MidiMessage message, short senderType) {
        for (IMidiFilter f : filters) {
            if (f.filter(message, senderType) == false) {
                return false;
            }
        }
        return true;
    }

    private JMPSequencer getSequencer() {
        return (JMPSequencer) (getMidiPlayer().getSequencer());
    }

    public void exportMidiFile(File file) throws Exception {
        getMidiPlayer().saveFile(file);
    }

    public boolean updateMidiOut(String name) {
        return getMidiPlayer().updateMidiOut(name);
    }

    public boolean updateMidiIn(String name) {
        return getMidiPlayer().updateMidiIn(name);
    }

    public Receiver getCurrentReciever() {
        return getMidiPlayer().getCurrentReciver();
    }

    public Transmitter getCurrentTransmitter() {
        return getMidiPlayer().getCurrentTransmitter();
    }

    @Override
    public boolean isRunning() {
        return getSequencer().isRunning();
    }

    @Override
    public double getTempoInBPM() {
        return getSequencer().getTempoInBPM();
    }

    @Override
    public double getFirstTempoInBPM() {
        return getSequencer().getFirstTempoInBPM();
    }

    @Override
    public long getTickPosition() {
        return getSequencer().getTickPosition();
    }

    @Override
    public long getTickLength() {
        return getSequencer().getTickLength();
    }

    @Override
    public long getMicrosecondPosition() {
        return getSequencer().getMicrosecondPosition();
    }

    @Override
    public long getMicrosecondLength() {
        return getSequencer().getMicrosecondLength();
    }

    @Override
    public boolean isRenderingOnlyMode() {
        if (getMidiPlayer().getSeqMode() == ESeqMode.TickOnly) {
            return true;
        }
        return false;
    }

    @Override
    public int getResolution() {
        if (isValidSequence() == false) {
            return 0;
        }
        return getSequencer().getSequence().getResolution();
    }

    @Override
    public void parseMappedByteBuffer(short trkIndex, MappedParseFunc func) throws IOException {
        if (isValidSequence() == false) {
            return;
        }
        ((MappedSequence) getSequencer().getSequence()).parse((short) trkIndex, func);
    }

    @Override
    public boolean isValidSequence() {
        if (getSequencer() == null) {
            return false;
        }
        if (getSequencer().getSequence() == null) {
            return false;
        }
        return true;
    }

    @Override
    public long getNumOfNote() {
        if (isValidSequence() == false) {
            return 0;
        }
        return ((MappedSequence) getSequencer().getSequence()).getNumOfNotes();
    }

    @Override
    public int getNumOfTrack() {
        if (isValidSequence() == false) {
            return 0;
        }
        return ((MappedSequence) getSequencer().getSequence()).getNumTracks();
    }
}
