package gui;

import java.awt.event.MouseListener;
import java.awt.event.WindowListener;

import fmp.FlagMediaPlayer;
import javafx.scene.media.MediaPlayer;

public class FlagMediaAccessor {

    public static enum PlayerStatus {
        PLAYING, PAUSED, STOPPED, DISPOSED, HALTED, STALLED, READY, UNKNOWN
    };

    public static MediaPlayer.Status playerStatusToFx(PlayerStatus state) {
        switch (state) {
            case DISPOSED:
                return MediaPlayer.Status.DISPOSED;
            case HALTED:
                return MediaPlayer.Status.HALTED;
            case PAUSED:
                return MediaPlayer.Status.PAUSED;
            case PLAYING:
                return MediaPlayer.Status.PLAYING;
            case READY:
                return MediaPlayer.Status.READY;
            case STALLED:
                return MediaPlayer.Status.STALLED;
            case STOPPED:
                return MediaPlayer.Status.STOPPED;
            case UNKNOWN:
            default:
                return MediaPlayer.Status.UNKNOWN;
        }
    }

    public static PlayerStatus fxToPlayerStatus(MediaPlayer.Status state) {
        switch (state) {
            case DISPOSED:
                return PlayerStatus.DISPOSED;
            case HALTED:
                return PlayerStatus.HALTED;
            case PAUSED:
                return PlayerStatus.PAUSED;
            case PLAYING:
                return PlayerStatus.PLAYING;
            case READY:
                return PlayerStatus.READY;
            case STALLED:
                return PlayerStatus.STALLED;
            case STOPPED:
                return PlayerStatus.STOPPED;
            case UNKNOWN:
            default:
                return PlayerStatus.UNKNOWN;

        }
    }

    private FlagMediaPlayerWindow win;

    public FlagMediaAccessor(FlagMediaPlayerWindow w) {
        win = w;
    }
    
    public void addMouseListener(MouseListener l) {
        win.getMediaPanel().addMouseListener(l);
    }

    public boolean isAudioOnly() {
        return win.getMediaPanel().isAudioOnly();
    }

    public void setVisibleView(boolean b) {
        setVisibleView(b, false);
    }

    public void setVisibleView(boolean b, boolean LocationRelativeTo) {
        if (isAudioOnly() == true) {
            return;
        }
        if (b == true && LocationRelativeTo == true) {
            win.setLocationRelativeTo(null);
        }
        win.setVisible(b);
    }
    
    public void addWindowListener(WindowListener l) {
        win.addWindowListener(l);
    }

    public boolean isVisibleView() {
        return win.isVisible();
    }

    public void setTitle(String title) {
        win.setTitle(title);
    }

    public PlayerStatus getPlayerStatus() {
        return fxToPlayerStatus(win.getMediaPanel().getPlayer().getStatus());
    }

    private void waitForChangeState(PlayerStatus state, long maxSleepMills) {
        final long waitTime = 100;
        long cnt = maxSleepMills / waitTime;
        for (int i = 0; getPlayerStatus() != state; i++) {
            try {
                Thread.sleep(waitTime);
            }
            catch (Exception e) {
            }
            if (i > cnt) {
                break;
            }
        }
    }

    public void play(long timeoutMills) {
        win.getMediaPanel().getPlayer().play();
        waitForChangeState(PlayerStatus.PLAYING, timeoutMills);
    }

    public void pause(long timeoutMills) {
        win.getMediaPanel().getPlayer().pause();
        waitForChangeState(PlayerStatus.PAUSED, timeoutMills);
    }

    public void stop(long timeoutMills) {
        win.getMediaPanel().getPlayer().stop();
        waitForChangeState(PlayerStatus.STOPPED, timeoutMills);
    }

    public void seek(long mills) {
        win.getMediaPanel().getPlayer().seek(javafx.util.Duration.millis(mills));
    }

    public long getCurrentMills() {
        MediaPlayer player = win.getMediaPanel().getPlayer();
        long curTime = (long) player.getCurrentTime().toMillis();
        if (curTime > getTotalMills()) {
            curTime = getTotalMills();
        }
        return curTime;
    }

    public int getCurrentSeconds() {
        return (int) (getCurrentMills() / 1000);
    }

    public long getTotalMills() {
        MediaPlayer player = win.getMediaPanel().getPlayer();
        return (long) player.getTotalDuration().toMillis() - FlagMediaPlayer.TRACK_END_OFFSET;
    }

    public int getTotalSeconds() {
        return (int) (getTotalMills() / 1000);
    }

    public boolean isValid() {
        if (win == null) {
            return false;
        }
        if (win.getMediaPanel() == null) {
            return false;
        }
        return true;
    }

    public void closeResource(long timeoutMills) {
        if (win != null) {
            // 開いていたビューを破棄する
            win.setVisible(false);
            if (getPlayerStatus() != PlayerStatus.STOPPED) {
                stop(timeoutMills);
            }
            win.exitResource();
        }
    }

}
