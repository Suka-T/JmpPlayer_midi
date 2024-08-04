package jmp.skin;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import function.Utility;
import jmp.core.SystemManager;

public class SkinLocalConfig {
    public static final String KEY_TRANSPARENT = "TRANSPARENT";
    public static final String KEY_CONVERT_TRANS = "CONVERT_TRANS";
    public static final String KEY_BTN_BACKGROUND = "BTN_BACKGROUND";
    public static final String KEY_APP_BACKGROUND = "APPLICATION_BACKGROUND";

    public static final Color DEFAULT_TRANS_COLOR = new Color(255, 127, 39);
    public static final Color DEFAULT_BTN_BACKGROUND = Utility.convertCodeToHtmlColor("#888888");
    public static final Color DEFAULT_APP_BACKGROUND = SystemManager.DEFAULT_PLAYER_BACK_COLOR;

    private boolean isConvertTransparent = false;
    private Color transparentColor = DEFAULT_TRANS_COLOR;
    private Color btnBackgroundColor = DEFAULT_BTN_BACKGROUND;
    private Color appBackgroundColor = DEFAULT_APP_BACKGROUND;

    public SkinLocalConfig() {
    }

    public void initialize() {
        isConvertTransparent = false;
        transparentColor = DEFAULT_TRANS_COLOR;
        btnBackgroundColor = DEFAULT_BTN_BACKGROUND;
        appBackgroundColor = DEFAULT_APP_BACKGROUND;
    }

    public void read(File file) throws IOException {
        List<String> content = Utility.getTextFileContents(file.getPath());

        for (String line : content) {
            if (line.startsWith("#") == true) {
                continue;
            }

            String[] sLine = line.split("=");
            if (sLine.length >= 2) {
                String key = sLine[0];
                String value = sLine[1];
                if (key.equalsIgnoreCase(KEY_TRANSPARENT) == true) {
                    try {
                        transparentColor = Utility.convertCodeToHtmlColor(value);
                    }
                    catch (NumberFormatException nfe) {
                        transparentColor = DEFAULT_TRANS_COLOR;
                    }
                }
                else if (key.equalsIgnoreCase(KEY_CONVERT_TRANS) == true) {
                    isConvertTransparent = Utility.tryParseBoolean(value, false);
                }
                else if (key.equalsIgnoreCase(KEY_BTN_BACKGROUND) == true) {
                    Color color = new Color(0, 0, 0, 0);
                    if (value.equalsIgnoreCase("TRANS") == false) {
                        try {
                            color = Utility.convertCodeToHtmlColor(value);
                        }
                        catch (NumberFormatException nfe) {
                            color = DEFAULT_BTN_BACKGROUND;
                        }
                    }
                    btnBackgroundColor = color;
                }
                else if (key.equalsIgnoreCase(KEY_APP_BACKGROUND) == true) {
                    Color color = new Color(0, 0, 0, 0);
                    try {
                        color = Utility.convertCodeToHtmlColor(value);
                    }
                    catch (NumberFormatException nfe) {
                        color = DEFAULT_APP_BACKGROUND;
                    }
                    appBackgroundColor = color;
                }
            }
        }
    }

    public void setTransparentColor(Color transparentColor) {
        this.transparentColor = transparentColor;
    }

    public Color getTransparentColor() {
        return transparentColor;
    }

    public boolean isConvertTransparent() {
        return isConvertTransparent;
    }

    public void setConvertTransparent(boolean isConvertTransparent) {
        this.isConvertTransparent = isConvertTransparent;
    }

    public Color getBtnBackgroundColor() {
        return btnBackgroundColor;
    }

    public void setBtnBackgroundColor(Color btnBackgroundColor) {
        this.btnBackgroundColor = btnBackgroundColor;
    }

    public Color getAppBackgroundColor() {
        return appBackgroundColor;
    }

    public void setAppBackgroundColor(Color appBackgroundColor) {
        this.appBackgroundColor = appBackgroundColor;
    }
}
