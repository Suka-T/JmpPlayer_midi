package jmp.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import function.Utility;
import jmp.core.JMPCore;
import jmp.core.SoundManager;
import jmp.core.SystemManager;

public class JmpQuickLaunch extends JPanel implements MouseListener {

    public static List<JmpQuickLaunch> Accessor = null;

    public static final Color BORDER_COLOR = Color.LIGHT_GRAY;
    public static final Color BACK_COLOR = Color.WHITE;
    public static final int SIZE = 15;
    public static final int MERGIN = 2;
    public static final int TOTAL_WIDTH = ((SIZE + (MERGIN * 2))) * 3;

    /**
     * Create the panel.
     */
    public JmpQuickLaunch() {
        setLayout(null);

        setBackground(Utility.convertCodeToHtmlColor(JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_PLAYER_BACK_COLOR)));

        addMouseListener(this);

        if (Accessor == null) {
            Accessor = new ArrayList<JmpQuickLaunch>();
        }
        Accessor.add(this);
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int x = startX();
        int y = MERGIN;
        g.setColor(BACK_COLOR);
        g.fillRect(x, y, SIZE, SIZE);
        paintPrevMark(g, x + 2, y + 2, SIZE - 4, SIZE - 4);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        g.setColor(BACK_COLOR);
        g.fillRect(x, y, SIZE, SIZE);
        paintPlayStopMark(g, x + 2, y + 2, SIZE - 4, SIZE - 4);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);

        x += (SIZE + (MERGIN * 2));
        g.setColor(BACK_COLOR);
        g.fillRect(x, y, SIZE, SIZE);
        paintNextMark(g, x + 2, y + 2, SIZE - 4, SIZE - 4);
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, SIZE, SIZE);
    }

    @Override
    public void paintComponents(Graphics g) {
        // super.paintComponents(g);
    }

    public void paintNextMark(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        int xPoints[] = { x, x, (int) (x + (width * 0.7)) };
        int yPoints[] = { y, y + height, y + (height / 2) };
        Color color = Color.BLUE;
        Color bdColor = Color.GRAY;
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(bdColor);
        for (int j = 0; j < 1; j++) {
            int[] xp, yp;
            xp = Arrays.copyOf(xPoints, xPoints.length);
            yp = Arrays.copyOf(yPoints, yPoints.length);
            xp[0] += j;
            xp[1] += j;
            xp[2] -= j;
            yp[0] += j;
            yp[1] -= j;
            g2d.drawPolygon(xp, yp, 3);
        }

        g2d.setColor(color);
        g2d.fillRect(x + (int) (width * 0.7), y, (int) (width * 0.3), height);
        g2d.setColor(bdColor);
        for (int j = 0; j < 1; j++) {
            g2d.drawRect(x + (int) (width * 0.7) + j, y + j, (int) (width * 0.3) - (j * 2), height - (j * 2));
        }
    }

    public void paintPrevMark(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        Color color = Color.BLUE;
        Color bdColor = Color.GRAY;
        g2d.setColor(color);
        g2d.fillRect(x, y, (int) (width * 0.3), height);
        g2d.setColor(bdColor);
        for (int i = 0; i < 1; i++) {
            g2d.drawRect(x + i, y + i, (int) (width * 0.3) - (i * 2), height - (i * 2));
        }

        int xPoints[] = { x + (int) (width * 0.3), x + width, x + width };
        int yPoints[] = { y + (height / 2), y, y + height };
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(bdColor);
        for (int i = 0; i < 1; i++) {
            int[] xp, yp;
            xp = Arrays.copyOf(xPoints, xPoints.length);
            yp = Arrays.copyOf(yPoints, yPoints.length);
            xp[0] += i;
            xp[1] -= i;
            xp[2] -= i;
            yp[1] += i;
            yp[2] -= i;
            g2d.drawPolygon(xp, yp, 3);
        }
    }

    public void paintPlayStopMark(Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        SoundManager sm = JMPCore.getSoundManager();
        Color bdColor = Color.GRAY;
        if (sm.isPlay() == true) {
            int offsetW = (width / 2) - 4;
            x += 2;

            Color color = Color.RED;
            g2d.setColor(color);
            g2d.fillRect(x, y, offsetW, height);
            g2d.setColor(color);
            for (int i = 0; i < 1; i++) {
                g2d.drawRect(x + i, y + i, offsetW - (i * 2), height - (i * 2));
            }

            g2d.setColor(color);
            g2d.fillRect(x + offsetW + 4, y, offsetW, height);
            g2d.setColor(color);
            for (int i = 0; i < 1; i++) {
                g2d.drawRect(x + offsetW + 4 + i, y + i, offsetW - (i * 2), height - (i * 2));
            }
        }
        else {
            // 再生ボタン
            Color color = Utility.convertCodeToHtmlColor("#00ee00");
            g2d.setColor(color);

            int xPoints[] = { x, x, x + width };
            int yPoints[] = { y, y + height, y + (height / 2) };
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(bdColor);
            for (int i = 0; i < 1; i++) {
                int[] xp, yp;
                xp = Arrays.copyOf(xPoints, xPoints.length);
                yp = Arrays.copyOf(yPoints, yPoints.length);
                xp[0] += i;
                xp[1] += i;
                xp[2] -= i;
                yp[0] += i;
                yp[1] -= i;
                g2d.drawPolygon(xp, yp, 3);
            }
        }
    }

    private int startX() {
        // return MERGIN;
        return getWidth() - TOTAL_WIDTH;
    }

    private int startY() {
        return MERGIN;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        SoundManager sm = JMPCore.getSoundManager();
        int x = startX();
        int y = startY();
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            sm.initPosition();
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            sm.togglePlayStop();
            return;
        }

        x += (SIZE + (MERGIN * 2));
        if ((x < e.getX()) && (e.getX() < x + SIZE) && (y < e.getY()) && (e.getY() < y + SIZE)) {
            sm.endPosition();
            return;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
