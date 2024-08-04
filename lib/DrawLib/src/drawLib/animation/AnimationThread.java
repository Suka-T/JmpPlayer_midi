package drawLib.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import drawLib.gui.RepaintEngine;

public class AnimationThread extends Thread {
    /** 定周期の間隔（ms） */
    public static long CyclicTime = 20; // mills

    private int maxAnimationCount = 100;

    public List<AnimationObject> aObject;

    private RepaintEngine repaintEngine = null;
    private ImageWorker worker = null;

    private boolean isRunnable = true;

    public AnimationThread(RepaintEngine re) {
        repaintEngine = re;
        _init(false);
    }

    public AnimationThread(RepaintEngine re, boolean isUseWorker) {
        repaintEngine = re;
        _init(isUseWorker);
    }

    private void _init(boolean isUseWorker) {
        if (isUseWorker == true) {
            worker = new ImageWorker();
        }
        else {
            worker = null;
        }
        aObject = Collections.synchronizedList(new ArrayList<AnimationObject>());
        isRunnable = true;
    }

    @Override
    public void run() {
        if (worker != null) {
            worker.start();
        }

        while (isRunnable == true) {
            try {
                synchronized (aObject) {
                    Iterator<AnimationObject> i = aObject.iterator(); // Must be
                                                                      // in
                                                                      // synchronized
                                                                      // block
                    while (i.hasNext()) {
                        AnimationObject obj = i.next();
                        obj.update();
                        if (obj.isAlive() == false) {
                            i.remove();
                        }
                    }
                }
                Thread.sleep(CyclicTime);
            }
            catch (Exception e) {
            }
            finally {
                increment();
            }
        }
    }

    public void registerAnimationObject(AnimationObject obj) {
        if (aObject.size() <= getMaxAnimationCount() || getMaxAnimationCount() == -1) {
            aObject.add(obj);
        }
    }

    public int registerSize() {
        return aObject.size();
    }

    private void increment() {
        synchronized (aObject) {
            Iterator<AnimationObject> i = aObject.iterator(); // Must be in
                                                              // synchronized
                                                              // block
            while (i.hasNext()) {
                AnimationObject obj = i.next();
                obj.increment();
            }
        }
    }

    public void draw(Graphics g) {

        if (worker == null) {
            ArrayList<AnimationObject> list = null;
            // synchronized (aObject) {
            list = new ArrayList<AnimationObject>(aObject);
            // }

            for (AnimationObject o : list) {
                o.draw(g);
            }
        }
        else {
            g.drawImage(offScreenImage, 0, 0, null);
        }
    }

    private void drawOffscreen(Graphics g) {
        ArrayList<AnimationObject> list = null;
        synchronized (aObject) {
            list = new ArrayList<AnimationObject>(aObject);
        }

        for (AnimationObject o : list) {
            o.draw(g);
        }
    }

    public void exit() {
        if (worker != null) {
            worker.exit();
        }
        isRunnable = false;
    }

    public int getMaxAnimationCount() {
        return maxAnimationCount;
    }

    public void setMaxAnimationCount(int maxAnimationCount) {
        this.maxAnimationCount = maxAnimationCount;
    }

    private BufferedImage offScreenImage;

    private class ImageWorker extends Thread {
        private int fps = 60;
        private long fixSleep = (1000 << 16) / fps;
        private long errorTime = 0;
        private long sleepTime = 0;
        private long pastTime;
        private long newTime = System.currentTimeMillis() << 16;

        private Graphics2D offScreenGraphic;
        private boolean isRunnable = true;

        public ImageWorker() {
            isRunnable = true;
        }

        @Override
        public void run() {
            BufferedImage bi = new BufferedImage(repaintEngine.getImageWidth(), repaintEngine.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bg = bi.createGraphics();
            while (isRunnable) {
                try {

                    // 再描画前のタイムを保持
                    fixSleep = (1000 << 16) / repaintEngine.getFixedFPS();
                    pastTime = System.currentTimeMillis() << 16;

                    if (offScreenImage == null) {
                        offScreenImage = new BufferedImage(repaintEngine.getImageWidth(), repaintEngine.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
                        offScreenGraphic.drawImage(bi, 0, 0, null);
                        // System.out.println("a");
                        continue;
                    }

                    if (repaintEngine.getImageWidth() != offScreenImage.getWidth() || repaintEngine.getImageHeight() != offScreenImage.getHeight()) {
                        bi = new BufferedImage(repaintEngine.getImageWidth(), repaintEngine.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
                        offScreenImage = null;
                        // System.out.println("b");
                        continue;
                    }

                    // オフスクリーン
                    bg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                    Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, repaintEngine.getImageWidth(), repaintEngine.getImageWidth());
                    bg.fill(rect);
                    bg.setPaintMode();
                    drawOffscreen(bg);

                    offScreenGraphic = offScreenImage.createGraphics();
                    offScreenGraphic.setColor(Color.BLACK);
                    offScreenGraphic.fill(rect);
                    offScreenGraphic.drawImage(bi, 0, 0, null);

                    // Thread.sleep(1);
                    newTime = System.currentTimeMillis() << 16;
                    sleepTime = fixSleep - (newTime - pastTime) - errorTime;
                    sleepTime = sleepTime < 0x02 ? 0x02 : sleepTime;
                    pastTime = newTime;

                    // fps固定のためのスリープ処理
                    ImageWorker.sleep(sleepTime >> 16);
                    newTime = System.currentTimeMillis() << 16;
                    errorTime = newTime - pastTime - sleepTime;
                }
                catch (Exception e) {
                }
            }
        }

        public void exit() {
            isRunnable = false;
        }
    }
}
