package fmp;

import java.awt.event.MouseEvent;

import javafx.scene.media.MediaPlayer;

public abstract class FlagMediaEvent {

    protected boolean waitForTrriger = false;

    public FlagMediaEvent() {
    }

    public boolean isWaitForTrriger() {
        return waitForTrriger;
    }

    public void setWaitForTrriger(boolean waitForTrriger) {
        this.waitForTrriger = waitForTrriger;
    }

    abstract public boolean isExecute(MediaPlayer mp);

    abstract public void execute(MediaPlayer mp);

    abstract public boolean isTrriger(MediaPlayer mp);

    abstract public void trrigerEvent(MediaPlayer mp);

    abstract public void onMouseEvent(MouseEvent e);
}
