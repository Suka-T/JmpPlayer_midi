package jmp.gui.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class ControlButtonUI extends BasicButtonUI {
    private ArrayList<IButtonMarkPaint> painters = new ArrayList<IButtonMarkPaint>();
    public static final int MARK_SIZE = 30;

    public ControlButtonUI() {
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        paintMark(g, (c.getWidth() - MARK_SIZE) / 2, (c.getHeight() - MARK_SIZE) / 2, MARK_SIZE, MARK_SIZE);
    }

    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        // ※テキスト表示はマスクする。
        // super.paintText(g, b, textRect, text);
    }

    protected void paintMark(Graphics g, int x, int y, int w, int h) {
        for (IButtonMarkPaint mp : painters) {
            mp.paintMark(g, x, y, w, h);
        }
    }

    public void addMarkPainter(IButtonMarkPaint markPainter) {
        painters.add(markPainter);
    }
}
