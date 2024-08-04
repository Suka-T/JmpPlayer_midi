package jlib.core;

import jlib.plugin.IPlugin;
import jlib.util.IUtilityToolkit;

public interface ISystemManager {

    // パスのID

    /** "DATA"ディレクトリ */
    public static final int PATH_DATA_DIR = 0;
    /** "RES"ディレクトリ */
    public static final int PATH_RES_DIR = (PATH_DATA_DIR + 1);
    /** "PLUGINS"ディレクトリ */
    public static final int PATH_PLUGINS_DIR = (PATH_RES_DIR + 1);
    /** "JMS"ディレクトリ */
    public static final int PATH_JMS_DIR = (PATH_PLUGINS_DIR + 1);
    /** "JAR"ディレクトリ */
    public static final int PATH_JAR_DIR = (PATH_JMS_DIR + 1);
    /** "JMZ"ディレクトリ */
    public static final int PATH_ZIP_DIR = (PATH_JAR_DIR + 1);
    /** "OUTPUT"ディレクトリ */
    public static final int PATH_OUTPUT_DIR = (PATH_ZIP_DIR + 1);
    /** "SAVE"ディレクトリ */
    public static final int PATH_SAVE_DIR = (PATH_OUTPUT_DIR + 1);
    /** "activate"ファイル */
    public static final int PATH_ACTIVATE_FILE = (PATH_SAVE_DIR + 1);
    /** "syscommon"ファイル */
    public static final int PATH_SYSCOMMON_FILE = (PATH_ACTIVATE_FILE + 1);
    /** "SKIN"ディレクトリ */
    public static final int PATH_SKIN_DIR = (PATH_SYSCOMMON_FILE + 1);
    /** システムパス総数 */
    public static final int NUM_OF_PATH = (PATH_SKIN_DIR + 1);

    // 設定値取得ID

    /** MIDIファイル拡張子 */
    public static final int COMMON_REGKEY_NO_EXTENSION_MIDI = 0;
    /** WAVファイル拡張子 */
    public static final int COMMON_REGKEY_NO_EXTENSION_WAV = 1;
    /** MusicXMLファイル拡張子 */
    public static final int COMMON_REGKEY_NO_EXTENSION_MUSICXML = 2;
    /* MIDI Toolkit名 */
    public static final int COMMON_REGKEY_NO_USE_MIDI_TOOLKIT = 3;
    /* UTIL Toolkit名 */
    public static final int COMMON_REGKEY_NO_USE_UTIL_TOOLKIT = 4;
    /** プレイヤー背景色 */
    public static final int COMMON_REGKEY_NO_PLAYER_BACK_COLOR = 5;
    /** デバッグモード */
    public static final int COMMON_REGKEY_NO_DEBUGMODE = 6;
    /** チャンネルカラーフォーマット */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT = 7;
    /** チャンネルカラー 1ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_1 = 8;
    /** チャンネルカラー 2ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_2 = 9;
    /** チャンネルカラー 3ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_3 = 10;
    /** チャンネルカラー 4ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_4 = 11;
    /** チャンネルカラー 5ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_5 = 12;
    /** チャンネルカラー 6ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_6 = 13;
    /** チャンネルカラー 7ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_7 = 14;
    /** チャンネルカラー 8ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_8 = 15;
    /** チャンネルカラー 9ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_9 = 16;
    /** チャンネルカラー 10ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_10 = 17;
    /** チャンネルカラー 11ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_11 = 18;
    /** チャンネルカラー 12ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_12 = 19;
    /** チャンネルカラー 13ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_13 = 20;
    /** チャンネルカラー 14ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_14 = 21;
    /** チャンネルカラー 15ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_15 = 22;
    /** チャンネルカラー 16ch */
    public static final int COMMON_REGKEY_NO_CH_COLOR_FORMAT_16 = 23;
    /** ffmpeg出力パス */
    public static final int COMMON_REGKEY_NO_FFMPEG_OUTPUT = 24;
    /** ffmpegコマンド windows */
    public static final int COMMON_REGKEY_NO_FFMPEG_WIN = 25;
    /** ffmpegコマンド mac */
    public static final int COMMON_REGKEY_NO_FFMPEG_MAC = 26;
    /** ffmpegコマンド other */
    public static final int COMMON_REGKEY_NO_FFMPEG_OTHER = 27;
    /** 歌詞文字エンコード */
    public static final int COMMON_REGKEY_NO_LYRIC_CHARCODE = 28;
    /** メディアファイル拡張子 */
    public static final int COMMON_REGKEY_NO_EXTENSION_MEDIA = 29;
    /** MMLファイル拡張子 */
    public static final int COMMON_REGKEY_NO_EXTENSION_MML = 30;
    /** 連続再生の動作(DIR=ディレクトリ内、PLT=プレイリスト) */
    public static final int COMMON_REGKEY_NO_AUTOPLAY_FUNC = 31;
    /** cregキー数 */
    public static final int NUMBER_OF_COMMON_REGKEY = 32;

    /**
     * スタンドアロンモードか
     *
     * @return
     */
    abstract boolean isEnableStandAlonePlugin();

    /**
     * ファイルパス取得
     *
     * @param id
     *            取得したいパスのID
     * @param plugin
     *            自身のプラグインインスタンス
     * @return パス文字列
     */
    abstract String getSystemPath(int id, IPlugin plugin);

    /**
     * プラグイン名取得
     *
     * @param plugin
     *            自身のプラグインインスタンス
     * @return プラグイン名
     */
    abstract String getPluginName(IPlugin plugin);

    /**
     * 共通レジスタに対してパラメータ設定を行う
     *
     * @param key
     *            キー文字列
     * @param value
     *            パラメータ
     * @return 設定に成功したか?
     */
    abstract boolean setCommonRegisterValue(String key, String value);

    /**
     * 共通レジスタに対してパラメータ設定を行う
     *
     * @param keyNo
     *            キー番号
     * @param value
     *            パラメータ
     * @return 設定に成功したか?
     */
    default boolean setCommonRegisterValue(int keyNo, String value) {
        return setCommonRegisterValue(getCommonRegisterKeyName(keyNo), value);
    }

    /**
     * 共通レジスタからパラメータを取得する
     *
     * @param key
     *            キー文字列
     * @return パラメータ(取得できなかった場合は、""を返す)
     */
    abstract String getCommonRegisterValue(String key);

    /**
     * 共通レジスタからパラメータを取得する
     *
     * @param keyNo
     *            キー番号
     * @return パラメータ(取得できなかった場合は、""を返す)
     */
    default String getCommonRegisterValue(int keyNo) {
        return getCommonRegisterValue(getCommonRegisterKeyName(keyNo));
    }

    /**
     * 共通レジスタのキー名を取得する
     *
     * @param keyNo
     *            キー番号
     * @return キー名(取得できなかった場合は、""を返す)
     */
    abstract String getCommonRegisterKeyName(int keyNo);

    /**
     * 共通レジスタのキーセットを取得する
     *
     * @return キーセット
     */
    abstract String[] getCommonKeySet();

    /**
     * 便利関数ツールキット取得
     *
     * @return ツールキット
     */
    abstract IUtilityToolkit getUtilityToolkit();

    /**
     * 現在設定中の言語コードを取得する。
     *
     * @return 言語コード
     */
    abstract String getCurrentLanguageCode();

    /**
     * 対応する言語コードを取得する。
     *
     * @return 言語コード(配列に格納)
     */
    abstract String[] getSupportedLanguageCode();
}
