package jlib.gui;

public interface IJmpMainWindow extends IJmpWindow {

    /**
     * ファイルオープン処理実行
     */
    abstract void fileOpenFunc();

    /**
     * ステータステキスト設定
     *
     * @param text
     *            テキスト
     * @param status
     *            ステータス
     */
    abstract void setStatusText(String text, boolean status);

    /**
     * ステータスメッセージ初期化
     */
    abstract void clearStatusMessage();

    /**
     * 更新処理
     */
    abstract void update();

    /**
     * 終了
     */
    abstract void exit();

    /**
     * 設定初期化(起動時の1度のみ呼び出される)
     */
    default void initializeSetting() {
    }

    /**
     * 歌詞表示
     *
     * @param text
     */
    abstract void setLyric(String text);
}
