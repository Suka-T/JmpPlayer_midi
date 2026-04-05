package jmsynth.oscillator;

public class ShortNoisWaveOscillator extends NoisWaveOscillator {

    private int ntStep = 0;

    public ShortNoisWaveOscillator() {
        super();
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_NOISE_S;
    }

    @Override
    protected double generateNoiseCoefficient() {
        // 短周期を繰り返す
        double val = moiseTable[ntStep];
        ntStep++;
        if (ntStep >= NOISE_VAR) {
            ntStep = 0;
        }
        return val;
    }

}
