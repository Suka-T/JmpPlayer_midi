package jmsynth.oscillator;

public class SinWaveOscillator extends AbstractWaveGenOscillator {

    public SinWaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_SINE;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        if (oscConfig.isValidNesSimulate() == true) {
            return WaveGenerater.makeSinWave(f, overallLevel, oscConfig.isWaveReverse(), 95.0);
        }
        else {
            return WaveGenerater.makeSinWave(f, overallLevel, oscConfig.isWaveReverse());
        }
    }

}
