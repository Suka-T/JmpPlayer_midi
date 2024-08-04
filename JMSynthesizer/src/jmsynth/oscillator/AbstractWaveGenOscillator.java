package jmsynth.oscillator;

import jmsynth.sound.Tone;

public abstract class AbstractWaveGenOscillator implements IOscillator {

    private final static double LEVEL_OFFSET = COMMON_LEVEL_OFFSET;

    public AbstractWaveGenOscillator() {
    }

    @Override
    public int makeTone(byte[] data, int sampleRate, Tone tone, OscillatorConfig oscConfig) {
        int length = sampleRate;// ネイティブに変数ロード
        int toneStep = tone.getToneStep();// ネイティブに変数ロード
        byte overallLevel = (byte) (tone.getOverallLevel());// ネイティブに変数ロード

        byte y = 0;// 生成データ一時格納変数（音量）

        /* 周期の設定（短いほど高音になる） */
        int amplitude = (int) tone.getAmplitude();

        overallLevel *= LEVEL_OFFSET;
        for (int i = 0; i < length; i = i + 2) {
            toneStep++;
            double f = (1.0 * (double) toneStep / (double) amplitude) - (double) (toneStep / amplitude);
            y = (byte) (makeWave(f, (overallLevel & 0xff), oscConfig));

            /* Lch 分 */
            data[i] += y;

            /* Rch 分 */
            data[i + 1] += y;
        }
        if (toneStep >= (Integer.MAX_VALUE - 100000)) {
            /*
             * toneStepがintの最大値を超えないようにする。
             * ユースケースとしてNoteONが最大値を超えるほど押されることはありえないので 音声が一瞬途切れるが単純に0クリアする。
             */
            toneStep = 0;
        }
        tone.setToneStep(toneStep);
        return length;
    }

    @Override
    public boolean isToneSync() {
        return false;
    }

    @Override
    public int toneLoopPoint() {
        return 0;
    }

    @Override
    public int toneEndPoint() {
        return -1;
    }
}
