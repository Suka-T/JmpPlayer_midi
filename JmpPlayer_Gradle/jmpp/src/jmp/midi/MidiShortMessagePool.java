package jmp.midi;

import javax.sound.midi.InvalidMidiDataException;

import jlib.util.ObjectPool;
import jlib.util.ObjectPool.QueueType;

public class MidiShortMessagePool {
    // private final ArrayBlockingQueue<LightweightShortMessage> pool;
    private ObjectPool<LightweightShortMessage> pool;

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
        pool = new ObjectPool<LightweightShortMessage>(QueueType.ConcurrentLinkedQueue, size, LightweightShortMessage::new);
    }

    public LightweightShortMessage borrow(long packed) throws InvalidMidiDataException {
        LightweightShortMessage msg = pool.borrow();

        int trkPacked = getTrack(packed);
        int msgPacked = getShortMsg(packed);

        msg.setTrackIndex((short) trkPacked);
        msg.setPackedMsg(msgPacked);
        return msg;
    }

    public void release(LightweightShortMessage msg) {
        pool.release(msg);
    }
}
