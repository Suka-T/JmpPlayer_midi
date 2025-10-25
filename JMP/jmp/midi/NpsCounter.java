package jmp.midi;

public class NpsCounter {
    
    private long startTime = System.currentTimeMillis();
    private long pastNotesCount = 0;
    private double nps = 0.0;
    private long delay = 0;

    public NpsCounter() {
    }
    
    public void reset() {
        nps = 0.0;
        delay = 0;
        pastNotesCount = 0;
        startTime = System.currentTimeMillis();
    }
    
    public boolean timerEvent(long currentNotesCount) {
        if (0 < this.delay) {
            this.delay--;
            startTime = System.currentTimeMillis();
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= 1000) {
            long diff = currentNotesCount - pastNotesCount;
            nps = (double) diff * 1000 / (currentTime - startTime);
            pastNotesCount = currentNotesCount;
            startTime = currentTime;
            return true;
        }
        return false;
    }

    public double getNps() {
        return nps;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

}
