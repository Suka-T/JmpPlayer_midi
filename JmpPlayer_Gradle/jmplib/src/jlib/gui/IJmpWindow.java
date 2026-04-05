package jlib.gui;

public interface IJmpWindow {
    /**
     * ウィンドウ表示化
     */
    abstract void showWindow();

    /**
     * ウィンドウ非表示化
     */
    abstract void hideWindow();

    /**
     * ウィンドウ表示/非表示
     */
    default void setWindowVisible(boolean visible) {
        if (visible == true) {
            showWindow();
        }
        else {
            hideWindow();
        }
    }

    /**
     * ウィンドウ表示/非表示取得
     */
    abstract boolean isWindowVisible();

    /**
     * デフォルト位置に戻す
     */
    abstract void setDefaultWindowLocation();

    /**
     * 画面再描画
     */
    abstract void repaintWindow();

    /**
     * ウィンドウ表示/非表示切り替え
     */
    default void toggleWindowVisible() {
        if (isWindowVisible() == true) {
            hideWindow();
        }
        else {
            showWindow();
        }
    }

    /*
     * 言語更新イベント
     */
    default void updateLanguage() {
    }

    /**
     * レイアウトの初期化
     */
    default void initializeLayout() {
    }

    /**
     * 設定変更イベント
     *
     * @param key
     *            設定キー
     */
    default void updateConfig(String key) {
    }

    /**
     * 再生前イベント
     */
    default void processingBeforePlay() {
    }

    /**
     * 再生後イベント
     */
    default void processingAfterPlay() {
    }

    /**
     * 停止前イベント
     */
    default void processingBeforeStop() {
    }

    /**
     * 停止後イベント
     */
    default void processingAfterStop() {
    }
}
