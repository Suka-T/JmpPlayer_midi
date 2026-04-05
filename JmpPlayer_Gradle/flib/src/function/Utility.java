package function;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * アプリケーション全般クラス
 *
 */
public class Utility {
    /////
    // ** 削除予定 **

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenSaveFileDialog(JFileChooser filechooser, Component parent, File defaultDirectory, String defaultFileName) {
        return openSaveFileDialog(filechooser, parent, defaultDirectory, defaultFileName);
    }

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenSaveFileDialog(Component parent, File defaultDirectory, String defaultFileName, String filterName, String... filterExtetions) {
        return openSaveFileDialog(parent, defaultDirectory, defaultFileName, filterName, filterExtetions);
    }

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenSaveFileDialog(Component parent, File defaultDirectory, String defaultFileName) {
        return openSaveFileDialog(parent, defaultDirectory, defaultFileName);
    }

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenLoadFileDialog(JFileChooser filechooser, Component parent, File defaultDirectory) {
        return openLoadFileDialog(filechooser, parent, defaultDirectory);
    }

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenLoadFileDialog(Component parent, File defaultDirectory, String filterName, String... filterExtetions) {
        return openLoadFileDialog(parent, defaultDirectory, filterName, filterExtetions);
    }

    /**
     * @deprecated 別のメソッドに置き換えられました(先頭文字を小文字に変更)
     */
    public static File OpenLoadFileDialog(Component parent, File defaultDirectory) {
        return openLoadFileDialog(parent, defaultDirectory);
    }
    ///////////////////////

    /**
     * 拡張子付きのファイル名の取得
     *
     * @param path
     *            パス
     * @return 拡張子付きファイル名
     */
    public static String getFileNameAndExtension(String path) {
        File file = new File(path);
        return getFileNameAndExtension(file);
    }

    /**
     * 拡張子付きのファイル名の取得
     *
     * @param file
     *            ファイル
     * @return 拡張子付きファイル名
     */
    public static String getFileNameAndExtension(File file) {
        String name = "";
        try {
            name = file.getName();
        }
        catch (Exception e) {
            name = "";
        }
        return name;
    }

    /**
     * 拡張子無しのファイル名取得
     *
     * @param path
     *            パス
     * @return 拡張子無しファイル名
     */
    public static String getFileNameNotExtension(String path) {
        File file = new File(path);
        return getFileNameNotExtension(file);
    }

    /**
     * 拡張子無しのファイル名取得
     *
     * @param file
     *            ファイル
     * @return 拡張子無しファイル名
     */
    public static String getFileNameNotExtension(File file) {
        String name = "";
        try {
            String[] s = file.getName().split("\\.");
            if (s.length > 0) {
                name = s[0];
            }
        }
        catch (Exception e) {
            name = "";
        }
        return name;
    }

    /**
     * 拡張子を取得
     *
     * @param path
     *            パス
     * @return 拡張子
     */
    public static String getExtension(String path) {
        File file = new File(path);
        return getExtension(file);
    }

    /**
     * 拡張子を取得
     *
     * @param file
     *            ファイル
     * @return 拡張子
     */
    public static String getExtension(File file) {
        String ex = "";
        try {
            String name = getFileNameAndExtension(file);
            
            int such = name.lastIndexOf(".");
            if (such != -1) {
                String s = name.substring(such);
                if (s.length() > 1) {
                    ex = s.substring(1);
                }
            }
        }
        catch (Exception e) {
            ex = "";
        }
        return ex;
    }

    public static boolean isExsistFile(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public static boolean isExsistFile(String path) {
        if (path == null) {
            return false;
        }

        File f = new File(path);
        return isExsistFile(f);
    }

    /**
     * 指定ファイルのパスノード名を配列に格納する
     *
     * @param file
     * @return
     */
    public static String[] getFileNodeNames(File file) {
        List<String> buf = new LinkedList<String>();

        while (file != null) {
            String name = file.getName();
            buf.add(name);
            file = file.getParentFile();
        }

        String[] ret = new String[buf.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = buf.get(buf.size() - i - 1);
        }
        return ret;
    }

    /**
     * 親ディレクトリパス取得
     *
     * @param path
     *            パス
     * @return 親ディレクトリパス
     */
    public static String getParentDir(String path) {
        String name = "";
        try {
            File file = new File(path);
            name = file.getParent();
        }
        catch (Exception e) {
            name = "";
        }
        return name + Platform.getSeparator();
    }

    /**
     * 拡張子チェック
     *
     * @param path
     *            ファイルパス
     * @param exStr
     *            一致するか判定する拡張子(ex: checkExtension("C:/text.txt", "txt"))
     * @return 結果(一致：true)
     */
    public static boolean checkExtension(String path, String exStr) {
        String ex = getExtension(path);
        return (ex != null) ? ex.equalsIgnoreCase(exStr) : false;
    }

    /**
     * 拡張子チェック
     *
     * @param f
     *            ファイル
     * @param exStr
     *            一致するか判定する拡張子
     * @return 結果(一致：true)
     */
    public static boolean checkExtension(File f, String exStr) {
        if (f.isFile() == false) {
            return false;
        }
        return checkExtension(f.getPath(), exStr);
    }

    /**
     * 拡張子チェック(複数指定可能)
     *
     * @param f
     *            ファイル
     * @param exStr
     *            一致するか判定する拡張子
     * @return 結果(一致：true)
     */
    public static boolean checkExtensions(File f, String... exStr) {
        boolean ret = false;
        for (String ex : exStr) {
            if (checkExtension(f, ex) == true) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * 拡張子チェック(複数指定可能)
     *
     * @param path
     *            ファイルパス
     * @param exStr
     *            一致するか判定する拡張子(ex: checkExtension("C:/text.txt", "txt", "csv",
     *            "ini"))
     * @return 結果(一致：true)
     */
    public static boolean checkExtensions(String path, String... exStr) {
        boolean ret = false;
        for (String ex : exStr) {
            if (checkExtension(path, ex) == true) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * クリップボードへ文字列セット
     */
    public static void setClipboard(String str) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        StringSelection ss = new StringSelection(str);
        clip.setContents(ss, ss);
    }

    /**
     * クリップボードのコンテンツを取得
     *
     * @return コンテンツ
     */
    public static Transferable getClipboardContents() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        Transferable obj = clip.getContents(null);
        return obj;
    }

    /**
     * クリップボードのテキストデータを取得
     *
     * @return テキストデータ
     */
    public static String getClipboardString() {
        Transferable obj = getClipboardContents();
        String str = "";

        try {
            str = (String) obj.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception e) {
            str = "";
        }
        return str;
    }

    /**
     * 現在の日時文字列を取得する。<br>
     * <b>ログ出力フォーマット("yyMMddHHmmss")
     */
    public static String getCurrentTimeStr() {
        return getCurrentTimeStr(GlobalConfig.LogStringFormat);
    }

    /**
     * 現在の日時文字列を取得する。
     *
     * @return 日時文字列
     */
    public static String getCurrentTimeStr(String format) {
        String strDate;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            strDate = sdf.format(cal.getTime());
        }
        catch (Exception e) {
            strDate = "XXXX";
        }
        return strDate;
    }

    /**
     * オブジェクトクラスのインスタンスをディープコピーする
     *
     * @param obj
     *            オブジェクト
     * @return コピーインスタンス
     */
    public static Object objClone(Object obj) {
        Object cpObj = null;
        ByteArrayOutputStream baOStream = null;
        ByteArrayInputStream baIStream = null;
        ObjectOutputStream oOStream = null;
        ObjectInputStream oIStream = null;

        try {
            // objの符号化
            baOStream = new ByteArrayOutputStream();
            oOStream = new ObjectOutputStream(baOStream);
            oOStream.writeObject(obj);

            // objをバイトデータ配列化
            byte[] buf = baOStream.toByteArray();

            // objを複合化
            baIStream = new ByteArrayInputStream(buf);
            oIStream = new ObjectInputStream(baIStream);
            cpObj = oIStream.readObject();

            // streamを閉じる
            baOStream.close();
            baIStream.close();
            oOStream.close();
            oIStream.close();
        }
        catch (Exception e) {
            // nullを返す
            cpObj = null;
        }
        finally {
            // streamインスタンスの破棄
            baOStream = null;
            baIStream = null;
            oOStream = null;
            oIStream = null;
        }
        return cpObj;
    }

    /**
     * スレッドスリープ(Exception回避)
     *
     * @param millis
     *            ms
     */
    public static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception e) {
        }
    }

