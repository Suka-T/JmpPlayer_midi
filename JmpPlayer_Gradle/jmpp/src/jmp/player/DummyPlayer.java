package jmp.player;

import java.io.File;

import jlib.player.Player;

public class DummyPlayer extends Player implements IMoviePlayerModel {

    public DummyPlayer() {
    }

    @Override
    public void play() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunnable() {
        return false;
    }

    @Override
    public void setPosition(long pos) {
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int getPositionSecond() {
        return 0;
    }

    @Override
    public int getLengthSecond() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        return false;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

    @Override
    public void setVisibleView(boolean visible) {
    }

    @Override
    public boolean isValidView() {
        return false;
    }

    @Override
    public boolean isVisibleView() {
        return false;
    }

}
