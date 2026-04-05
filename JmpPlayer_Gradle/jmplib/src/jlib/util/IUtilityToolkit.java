package jlib.util;

import java.awt.Color;

import javax.swing.filechooser.FileFilter;

public interface IUtilityToolkit {

    /**
     * 改行文字を取得
     *
     * @return
     */
    abstract String getNewLine();

    /**
     * HTMLカラーコードをAWTカラーインスタンスに変換する
     *
     * @param code
     * @return
     * @throws NumberFormatException
     */
    abstract Color convertCodeToHtmlColor(String code) throws NumberFormatException;

    /**
     * AWTカラーインスタンスをHTMLカラーコードに変換する
     *
     * @param htmlColor
     * @return
     * @throws NumberFormatException
     */
    abstract String convertHtmlColorToCode(Color htmlColor) throws NumberFormatException;

    /**
     * 拡張子をカンマ区切りの文字列形式に変換する
     *
     *
     * @param ex
     * @return
     */
    abstract String extensions2Str(String... ex);

    /**
     * カンマ区切りの文字列形式を拡張子に変換する
     *
     * @param str
     * @return
     */
    abstract String[] str2Extensions(String str);

    /**
     * ファイルフィルター生成
     *
     * @param exName
     * @param ex
     * @return
     */
    abstract FileFilter createFileFilter(String exName, String... ex);
}
