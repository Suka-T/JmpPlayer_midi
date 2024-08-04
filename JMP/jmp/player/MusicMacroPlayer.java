package jmp.player;

import java.io.File;

import javax.sound.midi.Sequence;

import jlib.player.Player;
import jmp.convert.mml.MusicMacroReader;

public class MusicMacroPlayer extends Player {

    private MidiPlayer midiPlayer;

    public MusicMacroPlayer(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
    }

    @Override
    public void play() {
        midiPlayer.play();
    }

    @Override
    public void stop() {
        midiPlayer.stop();
    }

    @Override
    public boolean isRunnable() {
        return midiPlayer.isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        midiPlayer.setPosition(pos);
    }

    @Override
    public long getPosition() {
        return midiPlayer.getPosition();
    }

    @Override
    public long getLength() {
        return midiPlayer.getLength();
    }

    @Override
    public boolean isValid() {
        return midiPlayer.isValid();
    }

    @Override
    public int getPositionSecond() {
        return midiPlayer.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return midiPlayer.getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        midiPlayer.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return midiPlayer.getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        MusicMacroReader reader = new MusicMacroReader();
        reader.load(file);

        Sequence seq = reader.convertToMidi();
        midiPlayer.loadMidiSequence(seq);
        return true;
        // File xmlFile = null;
        // File tmpDir = null;
        // if (Utility.checkExtension(file, "mxl") == true) {
        // int cnt = 0;
        // String tmpDirectoryPath = "";
        // while (tmpDirectoryPath.isEmpty() == true ||
        // Utility.isExsistFile(tmpDirectoryPath) == true) {
        // String tmpDirectoryName = String.format("_%s%d%s", "mxl", cnt,
        // Utility.getFileNameNotExtension(file));
        // tmpDirectoryPath = Utility.pathCombin(Platform.getCurrentPath(false),
        // tmpDirectoryName);
        // cnt++;
        // }
        // Utility.unZip(file.getPath(), tmpDirectoryPath);
        //
        // tmpDir = new File(tmpDirectoryPath);
        // if (tmpDir.exists() == true) {
        // for (File f : tmpDir.listFiles()) {
        // if (Utility.checkExtension(f, "xml") == true) {
        // xmlFile = f;
        // break;
        // }
        // }
        // }
        // }
        // else {
        // xmlFile = file;
        // }
        //
        // if (xmlFile == null) {
        // return false;
        // }
        //
        // MusicXMLReader reader = new MusicXMLReader();
        // reader.load(xmlFile);
        //
        // boolean result = false;
        // if (reader.isLoadResult() == true) {
        // confirmDialog.setVisible(true);
        // reader.setAutoAssignChannel(confirmDialog.isAutoAssignChannel());
        // reader.setAutoAssignProgramChange(confirmDialog.isAutoAssignProgramChange());
        //
        // Sequence seq = reader.convertToMidi();
        // midiPlayer.loadMidiSequence(seq);
        // result = true;
        // }
        //
        // if (tmpDir != null) {
        // if (tmpDir.exists() == true) {
        // Utility.deleteFileDirectory(tmpDir);
        // }
        // }
        //
        // Player.Info info = midiPlayer.createInfo();
        // info.put(Player.Info.PLAYER_ABS_INFO_KEY_FILENAME, (file.getName() +
        // "(midi)"));
        // info.update();
        // setInfo(info);
        //
        // return result;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return midiPlayer.saveFile(file);
    }

    @Override
    public Info getInfo() {
        return midiPlayer.getInfo();
    }

    @Override
    public void setInfo(Info info) {
        midiPlayer.setInfo(info);
    }

}
