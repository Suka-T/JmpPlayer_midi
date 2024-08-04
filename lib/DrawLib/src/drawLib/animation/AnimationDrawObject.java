package drawLib.animation;

import java.awt.Graphics;

/**
 * アニメーション描画クラス
 *
 * @author akkut
 *
 */
public abstract class AnimationDrawObject {

    public AnimationDrawObject() {
    }

    /**
     * アニメーション描画
     *
     * @param g
     * @param info
     *            アニメーション情報
     * @param count
     *            分解能カウント
     */
    public abstract void draw(Graphics g, AnimationInfo info, int count);

}
