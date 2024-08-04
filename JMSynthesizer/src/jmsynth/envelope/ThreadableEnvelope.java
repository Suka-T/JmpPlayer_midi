package jmsynth.envelope;

public class ThreadableEnvelope extends Envelope implements Runnable {

    private boolean isRunnable = true;
    private Thread timerThread = null;

    public ThreadableEnvelope() {
        super();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void startEnv() {
        timerThread.start();
    }

    @Override
    public void endEnv() {
        isRunnable = false;
        try {
            timerThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunnable) {
            try {
                this.process();
                Thread.sleep(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
