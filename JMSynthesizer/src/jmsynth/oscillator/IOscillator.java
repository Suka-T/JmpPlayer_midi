package jmsynth.oscillator;

import jmsynth.sound.Tone;

public interface IOscillator {

    public static double COMMON_LEVEL_OFFSET = 1.0;
    
    public default String getOscillatorName() { return ""; };

    /**
     * 音色生成メソッド SampleRate：44100 Channels：1 SampleSizeInBits：16
     *
     * @param _data
     *            再生データ
     * @param bufSize
     *            バッファサイズ
     * @param tone
     *            音色データ
     * @return 生成したデータサイズ
     *
     *
     */
    public int makeTone(byte[] _data, int bufSize, Tone tone, OscillatorConfig oscConfig);

    abstract byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig);
    
    /**
     * 発音時に再生ポインタを初期化せずに続きから再生するか？<br>
     * （ループ音源はtrue推奨）
     * 
     * @return true：続きから再生
     */
    public boolean isToneSync();

    /**
     * 音源データのループ開始ポイントを返す<br>
     * （未設定の場合は0を返す）
     */
    public int toneLoopPoint();

    /**
     * 音源データのループ終了ポイントを返す<br>
     * （未設定の場合は音源データのサイズを返す）
     */
    public int toneEndPoint();

}
