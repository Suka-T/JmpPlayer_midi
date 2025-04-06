package jmp.player;

import java.io.File;

import jlib.player.Player;

public class DualPlayerSynchronizer extends Player {

    private Player[] aPlayer;
    
    public DualPlayerSynchronizer(Player primeFile, Player secondPlayer) {
        aPlayer = new Player[2];
        aPlayer[0] = primeFile;
        aPlayer[1] = secondPlayer;
    }

    @Override
    public void play() {
        // 同期再生は一時停止不可 
        setPosition(0);
        
        for (Player player : aPlayer) {
            player.play();
        }
    }

    @Override
    public void stop() {
        for (Player player : aPlayer) {
            player.stop();
        }
    }

    @Override
    public boolean isRunnable() {
        return this.aPlayer[0].isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        for (Player player : aPlayer) {
            player.setPosition(pos);
        }
    }

    @Override
    public long getPosition() {
        return this.aPlayer[0].getPosition();
    }

    @Override
    public long getLength() {
        return this.aPlayer[0].getLength();
    }

    @Override
    public boolean isValid() {
        return this.aPlayer[0].isValid();
    }

    @Override
    public int getPositionSecond() {
        return this.aPlayer[0].getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return this.aPlayer[0].getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        for (Player player : aPlayer) {
            player.setVolume(volume);
        }
    }

    @Override
    public float getVolume() {
        return this.aPlayer[0].getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        return this.aPlayer[0].loadFile(file);
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return this.aPlayer[0].saveFile(file);
    }
    
    public boolean loadSecondFile(File file) throws Exception {
        return this.aPlayer[1].loadFile(file);
    }
    
    @Override
    public void changingPlayer() {
        for (Player player : aPlayer) {
            player.changingPlayer();
        }
    }

}
