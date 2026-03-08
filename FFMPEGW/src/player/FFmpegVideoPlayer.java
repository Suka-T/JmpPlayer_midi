package player;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FFmpegVideoPlayer {
    class Frame {

        byte[] pixels;
        double timestamp;

        Frame(byte[] pixels, double ts) {
            this.pixels = pixels;
            this.timestamp = ts;
        }
    }

    private File file;

    private Process ffmpeg;

    private volatile boolean running;

    private Thread decodeThread;
    private Thread renderThread;

    private VideoPanel panel;

    private FFmpegMusicPlayer musicPlayer;

    private BlockingQueue<Frame> queue = new ArrayBlockingQueue<>(60);

    private int width = 640;
    private int height = 360;

    private int videoWidth = 640;
    private int videoHeight = 360;
    private int rotate = 0;

    private int FPS = 30;

    private long position = 0;
    private long length = -1;

    private float volume = 1.0f;

    private String ffmpegCommand = "ffmpeg";
    private String ffprobeCommand = "ffprobe";

    public FFmpegVideoPlayer(VideoPanel panel, FFmpegMusicPlayer music) {
        this.panel = panel;
        this.musicPlayer = music;
        rotate = 0;
    }

    public void setVideoFile(File file) {

        stop();

        this.file = file;
        this.position = 0;

        queue.clear();

        loadVideoSize();
        loadRotation();

        if (rotate == 90 || rotate == 270) {
            int tmp = videoWidth;
            videoWidth = videoHeight;
            videoHeight = tmp;
        }
        panel.setVideoSize(videoWidth, videoHeight);
        panel.setInitFrame();
    }

    private void loadVideoSize() {
        try {
            Process p = new ProcessBuilder(ffprobeCommand, //
                    "-v", "error", //
                    "-select_streams", "v:0", //
                    "-show_entries", "stream=width,height", //
                    "-of", "csv=p=0", //
                    file.getAbsolutePath()//
            ).start();//

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = br.readLine();
            String[] parts = line.split(",");

            videoWidth = Integer.parseInt(parts[0]);
            videoHeight = Integer.parseInt(parts[1]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRotation() {

        rotate = 0;

        try {

            Process p = new ProcessBuilder(ffprobeCommand, "-v", "error", "-select_streams", "v:0", "-show_entries", "stream_tags=rotate", "-of",
                    "default=noprint_wrappers=1", file.getAbsolutePath()).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("TAG:rotate=")) {
                    rotate = Integer.parseInt(line.substring(11));
                    break;
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startFFmpeg(long frame) throws IOException {

        double sec = frame / (double) FPS;

        ProcessBuilder pb = new ProcessBuilder(ffmpegCommand, //
                "-ss", String.valueOf(sec), //
                "-i", file.getAbsolutePath(), //
                "-vf", "fps=" + FPS + ",scale=" + width + ":" + height, //
                "-pix_fmt", //
                "bgr24", "-f", //
                "rawvideo", "-an", "-"//
        );//

        ffmpeg = pb.start();

        discardErrorStream(ffmpeg);
    }

    public void play() {

        if (running || file == null)
            return;

        running = true;

        try {
            startFFmpeg(position);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        startDecodeThread();
        startRenderThread();
    }

    private void startDecodeThread() {

        decodeThread = new Thread(() -> {

            try {

                InputStream is = new BufferedInputStream(ffmpeg.getInputStream(), 65536);

                int frameSize = width * height * 3;

                byte[] buffer = new byte[frameSize];

                long frameIndex = position;

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

                while (running) {

                    int read = readFully(is, buffer);

                    if (read != frameSize)
                        break;

                    System.arraycopy(buffer, 0, pixels, 0, frameSize);

                    double ts = frameIndex / (double) FPS;

                    queue.put(new Frame(buffer.clone(), ts));

                    frameIndex++;
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        });

        decodeThread.start();
    }

    private void startRenderThread() {

        renderThread = new Thread(() -> {

            try {

                while (running) {

                    Frame frame = queue.poll();

                    if (frame == null)
                        continue;

                    double audioTime = musicPlayer.getPosition() / 44100.0;

                    double diff = frame.timestamp - audioTime;

                    if (diff > 0) {

                        Thread.sleep((long) (diff * 1000));
                    }

                    if (diff < -0.05)
                        continue;

                    panel.setFrame(frame.pixels, width, height);

                    position = (long) (frame.timestamp * FPS);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        });

        renderThread.start();
    }

    private int readFully(InputStream is, byte[] buf) throws IOException {

        int total = 0;

        while (total < buf.length) {

            int r = is.read(buf, total, buf.length - total);

            if (r == -1)
                break;

            total += r;
        }

        return total;
    }

    private void discardErrorStream(Process p) {

        new Thread(() -> {

            try (InputStream es = p.getErrorStream()) {

                byte[] buf = new byte[1024];

                while (es.read(buf) != -1) {
                }

            }
            catch (Exception ignored) {
            }

        }).start();
    }

    public void stop() {

        running = false;

        if (renderThread != null)
            renderThread.interrupt();

        if (ffmpeg != null)
            ffmpeg.destroy();

        queue.clear();

        if (renderThread != null && Thread.currentThread() != renderThread) {
            try {
                renderThread.join();
            }
            catch (InterruptedException e) {
            }
        }
    }

    public boolean isRunnable() {
        return running;
    }

    public void setPosition(long pos) {

        position = pos;

        if (running) {

            stop();
            play();
        }
    }

    public long getPosition() {
        return position;
    }

    public long getLength() {
        return length;
    }

    public boolean isValid() {
        return file != null && file.exists();
    }

    public int getPositionSecond() {
        return (int) (position / FPS);
    }

    public int getLengthSecond() {
        return (int) (length / FPS);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    public String getFFmpegCommand() {
        return ffmpegCommand;
    }

    public void setFFmpegCommand(String ffmpegCommand) {
        this.ffmpegCommand = ffmpegCommand;
    }

    public String getFfprobeCommand() {
        return ffprobeCommand;
    }

    public void setFfprobeCommand(String ffprobeCommand) {
        this.ffprobeCommand = ffprobeCommand;
    }
    
    public void setQuality(int width, int height) {
        this.width = width;
        this.height = height;
    }
}