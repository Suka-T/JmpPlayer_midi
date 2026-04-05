package jmsynth.app;

import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JFrame;
import javax.swing.TransferHandler;

import jmsynth.JMSoftSynthesizer;
import jmsynth.app.component.WaveViewerFrame;
import jmsynth.midi.MidiInterface;

public class TestDialog2 extends WaveViewerFrame {

    public class DropFileCallbackHandler extends TransferHandler {
        public DropFileCallbackHandler() {
            super();
        }

        /**
         * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
         */
        @Override
        public boolean canImport(TransferSupport support) {
            if (support.isDrop() == false) {
                // ドロップ操作でない場合は受け取らない
                return false;
            }

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
                // ファイルでない場合は受け取らない
                return false;
            }

            return true;
        }

        /**
         * ドロップされたアイテムを受け取る
         */
        @Override
        public boolean importData(TransferSupport support) {
            // ドロップアイテム受理の確認
            if (canImport(support) == false) {
                return false;
            }

            // ドロップ処理
            Transferable t = support.getTransferable();
            try {
                // ドロップアイテム取得
                Object item = t.getTransferData(DataFlavor.javaFileListFlavor);

                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) item;

                // 一番先頭のファイルを取得
                if ((files != null) && (files.size() > 0)) {
                    String path = files.get(0).getPath();
                    File file = new File(path);
                    loadFile(file);
                }
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
        }
    }

    private static JMSoftSynthesizer SoftSynthesizer;
    private static Sequencer SoftSequencer;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Startup();
                    TestDialog2 frame = new TestDialog2();
                    frame.setupGui();
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestDialog2() {
        super(SoftSynthesizer);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SoftSynthesizer.closeDevice();
                SoftSequencer.close();
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SoftSequencer.getSequence() == null) {
                    return;
                }

                if (SoftSequencer.isRunning() == true) {
                    SoftSequencer.stop();
                }
                else {
                    SoftSequencer.start();
                }
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
            }
        });
    }

    public void loadFile(File f) {
        if (SoftSequencer != null) {
            if (SoftSequencer.isRunning() == true) {
                try {
                    SoftSequencer.stop();
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                }
            }

            try {
                Sequence seq = MidiSystem.getSequence(f);
                SoftSequencer.setSequence(seq);
                SoftSequencer.start();
            }
            catch (InvalidMidiDataException e1) {
                e1.printStackTrace();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void Startup() {
        SoftSynthesizer = new JMSoftSynthesizer();
        SoftSynthesizer.openDevice();

        if (SoftSequencer == null) {
            try {
                SoftSequencer = MidiSystem.getSequencer(false);
                SoftSequencer.open();

                SoftSequencer.getTransmitter().setReceiver(new MidiInterface(SoftSynthesizer));
                // for (int i=0; i<16; i++) {
                // SoftSequencer.getTransmitter().setReceiver(new
                // MidiInterface(SoftSynthesizer.getChannel(i)));
                // }
            }
            catch (MidiUnavailableException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }
    }

    public void setupGui() {
        this.setTransferHandler(new DropFileCallbackHandler());
    }

}
