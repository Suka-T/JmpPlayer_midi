package wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import process.IConsoleOutCallback;
import process.IProcessingCallback;
import process.ProcessInvoker;
import wffmpeg.FFmpegWrapper;

/**
 * OSのプロセスからFFmpegを使用するためのラッパークラス
 *
 * @author akkut
 *
 */
public class ProcessingFFmpegWrapper extends FFmpegWrapper {
    /** FFmpegの実行パス */
    protected String path = "";

    /** 環境変数定義有効 */
    protected boolean isFFmpegInstalled = false;

    /** コマンド */
    protected String ffmpegCommand = "ffmpeg";

    protected ProcessInvoker invoker = null;

    /**
     * コンストラクタ
     *
     * @param path
     *            FFmpegの実行パス
     */
    public ProcessingFFmpegWrapper(String path) {
        super();

        this.path = path;
        this.invoker = new ProcessInvoker();
    }

    public ProcessingFFmpegWrapper() {
        super();

        this.invoker = new ProcessInvoker();
    }

    @Override
    protected void init() {
        super.init();
        this.path = "";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isWaitFor() {
        return invoker.isWaitFor();
    }

    public void setWaitFor(boolean waitFor) {
        invoker.setWaitFor(waitFor);
    }

    public void setCallback(IProcessingCallback callback) {
        invoker.setCallback(callback);
    }

    @Override
    public boolean isValid() {
        if (isFFmpegInstalled() == true) {
            return true;
        }
        File f = new File(path);
        return f.exists();
    }

    @Override
    public void convert(String inPath, String outPath) throws IOException {
        ArrayList<String> cmd = new ArrayList<String>();
        if (isOverwrite() == true) {
            // 上書き有無
            cmd.add("-y");
        }
        cmd.add("-i");
        cmd.add(inPath);
        cmd.add(outPath);

        if (isFFmpegInstalled() == true) {
            boolean isExistsCurrent = false;
            File exeFile = new File("ffmpeg.exe");
            if (path.isEmpty() == false && exeFile.exists() == true) {
                isExistsCurrent = true;
            }

            if (isExistsCurrent == true) {
                // カレントに有効なffmpegがあるときはそちらを優先的に使用する
                cmd.add(0, exeFile.getPath());
            }
            else {
                cmd.add(0, getFFmpegCommand());
            }
        }
        else {
            cmd.add(0, path);
        }
        invoker.exec(cmd);
    }

    public boolean isFFmpegInstalled() {
        return isFFmpegInstalled;
    }

    public void setFFmpegInstalled(boolean isFFmpegInstalled) {
        this.isFFmpegInstalled = isFFmpegInstalled;
    }

    public String getFFmpegCommand() {
        return ffmpegCommand;
    }

    public void setFFmpegCommand(String ffmpegCommand) {
        this.ffmpegCommand = ffmpegCommand;
    }

    public void setConsoleOut(IConsoleOutCallback cb) {
        invoker.setConsoleOutCallback(cb);
    }
}
