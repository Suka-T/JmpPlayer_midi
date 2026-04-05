package jmp.gui.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JDialog;

import jlib.gui.IJmpWindow;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.WindowManager;

public class JMPDialog extends JDialog implements IJMPComponentUI, IJmpWindow {

    public JMPDialog() {
        _init();
    }

    public JMPDialog(Frame owner) {
        super(owner);
        _init();
    }

    public JMPDialog(Dialog owner) {
        super(owner);
        _init();
    }

    public JMPDialog(Window owner) {
        super(owner);
        _init();
    }

    public JMPDialog(Frame owner, boolean modal) {
        super(owner, modal);
        _init();
    }

    public JMPDialog(Frame owner, String title) {
        super(owner, title);
        _init();
    }

    public JMPDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        _init();
    }

    public JMPDialog(Dialog owner, String title) {
        super(owner, title);
        _init();
    }

    public JMPDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        _init();
    }

    public JMPDialog(Window owner, String title) {
        super(owner, title);
        _init();
    }

    public JMPDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        _init();
    }

    public JMPDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        _init();
    }

    public JMPDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        _init();
    }

    public JMPDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        _init();
    }

    public JMPDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        _init();
    }

    public JMPDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        _init();
    }

    protected void setJmpIcon() {
        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }
    }

    private void _init() {
        getContentPane().setBackground(getJmpBackColor());
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
