package jmp.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import jmp.JMPFlags;
import jmp.core.asset.FileLoadCoreAsset;
import jmp.file.FileResult;
import jmp.lang.DefineLanguage.LangID;
import jmp.plugin.PluginWrapper;
import jmp.task.ICallbackFunction;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public class FileCallbackCreator {

    private static FileCallbackCreator instance = new FileCallbackCreator();

    private FileCallbackCreator() {
    }

    public static FileCallbackCreator getInstance() {
        return instance;
    }

    public abstract class FileCallbackFunction implements ICallbackFunction {
        protected FileResult beginResult;
        protected FileResult endResult;
        protected File file;

        public FileCallbackFunction() {
            this.beginResult = new FileResult();
            this.endResult = new FileResult();
        }

        @Override
        public void preCall() {
            /* 事前の判定 */
            validatePreProcces();

            // 事前判定の結果を通知
            JMPCore.getTaskManager().sendNotifyMessage(NotifyID.FILE_RESULT_BEGIN, beginResult);
        }

        @Override
        public void callback() {
            if (beginResult.status == true) {
                fileProcces();
            }
        }

        @Override
        public void postCall() {
            // 後処理
            cleanupProcces();

            if (endResult.status == true) {
                // 終了判定の結果を通知
                JMPCore.getTaskManager().sendNotifyMessage(NotifyID.FILE_RESULT_END, endResult);
            }
        }

        /**
         * 事前判定
         */
        abstract void validatePreProcces();

        /**
         * 後処理
         */
        abstract void cleanupProcces();

        /**
         * ファイルメイン処理
         */
        abstract void fileProcces();
    }

    private static final String SUCCESS_MSG_FOAMET_LOAD = "%s ...(%s)";

    /**
     * ファイルロードの実処理
     * 
     * @author akkut
     *
     */
    private class LoadCallbackFunc extends FileCallbackFunction {
        private boolean noneHistoryFlag = false;
        private boolean loadToPlayFlag = false;

        public LoadCallbackFunc(File f, boolean noneHistoryFlag, boolean toPlay) {
            super();
            this.file = f;
            this.noneHistoryFlag = noneHistoryFlag;
            this.loadToPlayFlag = toPlay;
        }

        @Override
        public void validatePreProcces() {
            SystemManager system = JMPCore.getSystemManager();
            LanguageManager lm = JMPCore.getLanguageManager();
            SoundManager sm = JMPCore.getSoundManager();

            String ex = JmpUtil.getExtension(file);

            /* 事前の判定 */
            beginResult.status = true;
            beginResult.statusMsg = lm.getLanguageStr(LangID.Now_loading);
            if (JMPFlags.NowLoadingFlag == true) {
                // ロード中
                beginResult.status = false;
                beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_1);
            }
            else if (sm.isPlay() == true) {
                // 再生中
                beginResult.status = false;
                beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_3);
            }
            else if (file.getPath().isEmpty() == true || file.canRead() == false || file.exists() == false) {
                // アクセス不可
                beginResult.status = false;
                beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_4);
            }
            else if (sm.isSupportedExtensionAccessor(ex) == false) {
                // サポート外
                beginResult.status = false;
                beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_2);
            }
            else if (system.isEnableStandAlonePlugin() == true) {
                // サポート外(スタンドアロンモード時)
                PluginWrapper pw = JMPCore.getStandAlonePluginWrapper();
                if (pw.isSupportExtension(file) == false) {
                    beginResult.status = false;
                    beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_2);
                }
            }
        }

        @Override
        void fileProcces() {
            /* Coreのロード処理 */
            DataManager dm = JMPCore.getDataManager();
            SoundManager sm = JMPCore.getSoundManager();
            LanguageManager lm = JMPCore.getLanguageManager();

            // ロード中フラグを立てる
            JMPFlags.NowLoadingFlag = true;

            // ファイル名をバックアップ
            String tmpFileName = dm.getLoadedFile();

            dm.clearCachedFiles(file);

            /* ロード処理 */
            endResult.status = true;
            endResult.statusMsg = "";

            /* coreのOperateをコール */
            FileLoadCoreAsset asset = new FileLoadCoreAsset(file, endResult);
            JMPCore.operate(asset, true);

            /* 事後処理 */
            if (endResult.status == true) {
                
                JMPFlags.Log.cprintln(">> File load success.", true);
                JMPFlags.Log.cprintln(file.getName(), true);
                String sFileSize = "?? ";
                long fileSize = -1;
                try {
                    fileSize = Files.size(file.toPath());
                    if (fileSize < 1024L) {
                        sFileSize = String.format("%d ", fileSize);
                    }
                    else if (fileSize < (1024L * 1000L)) {
                        sFileSize = String.format("%.3f K", (double)fileSize / 1024.0);
                    }
                    else if (fileSize < (1024L * 1000000L)) {
                        sFileSize = String.format("%.3f M", (double)fileSize / (double)(1024L * 1000));
                    }
                    else if (fileSize < (1024L * 1000000000L)) {
                        sFileSize = String.format("%.3f G", (double)fileSize / (double)(1024L * 1000000L));
                    }
                }
                catch (IOException e) {
                }
                JMPFlags.Log.cprintln(sFileSize + "B", true);
                JMPFlags.Log.cprintln("", true);

                // 履歴に追加
                if (this.noneHistoryFlag == false) {
                    dm.addHistory(file.getPath());
                }

                // 自動再生
                if (this.loadToPlayFlag == true) {
                    sm.play();
                }

                // 新しいファイル名
                dm.setLoadedFile(file.getPath());

                // メッセージ発行
                String successFileName = JmpUtil.getFileNameAndExtension(dm.getLoadedFile());
                String successMsg = lm.getLanguageStr(LangID.FILE_LOAD_SUCCESS);
                endResult.statusMsg = String.format(SUCCESS_MSG_FOAMET_LOAD, successFileName, successMsg);

                if (sm.getCurrentPlayerInfo() != null) {
                    sm.getCurrentPlayerInfo().update();

                    JMPFlags.Log.cprintln(">> PlayerInfo", true);
                    JMPFlags.Log.cprintln(sm.getCurrentPlayerInfo().getMessage(), true);
                }
            }
            else {
                JMPFlags.Log.cprintln(">> File load falied.", true);
                
                // 前のファイル名に戻す
                dm.setLoadedFile(tmpFileName);

                // ファイル読み込み失敗時、連続再生を停止する
                JMPFlags.NextPlayFlag = false;
            }
        }

        @Override
        void cleanupProcces() {
            // フラグ初期化
            JMPFlags.NoneHistoryLoadFlag = false; // 履歴保存

            // ロード中フラグ解除
            JMPFlags.NowLoadingFlag = false;
        }
    }

    public FileCallbackFunction createLoadCallback(File f, boolean toPlay) {
        return new LoadCallbackFunc(f, JMPFlags.NoneHistoryLoadFlag, toPlay);
    }
}
