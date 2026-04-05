package function;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * エラー処理クラス
 *
 */
public class Error {
    /**
     * エラーメッセージを取得する
     *
     * @param e
     *            エラー
     */
    public static String getMsg(Exception e) {
        String msg = Utility.stringsCombin(GlobalConfig.ErrorHeaderMessage, Platform.getNewLine(), getPrintStackTrace(e));
        return msg;
    }

    /**
     * エラーメッセージをクリップボードにコピーする
     *
     * @param msg
     *            エラーメッセージ
     */
    public static void copyMsg(String msg) {
        String contents = Utility.stringsCombin(GlobalConfig.ErrorHeaderMessage, Platform.getNewLine(), msg);
        Utility.setClipboard(contents);
        return;
    }

    /**
     * エラーメッセージをクリップボードにコピーする
     *
     * @param e
     *            例外情報
     */
    public static void copyMsg(Exception e) {
        copyMsg(getPrintStackTrace(e));
        return;
    }

    /**
     * エラーメッセージを取得
     *
     * @param e
     *            例外情報
     * @return エラーメッセージ
     */
    public static String getPrintStackTrace(Exception e) {
        String msg = "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        msg = sw.toString();

        pw.close();
        pw = null;
        return msg;
    }

}
