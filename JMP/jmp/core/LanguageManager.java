package jmp.core;

import java.util.HashMap;
import java.util.Map;

import function.Platform;
import function.Platform.KindOfPlatform;
import jmp.lang.DefineLanguage;
import jmp.lang.DefineLanguage.LangID;
import jmp.lang.FontRsrc;
import jmp.lang.FontSet;
import jmp.lang.LanguageTable;

/**
 *
 * 言語マネージャー
 *
 */
public class LanguageManager extends AbstractManager {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_UTF16 = "UTF-16";
    public static final String CHARSET_SJIS = "SJIS";
    public static final String CHARSET_EUCKR = "EUC_KR";// 韓国語

    /** デフォルト文字コード */
    public static final String CHARSET_DEFAULT = CHARSET_UTF16;

    private static final Map<KindOfPlatform, Integer> s_platToFontFamily = new HashMap<KindOfPlatform, Integer>() {
        {
            put(KindOfPlatform.WINDOWS, FontSet.FONT_OF_WIN);
            put(KindOfPlatform.MAC, FontSet.FONT_OF_MAC);
            put(KindOfPlatform.LINUX, FontSet.FONT_OF_OTHR);
            put(KindOfPlatform.SUN_OS, FontSet.FONT_OF_OTHR);
            put(KindOfPlatform.OTHER, FontSet.FONT_OF_OTHR);
        }
    };

    private FontRsrc defaultFontRsrc = null;
    private Map<Integer, FontSet> fontInfos = null;

    /**
     * コンストラクタ
     */
    LanguageManager() {
        super("language");
    }

    /**
     * フォントリソース作成
     */
    public void makeFontRsrc() {

        /* OSごとのフォントファミリを登録 */

        fontInfos = new HashMap<Integer, FontSet>();

        defaultFontRsrc = new FontRsrc(WindowManager.DEFAULT_FONT);

        int lang = 0;
        String charset;
        FontRsrc win, mac, other;

        /* フォントファミリ(English) */
        lang = DefineLanguage.INDEX_LANG_ENGLISH;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Open Sans");
        mac = new FontRsrc("Helvetica Neue");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));

        /* フォントファミリ(Japanese) */
        lang = DefineLanguage.INDEX_LANG_JAPANESE;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Meiryo");
        mac = new FontRsrc("Hiragino Sans");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));

        /* フォントファミリ(Chinese) */
        lang = DefineLanguage.INDEX_LANG_CHINESE;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Microsoft YaHei");
        mac = new FontRsrc("PingFang SC");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));

        /* フォントファミリ(Traditional Chinese) */
        lang = DefineLanguage.INDEX_LANG_TRADITIONALCHINESE;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Helvetica");
        mac = new FontRsrc("SF Pro TC");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));

        /* フォントファミリ(Korean) */
        lang = DefineLanguage.INDEX_LANG_KOREAN;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Malgun Gothic");
        mac = new FontRsrc("Gulim");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));

        /* フォントファミリ(Russian) */
        lang = DefineLanguage.INDEX_LANG_RUSSIAN;
        charset = CHARSET_DEFAULT;
        win = new FontRsrc("Times New Roman");
        mac = new FontRsrc("Times");
        other = defaultFontRsrc;
        fontInfos.put(lang, new FontSet(charset, win, mac, other));
    }

    /**
     * フォント名取得
     * 
     * @param lang
     * @param type
     * @return
     */
    public String getFontName(int lang, int type) {
        FontRsrc f;
        if (fontInfos.containsKey(lang) == true) {
            int family = FontSet.FONT_OF_OTHR;
            KindOfPlatform plat = Platform.getRunPlatform();
            if (s_platToFontFamily.containsKey(plat) == true) {
                family = s_platToFontFamily.get(plat);
            }
            f = fontInfos.get(lang).getRsrc(family);
        }
        else {
            f = defaultFontRsrc;
        }
        return f.getName(type);
    }

    /**
     * フォント名取得
     * 
     * @param lang
     * @return
     */
    public String getFontName(int lang) {
        return getFontName(lang, 0);
    }

    /**
     * 対応している言語か
     *
     * @param index
     * @return
     */
    public boolean isValidLanguageIndex(int index) {
        if (fontInfos.containsKey(index) == true) {
            return fontInfos.get(index).isValid();
        }
        return false;
    }

    private String getCode(int index) {
        String code = CHARSET_DEFAULT;
        if (fontInfos.containsKey(index) == true) {
            code = fontInfos.get(index).getCharset();
        }
        return code;
    }

    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public String getTitle(int index, int lang) {
        return LanguageTable.getTitle(index, lang, getCode(index));
    }

    /**
     * 言語タイトルからインデックスを取得
     *
     * @param title
     * @return
     */
    public int getIndex(LangID title) {
        return LanguageTable.getIndex(title);
    }

    /**
     * 文字列取得
     *
     * @param id
     *            文字列ID
     * @return 文字列
     */
    public String getLanguageStr(LangID id) {
        int langIndex = getLanguage();
        return getLanguageStr(id, langIndex);
    }

    /**
     * 文字列取得
     *
     * @param id
     * @param langIndex
     * @return
     */
    public String getLanguageStr(LangID id, int langIndex) {
        return LanguageTable.getLanguageStr(id, langIndex, getCode(langIndex));
    }

    /**
     * 現在の言語名
     *
     * @return
     */
    public String getCurLanguageName() {
        return getTitle(getLanguage(), DefineLanguage.INDEX_LANG_ENGLISH);
    }

    public String getReadmeContent() {
        int langIndex = getLanguage();
        return LanguageTable.getReadmeContent(langIndex);
    }

    /**
     * 言語コード取得
     *
     * @param langIndex
     * @return
     */
    public String getLanguageCode(int langIndex) {
        return LanguageTable.getLangCode(langIndex);
    }

    /**
     * 言語コードインデックス取得
     *
     * @param code
     * @return
     */
    public int getLanguageCodeIndex(String code) {
        return LanguageTable.getLangCodeIndex(code);
    }
}
