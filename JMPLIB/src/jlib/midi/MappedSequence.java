package jlib.midi;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;

public class MappedSequence extends Sequence {
	private long tickLength = 0; 
	private int numTracks = 0;
	private long numOfNotes = 0;
	private List<MappedByteBuffer> mappedByteBuffers = null;

	public MappedSequence(float divisionType, int resolution, int numTracks) throws InvalidMidiDataException {
		super(divisionType, resolution, numTracks);
		
		mappedByteBuffers = new ArrayList<MappedByteBuffer>();
		this.numTracks = numTracks;
	}
	
	public void addMap(MappedByteBuffer map) {
		mappedByteBuffers.add(map);
	}
	
	public MappedByteBuffer getMap(int index) {
		return mappedByteBuffers.get(index);
	}
	
	public void parse(short trkIndex, MappedParseFunc func) throws Exception {
		long startTick = func.getStartTick();
		long endTick = func.getEndTick();
		if (startTick != -1) {
			if (startTick < 0) {
				startTick = 0;
			}
			else if (getTickLength() < startTick) {
				startTick = getTickLength();
			}
		}
		if (endTick != -1) {
			if (endTick < 0) {
				endTick = 0;
			}
			else if (getTickLength() < endTick) {
				endTick = getTickLength();
			}
		}
		
		if (endTick != -1 && startTick != -1) {
			if (endTick < startTick) {
				// conflict args
				return;
			}
		}
		// renew tick 
		func.setStartTick(startTick);
		func.setEndTick(endTick);
		
		MappedByteBuffer org = getMap(trkIndex);
		MappedByteBuffer copy = org.duplicate();
		func.parse(trkIndex, copy);
	}

    public List<MidiEvent> parseTrackBuffer(short trkIndex) throws Exception {
    	ByteBuffer buf = getMap(trkIndex);
        List<MidiEvent> events = new ArrayList<>();
        int tick = 0;
        int lastStatus = 0;

        while (buf.hasRemaining()) {
            int delta = readVariableLength(buf);
            tick += delta;

            int statusByte = buf.get() & 0xFF;
            if (statusByte < 0x80) {
                //if (lastStatus == 0) throw new IOException("Invalid running status");
                buf.position(buf.position() - 1);
                statusByte = lastStatus;
            } else {
                lastStatus = statusByte;
            }

            if (statusByte == 0xFF) {
                int type = buf.get() & 0xFF;
                int length = readVariableLength(buf);
                byte[] metaData = new byte[length];
                buf.get(metaData);
                MetaMessage meta = new MetaMessage();
                meta.setMessage(type, metaData, length);
                events.add(new MidiEvent(meta, tick));
            } else if (statusByte >= 0x80 && statusByte <= 0xEF) {
                int command = statusByte & 0xF0;
                int data1 = buf.get() & 0xFF;
                int data2 = (command != 0xC0 && command != 0xD0) ? (buf.get() & 0xFF) : 0;
                LightweightShortMessage sm = new LightweightShortMessage(
                        statusByte | (data1 << 8) | (data2 << 16), trkIndex);
                events.add(new MidiEvent(sm, tick));
            } else if (statusByte == 0xF0 || statusByte == 0xF7) {
                int length = readVariableLength(buf);
                byte[] sysexData = new byte[length];
                buf.get(sysexData);
                SysexMessage sx = new SysexMessage();
                sx.setMessage(statusByte, sysexData, length);
                events.add(new MidiEvent(sx, tick));
            } else {
                throw new IOException("Unknown status byte: " + statusByte);
            }
        }

        System.out.println("parsed track " + (trkIndex + 1));
        return events;
    }

    private static int readVariableLength(ByteBuffer buf) throws IOException {
        int value = 0;
        int b;
        do {
            if (!buf.hasRemaining()) throw new EOFException();
            b = buf.get() & 0xFF;
            value = (value << 7) | (b & 0x7F);
        } while ((b & 0x80) != 0);
        return value;
    }

	public int getNumTracks() {
		return numTracks;
	}
	
	public void setTickLength(long tickLen) {
		tickLength = tickLen;
	}
	
	@Override
    public long getTickLength() {
		return tickLength;
    }

	public long getNumOfNotes() {
		return numOfNotes;
	}

	public void setNumOfNotes(long numOfNotes) {
		this.numOfNotes = numOfNotes;
	}
}
