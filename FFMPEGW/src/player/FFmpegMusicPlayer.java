package player;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class FFmpegMusicPlayer {

    private File file;

    private Process ffmpeg;
    private SourceDataLine line;

    private volatile boolean running = false;

    private long position = 0;
    private long length = -1;

    private float volume = 1.0f;

    private Thread playThread;

    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNELS = 2;

    private String ffmpegCommand = "ffmpeg";
    private String ffprobeCommand = "ffprobe";

    public FFmpegMusicPlayer() {
    }

    public void setMusicFile(File f) throws Exception {
        this.file = f;

        double duration = getDuration(f);

        position = 0;
        length = (long) (duration * SAMPLE_RATE);
    }

    public double getDuration(File file) throws Exception {

        // position取得 
        ProcessBuilder pb = new ProcessBuilder(ffprobeCommand, "-v", "error", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1",
                file.getAbsolutePath());

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine();

        process.waitFor();

        if (line == null)
            return -1;

        return Double.parseDouble(line);
    }

    public void preload() {
        try {
            startFFmpeg(position);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {

        if (running)
            return;

        running = true;

        playThread = new Thread(() -> {
            try {

                preload();

                InputStream is = new BufferedInputStream(ffmpeg.getInputStream(), 65536);

                AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, CHANNELS, true, false);

                line = AudioSystem.getSourceDataLine(format);

                line.open(format, 65536);
                line.start();

                int frameSize = CHANNELS * 2;

                byte[] buffer = new byte[16384];
                byte[] data = new byte[16384];
                byte[] remain = new byte[4];
                int remainSize = 0;

                while (running) {

                    int read = is.read(buffer);

                    if (read == -1)
                        break;

                    int total = remainSize + read;

                    // byte[] data = new byte[total];

                    System.arraycopy(remain, 0, data, 0, remainSize);
                    System.arraycopy(buffer, 0, data, remainSize, read);

                    int aligned = total - (total % frameSize);

                    applyVolume(data, aligned);

                    line.write(data, 0, aligned);

                    remainSize = total - aligned;

                    if (remainSize > 0) {
                        System.arraycopy(data, aligned, remain, 0, remainSize);
                    }

                    position += aligned / frameSize;
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                stop();
            }

        });

        playThread.start();
    }

    private void startFFmpeg(long posSample) throws IOException {

        // FFmpegプロセス開始 
        double sec = posSample / (double) SAMPLE_RATE;
        ProcessBuilder pb = new ProcessBuilder(//
                ffmpegCommand, //
                "-ss", String.valueOf(sec), //
                "-i", file.getAbsolutePath(), //
                "-vn", //
                "-f", "s16le", //
                "-ac", "2", //
                "-ar", "44100", //
                "-"//
        );//

        pb.redirectErrorStream(false);

        ffmpeg = pb.start();

        discardErrorStream(ffmpeg);
    }

    private void discardErrorStream(Process process) {

        new Thread(() -> {

            try (InputStream es = process.getErrorStream()) {

                byte[] buf = new byte[1024];

                while (es.read(buf) != -1) {
                    // 何もしない（読み捨て）
                }

            }
            catch (IOException e) {
            }

        }, "ffmpeg-error-discard").start();
    }

    public void stop() {

        running = false;

        if (line != null) {
            line.stop();
            line.close();
        }

        if (ffmpeg != null) {
            ffmpeg.destroy();
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
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public int getPositionSecond() {
        return (int) (position / SAMPLE_RATE);
    }

    public int getLengthSecond() {
        return (int) (length / SAMPLE_RATE);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    private void applyVolume(byte[] buffer, int len) {

        for (int i = 0; i < len; i += 2) {

            short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xff));

            sample = (short) (sample * volume);

            buffer[i] = (byte) (sample & 0xff);
            buffer[i + 1] = (byte) ((sample >> 8) & 0xff);
        }
    }

    public boolean isSupportedExtension(String ext) {

        if (ext == null)
            return false;

        ext = ext.toLowerCase();

        return ext.equals("mp4") || ext.equals("m4a") || ext.equals("mov");
    }

    public String[] getSupportExtentions() {
        return new String[] { "mp4", "m4a", "mov" };
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
}
