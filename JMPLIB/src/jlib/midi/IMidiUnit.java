package jlib.midi;

import javax.sound.midi.Sequence;

/**
 * 高度なMidi関係の設定にアクセスするためのクラス
 *
 * @author akkut
 *
 */
public interface IMidiUnit {

    /**
     * Midiシーケンサーの再生状態取得
     *
     * @return
     */
    abstract boolean isRunning();

    /**
     * BPMベースのテンポを取得
     *
     * @return
     */
    abstract double getTempoInBPM();

    /**
     * 現在のティック取得
     *
     * @return
     */
    abstract long getTickPosition();

    /**
     * 総ティック数取得
     *
     * @return
     */
    abstract long getTickLength();

    /**
     * シーケンスオブジェクト取得
     *
     * @return
     */
    abstract Sequence getSequence();

    /**
     * 現在秒数取得(usec)
     *
     * @return
     */
    abstract long getMicrosecondPosition();

    /**
     * 総秒数取得(usec)
     * 
     * @return
     */
    abstract long getMicrosecondLength();
}
