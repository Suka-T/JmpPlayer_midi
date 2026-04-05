package jlib.midi;

public class MetaInfo {
    // length 未定義
    public static final int NON = -1;

    public int type;
    public int length = NON;

    public MetaInfo(int code, int length) {
        this.type = code;
        this.length = length;
    }
}
