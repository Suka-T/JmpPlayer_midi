package jmsynth.oscillator;

public class PulseWaveOscillator extends AbstractWaveGenOscillator {
    private double duty = 0.5;

    public PulseWaveOscillator() {
    }
    
    public PulseWaveOscillator(double duty) {
        this.duty = duty;
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_PULSE;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makePulseWave(f, overallLevel, this.duty, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }
}
