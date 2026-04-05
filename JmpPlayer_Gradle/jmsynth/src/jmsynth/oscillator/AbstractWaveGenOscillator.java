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
            if (toneStep > amplitude) {
                toneStep = 0;
            }
            
            double f = (1.0 * (double) toneStep / (double) amplitude) - (double) (toneStep / amplitude);
            y = (byte) (makeWave(f, (overallLevel & 0xff), oscConfig));
            
            int mix = data[i] + y;
            mix = clamp(mix, -128, 127);

            /* Lch 分 */
            data[i] = (byte)mix;

            /* Rch 分 */
            data[i + 1] = (byte)mix;
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
    
    public static int clamp(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
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