    /**
     * HTMLカラーコードをAWTカラーインスタンスに変換
     *
     * @param code
     *            HTMLカラーコード
     * @return カラー
     */
    public static Color convertCodeToHtmlColor(String code) throws NumberFormatException {
        String value = "";

        // "#"の有無に依存しないようにする
        String[] offsetStr = code.split("#");
        if (offsetStr.length > 0) {
            value = offsetStr[offsetStr.length - 1];
        }

        if (value.length() != 6) {
            throw new NumberFormatException();
        }

        return Color.decode("#" + value.trim());
    }

    /**
     * AWTカラーインスタンスをHTMLカラーコードに変換(#を付加)
     *
     * @param htmlColor
     *            カラーインスタンス
     * @return HTMLカラーコード
     * @throws NumberFormatException
     */
    public static String convertHtmlColorToCode(Color htmlColor) throws NumberFormatException {
        return convertHtmlColorToCode(htmlColor, true);
    }

    /**
     * AWTカラーインスタンスをHTMLカラーコードに変換
     *
     * @param htmlColor
     *            カラーインスタンス
     * @param isAddingSharp
     *            ＃を付加するか
     * @return HTMLカラーコード
     * @throws NumberFormatException
     */
    public static String convertHtmlColorToCode(Color htmlColor, boolean isAddingSharp) throws NumberFormatException {
        String strR = Integer.toHexString(htmlColor.getRed());
        String strG = Integer.toHexString(htmlColor.getGreen());
        String strB = Integer.toHexString(htmlColor.getBlue());

        // 二桁表示
        strR = (strR.length() == 1) ? stringsCombin("0", strR) : strR;
        strG = (strG.length() == 1) ? stringsCombin("0", strG) : strG;
        strB = (strB.length() == 1) ? stringsCombin("0", strB) : strB;
        if (isAddingSharp == true) {
            // #を付加
            return stringsCombin("#", strR, strG, strB);
        }
        else {
            return stringsCombin(strR, strG, strB);
        }
    }

    /**
     * 文字列型をint変換（defaultは-1）
     *
     * @param val
     *            値
     * @return intValue（defaultは-1）
     */
    public static int tryParseInt(String val) {
        return tryParseInt(val, -1);
    }

    /**
     * 文字列型をint変換
     *
     * @param val
     *            値
     * @param defaultVal
     *            デフォルト
     * @return intValue
     */
    public static int tryParseInt(String val, int defaultVal) {
        int ret;
        try {
            ret = Integer.parseInt(val);
        }
        catch (Exception e) {
            ret = defaultVal;
        }
        return ret;
    }

    /**
     * 文字列型をfloat変換（defaultは-1）
     *
     * @param val
     *            値
     * @return floatValue（defaultは-1）
     */
    public static float tryParseFloat(String val) {
        return tryParseFloat(val, -1.0f);
    }

    /**
     * 文字列型をfloat変換
     *
     * @param val
     *            値
     * @param defaultVal
     *            デフォルト
     * @return floatValue
     */
    public static float tryParseFloat(String val, float defaultVal) {
        float ret;
        try {
            ret = Float.parseFloat(val);
        }
        catch (Exception e) {
            ret = defaultVal;
        }
        return ret;
    }

    /**
     * 文字列型をdouble変換（defaultは-1）
     *
     * @param val
     *            値
     * @return doubleValue（defaultは-1）
     */
    public static double tryParseDouble(String val) {
        return tryParseDouble(val, -1.0f);
    }

