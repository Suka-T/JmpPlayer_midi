package jmsynth.app.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.sound.sampled.FloatControl;
import javax.swing.JPanel;

import jmsynth.JMSoftSynthesizer;
import jmsynth.sound.SoundSourceChannel;

public class MultiWaveViewerPanel extends JPanel implements MouseListener {

    public static final float WAVE_BOLD = 1.0f;
    public static final int TRACE_VIEW_MODE_DETAIL = 0;
    public static final int TRACE_VIEW_MODE_MERGE = 1;
    public static final int TRACE_VIEW_MODE_SPECT = 2;
    public static final int TRACE_VIEW_MODE_INFO = 3;

    private static Color BG_COLOR = Color.BLACK;
    // private static Color BG_COLOR = new Color(230, 230, 230);
    private static Color DEFAULT_COLOR = Color.WHITE;
    private static Color CENTER_COLOR = Color.DARK_GRAY;

    byte[] d1 = null;
    byte[] d2 = null;
    byte[] d3 = null;
    byte[] d4 = null;
    byte[] d5 = null;
    byte[] d6 = null;
    byte[] d7 = null;
    byte[] d8 = null;
    byte[] d9 = null;
    byte[] d10 = null;
    byte[] d11 = null;
    byte[] d12 = null;
    byte[] d13 = null;
    byte[] d14 = null;
    byte[] d15 = null;
    byte[] d16 = null;
    public boolean autoRepaintTask = false;
    public boolean visibleAuto = false;
    public boolean[] visibleWave = new boolean[16];
    public int traceViewMode = TRACE_VIEW_MODE_DETAIL;

    private Color[] waveColorTable = null;
    private Color[] backupWaveColorTable = null;
    private Color ingStrColor = DEFAULT_COLOR;
    private Color ingRowColor = Color.BLUE;
    private Color ingMidColor = Color.ORANGE;
    private Color ingCurColor = Color.RED;
    private Color ingNomColor = Color.WHITE;

    private static boolean hispeedSpectrum = false;
    private boolean hispeedSpectrumRunnable = true;
    private byte[] hWaveBuf = null;
    private double[] hSpetrumBuf = null;
    private Thread spectrumMonitorThread = null;

    private JMSoftSynthesizer synth;
    
    private int pressedX = -1;
    private int pressedY = -1;
    private int selectedChannel = -1;
    private double selectedValue = 0.0;

