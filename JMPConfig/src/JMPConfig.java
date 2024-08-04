import jlib.plugin.JMidiPlugin;

public class JMPConfig extends JMidiPlugin {

    public static JMPConfigFrame MainFrame = null;

    public static void main(String[] args) {
        System.out.println("**JMPConfig**");
    }

    public JMPConfig() {
    }

    @Override
    public void close() {
        /*
         * プラグインが閉じられた際の処理を記述します。
         */
        MainFrame.setVisible(false);
    }

    @Override
    public void exit() {
        /*
         * JamPlayerが終了された際の処理を記述します。
         */
        if (MainFrame != null) {
            MainFrame.dispose();
        }
    }

    @Override
    public boolean isOpen() {
        /*
         * プラグインが開かれている状態を示す記述します。
         */
        return MainFrame.isVisible();
    }

    @Override
    public void initialize() {
        /*
         * JamPlayerが起動された際の処理を記述します。
         */
        MainFrame = new JMPConfigFrame();
    }

    @Override
    public void open() {
        /*
         * プラグインが開かれた際の処理を記述します。
         */
        MainFrame.setVisible(true);
    }

    @Override
    protected void noteOn(int channel, int midiNumber, int velocity, long timeStamp, short senderType) {
        /*
         * ノートオンイベントを送信した際の処理を記述します。
         */
        MainFrame.setNoteOn(channel, midiNumber);
    }

    @Override
    protected void noteOff(int channel, int midiNumber, long timeStamp, short senderType) {
        /*
         * ノートオフイベントを送信した際の処理を記述します。
         */
        MainFrame.setNoteOff(channel, midiNumber);
    }

    @Override
    protected void programChange(int channel, int programNumber, long timeStamp, short senderType) {
        /*
         * プログラムチェンジイベントを送信した際の処理を記述します。
         */
    }

    @Override
    protected void pitchBend(int channel, int pbValue, long timeStamp, short senderType) {
        /*
         * ピッチベンドイベントを送信した際の処理を記述します。
         */
    }

}
