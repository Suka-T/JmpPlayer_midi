package jmp.midi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiFilter;
import jlib.midi.IMidiUnit;
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

    private Sequencer getSequencer() {
        return getMidiPlayer().getSequencer();
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
    public long getTickPosition() {
        return getSequencer().getTickPosition();
    }

    @Override
    public long getTickLength() {
        return getSequencer().getTickLength();
    }

    @Override
    public Sequence getSequence() {
        if (getSequencer() == null) {
            return null;
        }
        return getSequencer().getSequence();
    }

    @Override
    public long getMicrosecondPosition() {
        return getSequencer().getMicrosecondPosition();
    }

    @Override
    public long getMicrosecondLength() {
        return getSequencer().getMicrosecondLength();
    }

}