    /**
     * Create the panel.
     */
    public MultiWaveViewerPanel(JMSoftSynthesizer synth) {
        this.synth = synth;
        synth.setWaveRepaintListener(0, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d1 = waveData;
            }
        });
        synth.setWaveRepaintListener(1, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d2 = waveData;
            }
        });
        synth.setWaveRepaintListener(2, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d3 = waveData;
            }
        });
        synth.setWaveRepaintListener(3, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d4 = waveData;
            }
        });
        synth.setWaveRepaintListener(4, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d5 = waveData;
            }
        });
        synth.setWaveRepaintListener(5, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d6 = waveData;
            }
        });
        synth.setWaveRepaintListener(6, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d7 = waveData;
            }
        });
        synth.setWaveRepaintListener(7, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d8 = waveData;
            }
        });
        synth.setWaveRepaintListener(8, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d9 = waveData;
            }
        });
        synth.setWaveRepaintListener(9, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d10 = waveData;
            }
        });
        synth.setWaveRepaintListener(10, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d11 = waveData;
            }
        });
        synth.setWaveRepaintListener(11, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d12 = waveData;
            }
        });
        synth.setWaveRepaintListener(12, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d13 = waveData;
            }
        });
        synth.setWaveRepaintListener(13, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d14 = waveData;
            }
        });
        synth.setWaveRepaintListener(14, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d15 = waveData;
            }
        });
        synth.setWaveRepaintListener(15, new IWaveRepaintListener() {

            @Override
            public void repaintWave(byte[] waveData) {
                d16 = waveData;

                // 16chをトリガーにして再描画する
                if (autoRepaintTask == true) {
                    repaintWavePane();
                }
            }
        });
        cinit();
        
        this.addMouseListener(this);
    }

    public void repaintWavePane() {
        if (isVisible() == true) {
            repaint();
        }
    }

    private void cinit() {
        // setPreferredSize(new Dimension(240, 150));
        setBackground(BG_COLOR);
        Arrays.fill(visibleWave, true);

        waveColorTable = new Color[16];
        backupWaveColorTable = new Color[16];
        for (int i = 0; i < waveColorTable.length; i++) {
            waveColorTable[i] = DEFAULT_COLOR;
            backupWaveColorTable[i] = DEFAULT_COLOR;
        }
        setRestoreColorTable();

        if (hispeedSpectrum == true) {
            spectrumMonitorThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (hispeedSpectrumRunnable) {

                        if (hWaveBuf == null) {
                            // System.out.println("a");
                            continue;
                        }
                        try {
                            hSpetrumBuf = DFT(hWaveBuf);
                        }
                        catch (Exception e) {
                        }

                        // try {
                        // Thread.sleep(100);
                        // }
                        // catch (InterruptedException e) {
                        // e.printStackTrace();
                        // }
                    }
                }
            });
            spectrumMonitorThread.start();
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    private byte[] getDataArray(int ch) {
        switch (ch) {
            case 0:
                return d1;//
            case 1:
                return d2;//
            case 2:
                return d3;//
            case 3:
                return d4;//
            case 4:
                return d5;//
            case 5:
                return d6;//
            case 6:
                return d7;//
            case 7:
                return d8;//
            case 8:
                return d9;//
            case 9:
                return d10;//
            case 10:
                return d11;//
            case 11:
                return d12;//
            case 12:
                return d13;//
            case 13:
                return d14;//
            case 14:
                return d15;//
            case 15:
                return d16;//
            default:
                return null;
        }
    }

    private boolean isVisibleWave(int ch) {
        if (traceViewMode == TRACE_VIEW_MODE_MERGE) {
            return true;
        }
        return visibleWave[ch];
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        // g.drawString(panelName, 10, 20);

        for (int i = 0; i < 16; i++) {
            if (visibleAuto == true) {
                byte[] d = getDataArray(i);
                if (d != null && d.length >= 0 && d[0] != 0) {
                    visibleWave[i] = true;
                }
            }
        }

        if (traceViewMode == TRACE_VIEW_MODE_DETAIL) {
            for (int i = 0; i < 16; i++) {
                paintWave(g, getDataArray(i), i);
            }
        }
        else if (traceViewMode == TRACE_VIEW_MODE_SPECT) {
            paintSpectrum(g2d);
        }
        else if (traceViewMode == TRACE_VIEW_MODE_MERGE) {
            // paintMergeWave(g); 波形合成表示はボツ!
            for (int i = 0; i < 16; i++) {
                paintWave(g, getDataArray(i), i);
            }
            paintChannelInfo(g);
        }
        else {
            paintChannelInfo(g);
            //paintControlPanel(g, 0);
        }
    }

    public void paintMergeWave(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (d1 == null) {
            return;
        }
        if (d1.length <= 2) {
            return;
        }

        int[] dispData = new int[d1.length];
        Arrays.fill(dispData, 0);
        for (int j = 0; j < visibleWave.length; j++) {
            for (int i = 0; i < d1.length; i++) {
                dispData[i] += getDataArray(j)[i];
            }
        }

        int x = 0;
        // int y = 0;
        int xoffset = 0;
        int yCenter = this.getHeight() / 2;
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();

        g2d.setColor(CENTER_COLOR);
        g2d.drawLine(x, yCenter, vWidth - 1, yCenter);

        int length = dispData.length / 2;
        double xDelta = (double) vWidth / (double) (length);
        double yDelta = vHeight / 300.0;
        int j = 0;

        int CLIPPING_PX = (vHeight / 2);
        int xPoints[] = new int[length];
        int yPoints[] = new int[length];
        for (int i = 0; i < dispData.length; i += 2) {
            int dPx = (int) (dispData[i]);
            xPoints[j] = xoffset + (int) (xDelta * j);
            yPoints[j] = (int) (yDelta * dPx) + yCenter;
            if (yPoints[j] > (yCenter + CLIPPING_PX)) {
                yPoints[j] = (yCenter + CLIPPING_PX);
            }
            else if (yPoints[j] < (yCenter - CLIPPING_PX)) {
                yPoints[j] = (yCenter - CLIPPING_PX);
            }
            j++;
        }
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(WAVE_BOLD, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
        g2d.drawPolyline(xPoints, yPoints, length);
        g2d.setStroke(new BasicStroke());
    }

    private void drawControlBar(Graphics g, int x, int y, int width, int height, double cur, double min, double max) {
        Graphics2D g2d = (Graphics2D) g;
        Font f = g2d.getFont();
        
        int strOffset = 20;
        cur -= min;
        max -= min;
        int mem = (int) ((double) width * (cur / max));
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, mem, height);
        
        if (traceViewMode == TRACE_VIEW_MODE_INFO) {
            // 情報表示は数値を表示  
            g2d.setColor(Color.WHITE);
            //g2d.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
            String sVal = String.format("%.3f", cur);
            g2d.drawString(sVal, x + width + 10, y + strOffset);
        }
        g2d.setFont(f);
        g2d.setColor(Color.WHITE);
    }
    
    private void drawBar(Graphics g, int x, int y, int height, double cur, double min, double max) {
        Graphics2D g2d = (Graphics2D) g;
        Font f = g2d.getFont();
        
        Color strColor = ingStrColor;
        Color rowColor = ingRowColor;
        Color midColor = ingMidColor;
        Color curColor = ingCurColor;
        Color normalColor = ingNomColor;
        if (traceViewMode == TRACE_VIEW_MODE_INFO) {
            strColor = Color.WHITE;
            rowColor = Color.DARK_GRAY;
            midColor = Color.DARK_GRAY;
            curColor = Color.DARK_GRAY;
            normalColor = Color.DARK_GRAY;
        }
        
        int gap = 1;
        int numOfMem = 100;
        if (traceViewMode == TRACE_VIEW_MODE_MERGE) {
            gap = 2;
            numOfMem = 50;
        }
        int mem = 0;
        cur -= min;
        max -= min;
        mem = (int) ((double) numOfMem * (cur / max));
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < mem; i++) {
            if (i == 0) {
                g2d.setColor(rowColor);
            }
            else if (i == (numOfMem - 1) / 2) {
                g2d.setColor(midColor);
            }
            else {
                g2d.setColor(normalColor);
            }
            g2d.drawLine(x + (i * gap), y, x + (i * gap), y + height);
        }
        if (mem > 0) {
            g2d.setColor(curColor);
            g2d.drawLine(x + (mem - 1) * gap, y, x + (mem - 1) * gap, y + height);
        }
        
        if (traceViewMode == TRACE_VIEW_MODE_INFO) {
            // 情報表示は数値を表示  
            g2d.setColor(strColor);
            g2d.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
            String sVal = String.format("%.3f", cur);
            g2d.drawString(sVal, x, y+10);
        }
        g2d.setFont(f);
        g2d.setColor(strColor);
    }

    public void paintChannelInfo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int numOfRow = 4;
        int numOfCol = 4;

        int rh = 12;
        int w = getWidth() / numOfCol;
        int h = getHeight() / numOfRow;
        String title = "";
        String value = "";
        final int valXGap = 50;
        double max, min, cur;
        for (int c = 0; c < numOfRow; c++) {
            for (int r = 0; r < numOfRow; r++) {
                int x = 5 + (c * w);
                int y = 15 + (r * h);
                int i = (r * numOfCol) + c;
                SoundSourceChannel channel = synth.getChannel(i);
                Color waveColor = waveColorTable[channel.getChannel()];
                g2d.setColor(waveColor);
                g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
                g2d.drawString("CH" + (channel.getChannel() + 1), x, y);
                g2d.setColor(Color.WHITE);
                x += 15;
                y += 14;
                g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                title = "poly:";
                value = String.format("%d|%d", channel.getNumOfTones(), channel.getNumOfTonePool());
                g2d.drawString(title, x, y);
                Font f = g2d.getFont();
                g2d.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
                g2d.drawString(value, x + valXGap, y);
                g2d.setFont(f);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "level:";
                g2d.drawString(title, x, y);
                max = 15.875;
                cur = (double) channel.getToneOverallLevel();
                min = 0.0;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "env:";
                // value = String.format("%.5f",
                // channel.getToneEnvelopeOffset());
                g2d.drawString(title, x, y);
                max = 1.0;
                cur = (double) channel.getToneEnvelopeOffset();
                min = 0.0;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "vel:";
                g2d.drawString(title, x, y);
                // value = String.format("%d", channel.getToneVelocity());
                max = 127.0;
                cur = (double) channel.getToneVelocity();
                min = 0.0;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "exp:";
                value = String.format("%d", channel.getToneExpression());
                g2d.drawString(title, x, y);
                // g2d.drawString(value, x + valXGap, y);
                max = 127.0;
                cur = (double) channel.getToneExpression();
                min = 0.0;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "mod:";
                g2d.drawString(title, x, y);
                max = 127.0;
                cur = (double) channel.getModulator().getDepth();
                min = 0.0;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "pan:";
                g2d.drawString(title, x, y);
                max = channel.getFloatControl(FloatControl.Type.PAN).getMaximum();
                cur = channel.getFloatControl(FloatControl.Type.PAN).getValue();
                min = channel.getFloatControl(FloatControl.Type.PAN).getMinimum();
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                // y += rh;
                // g2d.setColor(Color.WHITE);
                // title = "NRPN:";
                // value = String.format("%d", channel.getNRPN(i));
                // g2d.drawString(title, x, y);
                // g2d.drawString(value, x + valXGap, y);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "pitch:";
                value = String.format("%.5f", channel.getTonePitch());
                g2d.drawString(title, x, y);
                max = channel.getPitchBendSenc();
                cur = channel.getTonePitch();
                min = -1.0 * channel.getPitchBendSenc();
                ;
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                g2d.setColor(Color.WHITE);
                y += rh;
                g2d.setColor(Color.WHITE);
                title = "volume:";
                g2d.drawString(title, x, y);
                max = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getMaximum();
                cur = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getValue();
                min = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getMinimum();
                drawBar(g, x + valXGap, y - 10, 10, cur, min, max);
                g2d.setColor(null);
                g2d.setFont(null);
            }
        }
    }
    
    private final static int CONTROL_DRG_ROW = 30;
    
    private final static int CONTROL_DRG_X = 50;
    private final static int CONTROL_DRG_Y = 50;
    
    private final static int CONTROL_DRG_BAR_X = 125;
    private final static int CONTROL_DRG_BAR_Y = CONTROL_DRG_Y + 45;
    private final static int CONTROL_DRG_BAR_WIDTH = 300;
    private final static int CONTROL_DRG_BAR_HEIGHT = 20;
    
    private final static int CONTROL_DRG_POLY_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_POLY_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 0);
    
    private final static int CONTROL_DRG_LEVEL_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_LEVEL_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 1);
    
    private final static int CONTROL_DRG_ENV_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_ENV_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 2);
    
    private final static int CONTROL_DRG_VEL_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_VEL_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 3);
    
    private final static int CONTROL_DRG_EXP_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_EXP_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 4);
    
    private final static int CONTROL_DRG_MOD_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_MOD_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 5);
    
    private final static int CONTROL_DRG_PAN_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_PAN_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 6);
    
    private final static int CONTROL_DRG_PIT_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_PIT_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 7);
    
    private final static int CONTROL_DRG_VOL_X = CONTROL_DRG_X + CONTROL_DRG_BAR_X;
    private final static int CONTROL_DRG_VOL_Y = CONTROL_DRG_BAR_Y + (CONTROL_DRG_ROW * 8);
    
    public void paintControlPanel(Graphics g, int ch) {
        if (ch == -1) {
            return;
        }
        final Font titleFont = new Font(Font.DIALOG, Font.PLAIN, 24);
        
        Graphics2D g2d = (Graphics2D) g;
        int drgX = CONTROL_DRG_X;
        int drgY = CONTROL_DRG_Y;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(drgX, drgY, getWidth() - drgX, getHeight() - drgY);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(drgX, drgY, getWidth() - (drgX * 2), getHeight() - (drgY * 2));
        g2d.drawRect(drgX+1, drgY+1, getWidth() - (drgX * 2) - 2, getHeight() - (drgY * 2) - 2);
        int rh = CONTROL_DRG_ROW;
        int x = drgX + 10;
        int y = drgY + 30;
        int width = 300;
        int barH = CONTROL_DRG_BAR_HEIGHT;
        int strOffset = 16;
        String title = "";
        String value = "";
        final int valXGap = 100;
        double max, min, cur;
        SoundSourceChannel channel = synth.getChannel(ch);
        Color waveColor = waveColorTable[channel.getChannel()];
        g2d.setColor(waveColor);
        g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
        g2d.drawString("CH" + (channel.getChannel() + 1), x, y);
        g2d.setColor(Color.WHITE);
        x += 15;
        y += 30;
        g2d.setFont(titleFont);
        g2d.setColor(Color.WHITE);
        title = "poly:";
        value = String.format("%d | %d", channel.getNumOfTones(), channel.getNumOfTonePool());
        g2d.drawString(title, x, y);
        Font f = g2d.getFont();
        g2d.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
        g2d.drawString(value, x + valXGap, y);
        g2d.setFont(f);
        y += rh;
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        title = "level:";
        g2d.drawString(title, x, y);
        max = 15.875;
        cur = (double) channel.getToneOverallLevel();
        min = 0.0;
        drawControlBar(g, CONTROL_DRG_LEVEL_X, CONTROL_DRG_LEVEL_Y, width, barH, cur, min, max);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "env:";
        // value = String.format("%.5f",
        // channel.getToneEnvelopeOffset());
        g2d.drawString(title, x, y);
        max = 1.0;
        cur = (double) channel.getToneEnvelopeOffset();
        min = 0.0;
        drawControlBar(g, CONTROL_DRG_ENV_X, CONTROL_DRG_ENV_Y, width, barH, cur, min, max);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "vel:";
        g2d.drawString(title, x, y);
        // value = String.format("%d", channel.getToneVelocity());
        max = 127.0;
        cur = (double) channel.getToneVelocity();
        min = 0.0;
        drawControlBar(g, CONTROL_DRG_VEL_X, CONTROL_DRG_VEL_Y, width, barH, cur, min, max);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "exp:";
        value = String.format("%d", channel.getToneExpression());
        g2d.drawString(title, x, y);
        // g2d.drawString(value, x + valXGap, y);
        max = 127.0;
        cur = (double) channel.getToneExpression();
        min = 0.0;
        drawControlBar(g, CONTROL_DRG_EXP_X, CONTROL_DRG_EXP_Y, width, barH, cur, min, max);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "mod:";
        g2d.drawString(title, x, y);
        max = 127.0;
        cur = (double) channel.getModulator().getDepth();
        min = 0.0;
        drawControlBar(g, CONTROL_DRG_MOD_X, CONTROL_DRG_MOD_Y, width, barH, cur, min, max);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "pan:";
        g2d.drawString(title, x, y);
        max = channel.getFloatControl(FloatControl.Type.PAN).getMaximum();
        cur = channel.getFloatControl(FloatControl.Type.PAN).getValue();
        min = channel.getFloatControl(FloatControl.Type.PAN).getMinimum();
        drawControlBar(g, CONTROL_DRG_PAN_X, CONTROL_DRG_PAN_Y, width, barH, cur, min, max);
        // y += rh;
        // g2d.setColor(Color.WHITE);
        // title = "NRPN:";
        // value = String.format("%d", channel.getNRPN(i));
        // g2d.drawString(title, x, y);
        // g2d.drawString(value, x + valXGap, y);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "pitch:";
        value = String.format("%.5f", channel.getTonePitch());
        g2d.drawString(title, x, y);
        max = channel.getPitchBendSenc();
        cur = channel.getTonePitch();
        min = -1.0 * channel.getPitchBendSenc();
        drawControlBar(g, CONTROL_DRG_PIT_X, CONTROL_DRG_PIT_Y, width, barH, cur, min, max);
        g2d.setColor(Color.WHITE);
        y += rh;
        g2d.setColor(Color.WHITE);
        title = "volume:";
        g2d.drawString(title, x, y);
        max = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getMaximum();
        cur = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getValue();
        min = channel.getFloatControl(FloatControl.Type.MASTER_GAIN).getMinimum();
        drawControlBar(g, CONTROL_DRG_VOL_X, CONTROL_DRG_VOL_Y, width, barH, cur, min, max);
        g2d.setColor(null);
        g2d.setFont(null);
    }

    public void paintWave(Graphics g, byte[] d, int ch) {
        Graphics2D g2d = (Graphics2D) g;
        if (d == null) {
            return;
        }
        if (d.length <= 2) {
            return;
        }

        if (visibleAuto == true) {
            for (int i = 0; i < d.length; i += 2) {
                if (d[i] != 0) {
                    visibleWave[ch] = true;
                    break;
                }
            }
        }

        // データ点描画
        Color waveColor = waveColorTable[ch];
        int xoffset = 0;
        int yCenter = this.getHeight() / 2;
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();
        int vvHeight = this.getHeight();

        int visibleIndex = ch;
        int col = 4;
        int row = 4;
        int cnt = 0;
        for (int i = 0; i < visibleWave.length; i++) {
            if (i == ch) {
                visibleIndex = cnt;
            }
            else {
            }

            if (isVisibleWave(i) == true) {
                cnt++;
            }
        }
        if (cnt == 0) {
            return;
        }

        switch (cnt) {
            case 1:
                row = 1;
                col = 1;
                break;
            case 2:
                row = 2;
                col = 1;
                break;
            case 3:
            case 4:
                row = 2;
                col = 2;
                break;
            case 5:
            case 6:
                row = 3;
                col = 2;
                break;
            case 7:
            case 8:
            case 9:
                row = 3;
                col = 3;
                break;
            case 10:
            case 11:
            case 12:
                row = 4;
                col = 3;
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            default:
                row = 4;
                col = 4;
                break;
        }

        vHeight /= row;
        vvHeight /= row;
        vWidth /= col;
        yCenter = (vHeight / 2) + (vHeight * (visibleIndex / col));
        xoffset = (vWidth) * (visibleIndex % col);

        if (isVisibleWave(ch) == true) {
            g2d.setColor(CENTER_COLOR);
            g2d.drawLine(xoffset, yCenter, xoffset + vWidth, yCenter);
        }

        if (d != null && d.length > 2) {
            int length = d.length / 2;
            double xDelta = (double) vWidth / (double) (length);
            double yDelta = vvHeight / 64.0;
            int j = 0;

            int CLIPPING_PX = (vHeight / 2);
            int xPoints[] = new int[length];
            int yPoints[] = new int[length];
            for (int i = 0; i < d.length; i += 2) {
                xPoints[j] = xoffset + (int) (xDelta * j);
                yPoints[j] = (int) (yDelta * d[i]) + yCenter;
                if (yPoints[j] > (yCenter + CLIPPING_PX)) {
                    yPoints[j] = (yCenter + CLIPPING_PX);
                }
                else if (yPoints[j] < (yCenter - CLIPPING_PX)) {
                    yPoints[j] = (yCenter - CLIPPING_PX);
                }
                j++;
            }
            if (isVisibleWave(ch) == true) {
                g2d.setColor(waveColor);
                g2d.setStroke(new BasicStroke(WAVE_BOLD, BasicStroke.CAP_ROUND, BasicStroke.CAP_ROUND));
                g2d.drawPolyline(xPoints, yPoints, length);
                g2d.setStroke(new BasicStroke());
            }
        }

        if (isVisibleWave(ch) == true) {
            if (traceViewMode != TRACE_VIEW_MODE_MERGE) {
                // g2d.setColor(Color.DARK_GRAY);
                // g2d.drawString("wave" + (ch + 1), xoffset + 5 + 1, (int)
                // (yCenter - (vHeight / 3)) + 1);
                // g2d.setColor(waveColor);
                // g2d.drawString("wave" + (ch + 1), xoffset + 5, (int) (yCenter
                // - (vHeight / 3)));
                int x = 5 + ((visibleIndex % col) * vWidth);
                int y = 15 + ((visibleIndex / col) * vHeight);
                g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString("CH" + (ch + 1), x + 1, y + 1);
                g2d.setColor(waveColor);
                g2d.drawString("CH" + (ch + 1), x, y);
                g2d.setFont(null);
            }
            g2d.setStroke(new BasicStroke(1.5f));
            for (int i = 1; i <= 3; i++) {
                g2d.setColor(Color.WHITE);
                g2d.drawLine(vWidth * i, 0, vWidth * i, getHeight());
                g2d.drawLine(0, vHeight * i, getWidth(), vHeight * i);
            }
            g2d.setStroke(new BasicStroke());
        }
    }

    public void setDefaultColorTable() {
        for (int i = 0; i < waveColorTable.length; i++) {
            this.waveColorTable[i] = DEFAULT_COLOR;
        }
        ingStrColor = Color.LIGHT_GRAY;
        ingRowColor = Color.LIGHT_GRAY;
        ingMidColor = Color.LIGHT_GRAY;
        ingCurColor = Color.GRAY;
        ingNomColor = Color.LIGHT_GRAY;
    }

    public void setRestoreColorTable() {
        for (int i = 0; i < waveColorTable.length; i++) {
            this.waveColorTable[i] = this.backupWaveColorTable[i];
        }
        ingStrColor = DEFAULT_COLOR;
        ingRowColor = Color.BLUE;
        ingMidColor = Color.ORANGE;
        ingCurColor = Color.MAGENTA;
        ingNomColor = Color.WHITE;
    }

    public void setWaveColorTable(Color[] waveColorTable) {
        for (int i = 0; i < waveColorTable.length; i++) {
            this.waveColorTable[i] = waveColorTable[i];
            this.backupWaveColorTable[i] = waveColorTable[i];
        }
        ingStrColor = DEFAULT_COLOR;
        ingRowColor = Color.BLUE;
        ingMidColor = Color.ORANGE;
        ingCurColor = Color.MAGENTA;
        ingNomColor = Color.WHITE;
    }

    int width, height; // グラフ描画領域の幅と高さ
    Insets margin = new Insets(17, 40, 20, 30); // 上, 左, 下, 右

    int nx(double x) {
        return (int) Math.round(margin.left + x * width);
    }

    int ny(double y) {
        return (int) Math.round(margin.top + (1 - y) * height);
    }

    int yVal(double v) {
        return ny((v - vMin) / (vMax - vMin));
    }

    double vMax = 0.0, vMin = 0.0;
    static double[] spectrumBuf;

    class Complex {
        double r, i;

        public Complex(double re, double im) {
            r = re;
            i = im;
        }
    }

    private double[] DFT(byte[] data) {
        double Pi = Math.PI;
        int N = data.length;
        Complex[] x = new Complex[N];
        double[] amp = new double[N / 2]; // 振幅スペクトル
        for (int i = 0; i < N; i++) {
            x[i] = new Complex((data[i] & 0xFF) - 128, 0);
        }
        for (int n = 0; n < N / 2; n++) {
            double r = 0.0, i = 0.0;
            for (int k = 0; k < N; k++) {
                r += x[k].r * Math.cos(2.0 * Pi * n * k / N) + x[k].i * Math.sin(2.0 * Pi * n * k / N);
                i += x[k].i * Math.cos(2.0 * Pi * n * k / N) - x[k].r * Math.sin(2.0 * Pi * n * k / N);
            }
            amp[n] = Math.sqrt(r * r + i * i); // 振幅スペクトル
            if (amp[n] > vMax)
                vMax = amp[n];
        }
        return amp;
    }

    private void tick(Graphics g, int nX, int nY) {
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getAscent(); // 文字の高さ
        double freq1 = SoundSourceChannel.SAMPLE_RATE / spectrumBuf.length / 2;
        g.setColor(Color.WHITE);
        for (int d = 0; d <= nX; d++) {
            double x = (double) d / nX;
            double freq = freq1 * x * spectrumBuf.length;
            // double time = x * data.length / af.getSampleRate();
            g.drawLine(nx(x), ny(0), nx(x), ny(0) + 3);
            String str = String.format("%.2f", freq / 1000);
            if (d == nX)
                str += "KHz";
            int w = fm.stringWidth(str);
            g.drawString(str, nx(x) - w / 2, ny(0) + h + 2);
        }
        for (int n = 0; n <= nY; n++) {
            double y = (double) n / nY;
            g.setColor(Color.lightGray);
            g.drawLine(nx(0), ny(y), nx(1.0), ny(y));
            String str = String.format("%.0f", vMin + (vMax - vMin) * y);
            int w = fm.stringWidth(str);
            g.setColor(Color.WHITE);
            g.drawString(str, nx(0) - w - 5, ny(y) + h / 2);
        }
    }

    public void paintSpectrum(Graphics g) {
        width = getWidth() - margin.left - margin.right;
        height = getHeight() - margin.top - margin.bottom;
        g.drawRect(margin.left, margin.top, width - 1, height - 1); // 座標軸描画
        if (d1 == null) {
            return;
        }
        if (hispeedSpectrum == true) {
            hWaveBuf = Arrays.copyOf(d1, d1.length);
            if (hSpetrumBuf == null) {
                return;
            }
            spectrumBuf = Arrays.copyOf(hSpetrumBuf, hSpetrumBuf.length);
        }
        else {
            spectrumBuf = DFT(d1);
        }

        if (spectrumBuf == null) {
            return;
        }
        tick(g, 10, 5);
        g.setColor(Color.red);
        for (int n = 0; n < spectrumBuf.length; n++) {
            double x = (double) n / spectrumBuf.length;
            g.drawLine(nx(x), ny(0), nx(x), yVal(spectrumBuf[n]));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        hispeedSpectrumRunnable = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        if (CONTROL_DRG_POLY_X <= mx && mx <= CONTROL_DRG_POLY_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_POLY_Y <= my && my <= CONTROL_DRG_POLY_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_LEVEL_X <= mx && mx <= CONTROL_DRG_LEVEL_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_LEVEL_Y <= my && my <= CONTROL_DRG_LEVEL_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_ENV_X <= mx && mx <= CONTROL_DRG_ENV_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_ENV_Y <= my && my <= CONTROL_DRG_ENV_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_VEL_X <= mx && mx <= CONTROL_DRG_VEL_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_VEL_Y <= my && my <= CONTROL_DRG_VEL_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_EXP_X <= mx && mx <= CONTROL_DRG_EXP_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_EXP_Y <= my && my <= CONTROL_DRG_EXP_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_MOD_X <= mx && mx <= CONTROL_DRG_MOD_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_MOD_Y <= my && my <= CONTROL_DRG_MOD_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_PAN_X <= mx && mx <= CONTROL_DRG_PAN_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_PAN_Y <= my && my <= CONTROL_DRG_PAN_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_PIT_X <= mx && mx <= CONTROL_DRG_PIT_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_PIT_Y <= my && my <= CONTROL_DRG_PIT_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        else if (CONTROL_DRG_VOL_X <= mx && mx <= CONTROL_DRG_VOL_X + CONTROL_DRG_BAR_WIDTH 
                && CONTROL_DRG_VOL_Y <= my && my <= CONTROL_DRG_VOL_Y + CONTROL_DRG_BAR_HEIGHT) {
            
        }
        
        pressedX = mx;
        pressedY = my;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {        
    }
}
