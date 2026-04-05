package jmsynth.midi;

import java.util.Arrays;

public class MidiUtil {
    /** GMシステムオンメッセージ */
    public static final byte[] GM_SYSTEM_ON = new byte[] { (byte) 0xf0, 0x7e, 0x7f, 0x09, 0x01, (byte) 0xf7 };
    /** XGシステムオンメッセージ */
    public static final byte[] XG_SYSTEM_ON = new byte[] { (byte) 0xf0, 0x43, 0x10, 0x4c, 0x00, 0x00, 0x7e, 0x00, (byte) 0xf7 };
    /** GSシステムリセットメッセージ */
    public static final byte[] GS_RESET = new byte[] { (byte) 0xf0, 0x41, 0x10, 0x42, 0x12, 0x40, 0x00, 0x7f, 0x00, 0x41, (byte) 0xf7 };

    /** Metaヘッダーバイト */
    public static final int META = 0xff;

    public static boolean isGmSystemOn(byte[] data) {
        return Arrays.equals(data, MidiUtil.GM_SYSTEM_ON);
    }

    public static boolean isXgSystemOn(byte[] data) {
        return Arrays.equals(data, MidiUtil.XG_SYSTEM_ON);
    }

    public static boolean isGsReset(byte[] data) {
        return Arrays.equals(data, MidiUtil.GS_RESET);
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
}
