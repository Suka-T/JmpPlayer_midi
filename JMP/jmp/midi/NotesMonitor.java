package jmp.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jlib.midi.IMidiEventListener;
import jlib.midi.INotesMonitor;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;

public class NotesMonitor implements IMidiEventListener, INotesMonitor {

    public class KeyStateMonitor {
        public int channel = -1;
        public int track = -1;

        public KeyStateMonitor() {
        };

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
    private int[] pitchBendMonitor = null;
    private int[] expressionMonitor = null;

    public NotesMonitor() {
        noteOnMonitorChannel = new int[16][128];
        noteOnMonitorTrack = new ArrayList<int[]>();
        pitchBendMonitor = new int[16];
        expressionMonitor = new int[16];
        reset();
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        if (message instanceof LightweightShortMessage) {
            LightweightShortMessage sm = (LightweightShortMessage) message;
            int command = sm.getCommand();
            int channel = sm.getChannel();
            int data1 = sm.getData1();
            int data2 = sm.getData2();
            int trackIndex = sm.getTrackIndex();
            if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) && (data2 > 0)) {
                notesCount++;
                noteOnMonitorChannel[channel][data1] = 1;
                if (trackIndex < noteOnMonitorTrack.size()) {
                    noteOnMonitorTrack.get(trackIndex)[data1] = 1;
                }
            }
            else if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF)
                    || (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && data2 <= 0)) {
                noteOnMonitorChannel[channel][data1] = 0;
                if (trackIndex < noteOnMonitorTrack.size()) {
                    noteOnMonitorTrack.get(trackIndex)[data1] = 0;
                }
            }
            else if (command == MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND) {
                pitchBendMonitor[channel] = MidiByte.mergeLsbMsbValue(data1, data2) - 8192;
            }
            else if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE) && data1 == 11) {
                expressionMonitor[channel] = data2;
            }
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

        if (JMPCore.getSoundManager().getMidiUnit().isValidSequence() == true) {
            newNumOfNotes = JMPCore.getSoundManager().getMidiUnit().getNumOfNote();
            newNumOfTrack = JMPCore.getSoundManager().getMidiUnit().getNumOfTrack();
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
        int topChStat = -1;
        for (int i = 15; i >= 0; i--) {
            if (noteOnMonitorChannel[i][midiNo] != 0) {
                topChStat = i;
                break;
            }
        }
        return topChStat;
    }

    @Override
    public int getTopNoteOnTrack(int midiNo) {
        int topTrkStat = -1;
        for (int i = getNumOfTrack() - 1; i >= 0; i--) {
            if (i < noteOnMonitorTrack.size()) {
                if (noteOnMonitorTrack.get(i)[midiNo] != 0) {
                    topTrkStat = i;
                    break;
                }
            }
        }
        return topTrkStat;
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
