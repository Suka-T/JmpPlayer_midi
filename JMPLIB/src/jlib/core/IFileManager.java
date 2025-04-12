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
     * 2ファイル同時ロード 
     * 
     * @param f プライマリファイル 
     * @param sub セカンダリファイル 
     */
    abstract void loadDualFile(File f, File sub);
    
    /**
     * 2ファイル同時ロード(ロード後、再生する)
     * 
     * @param f プライマリファイル 
     * @param sub セカンダリファイル 
     */
    abstract void loadDualFileToPlay(File f, File sub);

    /**
     * 2ファイル同時ロード 
     * 
     * @param path1 プライマリファイル 
     * @param path2 セカンダリファイル 
     */
    default void loadDualFile(String path1, String path2) {
        loadDualFile(new File(path1), new File(path2));
    }

    /**
     * 2ファイル同時ロード (ロード後、再生する)
     * 
     * @param path1 プライマリファイル 
     * @param path2 セカンダリファイル 
     */
    default void loadDualFileToPlay(String path1, String path2) {
        loadDualFileToPlay(new File(path1), new File(path2));
    }
    
    /**
     * リロード処理
     */
    abstract void reload();
}
