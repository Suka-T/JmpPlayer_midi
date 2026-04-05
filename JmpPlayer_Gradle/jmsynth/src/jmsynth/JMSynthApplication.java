package jmsynth;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import jmsynth.app.component.WaveViewerFrame;
import jmsynth.midi.MidiInterface;

public class JMSynthApplication {
    private JMSoftSynthesizer synth;
    private MidiInterface iface;
    private WaveViewerFrame appWindow;
    private Sequencer sequencer;

    public JMSynthApplication() {
        synth = new JMSoftSynthesizer();
        iface = new MidiInterface(synth);
        appWindow = new WaveViewerFrame(iface);
    }

    public void open() throws MidiUnavailableException {
        iface.open();

        sequencer = MidiSystem.getSequencer(false);
        sequencer.open();
        sequencer.getTransmitter().setReceiver(iface);
    }

    public void close() {
        sequencer.close();
        iface.close();
    }

    public JMSoftSynthesizer getSoftSynthesizer() {
        return synth;
    }

    public MidiInterface getMidiInterface() {
        return iface;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public WaveViewerFrame getAppWindow() {
        return appWindow;
    }

    public void loadMidiFile(File f) throws InvalidMidiDataException, IOException {
        if (sequencer != null) {
            if (sequencer.isRunning() == true) {
                try {
                    sequencer.stop();
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                }
            }

            Sequence seq = MidiSystem.getSequence(f);
            sequencer.setSequence(seq);
        }
    }

}
