package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.List;

import jmsynth.sound.Tone;

public class OscillatorManager extends AbstractWaveGenOscillator {
    private Object mutex = new Object();
    public static final int NUM_OF_OSCILLATOR = 3;
    private List<IOscillator> aOsc;

    public OscillatorManager() {
        aOsc = new ArrayList<IOscillator>();

        synchronized (mutex) {
            aOsc.clear();
        }
    }

    public IOscillator getOscillator(int index) {
        if (aOsc.size() <= 0) {
            return null;
        }
        return aOsc.get(index);
    }

    public void clearWave() {
        synchronized (mutex) {
            aOsc.clear();
        }
    }

    public int getOscillatorCount() {
        return aOsc.size();
    }

    public void addOscillator(IOscillator osc) {
        synchronized (mutex) {
            aOsc.add(osc);
        }
    }

    private boolean isNoiseSound() {
        if (getOscillatorCount() > 0) {
            /* Noiseは波形合成できないため専用のインターフェースを介する */
            if (getOscillator(0).getOscillatorName().equals(OscillatorFactory.OSCILLATOR_NAME_NOISE_L)
                    || getOscillator(0).getOscillatorName().equals(OscillatorFactory.OSCILLATOR_NAME_NOISE_S)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int makeTone(byte[] data, int sampleRate, Tone tone, OscillatorConfig oscConfig) {
        synchronized (mutex) {
            if (isNoiseSound() == true) {
                /* Noiseは波形合成できないため専用のインターフェースを介する */
                return getOscillator(0).makeTone(data, sampleRate, tone, oscConfig);
            }
            return super.makeTone(data, sampleRate, tone, oscConfig);
        }
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        if (isNoiseSound() == true) {
            /* Noiseは波形合成できないため専用のインターフェースを介する */
            return 0;
        }
        
        int count = 0;
        int data = 0;
        for (int i = 0; i < getOscillatorCount(); i++) {
            IOscillator osc = getOscillator(i);
            data += osc.makeWave(f, overallLevel, oscConfig);
            count++;
        }
        if (count <= 0) {
            return 0;
        }
        return (byte) (data / count);
    }

}
