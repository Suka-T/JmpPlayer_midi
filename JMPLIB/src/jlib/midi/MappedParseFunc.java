package jlib.midi;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class MappedParseFunc {
    private StringBuilder sb = new StringBuilder(64); // 初期容量を指定
    private long startTick = -1;
    private long endTick = -1;

    public MappedParseFunc() {
        this.startTick = -1;
        this.endTick = -1;
    }

    public MappedParseFunc(long startTick, long endTick) {
        this.startTick = startTick;
        this.endTick = endTick;
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
        while (buf.hasRemaining()) {
            if (interrupt() == true) {
                break;
            }
            
            delta = readVariableLength(buf);
            tick += delta;

            calcTick(trk, tick);

            if (tick > this.endTick && this.endTick != -1) {
                break;
            }

            statusByte = buf.get() & 0xFF;
            if (statusByte < 0x80) {
                if (lastStatus == 0)
                    throw new IOException("Invalid running status");
                buf.position(buf.position() - 1);
                statusByte = lastStatus;
            }
            else {
                lastStatus = statusByte;
            }

            // イベントデータを処理 or スキップ
            inRange = (this.startTick == -1 || tick >= this.startTick);

            if (statusByte == 0xFF) { // Meta
                type = buf.get() & 0xFF;
                length = readVariableLength(buf);
                byte[] metaData = new byte[length];
                buf.get(metaData);
                if (inRange) {
                    metaMessage(trk, tick, type, metaData, length);
                }
            }
            else if (statusByte >= 0x80 && statusByte <= 0xEF) { // Channel
                command = statusByte & 0xF0;
                data1 = buf.get() & 0xFF;
                data2 = (command != 0xC0 && command != 0xD0) ? (buf.get() & 0xFF) : 0;
                if (inRange) {
                    shortMessage(trk, tick, statusByte, data1, data2);
                }
            }
            else if (statusByte == 0xF0 || statusByte == 0xF7) { // SysEx
                length = readVariableLength(buf);
                byte[] sysexData = new byte[length];
                buf.get(sysexData);
                if (inRange) {
                    sysexMessage(trk, tick, statusByte, sysexData, length);
                }
            }
            else {
                throw new IOException("Unknown status byte: " + statusByte);
            }
        }
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
