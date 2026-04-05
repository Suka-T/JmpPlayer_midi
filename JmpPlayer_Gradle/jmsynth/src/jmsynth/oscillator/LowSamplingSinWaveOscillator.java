package jmsynth.oscillator;

public class LowSamplingSinWaveOscillator extends AbstractWaveGenOscillator {

    public LowSamplingSinWaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_SINE;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSinWaveForLowSampling(f, overallLevel, oscConfig.isWaveReverse());
    }

}
