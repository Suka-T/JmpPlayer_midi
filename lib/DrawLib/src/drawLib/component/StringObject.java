package drawLib.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import drawLib.DrawObjectEx;

public class StringObject extends DrawObjectEx {

    public static enum AlignX {
        LEFT, RIGHT, CENTOR
    }

    public static enum AlignY {
        TOP, BOTTOM, CENTOR
    }

    private String text = "";

    private Font font = null;
    private Color foreColor = Color.WHITE;
    private Color backColor = null;
    private AlignX alignX = AlignX.LEFT;
    private AlignY alignY = AlignY.TOP;

    public StringObject() {
        super();
    }

    public StringObject(String text) {
        super();

        setText(text);
    }

    @Override
    public void draw(Graphics g) {
        if (isVisible() == false) {
            return;
        }

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        Color frColor = getForeColor();
        Color bgColor = getBackColor();

        if (bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(x, y, width, height);
        }

        int sx, sy, sw, sh;
        sx = x;
        sy = y;
        sw = width;
        sh = height;
        g.setColor(frColor);
        g.setFont(getFont());
        g.drawString(getText(), sx, sy);

        super.draw(g);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AlignX getAlignX() {
        return alignX;
    }

    public void setAlignX(AlignX alignX) {
        this.alignX = alignX;
    }

    public AlignY getAlignY() {
        return alignY;
    }

    public void setAlignY(AlignY alignY) {
        this.alignY = alignY;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getForeColor() {
        return foreColor;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

}
