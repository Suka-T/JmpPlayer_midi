package jmp.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jmp.core.JMPCore;

public class JMPSequencer implements Sequencer {

    private Sequencer abstractSequencer = null;

    public JMPSequencer(Sequencer abstractSequenser) {
        this.abstractSequencer = abstractSequenser;
    }

    @Override
    public Info getDeviceInfo() {
        return abstractSequencer.getDeviceInfo();
    }

    @Override
    public void open() throws MidiUnavailableException {
        abstractSequencer.open();
    }

    @Override
    public void close() {
        abstractSequencer.close();
    }

    @Override
    public boolean isOpen() {
        return abstractSequencer.isOpen();
    }

    @Override
    public int getMaxReceivers() {
        return abstractSequencer.getMaxReceivers();
    }

    @Override
    public int getMaxTransmitters() {
        return abstractSequencer.getMaxTransmitters();
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        return abstractSequencer.getReceiver();
    }

    @Override
    public List<Receiver> getReceivers() {
        return abstractSequencer.getReceivers();
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        return abstractSequencer.getTransmitter();
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return abstractSequencer.getTransmitters();
    }

    @Override
    public void setSequence(Sequence sequence) throws InvalidMidiDataException {
        abstractSequencer.setSequence(sequence);
    }

    @Override
    public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException {
        abstractSequencer.setSequence(stream);
    }

    @Override
    public Sequence getSequence() {
        return abstractSequencer.getSequence();
    }

    @Override
    public void start() {
        if (getSequence() == null) {
            // 念のためNULLチェック
            return;
        }

        // MidiPlayerに変更する
        JMPCore.getSoundManager().changeMidiPlayer();

        // ロード時にシステムセットアップを送信する
        if (JMPCore.getDataManager().isSendMidiSystemSetup() == true) {
            if (getTickPosition() <= 0) {
                JMPCore.getSoundManager().sendMidiSystemSetupMessage();
            }
        }

        abstractSequencer.start();

        JMPCore.getPluginManager().startSequencer();
    }

    @Override
    public void stop() {
        abstractSequencer.stop();

        JMPCore.getPluginManager().stopSequencer();
    }

    @Override
    public boolean isRunning() {
        return abstractSequencer.isRunning();
    }

    @Override
    public void startRecording() {
        abstractSequencer.startRecording();
    }

    @Override
    public void stopRecording() {
        abstractSequencer.stopRecording();
    }

    @Override
    public boolean isRecording() {
        return abstractSequencer.isRecording();
    }

    @Override
    public void recordEnable(Track track, int channel) {
        abstractSequencer.recordEnable(track, channel);
    }

    @Override
    public void recordDisable(Track track) {
        abstractSequencer.recordDisable(track);
    }

    @Override
    public float getTempoInBPM() {
        return abstractSequencer.getTempoInBPM();
    }

    @Override
    public void setTempoInBPM(float bpm) {
        abstractSequencer.setTempoInBPM(bpm);
    }

    @Override
    public float getTempoInMPQ() {
        return abstractSequencer.getTempoInMPQ();
    }

    @Override
    public void setTempoInMPQ(float mpq) {
        abstractSequencer.setTempoInMPQ(mpq);
    }

    @Override
    public void setTempoFactor(float factor) {
        abstractSequencer.setTempoFactor(factor);
    }

    @Override
    public float getTempoFactor() {
        return abstractSequencer.getTempoFactor();
    }

    @Override
    public long getTickLength() {
        return abstractSequencer.getTickLength();
    }

    @Override
    public long getTickPosition() {
        return abstractSequencer.getTickPosition();
    }

    @Override
    public void setTickPosition(long tick) {
        long before = abstractSequencer.getTickPosition();

        abstractSequencer.setTickPosition(tick);

        JMPCore.getPluginManager().updateTickPosition(before, abstractSequencer.getTickPosition());
    }

    @Override
    public long getMicrosecondLength() {
        return abstractSequencer.getMicrosecondLength();
    }

    @Override
    public long getMicrosecondPosition() {
        return abstractSequencer.getMicrosecondPosition();
    }

    @Override
    public void setMicrosecondPosition(long microseconds) {
        abstractSequencer.setMicrosecondPosition(microseconds);
    }

    @Override
    public void setMasterSyncMode(SyncMode sync) {
        abstractSequencer.setMasterSyncMode(sync);
    }

    @Override
    public SyncMode getMasterSyncMode() {
        return abstractSequencer.getMasterSyncMode();
    }

    @Override
    public SyncMode[] getMasterSyncModes() {
        return abstractSequencer.getMasterSyncModes();
    }

    @Override
    public void setSlaveSyncMode(SyncMode sync) {
        abstractSequencer.setSlaveSyncMode(sync);
    }

    @Override
    public SyncMode getSlaveSyncMode() {
        return abstractSequencer.getSlaveSyncMode();
    }

    @Override
    public SyncMode[] getSlaveSyncModes() {
        return abstractSequencer.getSlaveSyncModes();
    }

    @Override
    public void setTrackMute(int track, boolean mute) {
        abstractSequencer.setTrackMute(track, mute);
    }

    @Override
    public boolean getTrackMute(int track) {
        return abstractSequencer.getTrackMute(track);
    }

    @Override
    public void setTrackSolo(int track, boolean solo) {
        abstractSequencer.setTrackSolo(track, solo);
    }

    @Override
    public boolean getTrackSolo(int track) {
        return abstractSequencer.getTrackSolo(track);
    }

    @Override
    public boolean addMetaEventListener(MetaEventListener listener) {
        return abstractSequencer.addMetaEventListener(listener);
    }

    @Override
    public void removeMetaEventListener(MetaEventListener listener) {
        abstractSequencer.removeMetaEventListener(listener);
    }

    @Override
    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers) {
        return abstractSequencer.addControllerEventListener(listener, controllers);
    }

    @Override
    public int[] removeControllerEventListener(ControllerEventListener listener, int[] controllers) {
        return abstractSequencer.removeControllerEventListener(listener, controllers);
    }

    @Override
    public void setLoopStartPoint(long tick) {
        abstractSequencer.setLoopStartPoint(tick);
    }

    @Override
    public long getLoopStartPoint() {
        return abstractSequencer.getLoopStartPoint();
    }

    @Override
    public void setLoopEndPoint(long tick) {
        abstractSequencer.setLoopEndPoint(tick);
    }

    @Override
    public long getLoopEndPoint() {
        return abstractSequencer.getLoopEndPoint();
    }

    @Override
    public void setLoopCount(int count) {
        abstractSequencer.setLoopCount(count);
    }

    @Override
    public int getLoopCount() {
        return abstractSequencer.getLoopCount();
    }

}
