package drawLib;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class DrawObject implements DrawObjectExtender, VisibleFunc {
    protected Graphics tmpg = null;
    private short x, y, width, height; // 省メモリのためShortを使用
    private boolean visible = true;

    public DrawObject() {
    }

    /**
     * 初期化
     */
    protected void initialize() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    /**
     * 描画処理
     *
     * @param x
     *            X座標
     * @param y
     *            Y座標
     * @param g
     *            グラフィッククラス
     */
    public void draw(int x, int y, Graphics g) {
        setLocation(x, y);
        draw(g);
    }

    @Override
    public void draw(Graphics g) {
        tmpg = g;
    }

    /**
     * <b> 【 !!非推奨!! 】 </b>再描画
     */
    public void repaint() {
        if (tmpg != null) {
            draw(tmpg);
        }
    }

    public int getX() {
        return (int) getOrgX();
    }

    public short getOrgX() {
        return x;
    }

    public void setX(int x) {
        setX((short) x);
    }

    public void setX(short x) {
        this.x = x;
    }

    public int getY() {
        return (int) getOrgY();
    }

    protected short getOrgY() {
        return y;
    }

    public void setY(int y) {
        setY((short) y);
    }

    public void setY(short y) {
        this.y = y;
    }

    /**
     * X1座標取得<br>
     * getX()と等価
     *
     * @return x1
     */
    public int getX1() {
        return getX();
    }

    /**
     * X2座標取得
     *
     * @return x2
     */
    public int getX2() {
        return getX() + getWidth() - 1;
    }

    /**
     * Y1座標取得<br>
     * getY()と等価
     *
     * @return y1
     */
    public int getY1() {
        return getY();
    }

    /**
     * Y2座標取得
     *
     * @return y2
     */
    public int getY2() {
        return getY() + getHeight() - 1;
    }

    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        setWidth((short) width);
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        setHeight((short) height);
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public void setBounds(short width, short height) {
        setWidth(width);
        setHeight(height);
    }

    public void setBounds(int width, int height) {
        setBounds((short) width, (short) height);
    }

    /**
     * イベント処理
     *
     * @param e
     * @return
     */
    public boolean eventIn(MouseEvent e) {
        if (isVisible() == false) {
            return false;
        }

        if (((getX1() <= e.getX()) && (getX2() >= e.getX()) && (getY1() <= e.getY()) && (getY2() >= e.getY()))) {
            return true;
        }
        return false;
    }

    public boolean isCollision(DrawObjectEx dst) {
        if (dst.isVisible() == false) {
            return false;
        }
        if (dst == this) {
            return false;
        }

        return isCollisionRect(dst.getX(), dst.getY(), dst.getWidth(), dst.getHeight());
    }

    public boolean isCollisionRect(int dstX, int dstY, int dstWidth, int dstHeight) {
        boolean ret = false;
        int sx1 = this.getX();
        int sx2 = this.getX() + this.getWidth();
        int sy1 = this.getY();
        int sy2 = this.getY() + this.getHeight();

        int dstX2 = dstX + dstWidth;
        int dstY2 = dstY + dstHeight;

        // オブジェクト同士の当たり判定
        if (((sx1 > dstX && sx1 < dstX2) || (dstX > sx1 && dstX < sx2)) && ((sy1 > dstY && sy1 < dstY2) || (dstX > sy1 && dstY < sy2))) {
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        // マウスイベント用のeventin
        return eventIn(e);
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        // マウスイベント用のeventin
        return eventIn(e);
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        // マウスイベント用のeventin
        return eventIn(e);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCenterX() {
        return (int) (getX() + (getWidth() / 2));
    }

    public int getCenterY() {
        return (int) (getY() + (getHeight() / 2));
    }

    public void setAbsCenterLocation(int width, int height) {
        setLocation((int) ((width - getWidth()) / 2), (int) ((height - getHeight()) / 2));
    }

    public void setAbsCenterWidth(int width) {
        setX((int) ((width - getWidth()) / 2));
    }

    public void setAbsCenterHeight(int height) {
        setY((int) ((height - getHeight()) / 2));
    }
}
