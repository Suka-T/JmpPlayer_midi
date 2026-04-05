package jmsynth.sound;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jmsynth.app.component.IWaveRepaintListener;
import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorConfig;
import jmsynth.oscillator.OscillatorManager;
import jmsynth.oscillator.OscillatorSet;

public class SoundSourceChannel extends Thread implements ISynthController {
    public static final float SAMPLE_RATE = 44100.0f; // サンプルレート
    // public static final float SAMPLE_RATE = 22050.0f; // サンプルレート
    // public static final float SAMPLE_RATE = 11025.0f; // サンプルレート

    public static final boolean SAMPLE_16BITS = true;
    public static final int SAMPLE_SIZE = SAMPLE_16BITS ? 16 : 8;
    public static final int CHANNEL = 2;
    public static final int FRAME_SIZE = CHANNEL * (SAMPLE_SIZE / 8);
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;

    // 1フレームで再生するバイト数
    public final static int BUF_SIZE = ((int) SAMPLE_RATE / 50 * FRAME_SIZE);

    // 再生フォーマット
    private AudioFormat audioFormat;

    // 出力ライン
    public SourceDataLine line = null;

    private byte[] displayBuf = null;

    private int channel;
    public boolean isRunnable;

    private OscillatorConfig oscConfig;

    private int NRPN = 0;
    private double pitch_sc = 2.0;
    
    protected ToneManager toneManager = null;

    private IWaveRepaintListener waveRepaintListener = null;

    private OscillatorManager oscManager = null;

