package jmsynth.oscillator;

public class WaveValuePoint {
    public double level = 0.0;// -1,0 ~ 1.0
    public double f = 0.0;// 0,0 ~ 1.0

    public WaveValuePoint(double f, double level) {
        this.level = level;
        this.f = f;
    }
}
