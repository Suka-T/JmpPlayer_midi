package jlib.midi;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.concurrent.TimeUnit;

public class MappedParseFunc {

    public static final byte READ_FLAG_META_MESSAGE = 0x01;
    public static final byte READ_FLAG_SHORT_MESSAGE = 0x02;
    public static final byte READ_FLAG_SYSEX_MESSAGE = 0x04;
    public static final byte READ_FLAG_ALL = (READ_FLAG_META_MESSAGE | READ_FLAG_SHORT_MESSAGE | READ_FLAG_SYSEX_MESSAGE);

    // private StringBuilder sb = new StringBuilder(64); // 初期容量を指定
    private long startTick = -1;
    private long endTick = -1;
    private long waitMargin = 1000;
    private long waitTime = 2;
    private byte readFlag = READ_FLAG_ALL;

    public void setWaintTime(long margin, long waitTime) {
        this.waitMargin = margin;
        this.waitTime = waitTime;
    }

    public MappedParseFunc() {
        this.startTick = -1;
        this.endTick = -1;
    }

    public MappedParseFunc(long startTick, long endTick) {
        this.startTick = startTick;
        this.endTick = endTick;
        this.readFlag = READ_FLAG_ALL;
    }
    
    public MappedParseFunc(long startTick, long endTick, byte readFlag) {
        this.startTick = startTick;
        this.endTick = endTick;
        this.readFlag = readFlag;
    }

    public void setStartTick(long tick) {
        startTick = tick;
    }

    public long getStartTick() {
        return startTick;
    }

    public void setEndTick(long tick) {
        endTick = tick;
    }

    public long getEndTick() {
        return endTick;
    }

    public byte getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(byte readFlag) {
        this.readFlag = readFlag;
    }

    public void metaMessage(int trk, long tick, int type, byte[] metaData, int length) {
    };

    public void shortMessage(int trk, long tick, int statusByte, int data1, int data2) {
    };

    public void sysexMessage(int trk, long tick, int statusByte, byte[] sysexData, int length) {
    };

    public void calcTick(int trk, int tick) {
    };

    public boolean interrupt() {
        return false;
    }

    public void end() {

    }

    public void endTrack(int trk) {
    }
    
    protected boolean isMeta(int statusByte) {
        if (statusByte == 0xFF) { // Meta
            return true;
        }
        return false;
    }
    
    protected boolean isShort(int statusByte) {
        if (statusByte >= 0x80 && statusByte <= 0xEF) { // Channel
            return true;
        }
        return false;
    }
    
    protected boolean isSysEx(int statusByte) {
        if (statusByte == 0xF0 || statusByte == 0xF7) { // SysEx
            return true;
        }
        return false;
    }

    public void parse(int trk, MappedByteBuffer buf) throws IOException {
        buf.rewind();

        int tick = 0;
        int lastStatus = 0;

        // sb.setLength(0);
        // sb.append("[READ_");
        // sb.append(trk);
        // sb.append("]");
        // sb.append(this.startTick);
        // sb.append("-");
        // sb.append(this.endTick);
        // System.out.println(sb.toString());

        int delta = 0;
        int statusByte = 0;
        boolean inRange = false;
        int type = 0;
        int length = 0;
        int command = 0;
        int data1 = 0;
        int data2 = 0;
        long pastMillis = System.currentTimeMillis();
        long curMillis = System.currentTimeMillis();
        while (buf.hasRemaining()) {
            if (interrupt() == true) {
                break;
            }

            curMillis = System.currentTimeMillis();
            if ((curMillis - pastMillis) > waitMargin) {
                pastMillis = System.currentTimeMillis();
                try {
                    TimeUnit.MILLISECONDS.sleep(waitTime <= 0 ? 1 : waitTime);
                }
                catch (InterruptedException e) {
                }
            }

            delta = readVariableLength(buf);
            tick += delta;

            calcTick(trk, tick);

            if (tick > this.endTick && this.endTick != -1) {
                return;
            }

            statusByte = buf.get() & 0xFF;
            if (statusByte < 0x80) {
                // if (lastStatus == 0) throw new IOException("Invalid running
                // status");
                buf.position(buf.position() - 1);
                statusByte = lastStatus;
            }
            else {
                lastStatus = statusByte;
            }

            // イベントデータを処理 or スキップ
            inRange = (this.startTick == -1 || tick >= this.startTick);

            if (isMeta(statusByte)) { // Meta
                type = buf.get() & 0xFF;
                length = readVariableLength(buf);
                if ((readFlag & READ_FLAG_META_MESSAGE) != 0) {
                    byte[] metaData = new byte[length];
                    buf.get(metaData);
                    if (inRange) {
                        metaMessage(trk, tick, type, metaData, length);
                    }
                }
                else {
                    buf.position(buf.position() + length);
                }
            }
            else if (isShort(statusByte)) { // Channel
                command = statusByte & 0xF0;
                data1 = buf.get() & 0xFF;
                data2 = (command != 0xC0 && command != 0xD0) ? (buf.get() & 0xFF) : 0;
                if ((readFlag & READ_FLAG_SHORT_MESSAGE) != 0) {
                    if (inRange) {
                        shortMessage(trk, tick, statusByte, data1, data2);
                    }
                }
            }
            else if (isSysEx(statusByte)) { // SysEx
                length = readVariableLength(buf);
                if ((readFlag & READ_FLAG_SYSEX_MESSAGE) != 0) {
                    byte[] sysexData = new byte[length];
                    buf.get(sysexData);
                    if (inRange) {
                        sysexMessage(trk, tick, statusByte, sysexData, length);
                    }
                }
                else {
                    buf.position(buf.position() + length);
                }
            }
            else {
                // throw new IOException("Unknown status byte: " + statusByte);
            }
        }

        endTrack(trk);
        end();
    }

    private static int readVariableLength(ByteBuffer buf) throws IOException {
        int value = 0;
        int b;
        do {
            if (!buf.hasRemaining())
                throw new EOFException();
            b = buf.get() & 0xFF;
            value = (value << 7) | (b & 0x7F);
        }
        while ((b & 0x80) != 0);
        return value;
    }
}
