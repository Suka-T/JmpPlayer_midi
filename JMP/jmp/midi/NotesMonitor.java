package jmp.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jlib.midi.INotesMonitor;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;

public class NotesMonitor implements IMidiEventListener, INotesMonitor {

    private long notesCount = 0;
    private long numOfNotes = 0;

    private long startTime = System.currentTimeMillis();
    private long pastNotesCount = 0;
    private double nps = 0;

    private int[][] noteOnMonitor = null;
    
    private MidiEvent[][] nativeTracks = null;

    public NotesMonitor() {
        noteOnMonitor = new int[16][128];
        reset();
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        IMidiToolkit midiToolkit = JMPCore.getSoundManager().getMidiToolkit();
        if (midiToolkit.isNoteOn(message) == true) {
            notesCount++;
            
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            int data1 = MidiByte.getData1(mes, mes.length);
            noteOnMonitor[channel][data1] = 1;
        }
        else if (midiToolkit.isNoteOff(message) == true) {
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            int data1 = MidiByte.getData1(mes, mes.length);
            noteOnMonitor[channel][data1] = 0;
        }
    }

    @Override
    public void reset() {
        notesCount = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 128; j++) {
                noteOnMonitor[i][j] = 0;
            }
        }
    }

    @Override
    public void clearNumOfNotes() {
        numOfNotes = 0;
    }

    @Override
    public void analyzeMidiSequence() {
        clearNumOfNotes();
        reset();

        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null) {
            return;
        }

        Track[] tracks = sequence.getTracks();
        if (tracks == null) {
            return;
        }

        IMidiToolkit midiToolkit = JMPCore.getSoundManager().getMidiToolkit();
        nativeTracks = new MidiEvent[tracks.length][];
        for (int trkCount = 0; trkCount < tracks.length; trkCount++) {
            nativeTracks[trkCount] = new MidiEvent[tracks[trkCount].size()];
            for (int i = 0; i < nativeTracks[trkCount].length; i++) {
                nativeTracks[trkCount][i] = tracks[trkCount].get(i);
                MidiMessage message = nativeTracks[trkCount][i].getMessage();
                if (midiToolkit.isNoteOn(message) == true) {
                    numOfNotes++;
                }
            }
        }
    }

    public void timerEvent() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= 200) {
            long diff = notesCount - pastNotesCount;
            nps = (double) diff * 1000 / (currentTime - startTime);
            pastNotesCount = notesCount;
            startTime = currentTime;
        }
    }

    @Override
    public long getNotesCount() {
        return notesCount;
    }

    @Override
    public long getNumOfNotes() {
        return numOfNotes;
    }

    @Override
    public double getNps() {
        return nps;
    }
    
    @Override
    public int getPolyphony() {
        int num = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 128; j++) {
                if (noteOnMonitor[i][j] != 0) {
                    num++;
                }
            }
        }
        return num;
    }
    
    @Override
    public boolean isNoteOn(int channel, int midiNo) {
        if (noteOnMonitor[channel][midiNo] != 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public int getNumOfTrack() {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null || nativeTracks == null) {
            return 0;
        }
        return nativeTracks.length;
    }
    
    @Override
    public int getNumOfTrackEvent(int trackIndex) {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null || nativeTracks[trackIndex] == null) {
            return 0;
        }
        return nativeTracks[trackIndex].length;
    }
    
    @Override
    public final MidiEvent[] getTrack(int trackIndex) {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null || nativeTracks[trackIndex] == null) {
            return null;
        }
        return nativeTracks[trackIndex];
    }
    
    @Override
    public final MidiEvent getTrackEvent(int trackIndex, int eventIndex) {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null || nativeTracks == null || nativeTracks[trackIndex] == null) {
            return null;
        }
        return nativeTracks[trackIndex][eventIndex];
    }

}
