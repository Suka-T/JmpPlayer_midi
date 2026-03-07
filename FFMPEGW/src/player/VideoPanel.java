package player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JPanel;

public class VideoPanel extends JPanel {

    private BufferedImage image;
    private byte[] pixels;

    private int videoWidth = 0;
    private int videoHeight = 0;

    public VideoPanel(int width, int height) {

        image = new BufferedImage(640, 360, BufferedImage.TYPE_3BYTE_BGR);
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        setVideoSize(width, height);
    }

    public void setInitFrame() {
        image = null;

        repaint();
    }

    public void setFrame(byte[] framePixels, int width, int height) {
        if (image == null || image.getWidth() != width || image.getHeight() != height) {
            // 画像サイズ変更時は新しくバッファーを作り直す
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        }
        System.arraycopy(framePixels, 0, pixels, 0, pixels.length);

        repaint();
    }

    public void setVideoSize(int width, int height) {
        videoWidth = width;
        videoHeight = height;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, panelW, panelH);
        if (image == null) {
            return;
        }

        int imgW = videoWidth;
        int imgH = videoHeight;

        double scale = Math.min((double) panelW / imgW, (double) panelH / imgH);

        int drawW = (int) (imgW * scale);
        int drawH = (int) (imgH * scale);

        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g.drawImage(image, x, y, drawW, drawH, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(videoWidth, videoHeight);
    }
}
