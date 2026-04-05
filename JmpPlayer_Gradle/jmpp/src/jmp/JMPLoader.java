package jmp;

import java.io.File;

import function.Platform;
import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.SystemManager;
import jmp.file.CommonRegisterINI;
import jmp.file.ConfigDatabaseWrapper;
import jmp.util.JmpUtil;
import lib.MakeJmpLib;

/**
 * JMPリソースのロードを行うクラス
 *
 * @author abs
 *
 */
public class JMPLoader {

    // 起動設定
    public static boolean MainThreadRunnable = false;

    /** プラグインフォルダを使用するか */
    public static boolean UsePluginDirectory = true;
    public static boolean UseConfigFile = true;
    public static boolean UseHistoryFile = true;
    public static boolean UseSkinFile = true;

    /* コマンド文字列 */
    public static final String CMD_MANUAL = "-man";
    public static final String CMD_STDPLG = "-stdplg";
    public static final String CMD_NONPLG = "-nonplg";
    public static final String CMD_PLGLST = "-plglst";
    public static final String CMD_UNSYNC = "-unsync";
    public static final String CMD_SYSOUT = "-c";
    public static final String CMD_MKJMP = "-mkjmp";
    public static final String CMD_UN_LOAD_SKIN = "-nskin";
    public static final String CMD_COMMON_REG = "-creg";
    public static final String CMD_TERM = "-term";

    /* フォーマット */
    private static final String PLGLST_FORMAT = "<%d> %s";
    private static final String MANUAL_FORMAT_CMD = " %s";
    private static final String MANUAL_FORMAT_DSC = "     %s";

    private static void printManual() {
        printManualLine(//
                CMD_STDPLG + " [plugin name]", //
                "Plugin standalone boot mode."//
        );//
        printManualLine(//
                CMD_NONPLG, // 11
                "Start without loading the plugin."//
        );//
        printManualLine(//
                CMD_PLGLST, //
                "Display a list of plugins."//
        );//
        printManualLine(//
                CMD_UNSYNC, //
                "Asynchronization option."//
        );//
        printManualLine(//
                CMD_SYSOUT, //
                "Enable console output."//
        );//
        printManualLine(//
                CMD_UN_LOAD_SKIN, //
                "Do not load skins."//
        );//
        printManualLine(//
                CMD_MKJMP, //
                "Call the plug-in package creation library.", //
                "※ Write at the beginning of the command."//
        );//
        printManualLine(//
                CMD_COMMON_REG + " [key] [value]", //
                "syscommon edit." //
        );//
    }

    private static void printManualLine(String cmd, String... description) {
        System.out.println(String.format(MANUAL_FORMAT_CMD, cmd));
        for (String d : description) {
            System.out.println(String.format(MANUAL_FORMAT_DSC, d));
        }
        System.out.println();
    }

