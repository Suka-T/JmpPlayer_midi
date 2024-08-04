package jmp.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;

import jlib.midi.IMidiEventListener;
import jlib.player.IPlayerListener;
import jlib.plugin.IPlugin;

public class PluginObserver implements IPlugin, IPlayerListener, IMidiEventListener {

    /** プラグイン格納用コレクション */
    private Map<String, PluginWrapper> aPlugins = null;

    private List<PluginWrapper> accessor = null;

    public PluginObserver() {
        aPlugins = new HashMap<String, PluginWrapper>();
        accessor = new ArrayList<PluginWrapper>();
        aPlugins.clear();
        accessor.clear();
    }

    public boolean addPlugin(String name, IPlugin plugin) {
        return addPlugin(name, plugin, false);
    }

    public boolean addPlugin(String name, IPlugin plugin, boolean isOverwrite) {
        if (aPlugins.containsKey(name) == true) {
            if (isOverwrite == true) {
                PluginWrapper rem = aPlugins.remove(name);
                if (rem != null) {
                    rem.close();
                    rem.exit();
                }
            }
            else {
                return false;
            }
        }
        if (plugin != null) {
            aPlugins.put(name, new PluginWrapper(plugin, name));
        }
        else {
            aPlugins.put(name, new PluginWrapper(name));
        }

        accessor.clear();
        for (PluginWrapper pw : aPlugins.values()) {
            accessor.add(pw);
        }
        return true;
    }

    public PluginWrapper getPluginWrapper(String name) {
        if (aPlugins.containsKey(name) == false) {
            return null;
        }
        return aPlugins.get(name);
    }

    public PluginWrapper getPluginWrapper(IPlugin plugin) {
        String name = getPluginName(plugin);
        return getPluginWrapper(name);
    }

    public int getNumberOfPlugin() {
        return aPlugins.size();
    }

    public String getPluginName(IPlugin plugin) {
        String name = "";
        for (String key : aPlugins.keySet()) {
            if (aPlugins.get(key).equalsPlugin(plugin) == true) {
                name = key;
            }
        }
        return name;
    }

    public Set<String> getPluginsNameSet() {
        return aPlugins.keySet();
    }

    public Collection<PluginWrapper> getPlugins() {
        return accessor;
    }

    @Override
    public void initialize() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.initialize();
        }
    }

    @Override
    public void exit() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.exit();
        }
    }

    @Override
    public void open() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.open();
        }
    }

    @Override
    public void close() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.close();
        }
    }

    @Override
    public void update() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.update();
        }
    }

    @Override
    public void startSequencer() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.startSequencer();
        }
    }

    @Override
    public void stopSequencer() {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.stopSequencer();
        }
    }

    @Override
    public void updateTickPosition(long before, long after) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.updateTickPosition(before, after);
        }
    }

    @Override
    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.catchMidiEvent(message, timeStamp, senderType);
        }
    }

    @Override
    public void loadFile(File file) {
        for (PluginWrapper pw : getPlugins()) {
            if (pw.isSupportExtension(file) == true) {
                pw.loadFile(file);
            }
        }
    }

    @Override
    public void notifyUpdateCommonRegister(String key) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.notifyUpdateCommonRegister(key);
        }
    }

    @Override
    public void notifyUpdateConfig(String key) {
        for (PluginWrapper pluginWrap : getPlugins()) {
            pluginWrap.notifyUpdateConfig(key);
        }
    }

    @Override
    public boolean isEnable() {
        /* 未使用 */
        return true;
    }

    @Override
    public boolean isOpen() {
        /* 未使用 */
        return false;
    }

}
