package jmp.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;

import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jlib.midi.INotesMonitor;
import jlib.midi.LightweightShortMessage;
import jlib.midi.MappedSequence;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;

public class NotesMonitor implements IMidiEventListener, INotesMonitor {
	
	public class KeyStateMonitor {
		public int channel = -1;
		public int track = -1;
		
		public KeyStateMonitor() {};
		public void reset() {
			channel = -1;
			track = -1;
		}
	}

    private long notesCount = 0;
    private long numOfNotes = 0;
    private int numOfTrack = 0;

    private long startTime = System.currentTimeMillis();
    private long pastNotesCount = 0;
    private double nps = 0;

    private int[][] noteOnMonitorChannel = null;
    private List<int[]> noteOnMonitorTrack = null;
    private KeyStateMonitor[] noteOnMonitorChannelTop = null;
    private int[] pitchBendMonitor = null;
    private int[] expressionMonitor = null;

    public NotesMonitor() {
        noteOnMonitorChannel = new int[16][128];
        noteOnMonitorTrack = new ArrayList<int[]>();
        noteOnMonitorChannelTop = new KeyStateMonitor[128];
        for (int i = 0; i < noteOnMonitorChannelTop.length; i++) {
        	noteOnMonitorChannelTop[i] = new KeyStateMonitor();
        }
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
            int trackIndex = 0;
            if (message instanceof LightweightShortMessage) {
            	LightweightShortMessage lwMes = (LightweightShortMessage)message;
            	trackIndex = lwMes.getTrackIndex();
            }
            
            noteOnMonitorChannel[channel][data1] = 1;
            noteOnMonitorTrack.get(trackIndex)[data1] = 1;
            
            int topChStat = -1;
            for (int i = 15; i >= 0; i--) {
                if (noteOnMonitorChannel[i][data1] != 0) {
                	topChStat = i;
                    break;
                }
            }
            noteOnMonitorChannelTop[data1].channel = topChStat;
            int topTrkStat = -1;
            for (int i = getNumOfTrack() - 1; i >= 0; i--) {
                if (noteOnMonitorTrack.get(i)[data1] != 0) {
                	topTrkStat = i;
                    break;
                }
            }
            noteOnMonitorChannelTop[data1].track = topTrkStat;
        }
        else if (midiToolkit.isNoteOff(message) == true) {
            byte[] mes = message.getMessage();
            int channel = MidiByte.getChannel(mes, mes.length);
            int data1 = MidiByte.getData1(mes, mes.length);
            int trackIndex = 0;
            if (message instanceof LightweightShortMessage) {
            	LightweightShortMessage lwMes = (LightweightShortMessage)message;
            	trackIndex = lwMes.getTrackIndex();
            }
            
            noteOnMonitorChannel[channel][data1] = 0;
            noteOnMonitorTrack.get(trackIndex)[data1] = 0;
            
            int topChStat = -1;
            for (int i = 15; i >= 0; i--) {
                if (noteOnMonitorChannel[i][data1] != 0) {
                	topChStat = i;
                    break;
                }
            }
            noteOnMonitorChannelTop[data1].channel = topChStat;
            int topTrkStat = -1;
            for (int i = getNumOfTrack() - 1; i >= 0; i--) {
                if (noteOnMonitorTrack.get(i)[data1] != 0) {
                	topTrkStat = i;
                    break;
                }
            }
            noteOnMonitorChannelTop[data1].track = topTrkStat;
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
        }
        
        resetNoteMonitor();
    }
    
    @Override
    public void resetNoteMonitor() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 128; j++) {
                noteOnMonitorChannel[i][j] = 0;
                noteOnMonitorChannelTop[j].reset();
            }
        }
        
        noteOnMonitorTrack.clear();
        for (int i = 0; i < getNumOfTrack(); i++) {
        	noteOnMonitorTrack.add(new int[128]);
        	for (int j = 0; j < 128; j++) {
        		noteOnMonitorTrack.get(i)[j] = 0;
        	}
        }
    }

    @Override
    public void clearNumOfNotes() {
        numOfNotes = 0;
    }

    @Override
    public void analyzeMidiSequence() {

    	long newNumOfNotes = 0;
    	int newNumOfTrack = 0;
    	clearNumOfNotes();
    	
        Sequence sequence = JMPCore.getSoundManager().getMidiUnit().getSequence();
        if (sequence != null) {
        	newNumOfNotes = ((MappedSequence)sequence).getNumOfNotes();
        	newNumOfTrack = ((MappedSequence)sequence).getNumTracks();
        }
        numOfNotes = newNumOfNotes;
        numOfTrack = newNumOfTrack;
        reset();
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
                if (noteOnMonitorChannel[i][j] != 0) {
                    num++;
                }
            }
        }
        return num;
    }
    
    @Override
    public boolean isNoteOn(int channel, int midiNo) {
        if (noteOnMonitorChannel[channel][midiNo] != 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public int getTopNoteOnChannel(int midiNo) {
        return noteOnMonitorChannelTop[midiNo].channel;
    }
    
    @Override
    public int getTopNoteOnTrack(int midiNo) {
        return noteOnMonitorChannelTop[midiNo].track;
    }
    
    @Override
    public int getNumOfTrack() {
        return numOfTrack;
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
