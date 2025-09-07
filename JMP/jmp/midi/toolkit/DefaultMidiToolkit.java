package jmp.midi.toolkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import jlib.core.ISoundManager;
import jlib.midi.IMidiToolkit;
import jlib.midi.MidiByte;
import jmp.core.JMPCore;
import jmp.midi.JMPBuiltinSynthMidiDevice;
import jmp.midi.JMPMidiReader;
import jmp.midi.MidiByteMessage;

public class DefaultMidiToolkit implements IMidiToolkit {
    public static final String DEFAULT_RECVNAME = "Gervill";
    public static final String RECOMMENDED_RECVNAMES[] = { "OmniMIDI", "Keppy" };
    
    DefaultMidiToolkit() {
    };

    @Override
    public List<MidiDevice> getMidiDevices() {
        ArrayList<MidiDevice> devices = new ArrayList<MidiDevice>();

        MidiDevice.Info[] infos = getMidiDeviceInfo(true, true);

        for (int i = 0; i < infos.length; i++) {
            MidiDevice.Info info = infos[i];
            MidiDevice device = null;

            try {
                device = getMidiDevice(info);
                devices.add(device);
            }
            catch (MidiUnavailableException me) {
            }
            catch (Exception e) {
            }
        }

        return devices;
    }

    @Override
    public Sequence readMidiFile(File file) throws InvalidMidiDataException, IOException {
        Sequence seq = null;
        try {
            seq = JMPMidiReader.parseSmf(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return seq;
        // JmpMidiFileReader reader = new JmpMidiFileReader();
        // return reader.getSequence(file);
        // return MidiSystem.getSequence(file);
    }

    @Override
    public MidiDevice.Info[] getMidiDeviceInfo(boolean incTransmitter, boolean incReciever) {
        ArrayList<MidiDevice.Info> ret = new ArrayList<MidiDevice.Info>();

        // 内蔵シンセ追加
        ret.add(JMPBuiltinSynthMidiDevice.INFO);

        MidiDevice.Info[] tmp = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < tmp.length; i++) {
            ret.add(tmp[i]);
        }

        for (int i = ret.size() - 1; i >= 0; i--) {
            MidiDevice dev;
            try {
                dev = getMidiDevice(ret.get(i));
            }
            catch (MidiUnavailableException e) {
                ret.remove(i);
                continue;
            }
            if (incTransmitter == false && dev.getMaxTransmitters() != 0) {
                // Transmitterは除外
                ret.remove(i);
                continue;
            }
            else if (incReciever == false && dev.getMaxReceivers() != 0) {
                // Recieverは除外
                ret.remove(i);
                continue;
            }
        }
        return (MidiDevice.Info[]) ret.toArray(new MidiDevice.Info[0]);
    }

    @Override
    public MidiDevice getMidiDevice(Info info) throws MidiUnavailableException {
        // 内蔵シンセ
        if (JMPBuiltinSynthMidiDevice.INFO.equals(info) == true) {
            return new JMPBuiltinSynthMidiDevice();
        }
        return MidiSystem.getMidiDevice(info);
    }

    @Override
    public MidiDevice getMidiDevice(String name) throws MidiUnavailableException {
        MidiDevice dev = null;
        MidiDevice.Info recInfo = null;
        for (MidiDevice.Info info : getMidiDeviceInfo(true, true)) {
            if (info.getName().equals(name) == true) {
                recInfo = info;
                break;
            }
        }
        if (recInfo != null) {
            dev = getMidiDevice(recInfo);
        }
        else {
            throw new MidiUnavailableException();
        }
        return dev;
    }
    
    @Override
    public String[] getMidiRecieverItems() {
        List<String> ret = new ArrayList<String>();
        MidiDevice.Info[] infosOfRecv = JMPCore.getSoundManager().getMidiToolkit().getMidiDeviceInfo(false, true);
        
        ret.add(ISoundManager.AUTO_RECEIVER_NAME);
        ret.add(ISoundManager.NULL_RECEIVER_NAME);
        ret.add(ISoundManager.RENDER_ONLY_RECEIVER_NAME);
        for (int i = 0; i < infosOfRecv.length; i++) {
            ret.add(infosOfRecv[i].getName());
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public MidiMessage createByteMidiMessage(byte[] data) {
        return new MidiByteMessage(data);
    }

    @Override
    public MidiMessage createSysexBeginMessage(byte[] data, int length) throws InvalidMidiDataException {
        return new SysexMessage(MidiByte.Status.System.SystemCommon.Fst.SYSEX_BEGIN, data, length);
    }

    @Override
    public MidiMessage createSysexEndMessage(byte[] data, int length) throws InvalidMidiDataException {
        return new SysexMessage(MidiByte.Status.System.SystemCommon.Fst.SYSEX_END, data, length);
    }

    @Override
    public MidiMessage createShortMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        return new ShortMessage(command, channel, data1, data2);
    }

    @Override
    public MidiMessage createMetaMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
        return new MetaMessage(type, data, length);
    }

    @Override
    public MidiEvent createMidiEvent(MidiMessage mes, long position) throws InvalidMidiDataException {
        return new MidiEvent(mes, position);
    }

    @Override
    public String getAutoSelectRecieverName() {
        MidiDevice.Info[] infosOfRecv = getMidiDeviceInfo(false, true);

        // デフォルト
        for (String name : RECOMMENDED_RECVNAMES) {
            for (int i = 0; i < infosOfRecv.length; i++) {
                if (infosOfRecv[i].getName().contains(name) == true) {
                    return name;
                }
            }
        }
        return DEFAULT_RECVNAME;
    }
}
