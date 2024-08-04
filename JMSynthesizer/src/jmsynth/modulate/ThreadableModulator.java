package jmsynth.modulate;

public class ThreadableModulator extends Modulator implements Runnable {

    private boolean isRunnable = true;
    private Thread timerThread = null;

    public ThreadableModulator() {
        super();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        while (isRunnable) {
            try {
                process();
                Thread.sleep(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startMod() {
        timerThread.start();
    }

    @Override
    public void endMod() {
        isRunnable = false;
        try {
            timerThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
