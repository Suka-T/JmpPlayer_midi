package jmsynth;

import javax.sound.midi.MidiUnavailableException;

import jmsynth.midi.MidiInterface;

public class JMSynthEngine {

    /**
     * JMSynthインスタンスを取得
     *
     * @param isOpen
     *            true:open
     * @return
     */
    public static JMSoftSynthesizer getSoftSynthesizer(boolean isOpen) {
        JMSoftSynthesizer synth = new JMSoftSynthesizer();
        if (isOpen == true) {
            synth.openDevice();
        }
        return synth;
    }

    /**
     * JMSynthインスタンスを取得
     *
     * @return
     */
    public static JMSoftSynthesizer getSoftSynthesizer() {
        return getSoftSynthesizer(true);
    }

    /**
     * MIDIインターフェースでラップしたJMSynthインスタンスを取得
     *
     * @param isOpen
     *            true:open
     * @return
     */
    public static MidiInterface getMidiInterface(boolean isOpen) {
        JMSoftSynthesizer synth = getSoftSynthesizer(isOpen);
        return new MidiInterface(synth);
    }

    /**
     * MIDIインターフェースでラップしたJMSynthインスタンスを取得
     *
     * @return
     */
    public static MidiInterface getMidiInterface() {
        return getMidiInterface(true);
    }

    /**
     * JMSynthをアプリケーションとしてパッケージ化されたインスタンスを取得
     *
     * @param isOpen
     *            isOpen true:open
     * @return
     * @throws MidiUnavailableException
     */
    public static JMSynthApplication getJMSynthApplication(boolean isOpen) throws MidiUnavailableException {
        JMSynthApplication app = new JMSynthApplication();
        if (isOpen == true) {
            app.open();
        }
        return app;
    }

    /**
     * JMSynthをアプリケーションとしてパッケージ化されたインスタンスを取得
     *
     * @return
     * @throws MidiUnavailableException
     */
    public static JMSynthApplication getJMSynthApplication() throws MidiUnavailableException {
        return getJMSynthApplication(true);
    }

}
