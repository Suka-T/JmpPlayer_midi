package jmp.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Platform.SystemProperty;
import function.Utility;
import jmp.core.ManagerInstances;
import jmp.lang.VBA;

public class JmpUtil {
    private JmpUtil() {
    }

    public static String getDesktopPathOrCurrent() {
        String current = Platform.getCurrentPath();
        return getDesktopPath(current);
    }

    public static String getDesktopPath(String defaultPath) {
        String desktop = Platform.getProperty(SystemProperty.USER_HOME);
        if (JmpUtil.isExsistFile(desktop) == false) {
            return defaultPath;
        }

        String newDesktop = JmpUtil.pathCombin(desktop, "Desktop");
        if (JmpUtil.isExsistFile(newDesktop) == false) {
            return desktop;
        }
        return newDesktop;
    }

    public static String genExtensions2Str(String... ex) {
        String ret = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += ex[i];
        }
        return ret;
    }

    public static String[] genStr2Extensions(String str) {
        String[] ret = str.split(",");
        return ret;
    }

    public static FileNameExtensionFilter createFileFilter(String exName, String... ex) {
        String exs = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                exs += ", ";
            }
            exs += String.format("*.%s", ex[i]);
        }

        String description = String.format("%s (%s)", exName, exs);
        return new FileNameExtensionFilter(description, ex);
    }

    public static boolean checkConfigKey(String key, String dKey) {
        if (key.equals(dKey) == true || key.equals(ManagerInstances.CFG_KEY_INITIALIZE) == true) {
            return true;
        }
        return false;
    }

    public static ImageIcon convertImageIcon(Image img) {
        return img == null ? null : new ImageIcon(img);
    }

    public static boolean toBoolean(String str) {
        return toBoolean(str, false);
    }

    public static boolean toBoolean(String str, boolean def) {
        String tStr = str.trim();
        if (tStr.equalsIgnoreCase("on") == true) {
            return true;
        }
        else if (tStr.equalsIgnoreCase("off") == true) {
            return false;
        }
        else {
            return Utility.tryParseBoolean(str, def);
        }
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int def) {
        return Utility.tryParseInt(str, def);
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long def) {
        return Utility.tryParseLong(str, def);
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float def) {
        return Utility.tryParseFloat(str, def);
    }

    public static Double toDouble(String str) {
        return toDouble(str, 0.0);
    }

    public static Double toDouble(String str, double def) {
        return Utility.tryParseDouble(str, def);
    }

    public static List<String> readTextFile(File file) throws IOException {
        return readTextFile(file.getPath());
    }

    public static List<String> readTextFile(String path) throws IOException {
        List<String> textContents = Utility.getTextFileContents(path);
        return textContents;
    }

    public static void writeTextFile(String path, String textContents) throws FileNotFoundException, UnsupportedEncodingException {
        Utility.outputTextFile(path, textContents);
    }

    public static void writeTextFile(File file, List<String> textContents) throws FileNotFoundException, UnsupportedEncodingException {
        writeTextFile(file.getPath(), textContents);
    }

    public static void writeTextFile(String path, List<String> textContents) throws FileNotFoundException, UnsupportedEncodingException {
        Utility.outputTextFile(path, textContents);
    }

    public static boolean isExsistFile(String path) {
        return Utility.isExsistFile(path);
    }

    public static boolean isExsistFile(File file) {
        return Utility.isExsistFile(file);
    }

    public static String pathCombin(String... paths) {
        return Utility.pathCombin(paths);
    }

    public static boolean checkExtension(File f, String ex) {
        return Utility.checkExtension(f, ex);
    }

    public static boolean checkExtension(String path, String ex) {
        return Utility.checkExtension(path, ex);
    }

    public static void threadSleep(long time) {
        Utility.threadSleep(time);
    }

    public static String getFileNameNotExtension(String path) {
        return Utility.getFileNameNotExtension(path);
    }

    public static String getFileNameNotExtension(File f) {
        return Utility.getFileNameNotExtension(f);
    }

    public static String getFileNameAndExtension(File f) {
        return Utility.getFileNameAndExtension(f);
    }

    public static String getFileNameAndExtension(String path) {
        return Utility.getFileNameAndExtension(path);
    }

    public static boolean deleteFileDirectory(File f) {
        return Utility.deleteFileDirectory(f);
    }

    public static String getExtension(File f) {
        return Utility.getExtension(f);
    }

    public static String getExtension(String path) {
        return Utility.getExtension(path);
    }

    public static Image transformImageToTransparency(BufferedImage image, Color color) {
        return Utility.transformImageToTransparency(image, color);
    }

    public static String convertCharset(String src, String charset) throws CharacterCodingException {
        Charset ccs = Charset.forName(VBA.SRC_CHARSET);
        CharsetEncoder encoder = ccs.newEncoder();
        ByteBuffer bytebuf = encoder.encode(CharBuffer.wrap(src));
        byte[] bytes = new byte[bytebuf.limit()];
        bytebuf.get(bytes);

        Charset dcs = Charset.forName(charset);
        CharsetDecoder coder = dcs.newDecoder();
        CharBuffer charbuf = coder.decode(ByteBuffer.wrap(bytes));
        return charbuf.toString();
    }
}
