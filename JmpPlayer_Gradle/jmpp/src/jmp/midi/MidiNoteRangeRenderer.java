package jmp.midi;

import java.util.ArrayList;
import java.util.List;

public class MidiNoteRangeRenderer {
    private List<int[]> lst = null;
    private long resolution = 100;
    private long blockMaxTick = 10000;
    
    private Object mutex = new Object();

    public MidiNoteRangeRenderer() {
        lst = new ArrayList<int[]>();
    }
    
    public void initialize(long resolution) {
        this.resolution = resolution;
        this.blockMaxTick = resolution * 100;
        lst.clear();
    }
    
    public void count(long tick, int noteNo) {
        int index1 = (int) (tick / blockMaxTick);
        int index2 = (int) ((tick % blockMaxTick) / resolution);
        if (lst.size() <= index1) {
            synchronized (mutex) {
                int[] a = new int[(int) (blockMaxTick / resolution)];
                for (int i=0; i<a.length; i++) {
                    a[i] = 4;
                }
                lst.add(a);
            }
        }
        
        int ran = 4;
        if (noteNo < 12 || 114 < noteNo) {
        	ran = 0; // Full 
        }
        else if (noteNo < 21 || 108 < noteNo) { 
        	ran = 1; // Full 
        }
        else if (noteNo < 28 || 103 < noteNo) {
        	ran = 2; // 88K
        }
        else if (noteNo < 36 || 96 < noteNo) { 
        	ran = 3; // 76K
        }
        else /*if (noteNo < 47 || 79 <= noteNo) */{
        	ran = 4; // 61K
        }
        
        if (ran < lst.get(index1)[index2]) {
        	lst.get(index1)[index2] = ran;
        }
    }
    
    public int getNoteRange(long currentTick) {
        if (lst.isEmpty()) {
            return 0;
        }

        int index1 = (int) (currentTick / blockMaxTick);
        int index2 = (int) ((currentTick % blockMaxTick) / resolution);
        if (lst.size() <= index1) {
        	return 0;
        }
        return lst.get(index1)[index2];
    }
}
