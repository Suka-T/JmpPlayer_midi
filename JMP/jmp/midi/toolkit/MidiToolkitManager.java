package jmp.midi.toolkit;

import java.util.ArrayList;
import java.util.List;

import jlib.midi.IMidiToolkit;

public class MidiToolkitManager {

    public static final String DEFAULT_MIDI_TOOLKIT_NAME = DefaultMidiToolkit.class.getSimpleName();

    private static DefaultMidiToolkit DefaultMidiToolkit = null;
    private List<IMidiToolkit> lst = null;

    private static MidiToolkitManager singleton = null;

    public static MidiToolkitManager getInstance() {
        if (singleton == null) {
            DefaultMidiToolkit = new DefaultMidiToolkit();

            singleton = new MidiToolkitManager();
            singleton.addMidiToolkit(DefaultMidiToolkit);
        }
        return singleton;
    }

    private MidiToolkitManager() {
        lst = new ArrayList<IMidiToolkit>();
    }

    public void addMidiToolkit(IMidiToolkit kit) {
        lst.add(kit);
    }

    public IMidiToolkit getMidiToolkit(String className) {
        for (IMidiToolkit kit : lst) {
            if (kit.getClass().getSimpleName().equals(className)) {
                return kit;
            }
        }
        return DefaultMidiToolkit;
    }

}
