package jmsynth.oscillator;

public class OscillatorConfig {

    private boolean isWaveReverse = false;
    private boolean isValidNesSimulate = false;

    public OscillatorConfig() {
        setWaveReverse(false);
    }

    /**
     * 逆波形
     *
     * @return
     */
    public boolean isWaveReverse() {
        return isWaveReverse;
    }

    /**
     * 逆波形
     *
     * @param isReverse
     */
    public void setWaveReverse(boolean isWaveReverse) {
        this.isWaveReverse = isWaveReverse;
    }

    /**
     * FES風の波形モード
     *
     * @return
     */
    public boolean isValidNesSimulate() {
        return isValidNesSimulate;
    }

    /**
     * FES風の波形モード
     *
     * @param isValidNesSimulate
     */
    public void setValidNesSimulate(boolean isValidNesSimulate) {
        this.isValidNesSimulate = isValidNesSimulate;
    }
}
