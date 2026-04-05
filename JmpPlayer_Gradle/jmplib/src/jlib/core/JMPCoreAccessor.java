package jlib.core;

/**
 * 各マネージャークラスへアクセスする
 *
 * @author abs
 *
 */
public class JMPCoreAccessor {

    /**
     * SystemManager取得
     *
     * @return
     */
    public static ISystemManager getSystemManager() {
        return JMPCoreManagerRegister.systemManager;
    }

    /**
     * SoundManager取得
     *
     * @return
     */
    public static ISoundManager getSoundManager() {
        return JMPCoreManagerRegister.soundManager;
    }

    /**
     * DataManager取得
     *
     * @return
     */
    public static IDataManager getDataManager() {
        return JMPCoreManagerRegister.dataManager;
    }

    /**
     * WindowManager取得
     *
     * @return
     */
    public static IWindowManager getWindowManager() {
        return JMPCoreManagerRegister.windowManager;
    }

    /**
     * FileManager取得
     *
     * @return
     */
    public static IFileManager getFileManager() {
        return JMPCoreManagerRegister.fileManager;
    }
}
