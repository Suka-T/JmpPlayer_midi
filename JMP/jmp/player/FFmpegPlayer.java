package jmp.player;

import java.io.File;

import function.Utility;
import jlib.player.Player;
import jmp.core.JMPCore;
import jmp.core.SystemManager;

public class FFmpegPlayer extends Player {

    private Player mediaPlayer = null;

    public FFmpegPlayer(Player mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void play() {
        mediaPlayer.play();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public boolean isRunnable() {
        return mediaPlayer.isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        mediaPlayer.setPosition(pos);
    }

    @Override
    public long getPosition() {
        return mediaPlayer.getPosition();
    }

    @Override
    public long getLength() {
        return mediaPlayer.getLength();
    }

    @Override
    public boolean isValid() {
        return mediaPlayer.isValid();
    }

    @Override
    public int getPositionSecond() {
        return mediaPlayer.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return mediaPlayer.getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return mediaPlayer.getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        SystemManager system = JMPCore.getSystemManager();
        boolean tmpWaitFor = system.isFFmpegWrapperWaitFor();
        system.setFFmpegWrapperWaitFor(true);

        String inpath = file.getPath();

        String inname = Utility.getFileNameNotExtension(inpath);
        File outdir = new File(JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_FFMPEG_OUTPUT));

        String outpath = Utility.pathCombin(outdir.getPath(), (inname + ".wav"));

        File in = new File(inpath);
        File out = new File(outpath);

        system.setFFmpegWrapperCallback(null);

        try {
            system.executeConvert(in.getPath(), out.getPath(), true);
        }
        catch (Exception e1) {
            return false;
        }

        system.setFFmpegWrapperWaitFor(tmpWaitFor);

        if (out.canRead() == false) {
            // 読み込めない場合は変換失敗とする
            return false;
        }
        return mediaPlayer.loadFile(out);
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return mediaPlayer.saveFile(file);
    }

    @Override
    public boolean isAllSupported() {
        SystemManager system = JMPCore.getSystemManager();
        if (system.isValidFFmpegWrapper() == false) {
            return false;
        }

        return super.isAllSupported();
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        SystemManager system = JMPCore.getSystemManager();
        if (system.isValidFFmpegWrapper() == false) {
            return false;
        }

        return super.isSupportedExtension(extension);
    }

    @Override
    public Info getInfo() {
        return mediaPlayer.getInfo();
    }

    @Override
    public void setInfo(Info info) {
        mediaPlayer.setInfo(info);
    }

}
