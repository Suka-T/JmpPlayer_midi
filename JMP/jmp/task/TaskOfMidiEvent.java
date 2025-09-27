package jmp.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jlib.midi.IMidiEventListener;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.midi.NotesMonitor;

public class TaskOfMidiEvent extends TaskOfBase {

    public class JmpMidiPacket {
        public MidiMessage message = null;
        public long timeStamp = 0;
        public short senderType = IMidiEventListener.SENDER_MIDI_OUT;

        public JmpMidiPacket(MidiMessage message, long timeStamp, short senderType) {
            this.message = message;
            this.timeStamp = timeStamp;
            this.senderType = senderType;
        }
    }

    private List<JmpMidiPacket> stack = null;
    private Object mutex = new Object();

    public TaskOfMidiEvent(int priority) {
        super(20, priority, true);
        stack = Collections.synchronizedList(new LinkedList<JmpMidiPacket>());
    }

    public void add(MidiMessage message, long timeStamp, short senderType) {

        JmpMidiPacket packet = new JmpMidiPacket(message, timeStamp, senderType);
        synchronized (mutex) {
            // プラグインに送信するパケットを発行
            stack.add(packet);
        }
    }

    @Override
    public void clearQue() {
        super.clearQue();
        synchronized (mutex) {
            stack.clear();
        }
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        PluginManager pm = JMPCore.getPluginManager();
        synchronized (mutex) {
            // スタックされたパケットをプラグインに送信
            Iterator<JmpMidiPacket> i = stack.iterator();
            while (i.hasNext()) {
                JmpMidiPacket packet = i.next();
                pm.processMidiEvent(packet.message, packet.timeStamp, packet.senderType);
                i.remove();
            }
        }
        
        ((NotesMonitor) JMPCore.getSoundManager().getNotesMonitor()).timerEvent();
    }

    @Override
    void end() {
    }

    @Override
    protected void notifySleepTimeCalc(long sleepTime) {
        // System.out.println("" + sleepTime);
    }

}
