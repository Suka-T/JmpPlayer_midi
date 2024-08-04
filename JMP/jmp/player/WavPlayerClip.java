package jmp.player;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import function.Platform;
import jlib.player.Player;

/**
 * 自前でクリップを作成するクラス。<br>
 * (全ての音声データをインメモリに書き込んだ後再生する)
 * 
 * @author akkut
 *
 */
public class WavPlayerClip extends WavPlayer {
    private Clip clip = null;

    public WavPlayerClip() {
        super();
    }

    public void setClip(Clip c) {
        // クリップの破棄
        disposeClip();
        clip = null;

        Platform.requestGarbageCollection();

        clip = c;
    }

    public Clip getClip() {
        return clip;
    }

    // クリップの破棄
    private void disposeClip() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    @Override
    public boolean close() {
        boolean ret = super.close();

        // クリップを破棄
        disposeClip();
        return ret;
    }

    @Override
    public void play() {
        if (isValid() == false) {
            return;
        }
        clip.start();
    }

    @Override
    public void stop() {
        if (isValid() == false) {
            return;
        }
        clip.stop();
    }

    @Override
    public boolean isRunnable() {
        if (isValid() == false) {
            return false;
        }
        return clip.isRunning();
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        clip.setFramePosition((int) pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return clip.getFramePosition();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return clip.getFrameLength();
    }

    @Override
    public boolean isValid() {
        if (clip == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        long pos = clip.getFramePosition();
        float flame = clip.getFormat().getSampleRate();
        int time = (int) (pos / flame);
        return time;
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        long length = clip.getFrameLength();
        float flame = clip.getFormat().getSampleRate();
        int time = (int) (length / flame);
        return time;
    }

    @Override
    public void setVolume(float volume) {
        if (isValid() == false) {
            return;
        }
    }

    public FloatControl getFloatControl(FloatControl.Type type) {
        FloatControl control = (FloatControl) clip.getControl(type);
        return control;
    }

    @Override
    public float getVolume() {
        if (isValid() == false) {
            return 0;
        }
        return 0;
        // FloatControl control =
        // getFloatControl(FloatControl.Type.MASTER_GAIN);
        // float range = control.getMaximum() - control.getMinimum();
        // float volume = control.getValue() - control.getMinimum();
        // int pos = (int) ((volume * volumeSlider.getMaximum()) / range);
        // return volume;
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        loadWavFile(file);

        Player.Info info = createInfo();
        info.put(Player.Info.PLAYER_ABS_INFO_KEY_FILENAME, file.getName());
        info.update();
        setInfo(info);

        return true;
    }

    public Player.Info createInfo() {
        Player.Info info = new Player.Info();

        Clip clip = getClip();
        if (clip == null) {
            info.update();
            return info;
        }

        AudioFormat format = clip.getFormat();
        if (format == null) {
            info.update();
            return info;
        }

        String sEnc = "";
        AudioFormat.Encoding enc = format.getEncoding();
        if (enc == AudioFormat.Encoding.ALAW) {
            sEnc = "ALAW";
        }
        else if (enc == AudioFormat.Encoding.PCM_FLOAT) {
            sEnc = "PCM_FLOAT";
        }
        else if (enc == AudioFormat.Encoding.PCM_SIGNED) {
            sEnc = "PCM_SIGNED";
        }
        else if (enc == AudioFormat.Encoding.PCM_UNSIGNED) {
            sEnc = "PCM_UNSIGNED";
        }
        else if (enc == AudioFormat.Encoding.ULAW) {
            sEnc = "ULAW";
        }
        else {
            sEnc = "UNKNOWN";
        }
        info.put("Channels", String.valueOf(format.getChannels()));
        info.put("Encoding", sEnc);
        info.put("Frame Rate", String.valueOf(format.getFrameRate()));
        info.put("Frame Size", String.valueOf(format.getFrameSize()));
        info.put("Sample Rate", String.valueOf(format.getSampleRate()));
        info.put("Sample Size", String.valueOf(format.getSampleSizeInBits()));

        info.update();
        return info;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

    /**
     * Clip作成
     *
     * @param path
     *            ファイルパス
     * @return クリップ
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    protected Clip createClip(String path) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File file = new File(path);
        return createClip(file);
    }

    /**
     * Clip作成
     *
     * @param file
     *            ファイル
     * @return クリップ
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected Clip createClip(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = null;

        // オーディオ入力ストリーム取得
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        tmpIs = ais;
        // オーディオフォーマット取得
        AudioFormat audioFormat = ais.getFormat();
        DataLine.Info dataLine = new DataLine.Info(Clip.class, audioFormat);
        clip = (Clip) AudioSystem.getLine(dataLine);
        clip.open(ais);
        return clip;
    }

    private AudioInputStream tmpIs = null;

    public ArrayList<Long> sampling(int samplingSize) throws IOException {
        ArrayList<Long> data = new ArrayList<>();
        if (tmpIs == null) {
            return data;
        }
        if (samplingSize < 0) {
            return data;
        }
        int sample_size = tmpIs.getFormat().getSampleSizeInBits() / 8;
        while (data.size() < samplingSize) {
            byte[] buffer = new byte[8];
            tmpIs.read(buffer, 8 - sample_size, sample_size);
            if (buffer[8 - sample_size] < 0) {
                for (int i = 0; i < 8 - sample_size; i++) {
                    buffer[i] = -1;
                }
            }
            data.add(ByteBuffer.wrap(buffer).getLong());
        }
        return data;
    }

    protected void loadWavFile(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = createClip(file);
        setClip(clip);
    }
}
