package jlib.core;

public interface IDataManager {

    /** プレイリスト表示中のフォルダパス */
    public static final String CFG_KEY_PLAYLIST = "PLAYLIST";
    /** MIDI_OUT デバイス名 */
    public static final String CFG_KEY_MIDIOUT = "MIDIOUT";
    /** MIDI_IN デバイス名 */
    public static final String CFG_KEY_MIDIIN = "MIDIIN";
    /** 自動再生 */
    public static final String CFG_KEY_AUTOPLAY = "AUTOPLAY";
    /** ループ再生 */
    public static final String CFG_KEY_LOOPPLAY = "LOOPPLAY";
    /** 起動MIDIデバイス設定を開く */
    public static final String CFG_KEY_SHOW_STARTUP_DEVICE_SETUP = "SHOW_STARTUP_DEVICE_SETUP";
    /** 言語設定 */
    public static final String CFG_KEY_LANGUAGE = "LANGUAGE";
    /** ロードしたファイル名 */
    public static final String CFG_KEY_LOADED_FILE = "LOADED_FILE";
    /** 歌詞表示 */
    public static final String CFG_KEY_LYRIC_VIEW = "LYRIC_VIEW";
    /** FFmpeg exeパス */
    public static final String CFG_KEY_FFMPEG_PATH = "FFMPEG_PATH";
    /** FFmpeg出力パス */
    public static final String CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE = "FFMPEG_LEAVE_OUTPUT_FILE";
    /** 内蔵FFmpeg使用 */
    public static final String CFG_KEY_FFMPEG_INSTALLED = "FFMPEG_INSTALLED";
    /** 再生前にシステムセットアップを送信するか */
    public static final String CFG_KEY_SEND_MIDI_SYSTEMSETUP = "SEND_MIDI_SYSTEMSETUP";
    /** YoutubeDL exeパス */
    public static final String CFG_KEY_YOUTUBEDL_PATH = "YOUTUBEDL_PATH";
    /** YoutubeDL コマンド */
    public static final String CFG_KEY_YOUTUBEDL_COMMAND = "YOUTUBEDL_COMMAND";
    /** 内蔵YoutubeDL使用 */
    public static final String CFG_KEY_YOUTUBEDL_INSTALLED = "YOUTUBEDL_INSTALLED";
    /** ランダム再生 */
    public static final String CFG_KEY_RANDOMPLAY = "RANDOMPLAY";
    /** YoutubeDLファイル名指定モード(TITLE or ID) */
    public static final String CFG_KEY_YOUTUBEDL_FILENAME_MODE = "YOUTUBEDL_FILENAME_MODE";
    /** Plugin準拠バージョンの確認 */
    public static final String CFG_KEY_CHECK_PLUGIN_VERSION = "CHECK_PLUGIN_VERSION";

    /**
     * コンフィグ変更
     *
     * @param key
     *            キー名
     * @param value
     *            コンフィグ値
     */
    abstract void setConfigParam(String key, String value);

    /**
     * コンフィグ値取得
     *
     * @param key
     *            キー名
     * @return コンフィグ値
     */
    abstract String getConfigParam(String key);

    /**
     * コンフィグキーセット取得
     *
     * @return コンフィグキーセット
     */
    abstract String[] getKeySet();
}
