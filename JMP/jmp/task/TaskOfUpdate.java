package jmp.task;

import jlib.gui.IJmpMainWindow;
import jlib.player.IPlayer;
import jmp.JMPLibrary;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.SoundManager;
import jmp.core.WindowManager;
import jmp.gui.JmpQuickLaunch;
import jmp.plugin.PluginWrapper;
import jmp.task.TaskPacket.PacketType;

/**
 * 更新タスク
 *
 * @author abs
 *
 */
public class TaskOfUpdate extends TaskOfBase {
    private static final int CYCLIC_UPDATE_MSEC = 100;
    private static final int CYCLIC_REPAINT_MSEC = 5000;
    private static final int CYCLIC_REPAINT_MSEC_PLAY = 1000;
    private static final int CYCLIC_REPAINT_BUILTIN_SYNTH_MSEC = 50;
    private int cyclicUpdateCount = 0;
    private int cyclicRepaintCount = 0;
    private int cyclicBuiltinRepaintCount = 0;
    private boolean pastRunnableState = false;
    private boolean requestUpdateFlag = false;

    public TaskOfUpdate() {
        super(50, true);
    }

    @Override
    void begin() {
        cyclicUpdateCount = 0;
        cyclicRepaintCount = 0;
        cyclicBuiltinRepaintCount = 0;
    }

    @Override
    void loop() {
        WindowManager wm = JMPCore.getWindowManager();
        PluginManager pm = JMPCore.getPluginManager();
        SoundManager sm = JMPCore.getSoundManager();
        IJmpMainWindow mainWindow = wm.getMainWindow();

        boolean isUpdate = false;
        boolean isRepaint = false;
        boolean isRepaintMain = false;
        boolean isRepaintBuiltin = false;

        // 再描画カウント
        if (JMPCore.getSoundManager().isPlay() == true) {
            if ((CYCLIC_REPAINT_MSEC_PLAY / getSleepTime()) <= cyclicRepaintCount) {
                isRepaintMain = true;
            }

            /* 【例外処理】シークが終端まで到達してるのに再生が継続している場合、強制的に停止する */
            IPlayer player = sm.getCurrentPlayer();
            if (player.getPosition() >= player.getLength()) {
                player.stop();
                isRepaintMain = true;
            }
        }
        else {
            if (pastRunnableState == true) {
                // 停止時の初回のみ再描画する
                isRepaint = true;
            }
        }
        // 定周期再描画
        if ((CYCLIC_REPAINT_MSEC / getSleepTime()) <= cyclicRepaintCount) {
            isRepaint = true;
        }
        // 更新カウント
        if ((CYCLIC_UPDATE_MSEC / getSleepTime()) <= cyclicUpdateCount) {
            isUpdate = true;
        }
        // 内蔵シンセ再描画
        if ((CYCLIC_REPAINT_BUILTIN_SYNTH_MSEC / getSleepTime()) <= cyclicBuiltinRepaintCount) {
            isRepaintBuiltin = true;
        }

        // 強制再描画
        if (requestUpdateFlag == true) {
            isUpdate = true;
            isRepaint = true;
            isRepaintBuiltin = true;
            requestUpdateFlag = false;
        }

        // 更新
        if (isUpdate == true) {
            mainWindow.update();
            pm.update();
            cyclicUpdateCount = 0;
        }
        if (isRepaint == true) {
            // 再描画
            isRepaintMain = true;
            wm.repaintAll();
            cyclicRepaintCount = 0;
        }
        if (isRepaintMain == true) {
            mainWindow.repaintWindow();
            for (JmpQuickLaunch launch : JmpQuickLaunch.Accessor) {
                launch.repaint();
            }
        }
        // 自作シンセ再描画
        if (isRepaintBuiltin == true) {
            wm.repaintBuiltinSynthFrame();
            cyclicBuiltinRepaintCount = 0;
        }
        cyclicRepaintCount++;
        cyclicUpdateCount++;
        cyclicBuiltinRepaintCount++;

        pastRunnableState = JMPCore.getSoundManager().isPlay();

        // スタンドアロンプラグインが閉じられているか確認
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            PluginWrapper pw = JMPCore.getStandAlonePluginWrapper();
            if (pw != null) {
                if (pw.isOpen() == false) {
                    JMPLibrary.exitApplication();
                }
            }
        }
    }

    @Override
    void end() {
    }

    @Override
    protected void interpret(TaskPacket obj) {
        if (obj.getType() == PacketType.RequestUpdate) {
            requestUpdateFlag = true;
        }
    }
}
