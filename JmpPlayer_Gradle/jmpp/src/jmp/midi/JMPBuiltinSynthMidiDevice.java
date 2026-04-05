package jmp.midi;

import java.awt.Color;

import javax.sound.midi.MidiUnavailableException;

import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.gui.BuiltinSynthSetupDialog;
import jmsynth.midi.JMSynthMidiDevice;
import jmsynth.midi.MidiInterface;

public class JMPBuiltinSynthMidiDevice extends JMSynthMidiDevice {

    public static MidiInterface SCurrentFaceAccesser;

    public JMPBuiltinSynthMidiDevice() {
        super();
    }

    @Override
    public void open() throws MidiUnavailableException {
        super.open();

        SystemManager system = JMPCore.getSystemManager();
        SCurrentFaceAccesser = getJMsynthInterface();

        // Window登録
        BuiltinSynthSetupDialog wvf = new BuiltinSynthSetupDialog(SCurrentFaceAccesser);
        Color[] ct = new Color[16];
        for (int i = 0; i < 16; i++) {
            String key = String.format(SystemManager.COMMON_REGKEY_CH_COLOR_FORMAT, i + 1);
            ct[i] = system.getUtilityToolkit().convertCodeToHtmlColor(system.getCommonRegisterValue(key));
        }
        wvf.setWaveColorTable(ct);
        JMPCore.getWindowManager().registerBuiltinSynthFrame(wvf);
    }

    @Override
    public void close() {
        super.close();

        SCurrentFaceAccesser = null;
    }

}
