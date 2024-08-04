package drawLib.animation;

import java.awt.Graphics;

import drawLib.gui.RepaintEngine;

public class AnimationEngine {

    public enum Threading {
        Single, Multi
    }

    private Threading threadingType = Threading.Single;

    private AnimationThread[] thread = null;

    public AnimationEngine() {
        // シングルスレッド
        thread = new AnimationThread[1];
        threadingType = Threading.Single;
    }

    public AnimationEngine(int numOfThread) {
        // マルチスレッド
        thread = new AnimationThread[numOfThread];
        threadingType = Threading.Multi;
    }

    public void initEngine(RepaintEngine re, boolean isUseWorker) {
        for (int i = 0; i < thread.length; i++) {
            thread[i] = new AnimationThread(re, isUseWorker);
            thread[i].start();
        }
    }

    public void exitEngine() {
        if (threadingType == Threading.Single) {
            if (thread[0] != null) {
                thread[0].exit();
            }
        }
        else {
            for (AnimationThread t : thread) {
                t.exit();
            }
        }
    }

    public void register(AnimationObject obj) {
        if (threadingType == Threading.Single) {
            if (thread[0] != null) {
                thread[0].registerAnimationObject(obj);
            }
        }
        else {
            AnimationThread target = null;
            for (AnimationThread t : thread) {
                if (target == null) {
                    target = t;
                }
                else {
                    if (target.registerSize() > t.registerSize()) {
                        target = t;
                    }
                }
            }

            if (target != null) {
                target.registerAnimationObject(obj);
            }
        }
    }

    public void draw(Graphics g) {
        if (threadingType == Threading.Single) {
            if (thread[0] != null) {
                thread[0].draw(g);
            }
        }
        else {
            for (AnimationThread t : thread) {
                t.draw(g);
            }
        }
    }

    public int getMaxAnimationCount() {
        return thread[0].getMaxAnimationCount();
    }

    public void setMaxAnimationCount(int maxAnimationCount) {
        for (AnimationThread t : thread) {
            t.setMaxAnimationCount(maxAnimationCount);
        }
    }

}
