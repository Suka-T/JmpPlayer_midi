package jmp.player;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import fmp.FlagMediaPlayer;
import fmp.FlagMediaPlayerProrpaty;
import function.Utility;
import gui.FlagMediaAccessor;
import gui.FlagMediaAccessor.PlayerStatus;
import jlib.player.Player;
import jmp.core.JMPCore;

public class MoviePlayer extends Player implements IMoviePlayerModel {

    private static final long TIMEOUT_MILLS = 15 * 1000;

    // FlagMediaPlayerWindow win;
    private FlagMediaAccessor mediaAccessor;

    public MoviePlayer() {
        super();
    }

    @Override
    public void play() {
        if (isValid() == false) {
            return;
        }
        if (mediaAccessor.isVisibleView() == false) {
            if (mediaAccessor.isAudioOnly() == false) {
                mediaAccessor.setVisibleView(true);
            }
        }
        if (isRunnable() == false) {
            mediaAccessor.play(TIMEOUT_MILLS);
        }
    }

    @Override
    public void stop() {
        if (isValid() == false) {
            return;
        }
        if (isRunnable() == true) {
            mediaAccessor.pause(TIMEOUT_MILLS);
        }
    }

    @Override
    public boolean isRunnable() {
        if (isValid() == false) {
            return false;
        }
        if (mediaAccessor.getPlayerStatus() == PlayerStatus.PLAYING) {
            return true;
        }
        return false;
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        mediaAccessor.seek(pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return mediaAccessor.getCurrentMills();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return mediaAccessor.getTotalMills();
    }

    @Override
    public boolean isValid() {
        if (mediaAccessor == null) {
            return false;
        }
        return mediaAccessor.isValid();
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        return mediaAccessor.getCurrentSeconds();
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        return mediaAccessor.getTotalSeconds();
    }

    @Override
    public void setVolume(float volume) {
    }

    @Override
    public float getVolume() {
        return 0;
    }

    private void closeResource() {
        if (mediaAccessor != null) {
            mediaAccessor.closeResource(5000);
        }
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        if (isValid() == true) {
            // 開いていたビューを破棄する
            closeResource();
        }
        // 新しく作成
        FlagMediaPlayerProrpaty prop = new FlagMediaPlayerProrpaty();
        prop.alwaysAudioOnly = false;
        prop.playAfterExit = false;

        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle desktopBounds = env.getMaximumWindowBounds();
        prop.fixScreenSize = new Dimension((int) (desktopBounds.width * 0.8), (int) (desktopBounds.height * 0.8));
        mediaAccessor = FlagMediaPlayer.openSingleWindow(file, prop);
        if (mediaAccessor == null) {
            return false;
        }

        mediaAccessor.setTitle("Movie - " + Utility.getFileNameAndExtension(file));

        // 動画ファイルは画面表示する
        if (mediaAccessor.isAudioOnly() == false) {
            // ビューにマウスリスナー登録
            mediaAccessor.addMouseListener(new MouseListener() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    JMPCore.getWindowManager().getMainWindow().showWindow();
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (isRunnable() == false) {
                            if (getPosition() >= getLength()) {
                                setPosition(0);
                            }
                            play();
                        }
                        else {
                            stop();
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                }
            });
            mediaAccessor.addWindowListener(new WindowListener() {

                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    JMPCore.getTaskManager().requestWindowUpdate();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }
            });
            // 画面中心に表示
            mediaAccessor.setVisibleView(true, true);
        }
        return true;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

    @Override
    public boolean close() {
        closeResource();
        return super.close();
    }

    @Override
    public void changingPlayer() {
        closeResource();
        super.changingPlayer();
    }

    @Override
    public void setVisibleView(boolean visible) {
        if (isValid() == false) {
            return;
        }
        mediaAccessor.setVisibleView(visible);
    }

    @Override
    public boolean isValidView() {
        if (isValid() == false) {
            return false;
        }
        if (mediaAccessor.isAudioOnly() == true) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isVisibleView() {
        if (isValid() == false) {
            return false;
        }
        return mediaAccessor.isVisibleView();
    }
}
