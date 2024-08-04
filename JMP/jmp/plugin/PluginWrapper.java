package jmp.plugin;

import java.io.File;

import javax.sound.midi.MidiMessage;

import function.Utility;
import jlib.midi.IMidiEventListener;
import jlib.player.IPlayerListener;
import jlib.plugin.IPlugin;
import jlib.plugin.ISupportExtensionConstraints;
import jlib.plugin.JMPlugin;

public class PluginWrapper implements IPlugin, IPlayerListener, IMidiEventListener {
    private final static JMPlugin DUMMY_PLG = new JMPlugin() {

        @Override
        public void open() {
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void initialize() {
        }

        @Override
        public void exit() {
        }

        @Override
        public void close() {
        }
    };

    public static enum PluginState {
        CONNECTED, DISCONNECTED, INVALID;

        public static PluginState strToState(String str) {
            for (PluginState p : values()) {
                if (str.equalsIgnoreCase(p.toString()) == true) {
                    return p;
                }
            }
            return CONNECTED;
        }

        @Override
        public String toString() {
            switch (this) {
                case CONNECTED:
                    return "CONNECTED";
                case DISCONNECTED:
                    return "DISCONNECTED";
                case INVALID:
                    return "INVALID";
                default:
                    return "";

            }
        }
    }

    private IPlugin plugin = null;
    private IPlayerListener playerListener = null;
    private IMidiEventListener midiEventListener = null;
    private ISupportExtensionConstraints supportExtensionConstraints = null;
    private PluginState state = PluginState.CONNECTED;
    private String name = "";

    public PluginWrapper(String name) {
        toInvalidPlugin();
        this.state = PluginState.INVALID;
        this.name = name;
    }

    public PluginWrapper(IPlugin plg, String name) {
        setInterface(plg);
        this.state = PluginState.CONNECTED;
        this.name = name;
    }

    private IPlayerListener makeDummyPlayerListener() {
        return new IPlayerListener() {

            @Override
            public void updateTickPosition(long before, long after) {
            }

            @Override
            public void stopSequencer() {
            }

            @Override
            public void startSequencer() {
            }
        };
    }

    private IMidiEventListener makeDummyMidiEventListener() {
        return new IMidiEventListener() {

            @Override
            public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
            }
        };
    }

    private ISupportExtensionConstraints makeDummySupportExtensionConstraints() {
        return new ISupportExtensionConstraints() {
            @Override
            public String allowedExtensions() {
                return "";
            }
        };
    }

    public void setInterface() {
        if (this.plugin instanceof IPlayerListener) {
            this.playerListener = (IPlayerListener) this.plugin;
        }
        else {
            this.playerListener = makeDummyPlayerListener();
        }

        if (this.plugin instanceof IMidiEventListener) {
            this.midiEventListener = (IMidiEventListener) this.plugin;
        }
        else {
            this.midiEventListener = makeDummyMidiEventListener();
        }

        if (this.plugin instanceof ISupportExtensionConstraints) {
            this.supportExtensionConstraints = (ISupportExtensionConstraints) this.plugin;
        }
        else {
            this.supportExtensionConstraints = makeDummySupportExtensionConstraints();
        }
    }

    public void setInterface(IPlugin plg) {
        this.plugin = plg;
        setInterface();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }

    public boolean equalsPlugin(IPlugin plg) {
        if (plg == this) {
            // 自分自身をさすか？
            return true;
        }
        else if (plugin == plg) {
            // Pluginと比較
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPlugin) {
            return equalsPlugin((IPlugin) obj);
        }
        return super.equals(obj);
    }

    public void toInvalidPlugin() {
        this.plugin = DUMMY_PLG;
        this.playerListener = makeDummyPlayerListener();
        this.midiEventListener = makeDummyMidiEventListener();
        this.supportExtensionConstraints = makeDummySupportExtensionConstraints();
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        if (getState() != PluginState.CONNECTED) {
            return;
        }

        midiEventListener.catchMidiEvent(message, timeStamp, senderType);
    }

    @Override
    public void startSequencer() {
        playerListener.startSequencer();
    }

    @Override
    public void stopSequencer() {
        playerListener.stopSequencer();
    }

    @Override
    public void updateTickPosition(long before, long after) {
        playerListener.updateTickPosition(before, after);
    }

    @Override
    public void initialize() {
        plugin.initialize();
    }

    @Override
    public void exit() {
        plugin.exit();
    }

    @Override
    public void open() {
        plugin.open();
    }

    @Override
    public void close() {
        plugin.close();
    }

    @Override
    public boolean isEnable() {
        if (getState() == PluginState.INVALID) {
            // 無効なプラグイン
            return false;
        }
        return plugin.isEnable();
    }

    @Override
    public boolean isOpen() {
        return plugin.isOpen();
    }

    @Override
    public void loadFile(File file) {
        plugin.loadFile(file);
    }

    @Override
    public void notifyUpdateCommonRegister(String key) {
        plugin.notifyUpdateCommonRegister(key);
    }

    @Override
    public void notifyUpdateConfig(String key) {
        plugin.notifyUpdateConfig(key);
    }

    @Override
    public void update() {
        plugin.update();
    }

    public final ISupportExtensionConstraints getSupportExtensionConstraints() {
        return this.supportExtensionConstraints;
    }

    public boolean isSupportExtension(File file) {
        if (this.supportExtensionConstraints == null) {
            return true;
        }
        if (this.supportExtensionConstraints.allowedExtensions() == null) {
            return true;
        }
        if (this.supportExtensionConstraints.allowedExtensions().isEmpty() == true) {
            return true;
        }

        boolean ret = false;
        String[] allowsEx = this.supportExtensionConstraints.allowedExtensionsArray();
        for (String ae : allowsEx) {
            if (Utility.checkExtension(file, ae) == true) {
                ret = true;
                break;
            }
        }
        return ret;
    }
}
