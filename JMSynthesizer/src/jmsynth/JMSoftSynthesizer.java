package jmsynth;

import jmsynth.app.component.IWaveRepaintListener;
import jmsynth.envelope.Envelope;
import jmsynth.envelope.EnvelopeFactory;
import jmsynth.modulate.Modulator;
import jmsynth.modulate.ModulatorFactory;
import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;
import jmsynth.sound.DrumSoundSourceChannel;
import jmsynth.sound.ISynthController;
import jmsynth.sound.SoundSourceChannel;

public class JMSoftSynthesizer implements ISynthController {

    public static final String INFO_NAME = "JMSynthesizer";
    public static final String INFO_VENDOR = "Akt";
    public static final String INFO_DESCRIPTION = "Self-made built-in 8bit tune synthesizer.";
    public static final String INFO_VERSION = jmsynth.Version.NO;

    protected SoundSourceChannel[] channels = null;
    private EnvelopeFactory envelopeFactory = null;
    private ModulatorFactory modulatorFactory = null;

    private static final short CHANNEL_TYPE_SOUND = 0;
    private static final short CHANNEL_TYPE_DRUM = 1;

    // エンベロープのスレッドモード
    private static final boolean USE_MULTITHREAD_ENV = true;
    // モジュレーションのスレッドモード
    private static final boolean USE_MULTITHREAD_MOD = true;

    // チャンネル構成
    private static final short[] CHANNEL_TYPE_LIST = { //
            CHANNEL_TYPE_SOUND, // 1ch
            CHANNEL_TYPE_SOUND, // 2ch
            CHANNEL_TYPE_SOUND, // 3ch
            CHANNEL_TYPE_SOUND, // 4ch
            CHANNEL_TYPE_SOUND, // 5ch
            CHANNEL_TYPE_SOUND, // 6ch
            CHANNEL_TYPE_SOUND, // 7ch
            CHANNEL_TYPE_SOUND, // 8ch
            CHANNEL_TYPE_SOUND, // 9ch
            CHANNEL_TYPE_DRUM, // 10ch
            CHANNEL_TYPE_SOUND, // 11ch
            CHANNEL_TYPE_SOUND, // 12ch
            CHANNEL_TYPE_SOUND, // 13ch
            CHANNEL_TYPE_SOUND, // 14ch
            CHANNEL_TYPE_SOUND, // 15ch
            CHANNEL_TYPE_SOUND, // 16ch
    };

    /**
     *
     * @param polyphony
     *            同時発音可能数
     */
    public JMSoftSynthesizer(int polyphony) {
        makeChannel(polyphony);
    }

    /**
     *
     */
    public JMSoftSynthesizer() {
        makeChannel(64);
    }

    private void makeChannel(int polyphony) {

        envelopeFactory = new EnvelopeFactory(USE_MULTITHREAD_ENV);
        modulatorFactory = new ModulatorFactory(USE_MULTITHREAD_MOD);

        // チャンネル生成
        channels = new SoundSourceChannel[CHANNEL_TYPE_LIST.length];
        for (int ch = 0; ch < channels.length; ch++) {
            channels[ch] = createChannel(ch, polyphony);
        }

        // 全てのチャンネルを初期設定にする
        initializeAllChannel();
    }

