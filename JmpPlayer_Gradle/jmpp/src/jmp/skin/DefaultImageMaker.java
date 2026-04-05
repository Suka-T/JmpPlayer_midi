package jmp.skin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import function.Utility;
import jmp.util.JmpUtil;

public class DefaultImageMaker {

    public static final int DEFAULT_PLAYER_ICON_SIZE = 30;

    private static Image defDummyIcon = null;

    public static Image makeDummyIcon() {
        if (defDummyIcon == null) {
            BufferedImage buf = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);
            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            defDummyIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defDummyIcon;
    }

    private static Image makeDefaultFileIcon(Color fileColor, int bdHighlight) {
        Image ret = null;
        BufferedImage buf = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) buf.getGraphics();
        int x = 0;
        int y = 0;
        int width = buf.getWidth(null);
        int height = buf.getHeight(null);

        int fx = 3;
        int fy = 2;
        int fw = 14;
        int fh = 16;

        Color transColor = Color.ORANGE;
        g2d.setColor(transColor);
        g2d.fillRect(x, y, width, height);

        Color color = fileColor;
        g2d.setColor(Utility.convertHighLightColor(color, bdHighlight));
        g2d.fillRect(fx, fy, fw, fh);

        g2d.setColor(color);
        g2d.fillRect(fx + 2, fy + 2, fw - 4, fh - 4);

