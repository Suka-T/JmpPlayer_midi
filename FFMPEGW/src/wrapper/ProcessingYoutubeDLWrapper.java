package wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import process.IConsoleOutCallback;
import process.IProcessingCallback;
import process.ProcessInvoker;

public class ProcessingYoutubeDLWrapper {

    public enum FileNameConfig {
        WEB_TITLE, WEB_ID,
    }

    /** 実行パス指定 */
    protected String path = "";
    
    /** 実行コマンド指定 */
    protected String command = "";

    /** 環境変数定義有効 */
    private boolean isYoutubeDlInstalled = false;

    private boolean audioOnly = true;

    private FileNameConfig fileNameConfig = FileNameConfig.WEB_TITLE;

    protected String output = "";

    protected ProcessInvoker invoker = null;

    public ProcessingYoutubeDLWrapper() {
        this.invoker = new ProcessInvoker();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
    
    public void setCommand(String command) {
        this.command = new String(command);
    }

    public String getCommand() {
        return this.command;
    }

    public void setOutput(String path) {
        this.output = path;
    }

    public String getOutput() {
        return this.output;
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

    public boolean isValid() {
        if (isYoutubeDlInstalled() == true) {
            return true;
        }
        File f = new File(path);
        return f.exists();
    }

    public void convert(String url, String format) throws IOException {
        ArrayList<String> cmd = new ArrayList<String>();
        // if (output.isEmpty() == false) {
        // cmd.add("-o");
        // cmd.add(output);
        // }
        cmd.add("\"" + url + "\"");

        switch (getFileNameConfig()) {
            case WEB_ID:
                cmd.add("--id");
                break;
            case WEB_TITLE:
            default:
                break;
        }
        if (isAudioOnly() == true) {
            cmd.add("-x");
            cmd.add("--audio-format");
        }
        else {
            cmd.add("-f");
        }
        cmd.add(format);

        if (isYoutubeDlInstalled() == true) {
            cmd.add(0, getCommand());
        }
        else {
            cmd.add(0, "\"" + path + "\"");
        }
        invoker.exec(cmd);
    }

    public boolean isYoutubeDlInstalled() {
        return isYoutubeDlInstalled;
    }

    public void setYoutubeDlInstalled(boolean isYoutubeDlInstalled) {
        this.isYoutubeDlInstalled = isYoutubeDlInstalled;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public void setConsoleOut(IConsoleOutCallback cb) {
        invoker.setConsoleOutCallback(cb);
    }

    public FileNameConfig getFileNameConfig() {
        return fileNameConfig;
    }

    public void setFileNameConfig(FileNameConfig fileNameConfig) {
        this.fileNameConfig = fileNameConfig;
    }
}