    /**
     * 文字列型をdouble変換
     *
     * @param val
     *            値
     * @param defaultVal
     *            デフォルト
     * @return doubleValue
     */
    public static double tryParseDouble(String val, double defaultVal) {
        double ret;
        try {
            ret = Double.parseDouble(val);
        }
        catch (Exception e) {
            ret = defaultVal;
        }
        return ret;
    }

    /**
     * 文字列型をlong変換（defaultは-1）
     *
     * @param val
     *            値
     * @return longValue（defaultは-1）
     */
    public static long tryParseLong(String val) {
        return tryParseLong(val, -1);
    }

    /**
     * 文字列型をlong変換
     *
     * @param val
     *            値
     * @param defaultVal
     *            デフォルト
     * @return longValue
     */
    public static long tryParseLong(String val, long defaultVal) {
        long ret;
        try {
            ret = Long.parseLong(val);
        }
        catch (Exception e) {
            ret = defaultVal;
        }
        return ret;
    }

    /**
     * 文字列型をboolean変換（defaultはfalse）
     *
     * @param val
     *            値
     * @return boolean
     */
    public static boolean tryParseBoolean(String val) {
        return tryParseBoolean(val, false);
    }

    /**
     * 文字列型をboolean変換
     *
     * @param val
     *            値
     * @param defaultVal
     *            デフォルト
     * @return boolean
     */
    public static boolean tryParseBoolean(String val, boolean defaultVal) {
        boolean ret;
        try {
            if (val.equalsIgnoreCase("t") || val.equalsIgnoreCase("true") || val.equalsIgnoreCase("1")) {
                ret = true;
            }
            else if (val.equalsIgnoreCase("f") || val.equalsIgnoreCase("false") || val.equalsIgnoreCase("0")) {
                ret = false;
            }
            else {
                ret = defaultVal;
            }
        }
        catch (Exception e) {
            ret = defaultVal;
        }
        return ret;
    }

    /**
     * 16進文字列をバイトデータに変換
     *
     * @param hex
     *            16進文字列
     * @return byte配列(例外時は空配列を返す)
     */
    public static byte[] tryParseHexBinary(String hex) {
        return tryParseHexBinary(hex, new byte[0]);
    }

    /**
     * 16進文字列をバイトデータに変換
     *
     * @param hex
     *            16進文字列
     * @param defaultVal
     *            デフォルト
     * @return byte配列(例外時は空配列を返す)
     */
    public static byte[] tryParseHexBinary(String hex, byte[] defaultVal) {
        byte[] data = null;
        try {
            data = parseHexBinary(hex);
        }
        catch (Exception e) {
            data = defaultVal;
        }
        return data;
    }

