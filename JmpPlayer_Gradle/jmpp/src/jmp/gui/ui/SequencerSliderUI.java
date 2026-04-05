package jmp.gui.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

import function.Utility;
import jmp.JMPFlags;
import jmp.core.JMPCore;

public class SequencerSliderUI extends BasicSliderUI {
    public static final int THUMB_SIZE = 9;
    public static final Color BLINK_COLOR_1 = Utility.convertColorAlpha(Color.WHITE, 165);
    public static final Color BLINK_COLOR_2 = Color.WHITE;

    public static final Color TRACK_BORDER_COLOR = Color.WHITE;
    public static final Color TRACK_BACK_COLOR = Utility.convertColorAlpha(Color.WHITE, 100);

    public SequencerSliderUI(JSlider b) {
        super(b);
    }

    @Override
    public void paintThumb(Graphics g) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            // 色をトグルさせる
            Color color = (JMPFlags.PlayingTimerToggleFlag == false) ? BLINK_COLOR_1 : BLINK_COLOR_2;
            if (JMPCore.getSoundManager().isPlay() == false) {
                color = BLINK_COLOR_1;
            }

            Rectangle knobBounds = thumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;
            g.translate(knobBounds.x, knobBounds.y);
            g.setColor(color);
            // g.drawLine((w / 2) - 1, 0, (w / 2) - 1, h);
            // g.drawLine((w / 2), 0, (w / 2), h);
            // g.drawLine((w / 2) + 1, 0, (w / 2) + 1, h);
            g.fillRect((w / 2) - (THUMB_SIZE / 2), 0, (w / 2) + (THUMB_SIZE / 2), h - 1);
            g.setColor(BLINK_COLOR_2);
            g.drawRect((w / 2) - (THUMB_SIZE / 2), 0, (w / 2) + (THUMB_SIZE / 2), h - 1);
            g.translate(-knobBounds.x, -knobBounds.y);
        }
        else {
            super.paintThumb(g);
        }
    }

    @Override
    public void paintTrack(Graphics g) {
        // super.paintTrack(g);
        Rectangle trackBounds = trackRect;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int cy = (trackBounds.height / 2) - 2;
            int cw = trackBounds.width;

            final int TRACK_HEIGHT = 3;
            g.translate(trackBounds.x, trackBounds.y + cy);
            g.setColor(TRACK_BACK_COLOR);
            g.fillRect(0, 0, cw, TRACK_HEIGHT);
            g.setColor(TRACK_BORDER_COLOR);
            g.drawRect(0, 0, cw, TRACK_HEIGHT);
            g.translate(-trackBounds.x, -(trackBounds.y + cy));
        }
        else {
            super.paintTrack(g);
        }
    }

}
