package jmsynth.oscillator;

public class Pulse25WaveOscillator extends AbstractWaveGenOscillator {

    public Pulse25WaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_PULSE25;
    }


    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makePulseWave(f, overallLevel, 0.25, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }

}
