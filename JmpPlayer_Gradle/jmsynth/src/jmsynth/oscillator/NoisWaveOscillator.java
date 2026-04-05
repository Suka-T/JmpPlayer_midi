package jmsynth.oscillator;

import jmsynth.sound.Tone;

public class NoisWaveOscillator implements IOscillator {

    // private static double LEVEL_OFFSET = 1.0;

    class NoiseSim {
        public NoiseSim(int a) {
            amp = a;
        }

        public int amp = 0;
    }

    protected final static int NOISE_VAR = 32;

    private NoiseSim[] simSet = new NoiseSim[128];
    protected double[] moiseTable = new double[NOISE_VAR];
    static final int[] AMP_TABLE = { 150, 100, 50, 30, 20, 15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, };

    public NoisWaveOscillator() {
        int i = 0;
        int j = 0;
        while (true) {
            simSet[i] = new NoiseSim(AMP_TABLE[j]);
            i++;
            j++;
            if (j >= AMP_TABLE.length) {
                j = 0;
            }
            if (i >= simSet.length) {
                break;
            }
        }
        for (i = 0; i < NOISE_VAR; i++) {
            moiseTable[i] = Math.random();
        }
    }

    @Override
    public int makeTone(byte[] data, int bufSize, Tone tone, OscillatorConfig oscConfig) {
        int length = bufSize;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード

        double f = 0;// 生成データ一時格納変数（音量）

        /// * ノイズ周期の設定（短いほど高音になる） */
        // int amplitude = (int) (50 - tone.getNote());

        /* ノート番号の音階に従って音の高さを調整する */
        // int amplitude = (int) (tone.getNote() * 0.1);

        /* あらかじめ定義したAmpをシミュレートする */
        int amplitude = simSet[tone.getNote()].amp;

        double overallLevel = (double) (tone.getOverallLevel() * 2.0);// ネイティブに変数ロード

        byte y = 0;
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;

            /* toneStepがamplitudeを超えたら音量を変更する */
            if (toneStep > amplitude) {

                // Level計算用の係数を取得
                double nc = generateNoiseCoefficient();

                /* 音量データ生成 */
                f = (-overallLevel / 2 + (nc * overallLevel));
                toneStep = 0;
            }

            y = (byte) (data[i] + f);

            /* Lch 分 */
            data[i] = y;
            /* Rch 分 */
            data[i + 1] = y;

        }
        tone.setToneStep(toneStep);
        return length;
    }

    protected double generateNoiseCoefficient() {
        return 1.0;
    }

    @Override
    public boolean isToneSync() {
        return true;
    }

    @Override
    public int toneLoopPoint() {
        return 0;
    }

    @Override
    public int toneEndPoint() {
        return -1;
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return 0;
    }

}
