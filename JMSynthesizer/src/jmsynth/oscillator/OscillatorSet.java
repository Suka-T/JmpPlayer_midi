package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.List;

import jmsynth.envelope.EnvelopeFactory;

public class OscillatorSet {

    // 複数のオシレータ設計を考慮（出力側は未実装）
    private List<IOscillator> oscs = null;

    private double attackTime = 0.0;
    private double decayTime = 0.0;
    private double sustainLevel = 1.0;
    private double releaseTime = 0.0;

    private long maxAttackMills = 1000;
    private long maxDecayMills = 1000;
    private long maxReleaseMills = 1000;

    public OscillatorSet(IOscillator... osc) {
        oscs = new ArrayList<IOscillator>();
        oscs.clear();
        setOscillators(osc);
        attackTime = EnvelopeFactory.DEFAULT_A;
        decayTime = EnvelopeFactory.DEFAULT_D;
        sustainLevel = EnvelopeFactory.DEFAULT_S;
        releaseTime = EnvelopeFactory.DEFAULT_R;
        maxAttackMills = EnvelopeFactory.DEFAULT_MAX_A;
        maxDecayMills = EnvelopeFactory.DEFAULT_MAX_D;
        maxReleaseMills = EnvelopeFactory.DEFAULT_MAX_R;
    }

    public OscillatorSet(double a, double d, double s, double r, IOscillator... osc) {
        oscs = new ArrayList<IOscillator>();
        oscs.clear();
        setOscillators(osc);
        attackTime = a;
        decayTime = d;
        sustainLevel = s;
        releaseTime = r;
        maxAttackMills = EnvelopeFactory.DEFAULT_MAX_A;
        maxDecayMills = EnvelopeFactory.DEFAULT_MAX_D;
        maxReleaseMills = EnvelopeFactory.DEFAULT_MAX_R;
    }

    public OscillatorSet(double a, double d, double s, double r, long ma, long md, long mr, IOscillator... osc) {
        oscs = new ArrayList<IOscillator>();
        oscs.clear();
        setOscillators(osc);
        attackTime = a;
        decayTime = d;
        sustainLevel = s;
        releaseTime = r;
        maxAttackMills = ma;
        maxDecayMills = md;
        maxReleaseMills = mr;
    }

    public void addOscillators(IOscillator osc) {
        oscs.add(osc);
    }
    
    public void setOscillators(IOscillator... osc) {
        oscs.clear();
        for (int i = 0; i < osc.length; i++) {
            oscs.add(osc[i]);
        }
    }

    public IOscillator getOscillator() {
        return getOscillator(0);
    }

    public IOscillator getOscillator(int index) {
        if (size() <= 0) {
            // 例外
            return null;
        }
        return oscs.get(index);
    }

    public int size() {
        return oscs.size();
    }

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
    }

    public double getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(double decayTime) {
        this.decayTime = decayTime;
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
    }

    public long getMaxAttackMills() {
        return maxAttackMills;
    }

    public void setMaxAttackMills(long maxAttackMills) {
        this.maxAttackMills = maxAttackMills;
    }

    public long getMaxDecayMills() {
        return maxDecayMills;
    }

    public void setMaxDecayMills(long maxDecayMills) {
        this.maxDecayMills = maxDecayMills;
    }

    public long getMaxReleaseMills() {
        return maxReleaseMills;
    }

    public void setMaxReleaseMills(long maxReleaseMills) {
        this.maxReleaseMills = maxReleaseMills;
    }
}
