package jmp.lang;

public class FontSet {
    public static final int FONT_OF_WIN = 0;
    public static final int FONT_OF_MAC = 1;
    public static final int FONT_OF_OTHR = 2;

    private boolean valid = true;
    private String charset = "UTF-16";
    private FontRsrc[] fonts = new FontRsrc[3];

    public FontSet(String charset, FontRsrc win, FontRsrc mac, FontRsrc other) {
        this.charset = charset;
        this.fonts[FONT_OF_WIN] = win;
        this.fonts[FONT_OF_MAC] = mac;
        this.fonts[FONT_OF_OTHR] = other;
    }

    public final FontRsrc getRsrc(int family) {
        return fonts[family];
    }

    public String getCharset() {
        return charset;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
