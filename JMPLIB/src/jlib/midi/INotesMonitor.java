package jlib.midi;

public interface INotesMonitor {
    abstract void reset();
    abstract void resetNoteMonitor();
    abstract void clearNumOfNotes();
    abstract void analyzeMidiSequence();
    abstract long getNotesCount();
    abstract long getNumOfNotes();
    abstract double getNps();
    abstract int getPolyphony();
    abstract boolean isNoteOn(int channel, int midiNo);
    abstract int getTopNoteOnChannel(int midiNo);
    abstract int getTopNoteOnTrack(int midiNo);
    abstract int getNumOfTrack();    
    abstract int getPitchBend(int channel);
    abstract int getExpression(int channel);
}
