package jlib.plugin;

import java.io.File;

/**
 * JMPPプラグインインターフェース
 *
 * @author abs
 *
 */
public interface IPlugin {

    /**
     * プラグイン初期化
     */
    abstract void initialize();

    /**
     * プラグイン終了
     */
    abstract void exit();

    /**
     * プラグインオープン処理
     */
    abstract void open();

    /**
     * プラグインクローズ処理
     */
    abstract void close();

    /**
     * 更新処理
     */
    abstract void update();

    /**
     * プラグイン有効状態
     *
     * @return 有効状態
     */
    abstract boolean isEnable();

    /**
     * プラグインのオープン状態か判定
     *
     * @return 状態
     */
    abstract boolean isOpen();

    /**
     * ファイルロード
     *
     * @param file
     *            ファイル
     */
    abstract void loadFile(File file);

    /**
     * CommonRedister更新通知
     *
     * @param key
     *            キー名
     */
    abstract void notifyUpdateCommonRegister(String key);

    /**
     * Config更新通知
     *
     * @param key
     *            キー名
     */
    abstract void notifyUpdateConfig(String key);
}
