package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import fmp.FlagMediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FMPMainWindow extends JFrame {

    private List<FlagMediaPlayerWindow> frames = new LinkedList<FlagMediaPlayerWindow>();
    private JPanel contentPane;
    protected Thread th;
    protected boolean isRunnable = true;
    private JSlider slider;
    private JLabel lblTime;

    /**
     * Create the frame.
     */
    public FMPMainWindow() {
        setResizable(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FlagMediaPlayer.exit();
            }
        });
        th = new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRunnable) {
                    updateLabel();
                }
            }
        });
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JButton btnPlay = new JButton("PLAY");
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FlagMediaPlayer.ActiveWindow != null) {
                    MediaPanel mp = FlagMediaPlayer.ActiveWindow.getMediaPanel();
                    MediaPlayer player = mp.getPlayer();
                    player.play();
                }
            }
        });
        btnPlay.setBounds(12, 10, 91, 21);
        panel.add(btnPlay);

        JButton btnPause = new JButton("PAUSE");
        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FlagMediaPlayer.ActiveWindow != null) {
                    MediaPanel mp = FlagMediaPlayer.ActiveWindow.getMediaPanel();
                    MediaPlayer player = mp.getPlayer();
                    player.pause();
                    System.out.println("" + player.getCurrentTime().toSeconds());
                }
            }
        });
        btnPause.setBounds(12, 39, 91, 21);
        panel.add(btnPause);

        JButton btnStop = new JButton("STOP");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FlagMediaPlayer.ActiveWindow != null) {
                    MediaPanel mp = FlagMediaPlayer.ActiveWindow.getMediaPanel();
                    MediaPlayer player = mp.getPlayer();
                    player.stop();
                }
            }
        });
        btnStop.setBounds(12, 70, 91, 21);
        panel.add(btnStop);

        slider = new JSlider();
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (FlagMediaPlayer.ActiveWindow != null) {
                    MediaPanel mp = FlagMediaPlayer.ActiveWindow.getMediaPanel();
                    MediaPlayer player = mp.getPlayer();
                    player.pause();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (FlagMediaPlayer.ActiveWindow != null) {
                    MediaPanel mp = FlagMediaPlayer.ActiveWindow.getMediaPanel();
                    MediaPlayer player = mp.getPlayer();
                    System.out.println("" + slider.getValue());
                    player.seek(javafx.util.Duration.seconds(slider.getValue()));
                    player.play();
                }
            }
        });
        slider.setBounds(12, 225, 410, 26);
        panel.add(slider);

        lblTime = new JLabel("New label");
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTime.setBounds(311, 202, 111, 13);
        panel.add(lblTime);

        setTransferHandler(new DropFileCallbackHandler(new IDropFileCallback() {

            @Override
            public void catchDropFile(File file) {
                try {
                    createMediaWindow(file);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        th.start();
    }

    public void createMediaWindow(File file) throws Exception {
        if (file.canRead() == true) {
            MediaPanel mp = new MediaPanel(file, null);

            FlagMediaPlayerWindow win = new FlagMediaPlayerWindow(mp);

            Media media = mp.getMedia();
            MediaPlayer player = mp.getPlayer();

            // 読み込み待ち
            for (int i = 0; player.getStatus() != MediaPlayer.Status.READY; i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                }
                if (i > 20) {
                    win.dispose();
                    throw new Exception("動画読み込みタイムアウト");
                }
            }

            frames.add(win);

            // 読み込み完了後なら動画サイズを取得できる
            int videoW = media.getWidth();
            int videoH = media.getHeight();

            // MoviePanelのサイズを動画に合わせてJFrameに追加
            mp.setPreferredSize(new Dimension(videoW, videoH));
            win.getContentPane().add(mp);

            // JFrame側のパネルサイズを動画に合わせる
            win.getContentPane().setPreferredSize(new Dimension(videoW, videoH));

            // JFrameサイズをパネル全体が見えるサイズに自動調整
            win.pack();

            // 中心に表示
            win.setLocationRelativeTo(null);
            win.setVisible(true);

            mp.threadStart();
        }
    }

    @Override
    public void dispose() {
        isRunnable = false;
        for (FlagMediaPlayerWindow fr : frames) {
            if (fr != null) {
                fr.getMediaPanel().exit();
                fr.setVisible(false);
                fr.dispose();
            }
        }
        super.dispose();
    }

    public void updateLabel() {
        int curTime = 0;
        int totalTime = 0;
        if (FlagMediaPlayer.ActiveWindow != null) {
            MediaPlayer player = FlagMediaPlayer.ActiveWindow.getMediaPanel().getPlayer();
            curTime = (int) player.getCurrentTime().toSeconds();
            totalTime = (int) player.getTotalDuration().toSeconds();
        }

        String curText = String.format("%02d:%02d", curTime / 60, curTime % 60);
        String totalText = String.format("%02d:%02d", totalTime / 60, totalTime % 60);
        lblTime.setText(String.format("%s/%s", curText, totalText));
        slider.setMinimum(0);
        slider.setMaximum(totalTime);
        slider.setValue(curTime);

    }
}
