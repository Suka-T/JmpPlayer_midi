package jmp.util.toolkit;

import java.awt.Color;

import javax.swing.filechooser.FileFilter;

import function.Platform;
import function.Utility;
import jlib.util.IUtilityToolkit;
import jmp.util.JmpUtil;

public class DefaultUtilityToolkit implements IUtilityToolkit {

    public DefaultUtilityToolkit() {
    }

    @Override
    public String getNewLine() {
        return Platform.getNewLine();
    }

    @Override
    public Color convertCodeToHtmlColor(String code) throws NumberFormatException {
        return Utility.convertCodeToHtmlColor(code);
    }

    @Override
    public String convertHtmlColorToCode(Color htmlColor) throws NumberFormatException {
        return Utility.convertHtmlColorToCode(htmlColor);
    }

    @Override
    public String extensions2Str(String... ex) {
        return JmpUtil.genExtensions2Str(ex);
    }

    @Override
    public String[] str2Extensions(String str) {
        return JmpUtil.genStr2Extensions(str);
    }

    @Override
    public FileFilter createFileFilter(String exName, String... ex) {
        return JmpUtil.createFileFilter(exName, ex);
    }

}
