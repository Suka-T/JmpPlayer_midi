package jmp.player;

import java.io.File;

import jlib.player.Player;

/**
 * MediaPlayerのリソースを使いまわし
 * 
 * @author akkut
 *
 */
public class WavPlayerMin extends WavPlayer {

    private Player mediaPlayer;

    public WavPlayerMin(Player mediaPlayer) {
        super();
        this.mediaPlayer = mediaPlayer;
    }

    private Player getMediaPlayer() {
        return this.mediaPlayer;
    }

    @Override
    public void play() {
        getMediaPlayer().play();
    }

    @Override
    public void stop() {
        getMediaPlayer().stop();
    }

    @Override
    public boolean isRunnable() {
        return getMediaPlayer().isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        getMediaPlayer().setPosition(pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return getMediaPlayer().getPosition();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return getMediaPlayer().getLength();
    }

    @Override
    public boolean isValid() {
        return getMediaPlayer().isValid();
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        return getMediaPlayer().getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        return getMediaPlayer().getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        getMediaPlayer().setVolume(volume);
    }

    @Override
    public float getVolume() {
        return getMediaPlayer().getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        return getMediaPlayer().loadFile(file);
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return getMediaPlayer().saveFile(file);
    }

}
