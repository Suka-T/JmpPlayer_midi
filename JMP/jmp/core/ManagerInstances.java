package jmp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jlib.core.IDataManager;
import jlib.core.JMPCoreManagerRegister;
import jmp.JMPFlags;
import jmp.file.ConfigDatabase;
import jmp.file.IJmpConfigDatabase;
import jmp.file.JmpConfigValueType;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public class ManagerInstances {

    static SystemManager SSystemManager = new SystemManager();
    static DataManager SDataManager = new DataManager();
    static SoundManager SSoundManager = new SoundManager();
    static PluginManager SPluginManager = new PluginManager();
    static WindowManager SWindowManager = new WindowManager();
    static LanguageManager SLanguageManager = new LanguageManager();
    static TaskManager STaskManager = new TaskManager();
    static ResourceManager SResourceManager = new ResourceManager();
    static FileManager SFileManager = new FileManager();

    /** 初期化・終了実行順位 */
    private static AbstractManager[] aManager = { //
            SResourceManager, //
            SSystemManager, //
            SLanguageManager, //
            STaskManager, //
            SDataManager, //
            SFileManager, //
            SWindowManager, //
            SSoundManager, //
            SPluginManager,//
    };//

    private static List<AbstractManager> asc = null;
    private static List<AbstractManager> desc = null;

    // 設定データベース
    static ConfigDatabase SDatabase = null;

    /** 初期化キー */
    public static final String CFG_KEY_INITIALIZE = "INITIALIZE";

    public static enum TypeOfKey {
        STRING, LONG, INT, SHORT, FLOAT, DOUBLE, BOOL,
    }

    // 設定キーリスト
    // ↓KEY追加後、必ず追加すること!!
    public static final HashMap<String, JmpConfigValueType> CFG_INIT_TABLE = new HashMap<String, JmpConfigValueType>() {
        {
            // キー文字列, 初期値
            put(IDataManager.CFG_KEY_PLAYLIST, new JmpConfigValueType(JmpUtil.getDesktopPathOrCurrent()));
            put(IDataManager.CFG_KEY_MIDIOUT, new JmpConfigValueType(""));
            put(IDataManager.CFG_KEY_MIDIIN, new JmpConfigValueType(""));
            put(IDataManager.CFG_KEY_AUTOPLAY, new JmpConfigValueType(IJmpConfigDatabase.IJ_FALSE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_LOOPPLAY, new JmpConfigValueType(IJmpConfigDatabase.IJ_FALSE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_LANGUAGE, new JmpConfigValueType("en"));
            put(IDataManager.CFG_KEY_LOADED_FILE, new JmpConfigValueType(""));
            put(IDataManager.CFG_KEY_LYRIC_VIEW, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_FFMPEG_PATH, new JmpConfigValueType("ffmpeg.exe"));
            put(IDataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_FFMPEG_INSTALLED, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_SEND_MIDI_SYSTEMSETUP, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_YOUTUBEDL_PATH, new JmpConfigValueType("youtube-dl.exe"));
            put(IDataManager.CFG_KEY_YOUTUBEDL_COMMAND, new JmpConfigValueType("youtube-dl"));
            put(IDataManager.CFG_KEY_YOUTUBEDL_INSTALLED, new JmpConfigValueType(IJmpConfigDatabase.IJ_TRUE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_RANDOMPLAY, new JmpConfigValueType(IJmpConfigDatabase.IJ_FALSE, TypeOfKey.BOOL));
            put(IDataManager.CFG_KEY_YOUTUBEDL_FILENAME_MODE, new JmpConfigValueType("ID"));
            put(IDataManager.CFG_KEY_CHECK_PLUGIN_VERSION, new JmpConfigValueType(IJmpConfigDatabase.IJ_FALSE, TypeOfKey.BOOL));
        }
    };

    static void setConfigParam(String key, String value) {
        SDatabase.setConfigParam(key, value);
    }

    static String getConfigParam(String key) {
        return SDatabase.getConfigParam(key);
    }

    static void sendNotify(NotifyID id, String key) {
        STaskManager.sendNotifyMessage(id, key);
    }

    static void register(AbstractManager m) {
        // ライブラリに登録
        JMPCoreManagerRegister.register(m);
    }

    static boolean isFinishedAllInitialize() {
        for (AbstractManager am : getManagersOfAsc()) {
            if (am.isFinishedInitialize() == false) {
                return false;
            }
        }
        return true;
    }

    static boolean callInitFunc() {
        boolean result = true;

        remakeAsc();
        remakeDesc();
        
        JMPFlags.Log.cprintln("");
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE initializing");
        JMPFlags.Log.cprintln("##");
        for (AbstractManager am : getManagersOfAsc()) {
            JMPFlags.Log.cprint(">> " + am.getName() + " init... ");
            if (result == true) {
                result = am.initFunc();
            }
            JMPFlags.Log.cprintln(result == true ? "Done!" : "##Error##");
        }
        JMPFlags.Log.cprintln("## finished");
        JMPFlags.Log.cprintln("");
        return result;
    }

    static boolean callEndFunc() {
        boolean result = true;
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE exiting");
        JMPFlags.Log.cprintln("##");
        for (AbstractManager am : getManagersOfDesc()) {
            JMPFlags.Log.cprint(">> " + am.getName() + " exit... ");
            if (result == true) {
                result = am.endFunc();
            }
            JMPFlags.Log.cprintln(result == true ? "Done!" : "##Error##");
        }
        JMPFlags.Log.cprintln("## finished");
        return result;
    }

    static void remakeAsc() {
        if (asc == null) {
            asc = new ArrayList<AbstractManager>();
        }
        copyArray2ManagerList(asc, true);
    }

    static void remakeDesc() {
        if (desc == null) {
            desc = new ArrayList<AbstractManager>();
        }
        copyArray2ManagerList(desc, false);
    }

    private static void copyArray2ManagerList(List<AbstractManager> lst, boolean isAsc) {

        int startP, counter;
        if (isAsc == true) {
            startP = 0;
            counter = 1;
        }
        else {
            startP = aManager.length - 1;
            counter = -1;
        }

        // リストを空にする
        lst.clear();

        // マネージャーリストをコピー
        int i = startP;
        while (true) {
            if (i < 0 || i >= aManager.length) {
                break;
            }
            lst.add(aManager[i]);
            i += counter;
        }
    }

    public static List<AbstractManager> getManagersOfAsc() {
        if (asc == null) {
            remakeAsc();
        }
        return asc;
    }

    public static List<AbstractManager> getManagersOfDesc() {
        if (desc == null) {
            remakeDesc();
        }
        return desc;
    }
}
