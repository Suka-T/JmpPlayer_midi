package jmp.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import jlib.core.IDataManager;
import jmp.JMPLoader;
import jmp.core.ManagerInstances.TypeOfKey;
import jmp.file.ConfigDatabase;
import jmp.file.ConfigDatabaseWrapper;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public class DataManager extends AbstractManager implements IDataManager {
    public static final String CONFIG_FILE = "config.txt";
    public static final String HISTORY_FILE = "history.txt";

    public static final int MAX_HISTORY_SIZE = 50;

    // 履歴データ
    private List<String> historyData = null;

    // 変換したファイルをログ
    private List<File> convertedFiles = null;

    /**
     * コンストラクタ
     */
    DataManager() {
        super("data");
    }

    protected boolean initFunc() {
        super.initFunc();
        historyData = new LinkedList<String>();
        if (ManagerInstances.SDatabase == null) {
            ConfigDatabaseWrapper wrap = new ConfigDatabaseWrapper();
            ManagerInstances.SDatabase = wrap.getConfigDatabase();
            readingConfigFile();
        }
        readingHistoryFile();

        convertedFiles = new LinkedList<File>();
        readingConvertedFile();
        deletedCachedFiles();

        // SystemManagerが持つ固有の設定値と設定ファイルの項目を同期
        JMPCore.getSystemManager().syncDatabase(IDataManager.CFG_KEY_FFMPEG_PATH);
        JMPCore.getSystemManager().syncDatabase(IDataManager.CFG_KEY_FFMPEG_INSTALLED);
        JMPCore.getSystemManager().syncDatabase(IDataManager.CFG_KEY_YOUTUBEDL_PATH);
        JMPCore.getSystemManager().syncDatabase(IDataManager.CFG_KEY_YOUTUBEDL_INSTALLED);

        // ロードファイルの初期化
        setLoadedFile("");

        return true;
    }

    protected boolean endFunc() {
        if (super.endFunc() == false) {
            return false;
        }

        deletedCachedFiles();
        outputConvertedFile();
        if (JMPLoader.UseConfigFile == true) {
            outputConfigFile();
        }
        if (JMPLoader.UseHistoryFile == true) {
            outputHistoryFile();
        }
        return true;
    }

    public String getReadInfoForAppName() {
        return ManagerInstances.SDatabase.getAppName();
    }

    public String getReadInfoForVersion() {
        return ManagerInstances.SDatabase.getVersion();
    }

    public void setConfigDatabase(ConfigDatabase db) {
        ManagerInstances.SDatabase = db;
    }

    public void clearCachedFiles(File eqnore) {
        if (convertedFiles.size() > 0) {
            for (int i = convertedFiles.size() - 1; i >= 0; i--) {
                File f = convertedFiles.get(i);
                if (eqnore.getName().equals(f.getName()) == true) {
                    continue;
                }

                if (f.exists() == true) {
                    if (JmpUtil.deleteFileDirectory(f) == true) {
                    }
                    JmpUtil.threadSleep(200);
                }
            }
        }
    }

    private void deletedCachedFiles() {
        if (convertedFiles.size() > 0) {
            for (int i = convertedFiles.size() - 1; i >= 0; i--) {
                File f = convertedFiles.get(i);
                if (f.exists() == true) {
                    if (JmpUtil.deleteFileDirectory(f) == true) {
                        convertedFiles.remove(i);
                    }
                    JmpUtil.threadSleep(200);
                }
            }
        }
    }

    public void initializeConfigDatabase() {

        ConfigDatabaseWrapper configWrap = new ConfigDatabaseWrapper(ManagerInstances.SDatabase);

        // 言語設定は初期化しないためバックアップする
        int backupLanguage = configWrap.getLanguage();
        // ファイル名バックアップ
        String backupFileName = new String(configWrap.getLoadedFile());
        // Midi設定は初期化できないためJMPがもつ設定のみバックアップする
        String backupMidiIn = new String(configWrap.getConfigParam(CFG_KEY_MIDIIN));
        String backupMidiOut = new String(configWrap.getConfigParam(CFG_KEY_MIDIOUT));

        // 初期化実行
        configWrap.initialize();

        // 言語設定を復元
        configWrap.setLanguage(backupLanguage);
        configWrap.setLoadedFile(backupFileName);
        configWrap.setConfigParam(CFG_KEY_MIDIIN, backupMidiIn);
        configWrap.setConfigParam(CFG_KEY_MIDIOUT, backupMidiOut);

        // 設定変更通知
        JMPCore.getTaskManager().sendNotifyMessage(NotifyID.UPDATE_CONFIG, ManagerInstances.CFG_KEY_INITIALIZE);
    }

    private boolean readingConfigFile() {
        String path = JmpUtil.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), CONFIG_FILE);
        return ManagerInstances.SDatabase.reading(path);
    }

    private boolean readingHistoryFile() {
        boolean ret = true;
        String path = JmpUtil.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), HISTORY_FILE);
        File file = new File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(path);

            historyData.clear();
            for (String line : textContents) {
                historyData.add(line);
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private boolean readingConvertedFile() {
        boolean ret = true;
        String path = JmpUtil.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), "cached");
        File file = new File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(path);

            for (String line : textContents) {
                File f = new File(line);
                if (f.exists() == true) {
                    convertedFiles.add(f);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }

        JmpUtil.deleteFileDirectory(file);
        return ret;
    }

    private boolean outputConfigFile() {
        String path = JmpUtil.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), CONFIG_FILE);
        return ManagerInstances.SDatabase.output(path);
    }

    private boolean outputHistoryFile() {
        boolean ret = true;
        String path = JmpUtil.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), HISTORY_FILE);

        try {
            JmpUtil.writeTextFile(path, historyData);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private boolean outputConvertedFile() {
        boolean ret = true;
        String saveDir = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR);
        String path = JmpUtil.pathCombin(saveDir, "cached");
        if (JmpUtil.isExsistFile(saveDir) == false) {
            return false;
        }

        if (convertedFiles.size() <= 0) {
            return true;
        }

        List<String> list = new LinkedList<String>();
        for (int i = 0; i < convertedFiles.size(); i++) {
            File f = convertedFiles.get(i);
            list.add(f.getAbsolutePath());
        }

        try {
            JmpUtil.writeTextFile(path, list);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    public final TypeOfKey getConfigParamKeyType(String key) {
        if (ManagerInstances.CFG_INIT_TABLE.containsKey(key) == false) {
            return TypeOfKey.STRING;
        }
        return ManagerInstances.CFG_INIT_TABLE.get(key).type;
    }

    @Override
    public String[] getKeySet() {
        String[] keyset = ManagerInstances.SDatabase.getKeySet();
        String[] arr = new String[keyset.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = keyset[i];
        }
        return arr;
    }

    public void addHistory(String path) {
        historyData.add(0, path);

        int size = historyData.size();
        if (size > MAX_HISTORY_SIZE) {
            historyData.remove(size - 1);
        }
    }

    public List<String> getHistory() {
        return historyData;
    }

    public int getHistorySize() {
        return historyData.size();
    }

    public void clearHistory() {
        historyData.clear();
    }

    public void addConvertedFile(File file) {
        if (convertedFiles.contains(file) == false) {
            convertedFiles.add(file);
        }
        outputConvertedFile();
    }
}
