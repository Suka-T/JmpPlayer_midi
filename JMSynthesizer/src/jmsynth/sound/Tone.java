package jmsynth.sound;

public class Tone {

    // 周波数
    private double frequency;
    // 波長(バイト数)
    private double amplitude;

    // Note情報
    private float defaultPitch = 440;
    private int note = 1;

    private double pitch = 0;
    private int overallLevel = 0; // (ベロシティ値 ÷ 8) × (エクスプレッション値 ÷ 127)
    private int velocity = 60; // ベロシティ
    private int expression = 127; // エクスプレッション値
    private int tempExpression = 127; // エクスプレッション値(リリース用に内部的に保持する)

    private int toneStep = 0;
    private double tablePointer = 0;// Waveテーブルのポインタ
    private double stapTimingCounter = 0;// Waveテーブルのポインタを進めるタイミングのカウンター

    private float vibratoDepth = 0;

    private double envelopeOffset = 1.0;
    private long startMills = 0;

    private boolean releaseFlag = false;

    private double modulationValue = 0.0;

    public Tone() {
    }

    public void reset() { //
        //setVelocity(127);

        defaultPitch = 440;
        //note = 1;
        //pitch = 0;
        // vibrato = 0;
        expression = 127; // エクスプレッション値

        // vibratoDepth = 3;
        // vibratoRate = 0.5;
        // vibratoDelay = 60;
        // vibratoCount = 0;
        // variation = 0;
        //startMills = 0;
        setOverallLevel();
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        if (frequency <= 0) {
            return;
        }
        this.amplitude = (double)SoundSourceChannel.SAMPLE_RATE / frequency; // 振幅
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public int getNote() {
        return note;
    }

    private double note2freq(double note) {
        return (double) Math.floor(defaultPitch * Math.pow(2.0, ((double) note - 69.0) / 12.0));
    }

    public void setNote(int note) {
        this.pitch = 0;
        this.note = note;
        this.toneStep = 0;
        setFrequency(note2freq(note));
    }

    public int getOverallLevel() {
        return overallLevel;
    }

    public void setOverallLevel() {
        int exp = expression;
        if (releaseFlag == true) {
            exp = tempExpression;
        }
        this.overallLevel = (int) (((double) velocity / 8.0) * ((double) exp / 127.0) * (double) envelopeOffset);

    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
        setOverallLevel();
    }

    public int getExpression() {
        return expression;
    }

    public void setExpression(int expression) {
        if (expression > 127) {
            expression = 127;
        }
        this.expression = expression;
        setOverallLevel();
    }

    public int getToneStep() {
        return toneStep;
    }

    public void setToneStep(int toneStep) {
        this.toneStep = toneStep;
    }

    public double getTablePointer() {
        return tablePointer;
    }

    public void setTablePointer(double tablePointer) {
        this.tablePointer = tablePointer;
    }

    public double getStapTimingCounter() {
        return stapTimingCounter;
    }

    public void setStapTimingCounter(double stapTimingCounter) {
        this.stapTimingCounter = stapTimingCounter;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
        setFrequency(note2freq(((double) note + pitch + modulationValue)));
    }

    public float getVibratoDepth() {
        return vibratoDepth;
    }

    public void setVibratoDepth(float vibratoDepth) {
        this.vibratoDepth = vibratoDepth;
    }

    public double getEnvelopeOffset() {
        return this.envelopeOffset;
    }

    public void setEnvelopeOffset(double envelopeOffset) {
        this.envelopeOffset = envelopeOffset;
        setOverallLevel();
    }

    public void resetEnvelopeOffset() {
        this.envelopeOffset = 1.0;
    }

    public long getStartMills() {
        return startMills;
    }

    public void setStartMills(long startMills) {
        this.startMills = startMills;
        setEnvelopeOffset(0.0);
    }

    public void setStartMills() {
        setStartMills(System.currentTimeMillis());
    }

    public boolean isReleaseFlag() {
        return releaseFlag;
    }

    public void setReleaseFlag(boolean releaseFlag) {
        this.releaseFlag = releaseFlag;
        if (this.releaseFlag == true) {
            tempExpression = expression;
        }
    }

    public void setModulationValue(double modulationValue) {
        this.modulationValue = modulationValue;
        setPitch(getPitch()); // ピッチの再計算
    }

}
