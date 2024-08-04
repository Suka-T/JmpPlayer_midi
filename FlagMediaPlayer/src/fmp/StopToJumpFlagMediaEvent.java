package fmp;

import java.awt.event.MouseEvent;

import com.sun.javafx.geom.Rectangle;

import javafx.scene.media.MediaPlayer;

public class StopToJumpFlagMediaEvent extends FlagMediaEvent {

    protected boolean isExecute = false;
    protected boolean isTrriger = false;
    protected double sTick = -1;
    protected double eTick = -1;
    protected double jTick = -1;
    protected Rectangle rect;

    public StopToJumpFlagMediaEvent(double startTick, double endTick, double jumpTick, Rectangle rect) {
        super();
        sTick = startTick;
        eTick = endTick;
        jTick = jumpTick;
        this.rect = rect;
    }

    @Override
    public boolean isExecute(MediaPlayer mp) {
        if (isExecute == true) {
            return false;
        }

        if (sTick < mp.getCurrentTime().toSeconds() && mp.getCurrentTime().toSeconds() <= eTick) {
            return true;
        }
        return false;
    }

    @Override
    public void execute(MediaPlayer mp) {
        isExecute = true;
        setWaitForTrriger(true);
        mp.stop();
    }

    @Override
    public boolean isTrriger(MediaPlayer mp) {
        if (isWaitForTrriger() == false) {
            return false;
        }
        return isTrriger;
    }

    @Override
    public void trrigerEvent(MediaPlayer mp) {
        mp.seek(javafx.util.Duration.seconds(jTick));
        setWaitForTrriger(false);
        isExecute = false;
    }

    @Override
    public void onMouseEvent(MouseEvent e) {
        if (rect.x <= e.getX() && e.getX() <= (rect.x + rect.width - 1) && rect.y <= e.getY() && e.getY() <= (rect.y + rect.height - 1)) {
            isTrriger = true;
        }
    }

}
