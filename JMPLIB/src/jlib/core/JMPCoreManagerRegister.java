package jlib.core;

/**
 * 上位アプリケーションからライブラリにマネージャークラスを登録する
 *
 * @author akkut
 *
 */
public class JMPCoreManagerRegister {

    static ISystemManager systemManager = null;
    static ISoundManager soundManager = null;
    static IDataManager dataManager = null;
    static IWindowManager windowManager = null;
    static IFileManager fileManager = null;

    /**
     * マネージャー登録
     *
     * @param manager
     */
    public static void register(IManager manager) {
        if (manager instanceof ISystemManager) {
            systemManager = (ISystemManager) (systemManager == null ? manager : systemManager);
        }
        else if (manager instanceof ISoundManager) {
            soundManager = (ISoundManager) (soundManager == null ? manager : soundManager);
        }
        else if (manager instanceof IDataManager) {
            dataManager = (IDataManager) (dataManager == null ? manager : dataManager);
        }
        else if (manager instanceof IWindowManager) {
            windowManager = (IWindowManager) (windowManager == null ? manager : windowManager);
        }
        else if (manager instanceof IFileManager) {
            fileManager = (IFileManager) (fileManager == null ? manager : fileManager);
        }
    }
}
