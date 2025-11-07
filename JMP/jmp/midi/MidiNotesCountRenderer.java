package jmp.midi;

import java.util.ArrayList;
import java.util.List;

public class MidiNotesCountRenderer {
    public static final long BLOCK_MAX_TICK = 1000000;
    
    private List<int[]> lst = null;
    private long resolution = 100;
    
    private Object mutex = new Object();

    public MidiNotesCountRenderer() {
        lst = new ArrayList<int[]>();
    }
    
    public void initialize(long resolution) {
        this.resolution = resolution;
        lst.clear();
    }
    
    public void count(long tick) {
        int index1 = (int) (tick / BLOCK_MAX_TICK);
        int index2 = (int) ((tick % BLOCK_MAX_TICK) / resolution);
        if (lst.size() <= index1) {
            synchronized (mutex) {
                int[] a = new int[(int) (BLOCK_MAX_TICK / resolution)];
                for (int i=0; i<a.length; i++) {
                    a[i] = 0;
                }
                lst.add(a);
            }
        }
        
        lst.get(index1)[index2]++;
    }
    
    public long getNotesCount(long currentTick) {
        if (lst.isEmpty()) {
            return 0;
        }
        
        long nc = 0;
        int index1 = (int) (currentTick / BLOCK_MAX_TICK);
        int index2 = 0;
        for (int i=0; i<=index1; i++) {
            if (lst.size() <= i) {
                continue;
            }
            
            int[] array = lst.get(i);
            if (i == index1) {
                index2 = (int) ((currentTick % BLOCK_MAX_TICK) / resolution);
            }
            else {
                index2 = (int) (BLOCK_MAX_TICK / resolution);
            }
            if (array != null) {
                for (int j=0; j<index2; j++) {
                    nc += array[j];
                }
            }
        }
        return nc;
    }
}
