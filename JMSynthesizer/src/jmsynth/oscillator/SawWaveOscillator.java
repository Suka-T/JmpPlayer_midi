package jmsynth.oscillator;

public class SawWaveOscillator extends AbstractWaveGenOscillator {

    public SawWaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_SAW;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSawWave(f, overallLevel, oscConfig.isWaveReverse());
    }

}
