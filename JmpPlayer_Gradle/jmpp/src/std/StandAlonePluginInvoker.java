package std;

import jlib.plugin.IPlugin;
import jmp.JMPLoader;
import jmp.file.ConfigDatabaseWrapper;

/**
 * プラグインのスタンドアロン実行
 *
 * @author akkut
 *
 */
public class StandAlonePluginInvoker {
    public static void exec(String args[], IPlugin plugin) {
        exec(args, null, plugin);
    }

    public static void exec(String args[], ConfigDatabaseWrapper config, IPlugin plugin) {
        boolean res = JMPLoader.invoke(args, config, plugin);
        // ただしfalse起動失敗は常に終了する
        if (res == false) {
            System.exit(1);
        }
    }

    public static void execSimple(String args[], IPlugin plugin) {
        JMPLoader.UsePluginDirectory = false;
        JMPLoader.UseConfigFile = false;
        JMPLoader.UseHistoryFile = false;
        JMPLoader.UseSkinFile = false;

        exec(args, null, plugin);
    }
}
