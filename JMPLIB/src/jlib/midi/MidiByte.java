package jlib.midi;

import java.util.ArrayList;
import java.util.List;

/**
 * MIDIメッセージバイト定義
 *
 * @author akt
 *
 */
public class MidiByte {

    private static List<String> cloneIdentList(String[] idents, boolean deleteBlank) {
        List<String> ret = new ArrayList<String>();
        for (String ident : idents) {
            if (deleteBlank == true && ident.isEmpty() == true) {
                continue;
            }
            ret.add(ident);
        }
        return ret;
    }

    public static List<String> cloneCommandIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineCommand.IDENTS, deleteBlank);
    }

    public static List<String> cloneControlChangeIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineControlChange.IDENTS, deleteBlank);
    }

    public static List<String> cloneNoteNumberIdentList(boolean deleteBlank) {
        return cloneIdentList(DefineNoteNumberGM.IDENTS, deleteBlank);
    }

    /**
     * ステータスバイト取得
     *
     * @param data
     * @param length
     * @return
     */
    public static int getStatus(byte[] data, int length) {
        if (length < 1) {
            return 0;
        }
        return (data[0] & 0xff);
    }

    /**
     * チャンネル取得
     *
     * @param data
     * @param length
     * @return
     */
    public static int getChannel(byte[] data, int length) {
        return (getStatus(data, length) & 0x0f);
    }

    /**
     * コマンド取得
     *
     * @param data
     * @param length
     * @return
     */
    public static int getCommand(byte[] data, int length) {
        return (getStatus(data, length) & 0xf0);
    }

    /**
     * 第1データバイト取得
     *
     * @param data
     * @param length
     * @return
     */
    public static int getData1(byte[] data, int length) {
        if (length < 2) {
            return 0;
        }
        return (data[1] & 0xff);
    }

    /**
     * 第2データバイト取得
     *
     * @param data
     * @param length
     * @return
     */
    public static int getData2(byte[] data, int length) {
        if (length < 3) {
            return 0;
        }
        return (data[2] & 0xff);
    }

    /**
     * ステータスバイトがチャンネルメッセージか
     *
     * @param data
     * @param length
     * @return
     */
    public static boolean isChannelMessage(byte[] data, int length) {
        return isChannelMessage(getStatus(data, length));
    }

    /**
     * ステータスバイトがチャンネルメッセージか
     *
     * @param statusByte
     *            ステータスバイト
     * @return
     */
    public static boolean isChannelMessage(int statusByte) {
        int command = statusByte & 0xf0;
        return command < 0xf0;
    }

    /**
     * ステータスバイトがシステムメッセージか
     *
     * @param data
     * @param length
     * @return
     */
    public static boolean isSystemMessage(byte[] data, int length) {
        return isSystemMessage(getStatus(data, length));
    }

    /**
     * ステータスバイトがシステムメッセージか
     *
     * @param statusByte
     *            ステータスバイト
     * @return
     */
    public static boolean isSystemMessage(int statusByte) {
        int command = statusByte & 0xf0;
        return command >= 0xf0;
    }

    /**
     * メタメッセージか
     *
     * @param data
     * @param length
     * @return
     */
    public static boolean isMetaMessage(byte[] data, int length) {
        return isMetaMessage(getStatus(data, length));
    }

    /**
     * メタメッセージか
     *
     * @param statusByte
     *            ステータスバイト
     * @return
     */
    public static boolean isMetaMessage(int statusByte) {
        if (statusByte == META) {
            return true;
        }
        return false;
    }

    /**
     * LSB(7bit)とMSB(7bit)を結合した14bitの値を取得する
     *
     * @param lsb
     *            LSB(7bit)
     * @param msb
     *            MSB(7bit)
     * @return 14bit value
     */
    public static int mergeLsbMsbValue(int lsb, int msb) {
        int ret = 0x00;
        int bit = 7;
        // 先にMSBをシフトする
        ret = msb << bit;

        int mask = (8 - bit) >> 0xff;
        int value = (ret | (lsb & mask));
        return value;
    }

    /**
     * ステータスバイトからコマンド文字列に変換する
     *
     * @param statusByte
     *            ステータスバイト
     * @return コマンド文字列
     */
    public static String convertByteToChannelCommandString(int statusByte) {
        String res = "--";
        int command = (statusByte & 0xf0);
        if (command < 0xf0) {
            // チャンネルメッセージ
            res = DefineCommand.dataToIdent(command);
        }
        else {
            // システムメッセージ
            res = DefineCommand.dataToIdent(statusByte);
        }
        return res;
    }

    /**
     * コマンド文字列からステータスバイトに変換する
     *
     * @param ident
     *            コマンド文字列
     * @return ステータスバイト
     */
    public static int convertChannelCommandStringToByte(String ident) {
        return DefineCommand.identToData(ident);
    }

    /**
     * 第1データバイトからコントロールチェンジ文字列を取得する
     *
     * @param data1Byte
     *            第1データバイト
     * @return コントロールチェンジ文字列
     */
    public static String convertByteToControlChangeString(int data1Byte) {
        return DefineControlChange.dataToIdent(data1Byte);
    }

    public static int convertControlChangeStringToByte(String ident) {
        return DefineControlChange.identToData(ident);
    }

    /**
     * 第1データバイトからノート情報文字列を取得する
     *
     * @param data1Byte
     *            第1データバイト
     * @return ノート情報文字列
     */
    public static String convertByteToNoteString(int data1Byte) {
        return DefineNoteNumberGM.dataToIdent(data1Byte);
    }

    public static int convertNoteStringToByte(String ident) {
        return DefineNoteNumberGM.identToData(ident);
    }

    public static String convertByteToMetaString(int type) {
        return DefineMeta.dataToIdent(type);
    }

    public static int convertTypeStringToByte(String ident) {
        return DefineMeta.identToData(ident);
    }

    /*
     * SysEx定義
     */
    /** GMシステムオンメッセージ */
    public static final byte[] GM_SYSTEM_ON = new byte[] { (byte) 0xf0, 0x7e, 0x7f, 0x09, 0x01, (byte) 0xf7 };
    /** XGシステムオンメッセージ */
    public static final byte[] XG_SYSTEM_ON = new byte[] { (byte) 0xf0, 0x43, 0x10, 0x4c, 0x00, 0x00, 0x7e, 0x00, (byte) 0xf7 };
    /** GSシステムリセットメッセージ */
    public static final byte[] GS_RESET = new byte[] { (byte) 0xf0, 0x41, 0x10, 0x42, 0x12, 0x40, 0x00, 0x7f, 0x00, 0x41, (byte) 0xf7 };

    /**
     * Metaヘッダーバイト
     *
     */
    public static final int META = 0xff;

    /**
     * シーケンス番号を記す。<br>
     * トラック最初の０でないデルタタイムの前で送出可能なMIDIイベントの前に置く。<br>
     * フォーマット２において、各パターンの呼び出しに使用する。
     */
    public static final MetaInfo SEQUENCE_NUMBER = new MetaInfo(DefineMeta.SEQUENCE_NUMBER, 2);

    /**
     * 任意のテキスト（文字列）をSMF内に記載するために使う。
     */
    public static final MetaInfo TEXT = new MetaInfo(DefineMeta.TEXT, MetaInfo.NON);

    /**
     * 著作権表示用のテキスト文字列。トラックチャンクの先頭イベント（タイム０）とする。
     */
    public static final MetaInfo COPYRIGHT_NOTICE = new MetaInfo(DefineMeta.COPYRIGHT_NOTICE, MetaInfo.NON);

    /**
     * シーケンス／トラック名を記述するテキスト文字列。<br>
     * シーケンス名はフォーマット０のトラック内か、 フォーマット１の最初のトラック内に記載する。<br>
     * その他の場合は、トラック名として扱う。
     */
    public static final MetaInfo TRACK_NAME = new MetaInfo(DefineMeta.TRACK_NAME, MetaInfo.NON);

    /**
     * 各チャンネルに対応する楽器名を記述する。
     */
    public static final MetaInfo INSTRUMENT_NAME = new MetaInfo(DefineMeta.INSTRUMENT_NAME, MetaInfo.NON);

    /**
     * 歌詞を記述する場合に使用するテキスト文字列。
     */
    public static final MetaInfo LYRICS = new MetaInfo(DefineMeta.LYRICS, MetaInfo.NON);

    /**
     * フォーマット０／１の最初のトラックで使用し、シーケンス内のポイント名を記述するテキスト文字列。
     */
    public static final MetaInfo MARKER = new MetaInfo(DefineMeta.MARKER, MetaInfo.NON);

    /**
     * ビデオなどに対して指示を出すときに使う。テキスト文字列。
     */
    public static final MetaInfo CUE_POINT = new MetaInfo(DefineMeta.CUE_POINT, MetaInfo.NON);

    /**
     *
     */
    public static final MetaInfo PROGRAM_NAME = new MetaInfo(DefineMeta.PROGRAM_NAME, MetaInfo.NON);

    /**
     *
     */
    public static final MetaInfo DEVICE_NAME = new MetaInfo(DefineMeta.DEVICE_NAME, MetaInfo.NON);

    /**
     * メタイベントやSysExイベントなどチャンネル属性を持たないイベントに対して、チャンネル属性を 付加させる。<br>
     * MIDIイベントや次のチャンネルプリフィックスがでてくるまで有効。
     */
    public static final MetaInfo CHANNEL_PREFIX = new MetaInfo(DefineMeta.CHANNEL_PREFIX, 1);

    /**
     *
     */
    public static final MetaInfo PORT = new MetaInfo(DefineMeta.PORT, MetaInfo.NON);

    /**
     * トラックの終了を示す。トラックの最終位置に必ず配置する。データはないので、データ長は０。
     */
    public static final MetaInfo END_OF_TRACK = new MetaInfo(DefineMeta.END_OF_TRACK, 0);

    /**
     * ４分音符の長さを?sec単位で表し、一拍当たりの時間でテンポを表す。
     */
    public static final MetaInfo SET_TEMPO = new MetaInfo(DefineMeta.SET_TEMPO, 3);

    /**
     * トラックチャンクがスタートすべきSMPTE時間を示す。<br>
     * トラックの始まりの全ての０でないデルタタイムの MIDIイベントの前に置く。
     */
    public static final MetaInfo SMPTE_OFFSET = new MetaInfo(DefineMeta.SMPTE_OFFSET, 5);

    /**
     * [nn:dd:cc:bb]拍子記号の分子nn、２のdd乗で表される分母（ddが２の場合４、３の場合８）、
     * メトロノーム１カウントあたりのMIDIクロック数cc、４分音符中の３２分音符数bb。
     */
    public static final MetaInfo BEAT = new MetaInfo(DefineMeta.BEAT, 4);

    /**
     * [sf:mi]シャープまたはフラット記号の数を表すsf、メジャー／マイナーを示すmi。<br>
     * sfはフラットの数を 表すときはマイナス数値になる。また、miはメジャーのとき０、マイナのとき１になる。
     */
    public static final MetaInfo KEY_SEGNATURE = new MetaInfo(DefineMeta.KEY_SEGNATURE, 2);

    /**
     * シーケンサ固有のデータに使用。データは初めにマニファクチャラーズＩＤを表記する。
     */
    public static final MetaInfo SEQUENCER_SPECFIC = new MetaInfo(DefineMeta.SEQUENCER_SPECFIC, MetaInfo.NON);

    /**
     * ステータスバイト定義
     *
     * @author akt
     *
     */
    public class Status {

        /**
         * チャンネルメッセージバイト定義
         *
         * @author akt
         *
         */
        public class Channel {

            /**
             * チャンネルボイスメッセージバイト定義
             *
             * @author akt
             *
             */
            public class ChannelVoice {
                public class Fst {

                    /**
                     * ノートオフ
                     */
                    public static final int NOTE_OFF = DefineCommand.NOTE_OFF;

                    /**
                     * ノートオン
                     */
                    public static final int NOTE_ON = DefineCommand.NOTE_ON;

                    /**
                     * ポリフォニックキープレッシャー <br>
                     * キー毎のアフタータッチ
                     */
                    public static final int POLYPHONIC_KEY_PRESSURE = DefineCommand.POLYPHONIC_KEY_PRESSURE;

                    /**
                     * コントロールチェンジ
                     */
                    public static final int CONTROL_CHANGE = DefineCommand.CONTROL_CHANGE;

                    /**
                     * プログラムチェンジ
                     */
                    public static final int PROGRAM_CHANGE = DefineCommand.PROGRAM_CHANGE;

                    /**
                     * チャンネルプレッシャー <br>
                     * チャンネル全体のアフタータッチ
                     */
                    public static final int CHANNEL_PRESSURE = DefineCommand.CHANNEL_PRESSURE;

                    /**
                     * ピッチベンド <br>
                     * MSB,LSBを連結し14ビットの値(-8192~+8191)として扱う
                     */
                    public static final int PITCH_BEND = DefineCommand.PITCH_BEND;
                }
            }

            /**
             * チャンネルモードメッセージバイト定義
             *
             * @author akt
             *
             */
            public class ChannelMode {
                public class Fst {
                    /**
                     * コントロールチェンジ
                     */
                    public static final int CONTROL_CHANGE = MidiByte.Status.Channel.ChannelVoice.Fst.CONTROL_CHANGE;
                }

                public class Snd {
                    /**
                     * オールサウンドオフ <br>
                     * 発音中の音を残響やホールドされているものを含めて即時に停止する
                     */
                    public static final int ALL_SOUND_OFF = DefineControlChange.ALL_SOUND_OFF;

                    /**
                     * リセットオールコントローラー
                     */
                    public static final int RESET_ALL_CONTROLLER = DefineControlChange.RESET_ALL_CONTROLLER;

                    /**
                     * ローカルコントロール <br>
                     * オフにするとそのチャンネルはMIDI経由でのみ制御される
                     */
                    public static final int LOCAL_CONTROL = DefineControlChange.LOCAL_CONTROL;

                    /**
                     * オールノートオフ <br>
                     * 発音中の音をノートオフ状態にする(オールサウンドオフよりも弱い)
                     */
                    public static final int ALL_NOTE_OFF = DefineControlChange.ALL_NOTE_OFF;

                    /**
                     * オムニオフ
                     */
                    public static final int OMNI_OFF = DefineControlChange.OMNI_OFF;

                    /**
                     * オムニオン
                     */
                    public static final int OMNI_ON = DefineControlChange.OMNI_ON;

                    /**
                     * モノモード
                     */
                    public static final int MONO_MODE = DefineControlChange.MONO_ON;

                    /**
                     * ポリモード
                     */
                    public static final int POLY_MODE = DefineControlChange.POLY_ON;
                }

            }
        }

        /**
         * システムバイトメッセージバイト定義
         *
         * @author akt
         *
         */
        public class System {
            /**
             * システムコモンメッセージバイト定義
             *
             * @author akt
             *
             */
            public class SystemCommon {
                public class Fst {
                    /**
                     * システムエクスクルーシブ開始<br>
                     * Fst: マニュファクチャID 1バイトまたは3バイト
                     */
                    public static final int SYSEX_BEGIN = DefineCommand.SYSEX;

                    /**
                     * MIDIタイムコード<br>
                     * Fst: td hex (t=0～7:フレームL/H,秒L/H,分L/H,時L/H)
                     */
                    public static final int MIDI_TIME_CODE = DefineCommand.MIDI_TIME_CODE;

                    /**
                     * ソングポジション<br>
                     * 曲先頭からのMIDIビートのカウンター(1MIDIビートは6MIDIクロック)<br>
                     * Fst: LSB(7bit) Snd: MSB(7bit)
                     */
                    public static final int SONG_POSITION = DefineCommand.SONG_POSITION;

                    /**
                     * ソングセレクト<br>
                     * 複数のソングを扱う場合に使用<br>
                     * Fst: ソング番号
                     */
                    public static final int SONG_SELECT = DefineCommand.SONG_SELECT;

                    /**
                     * チューンリクエスト<br>
                     * アナログシンセの自動チューニング機能を動作させる
                     */
                    public static final int TUNE_SELECT = DefineCommand.TUNE_SELECT;

                    /**
                     * システムエクスクルーシブ終了
                     */
                    public static final int SYSEX_END = DefineCommand.SYSEX_SP;
                }

            }

            /**
             * システムリアルタイムメッセージバイト定義
             *
             * @author akt
             *
             */
            public class SystemRealTime {
                public class Fst {
                    /**
                     * MIDIクロック<br>
                     * 四分音符あたり24回送信
                     */
                    public static final int MIDI_CLOCK = DefineCommand.MIDI_CLOCK;

                    /**
                     * 現在のシーケンスを開始
                     */
                    public static final int START = DefineCommand.START;

                    /**
                     * 前回停止した位置から再生する
                     */
                    public static final int CONTINUE = DefineCommand.CONTINUE;

                    /**
                     * 再生中のシーケンスを停止
                     */
                    public static final int STOP = DefineCommand.STOP;

                    /**
                     * アクティブセンシング<br>
                     * 送出側は300mSec以内に1度送出。<br>
                     * 受信側は受信していたアクティブセンシングが途絶えた場合、音を停止
                     */
                    public static final int ACTIVE_SENSING = DefineCommand.ACTIVE_SENSING;

                    /**
                     * 電源投入時の状態にリセットする<br>
                     * 使用が推奨されるものではなく、<br>
                     * 送信側機器は電源投入後、自動的に送出しない
                     */
                    public static final int RESET = DefineCommand.RESET;
                }
            }
        }/* System */
    }/* Status */
}