    private static void printPlglst() {
        String path = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_JMS_DIR);
        File jmsDir = new File(path);
        int cnt = 0;
        if (jmsDir.exists() == true && jmsDir.isDirectory() == true) {
            for (File f : jmsDir.listFiles()) {
                if (JmpUtil.checkExtension(f, PluginManager.SETUP_FILE_EX) == true) {
                    String dsc = String.format(PLGLST_FORMAT, (cnt + 1), JmpUtil.getFileNameNotExtension(f));
                    System.out.println(dsc);
                    cnt++;
                }
            }
        }
        if (cnt <= 0) {
            System.out.println("<!> プラグインが存在しません。");
        }
    }

    private static IPlugin getStdPlugin(String jmsName) {
        String path = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_JMS_DIR);
        if (jmsName.endsWith("." + PluginManager.SETUP_FILE_EX) == false) {
            jmsName += "." + PluginManager.SETUP_FILE_EX;
        }
        path += Platform.getSeparator() + jmsName;

        IPlugin stdPlugin = null;
        File jms = new File(path);
        if (jms.exists() == true) {
            if (JmpUtil.checkExtension(jms.getPath(), PluginManager.SETUP_FILE_EX) == true) {
                stdPlugin = JMPCore.getPluginManager().readingPlugin(jms);
            }
        }
        if (stdPlugin == null) {
            System.out.println("<!> 指定したプラグインが存在しません。");
        }
        return stdPlugin;
    }

    // コンソール出力だけで完結する方式の起動方法に必要なセットアップ処理を行う
    private static void setupConsoleApplication() {
        // システムパス設定
        JMPCore.getSystemManager().makeSystemPath();
    }

    /**
     * コマンド引数の解析
     *
     * @param args
     * @return
     */
    private static InvokeArgs parseArgs(String[] args) {
        InvokeArgs res = new InvokeArgs();
        String jmsName = "";
        res.stdPlugin = null;
        res.doReturn = false;
        res.loadFile = null;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(CMD_MKJMP) == true) {
                args[0] = MakeJmpLib.CMD_CONSOLE;
                MakeJmpLib.call(args);
                res.doReturn = true;
                return res;
            }

            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase(CMD_MANUAL) == true) {
                    setupConsoleApplication();
                    printManual();
                    res.doReturn = true;
                    return res;
                }
                else if (args[i].equalsIgnoreCase(CMD_STDPLG) == true) {
                    i++;
                    if (i >= args.length) {
                        break;
                    }

                    jmsName = args[i];
                }
                else if (args[i].equalsIgnoreCase(CMD_NONPLG) == true) {
                    JMPFlags.NonPluginLoadFlag = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_PLGLST) == true) {
                    setupConsoleApplication();
                    printPlglst();
                    res.doReturn = true;
                    return res;
                }
                else if (args[i].equalsIgnoreCase(CMD_UNSYNC) == true) {
                    JMPFlags.UseUnsynchronizedMidiPacket = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_SYSOUT) == true) {
                    JMPFlags.CoreConsoleOut = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_UN_LOAD_SKIN) == true) {
                    UseSkinFile = false;
                }
                else if (args[i].equalsIgnoreCase(CMD_TERM) == true) {
                    JMPFlags.CoreConsoleOut = true;
                    JMPFlags.InvokeToConsole = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_COMMON_REG) == true) {
                    CommonRegisterINI ini = new CommonRegisterINI("", "", false);
                    i++;
                    if (i >= args.length) {
                        break;
                    }
                    ini.key = args[i];
                    i++;
                    if (i >= args.length) {
                        break;
                    }
                    ini.value = args[i];
                    res.creg.add(ini);
                }
                /* ※コマンドの判定を優先するため、このelseifは最後に挿入すること */
                else if (JmpUtil.isExsistFile(args[i]) == true) {
                    File f = new File(args[i]);
                    if (f.isFile() == true) {

                        res.loadFile = new File(args[i]);

                        // ロード設定
                        JMPFlags.StartupAutoConectSynth = true;
                    }
                }
            }
        }

        if (JMPFlags.NonPluginLoadFlag == true) {
            // ※nonplgとstdplgコマンドは併用不可
            jmsName = "";
        }

        // cregのコマンドを受け渡し
        JMPCore.cregStack = res.creg;

        /* スタンドアロンプラグインの特殊処理 */
        if (jmsName.isEmpty() == false) {
            // システムパス設定
            JMPCore.getSystemManager().makeSystemPath();

            // プラグインを探索
            res.stdPlugin = getStdPlugin(jmsName);
        }
        return res;
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @param config
     * @return
     */
    public static boolean invoke(String[] args, ConfigDatabaseWrapper config) {
        InvokeArgs rArgs = parseArgs(args);
        if (rArgs.doReturn == true) {
            return true;
        }
        return invokeImpl(config, rArgs.stdPlugin, rArgs.loadFile);
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @return
     */
    public static boolean invoke(String[] args) {
        InvokeArgs rArgs = parseArgs(args);
        if (rArgs.doReturn == true) {
            return true;
        }
        return invokeImpl(null, rArgs.stdPlugin, rArgs.loadFile);
    }

    /**
     * スタンドアロンプラグイン起動設定
     *
     * @param args
     * @param standAlonePlugin
     *            スタンドアロンプラグイン
     * @return
     */
    public static boolean invoke(ConfigDatabaseWrapper config, IPlugin standAlonePlugin) {
        return invokeImpl(config, standAlonePlugin, null);
    }

    /**
     * スタンドアロンプラグイン起動設定
     *
     * @param args
     * @param config
     * @param standAlonePlugin
     * @return
     */
    public static boolean invoke(String[] args, ConfigDatabaseWrapper config, IPlugin standAlonePlugin) {
        InvokeArgs rArgs = parseArgs(args);
        if (rArgs.doReturn == true) {
            return true;
        }
        return invokeImpl(config, standAlonePlugin, null);
    }

    /**
     * 事前ロードプラグイン起動設定（プラグイン開発用）
     *
     * @param args
     * @param config
     * @return
     */
    public static boolean invokePreLoadPlugin(String[] args, ConfigDatabaseWrapper config, String pluginName, IPlugin PreLoadPlugin) {
        JMPCore.getPluginManager().setPreLoadPlugin(pluginName, PreLoadPlugin);
        return invokeImpl(config, null, null);
    }

    /**
     * 起動処理本体
     * 
     * @param config
     * @param standAlonePlugin
     * @return
     */
    private static boolean invokeImpl(ConfigDatabaseWrapper config, IPlugin standAlonePlugin, File loadFile) {

        boolean ret = true;
        InvokeTask invokeTask = new InvokeTask(config, standAlonePlugin, loadFile);

        // invokeメソッドからの起動はLibraryモードではない
        JMPFlags.LibraryMode = false;

        if (MainThreadRunnable == true) {
            // このスレッド内で処理する
            invokeTask.run();
            ret = invokeTask.getResult();
            System.exit(ret ? 0 : 1);
        }
        else {
            // 別スレッドを起動する
            try {
                InvokeThread invokeThread = new InvokeThread(invokeTask);
                invokeThread.start();
            }
            catch (Exception e) {
                ret = false;
            }
        }
        return ret;
    }

}
