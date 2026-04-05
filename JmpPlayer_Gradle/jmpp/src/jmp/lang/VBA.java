package jmp.lang;

import java.util.HashMap;

import jmp.lang.DefineLanguage.LangID;

/**
 * VBAで出力するファイル内に記載されている定義
 *
 * @author akkut
 *
 */
public class VBA {
    // 出力ソース文字コード
    public static final String SRC_CHARSET = "UTF-16";

    // 改行コード
    static final String NL = function.Platform.getNewLine();

    // 特殊タグ変換テーブル
    private static final HashMap<String, String> SSpecialTagTable = new HashMap<String, String>() {
        {
            put(DefineLanguage.CODE_LINE_END, VBA.NL); // 改行タグ
        }
    };

    /**
     * 特殊タグ文字列の差し替え
     * 
     * @param src
     * @return
     */
    static String replaceSpecialTag(String src) {
        String str = new String(src);
        for (String key : SSpecialTagTable.keySet()) {
            if (str.contains(key) == true) {
                str = str.replace(key, SSpecialTagTable.get(key));
            }
        }
        return str;
    }
}

/* HashMapのラッパー */
class LangMap extends java.util.HashMap<LangID, LanguageWords> {
};
