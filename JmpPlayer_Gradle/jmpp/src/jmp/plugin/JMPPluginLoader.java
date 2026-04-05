package jmp.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import function.Utility;
import jlib.plugin.IPlugin;

public class JMPPluginLoader {
    private static IPlugin load(File jarFile, String className) {
        IPlugin plugin = null;
        try {
            URL[] urls = { jarFile.toURI().toURL() };
            ClassLoader loader = URLClassLoader.newInstance(urls);

            // クラスをロード
            Class<?> c = loader.loadClass(className);
            if (IPlugin.class.isAssignableFrom(c) == true) {
                try {
                    plugin = (IPlugin) c.newInstance();
                }
                catch (Exception ex) {
                }
            }
        }
        catch (Exception e) {
            plugin = null;
        }
        return plugin;
    }

    public static IPlugin load(File jarFile) {
        // Jar名をクラス名とする
        String className = Utility.getFileNameNotExtension(jarFile.getPath());
        return load(jarFile, className);
    }

}
