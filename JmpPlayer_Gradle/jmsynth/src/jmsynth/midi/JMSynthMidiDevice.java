package jmsynth.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthEngine;

public class JMSynthMidiDevice implements MidiDevice {

    private static class JMSInfo extends MidiDevice.Info {
        protected JMSInfo() {
            super(JMSoftSynthesizer.INFO_NAME, JMSoftSynthesizer.INFO_VENDOR, JMSoftSynthesizer.INFO_DESCRIPTION, JMSoftSynthesizer.INFO_VERSION);
        }
    }

    /** デバイス情報 */
    public static Info INFO = new JMSInfo();

    private boolean isOpen = false;
    private MidiInterface iface;
    private List<Receiver> receivers = null;
    private List<Transmitter> transmitters = null;

    public JMSynthMidiDevice() {
        iface = JMSynthEngine.getMidiInterface(false);
        transmitters = new ArrayList<Transmitter>();
        receivers = new ArrayList<Receiver>();
        receivers.add(iface);
    }

    public MidiInterface getJMsynthInterface() {
        return iface;
    }

    @Override
    public Info getDeviceInfo() {
        return INFO;
    }

    @Override
    public void open() throws MidiUnavailableException {
        iface.open();
        isOpen = true;
    }

    @Override
    public void close() {
        iface.close();
        isOpen = false;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public long getMicrosecondPosition() {
        return 0;
    }

    @Override
    public int getMaxReceivers() {
        return receivers.size();
    }

    @Override
    public int getMaxTransmitters() {
        return transmitters.size();
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        if (getMaxReceivers() <= 0) {
            return null;
        }
        return receivers.get(0);
    }

    @Override
    public List<Receiver> getReceivers() {
        return receivers;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        if (getMaxTransmitters() <= 0) {
            return null;
        }
        return transmitters.get(0);
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return transmitters;
    }

}
