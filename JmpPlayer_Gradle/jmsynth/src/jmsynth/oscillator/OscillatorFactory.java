package jmsynth.oscillator;

public class OscillatorFactory {
    
    public static final String OSCILLATOR_NAME_SINE = "SINE";
    public static final String OSCILLATOR_NAME_SAW = "SAW";
    public static final String OSCILLATOR_NAME_TRIANGLE = "TRIANGLE";
    public static final String OSCILLATOR_NAME_SQUARE = "SQUARE";
    public static final String OSCILLATOR_NAME_PULSE = "PULSE";
    public static final String OSCILLATOR_NAME_PULSE25 = "PULSE25";
    public static final String OSCILLATOR_NAME_PULSE125 = "PULSE125";
    public static final String OSCILLATOR_NAME_NOISE_L = "LONG_NOISE";
    public static final String OSCILLATOR_NAME_NOISE_S = "SHORT_NOISE";

    public OscillatorFactory() {
    }
    
    public IOscillator createOscillator(String name) {
        IOscillator ret  = null;
        if (name.equalsIgnoreCase(OSCILLATOR_NAME_SINE) == true) {
            ret  = new SinWaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_SAW) == true) {
            ret  = new SawWaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_TRIANGLE) == true) {
            ret  = new TriWaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_SQUARE) == true) {
            ret  = new SquareWaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_PULSE) == true) {
            ret  = new PulseWaveOscillator(0.25);
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_PULSE25) == true) {
            ret  = new Pulse25WaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_PULSE125) == true) {
            ret  = new Pulse125WaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_NOISE_L) == true) {
            ret  = new LongNoisWaveOscillator();
        }
        else if (name.equalsIgnoreCase(OSCILLATOR_NAME_NOISE_S) == true) {
            ret  = new ShortNoisWaveOscillator();
        }
        return ret;
    }

}
