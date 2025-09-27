package jmp.midi;

import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.midi.InvalidMidiDataException;

public class MidiShortMessagePool {
    private final ArrayBlockingQueue<LightweightShortMessage> pool;

    // ====== pack/unpack (ShortMessage → int) ======
    public static int packShortMsg(int status, int data1, int data2) {
        return (status & 0xFF) | ((data1 & 0xFF) << 8) | ((data2 & 0xFF) << 16);
    }

    public static int getStatus(int msg) {
        return msg & 0xFF;
    }

    public static int getData1(int msg) {
        return (msg >> 8) & 0xFF;
    }

    public static int getData2(int msg) {
        return (msg >> 16) & 0xFF;
    }

    // ====== pack/unpack (track + shortMsg → long) ======
    public static long packTrackMsg(int track, int shortMsg) {
        return ((long) track << 32) | (shortMsg & 0xFFFFFFFFL);
    }

    public static int getTrack(long packed) {
        return (int) (packed >> 32);
    }

    public static int getShortMsg(long packed) {
        return (int) packed;
    }

    public MidiShortMessagePool(int size) {
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.offer(new LightweightShortMessage());
        }
    }

    public LightweightShortMessage borrow(long packed) throws InvalidMidiDataException {
        LightweightShortMessage msg = pool.poll();
        if (msg == null) {
            // 足りない場合は新しく生成（ただしGCが出る）
            msg = new LightweightShortMessage();
            System.out.println("Over pool");
        }
        
        int trkPacked = getTrack(packed);
        int msgPacked = getShortMsg(packed);
        
        msg.setTrackIndex((short)trkPacked);
        msg.setPackedMsg(msgPacked);
        return msg;
    }

    public void release(LightweightShortMessage msg) {
        pool.offer(msg);
    }

}
