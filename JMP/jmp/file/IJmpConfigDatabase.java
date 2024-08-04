package jmp.file;

import function.Platform;
import function.Platform.KindOfPlatform;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.util.JmpUtil;

public interface IJmpConfigDatabase {

    public static final String IJ_TRUE = "ON";
    public static final String IJ_FALSE = "OFF";

    public static enum IJ_YoutubeDlFileNameConfig {
        IJ_Title, IJ_Id,
    }

    //
    // util
    //
    default void setConfigParamToInt(String key, int val) {
        setConfigParam(key, String.valueOf(val));
    }

    default int getConfigParamToInt(String key, int def) {
        return JmpUtil.toInt(getConfigParam(key), def);
    }

    default void setConfigParamToBoolean(String key, boolean val) {
        setConfigParam(key, val ? IJ_TRUE : IJ_FALSE);
    }

    default boolean getConfigParamToBoolean(String key, boolean def) {
        return JmpUtil.toBoolean(getConfigParam(key), def);
    }

    default void setConfigParamToFloat(String key, float val) {
        setConfigParam(key, String.valueOf(val));
    }

    default float getConfigParamToFloat(String key, float def) {
        return JmpUtil.toFloat(getConfigParam(key), def);
    }

    default void setConfigParamToLanguage(String key, int val) {
        LanguageManager lm = JMPCore.getLanguageManager();
        setConfigParam(key, lm.getLanguageCode(val));
    }

    default int getConfigParamToLanguage(String key, int def) {
        LanguageManager lm = JMPCore.getLanguageManager();
        String code = getConfigParam(key);
        return lm.getLanguageCodeIndex(code);
    }

    //
    // abstract
    //
    abstract void setConfigParam(String key, String value);

    abstract String getConfigParam(String key);

    //
    // getter setter
    //
    default boolean isAutoPlay() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_AUTOPLAY, false);
    }

    default void setAutoPlay(boolean isAutoPlay) {
        setConfigParamToBoolean(DataManager.CFG_KEY_AUTOPLAY, isAutoPlay);
    }

    default boolean isLoopPlay() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_LOOPPLAY, false);
    }

    default void setLoopPlay(boolean isLoopPlay) {
        setConfigParamToBoolean(DataManager.CFG_KEY_LOOPPLAY, isLoopPlay);
    }

    default boolean isShowStartupDeviceSetup() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, false);
    }

    default void setShowStartupDeviceSetup(boolean isShow) {
        setConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, isShow ? "TRUE" : "FALSE");
    }

    default int getLanguage() {
        return getConfigParamToLanguage(DataManager.CFG_KEY_LANGUAGE, 0);
    }

    default void setLanguage(int language) {
        setConfigParamToLanguage(DataManager.CFG_KEY_LANGUAGE, language);
    }

    default String getPlayListPath() {
        return getConfigParam(DataManager.CFG_KEY_PLAYLIST);
    }

    default void setPlayListPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_PLAYLIST, filePath);
    }

    default String getLoadedFile() {
        return getConfigParam(DataManager.CFG_KEY_LOADED_FILE);
    }

    default void setLoadedFile(String filePath) {
        setConfigParam(DataManager.CFG_KEY_LOADED_FILE, filePath);
    }

    default boolean isLyricView() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_LYRIC_VIEW, false);
    }

    default void setLyricView(boolean isLyricView) {
        setConfigParamToBoolean(DataManager.CFG_KEY_LYRIC_VIEW, isLyricView);
    }

    default String getFFmpegPath() {
        return getConfigParam(DataManager.CFG_KEY_FFMPEG_PATH);
    }

    default void setFFmpegPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_PATH, filePath);
    }

    default boolean isFFmpegLeaveOutputFile() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, false);
    }

    default void setFFmpegLeaveOutputFile(boolean isLeave) {
        setConfigParamToBoolean(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, isLeave);
    }

    default boolean isFFmpegInstalled() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return true;
        }
        return getConfigParamToBoolean(DataManager.CFG_KEY_FFMPEG_INSTALLED, false);
    }

    default void setFFmpegInstalled(boolean isEnableEnvironmentVariable) {
        setConfigParamToBoolean(DataManager.CFG_KEY_FFMPEG_INSTALLED, isEnableEnvironmentVariable);
    }

    default boolean isSendMidiSystemSetup() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_SEND_MIDI_SYSTEMSETUP, false);
    }

    default void setSendMidiSystemSetup(boolean isSendMidiSystemSetup) {
        setConfigParamToBoolean(DataManager.CFG_KEY_SEND_MIDI_SYSTEMSETUP, isSendMidiSystemSetup);
    }
    
    default String getYoutubeDlCommand() {
        return getConfigParam(DataManager.CFG_KEY_YOUTUBEDL_COMMAND);
    }

    default void setYoutubeDlCommand(String cmd) {
        setConfigParam(DataManager.CFG_KEY_YOUTUBEDL_COMMAND, cmd);
    }

    default String getYoutubeDlPath() {
        return getConfigParam(DataManager.CFG_KEY_YOUTUBEDL_PATH);
    }

    default void setYoutubeDlPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_YOUTUBEDL_PATH, filePath);
    }

    default boolean isYoutubeDlInstalled() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return true;
        }
        return getConfigParamToBoolean(DataManager.CFG_KEY_YOUTUBEDL_INSTALLED, false);
    }

    default void setYoutubeDlInstalled(boolean isEnableEnvironmentVariable) {
        setConfigParamToBoolean(DataManager.CFG_KEY_YOUTUBEDL_INSTALLED, isEnableEnvironmentVariable);
    }

    default boolean isRandomPlay() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_RANDOMPLAY, false);
    }

    default void setRandomPlay(boolean isRandomPlay) {
        setConfigParamToBoolean(DataManager.CFG_KEY_RANDOMPLAY, isRandomPlay);
    }

    default IJ_YoutubeDlFileNameConfig getYoutubeDlFileNameMode() {
        String s = getConfigParam(DataManager.CFG_KEY_YOUTUBEDL_FILENAME_MODE);
        if (s.equalsIgnoreCase("TITLE") == true) {
            return IJ_YoutubeDlFileNameConfig.IJ_Title;
        }
        else if (s.equalsIgnoreCase("ID") == true) {
            return IJ_YoutubeDlFileNameConfig.IJ_Id;
        }
        return IJ_YoutubeDlFileNameConfig.IJ_Id;
    }

    default void setYoutubeDlFileNameMode(IJ_YoutubeDlFileNameConfig youtubeDlFileNameMode) {
        String s = "ID";
        switch (youtubeDlFileNameMode) {
            case IJ_Title:
                s = "TITLE";
                break;
            case IJ_Id:
            default:
                s = "ID";
                break;
        }
        setConfigParam(DataManager.CFG_KEY_YOUTUBEDL_FILENAME_MODE, s);
    }
    
    default boolean isCheckPluginVersion() {
        return getConfigParamToBoolean(DataManager.CFG_KEY_CHECK_PLUGIN_VERSION, false);
    }

    default void setCheckPluginVersion(boolean isChecked) {
        setConfigParamToBoolean(DataManager.CFG_KEY_CHECK_PLUGIN_VERSION, isChecked);
    }
}
