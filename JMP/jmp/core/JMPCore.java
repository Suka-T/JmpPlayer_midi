package jmp.core;

import java.util.List;

import jlib.JMPLIB;
import jlib.core.IDataManager;
import jlib.plugin.IPlugin;
import jmp.ErrorDef;
import jmp.JMPFlags;
import jmp.core.asset.AbstractCoreAsset;
import jmp.file.CommonRegisterINI;
import jmp.file.ConfigDatabaseWrapper;
import jmp.file.FileResult;
import jmp.plugin.PluginWrapper;
import jmp.task.TaskOfNotify.NotifyID;

public class JMPCore {

    /** アプリケーション名 */
    public static final String APPLICATION_NAME = "JMPPlayer";

    /** アプリケーションバージョン */
    public static final String APPLICATION_VERSION = "1.00.001"; /* メジャーリリース番号.機能追加番号.修正番号 */

    /** ライブラリバージョン */
    public static final String LIBRALY_VERSION = JMPLIB.BUILD_VERSION;

    public static int ErrorId = ErrorDef.ERROR_ID_NOERROR;

    /** スタンドアロンモードのプラグイン */
    // private static IPlugin StandAlonePlugin = null;
    private static PluginWrapper StandAlonePluginWrapper = null;

    public static List<CommonRegisterINI> cregStack = null;

