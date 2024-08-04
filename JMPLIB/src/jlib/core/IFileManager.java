package jlib.core;

import java.io.File;

public interface IFileManager {

    /**
     * ファイルロード処理 <br>
     * (ロード後に再生)
     *
     * @param f
     *            ファイル
     */
    abstract void loadFileToPlay(File f);

    /**
     * ファイルロード処理
     *
     * @param f
     *            ファイル
     */
    abstract void loadFile(File f);

    /**
     * ファイルロード処理
     *
     * @param path
     *            ファイルパス
     */
    default void loadFile(String path) {
        loadFile(new File(path));
    }

    /**
     * ファイルロード処理(ロード後、再生する)
     *
     * @param path
     *            ファイルパス
     */
    default void loadFileToPlay(String path) {
        loadFileToPlay(new File(path));
    }

    /**
     * リロード処理
     */
    abstract void reload();
}
