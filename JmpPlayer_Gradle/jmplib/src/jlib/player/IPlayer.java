package jlib.player;

public interface IPlayer {
    /**
     * 再生
     */
    public abstract void play();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 再生中/停止中の状態
     *
     * @return ステータス
     */
    public abstract boolean isRunnable();

    /**
     * 再生データのポジション設定
     *
     * @param pos
     *            ポジション
     */
    public abstract void setPosition(long pos);

    /**
     * 再生データのポジション取得
     *
     * @return ポジション
     */
    public abstract long getPosition();

    /**
     * 再生データのサイズ取得
     *
     * @return サイズ
     */
    public abstract long getLength();

    /**
     * 有効/無効
     *
     * @return
     */
    public abstract boolean isValid();

    /**
     * ポジションの秒数
     *
     * @return
     */
    public abstract int getPositionSecond();

    /**
     * サイズの秒数
     *
     * @return
     */
    public abstract int getLengthSecond();

    /**
     * ボリューム設定
     *
     * @param volume
     *            ボリューム
     */
    public abstract void setVolume(float volume);

    /**
     * ボリューム取得
     *
     * @return ボリューム
     */
    public abstract float getVolume();

    /**
     * サポートする拡張子か判定する
     *
     * @param extension
     *            拡張子
     * @return
     */
    public abstract boolean isSupportedExtension(String extension);

    /**
     * サポートする拡張子一覧を取得する
     *
     * @return
     */
    public abstract String[] getSupportExtentions();
}
