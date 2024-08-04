package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 並列処理でプロセスを実行する機能を提供するクラス
 *
 * @author akkut
 *
 */
public class ProcessInvoker {

    private boolean isConsoleOut = true;

    private void pPrintln() {
        if (isConsoleOut() == true) {
            consoleOutCallback.println();
        }
    }

    private void pPrintln(String str) {
        if (isConsoleOut() == true) {
            consoleOutCallback.println(str);
        }
    }

    private void pPrint(String str) {
        if (isConsoleOut() == true) {
            consoleOutCallback.print(str);
        }
    }

    public class InputStreamThread extends Thread {
        private BufferedReader br;
        private boolean isExit = false;

        public InputStreamThread(InputStream is) {
            br = new BufferedReader(new InputStreamReader(is));
        }

        @Override
        public void run() {
            try {
                while (!isExit) {
                    String line = br.readLine();
                    if (line != null) {
                        pPrintln(line);
                    }

                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void exit() {
            isExit = true;
        }
    }

    public class LogingRunnable implements Runnable {
        private boolean isRunnable = true;
        private Process proc;

        public LogingRunnable(Process p) {
            proc = p;
            isRunnable = true;
        }

        @Override
        public void run() {
            pPrintln("## Processing... ##");
            notifyBegin();

            try {
                InputStreamThread ist = null;
                InputStreamThread est = null;
                if (isConsoleOut() == true) {
                    ist = new InputStreamThread(proc.getInputStream());
                    est = new InputStreamThread(proc.getErrorStream());
                    ist.start();
                    est.start();
                }

                proc.waitFor();

                if (isConsoleOut() == true) {
                    ist.exit();
                    est.exit();
                    ist.join();
                    est.join();
                }

                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = proc.exitValue();

            pPrintln("## EXIT(" + result + ") ##");
            notifyEnd(result);

            isRunnable = false;
        }

        private void notifyBegin() {
            if (callback != null) {
                callback.begin();
            }
        }

        private void notifyEnd(int result) {
            if (callback != null) {
                callback.end(result);
            }
        }

        public boolean isRunnable() {
            return isRunnable;
        }

    }

    /** waitあり */
    protected boolean waitFor = false;

    private IProcessingCallback callback = null;
    private IConsoleOutCallback consoleOutCallback = null;

    public ProcessInvoker() {
        waitFor = false;
        callback = null;
    }

    public void setCallback(IProcessingCallback callback) {
        this.callback = callback;
    }

    public boolean isWaitFor() {
        return waitFor;
    }

    public void setWaitFor(boolean waitFor) {
        this.waitFor = waitFor;
    }

    /**
     * コマンド実行
     *
     * @param cmd
     *            コマンド
     * @throws IOException
     * @throws InterruptedException
     */
    public void exec(List<String> cmd) throws IOException {
        for (String c : cmd) {
            pPrint(c + " ");
        }
        pPrintln();

        ProcessBuilder pb = new ProcessBuilder();
        // pb.inheritIO();

        pb.command(cmd);
        Process p = pb.start();

        LogingRunnable lr = new LogingRunnable(p);
        ExecutorService execTh = Executors.newSingleThreadExecutor();
        execTh.submit(lr);

        if (isWaitFor() == true) {
            while (lr.isRunnable() == true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;
                }

                if (isWaitFor() == false) {
                    // Wait設定解除時に抜ける
                    break;
                }
            }
        }

    }

    public boolean isConsoleOut() {
        if (consoleOutCallback == null) {
            return false;
        }
        return isConsoleOut;
    }

    public void setConsoleOut(boolean isConsoleOut) {
        this.isConsoleOut = isConsoleOut;
    }

    public void setConsoleOutCallback(IConsoleOutCallback consoleOutCallback) {
        this.consoleOutCallback = consoleOutCallback;
    }
}
