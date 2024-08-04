package wffmpeg;

import java.io.IOException;

/**
 * FFmpegを使用するためのラッパークラス
 *
 * @author akkut
 *
 */
public abstract class FFmpegWrapper {

    /** 上書き設定 */
    private boolean isOverwrite = false;

    public FFmpegWrapper() {
        init();
    }

    /**
     * 初期化
     */
    protected void init() {
        isOverwrite = false;
    }

    /**
     * 変換実行
     *
     * @param inPath
     *            入力ファイルパス(変換前)
     * @param outPath
     *            出力ファイルパス(変換後)
     * @throws IOException
     */
    public void convert(String inPath, String outPath) throws IOException {
    };

    /**
     * FFmpegの有効状態
     *
     * @return
     */
    public boolean isValid() {
        return true;
    };

    /**
     * 上書き設定状態
     *
     * @return
     */
    public boolean isOverwrite() {
        return isOverwrite;
    }

    /**
     * 上書き設定
     *
     * @param isOverwrite
     */
    public void setOverwrite(boolean isOverwrite) {
        this.isOverwrite = isOverwrite;
    }
}
