package jmp.lang;

import java.nio.charset.CharacterCodingException;

import jmp.util.JmpUtil;

public class LanguageWords {

    /** Blankテキスト */
    public static final String BLANK_TEXT = "???";

    private String[] words = null;

    public LanguageWords(String... strings) {
        words = strings;
    }

    public String[] getWords() {
        return words;
    }

    public int getSize() {
        return words.length;
    }

    public String getWord(int index, String charset) {
        String org = "";
        String dst = "";
        if (index < 0 || words.length <= index) {
            org = BLANK_TEXT;
        }
        else {
            org = words[index];
        }

        try {
            dst = JmpUtil.convertCharset(org, charset);
        }
        catch (CharacterCodingException e) {
            dst = new String(org);
        }
        return VBA.replaceSpecialTag(dst);
    }
}
