package drawLib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

public interface DrawObjectExtender {
    /**
     * 描画処理
     *
     * @param g
     *            グラフィックスクラス
     */
    abstract void draw(Graphics g);

    /**
     * mousePressedイベントの処理
     *
     * @param e
     * @return
     */
    abstract boolean mousePressed(MouseEvent e);

    /**
     * mouseReleasedイベントの処理
     *
     * @param e
     * @return
     */
    abstract boolean mouseReleased(MouseEvent e);

    /**
     * mouseClickedイベントの処理
     *
     * @param e
     * @return
     */
    abstract boolean mouseClicked(MouseEvent e);

    ////////////////////
    // 描画メソッド群
    //

    default void setGraphColor(Graphics g, Color color) {
        g.setColor(color);
    }

    default void setGraphFont(Graphics g, String name, int style, int size) {
        Font f = new Font(name, style, size);
        setGraphFont(g, f);
    }

    default void setGraphFont(Graphics g, Font font) {
        g.setFont(font);
    }

    default void drawRect(Graphics g, int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    default void draw3DRect(Graphics g, int x, int y, int width, int height, boolean raised) {
        g.draw3DRect(x, y, width, height, raised);
    }

    default void drawRoundRect(Graphics g, int x, int y, int width, int height) {
        int arcSize = (int) (width / 2);
        drawRoundRect(g, x, y, width, height, arcSize, arcSize);
    }

    default void drawRoundRect(Graphics g, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    default void fillRect(Graphics g, int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    default void fill3DRect(Graphics g, int x, int y, int width, int height, boolean raised) {
        g.fill3DRect(x, y, width, height, raised);
    }

    default void fillRoundRect(Graphics g, int x, int y, int width, int height) {
        int arcSize = (int) (width / 2);
        fillRoundRect(g, x, y, width, height, arcSize, arcSize);
    }

    default void fillRoundRect(Graphics g, int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    default void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    default void drawString(Graphics g, String str, int x, int y) {
        g.drawString(str, x, y);
    }

    default void drawOval(Graphics g, int x, int y, int width, int height) {
        g.drawOval(x, y, width, height);
    }

    default void drawPolygon(Graphics g, Polygon p) {
        g.drawPolygon(p);
    }

    default void drawPolygon(Graphics g, int[] xPoints, int[] yPoints, int numOfPoints) {
        g.drawPolygon(xPoints, yPoints, numOfPoints);
    }

    default void fillPolygon(Graphics g, Polygon p) {
        g.fillPolygon(p);
    }

    default void fillPolygon(Graphics g, int[] xPoints, int[] yPoints, int numOfPoints) {
        g.fillPolygon(xPoints, yPoints, numOfPoints);
    }
}
