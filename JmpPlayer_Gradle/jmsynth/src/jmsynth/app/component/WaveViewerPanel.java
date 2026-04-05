package jmsynth.app.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import jmsynth.JMSoftSynthesizer;
import jmsynth.sound.SoundSourceChannel;

public class WaveViewerPanel extends JPanel implements IWaveRepaintListener {

    private static Color BG_COLOR = new Color(0, 0, 102);
    private static Color WAVE_COLOR = new Color(255, 255, 102);
    private static Color CENTER_COLOR = Color.DARK_GRAY;

    /**
     * Create the panel.
     */
    public WaveViewerPanel(JMSoftSynthesizer synth, int channel) {
        synth.setWaveRepaintListener(channel, this);
        cinit();
    }

    public WaveViewerPanel(SoundSourceChannel chobj, int channel) {
        chobj.setWaveRepaintListener(this);
        cinit();
    }

    private void cinit() {
        setPreferredSize(new Dimension(240, 150));
        setBackground(BG_COLOR);
    }

    public void update(Graphics g) {
        paint(g);
    }

    byte[] waveBuf = null;

    @Override
    public void repaintWave(byte[] waveData) {
        waveBuf = waveData;
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        // g.drawString(panelName, 10, 20);

        // 中心線描画
        int yCenter = this.getHeight() / 2;
        g2d.setColor(CENTER_COLOR);
        g2d.drawLine(0, yCenter, this.getWidth(), yCenter);
        if (waveBuf == null) {
            return;
        }
        if (waveBuf.length <= 2) {
            return;
        }

        // データ点描画
        int length = waveBuf.length / 2;
        int xPoints[] = new int[length];
        int yPoints[] = new int[length];
        double xDelta = (double) this.getWidth() / (double) (length);
        double yDelta = this.getHeight() / 64.0;
        int j = 0;
        for (int i = 0; i < waveBuf.length; i += 2) {
            xPoints[j] = (int) (xDelta * j);
            yPoints[j] = (int) (yDelta * waveBuf[i]) + yCenter;
            j++;
        }
        g2d.setColor(WAVE_COLOR);
        g2d.drawPolyline(xPoints, yPoints, length);
    }
}
