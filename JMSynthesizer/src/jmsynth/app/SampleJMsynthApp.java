package jmsynth.app;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import jmsynth.JMSynthApplication;
import jmsynth.JMSynthEngine;

public class SampleJMsynthApp {

    public static JMSynthApplication APPLI;

    private static JFileChooser fileChooser = null;

    public static void main(String[] args) {
        try {
            APPLI = JMSynthEngine.getJMSynthApplication();
        }
        catch (MidiUnavailableException e1) {
            e1.printStackTrace();
            System.exit(1);
            return;
        }

        APPLI.getAppWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        APPLI.getAppWindow().setAutoRepaintTask(true);
        APPLI.getAppWindow().addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                APPLI.close();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }
        });
        APPLI.getAppWindow().addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                }
                int ret = fileChooser.showDialog(APPLI.getAppWindow(), "load");
                if (ret != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File f = fileChooser.getSelectedFile();
                if (f == null) {
                    return;
                }

                try {
                    APPLI.loadMidiFile(f);
                    APPLI.getSequencer().start();
                }
                catch (InvalidMidiDataException e1) {
                    // TODO 自動生成された catch ブロック
                    e1.printStackTrace();
                }
                catch (IOException e1) {
                    // TODO 自動生成された catch ブロック
                    e1.printStackTrace();
                }
            }
        });

        APPLI.getAppWindow().setVisible(true);
    }

}
