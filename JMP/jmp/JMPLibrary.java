package jmp;

import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.file.ConfigDatabaseWrapper;
import jmp.util.JmpUtil;

public class JMPLibrary {
    
    private static void consoleOutSystemInfo() {
        JMPFlags.Log.cprintln("_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/", true);
        JMPFlags.Log.cprintln("_/############################################_/", true);
        JMPFlags.Log.cprintln("_/####### ## ##### ##     ####################_/", true);
        JMPFlags.Log.cprintln("_/####### ## ##### ## ###  ###########  ######_/", true);
        JMPFlags.Log.cprintln("_/####### ##  ###  ## #### ####   #####  #####_/", true);
        JMPFlags.Log.cprintln("_/####### ##  ###  ## ###  #############  ####_/", true);
        JMPFlags.Log.cprintln("_/####### ##   #   ##     ##############  ####_/", true);
        JMPFlags.Log.cprintln("_/####### ## #   # ## ##################  ####_/", true);
        JMPFlags.Log.cprintln("_/### ### ## ## ## ## ##################  ####_/", true);
        JMPFlags.Log.cprintln("_/###  #  ## ## ## ## #########   #####  #####_/", true);
        JMPFlags.Log.cprintln("_/####   ### ##### ## ################  ######_/", true);
        JMPFlags.Log.cprintln("_/############################################_/", true);
        JMPFlags.Log.cprintln("_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/", true);
        JMPFlags.Log.cprintln("App  : " + JMPCore.APPLICATION_NAME + "(" + JMPCore.APPLICATION_VERSION + ")");
        JMPFlags.Log.cprintln("Date : " + Utility.getCurrentTimeStr(), true);
        JMPFlags.Log.cprintln("Java : " + Platform.getJavaVersion(), true);
        JMPFlags.Log.cprintln("OS   : " + Platform.getOSName(), true);
        JMPFlags.Log.cprintln("SLaF : " + UIManager.getSystemLookAndFeelClassName());
        JMPFlags.Log.cprintln("CLaF : " + UIManager.getCrossPlatformLookAndFeelClassName());
        JMPFlags.Log.cprintln(true);
    }

    /**
     * 終了
     */
    public static void exitApplication() {
        // Windowを非表示化
        JMPCore.getWindowManager().setVisibleAll(false);

        // タスクの終了
        JMPCore.getTaskManager().taskExit();
    }

    /**
     * ライブラリ初期化
     *
     * @param config
     * @return
     */
    public static boolean initCoreAssets(ConfigDatabaseWrapper config) {
        return initCoreAssets(config, null);
    }

    /**
     * ライブラリ初期化
     *
     * @param config
     * @param plugin
     * @return
     */
    public static boolean initCoreAssets(ConfigDatabaseWrapper config, IPlugin plugin) {
        boolean result = false;
        try {
            consoleOutSystemInfo();
            
            result = JMPCore.initFunc(config, plugin);
        }
        catch (Exception e) {
            try {
                String eMsg = function.Utility.getCurrentTimeStr() + function.Platform.getNewLine() + function.Error.getPrintStackTrace(e);
                JmpUtil.writeTextFile("errorlog_init.txt", eMsg);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            result = false;
        }
        return result;
    }

    /**
     * ライブラリ終了
     *
     * @return
     */
    public static boolean exitCoreAssets() {
        boolean result = true;
        try {
            result = JMPCore.endFunc();
        }
        catch (Exception e) {
            try {
                String eMsg = function.Utility.getCurrentTimeStr() + function.Platform.getNewLine() + function.Error.getPrintStackTrace(e);
                JmpUtil.writeTextFile("errorlog_end.txt", eMsg);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            result = false;
        }
        return result;
    }
}
