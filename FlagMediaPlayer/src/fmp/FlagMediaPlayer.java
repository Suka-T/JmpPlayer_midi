package fmp;

import java.awt.Dimension;
import java.io.File;

import gui.FMPMainWindow;
import gui.FlagMediaAccessor;
import gui.FlagMediaPlayerWindow;
import gui.MediaPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FlagMediaPlayer {

    // 終了位置に達するとフリーズするため、終了位置をごまかすことで暫定対応
    public static final long TRACK_END_OFFSET = 500;

    private static final long TIMEOUT_MILLS = 20 * 1000;

    private static boolean waitForChangeState(MediaPlayer player, MediaPlayer.Status state, long maxSleepMills) {
        boolean res = true;
        final long waitTime = 500;
        long cnt = maxSleepMills / waitTime;
        for (int i = 0; player.getStatus() != state; i++) {
            try {
                Thread.sleep(waitTime);
            }
            catch (Exception e) {
            }
            if (i > cnt) {
                res = false;
                break;
            }
        }
        return res;
    }

    public static FlagMediaPlayerWindow ActiveWindow = null;
    public static FMPMainWindow MainWindow = null;

    public static void main(String[] args) throws Exception {
        // openMainWindow();
        FlagMediaPlayerProrpaty property = new FlagMediaPlayerProrpaty();
        property.playAfterExit = true;
        openSingleWindow(new File("S:\\sample.mp4"), property);
    }

    public static FlagMediaAccessor openSingleWindow(File file, FlagMediaPlayerProrpaty property) throws Exception {
        MediaPanel panel = new MediaPanel(file, null);

        FlagMediaPlayerWindow win = new FlagMediaPlayerWindow(panel);

        Media media = panel.getMedia();
        MediaPlayer player = panel.getPlayer();

        // 読み込み待ち
        if (waitForChangeState(player, MediaPlayer.Status.READY, TIMEOUT_MILLS) == false) {
            // 失敗
            win.exitResource();
            return null;
        }

        player.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {
                // 終了位置までシークして一時停止状態にする
                MediaPlayer player = win.getMediaPanel().getPlayer();
                long endSeek = (long) player.getTotalDuration().toMillis();
                win.getMediaPanel().getPlayer().seek(javafx.util.Duration.millis(endSeek - TRACK_END_OFFSET));
                player.pause();
            }
        });

        // 動画サイズを取得
        int videoW = media.getWidth();
        int videoH = media.getHeight();
        if (videoW <= 0 && videoH <= 0) {
            panel.setAudioOnly(true);
        }

        // 常に音声のみ
        if (property.alwaysAudioOnly == true) {
            panel.setAudioOnly(true);
        }

        Dimension videoDim = null;
        if (property.fixScreenSize == null) {
            // MoviePanelのサイズを動画に合わせる
            videoDim = new Dimension(videoW, videoH);
        }
        else {
            // MoviePanelのサイズを固定
            videoDim = property.fixScreenSize;
        }
        panel.setPreferredSize(videoDim);
        win.getContentPane().add(panel);

        // Frameのサイズ調整
        win.getContentPane().setPreferredSize(videoDim);
        win.pack();

        panel.threadStart();

        if (property.playAfterExit == true) {
            win.setVisible(true);
            panel.getPlayer().play();

            while (true) {
                if (ActiveWindow == null) {
                    break;
                }
                Thread.sleep(1000);
            }

            win.exitResource();
            win.dispose();

            System.exit(0);
        }

        FlagMediaAccessor acc = new FlagMediaAccessor(win);
        return acc;
    }

    public static void openMainWindow() {
        ActiveWindow = null;

        MainWindow = new FMPMainWindow();
        MainWindow.setVisible(true);
    }

    public static void exit() {
        if (MainWindow != null) {
            MainWindow.setVisible(false);
            MainWindow.dispose();
        }

        System.exit(0);
    }

}
