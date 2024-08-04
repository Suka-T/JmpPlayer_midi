package drawLib.gui;

import java.awt.Graphics;

import javax.swing.JPanel;

public class DrawLibPanel extends JPanel {

    private RepaintEngine engine;

    public DrawLibPanel() {
        // super();
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
            engine.updateEngine(g);
        }
    }

    @Override
    public void repaint() {
        if (engine != null) {
            engine.repaint();
        }
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
