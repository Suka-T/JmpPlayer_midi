package jmp.gui;

import jlib.gui.IJmpWindow;
import jmsynth.app.component.WaveViewerFrame;
import jmsynth.midi.MidiInterface;

public class BuiltinSynthSetupDialog extends WaveViewerFrame implements IJmpWindow {
    /**
     * Create the frame.
     */
    public BuiltinSynthSetupDialog(MidiInterface iface) {
        super(iface);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void showWindow() {
        setVisible(true);
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
    public void setDefaultWindowLocation() {

    }

    @Override
    public void repaintWindow() {
        repaint();
    }

}
