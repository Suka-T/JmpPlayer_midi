package jmp.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import function.Utility;
import jlib.gui.IJmpWindow;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.WindowManager;
import jmp.gui.ui.IJMPComponentUI;

public class JmpPlayerLaunch extends JPanel implements MouseListener, MouseMotionListener, IJMPComponentUI {

    public static final Color BORDER_COLOR = Color.WHITE;
    public static final Color BACK_COLOR = Color.BLACK;
    public static final Color BACK_COLOR2 = Utility.convertCodeToHtmlColor("#c71585");
    public static final int SIZE = 16;
    public static final int MERGIN = 2;
    public static final int TOTAL_WIDTH = 250;// ((SIZE + (MERGIN * 2))) * 8;

    public static final int VOLUME_STARTX = 88;
    public static final int VOLUME_STARTY = 2;
    public static final int VOLUME_W = 60;
    public static final int VOLUME_H = SIZE;

    public static final int MAX_VOLUME_STARTX = VOLUME_STARTX + VOLUME_W + 10;
    public static final int MAX_VOLUME_STARTY = 2;
    public static final int MAX_VOLUME_W = 60;
    public static final int MAX_VOLUME_H = SIZE;

    private float volumeMax = 1.0f;

    /**
     * Create the panel.
     */
    public JmpPlayerLaunch() {
        setLayout(null);

        setBackground(getJmpBackColor());

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int x = startX();
        int y = MERGIN;
        boolean checked = false;

        checked = checkLoopMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintLoopMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        checked = checkAutoMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintAutoMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        checked = checkPlaylistMark();
        g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
        g.fillRect(x, y, SIZE, SIZE);
        paintPlaylistMark(g, x, y, SIZE, SIZE);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        if (JMPCore.getSoundManager().isValidMediaView() == true) {
            checked = checkVisibleMediaViewMark();
            g.setColor(checked == false ? BACK_COLOR : BACK_COLOR2);
            g.fillRect(x, y, SIZE, SIZE);
            paintVisibleMediaViewMark(g, x, y, SIZE, SIZE);
            g.setColor(BORDER_COLOR);
            g.drawRect(x, y, SIZE, SIZE);
        }

        float volume = JMPCore.getSoundManager().getLineVolume();
        int volumeW = (int) (((float) VOLUME_W * volume) / volumeMax);
        int volumeH = 10;
        for (int vx = 0; vx < VOLUME_W; vx++) {
            float vp = ((float) (vx + 1) / (float) VOLUME_W);
            if (1.0f < vp) {
                vp = 1.0f;
            }
            int vy = (int) ((float) volumeH * vp);
            g.setColor(Color.BLACK);
            g.drawLine(VOLUME_STARTX + vx, VOLUME_STARTY + VOLUME_H - vy, VOLUME_STARTX + vx, VOLUME_STARTY + VOLUME_H);
            if (vx < volumeW) {
                g.setColor(Color.CYAN);
                g.drawLine(VOLUME_STARTX + vx, VOLUME_STARTY + VOLUME_H - vy, VOLUME_STARTX + vx, VOLUME_STARTY + VOLUME_H);
            }
        }
        if (JMPFlags.DebugMode == true) {
            Font vFont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 12);
            g.setFont(vFont);
            g.setColor(Color.RED);
            g.drawString("" + (int) (100.0f * volume), VOLUME_STARTX + VOLUME_W - 20, VOLUME_STARTY + 15);
        }

        int volumeMaxW = (int) ((float) MAX_VOLUME_W * volumeMax);
        g.setColor(Color.BLACK);
        g.drawLine(MAX_VOLUME_STARTX, MAX_VOLUME_STARTY + (VOLUME_H / 2), MAX_VOLUME_STARTX + MAX_VOLUME_W - 1, MAX_VOLUME_STARTY + (VOLUME_H / 2));
        g.setColor(Color.CYAN);
        g.drawLine(MAX_VOLUME_STARTX, MAX_VOLUME_STARTY + (VOLUME_H / 2), MAX_VOLUME_STARTX + volumeMaxW - 1, MAX_VOLUME_STARTY + (VOLUME_H / 2));
        g.drawLine(MAX_VOLUME_STARTX + volumeMaxW - 1, MAX_VOLUME_STARTY + 5, MAX_VOLUME_STARTX + volumeMaxW - 1, MAX_VOLUME_STARTY + VOLUME_H - 5);
        g.setColor(Color.WHITE);
        g.drawLine(MAX_VOLUME_STARTX, MAX_VOLUME_STARTY + 2, MAX_VOLUME_STARTX, MAX_VOLUME_STARTY + VOLUME_H - 2);
        g.drawLine(MAX_VOLUME_STARTX + MAX_VOLUME_W - 1, MAX_VOLUME_STARTY + 2, MAX_VOLUME_STARTX + MAX_VOLUME_W - 1, MAX_VOLUME_STARTY + VOLUME_H - 2);