    public static boolean initFunc(ConfigDatabaseWrapper config, IPlugin plugin) {
        // システムパス設定
        getSystemManager().makeSystemPath();

        // フォントリソース作成
        getLanguageManager().makeFontRsrc();

        // 設定値を先行して登録する
        if (config == null) {
            getDataManager().setConfigDatabase(null);
        }
        else {
            getDataManager().setConfigDatabase(config.getConfigDatabase());
        }

        // 管理クラス初期化処理
        boolean result = ManagerInstances.callInitFunc();
        if (result == true) {

            // 全ての画面を最新版にする
            getWindowManager().updateBackColor();
            getWindowManager().updateDebugMenu();
            getWindowManager().updateLanguage();

            /* ライセンス確認 */
            if (JMPFlags.LibraryMode == false) {
                /* ライセンス確認 */
                if (JMPFlags.ActivateFlag == false) {
                    getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
                    // Notifyタスクがまだ有効ではないため、ここで言語更新する必要がある
                    getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE).updateLanguage();
                    getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE).showWindow();
                }

                // ダイアログが閉じられてから再度ActivateFlagを確認する
                if (JMPFlags.ActivateFlag == false) {
                    // アクティベートされなかった場合は終了する
                    result = false;
                }
            }
        }
        else {
            // 立ち上げ失敗
            JMPCore.ErrorId = ErrorDef.ERROR_ID_SYSTEM_FAIL_INIT_FUNC;
        }

        /* プレイヤーロード */
        if (result == true) {
            result = getSoundManager().openPlayer();
            if (result == false) {
                JMPCore.ErrorId = ErrorDef.ERROR_ID_UNKNOWN_FAIL_LOAD_PLAYER;
            }
        }

        /* 起動準備 */
        if (result == true) {

            // 設定ファイル情報表示
            JMPFlags.Log.cprintln("## file info ##");
            JMPFlags.Log.cprintln("AppName : " + JMPCore.getDataManager().getReadInfoForAppName());
            JMPFlags.Log.cprintln("Version : " + JMPCore.getDataManager().getReadInfoForVersion());
            JMPFlags.Log.cprintln();

            if (getDataManager().isShowStartupDeviceSetup() == false) {
                // 自動接続フラグを立てる
                JMPFlags.StartupAutoConectSynth = true;
            }

            // サウンドデバイス設定の初期処理
            if (getSoundManager().startupDeviceSetup() == true) {
                /* サウンドデバイスが用意出来たら次の設定へ */

                // プラグイン準備
                getPluginManager().startupPluginInstance(plugin);

                // Window初期処理
                getWindowManager().startupWindow();

                // 起動構成
                if (isEnableStandAlonePlugin() == false) {
                    // JMPPlayer起動
                    if (JMPFlags.LibraryMode == false) {
                        getWindowManager().getMainWindow().showWindow();
                    }
                }
                else {
                    // スタンドアロンプラグイン起動
                    getStandAlonePluginWrapper().open();
                }

                // タスク開始
                TaskManager taskManager = getTaskManager();
                taskManager.taskStart();
            }
            else {
                result = false;
            }
        }
        return result;
    }

    public static boolean endFunc() {
        // 念のためWindowを閉じる
        getWindowManager().setVisibleAll(false);

        if (getWindowManager().isValidBuiltinSynthFrame() == true) {
            getWindowManager().closeBuiltinSynthFrame();
        }

        // 動画ビューワを閉じる
        if (getSoundManager().isVisibleMediaView() == true) {
            getSoundManager().setVisibleMediaView(false);
        }

        // 終了前に全てのプラグインを閉じる
        getPluginManager().closeAllPlugins();

        // この時点でタスクが生きてたら終了する
        TaskManager taskManager = getTaskManager();
        if (taskManager.isRunnable() == true) {
            taskManager.taskExit();
            try {
                taskManager.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        boolean result = ManagerInstances.callEndFunc();
        if (result == false && isFinishedInitialize() == true) {
            JMPCore.ErrorId = ErrorDef.ERROR_ID_SYSTEM_FAIL_END_FUNC;
        }
        return result;
    }

    static boolean operate(AbstractCoreAsset asset, boolean dirIsAsc) {
        boolean res = true;
        List<AbstractManager> managers;

        if (dirIsAsc == true) {
            managers = ManagerInstances.getManagersOfAsc();
        }
        else {
            managers = ManagerInstances.getManagersOfDesc();
        }

        for (AbstractManager am : managers) {
            if (false == am.operate(asset)) {
                res = false;
                break;
            }
        }
        return res;
    }

    public static boolean isFinishedInitialize() {
        return ManagerInstances.isFinishedAllInitialize();
    }

    public static boolean isEnableStandAlonePlugin() {
        if (StandAlonePluginWrapper != null) {
            return true;
        }
        return false;
    }

    public static void setStandAlonePluginWrapper(PluginWrapper plg) {
        StandAlonePluginWrapper = plg;
    }

    public static PluginWrapper getStandAlonePluginWrapper() {
        return StandAlonePluginWrapper;
    }

    /* Notify処理 */
    public static void parseNotifyPacket(NotifyID id, Object... data) {
        if (data == null) {
            return;
        }
        if (data.length <= 0) {
            return;
        }

        switch (id) {
            case UPDATE_CONFIG: {
                /* Config変更通知 */
                String key = data[0].toString();
                for (AbstractManager am : ManagerInstances.getManagersOfAsc()) {
                    if (am.isFinishedInitialize() == true) {
                        am.notifyUpdateConfig(key);
                    }
                }
                break;
            }
            case UPDATE_SYSCOMMON: {
                /* Syscommon変更通知 */
                String key = data[0].toString();
                for (AbstractManager am : ManagerInstances.getManagersOfAsc()) {
                    if (am.isFinishedInitialize() == true) {
                        am.notifyUpdateCommonRegister(key);
                    }
                }
                break;
            }
            case FILE_RESULT_BEGIN: {
                /* ファイル処理 事前判定結果通知 */
                if (data[0] instanceof FileResult) {
                    FileResult result = (FileResult) data[0];
                    getFileManager().callFileResultBegin(result);
                }
                break;
            }
            case FILE_RESULT_END: {
                /* ファイル処理 事後判定結果通知 */
                if (data[0] instanceof FileResult) {
                    FileResult result = (FileResult) data[0];
                    getFileManager().callFileResultEnd(result);
                }
                break;
            }
            default:
                break;
        }
    }

    public static void initializeAllSetting() {
        // 全てのWindowを閉じる
        getWindowManager().setVisibleAll(false);
        getPluginManager().closeAllPlugins();

        getDataManager().initializeConfigDatabase();
        getDataManager().clearHistory();
        getSoundManager().clearPlayList();
        getWindowManager().initializeLayout();

        // 言語更新
        getWindowManager().updateLanguage();

        // 設定ファイルのFFmpegパスを同期
        getSystemManager().syncDatabase(IDataManager.CFG_KEY_FFMPEG_PATH);
        getSystemManager().syncDatabase(IDataManager.CFG_KEY_FFMPEG_INSTALLED);
    }

    public static SystemManager getSystemManager() {
        return ManagerInstances.SSystemManager;
    }

    public static DataManager getDataManager() {
        return ManagerInstances.SDataManager;
    }

    public static SoundManager getSoundManager() {
        return ManagerInstances.SSoundManager;
    }

    public static PluginManager getPluginManager() {
        return ManagerInstances.SPluginManager;
    }

    public static WindowManager getWindowManager() {
        return ManagerInstances.SWindowManager;
    }

    public static LanguageManager getLanguageManager() {
        return ManagerInstances.SLanguageManager;
    }

    public static TaskManager getTaskManager() {
        return ManagerInstances.STaskManager;
    }

    public static ResourceManager getResourceManager() {
        return ManagerInstances.SResourceManager;
    }

    public static FileManager getFileManager() {
        return ManagerInstances.SFileManager;
    }
}