    private static byte[] parseHexBinary(String s) {
        final int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    /**
     * コンポーネントをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @return dst
     */
    public static JButton copyButtonComponent(JButton src) {
        return copyButtonComponent(src, false);
    }

    /**
     * コンポーネントをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param isTextCopy
     *            テキストコピーを実施するか
     * @return dst
     */
    public static JButton copyButtonComponent(JButton src, boolean isTextCopy) {
        // アクションリスナーコピー
        JButton dst = (JButton) copyComponent((JComponent) src, true);
        if (dst == null) {
            return null;
        }

        if (src.getActionListeners() != null) {
            for (ActionListener al : src.getActionListeners()) {
                if (al == null) {
                    continue;
                }
                dst.addActionListener(al);
            }
        }

        if (isTextCopy == true) {
            dst.setText(src.getText());
        }
        else {
            dst.setText("");
        }
        return dst;
    }

    /**
     * コンポーネントをコピー<br>
     * チェックボックスはボタンで置き換える<br>
     * <b>(コピー先のテキストは空白になる)
     *
     * @param src
     *            コピー元コンポーネント
     * @return dst
     */
    public static JButton copyComponentForCheckBoxToButton(JCheckBox src) {
        return copyComponentForCheckBoxToButton(src, "");
    }

    /**
     * コンポーネントをコピー<br>
     * チェックボックスはボタンで置き換える
     *
     * @param src
     *            コピー元コンポーネント
     * @param text
     *            テキストを指定
     * @return dst
     */
    public static JButton copyComponentForCheckBoxToButton(JCheckBox src, String text) {
        JButton dst = (JButton) copyComponent((JComponent) src, true);
        if (dst == null) {
            return null;
        }

        dst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ボタン押下でSelectedStateを変更
                src.setSelected(!src.isSelected());
            }
        });
        dst.setText(text);
        return dst;
    }

    /**
     * コンポーネントをコピー<br>
     * <b>(リスナーはコピーしない)
     *
     * @param src
     *            コピー元コンポーネント
     * @return dst
     */
    public static JComponent copyComponent(JComponent src) {
        return copyComponent(src, false);
    }

    /**
     * コンポーネントをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param isListenersCopy
     *            リスナーをコピーするか
     * @return dst
     */
    public static JComponent copyComponent(JComponent src, boolean isListenersCopy) {
        JComponent dst = (JComponent) Utility.objClone(src);
        if (dst == null) {
            return null;
        }

        if (isListenersCopy == true) {
            // リスナーのコピー
            copyListeners(src, dst);
        }
        return dst;
    }

    /**
     * コンポーネントのリスナーコンテンツをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param dst
     *            コピー先コンポーネント
     */
    public static void copyListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        copyComponentListeners(src, dst); // コンポーネントリスナーコピー
        copyMouseListeners(src, dst); // マウスリスナーコピー
        copyMouseMotionListeners(src, dst); // マウスモーションリスナー
        copyMouseWheelListeners(src, dst); // マウスホイールリスナー
        copyKeyListeners(src, dst); // キーリスナー
    }

    /**
     * コンポーネントのマウスリスナーをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param dst
     *            コピー先コンポーネント
     */
    public static void copyMouseListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        // マウスリスナーコピー
        if (src.getMouseListeners() != null) {
            for (MouseListener ml : src.getMouseListeners()) {
                if (ml == null) {
                    continue;
                }
                dst.addMouseListener(ml);
            }
        }
    }

    public static void copyComponentListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        // コンポーネントリスナーコピー
        if (src.getComponentListeners() != null) {
            for (ComponentListener cl : src.getComponentListeners()) {
                if (cl == null) {
                    continue;
                }
                dst.addComponentListener(cl);
            }
        }
    }

    /**
     * コンポーネントのマウスモーションリスナーをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param dst
     *            コピー先コンポーネント
     */
    public static void copyMouseMotionListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        // マウスモーションリスナー
        if (src.getMouseMotionListeners() != null) {
            for (MouseMotionListener mml : src.getMouseMotionListeners()) {
                if (mml == null) {
                    continue;
                }
                dst.addMouseMotionListener(mml);
            }
        }
    }

    /**
     * コンポーネントのマウスホイールリスナーをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param dst
     *            コピー先コンポーネント
     */
    public static void copyMouseWheelListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        // マウスホイールリスナー
        if (src.getMouseWheelListeners() != null) {
            for (MouseWheelListener mwl : src.getMouseWheelListeners()) {
                if (mwl == null) {
                    continue;
                }
                dst.addMouseWheelListener(mwl);
            }
        }
    }

    /**
     * コンポーネントのキーリスナーをコピー
     *
     * @param src
     *            コピー元コンポーネント
     * @param dst
     *            コピー先コンポーネント
     */
    public static void copyKeyListeners(JComponent src, JComponent dst) {
        if ((src == null) || (dst == null)) {
            return;
        }

        // キーリスナー
        if (src.getKeyListeners() != null) {
            for (KeyListener kl : src.getKeyListeners()) {
                if (kl == null) {
                    continue;
                }
                dst.addKeyListener(kl);
            }
        }
    }

    /**
     * 背景色から最適な前面色を取得する。
     *
     * @param bgColor
     *            背景色
     * @return 前面色
     */
    public static Color getForegroundColor(Color bgColor) {
        if (bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue() < ((255 * 3) / 2)) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
    }

    /**
     * 色の明るさを調整する
     *
     * @param color
     *            色
     * @param offset
     *            調整値
     * @return dstColor
     */
    public static Color convertHighLightColor(Color color, int offset) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();

        r = ((r + offset) > 255) ? 255 : ((r + offset) < 0) ? 0 : (r + offset);
        g = ((g + offset) > 255) ? 255 : ((g + offset) < 0) ? 0 : (g + offset);
        b = ((b + offset) > 255) ? 255 : ((b + offset) < 0) ? 0 : (b + offset);

        return new Color(r, g, b, a);
    }

    /**
     * 色のアルファ値を変更
     *
     * @param color
     *            色
     * @param alpha
     *            アルファ値
     * @return
     */
    public static Color convertColorAlpha(Color color, int alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = alpha;
        return new Color(r, g, b, a);
    }

    /**
     * 画像のマスク処理
     *
     * @param image
     *            画像
     * @param mask
     *            マスク値
     * @return
     */
    private static Image transformImageToMask(BufferedImage image, int srcRgb, int mask) {
        ImageFilter filter = new RGBImageFilter() {
            public final int filterRGB(int x, int y, int rgb) {
                // 透過処理
                Color sc = new Color(srcRgb);
                Color dc = new Color(rgb);
                if ((sc.getRed() == dc.getRed()) && (sc.getGreen() == dc.getGreen()) && (sc.getBlue() == dc.getBlue())) {
                    return (rgb << 8) & mask;
                }
                return rgb;
            }
        };

        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    /**
     * 画像の指定色を透過処理する
     *
     * @param image
     *            画像
     * @param color
     *            指定色
     * @return
     */
    public static Image transformImageToTransparency(BufferedImage image, Color color) {
        return transformImageToMask(image, color.getRGB(), 0x00000000);
    }

    /**
     * 画像の黒を透過処理する
     *
     * @param image
     *            画像
     * @return
     */
    public static Image transformImageToTransparencyForBlack(BufferedImage image) {
        return transformImageToTransparency(image, Color.BLACK);
    }

    /**
     * ImageからBufferdImageを作成する
     *
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage imageToBufferedImage(Image img, int width, int height) {
        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ret.createGraphics();

        // BufferdImageに書き出す
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return ret;
    }

    /**
     * 文字列内にネストが成立している箇所が存在するか
     *
     * @param str
     *            文字列
     * @param nestWord
     *            ネスト文字列
     * @return リザルト
     */
    public static boolean existsNestString(String str, String nestWord) {
        return existsNestString(str, nestWord, nestWord);
    }

    /**
     * 文字列内にネストが成立している箇所が存在するか
     *
     * @param str
     *            文字列
     * @param pre
     *            開始ネスト
     * @param suf
     *            終了ネスト
     * @return リザルト
     */
    public static boolean existsNestString(String str, String pre, String suf) {
        try {
            int begin = str.indexOf(pre) + pre.length();
            int last = str.indexOf(suf);
            if (begin == -1 || last == -1) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * ネスト内の文字列取得
     *
     * @param str
     *            文字列
     * @param nestWord
     *            ネスト文字列
     * @return ネスト内の文字列
     */
    public static String getNestString(String str, String nestWord) {
        return getNestString(str, nestWord, nestWord);
    }

    /**
     * ネスト内の文字列取得
     *
     * @param str
     *            文字列
     * @param pre
     *            開始ネスト
     * @param suf
     *            終了ネスト
     * @return ネスト内の文字列
     */
    public static String getNestString(String str, String pre, String suf) {
        // ※少し冗長だけどタグの存在を確認
        if (existsNestString(str, pre, suf) == false) {
            return "";
        }

        String ret = "";
        try {
            int begin = str.indexOf(pre) + pre.length();
            int last = str.indexOf(suf);
            ret = str.substring(begin, last);
        }
        catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    /**
     * ビット抽出
     *
     * @param data
     *            対象データ(1Byte)
     * @param bit
     *            下位ビット数指定(1Byte)
     * @return
     */
    public static int extractBit(int data, int bit) {
        int mask = (8 - bit) >> 0xff;
        return (data & mask);
    }

    /**
     * ビットマージメソッド<br>
     * LSBとMSBの下位指定ビット数を結合
     *
     * @param lsb
     *            Least Significant Bit(1Byte)
     * @param msb
     *            Most Significant Bit(1Byte)
     * @param bit
     *            下位ビット数指定(1Byte)
     * @return マージ後データ(2Byte)
     */
    public static int mergeBit(int lsb, int msb, int bit) {
        int ret = 0x00;
        // 先にMSBをシフトする
        ret = msb << bit;

        // 下位bitにLSBを結合
        return (ret | extractBit(lsb, bit));
    }

    /**
     * ZIPファイルを作成
     *
     * @param dstFilePath
     *            出力先
     * @param targetFiles
     *            含むファイル
     * @return 結果
     * @throws IOException
     */
    public static boolean createZipFile(String dstFilePath, File... targetFiles) throws IOException {

        boolean ret = true;
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dstFilePath));
        for (int i = 0; i < targetFiles.length; i++) {
            File file = targetFiles[i];
            int deleteLength = file.getPath().length() - file.getName().length();
            createZip(out, file, deleteLength);
        }
        out.close();
        return ret;
    }

    private static void createZip(ZipOutputStream out, File targetFile, int deleteLength) throws IOException {
        if (targetFile.isDirectory()) {
            File[] files = targetFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                createZip(out, files[i], deleteLength);
            }
        }
        else {
            ZipEntry target = new ZipEntry(getEntryPath(targetFile, deleteLength));
            out.putNextEntry(target);
            byte buf[] = new byte[1024];
            int count;

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(targetFile));
            while ((count = in.read(buf, 0, 1024)) != -1) {
                out.write(buf, 0, count);
            }
            in.close();
            out.closeEntry();
            in = null;
            out = null;
        }
    }

    /**
     * ZIPエントリパスを返す。
     *
     * @param file
     *            ZIPエントリ対象ファイル
     * @return ZIPエントリのパス
     */
    private static String getEntryPath(File file, int deleteLength) {
        return file.getPath().replaceAll("\\\\", "/").substring(deleteLength);
    }

    /**
     * ファイル名をURLエンコードする
     *
     * @param fileName
     * @return エンコードされたファイル名
     */
    public String encodeFileName(String fileName) {

        try {
            // ファイル名を"Shift_JIS"に変更
            fileName = new String(fileName.getBytes("Shift_JIS"), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
        }
        return fileName;
    }

    /**
     * ZIPファイル内のコンテンツを取得する。<br>
     * ※解凍は行わないため、zip内のコンテンツをパス指定できない。<br>
     * パス指定する場合は、unZipを行うこと。
     *
     * @param filename
     *            ファイルパス
     * @return ファイルリスト
     * @throws IOException
     */
    public static ArrayList<File> getZipContents(String filename) throws IOException {
        ArrayList<File> ret = new ArrayList<>();
        ZipFile zipFile = new ZipFile(filename);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enumeration.nextElement();
            if (entry.isDirectory()) {
                ret.add(new File(entry.getName()));
            }
            else {
                File parent = new File(entry.getName()).getParentFile();
                if (parent != null) {
                    ret.add(parent);
                }
            }
        }
        zipFile.close();
        return ret;
    }

    /**
     * 指定ディレクトリをZIP圧縮する
     *
     * @param srcDirPath
     *            ディレクトリパス
     * @param zipPath
     *            ZIPファイルパス
     * @return
     * @throws IOException
     */
    public static boolean toZip(String srcDirPath, String zipPath) throws IOException {
        File dir = new File(srcDirPath);
        if (dir.isDirectory() == false) {
            return false;
        }

        return createZipFile(zipPath, dir.listFiles());
    }

    /**
     * ZIPファイルを解凍する。
     *
     * @param zipPath
     *            ZIPファイルパス
     * @param dstPath
     *            解凍ファイルパス
     * @throws IOException
     */
    public static void unZip(String zipPath, String dstPath) throws IOException {
        File dstFile = new File(dstPath);
        dstFile.mkdirs();

        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();

            String outputPath = Utility.pathCombin(dstFile.getPath(), entry.getName());
            if (entry.isDirectory()) {
                File newDir = new File(outputPath);
                newDir.mkdirs();
            }
            else {
                File parent = new File(entry.getName()).getParentFile();
                if (parent != null) {
                    File dstParent = new File(Utility.pathCombin(dstFile.getPath(), parent.getPath()));
                    dstParent.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(outputPath);
                InputStream ins = zipFile.getInputStream(entry);
                byte[] buf = new byte[1024];
                int size = 0;
                while ((size = ins.read(buf)) != -1) {
                    fos.write(buf, 0, size);
                }
                fos.close();
            }
        }
        zipFile.close();
    }

    /**
     * File及びDirectoryを削除する。
     *
     * @param file
     *            Fileオブジェクト
     * @return 結果
     */
    public static boolean deleteFileDirectory(File file) {
        boolean ret = false;
        if (file.exists() == false) {
            return false;
        }

        if (file.isDirectory() == true) {
            for (File child : file.listFiles()) {
                ret = deleteFileDirectory(child);
                if (ret == false) {
                    return false;
                }
            }
        }

        ret = file.delete();
        return ret;
    }

    /**
     * File及びDirectoryを削除する。
     *
     * @param path
     *            パス
     * @return 結果
     */
    public static boolean deleteFileDirectory(String path) {
        return deleteFileDirectory(new File(path));
    }

    /**
     * 文字列連結<br>
     * GlobalConfigの設定に従う
     *
     * @param strings
     *            結合する文字列
     * @return 連結後文字列
     */
    public static String stringsCombin(Object... strings) {
        return stringsCombin(GlobalConfig.IsDefaultUsingStringBuilder, strings);
    }

    /**
     * 文字列連結
     *
     * @param isUseBuilder
     *            StringBuilderを使用するか
     * @param strings
     *            結合する文字列
     * @return 連結後文字列
     */
    public static String stringsCombin(boolean isUseBuilder, Object... strings) {
        if (isUseBuilder == true) {
            return stringsCombinForBuilder(strings);
        }
        else {
            return stringsCombinForBuffer(strings);
        }
    }

    /**
     * 文字列連結<br>
     * 単語ごとに改行する
     *
     * @param strings
     *            結合する文字列
     * @return 連結後文字列
     */
    public static String stringsCombinAddingNewLine(Object... strings) {
        String ret = "";
        for (Object obj : strings) {
            String str = obj.toString();
            if (ret.equals("") == false) {
                ret = stringsCombin(ret, Platform.getNewLine(), str);
            }
            else {
                ret = stringsCombin(ret, str);
            }
        }
        return ret;
    }

    /**
     * 文字列連結<br>
     * StringBuilderを使用
     *
     * @param strings
     *            結合する文字列
     * @return 連結後文字列
     */
    public static String stringsCombinForBuilder(Object... strings) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : strings) {
            sb.append(obj.toString());
        }
        return sb.toString();
    }

    /**
     * 文字列連結<br>
     * StringBufferを使用
     *
     * @param strings
     *            結合する文字列
     * @return 連結後文字列
     */
    public static String stringsCombinForBuffer(Object... strings) {
        StringBuffer sb = new StringBuffer();
        for (Object obj : strings) {
            sb.append(obj.toString());
        }
        return sb.toString();
    }

    /**
     * パス文字列を作成
     *
     * @param strings
     *            エントリ名文字列
     * @return パス文字列
     */
    public static String pathCombin(String... strings) {
        String ret = "";
        for (String str : strings) {
            if (ret.equals("") == false) {
                if (ret.endsWith(Platform.getSeparator()) == true) {
                    ret = stringsCombin(ret, str);
                }
                else {
                    ret = stringsCombin(ret, Platform.getSeparator(), str);
                }
            }
            else {
                ret = stringsCombin(ret, str);
            }
        }
        return ret;
    }

    /**
     * ファイル移動
     *
     * @param src
     *            対象ファイルパス
     * @param dst
     *            移動先ファイルパス
     * @return 結果
     */
    public static boolean moveFile(String src, String dst) {
        return moveFile(new File(src), new File(dst));
    }

    /**
     * ファイル移動
     *
     * @param src
     *            対象ファイル
     * @param dst
     *            移動先ファイル
     * @return 結果
     */
    public static boolean moveFile(File src, File dst) {
        // renameToを使ってファイル移動
        return src.renameTo(dst);
    }

    /**
     * ファイル名変更
     *
     * @param src
     *            対象ファイル
     * @param dstName
     *            変更後のファイル名
     * @return 結果
     */
    public static boolean renameFile(File src, String dstName) {
        String parent = src.getParent();

        String dst = pathCombin(parent, dstName);
        return src.renameTo(new File(dst));
    }

    /**
     * ファイル名変更
     *
     * @param src
     *            対象ファイル
     * @param dstName
     *            変更後のファイル名
     * @return 結果
     */
    public static boolean renameFile(String src, String dstName) {
        File srcFile = new File(src);
        return renameFile(srcFile, dstName);
    }

    /**
     * ファイルコピー
     *
     * @param src
     *            コピー元ファイルパス
     * @param dst
     *            コピー先ファイルパス
     * @return 結果
     */
    public static boolean copyFile(String src, String dst) {
        return copyFile(new File(src), new File(dst));
    }

    /**
     * ファイルコピー
     *
     * @param src
     *            コピー元ファイル
     * @param dst
     *            コピー先ファイル
     * @return 結果
     */
    public static boolean copyFile(File src, File dst) {
        boolean ret = true;
        if (src.isDirectory() == true) {
            // フォルダコピー
            dst.mkdirs();
            for (File f : src.listFiles()) {
                String dstFilePath = Utility.stringsCombin(dst.getPath(), Platform.getSeparator(), f.getName());
                copyFile(f.getPath(), dstFilePath);
            }
        }

        FileChannel srcChannel, dstChannel;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dst);

            srcChannel = fis.getChannel();
            dstChannel = fos.getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);

            fis.close();
            fos.close();
            srcChannel.close();
            dstChannel.close();
        }
        catch (FileNotFoundException e) {
            ret = false;
        }
        catch (IOException e) {
            ret = false;
        }
        finally {
            fis = null;
            fos = null;
            srcChannel = null;
            dstChannel = null;
        }
        return ret;
    }

    /**
     * ディレクトリ階層にある指定拡張子を取得する。
     *
     * @param ex
     *            拡張子
     * @param src
     *            ディレクトリ
     * @param dst
     *            出力先
     */
    public static void toExport(String ex, File src, File dst) {
        if (dst.isDirectory() == false && dst.exists() == true) {
            // dstのチェック
            return;
        }

        if (dst.exists() == false) {
            dst.mkdir();
        }

        if (src.isDirectory() == false) {
            if (checkExtension(src, ex) == true) {
                // System.out.println(">>>cp " + src.getAbsolutePath());
                String srcPath = src.getPath();
                String dstPath = Utility.pathCombin(dst.getPath(), Utility.getFileNameAndExtension(srcPath));
                if (copyFile(srcPath, dstPath) == false) {
                    // System.out.println("false");
                }
            }
            return;
        }

        File[] lists = src.listFiles();
        for (File f : lists) {
            // System.out.println("lst " + f.getPath());
            toExport(ex, f, dst);
        }
    }

    /**
     * ディレクトリ階層にある指定拡張子を取得する。
     *
     * @param ex
     *            拡張子
     * @param src
     *            ディレクトリ
     * @param dst
     *            出力先
     */
    public static void toExport(String ex, String src, String dst) {
        File srcFile = new File(src);
        File dstFile = new File(dst);
        toExport(ex, srcFile, dstFile);
    }

    /**
     * テキストファイルのコンテンツを取得する。
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> getTextFileContents(String path) throws IOException {
        return getTextFileContents(path, GlobalConfig.DefaultCharset);
    }

    /**
     * テキストファイルのコンテンツを取得する。
     *
     * @param path
     * @param charset
     * @return
     * @throws IOException
     */
    public static List<String> getTextFileContents(String path, String charset) throws IOException {
        File file = new File(path);
        FileInputStream fs = new FileInputStream(file);
        return getTextFileContents(fs, charset);
    }

    /**
     * テキストファイルのコンテンツを取得する。
     *
     * @param stream
     * @param charset
     * @return
     * @throws IOException
     */
    public static List<String> getTextFileContents(InputStream stream, String charset) throws IOException {
        LinkedList<String> ret = new LinkedList<String>();
        BufferedReader reader;

        try {
            InputStreamReader isr = new InputStreamReader(stream, charset);
            reader = new BufferedReader(isr);

            // ファイルを読み込む
            String line = "";
            while ((line = reader.readLine()) != null) {
                ret.add(line);
            }
            reader.close();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        finally {
            reader = null;
        }
        return ret;
    }

    /**
     * テキストファイルのコンテンツを取得する。
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public static List<String> getTextFileContents(InputStream stream) throws IOException {
        return getTextFileContents(stream, GlobalConfig.DefaultCharset);
    }

    /**
     * テキストファイルのコンテンツを取得する。
     *
     * @param stream
     * @param charset
     * @return
     * @throws IOException
     */
    public static List<String> getTextFileContents(InputStreamReader streamReader) throws IOException {
        LinkedList<String> ret = new LinkedList<String>();
        BufferedReader reader;

        try {
            InputStreamReader isr = streamReader;
            reader = new BufferedReader(isr);

            // ファイルを読み込む
            String line = "";
            while ((line = reader.readLine()) != null) {
                ret.add(line);
            }
            reader.close();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        finally {
            reader = null;
        }
        return ret;
    }

    /**
     * テキストファイル出力
     *
     * @param path
     * @param text
     * @param charset
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void outputTextFile(String path, List<String> text, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw;
        try {
            pw = new PrintWriter(path, charset);
            for (String line : text) {
                pw.println(line);
            }
            pw.close();
        }
        catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (UnsupportedEncodingException uee) {
            throw uee;
        }
        finally {
            pw = null;
        }
    }

    /**
     * テキストファイル出力
     *
     * @param path
     * @param text
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void outputTextFile(String path, List<String> text) throws FileNotFoundException, UnsupportedEncodingException {
        outputTextFile(path, text, "UTF-8");
    }

    /**
     * テキストファイル出力
     *
     * @param path
     * @param text
     * @param charset
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void outputTextFile(String path, String text, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw;
        try {
            pw = new PrintWriter(path, charset);
            pw.println(text);
            pw.close();
        }
        catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (UnsupportedEncodingException uee) {
            throw uee;
        }
        finally {
            pw = null;
        }
    }

    /**
     * テキストファイル出力
     *
     * @param path
     * @param text
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void outputTextFile(String path, String text) throws FileNotFoundException, UnsupportedEncodingException {
        outputTextFile(path, text, "UTF-8");
    }

    /**
     * バイナリファイルのコンテンツを取得する
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBinaryContents(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * バイナリファイルのコンテンツを取得する
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] getBinaryContents(String path) throws IOException {
        File file = new File(path);
        return getBinaryContents(file);
    }

    public static File changeFileExtension(File file, String extension) {
        String fileName = getFileNameNotExtension(file);
        String dExtension = "." + extension;
        fileName = file.getParent() + Platform.getSeparator() + fileName + dExtension;
        return new File(fileName);
    }

    /**
     * ファイル保存ダイアログを開く
     *
     * @param filechooser
     * @param parent
     * @param defaultDirectory
     * @param defaultFileName
     * @return
     */
    public static File openSaveFileDialog(JFileChooser filechooser, Component parent, File defaultDirectory, String defaultFileName) {
        File ret = null;
        String name, dir;
        if (defaultDirectory == null || defaultDirectory.exists() == false) {
            dir = ".";
            name = defaultFileName;
        }
        else {
            if (defaultDirectory.isDirectory() == true) {
                dir = defaultDirectory.getParent();
            }
            else {
                dir = defaultDirectory.getPath();
            }
            name = defaultFileName;
        }

        // filechooser.addChoosableFileFilter(filter);
        File f = new File(new File(dir, name).getAbsolutePath());
        filechooser.setSelectedFile(f);

        int selected = filechooser.showSaveDialog(parent);
        if (selected == JFileChooser.APPROVE_OPTION) {
            ret = filechooser.getSelectedFile();
        }
        return ret;
    }

    public static File openSaveFileDialog(Component parent, File defaultDirectory, String defaultFileName, FileNameExtensionFilter... filter) {
        JFileChooser filechooser = new JFileChooser();
        for (int i = 0; i < filter.length; i++) {
            filechooser.addChoosableFileFilter(filter[i]);
        }
        File f = openSaveFileDialog(filechooser, parent, defaultDirectory, defaultFileName);
        if (f != null) {
            javax.swing.filechooser.FileFilter fil = filechooser.getFileFilter();
            if (fil instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fil;
                if (selectedFilter.accept(f) == false) {
                    String parentPath = f.getParent();
                    String fileName = getFileNameNotExtension(f);
                    fileName = stringsCombin(fileName, ".", selectedFilter.getExtensions()[0]);
                    f = new File(pathCombin(parentPath, fileName));
                    System.out.println(f.getPath());
                }
            }
        }
        return f;
    }

    /**
     * ファイル保存ダイアログを開く
     *
     * @param parent
     * @param defaultDirectory
     * @param defaultFileName
     * @param filterName
     * @param filterExtetions
     * @return
     */
    public static File openSaveFileDialog(Component parent, File defaultDirectory, String defaultFileName, String filterName, String... filterExtetions) {
        JFileChooser filechooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterName, filterExtetions);
        filechooser.addChoosableFileFilter(filter);
        return openSaveFileDialog(filechooser, parent, defaultDirectory, defaultFileName);
    }

    /**
     * ファイル保存ダイアログを開く
     *
     * @param parent
     * @param defaultDirectory
     * @param defaultFileName
     * @return
     */
    public static File openSaveFileDialog(Component parent, File defaultDirectory, String defaultFileName) {
        JFileChooser filechooser = new JFileChooser();
        return openSaveFileDialog(filechooser, parent, defaultDirectory, defaultFileName);
    }

    /**
     * ファイル読み込みダイアログを開く
     *
     * @param filechooser
     * @param parent
     * @param defaultDirectory
     * @return
     */
    public static File openLoadFileDialog(JFileChooser filechooser, Component parent, File defaultDirectory) {
        File ret = null;

        File dir = defaultDirectory;
        if (dir != null) {
            if (dir.isDirectory() == false) {
                dir = dir.getParentFile();
            }
            filechooser.setCurrentDirectory(dir);
        }

        // 確認ダイアログ
        int selected = filechooser.showOpenDialog(parent);
        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                ret = filechooser.getSelectedFile();
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * ファイル読み込みダイアログを開く
     *
     * @param parent
     * @param defaultDirectory
     * @param filterName
     * @param filterExtetions
     * @return
     */
    public static File openLoadFileDialog(Component parent, File defaultDirectory, String filterName, String... filterExtetions) {
        JFileChooser filechooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(filterName, filterExtetions);
        filechooser.setFileFilter(filter);
        return openLoadFileDialog(filechooser, parent, defaultDirectory);
    }

    public static File openLoadFileDialog(Component parent, File defaultDirectory, FileNameExtensionFilter... filter) {
        JFileChooser filechooser = new JFileChooser();
        for (int i = 0; i < filter.length; i++) {
            filechooser.addChoosableFileFilter(filter[i]);
        }
        return openLoadFileDialog(filechooser, parent, defaultDirectory);
    }

    /**
     * ファイル読み込みダイアログを開く
     *
     * @param parent
     * @param defaultDirectory
     * @return
     */
    public static File openLoadFileDialog(Component parent, File defaultDirectory) {
        JFileChooser filechooser = new JFileChooser();
        return openLoadFileDialog(filechooser, parent, defaultDirectory);
    }

    /** YESボタンが押された */
    public static final int CONFIRM_RESULT_YES = JOptionPane.YES_OPTION;
    /** NOボタンが押された */
    public static final int CONFIRM_RESULT_NO = JOptionPane.NO_OPTION;
    /** CANCELボタンが押された */
    public static final int CONFIRM_RESULT_CANCEL = JOptionPane.CANCEL_OPTION;
    /** OKボタンが押された */
    public static final int CONFIRM_RESULT_OK = JOptionPane.OK_OPTION;

    /**
     * アイコンがない確認ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static int openPlainDialog(Component parent, String title, String message) {
        return openPlainDialog(parent, title, message, false);
    }

    /**
     * アイコンがない確認ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @param showCancel
     * @return
     */
    public static int openPlainDialog(Component parent, String title, String message, boolean showCancel) {
        return openConfirmDialog(parent, title, message, showCancel, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * エラーダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static int openErrorDialog(Component parent, String title, String message) {
        return openErrorDialog(parent, title, message, false);
    }

    /**
     * エラーダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @param showCancel
     * @return
     */
    public static int openErrorDialog(Component parent, String title, String message, boolean showCancel) {
        return openConfirmDialog(parent, title, message, showCancel, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 質問ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static int openQuestionDialog(Component parent, String title, String message) {
        return openQuestionDialog(parent, title, message, false);
    }

    /**
     * 質問ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @param showCancel
     * @return
     */
    public static int openQuestionDialog(Component parent, String title, String message, boolean showCancel) {
        return openConfirmDialog(parent, title, message, showCancel, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * 警告ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static int openWarningDialog(Component parent, String title, String message) {
        return openWarningDialog(parent, title, message, false);
    }

    /**
     * 警告ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @param showCancel
     * @return
     */
    public static int openWarningDialog(Component parent, String title, String message, boolean showCancel) {
        return openConfirmDialog(parent, title, message, showCancel, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 情報ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static int openInfomationDialog(Component parent, String title, String message) {
        return openInfomationDialog(parent, title, message, false);
    }

    /**
     * 情報ダイアログを開く
     *
     * @param parent
     * @param title
     * @param message
     * @param showCancel
     * @return
     */
    public static int openInfomationDialog(Component parent, String title, String message, boolean showCancel) {
        return openConfirmDialog(parent, title, message, showCancel, JOptionPane.INFORMATION_MESSAGE);
    }

    private static int openConfirmDialog(Component parent, String title, String message, boolean showCancel, int messageType) {
        int optionType = JOptionPane.YES_NO_OPTION;
        if (showCancel == true) {
            optionType = JOptionPane.YES_NO_CANCEL_OPTION;
        }
        int option = JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
        return option;
    }

    /**
     * エクスプローラを開く
     *
     * @param path
     * @throws IOException
     */
    public static void openExproler(String path) throws IOException {
        openExproler(new File(path));
    }

    /**
     * エクスプローラを開く
     *
     * @param f
     * @throws IOException
     */
    public static void openExproler(File f) throws IOException {
        if (f.exists() == true) {
            File dir;
            if (f.isDirectory() == false) {
                dir = f.getParentFile();
            }
            else {
                dir = f;
            }

            if (Desktop.isDesktopSupported() == true) {
                Desktop.getDesktop().open(dir);
            }
        }
    }

    /**
     * プロセス開始
     *
     * @param cmd
     *            コマンド
     * @throws IOException
     */
    public static Process invokeProcess(String... cmd) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(cmd);
        return pb.start();
    }

    /**
     * 文字列内に検索ワードが含まれるか
     *
     * @param src
     *            検索対象
     * @param word
     *            検索ワード
     * @return 結果
     */
    public static boolean containsStr(String src, String word) {
        if (src.indexOf(word) != -1) {
            return true;
        }
        return false;
    }

    /**
     * 文字列内に検索ワードが何個含まれるか取得する
     *
     * @param src
     *            検索対象
     * @param word
     *            検索ワード
     * @return 個数
     */
    public static int getContainsStrCount(String src, String word) {
        int result = Integer.MAX_VALUE;
        if (containsStr(src, word) == false) {
            return 0;
        }

        char[] chars = word.toCharArray();
        String[] strs = new String[chars.length];
        for (int i = 0; i < strs.length; i++) {
            char[] tmp = { chars[i] };
            strs[i] = new String(tmp);
        }

        for (String s : strs) {
            int count = src.length() - src.replace(s, "").length();
            result = Math.min(result, count);
        }
        return result;
    }
}
