package gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fmp.FlagMediaPlayer;

public class FlagMediaPlayerWindow extends JFrame {

    private boolean singleMode;
    private MediaPanel mp = null;
    private JPanel contentPane;

    /**
     * Create the frame.
     */
    public FlagMediaPlayerWindow(MediaPanel mp) {
        init(mp, false);

    }

    public FlagMediaPlayerWindow(MediaPanel mp, boolean single) {
        init(mp, single);
    }

    private void init(MediaPanel mp, boolean single) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        this.mp = mp;
        this.singleMode = single;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (isVisible() == true) {
            FlagMediaPlayer.ActiveWindow = this;
        }
        else {
            FlagMediaPlayer.ActiveWindow = null;
        }
    }

    public void exitResource() {
        if (mp == null) {
            return;
        }
        mp.exit();
    }

    public MediaPanel getMediaPanel() {
        return mp;
    }

}
