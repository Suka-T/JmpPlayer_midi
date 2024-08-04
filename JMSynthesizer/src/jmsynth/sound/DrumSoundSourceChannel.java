package jmsynth.sound;

import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.IOscillator;

public class DrumSoundSourceChannel extends SoundSourceChannel {

    class TimeoutNoteOffThread extends Thread {
        private boolean isRunnable = true;

        public TimeoutNoteOffThread() {
            super();
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            super.run();
            while (isRunnable) {
                try {
                    long current = System.currentTimeMillis();
                    if (envelope != null) {
                        long a = envelope.getAttackMills();
                        long d = envelope.getDecayMills();
                        if (envelope.getSustainLevel() <= 0.0) {
                            for (int i = 0; i < toneManager.getPlayingToneLength(); i++) {
                                Tone tone = toneManager.getPlayingTone(i);
                                if (tone != null) {
                                    long startTime = tone.getStartMills();
                                    long elapsedTime = current - startTime;
                                    if ((a + d) < elapsedTime) {
                                        noteOffImpl(tone.getNote());
                                    }
                                }
                            }
                        }
                    }
                    Thread.sleep(1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void exit() {
            isRunnable = false;
        }
    }

    private TimeoutNoteOffThread thread = null;

    public DrumSoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope, Modulator modulator) {
        super(channel, oscType, polyphony, envelope, modulator);
        thread = new TimeoutNoteOffThread();
    }

    public DrumSoundSourceChannel(int channel, IOscillator oscType, int polyphony, Envelope envelope) {
        super(channel, oscType, polyphony, envelope);
        thread = new TimeoutNoteOffThread();
    }

    public DrumSoundSourceChannel(int channel, IOscillator oscType, int polyphony) {
        super(channel, oscType, polyphony);
        thread = new TimeoutNoteOffThread();
    }

    @Override
    public void noteOff(int ch, int note) {
        /* ノートオフ */
        if (envelope.getSustainLevel() > 0.0) {
           super.noteOff(ch, note);
        }
    }

    @Override
    public void openDevice() {
        thread.start();
        super.openDevice();
    }

    @Override
    public void closeDevice() {
        thread.exit();
        super.closeDevice();
    }
}
