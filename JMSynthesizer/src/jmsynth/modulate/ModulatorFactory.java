package jmsynth.modulate;

import java.util.Vector;

public class ModulatorFactory implements Runnable {

    public static final int DEFAULT_DEPTH = 0;

    private boolean isRunnable = true;
    private Thread timerThread = null;
    private Vector<Modulator> targets = null;
    private boolean useMultiThread = true;

    public ModulatorFactory(boolean useMultiThread) {
        this.useMultiThread = useMultiThread;
        targets = new Vector<Modulator>();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    public Modulator newModulatorInstance() {
        Modulator m;
        if (this.useMultiThread == false) {
            m = new Modulator();
        }
        else {
            m = new ThreadableModulator();
        }
        m.setDepth(DEFAULT_DEPTH);
        targets.add(m);
        return m;
    }

    public void timerStart() {
        if (this.useMultiThread == false) {
            timerThread.start();
        }
        else {
            for (int i = 0; i < targets.size(); i++) {
                Modulator m = targets.get(i);
                if (m == null) {
                    continue;
                }
                m.startMod();
            }
        }
    }

    @Override
    public void run() {
        while (isRunnable) {
            try {
                for (int i = 0; i < targets.size(); i++) {
                    Modulator m = targets.get(i);
                    if (m == null) {
                        continue;
                    }
                    m.process();
                }
                Thread.sleep(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dispose() {
        isRunnable = false;
        try {
            if (this.useMultiThread == false) {
                timerThread.join();
            }
            else {
                for (int i = 0; i < targets.size(); i++) {
                    Modulator m = targets.get(i);
                    if (m == null) {
                        continue;
                    }
                    m.endMod();
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
