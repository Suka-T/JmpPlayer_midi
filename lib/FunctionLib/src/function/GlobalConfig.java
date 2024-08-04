package function;

/**
 * 公開設定項目
 *
 * @author akkut
 *
 */
public class GlobalConfig {

    /** OS識別文字列(linux) */
    public static String OS_IdentificationStr_LINUX = "linux";
    /** OS識別文字列(Windows) */
    public static String OS_IdentificationStr_WINDOWS = "windows";
    /** OS識別文字列(mac) */
    public static String OS_IdentificationStr_MAC = "mac";
    /** OS識別文字列(sunos) */
    public static String OS_IdentificationStr_SUNOS = "sunos";

    /**
     * StringsCombinメソッドにおいてデフォルト設定でStringBuilderを使用するか<br>
     * (true : StringBuilder使用, false : StringBuffer使用)
     */
    public static boolean IsDefaultUsingStringBuilder = true;

    /**
     * ログ出力時の日付フォーマット
     */
    public static String LogStringFormat = "yyyyMMdd_HHmmss";

    /**
     * エラーメッセージに付与するヘッダー文字列を設定
     */
    public static String ErrorHeaderMessage = "---- Error ----";

    /**
     * デフォルトで使用するエンコード
     */
    public static String DefaultCharset = "UTF-8";

    public static String getGlobalConfigStr() {
        String line = "";
        String ret = "";

        final String sep = " = ";
        final String newLine = Platform.getNewLine();

        line = Utility.stringsCombin("OS_IdentificationStr_LINUX", sep, OS_IdentificationStr_LINUX);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("OS_IdentificationStr_WINDOWS", sep, OS_IdentificationStr_WINDOWS);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("OS_IdentificationStr_MAC", sep, OS_IdentificationStr_MAC);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("OS_IdentificationStr_SUNOS", sep, OS_IdentificationStr_SUNOS);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("IsDefaultUsingStringBuilder", sep, IsDefaultUsingStringBuilder ? "TRUE" : "FALSE");
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("LogStringFormat", sep, LogStringFormat);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("ErrorHeaderMessage", sep, ErrorHeaderMessage);
        ret = Utility.stringsCombin(ret, line, newLine);

        line = Utility.stringsCombin("DefaultCharset", sep, DefaultCharset);
        ret = Utility.stringsCombin(ret, line, newLine);

        return ret;
    }
}
