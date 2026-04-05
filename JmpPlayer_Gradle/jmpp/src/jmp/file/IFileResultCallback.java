package jmp.file;

public interface IFileResultCallback {

    /** 事前判定結果 */
    void begin(FileResult result);

    /** 終了結果 */
    void end(FileResult result);
}
