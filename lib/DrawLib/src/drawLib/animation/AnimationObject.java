package drawLib.animation;

import java.awt.Graphics;

/**
 * アニメーションの描画タイミングを管理するクラス
 *
 * @author akkut
 *
 */
public class AnimationObject {

    private AnimationInfo info = null;
    private AnimationDrawObject obj = null;

    private int subCount = 0;
    private int count = 0;
    private boolean isAlive = true;

    public AnimationObject(AnimationInfo info, AnimationDrawObject obj) {
        this.info = info;
    }

    public AnimationObject(int x, int y, long totalTime, long cycleTime, AnimationDrawObject obj) {
        int numOfCount = (int) (totalTime / cycleTime);
        setInfo(x, y, numOfCount, cycleTime, obj);
    }

    private void setInfo(int x, int y, int numOfCount, long cycleTime, AnimationDrawObject obj) {
        this.obj = obj;
        info = new AnimationInfo(x, y, numOfCount, cycleTime);
    }

    public void update() {
        if (info.numOfCount <= count) {
            isAlive = false;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void draw(Graphics g) {
        if (isAlive == true) {
            obj.draw(g, info, count);
        }
    }

    public void increment() {
        if (info.cycleTime != -1) {
            subCount++;
            int thrCount = (int) (info.cycleTime / AnimationThread.CyclicTime);
            if (subCount >= thrCount) {
                count++;
                subCount = 0;
            }
        }
        else {
            count++;
        }
    }

}