    private SoundSourceChannel createChannel(int ch, int polyphony) {
        SoundSourceChannel ret = null;
        Envelope env = envelopeFactory.newEnvelopeInstance();
        Modulator mod = modulatorFactory.newModulatorInstance();
        OscillatorFactory oscFc = new OscillatorFactory();

        switch (CHANNEL_TYPE_LIST[ch]) {
            case CHANNEL_TYPE_DRUM:
                /* ドラム音源 */
                ret = new DrumSoundSourceChannel(ch, oscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SINE), 
                        polyphony, env, mod);
                break;

            case CHANNEL_TYPE_SOUND:
            default:
                /* 通常音源 */
                ret = new SoundSourceChannel(ch, oscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SINE), 
                        polyphony, env, mod);
                break;
        }
        return ret;
    }

    public void initializeAllChannel() {
        for (int i = 0; i < getNumberOfChannel(); i++) {
            initializeChannel(i);
        }
    }

    public void initializeChannel(int ch) {
        if (ch < 0 || ch >= getNumberOfChannel()) {
            return;
        }

        IOscillator osc = null;
        SoundSourceChannel target = channels[ch];
        OscillatorFactory oscFc = new OscillatorFactory();
        boolean waveReverse = false;
        boolean validFesSim = false;
        double a = EnvelopeFactory.DEFAULT_A;
        double d = EnvelopeFactory.DEFAULT_D;
        double s = EnvelopeFactory.DEFAULT_S;
        double r = EnvelopeFactory.DEFAULT_R;
        long ma = EnvelopeFactory.DEFAULT_MAX_A;
        long md = EnvelopeFactory.DEFAULT_MAX_D;
        long mr = EnvelopeFactory.DEFAULT_MAX_R;
        int modDepth = ModulatorFactory.DEFAULT_DEPTH;

        switch (CHANNEL_TYPE_LIST[ch]) {
            case CHANNEL_TYPE_DRUM:
                /* ドラム音源 */
                osc = oscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_NOISE_L);
                a = 0.0;
                d = 0.25;
                s = 0.0;
                r = 0.0;
                break;

            case CHANNEL_TYPE_SOUND:
            default:
                /* 通常音源 */
                osc = oscFc.createOscillator(OscillatorFactory.OSCILLATOR_NAME_SINE);
                break;
        }

        if (target != null) {
            target.clearOscillator(ch);
            target.addOscillator(ch, osc);
            
            target.setWaveReverse(ch, waveReverse);
            target.setValidNesSimulate(ch, validFesSim);

            Envelope env = target.getEnvelope(ch);
            env.setAttackTime(a);
            env.setDecayTime(d);
            env.setSustainLevel(s);
            env.setReleaseTime(r);
            env.setMaxAttackMills(ma);
            env.setMaxDecayMills(md);
            env.setMaxReleaseMills(mr);

            Modulator mod = target.getModulator(ch);
            mod.setDepth(modDepth);
        }
        target.systemReset();
    }

    public int getNumberOfChannel() {
        return channels.length;
    }

    @Override
    public void openDevice() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].openDevice();
        }
        envelopeFactory.timerStart();
        modulatorFactory.timerStart();
    }

    @Override
    public void closeDevice() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].closeDevice();
        }

        if (envelopeFactory != null) {
            envelopeFactory.dispose();
        }
        if (modulatorFactory != null) {
            modulatorFactory.dispose();
        }
    }

    public SoundSourceChannel getChannel(int ch) {
        return channels[ch];
    }

    public void setWaveRepaintListener(int ch, IWaveRepaintListener waveRepaintListener) {
        channels[ch].setWaveRepaintListener(waveRepaintListener);
    }

    @Override
    public boolean checkChannel(int ch) {
        return true;
    }

    @Override
    public void noteOn(int ch, int note, int velocity) {
        channels[ch].noteOn(ch, note, velocity);
    }

    @Override
    public void noteOff(int ch, int note) {
        channels[ch].noteOff(ch, note);
    }

    @Override
    public void pitchBend(int ch, int pitch) {
        channels[ch].pitchBend(ch, pitch);
    }

    @Override
    public void setExpression(int ch, int exp) {
        channels[ch].setExpression(ch, exp);
    }

    @Override
    public void pitchBendSenc(int ch, int sc) {
        channels[ch].pitchBendSenc(ch, sc);
    }

    @Override
    public void setNRPN(int ch, int nRPN) {
        channels[ch].setNRPN(ch, nRPN);
    }

    @Override
    public int getNRPN(int ch) {
        return channels[ch].getNRPN(ch);
    }

    @Override
    public void resetAllController(int ch) {
        channels[ch].resetAllController(ch);
    }

    @Override
    public void allNoteOff(int ch) {
        channels[ch].allNoteOff(ch);
    }

    @Override
    public void allSoundOff(int ch) {
        channels[ch].allSoundOff(ch);
    }

    @Override
    public void setVibratoRate(int ch, int rate) {
        channels[ch].setVibratoRate(ch, rate);
    }

    @Override
    public void setVibratoDepth(int ch, int depth) {
        channels[ch].setVibratoDepth(ch, depth);
    }

    @Override
    public void setVibratoDelay(int ch, int delay) {
        channels[ch].setVibratoDelay(ch, delay);
    }

    @Override
    public void setVariation(int ch, int val) {
        channels[ch].setVariation(ch, val);
    }

    @Override
    public void setPan(int ch, int pan) {
        channels[ch].setPan(ch, pan);
    }

    @Override
    public void setVolume(int ch, float volume) {
        channels[ch].setVolume(ch, volume);
    }

    @Override
    public void addOscillator(int ch, IOscillator osc) {
        channels[ch].addOscillator(ch, osc);
    }
    
    @Override
    public void clearOscillator(int ch) {
        channels[ch].clearOscillator(ch);
    }
    
    @Override
    public OscillatorSet getOscillatorSet(int ch) {
        return channels[ch].getOscillatorSet(ch);
    }

    @Override
    public Envelope getEnvelope(int ch) {
        return channels[ch].getEnvelope();
    }

    @Override
    public IOscillator getOscillator(int ch, int index) {
        return channels[ch].getOscillator(ch, index);
    }

    @Override
    public void systemReset() {
        for (int i = 0; i < 16; i++) {
            channels[i].systemReset();
        }
    }

    @Override
    public void setModulationDepth(int ch, int depth) {
        channels[ch].setModulationDepth(ch, depth);
    }

    @Override
    public Modulator getModulator(int ch) {
        return channels[ch].getModulator(ch);
    }

    @Override
    public void setWaveReverse(int ch, boolean isReverse) {
        channels[ch].setWaveReverse(ch, isReverse);
    }

    @Override
    public boolean isWaveReverse(int ch) {
        return channels[ch].isWaveReverse(ch);
    }

    @Override
    public void setValidNesSimulate(int ch, boolean isValidFesSimulate) {
        channels[ch].setValidNesSimulate(ch, isValidFesSimulate);
    }

    @Override
    public boolean isValidNesSimulate(int ch) {
        return channels[ch].isValidNesSimulate(ch);
    }
}
