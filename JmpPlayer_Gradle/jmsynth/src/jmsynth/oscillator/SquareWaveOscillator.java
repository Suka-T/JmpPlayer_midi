package jmsynth.oscillator;

public class SquareWaveOscillator extends AbstractWaveGenOscillator {

    public SquareWaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_SQUARE;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSquareWave(f, overallLevel, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }

}
