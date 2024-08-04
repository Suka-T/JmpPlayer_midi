package jmp.gui.ui;

import java.awt.Graphics;

/**
 * コントロールボタンのマーク描画用インターフェース
 *
 * @author abs
 *
 */
public interface IButtonMarkPaint {
    abstract void paintMark(Graphics g, int x, int y, int width, int height);
}
