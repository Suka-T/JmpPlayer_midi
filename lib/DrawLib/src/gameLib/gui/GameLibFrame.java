package gameLib.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import drawLib.gui.DrawLibFrame;
import gameLib.GameLib;

public class GameLibFrame extends DrawLibFrame implements WindowListener, ComponentListener, MouseListener, MouseMotionListener, KeyListener {

    public GameLibFrame() {
        super();
        this.addWindowListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        GameLib.getInstance().exit();

        try {
            exitPane();
        }
        catch (Throwable e1) {
            e1.printStackTrace();
        }
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().componentResized(e);
        if (engine != null) {
            engine.repaintAndFlipScreen();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().componentMoved(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().componentShown(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().componentHidden(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseExited(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().mouseMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        GameLib.getInstance().getSceneManager().getCurrentScene().keyReleased(e);
    }
}
