package jmp.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jlib.midi.INotesMonitor;
import jlib.midi.MidiByte;
import jmp.JMPFlags;
import jmp.core.JMPCore;

public class NotesMonitor implements IMidiEventListener, INotesMonitor {

    private long notesCount = 0;
    private long numOfNotes = 0;

    private long startTime = System.currentTimeMillis();
    private long pastNotesCount = 0;
    private double nps = 0;

    private int[][] noteOnMonitor = null;
    private int[] noteOnMonitorTop = null;
    private int[] pitchBendMonitor = null;
    private int[] expressionMonitor = null;
    
    private MidiEvent[][] nativeTracks = null;

    public NotesMonitor() {
        noteOnMonitor = new int[16][128];
        noteOnMonitorTop = new int[128];
        pitchBendMonitor = new int[16];
        expressionMonitor = new int[16];
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
            
            int topStat = -1;
            for (int i = 15; i >= 0; i--) {
                if (noteOnMonitor[i][data1] != 0) {
                    topStat = i;
                    break;
                }
            }
            noteOnMonitorTop[data1] = topStat;
        }
        else if (midiToolkit.isNoteOff(message) == true) {
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            int data1 = MidiByte.getData1(mes, mes.length);
            noteOnMonitor[channel][data1] = 0;
            
            int topStat = -1;
            for (int i = 15; i >= 0; i--) {
                if (noteOnMonitor[i][data1] != 0) {
                    topStat = i;
                    break;
                }
            }
            noteOnMonitorTop[data1] = topStat;
        }
        else if (midiToolkit.isPitchBend(message) == true) {
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            int lsb = MidiByte.getData1(mes, mes.length);
            int msb = MidiByte.getData2(mes, mes.length);
            pitchBendMonitor[channel] = MidiByte.mergeLsbMsbValue(lsb, msb) - 8192;
        }
        else if (midiToolkit.isExpression(message) == true) {
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            expressionMonitor[channel] = MidiByte.getData2(mes, mes.length);
        }
    }

    @Override
    public void reset() {
        notesCount = 0;
        for (int i = 0; i < 16; i++) {
            expressionMonitor[i] = 127;
            pitchBendMonitor[i] = 0;
            for (int j = 0; j < 128; j++) {
                noteOnMonitor[i][j] = 0;
                noteOnMonitorTop[j] = -1;
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
        for (int trkCount = 0; trkCount < tracks.length; trkCount++) {
            for (int i = 0; i < tracks[trkCount].size(); i++) {
                MidiMessage message = tracks[trkCount].get(i).getMessage();
                if (midiToolkit.isNoteOn(message) == true) {
                    numOfNotes++;
                }
            }
        }
        
        nativeTracks = null;
        if (JMPFlags.MakeNativeMidiEventFlag == true) {
            nativeTracks = new MidiEvent[tracks.length][];
            for (int trkCount = 0; trkCount < tracks.length; trkCount++) {
                nativeTracks[trkCount] = new MidiEvent[tracks[trkCount].size()];
                for (int i = 0; i < nativeTracks[trkCount].length; i++) {
                    nativeTracks[trkCount][i] = tracks[trkCount].get(i);
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
    public int getTopNoteOnChannel(int midiNo) {
        return noteOnMonitorTop[midiNo];
    }
    
    @Override
    public int getNumOfTrack() {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null) {
            return 0;
        }
        
        if (JMPFlags.MakeNativeMidiEventFlag == true) {
            if (nativeTracks == null) {
                return 0;
            }
            return nativeTracks.length;
        }
        else {
            return sequence.getTracks().length;
        }
    }
    
    @Override
    public int getNumOfTrackEvent(int trackIndex) {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null) {
            return 0;
        }
        
        if (JMPFlags.MakeNativeMidiEventFlag == true) {
            if (nativeTracks == null || nativeTracks[trackIndex] == null) {
                return 0;
            }
            return nativeTracks[trackIndex].length;
        }
        else {
            return sequence.getTracks()[trackIndex].size();
        }
    }
    
    @Override
    public final MidiEvent getTrackEvent(int trackIndex, int eventIndex) {
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence == null) {
            return null;
        }
        
        if (JMPFlags.MakeNativeMidiEventFlag == true) {
            if (nativeTracks == null || nativeTracks[trackIndex] == null) {
                return null;
            }
            return nativeTracks[trackIndex][eventIndex];
        }
        else {
            return sequence.getTracks()[trackIndex].get(eventIndex);
        }
    }
    
    @Override
    public int getPitchBend(int channel) {
        return pitchBendMonitor[channel];
    }
    
    @Override
    public int getExpression(int channel) {
        return expressionMonitor[channel];
    }

}