    protected Envelope envelope = null;
    protected Modulator modulator = null;

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope, Modulator modulator) {
        init(channel, oscType, polyphony, envelope, modulator);
    }

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope) {
        init(channel, oscType, polyphony, envelope, null);
    }

    public SoundSourceChannel(int channel, IOscillator oscType, int polyphony) {
        init(channel, oscType, polyphony, null, null);
    }

    private void init(int channel, IOscillator oscType, int polyphony, Envelope envelope, Modulator modulator) {
        this.audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNEL, SIGNED, BIG_ENDIAN);

        this.channel = channel;

        this.oscConfig = new OscillatorConfig();
        
        this.oscManager = new OscillatorManager();
        this.oscManager.addOscillator(oscType);

        // ライン情報取得
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, this.audioFormat, BUF_SIZE);
        try {
            // 出力ライン取得
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open();
            line.flush();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }
        this.setPriority(MAX_PRIORITY);

        toneManager = new ToneManager(polyphony);

        this.envelope = envelope;
        if (this.envelope != null) {
            this.envelope.setTargetTones(toneManager);
        }

        this.modulator = modulator;
        if (this.modulator != null) {
            this.modulator.setTargetTones(toneManager);
        }
    }

    @Override
    public synchronized void start() {
        if (isRunnable == true) {
            return;
        }
        super.start();
    }

    public synchronized int makeTone(byte[] data, int bufSize) {
        int length = Math.min(bufSize, data.length);

        Arrays.fill(data, (byte) 0x00);

        for (int i = 0; i < toneManager.getActiveToneCount(); i++) {
            try {
                Tone tone = (Tone) toneManager.getActiveTone(i);
                oscManager.makeTone(data, length, tone, this.oscConfig);

                if (envelope.getReleaseTime() > 0.0) {
                    // リリース処理
                    if (tone.isReleaseFlag() == true && tone.getEnvelopeOffset() <= 0.0) {
                        int note = tone.getNote();
                        noteOffImpl(note);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException aiobe) {
                // ArrayIndexOutOfBoundsExceptionは起きがち
                // スレッドのタイミング次第なためとりあえず無視...
                // aiobe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return length;
    }

    public int calcSampleRate(long milliseconds) {
        return (int) (milliseconds / 1000 * BUF_SIZE);
    }

    // int[] samples = new int[BUF_SIZE]; // 波形データ
    // static final int AVERAGE_SAMPLE_COUNT = 10;
    //
    // public int samplesOfAverage(byte[] data, int length) {
    // int newLength = length;
    // for (int i = 0; i < samples.length; i++) {
    // int avgSample = 0;
    // int sampleCount = 0;
    // for (int a = 0; a < AVERAGE_SAMPLE_COUNT; a++) {
    // if ((i + a) >= data.length) {
    // break;
    // }
    // avgSample += data[i + a];
    // sampleCount++;
    // }
    // avgSample /= sampleCount;
    // samples[i] = avgSample;
    // }
    //
    // for (int i = 0; i < newLength; i++) {
    // data[i] = (byte) (samples[i] & 0xff);
    // }
    // return newLength;
    // }

    /**
     * バッファ再生処理
     */
    private static int REPAINT_CYCLE = 2;
    private int repWait = 0;

    public void run() {
        isRunnable = true;
        line.start();
        byte[] waveData = new byte[BUF_SIZE]; // 波形データ
        displayBuf = new byte[BUF_SIZE]; // 波形データ

        int sampleRate = BUF_SIZE;
        while (isRunnable) {
            try {
                
                this.envelope.process();
                this.modulator.process();
                
                int length = makeTone(waveData, sampleRate); // 再生するたびに作り直す
                // samplesOfAverage(waveData, length);

                if (repWait % REPAINT_CYCLE == 0) {
                    callWaveRepaint(waveData);
                    repWait = 0;
                }
                repWait++;

                line.write(waveData, 0, length);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        line.close();
    }

    public void callWaveRepaint(byte[] data) {
        if (waveRepaintListener == null) {
            return;
        }

        try {
            System.arraycopy(data, 0, displayBuf, 0, data.length);
            waveRepaintListener.repaintWave(displayBuf);
        }
        catch (Exception e) {
        }
    }

    protected void exit() { // 再生終了
        isRunnable = false;
    }
    
    public FloatControl getFloatControl(FloatControl.Type type) {
        FloatControl control = (FloatControl) line.getControl(type);
        return control;
    }
    
    public double convertFloatControlValue(float volume, float maximum, float minimum) {
        if (volume > 1.0f)
            volume = 1.0f;
        else if (volume < 0.0f)
            volume = 0.0f;
        double max = Math.pow(10.0, maximum / 20.0);
        double min = Math.pow(10.0, minimum / 20.0);
        double newValue = (max - min) * (volume * volume) + min;
        newValue = 20 * Math.log(newValue) / Math.log(10);
        return newValue;
    }

    /**
     * ボリュームの設定
     *
     * @param volume
     *            0.0 ~ 1.0
     */
    public void setVolume(float volume) {
        FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        double newValue = convertFloatControlValue(volume, control.getMaximum(), control.getMinimum());
        control.setValue((float) newValue);
    }

    /**
     * pan設定処理
     *
     * @param pan
     *            設定する pan 0～127
     */
    public void setPan(int pan) {
        try {
            // 0 ~ 64 ~ 127
            // -1 ~ 0 ~ 1
            if (pan < 0) {
                pan = 0;
            }
            else if (pan > 127) {
                pan = 127;
            }

            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.PAN);
            float _pan = ((float) pan - 64) / 127;
            control.setValue(_pan);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * リバーブ設定処理
     *
     * @param reverb
     *            リバーブの設定をします 0～127
     *
     */
    public void setReverb_ret(int reverb) {
        try {
            // 0 ~ 64 ~ 127
            // -1 ~ 0 ~ 1
            if (reverb < 0) {
                reverb = 0;
            }
            else if (reverb > 127) {
                reverb = 127;
            }

            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.REVERB_RETURN);

            float _reverb = ((float) reverb - 64) / 127;

            // System.out.println("REVERB_RETURN "+_reverb +" " + reverb +" ");
            control.setValue(_reverb);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getChannel() {
        return channel;
    }

    public boolean checkChannel(int ch) {
        int channel = ch;
        if (channel > toneManager.getPlayingToneLength()) {
            return false;
        }
        if (channel != this.channel) {
            return false;
        }
        return true;
    }

    public void noteOn(int ch, int note, int velocity) {
        noteOnImpl(note, velocity);
    }

    public void noteOff(int ch, int note) {
        noteOffImpl(note);
    }

    private Object onOffMutex = new Object();
    protected void noteOnImpl(int note, int velocity) {
        synchronized (onOffMutex) {
            if (note < 0 || 127 < note) {
                return;
            }
            
            try {
                if (velocity > 0) {
                    if (toneManager.isPlayingTone(note) == true) {
                        // 重複音声
                        Tone tone = toneManager.getPlayingTone(note);
                        if (tone.isReleaseFlag() == true) {
                            // リリースの途中破棄
                            toneManager.deactivateTone(note);
                            if (!oscManager.isToneSync()) {
                                tone.setTablePointer(0);
                            }
                        }
                        else {
                            // 単純にNoteOffを送る
                            noteOffImpl(note);
                        }
                    }
                    toneManager.activateTone(note, velocity);
                }
                else {
                    noteOffImpl(note);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void noteOffImpl(int note) {
        synchronized (onOffMutex) {
            if (note < 0 || 127 < note) {
                return;
            }
            
            Tone tone = toneManager.getPlayingTone(note);
            if (tone != null) {
                if ((envelope.getReleaseTime() > 0.0) && (tone.isReleaseFlag() == false)) {
                    tone.setReleaseFlag(true);
                    tone.setStartMills();
                }
                else {
                    toneManager.deactivateTone(note);
                    if (!oscManager.isToneSync()) {
                        tone.setTablePointer(0);
                    }
                }
            }
        }
    }

    public void pitchBend(int ch, int pitch) {
        Tone[] toneInstances = toneManager.getToneInstances();
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.setPitch((double) ((double)pitch * pitch_sc) / 8191.0);//
            // 16382で半音あがる
        }
    }

    public void setExpression(int ch, int exp) {
        Tone[] toneInstances = toneManager.getToneInstances();
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.setExpression(exp);
        }
    }

    public void pitchBendSenc(int ch, int sc) {
        pitch_sc = (double)sc;
    }
    
    public double getPitchBendSenc() {
        return pitch_sc;
    }

    public void setNRPN(int ch, int nRPN) {
        NRPN = nRPN;
    }

    public int getNRPN(int ch) {
        return NRPN;
    }

    public void resetAllController(int ch) {
        // 発音中の音源を元に戻す  
        Tone[] toneInstances = toneManager.getToneInstances();
        for (int i = 0; i < toneInstances.length; i++) {
            Tone tone = toneInstances[i];
            tone.reset();
        }
        
        setModulationDepth(ch, 0);
        setNRPN(ch, 0);
    }

    @Override
    public void allSoundOff(int ch) {
        /* 音源を強制的に破棄する */
        toneManager.allDeactivateTone();
    }

    public void allNoteOff(int ch) {
        for (int i = 0; i < toneManager.getPlayingToneLength(); i++) {
            if (toneManager.isPlayingTone(i) == true) {
                noteOffImpl(i);
            }
        }
//        for (int i = 0; i < toneManager.getActiveToneCount(); i++) {
//            Tone tone = toneManager.getActiveTone(i);
//            if (tone == null) {
//                continue;
//            }
//            if (toneManager.isPlayingTone(tone.getNote()) == true) {
//                // 発声中の音声を止める
//                if ((envelope.getReleaseTime() > 0.0) && (tone.isReleaseFlag() == false)) {
//                    tone.setReleaseFlag(true);
//                }
//                noteOffImpl(tone.getNote());
//            }
//        }
    }

    public void setVibratoRate(int ch, int rate) {
    }

    public void setVibratoDepth(int ch, int depth) {
    }

    public void setVibratoDelay(int ch, int delay) {
    }

    public void setVariation(int ch, int val) {
    }

    public IWaveRepaintListener getWaveRepaintListener() {
        return waveRepaintListener;
    }

    public void setWaveRepaintListener(IWaveRepaintListener waveRepaintListener) {
        this.waveRepaintListener = waveRepaintListener;
    }

    public IOscillator getOscillator(int index) {
        return getOscillator(0, index);
    }

    public IOscillator getOscillator(int ch, int index) {
        return oscManager.getOscillator(index);
    }

    @Override
    public void addOscillator(int ch, IOscillator osc) {
        oscManager.addOscillator(osc);
    }
    
    @Override
    public void clearOscillator(int ch) {
        oscManager.clearWave();
    }
    
    @Override
    public OscillatorSet getOscillatorSet(int ch) {
        OscillatorSet oscSet = new OscillatorSet();
        for (int i = 0; i < oscManager.getOscillatorCount(); i++) {
            oscSet.addOscillators(oscManager.getOscillator(i));
        }
        return oscSet;
    }

    @Override
    public void setPan(int ch, int pan) {
        setPan(pan);
    }

    @Override
    public void setVolume(int ch, float volume) {
        setVolume(volume);
    }

    @Override
    public void openDevice() {
        start();
    }

    @Override
    public void closeDevice() {
        exit();
    }

    @Override
    public Envelope getEnvelope(int ch) {
        return envelope;
    }

    public Envelope getEnvelope() {
        return getEnvelope(0);
    }

    @Override
    public void systemReset() {
        // Expressionを0にすることで初期化中の音声レベルを0にする
        setExpression(0, 0);

        pitch_sc = 2;
        pitchBend(0, 0);
        allSoundOff(0);
        resetAllController(0);
        setNRPN(0, 0);
        setPan(0, 64);
        setModulationDepth(0, 0);
        setVolume(0, 1.0f);

        // Expressionは最後に初期化
        setExpression(0, 127);
    }

    @Override
    public void setModulationDepth(int ch, int depth) {
        if (modulator != null) {
            modulator.setDepth(depth);
        }
    }

    public Modulator getModulator() {
        return getModulator(0);
    }

    @Override
    public Modulator getModulator(int ch) {
        return modulator;
    }

    public void setWaveReverse(boolean isReverse) {
        setWaveReverse(0, isReverse);
    }

    @Override
    public void setWaveReverse(int ch, boolean isReverse) {
        this.oscConfig.setWaveReverse(isReverse);
    }

    public boolean isWaveReverse() {
        return isWaveReverse(0);
    }

    @Override
    public boolean isWaveReverse(int ch) {
        return this.oscConfig.isWaveReverse();
    }

    public void setValidNesSimulate(boolean isValidNesSimulate) {
        setValidNesSimulate(0, isValidNesSimulate);
    }

    @Override
    public void setValidNesSimulate(int ch, boolean isValidNesSimulate) {
        this.oscConfig.setValidNesSimulate(isValidNesSimulate);
    }

    public boolean isValidNesSimulate() {
        return isValidNesSimulate(0);
    }

    @Override
    public boolean isValidNesSimulate(int ch) {
        return this.oscConfig.isValidNesSimulate();
    }
    
    public int getNumOfTones() {
        return toneManager.getPlayingToneCount();
    }
    
    public int getNumOfActiveTone() {
        return toneManager.getActiveToneCount();
    }
    
    public int getNumOfTonePool() {
        return toneManager.getDeactiveToneCount();
    }
    
    public double getTonePitch() {
        return toneManager.getCurrentPitch();
    }
    
    public int getToneExpression() {
        return toneManager.getCurrentExpression();
    }
    
    public int getToneVelocity() {
        return toneManager.getCurrentVelocity();
    }
    
    public double getToneEnvelopeOffset() {
        return toneManager.getCurrentEnvelopeOffset();
    }
    
    public int getToneOverallLevel() {
        return toneManager.getCurrentOverallLevel();
    }

}
