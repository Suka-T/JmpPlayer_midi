package jlib.core;

import jlib.midi.IMidiController;
import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiToolkit;
import jlib.midi.IMidiUnit;

public interface ISoundManager {

    /**
     * 再生中か
     *
     * @return true=再生中, false=停止中
     */
    abstract boolean isPlay();

    /**
     * 再生
     */
    abstract void play();

    /**
     * 停止
     */
    abstract void stop();

    /**
     * 再生/停止のトグル
     */
    default void togglePlayStop() {
        if (isPlay() == true) {
            stop();
        }
        else {
            play();
        }
    };

    /**
     * 再生データのポジション設定
     *
     * @param pos
     *            ポジション
     */
    abstract void setPosition(long pos);

    /**
     * 再生データのポジション取得
     *
     * @return ポジション
     */
    abstract long getPosition();

    /**
     * 再生データのサイズ取得
     *
     * @return サイズ
     */
    abstract long getLength();

    /**
     * ポジションの秒数
     *
     * @return
     */
    abstract int getPositionSecond();

    /**
     * サイズの秒数
     *
     * @return
     */
    abstract int getLengthSecond();

    /**
     * 開始位置に設定
     */
    default void initPosition() {
        setPosition(0);
    }

    /**
     * 終了位置に設定
     */
    default void endPosition() {
        setPosition(getLength() - 1);
    }

    /**
     * 1タップの移動量
     *
     * @return
     */
    default long getAmount() {
        long amount = 0;
        long length = getLength();
        if (length > 0) {
            // ポジションの移動量
            amount = length / 100;
        }
        return amount;
    }

    /**
     * 巻き戻し
     */
    default void rewind() {
        long tick = getPosition();
        tick -= getAmount();
        if (tick < 0) {
            tick = 0;
        }
        setPosition(tick);
    }

    /**
     * 早送り
     */
    default void fastForward() {
        long tick = getPosition();
        tick += getAmount();
        if (tick > getLength()) {
            tick = getLength();
        }
        setPosition(tick);
    }

    /**
     * サポートする拡張子か判定する
     *
     * @param extension
     *            拡張子
     * @return
     */
    abstract boolean isSupportedExtension(String extension);

    /**
     * Midiツールキット取得
     *
     * @return
     */
    abstract IMidiToolkit getMidiToolkit();

    /**
     * Midiコントローラ取得
     *
     * @param senderType
     *            センダータイプ
     * @return
     */
    abstract IMidiController getMidiController(short senderType);

    default IMidiController getMidiController() {
        return getMidiController(IMidiEventListener.SENDER_MIDI_OUT);
    }

    /**
     * 高度なMidi設定を取得する
     *
     * @return
     */
    abstract IMidiUnit getMidiUnit();

    /**
     * ラインボリューム設定
     *
     * @param v
     */
    abstract void setLineVolume(float v);

    /**
     * ラインボリューム取得
     *
     * @return
     */
    abstract float getLineVolume();

    /**
     * トランスポーズ取得
     *
     * @param channel
     * @return
     */
    abstract int getTranspose(int channel);

    /**
     * トランスポーズ設定
     *
     * @param channel
     * @param transpose
     */
    abstract void setTranspose(int channel, int transpose);

}
