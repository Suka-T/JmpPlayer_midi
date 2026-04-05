package jmp.gui.ui;

import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;

import jlib.gui.IJmpWindow;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.WindowManager;

public class JMPFrame extends JFrame implements IJMPComponentUI, IJmpWindow {

    public JMPFrame() {
        super();

        _init();
    }

    private void _init() {
        getContentPane().setBackground(getJmpBackColor());
    }

    protected void setJmpIcon() {
        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }
    }

    protected boolean isAutomationDefaultPosition() {
        return JMPFlags.WindowAutomationPosFlag;
    }

    @Override
    public void setDefaultWindowLocation() {
        Point p = getDefaultWindowLocation();
        if (p != null) {
            this.setLocation(p);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (super.isVisible() == false && b == true) {
            if (isAutomationDefaultPosition() == true) {
                setDefaultWindowLocation();
            }
        }
        super.setVisible(b);

        // メインウィンドウを更新する
        JMPCore.getWindowManager().repaint(WindowManager.WINDOW_NAME_MAIN);
    }

    @Override
    public void showWindow() {
        setVisible(true);
        repaintWindow();
    }

    @Override
    public void hideWindow() {
        setVisible(false);
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

    @Override
    public void updateBackColor() {
        getContentPane().setBackground(getJmpBackColor());
    }

    @Override
    public void repaintWindow() {
        repaint();
    }

}