        // for (int vx=0; vx<MAX_VOLUME_W; vx++) {
        // float vp = ((float)(vx+1) / (float)MAX_VOLUME_W);
        // if (1.0f < vp) {
        // vp = 1.0f;
        // }
        // int vy = (int)((float)volumeH * vp);
        // g.setColor(Color.BLACK);
        // g.drawLine(MAX_VOLUME_STARTX + vx, MAX_VOLUME_STARTY+VOLUME_H-vy,
        // MAX_VOLUME_STARTX + vx, MAX_VOLUME_STARTY+VOLUME_H);
        // if (vx < volumeMaxW) {
        // g.setColor(Color.CYAN);
        // g.drawLine(MAX_VOLUME_STARTX + vx, MAX_VOLUME_STARTY+VOLUME_H-vy,
        // MAX_VOLUME_STARTX + vx, MAX_VOLUME_STARTY+VOLUME_H);
        // }
        // }
        if (JMPFlags.DebugMode == true) {
            Font vFont = new Font(Font.DIALOG_INPUT, Font.PLAIN, 12);
            g.setFont(vFont);
            g.setColor(Color.RED);
            g.drawString("" + (int) (100.0f * volumeMax), MAX_VOLUME_STARTX + MAX_VOLUME_W - 20, MAX_VOLUME_STARTY + 15);
        }
    }

    @Override
    public void paintComponents(Graphics g) {
        // super.paintComponents(g);
    }

    public boolean checkAutoMark() {
        return JMPCore.getDataManager().isAutoPlay();
    }

    public void paintAutoMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnAutoIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    public boolean checkLoopMark() {
        return JMPCore.getDataManager().isLoopPlay();
    }

    public void paintLoopMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnLoopIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    public boolean checkPlaylistMark() {
        IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
        if (win != null) {
            return win.isWindowVisible();
        }
        return false;
    }

    public void paintPlaylistMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnListIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
        }
    }

    public boolean checkVisibleMediaViewMark() {
        return JMPCore.getSoundManager().isVisibleMediaView();
    }

    public void paintVisibleMediaViewMark(Graphics g, int x, int y, int width, int height) {
        Image img = JMPCore.getResourceManager().getBtnViewIcon();
        if (img != null) {
            int imgX = x + (width - img.getWidth(null)) / 2;
            int imgY = y + (height - img.getHeight(null)) / 2;
            imgX = (imgX < 0) ? 0 : imgX;
            imgY = (imgY < 0) ? 0 : imgY;
            g.drawImage(img, imgX, imgY, null);
            g.drawImage(img, x, y, null);
        }
    }

    private int startX() {
        return MERGIN * 2;
    }

    private int startY() {
        return MERGIN;
    }

    public void volumeSlide(MouseEvent e) {
        WindowManager wm = JMPCore.getWindowManager();
        if ((VOLUME_STARTX < e.getX()) && (e.getX() < VOLUME_STARTX + VOLUME_W + 1) && (VOLUME_STARTY < e.getY()) && (e.getY() < VOLUME_STARTY + VOLUME_H)) {
            int vx = e.getX() - VOLUME_STARTX;
            float volume = ((float) vx * volumeMax) / (float) VOLUME_W;
            JMPCore.getSoundManager().setLineVolume(volume);
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }

        if ((MAX_VOLUME_STARTX < e.getX()) && (e.getX() < MAX_VOLUME_STARTX + MAX_VOLUME_W + 1) && (MAX_VOLUME_STARTY < e.getY())
                && (e.getY() < MAX_VOLUME_STARTY + MAX_VOLUME_H)) {
            int vx = e.getX() - MAX_VOLUME_STARTX;
            float val = (float) vx / (float) MAX_VOLUME_W;
            volumeMax = val;
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        volumeSlide(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        DataManager dm = JMPCore.getDataManager();
        WindowManager wm = JMPCore.getWindowManager();
        int x = startX();
        int y = startY();
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            dm.setLoopPlay(!dm.isLoopPlay());
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            dm.setAutoPlay(!dm.isAutoPlay());
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            IJmpWindow win = wm.getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
            if (win != null) {
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            if (JMPCore.getSoundManager().isVisibleMediaView() == true) {
                JMPCore.getSoundManager().setVisibleMediaView(false);
            }
            else {
                JMPCore.getSoundManager().setVisibleMediaView(true);
            }
            wm.repaint(WindowManager.WINDOW_NAME_MAIN);
            return;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void updateBackColor() {
        setBackground(getJmpBackColor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        volumeSlide(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
