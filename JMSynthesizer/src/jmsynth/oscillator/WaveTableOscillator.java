package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.List;

public class WaveTableOscillator extends AbstractWaveGenOscillator {

    public final WaveValuePoint START_POINT = new WaveValuePoint(0.0, 0.0);
    public final WaveValuePoint END_POINT = new WaveValuePoint(1.0, 0.0);

    List<WaveValuePoint> points = null;

    public WaveTableOscillator(WaveValuePoint... p) {
        points = new ArrayList<WaveValuePoint>();
        for (WaveValuePoint vp : p) {
            points.add(vp);
        }
    }

    public WaveTableOscillator(List<WaveValuePoint> points) {

    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return designedWave(f, overallLevel);
    }

    public byte designedWave(double f, int overallLevel) {
        byte data = 0;
        WaveValuePoint currentPoint = null;
        WaveValuePoint nextPoint = null;
        for (WaveValuePoint p : points) {
            if (currentPoint == null) {
                currentPoint = START_POINT;
                nextPoint = p;
            }
            else {
                currentPoint = nextPoint;
                nextPoint = p;
            }

            int d = calc(f, overallLevel, currentPoint, nextPoint);
            if (d != -1) {
                data = (byte) d;
                break;
            }
        }
        currentPoint = nextPoint;
        nextPoint = END_POINT;
        int d = calc(f, overallLevel, currentPoint, nextPoint);
        if (d != -1) {
            data = (byte) d;
        }
        return data;
    }

    private int calc(double f, int overallLevel, WaveValuePoint currentPoint, WaveValuePoint nextPoint) {
        int data = -1;
        if (currentPoint.f <= f && f < nextPoint.f) {
            double fRen = nextPoint.f - currentPoint.f;
            double fCur = f - currentPoint.f;

            double lRen = nextPoint.level - currentPoint.level;
            double curLevel = currentPoint.level + (lRen * (fCur / fRen));
            data = (int) ((double) overallLevel * curLevel);
        }
        return data;
    }
}
