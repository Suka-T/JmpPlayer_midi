package jmp;

import java.io.File;

import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.TaskManager;
import jmp.file.ConfigDatabaseWrapper;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;

class InvokeTask implements Runnable {

    private boolean invokeResult = false;
    private ConfigDatabaseWrapper config;
    private IPlugin standAlonePlugin;
    private File loadFile;

    public InvokeTask(ConfigDatabaseWrapper config, IPlugin standAlonePlugin, File loadFile) {
        this.config = config;
        this.standAlonePlugin = standAlonePlugin;
        this.loadFile = loadFile;
    }

    @Override
    public void run() {

        // ライブラリ初期化処理
        boolean result = JMPLibrary.initCoreAssets(config, standAlonePlugin);

        /* 起動準備 */
        if (result == true) {
            TaskManager taskManager = JMPCore.getTaskManager();

            // コマンド引数で指定されたファイルを開く
            if (loadFile != null) {
                if (loadFile.canRead() == true) {
                    taskManager.queuing(new ICallbackFunction() {
                        @Override
                        public void callback() {
                            JmpUtil.threadSleep(500);
                            JMPCore.getFileManager().loadFileToPlay(loadFile);
                        }
                    });
                }
            }

            // タスクジョイン
            try {
                taskManager.join();
            }
            catch (InterruptedException e) {
                JMPCore.ErrorId = ErrorDef.ERROR_ID_UNKNOWN_EXIT_APPLI;
            }
        }

        // ライブラリ終了処理
        boolean endResult = JMPLibrary.exitCoreAssets();
        if (result == false) {
            endResult = result;
        }
        invokeResult = endResult;

        if (JMPCore.ErrorId != ErrorDef.ERROR_ID_NOERROR) {
            JMPCore.getSystemManager().showSystemErrorMessage(JMPCore.ErrorId);
        }
    }

    public boolean getResult() {
        return invokeResult;
    }
}
