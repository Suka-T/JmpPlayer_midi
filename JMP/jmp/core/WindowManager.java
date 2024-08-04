package jmp.core;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import jlib.core.IWindowManager;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jmp.gui.BuiltinSynthSetupDialog;
import jmp.gui.FFmpegConvertDialog;
import jmp.gui.FilePickupDialog;
import jmp.gui.HistoryDialog;
import jmp.gui.LicenseReaderDialog;
import jmp.gui.MidiDataTransportDialog;
import jmp.gui.MidiFileListDialog;
import jmp.gui.MidiMessageMonitor;
import jmp.gui.PluginManagerDialog;
import jmp.gui.SelectLanguageDialog;
import jmp.gui.SelectSynthsizerDialog;
import jmp.gui.WindowDatabase;
import jmp.gui.YoutubeConvertDialog;
import jmp.gui.ui.IJMPComponentUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.plugin.PluginWrapper;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;
import jmplayer.JMPPlayerWindow;

public class WindowManager extends AbstractManager implements IWindowManager {

    public static final Rectangle DEFAULT_PLAYER_WINDOW_SIZE = new Rectangle(20, 20, 480, 210);

    /* ライブラリ非公開Window */
    public static final String WINDOW_NAME_FILE_PICKUP = "DIRECTORY_VIEWER";

    public static final String[] WINDOW_NAMELIST = { //
            WINDOW_NAME_MAIN, //
            WINDOW_NAME_FILE_LIST, //
            WINDOW_NAME_HISTORY, //
            WINDOW_NAME_MIDI_SETUP, //
            WINDOW_NAME_MIDI_MONITOR, //
            WINDOW_NAME_MIDI_SENDER, //
            WINDOW_NAME_LANGUAGE, //
            WINDOW_NAME_LICENSE, //
            WINDOW_NAME_FFMPEG, //
            WINDOW_NAME_PLUGIN_MANAGER, //
            WINDOW_NAME_YOUTUBEDL, //
            WINDOW_NAME_FILE_PICKUP, //
            WINDOW_NAME_CONSOLE, //
    };

    private WindowDatabase database = null;

    /* JMsynth用のフレーム(WindowDatabaseに含まない) */
    private BuiltinSynthSetupDialog builtinSynthFrame = null;

    /* デフォルトのフォントファミリ */
    public static final String DEFAULT_FONT = Font.DIALOG;

    WindowManager() {
        super("window");

        database = new WindowDatabase();
    }

    @Override
    protected boolean initFunc() {

        // Windowインスタンス作成
        register(WINDOW_NAME_LICENSE, new LicenseReaderDialog());
        register(WINDOW_NAME_MIDI_MONITOR, new MidiMessageMonitor());
        register(WINDOW_NAME_MIDI_SENDER, new MidiDataTransportDialog());
        register(WINDOW_NAME_LANGUAGE, new SelectLanguageDialog());
        register(WINDOW_NAME_HISTORY, new HistoryDialog());
        register(WINDOW_NAME_FILE_LIST, new MidiFileListDialog());
        register(WINDOW_NAME_PLUGIN_MANAGER, new PluginManagerDialog());
        register(WINDOW_NAME_FFMPEG, new FFmpegConvertDialog());
        register(WINDOW_NAME_MIDI_SETUP, new SelectSynthsizerDialog(true, true));
        register(WINDOW_NAME_YOUTUBEDL, new YoutubeConvertDialog());
        register(WINDOW_NAME_FILE_PICKUP, new FilePickupDialog());
        register(WINDOW_NAME_CONSOLE, SystemManager.console);

        // メインウィンドウ登録
        registerMainWindow(new JMPPlayerWindow());

        return super.initFunc();
    }

    @Override
    protected boolean endFunc() {
        if (super.endFunc() == false) {
            return false;
        }
        closeBuiltinSynthFrame();
        setVisibleAll(false);
        return true;
    }

