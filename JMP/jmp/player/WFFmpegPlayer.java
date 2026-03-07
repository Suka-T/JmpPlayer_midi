package jmp.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import function.Platform;
import jlib.core.ISystemManager;
import jlib.player.Player;
import jmp.core.JMPCore;
import player.FFmpegMusicPlayer;
import player.FFmpegVideoPlayer;
import player.VideoPanel;

public class WFFmpegPlayer extends Player implements IMoviePlayerModel {

    private class VideoPlayerFrame extends JFrame {

        public VideoPanel videoPanel;

        public VideoPlayerFrame() {

            setTitle("JMP Video Player");
            setSize(1280, 720);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setLayout(new BorderLayout());

            // 動画描画パネル
            videoPanel = new VideoPanel(800, 500);
            videoPanel.setBackground(Color.black);

            add(videoPanel, BorderLayout.CENTER);

            this.addWindowListener(new WindowListener() {

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
                    setVisible(false);
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }
            });

            this.addComponentListener(new ComponentListener() {

                @Override
                public void componentShown(ComponentEvent e) {
                }

                @Override
                public void componentResized(ComponentEvent e) {
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                }
            });
        }
    }

    private static final String MEDIA_TYPE_AUDIO = "audio";
    private static final String MEDIA_TYPE_VIDEO = "video";

    private VideoPlayerFrame frame = null;
    private FFmpegMusicPlayer ffmpegMP = null;
    private FFmpegVideoPlayer ffmpegVP = null;

    private String mediaType = "";
    private String stFfmpegCommand = "";
    private String stFfprobeCommand = "";

    private boolean loadFlag = false;
    
    private boolean validMoviePlayer = true;

    public WFFmpegPlayer() {
        ffmpegMP = new FFmpegMusicPlayer();

        frame = new VideoPlayerFrame();
        ffmpegVP = new FFmpegVideoPlayer(frame.videoPanel, ffmpegMP);
    }

    @Override
    public void play() {
        if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
            ffmpegMP.preload();
            ffmpegMP.play();
        }
        else {
            if (isVisibleView() == false) {
                setVisibleView(true);
            }

            ffmpegMP.play();
            ffmpegVP.play();
        }
    }

    @Override
    public void stop() {
        if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
            ffmpegMP.stop();
        }
        else {
            ffmpegMP.stop();
            ffmpegVP.stop();
        }
    }

    @Override
    public boolean isRunnable() {
        return ffmpegMP.isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
            ffmpegMP.setPosition(pos);
        }
        else {
            ffmpegMP.setPosition(pos);
            ffmpegVP.setPosition(pos);
        }
    }

    @Override
    public long getPosition() {
        return ffmpegMP.getPosition();
    }

    @Override
    public long getLength() {
        return ffmpegMP.getLength();
    }

    @Override
    public boolean isValid() {
        if (loadFlag) {
            return false;
        }

        return ffmpegMP.isValid();
    }

    @Override
    public int getPositionSecond() {
        return ffmpegMP.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return ffmpegMP.getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        ffmpegMP.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return ffmpegMP.getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        loadFlag = true;
        updatePath();

        // 動画非対応
        mediaType = MEDIA_TYPE_AUDIO;
        if (hasVideoStream(file)) {
            mediaType = MEDIA_TYPE_VIDEO;
        }
        else {
            frame.setVisible(false);
            mediaType = MEDIA_TYPE_AUDIO;
        }
        loadFlag = false;

        ffmpegMP.setFFmpegCommand(stFfmpegCommand);
        ffmpegMP.setFfprobeCommand(stFfprobeCommand);

        ffmpegVP.setFFmpegCommand(stFfmpegCommand);
        ffmpegVP.setFfprobeCommand(stFfprobeCommand);

        if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
            ffmpegMP.setMusicFile(file);
        }
        else {
            ffmpegMP.setMusicFile(file);
            ffmpegVP.setVideoFile(file);
        }
        return true;
    }

    private void updatePath() {

        String ffmpegCommand = "ffmpeg";
        switch (Platform.getRunPlatform()) {
            case WINDOWS:
                ffmpegCommand = JMPCore.getSystemManager().getCommonRegisterValue(ISystemManager.COMMON_REGKEY_NO_FFMPEG_WIN);
                break;
            case MAC:
                ffmpegCommand = JMPCore.getSystemManager().getCommonRegisterValue(ISystemManager.COMMON_REGKEY_NO_FFMPEG_MAC);
                break;
            case LINUX:
            case SUN_OS:
            case OTHER:
            default:
                ffmpegCommand = JMPCore.getSystemManager().getCommonRegisterValue(ISystemManager.COMMON_REGKEY_NO_FFMPEG_OTHER);
                break;
        }

        String ffmpegPath = JMPCore.getDataManager().getFFmpegPath();
        boolean isFFmpegInstalled = JMPCore.getDataManager().isFFmpegInstalled();
        if (isFFmpegInstalled == true) {
            boolean isExistsCurrent = false;
            File ffmpegFile = new File("ffmpeg.exe");
            if (ffmpegPath.isEmpty() == false && ffmpegFile.exists() == true) {
                isExistsCurrent = true;
            }

            if (isExistsCurrent == true) {
                // カレントに有効なffmpegがあるときはそちらを優先的に使用する
                stFfmpegCommand = "ffmpeg.exe";
                stFfprobeCommand = replaceLastFFmpeg(stFfmpegCommand);
            }
            else {
                stFfmpegCommand = ffmpegCommand;
                stFfprobeCommand = replaceLastFFmpeg(stFfmpegCommand);
            }
        }
        else {
            stFfmpegCommand = ffmpegPath;
            stFfprobeCommand = replaceLastFFmpeg(stFfmpegCommand);
        }
    }

    public boolean hasVideoStream(File file) {
        
        if (isValidMoviePlayer() == false) {
            return false;
        }

        try {

            ProcessBuilder pb = new ProcessBuilder(stFfprobeCommand, "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=codec_name", "-of",
                    "csv=p=0", file.getAbsolutePath());

            Process p = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String codec = br.readLine();

            p.waitFor();

            if (codec == null)
                return false;

            // mp3アルバムアート除外
            if (codec.equals("mjpeg") || codec.equals("png"))
                return false;

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String replaceLastFFmpeg(String path) {

        int index = path.lastIndexOf("ffmpeg");

        if (index == -1)
            return path;

        return path.substring(0, index) + "ffprobe" + path.substring(index + 6);
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

    @Override
    public void changingPlayer() {
        setVisibleView(false);
        super.changingPlayer();
    }

    @Override
    public void setVisibleView(boolean visible) {
        frame.setVisible(visible);
    }

    @Override
    public boolean isValidView() {
        return (mediaType.equals(MEDIA_TYPE_VIDEO) && ffmpegVP.isValid());
    }

    @Override
    public boolean isVisibleView() {
        return frame.isVisible();
    }

    public boolean isValidMoviePlayer() {
        return validMoviePlayer;
    }

    public void setValidMoviePlayer(boolean validMoviePlayer) {
        this.validMoviePlayer = validMoviePlayer;
    }

}