        ret = JmpUtil.transformImageToTransparency(buf, transColor);
        return ret;
    }

    private static Image defFileOtherIcon = null;

    public static Image makeDefaultFileOtherIcon() {
        if (defFileOtherIcon == null) {
            defFileOtherIcon = makeDefaultFileIcon(Color.BLACK, 120);
        }
        return defFileOtherIcon;
    }

    private static Image defFileMidiIcon = null;

    public static Image makeDefaultFileMidiIcon() {
        if (defFileMidiIcon == null) {
            defFileMidiIcon = makeDefaultFileIcon(Color.PINK, -100);
        }
        return defFileMidiIcon;
    }

    private static Image defFileWavIcon = null;

    public static Image makeDefaultFileWavIcon() {
        if (defFileWavIcon == null) {
            defFileWavIcon = makeDefaultFileIcon(Color.CYAN, -100);
        }
        return defFileWavIcon;
    }

    private static Image defFileXmlIcon = null;

    public static Image makeDefaultFileXmlIcon() {
        if (defFileXmlIcon == null) {
            defFileXmlIcon = makeDefaultFileIcon(Color.LIGHT_GRAY, -100);
        }
        return defFileXmlIcon;
    }

    private static Image defFileMusicIcon = null;

    public static Image makeDefaultFileMusicIcon() {
        if (defFileMusicIcon == null) {
            defFileMusicIcon = makeDefaultFileIcon(new Color(138, 43, 226), 50);
        }
        return defFileMusicIcon;
    }

    private static Image defFileFolderIcon = null;

    public static Image makeDefaultFileFolderIcon() {
        if (defFileFolderIcon == null) {
            BufferedImage buf = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            int fx = 1;
            int fy = 4;
            int fw = 18;
            int fh = 14;

            Color transColor = Color.PINK;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.ORANGE;
            g2d.setColor(Utility.convertHighLightColor(color, -100));
            g2d.fillRect(fx, fy, fw, fh);

            g2d.setColor(color);
            g2d.fillRect(fx + 2, fy + 2, fw - 4, fh - 4);

            defFileFolderIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defFileFolderIcon;
    }

    private static Image defBtnPlayIcon = null;

    public static Image makeDefaultBtnPlayIcon() {
        if (defBtnPlayIcon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Utility.convertCodeToHtmlColor("#00ee00");
            g2d.setColor(color);

            int xPoints[] = { x, x, x + width };
            int yPoints[] = { y, y + height, y + (height / 2) };
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.LIGHT_GRAY);
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
            defBtnPlayIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnPlayIcon;
    }

    private static Image defBtnStopIcon = null;

    public static Image makeDefaultBtnStopIcon() {
        if (defBtnStopIcon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.RED;
            g2d.setColor(color);

            int offsetW = (width / 2) - 4;
            x += 2;

            g2d.fillRect(x, y, offsetW, height);
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 1; i++) {
                g2d.drawRect(x + i, y + i, offsetW - (i * 2), height - (i * 2));
            }

            g2d.setColor(color);
            g2d.fillRect(x + offsetW + 4, y, offsetW, height);
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 1; i++) {
                g2d.drawRect(x + offsetW + 4 + i, y + i, offsetW - (i * 2), height - (i * 2));
            }
            defBtnStopIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnStopIcon;
    }

    private static Image defBtnNextIcon = null;

    public static Image makeDefaultBtnNextIcon() {
        if (defBtnNextIcon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.BLUE;
            for (int i = 0; i < 2; i++) {
                int offsetW = width / 2;
                int xPoints[] = { x + (offsetW * i), x + (offsetW * i), x + (offsetW * i) + width - offsetW };
                int yPoints[] = { y, y + height, y + (height / 2) };
                g2d.setColor(color);
                g2d.fillPolygon(xPoints, yPoints, 3);
                g2d.setColor(Color.LIGHT_GRAY);
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
            }
            defBtnNextIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnNextIcon;
    }

    private static Image defBtnNext2Icon = null;

    public static Image makeDefaultBtnNext2Icon() {
        if (defBtnNext2Icon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            int xPoints[] = { x, x, (int) (x + (width * 0.7)) };
            int yPoints[] = { y, y + height, y + (height / 2) };
            Color color = Color.BLUE;
            g2d.setColor(color);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.LIGHT_GRAY);
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
            g2d.setColor(Color.LIGHT_GRAY);
            for (int j = 0; j < 1; j++) {
                g2d.drawRect(x + (int) (width * 0.7) + j, y + j, (int) (width * 0.3) - (j * 2), height - (j * 2));
            }
            defBtnNext2Icon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnNext2Icon;
    }

    private static Image defBtnPrevIcon = null;

    public static Image makeDefaultBtnPrevIcon() {
        if (defBtnPrevIcon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            x -= 2;
            Color color = Color.BLUE;
            for (int i = 0; i < 2; i++) {
                int offsetW = width / 2;
                int xPoints[] = { x + (offsetW * i), x + (offsetW * i) + width - offsetW, x + (offsetW * i) + width - offsetW };
                int yPoints[] = { y + (height / 2), y, y + height, };
                g2d.setColor(color);
                g2d.fillPolygon(xPoints, yPoints, 3);
                g2d.setColor(Color.LIGHT_GRAY);
                for (int j = 0; j < 1; j++) {
                    int[] xp, yp;
                    xp = Arrays.copyOf(xPoints, xPoints.length);
                    yp = Arrays.copyOf(yPoints, yPoints.length);
                    xp[0] += j;
                    xp[1] -= j;
                    xp[2] -= j;
                    yp[1] += j;
                    yp[2] -= j;
                    g2d.drawPolygon(xp, yp, 3);
                }
            }
            defBtnPrevIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnPrevIcon;
    }

    private static Image defBtnPrev2Icon = null;

    public static Image makeDefaultBtnPrev2Icon() {
        if (defBtnPrev2Icon == null) {
            BufferedImage buf = new BufferedImage(DEFAULT_PLAYER_ICON_SIZE, DEFAULT_PLAYER_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            x -= 2;
            Color color = Color.BLUE;
            g2d.setColor(color);
            g2d.fillRect(x, y, (int) (width * 0.3), height);
            g2d.setColor(Color.LIGHT_GRAY);
            for (int j = 0; j < 1; j++) {
                g2d.drawRect(x + j, y + j, (int) (width * 0.3) - (j * 2), height - (j * 2));
            }

            int xPoints[] = { x + (int) (width * 0.3), x + width, x + width };
            int yPoints[] = { y + (height / 2), y, y + height };
            g2d.setColor(color);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.LIGHT_GRAY);
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
            defBtnPrev2Icon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnPrev2Icon;
    }

    private static Image defBtnAutoIcon = null;

    public static Image makeDefaultBtnAutoIcon() {
        if (defBtnAutoIcon == null) {
            BufferedImage buf = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            final int ln = 2;
            int fx = 2;
            int fy = 4;
            int fw = 12;
            int fh = 8;

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.WHITE;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawLine(fx, fy, fx + fw - 1 - ln, fy + fh - 1);
            g2d.drawLine(fx + fw - 1 - ln, fy, fx + fw - 1, fy);
            g2d.drawLine(fx, fy + fh - 1, fx + fw - 1 - ln, fy);
            g2d.drawLine(fx + fw - 1 - ln, fy + fh - 1, fx + fw - 1, fy + fh - 1);
            g2d.setStroke(new BasicStroke());
            defBtnAutoIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnAutoIcon;
    }

    private static Image defBtnLoopIcon = null;

    public static Image makeDefaultBtnLoopIcon() {
        if (defBtnLoopIcon == null) {
            BufferedImage buf = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            int fx = 2;
            int fy = 4;
            int fw = 12;
            int fh = 8;

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.WHITE;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(fx, fy, fw, fh);
            g2d.setColor(transColor);
            g2d.drawLine(fx + 5, fy + fh, fx + fw - 5, fy + fh);
            g2d.setColor(color);
            g2d.drawLine(fx + fw - 5, fy + fh, fx + fw - 3, fy + fh - 2);
            g2d.drawLine(fx + fw - 5, fy + fh, fx + fw - 3, fy + fh + 2);
            defBtnLoopIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnLoopIcon;
    }

    private static Image defBtnListIcon = null;

    public static Image makeDefaultBtnListIcon() {
        if (defBtnListIcon == null) {
            BufferedImage buf = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            int fx = 4;
            int fy = 4;
            int fw = 8;
            int fh = 12;
            int fx2 = fx + fw;
            int fy2 = fy + fh;

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.WHITE;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.0f));
            for (int i = 0; i < 4; i++) {
                g2d.drawLine(fx, fy + ((fy2 / 4) * i), fx2, fy + ((fy2 / 4) * i));
            }
            defBtnListIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnListIcon;
    }

    private static Image defBtnViewIcon = null;

    public static Image makeDefaultBtnViewIcon() {
        if (defBtnViewIcon == null) {
            BufferedImage buf = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) buf.getGraphics();
            int x = 0;
            int y = 0;
            int width = buf.getWidth(null);
            int height = buf.getHeight(null);

            int fx = 3;
            int fy = 3;
            int fw = 10;
            int fh = 10;

            Color transColor = Color.ORANGE;
            g2d.setColor(transColor);
            g2d.fillRect(x, y, width, height);

            Color color = Color.WHITE;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRect(fx, fy, fw, fh);
            g2d.fillRect(fx, fy, fw, 3);
            defBtnViewIcon = JmpUtil.transformImageToTransparency(buf, transColor);
        }
        return defBtnViewIcon;
    }
}
