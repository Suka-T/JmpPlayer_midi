package jmp.midi;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

import function.Utility;

// 仮実装
@SuppressWarnings("unused")
public class JMPMidiReader {

    public class ModeSet {
        public int mode = 0;
        public int len = 0;

        public ModeSet(int mode, int len) {
            this.mode = mode;
            this.len = len;
        }
    }

    /* サーチモードタイプ */
    private static final int SEARCH_MODE_CHUNK_LEN = 4;
    private static final int SEARCH_MODE_HEAD_CHUNK = 1000;
    private static final int SEARCH_MODE_TRACK_CHUNK = 1001;
    // ヘッダチャンク
    private static final int SEARCH_MODE_HEAD_LENGTH = 0;
    private static final int SEARCH_MODE_HEAD_FORMAT = 1;
    private static final int SEARCH_MODE_HEAD_NUM_OF_TRACK = 2;
    private static final int SEARCH_MODE_HEAD_RESOLUTION = 3;

    // トラックチャンク
    private static final int SEARCH_MODE_TRACK_LENGTH = 100;
    private static final int SEARCH_MODE_TRACK_DATA = 101;

    private static final int SEARCH_MODE_END = -1;

    private ModeSet searchMode_head[] = {
            // 検索モード, 検索バイト長
            new ModeSet(SEARCH_MODE_HEAD_LENGTH, 4), // データ長
            new ModeSet(SEARCH_MODE_HEAD_FORMAT, 2), // フォーマット
            new ModeSet(SEARCH_MODE_HEAD_NUM_OF_TRACK, 2), // トラック数
            new ModeSet(SEARCH_MODE_HEAD_RESOLUTION, 2), // 分解能
            new ModeSet(SEARCH_MODE_END, 0), // 分解能
    };
    private ModeSet searchMode_track[] = {
            // 検索モード, 検索バイト長
            new ModeSet(SEARCH_MODE_TRACK_LENGTH, 4), // データ長
            new ModeSet(SEARCH_MODE_TRACK_DATA, -1), // データ本体可変
            new ModeSet(SEARCH_MODE_END, 0), // 分解能
    };

    private File midiFile = null;

    //
    private boolean isReadDeltaTime = true;
    private int curByteCount = 0;
    private List<Byte> curByteBuf = null;
    private int curTrackIndex = 0;

    public JMPMidiReader(File file) {
        this.midiFile = file;
    }

    public Sequence read() throws InvalidMidiDataException, IOException {
        Sequence sequence = null;
        curByteBuf = new LinkedList<Byte>();
        curByteCount = 0;

        // 格納変数
        float divisionType = 0.0f;
        int format = 0;
        int resolution = 0;
        int numTracks = 0;

        byte[] data = Utility.getBinaryContents(midiFile);

        int iMode = 0;

        boolean isNext = false;

        ModeSet[] curModeSet = null;
        int curChunk = -1;
        int curMode = 0;
        int curLen = SEARCH_MODE_CHUNK_LEN;
        _debugPrint("read");
        for (int i = 0; i < data.length; i += curLen) {

            if (curChunk == -1) {
                curLen = SEARCH_MODE_CHUNK_LEN;
            }
            else {
                curMode = curModeSet[iMode].mode;
                curLen = curModeSet[iMode].len;
                if (curLen == -1 || curMode == SEARCH_MODE_TRACK_DATA) {
                    curLen = 1;
                }
            }

            byte[] word = new byte[curLen];
            int si = i;
            for (int wi = 0; wi < curLen; wi++) {
                word[wi] = data[si];
                si++;
                if (si >= data.length) {
                    si = data.length - 1;
                }
            }

            if (curChunk == -1) {
                if (equalsByte(word, new byte[] { 0x4d, 0x54, 0x68, 0x64 }) == true) {
                    // ヘッダチャンク
                    curModeSet = searchMode_head;
                    curChunk = SEARCH_MODE_HEAD_CHUNK;
                    curLen = SEARCH_MODE_CHUNK_LEN;
                    iMode = 0;
                    _debugPrint("HEAD_CHUNK");
                }
                else if (equalsByte(word, new byte[] { 0x4d, 0x54, 0x72, 0x6b }) == true) {
                    // トラックチャンク
                    curModeSet = searchMode_track;
                    curChunk = SEARCH_MODE_TRACK_CHUNK;
                    curLen = SEARCH_MODE_CHUNK_LEN;
                    iMode = 0;
                    _debugPrint("TRACK_CHUNK");
                }
                else {
                    curLen = 1;
                }
                continue;
            }

            // 上から順番に実行
            switch (curMode) {

                case SEARCH_MODE_HEAD_LENGTH: {
                    int headLen = convertToInt(word);
                    isNext = true;

                    _debugPrint("len:" + headLen);
                }
                    break;

                case SEARCH_MODE_HEAD_FORMAT: {
                    format = convertToShort(word);
                    isNext = true;

                    _debugPrint("format:" + format);
                }
                    break;

                case SEARCH_MODE_HEAD_NUM_OF_TRACK: {
                    numTracks = convertToShort(word);
                    isNext = true;

                    _debugPrint("numTracks:" + numTracks);
                }
                    break;

                case SEARCH_MODE_HEAD_RESOLUTION: {
                    resolution = convertToShort(word);
                    isNext = true;

                    _debugPrint("resolution:" + resolution);
                }
                    break;

                case SEARCH_MODE_TRACK_LENGTH: {
                    int trackLen = convertToInt(word);
                    isNext = true;

                    _debugPrint("trackLen:" + trackLen);
                    sequence = new Sequence(divisionType, resolution, numTracks);
                    curByteBuf = new LinkedList<Byte>();
                    curByteCount = 0;
                    isReadDeltaTime = true;
                }
                    break;

                case SEARCH_MODE_TRACK_DATA: {
                    byte td = word[0];
                    if (isReadDeltaTime == true) {
                        boolean endFlag = false;
                        if ((td & 0x80) == 0x80) {
                            // 繰り上げフラグあり
                            endFlag = false;
                        }
                        else {
                            endFlag = true;
                        }
                        curByteBuf.add(td);

                        if (endFlag == true) {
                            byte[] aBuf = new byte[curByteBuf.size()];
                            for (int ii = 0; ii < aBuf.length; ii++) {
                                // aBuf[ii] = curByteBuf
                            }
                        }
                    }
                    int trackLen = convertToInt(word);
                    isNext = false;

                    _debugPrint("trackData:" + trackLen);
                }
                    break;

                case SEARCH_MODE_END:
                default: {
                    isNext = false;
                    curChunk = -1;
                    curLen = 1;
                }
                    break;
            }

            if (isNext == true) {
                iMode++;
                isNext = false;
            }
        }
        return sequence;
    }

    private boolean equalsByte(byte[] d1, byte[] d2) {
        if (d1.length != d2.length) {
            return false;
        }

        boolean ret = true;
        for (int i = 0; i < d1.length; i++) {
            if (d1[i] != d2[i]) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private short convertToShort(byte[] d) {
        short ret = 0;
        for (byte b : d) {
            ret = (short) ((ret << 2) + (b & 0xff));
        }
        return ret;
    }

    private int convertToInt(byte[] d) {
        int ret = 0;
        for (byte b : d) {
            ret = (ret << 4) + (b & 0xff);
        }
        return ret;
    }

    private void _debugPrint(String str) {
        System.out.println(str);
    }
}
