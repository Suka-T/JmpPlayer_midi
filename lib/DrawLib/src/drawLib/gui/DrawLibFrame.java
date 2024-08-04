package drawLib.gui;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

public class DrawLibFrame extends JFrame {

    protected RepaintEngine engine;

    public DrawLibFrame() {
        // super();
    }

    public DrawLibFrame(GraphicsConfiguration gc) {
        super(gc);
    }

    public void initPane() {
        engine = new RepaintEngine(this);
        engine.initEngine();
    }

    public void exitPane() throws Throwable {
        if (engine != null) {
            engine.exitEngine();
        }
    }

    public int getFPS() {
        return engine.getFPS();
    }

    public void repaintAndFlipScreen() {
        if (engine != null) {
            engine.repaintAndFlipScreen();
        }
    }

    public void update() {
        update(getGraphics());
    }

    @Override
    public void update(Graphics g) {
        if (engine != null) {
            engine.flip(g);
        }
    }

    @Override
    public void repaint() {
        update();
    }

    public boolean isUsingVolatileImage() {
        return engine.isUsingVolatileImage();
    }

    public void setUsingVolatileImage(boolean isUsingVolatileImage) {
        engine.setUsingVolatileImage(isUsingVolatileImage);
    }

    public void setFixedFPS(int fps) {
        engine.setFixedFPS(fps);
    }

    public int getFixedFPS() {
        return engine.getFixedFPS();
    }

    @Override
    protected void finalize() throws Throwable {
        exitPane();
    }
}