    public void startupWindow() {
        // メインウィンドウ初期化
        IJmpMainWindow win = getMainWindow();
        win.initializeSetting();
        win.hideWindow();

        // 言語更新
        updateLanguage();
    }

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        SystemManager system = JMPCore.getSystemManager();
        if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_PLAYER_BACK_COLOR)) == true) {
            updateBackColor();
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_DEBUGMODE)) == true) {
            updateDebugMenu();
            JMPCore.getTaskManager().requestWindowUpdate();
        }
        super.notifyUpdateCommonRegister(key);
    }

    public void processingBeforePlay() {
        // 再生前に行う処理
        IJmpWindow win;

        win = getWindow(WINDOW_NAME_LICENSE);
        if (win.isWindowVisible() == true) {
            win.hideWindow();
        }
        win = getWindow(WINDOW_NAME_LANGUAGE);
        if (win.isWindowVisible() == true) {
            win.hideWindow();
        }

        for (IJmpWindow w : database.getAccessor()) {
            if (w != null) {
                w.processingBeforePlay();
            }
        }
    }

    public void processingAfterPlay() {
        // 再生後に行う処理

        // 歌詞表示初期化
        getMainWindow().setLyric("");

        for (IJmpWindow w : database.getAccessor()) {
            if (w != null) {
                w.processingAfterPlay();
            }
        }
    }

    public void processingBeforeStop() {
        // 停止前に行う処理
        for (IJmpWindow w : database.getAccessor()) {
            if (w != null) {
                w.processingBeforeStop();
            }
        }
    }

    public void processingAfterStop() {
        // 停止後に行う処理

        // 歌詞表示初期化
        getMainWindow().setLyric("");

        for (IJmpWindow w : database.getAccessor()) {
            if (w != null) {
                w.processingAfterStop();
            }
        }
    }

    public boolean register(IJmpWindow window) {
        if (database == null) {
            return false;
        }
        return database.addWindow(window);
    }

    public boolean register(String name, IJmpWindow window) {
        if (database == null) {
            return false;
        }
        return database.setWindow(name, window);
    }

    public void registerMainWindow(IJmpMainWindow win) {
        if (getMainWindow() == null) {
            database.setMainWindow(win);
            register(WINDOW_NAME_MAIN, win);
        }
    }

    @Override
    public IJmpMainWindow getMainWindow() {
        return database.getMainWindow();
    }

    @Override
    public IJmpWindow getWindow(String name) {
        return database.getWindow(name);
    }

    @Override
    public String[] getWindowNameList() {
        return WINDOW_NAMELIST;
    }

    public void setVisibleAll(boolean visible) {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                if (visible == true) {
                    win.showWindow();
                }
                else {
                    win.hideWindow();
                }
            }
        }
    }

    public void updatePluginMenuItems() {
        PluginManager pm = JMPCore.getPluginManager();

        database.clearPluginMenuItem();
        for (String name : pm.getPluginsNameSet()) {
            PluginWrapper pw = pm.getPluginWrapper(name);
            database.addPluginMenuItem(name, pw);
        }

        ((JMPPlayerWindow) getMainWindow()).updatePluginMenu();
    }

    public List<JMenuItem> getPluginMenuItems() {
        return database.getPluginMenuItems();
    }

    public void updateConfig(String key) {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.updateConfig(key);
            }
        }
    }

    public void updateLanguage() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.updateLanguage();
            }
        }
    }

    public void initializeLayout() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                win.initializeLayout();
            }
        }

        // プラグインもすべて閉じる
        PluginManager pm = JMPCore.getPluginManager();
        for (String pname : pm.getPluginsNameSet()) {
            PluginWrapper plg = pm.getPluginWrapper(pname);
            if (plg != null) {
                plg.close();
            }
        }
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);
        updateConfig(key);

        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_LANGUAGE) == true) {
            // 言語更新
            updateLanguage();
        }

        // 再描画を実行させる
        JMPCore.getTaskManager().requestWindowUpdate();
    }

    public void showInformationMessageDialog(String message) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageDialog(String message, int option) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), option);
    }

    public void showMessageDialog(String message, String title, int option) {
        JMPCore.getTaskManager().queuing(new ICallbackFunction() {
            @Override
            public void callback() {
                IJmpMainWindow win = getMainWindow();
                Component parent = null;
                if (win instanceof Component) {
                    parent = (Component) win;
                }
                else {
                    parent = null;
                }
                JOptionPane.showMessageDialog(parent, message, title, option);
            }
        });
    }

    public void showFilePickupDialog(File dir) {
        showFilePickupDialog(dir, null);
    }

    public void showFilePickupDialog(File dir, File focus) {
        FilePickupDialog dlg = (FilePickupDialog) JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FILE_PICKUP);
        dlg.setDirectory(dir, focus);
        dlg.showWindow();
    }

    // 新しいJMsynthインスタンスを登録する
    public void registerBuiltinSynthFrame(BuiltinSynthSetupDialog wvf) {

        closeBuiltinSynthFrame();

        if (this.builtinSynthFrame != wvf) {
            disposeBuiltinSynthFrame();
        }
        this.builtinSynthFrame = wvf;
    }

    public void disposeBuiltinSynthFrame() {
        if (this.builtinSynthFrame != null) {
            this.builtinSynthFrame.dispose();
        }
    }

    public boolean isValidBuiltinSynthFrame() {
        if (JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT).equals(SystemManager.JMSYNTH_LIB_NAME) == false) {
            return false;
        }
        if (builtinSynthFrame == null) {
            return false;
        }
        return true;
    }

    public boolean isVisibleBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return false;
        }
        return builtinSynthFrame.isVisible();
    }

    public void openBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.setVisible(true);
    }

    public void closeBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.setVisible(false);
    }

    public void repaintBuiltinSynthFrame() {
        if (isValidBuiltinSynthFrame() == false) {
            return;
        }
        builtinSynthFrame.repaintWavePane();
    }

    public void repaint(String name) {
        IJmpWindow win = getWindow(name);
        win.repaintWindow();
    }

    public void repaintAll() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                if (win.isWindowVisible() == true) {
                    win.repaintWindow();
                }
            }
        }
    }

    public void updateBackColor() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                if (win instanceof IJMPComponentUI) {
                    IJMPComponentUI ui = (IJMPComponentUI) win;
                    ui.updateBackColor();
                }
            }
        }
    }

    public void updateDebugMenu() {
        for (IJmpWindow win : database.getAccessor()) {
            if (win != null) {
                if (win instanceof IJMPComponentUI) {
                    IJMPComponentUI ui = (IJMPComponentUI) win;
                    ui.updateDebugMenu();
                }
            }
        }
    }

    private Font getFontImpl(Font oldFont, int lang) {
        if (oldFont == null) {
            return null;
        }
        LanguageManager lm = JMPCore.getLanguageManager();
        return new Font(lm.getFontName(lang), oldFont.getStyle(), oldFont.getSize());
    }

    private void changeFontImpl(Object c, String str, int lang) {
        if (str != null) {
            if (c instanceof AbstractButton) {
                ((AbstractButton) c).setText(str);
            }
            else if (c instanceof JLabel) {
                ((JLabel) c).setText(str);
            }
        }

        if (c instanceof JComponent) {
            JComponent cmp = ((JComponent) c);
            Font newFont = getFontImpl(cmp.getFont(), lang);
            cmp.setFont(newFont);
        }
    }

    public Font getFont(Font oldFont, int lang) {
        return getFontImpl(oldFont, lang);
    }

    public Font getCurrentFont(Font oldFont) {
        return getFontImpl(oldFont, JMPCore.getDataManager().getLanguage());
    }

    public void changeFont(Object c) {
        changeFontImpl(c, null, JMPCore.getDataManager().getLanguage());
    }

    public void changeFont(Object c, int lang) {
        changeFontImpl(c, null, lang);
    }

    public void changeFont(Object c, LangID id) {
        LanguageManager lm = JMPCore.getLanguageManager();
        DataManager dm = JMPCore.getDataManager();
        changeFontImpl(c, lm.getLanguageStr(id), dm.getLanguage());
    }

    public void changeFont(Object c, LangID id, int lang) {
        changeFontImpl(c, JMPCore.getLanguageManager().getLanguageStr(id, lang), lang);
    }

    public void changeFont(Object c, String str) {
        changeFontImpl(c, str, JMPCore.getDataManager().getLanguage());
    }

    public void changeFont(Object c, String str, int lang) {
        changeFontImpl(c, str, lang);
    }

}
