package jmsynth.oscillator;

public class TriWaveOscillator extends AbstractWaveGenOscillator {

    public TriWaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_TRIANGLE;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeTriangleWave(f, overallLevel, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }

}
