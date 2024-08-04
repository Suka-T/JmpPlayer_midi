package jlib.core;

import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;

public interface IWindowManager {

    public static final String WINDOW_NAME_MAIN = "MAIN";
    public static final String WINDOW_NAME_FILE_LIST = "FILE_LIST";
    public static final String WINDOW_NAME_HISTORY = "HISTORY";
    public static final String WINDOW_NAME_MIDI_SETUP = "MIDI_SETUP";
    public static final String WINDOW_NAME_MIDI_MONITOR = "MIDI_MONITOR";
    public static final String WINDOW_NAME_MIDI_SENDER = "MIDI_SENDER";
    public static final String WINDOW_NAME_LANGUAGE = "LANGUAGE";
    public static final String WINDOW_NAME_LICENSE = "LICENSE";
    public static final String WINDOW_NAME_FFMPEG = "FFMPEG";
    public static final String WINDOW_NAME_PLUGIN_MANAGER = "PLUGIN_MANAGER";
    public static final String WINDOW_NAME_YOUTUBEDL = "YOUTUBEDL";
    public static final String WINDOW_NAME_CONSOLE = "CONSOLE";

    /**
     * MainWindow取得
     *
     * @return MainWindow
     */
    abstract IJmpMainWindow getMainWindow();

    /**
     * Windowインスタンス取得
     *
     * @param name
     *            Window識別名
     * @return Windowインスタンス
     */
    abstract IJmpWindow getWindow(String name);

    /**
     * Window識別名の一覧表示
     *
     * @return Window識別名一覧配列
     */
    abstract String[] getWindowNameList();

    /**
     * Window識別名の一覧表示
     *
     * @return Window識別名一覧(","区切りで表示)
     */
    default String getWindowNames() {
        String ret = "";
        for (String name : getWindowNameList()) {
            ret += (name + ",");
        }
        return ret;
    }
}
