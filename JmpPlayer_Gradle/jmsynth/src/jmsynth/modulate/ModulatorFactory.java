package jmsynth.modulate;

import java.util.Vector;

public class ModulatorFactory {

    public static final int DEFAULT_DEPTH = 0;

    private Vector<Modulator> targets = null;

    public ModulatorFactory() {
        targets = new Vector<Modulator>();
    }

    public Modulator newModulatorInstance() {
        Modulator m = new Modulator();
        m.setDepth(DEFAULT_DEPTH);
        targets.add(m);
        return m;
    }

    public void timerStart() {
        for (int i = 0; i < targets.size(); i++) {
            Modulator m = targets.get(i);
            if (m == null) {
                continue;
            }
            m.startMod();
        }
    }

    public void dispose() {
        for (int i = 0; i < targets.size(); i++) {
            Modulator m = targets.get(i);
            if (m == null) {
                continue;
            }
            m.endMod();
        }
    }

}
