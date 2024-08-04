package jmsynth.oscillator;

public class Pulse125WaveOscillator extends AbstractWaveGenOscillator {

    public Pulse125WaveOscillator() {
    }
    
    @Override
    public String getOscillatorName() {
        // Factoryの識別名を返す  
        return OscillatorFactory.OSCILLATOR_NAME_PULSE125;
    }


    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makePulseWave(f, overallLevel, 0.125, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }


}
