package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jlib.core.IFileManager;
import jmp.file.FileResult;
import jmp.file.IFileResultCallback;
import jmp.gui.ui.FileListTableModel;
import jmp.task.ICallbackFunction;

public class FileManager extends AbstractManager implements IFileManager {

    public enum AutoPlayMode {
        DIRECTORY, PLAY_LIST
    }

    private AutoPlayMode autoPlayMode = AutoPlayMode.DIRECTORY;

    private List<IFileResultCallback> loadCallbacks = null;

    /* ファイルリスト */
    private static final String[] columnNames = new String[] { "", "Name" };
    private DefaultTableModel fileListModel;
    private JTable fileList;

    public DefaultTableModel getFileListModel() {
        return fileListModel;
    }

    public JTable getFileList() {
        return fileList;
    }

    FileManager() {
        super("file");
    }

    @Override
    protected boolean initFunc() {
        loadCallbacks = new ArrayList<IFileResultCallback>();
        fileListModel = new FileListTableModel(columnNames, 0);
        fileList = new JTable(fileListModel);
        return super.initFunc();
    }

    /**
     * 指定ディレクトリにあるファイルを列挙する
     *
     * @param dir
     *            ディレクトリ
     * @return ファイルリスト(key:ファイル名, value:Fileオブジェクト)
     */
    public Map<String, File> getFileMap(File dir) {
        HashMap<String, File> result = new HashMap<String, File>();
        if (dir.isDirectory() == false) {
            return result;
        }

        for (File file : dir.listFiles()) {
            if (file == null) {
                continue;
            }

            if (file.exists() == false) {
                // 除外ケース
                continue;
            }
            result.put(file.getName(), file);
        }
        return result;
    }

    public void addLoadResultCallback(IFileResultCallback loadResultCallback) {
        loadCallbacks.add(loadResultCallback);
    }

    void callFileResultBegin(FileResult beginResult) {
        for (IFileResultCallback cb : loadCallbacks) {
            cb.begin(beginResult);
        }
    }

    void callFileResultEnd(FileResult endResult) {
        for (IFileResultCallback cb : loadCallbacks) {
            cb.end(endResult);
        }
    }

    @Override
    public void loadFileToPlay(File f) {
        ICallbackFunction func = FileCallbackCreator.getInstance().createLoadCallback(f, true);
        execFileProcess(func);
    }

    @Override
    public void loadFile(File f) {
        ICallbackFunction func = FileCallbackCreator.getInstance().createLoadCallback(f, false);
        execFileProcess(func);
    }

    private void execFileProcess(ICallbackFunction func) {
        // 実処理はSequenceタスクに委譲する
        JMPCore.getTaskManager().queuing(func);
    }

    @Override
    public void reload() {
        String path = JMPCore.getDataManager().getLoadedFile();
        loadFile(path);
    }

    public AutoPlayMode getAutoPlayMode() {
        return autoPlayMode;
    }

    public void setAutoPlayMode(AutoPlayMode autoPlayMode) {
        this.autoPlayMode = autoPlayMode;
    }
}
