package drawLib.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;

public class RepaintEngine {

    private Component src;
    private boolean flipScreenFlag = false;

    private RepaintThread repaintThread = null;
    private boolean isUsingVolatileImage = false;
    private FrameRate frameRate = null;
    private Image offScreenImage = null;
    private Graphics offScreenGraphic;
    private int fixedFPS = 30;

    public RepaintEngine(Component src) {
        this.src = src;
        frameRate = new FrameRate();
        frameRate.setUpdateCycleTime(100);
    }

    public void initEngine() {
        repaintThread = new RepaintThread();
        repaintThread.start();
    }

    public int getFPS() {
        return (int) frameRate.getFrameRate();
    }

    public Image getOffScreenImage() {
        if (isUsingVolatileImage() == true) {
            return src.createVolatileImage(src.getWidth(), src.getHeight());
        }
        else {
            return src.createImage(src.getWidth(), src.getHeight());
        }
    }

    public int getImageWidth() {
        return src.getWidth();
    }

    public int getImageHeight() {
        return src.getHeight();
    }

    public void repaintAndFlipScreen() {
        flipScreenFlag = true;

        // フリップ
        repaint();
    }

    public void updateEngine() {
        if (src != null) {
            updateEngine(src.getGraphics());
        }
    }

    public void paintFrame(Graphics g) {

    }

    public void flip(Graphics g) {
        // オフスクリーンをフリップ
        g.drawImage(offScreenImage, 0, 0, null);
    }

    public void updateEngine(Graphics g) {

        if (flipScreenFlag == true || offScreenImage == null) {
            if (g == null) {
                return;
            }
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, src.getWidth(), src.getHeight());

            // オフスクリーンを再描画
            offScreenImage = getOffScreenImage();
            if (offScreenImage == null) {
                return;
            }

            offScreenGraphic = offScreenImage.getGraphics();
            if (offScreenGraphic == null) {
                // Graphicsが取得できない場合は、再描画しない
                return;
            }
            flipScreenFlag = false;
        }

        offScreenGraphic.setColor(src.getBackground());
        offScreenGraphic.fillRect(0, 0, src.getWidth(), src.getHeight());

        // オフスクリーンを描画
        src.paint(offScreenGraphic);
        if (src instanceof Container) {
            ((Container) src).paintComponents(offScreenGraphic);
        }

        // レートの更新
        frameRate.frameCount();
    }

    public void repaint() {
        updateEngine();
    }

    public boolean isUsingVolatileImage() {
        return isUsingVolatileImage;
    }

    public void setUsingVolatileImage(boolean isUsingVolatileImage) {
        this.isUsingVolatileImage = isUsingVolatileImage;
    }

    public void setFixedFPS(int fps) {
        fixedFPS = fps;
    }

    public int getFixedFPS() {
        return fixedFPS;
    }

    public void exitEngine() throws Throwable {
        if (repaintThread != null) {
            repaintThread.exit();
        }
    }

    /**
     * 描画タスク
     *
     */
    private class RepaintThread extends Thread {
        private boolean isRunnable = true;

        private int fps = 60;
        private long fixSleep = (1000 << 16) / fps;
        private long errorTime = 0;
        private long sleepTime = 0;
        private long pastTime;
        private long newTime = System.currentTimeMillis() << 16;

        public RepaintThread() {
            isRunnable = true;
        }

        @Override
        public void run() {
            while (isRunnable) {
                try {
                    if (src.isVisible() == false) {
                        RepaintThread.sleep(200);
                        continue;
                    }

                    updateFramerate();

                    // 再描画前のタイムを保持
                    pastTime = System.currentTimeMillis() << 16;

                    // 再描画
                    updateEngine();
                    src.repaint();

                    // スリープタイムを計算
                    newTime = System.currentTimeMillis() << 16;
                    sleepTime = fixSleep - (newTime - pastTime) - errorTime;
                    sleepTime = sleepTime < 0x02 ? 0x02 : sleepTime;
                    pastTime = newTime;

                    // fps固定のためのスリープ処理
                    RepaintThread.sleep(sleepTime >> 16);
                    newTime = System.currentTimeMillis() << 16;
                    errorTime = newTime - pastTime - sleepTime;
                }
                catch (Exception e) {
                    // System.out.println("repaint task error");
                    // System.out.println(Error.getMsg(e));
                }
            }
        }

        private void updateFramerate() {
            fps = getFixedFPS();
            fixSleep = (1000 << 16) / fps;
        }

        public void exit() {
            isRunnable = false;
        }
    }
}
