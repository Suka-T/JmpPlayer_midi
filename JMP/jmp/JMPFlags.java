package jmp;

import jmp.core.JMPCore;

/**
 * アプリケーション全体で管理するフラグ
 *
 * @author akt
 *
 */
public class JMPFlags {
    /** ログ出力クラス */
    public static class Log {
        private static void cprintImpl(String msg, boolean ln, boolean console_out) {
            boolean out = false;
            if (JMPFlags.CoreConsoleOut == true || JMPFlags.DebugMode == true) {
                out = true;
            }
            if (out == true) {
                if (ln == true) {
                    System.out.println(msg);
                }
                else {
                    System.out.print(msg);
                }
            }
            if (out == true || console_out == true) {
                if (ln == true) {
                    JMPCore.getSystemManager().consoleOutln(msg);
                }
                else {
                    JMPCore.getSystemManager().consoleOut(msg);
                }
            }
        }

        /** コンソール出力 */
        public static void cprint(String msg, boolean console_out) {
            cprintImpl(msg, false, console_out);
        }

        public static void cprint(String msg) {
            cprintImpl(msg, false, false);
        }

        /** コンソール出力 */
        public static void cprintln(String msg, boolean console_out) {
            cprintImpl(msg, true, console_out);
        }

        public static void cprintln(String msg) {
            cprintImpl(msg, true, false);
        }

        public static void cprintln(boolean console_out) {
            cprintImpl("", true, console_out);
        }

        public static void cprintln() {
            cprintImpl("", true, false);
        }
    }

    /** ライブラリモード */
    public static boolean LibraryMode = true;

    /** デバッグモード */
    public static boolean DebugMode = false;

    /** 起動時デバッグコンソール表示 */
    public static boolean InvokeToConsole = false;

    /** アクティベートフラグ */
    public static boolean ActivateFlag = false;

    /** 自動シンセ接続 */
    public static boolean StartupAutoConectSynth = false;

    /** 履歴に残さずロードする */
    public static boolean NoneHistoryLoadFlag = false;

    /** ロード中フラグ */
    public static boolean NowLoadingFlag = false;

    /** プラグインをロードしない */
    public static boolean NonPluginLoadFlag = false;

    /** 非同期Midiパケットを使用するか */
    public static boolean UseUnsynchronizedMidiPacket = false;

    /** コアの初期化・終了初期の結果をコンソール出力するか */
    public static boolean CoreConsoleOut = false;

    /** 高速MIDIメッセージ使用 */
    public static boolean UseHispeedMidiMessage = false;

    /** Window自動配置調整 */
    public static boolean WindowAutomationPosFlag = true;

    /** 再生中の点滅表示を制御するためのフラグ。主にシーケンスバーで使用 */
    public static boolean PlayingTimerToggleFlag = false;

    /** 次の曲再生検索フラグ */
    public static boolean NextPlayFlag = false;

}
