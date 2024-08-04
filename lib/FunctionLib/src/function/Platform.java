package function;

import java.io.File;
import java.util.HashMap;

/**
 *
 * システム全般クラス<br>
 * <b>プラットフォーム依存な処理のラッパーを記述
 *
 */
public class Platform {
    /** プラットフォーム識別 */
    public static enum KindOfPlatform {
        LINUX, MAC, WINDOWS, SUN_OS, OTHER,
    }

    /** システムプロパティ */
    public static enum SystemProperty {
        VERSION,
        VENDOR,
        VENDOR_URL,
        HOME,
        VM_SPEC_VERSION,
        VM_SPEC_VENDOR,
        VM_SPEC_NAME,
        VM_VERSION,
        VM_VENDOR,
        VM_NAME,
        SPEC_VERSION,
        SPEC_VENDOR,
        SPEC_NAME,
        CLASS_VERSION,
        CLASS_PATH,
        LIBRARY_PATH,
        IO_TMPDIR,
        COMPILER,
        EXT_DIRS,
        OS_NAME,
        OS_ARCH,
        OS_VERSION,
        FILE_SEPARATOR,
        PATH_SEPARATOR,
        LINE_SEPARATOR,
        USER_NAME,
        USER_HOME,
        USER_DIR,
        FILE_ENCODING,
    }

    private static HashMap<SystemProperty, String> propertyToStr = new HashMap<SystemProperty, String>() {
        {
            put(SystemProperty.VERSION, "java.version"); // Java Runtime
                                                         // Environmentのバージョン
            put(SystemProperty.VENDOR, "java.vendor"); // Java Runtime
                                                       // Environmentのベンダー
            put(SystemProperty.VENDOR_URL, "java.vendor.url"); // JavaベンダーのURL
            put(SystemProperty.HOME, "java.home"); // Javaのインストール先ディレクトリ
            put(SystemProperty.VM_SPEC_VERSION, "java.vm.specification.version"); // Java仮想マシンの仕様バージョン
            put(SystemProperty.VM_SPEC_VENDOR, "java.vm.specification.vendor"); // Java仮想マシンの仕様のベンダー
            put(SystemProperty.VM_SPEC_NAME, "java.vm.specification.name"); // Java仮想マシンの仕様名
            put(SystemProperty.VM_VERSION, "java.vm.version"); // Java仮想マシンの実装バージョン
            put(SystemProperty.VM_VENDOR, "java.vm.vendor"); // Java仮想マシンの実装のベンダー
            put(SystemProperty.VM_NAME, "java.vm.name"); // Java仮想マシンの実装名
            put(SystemProperty.SPEC_VERSION, "java.specification.version"); // Java
                                                                            // Runtime
                                                                            // Environmentの仕様バージョン
            put(SystemProperty.SPEC_VENDOR, "java.specification.vendor"); // Java
                                                                          // Runtime
                                                                          // Environmentの仕様のベンダー
            put(SystemProperty.SPEC_NAME, "java.specification.name"); // Java
                                                                      // Runtime
                                                                      // Environmentの仕様名
            put(SystemProperty.CLASS_VERSION, "java.class.version"); // Javaクラスの形式のバージョン番号
            put(SystemProperty.CLASS_PATH, "java.class.path"); // Javaクラス・パス
            put(SystemProperty.LIBRARY_PATH, "java.library.path"); // ライブラリのロード時に検索するパスのリスト
            put(SystemProperty.IO_TMPDIR, "java.io.tmpdir"); // デフォルト一時ファイルのパス
            put(SystemProperty.COMPILER, "java.compiler"); // 使用するJITコンパイラの名前
            put(SystemProperty.EXT_DIRS, "java.ext.dirs"); // 拡張ディレクトリのパス
            put(SystemProperty.OS_NAME, "os.name"); // オペレーティング・システム名
            put(SystemProperty.OS_ARCH, "os.arch"); // オペレーティング・システムのアーキテクチャ
            put(SystemProperty.OS_VERSION, "os.version"); // オペレーティング・システムのバージョン
            put(SystemProperty.FILE_SEPARATOR, "file.separator"); // ファイル区切り文字
            put(SystemProperty.PATH_SEPARATOR, "path.separator"); // パス区切り文字
            put(SystemProperty.LINE_SEPARATOR, "line.separator"); // 行区切り文字
            put(SystemProperty.USER_NAME, "user.name"); // ユーザーのアカウント名
            put(SystemProperty.USER_HOME, "user.home"); // ユーザーのホーム・ディレクトリ
            put(SystemProperty.USER_DIR, "user.dir"); // ユーザーの現在の作業ディレクトリ
            put(SystemProperty.FILE_ENCODING, "file.encoding"); // ファイルの文字コード 2

        };
    };

    /** プラットフォーム→文字列 */
    private static HashMap<KindOfPlatform, String> platformToStr = new HashMap<KindOfPlatform, String>() {
        {
            put(KindOfPlatform.LINUX, "Linux");
            put(KindOfPlatform.MAC, "Mac");
            put(KindOfPlatform.WINDOWS, "Windows");
            put(KindOfPlatform.SUN_OS, "SunOS");
        };
    };

    public static String convertPropertyToStr(SystemProperty property) {
        String str = null;
        if (propertyToStr.containsKey(property) == true) {
            str = propertyToStr.get(property);
        }
        return str;
    }

    /**
     * System.getPropertyのラッパー
     *
     * @param property
     *            プロパティ
     * @return 設定値
     */
    public static String getProperty(SystemProperty property) {
        String key = convertPropertyToStr(property);
        String val = null;
        if (key != null) {
            val = getProperty(key);
        }
        return val;
    }

    /**
     * System.getPropertyのラッパー
     *
     * @param property
     *            プロパティ
     * @return 設定値
     */
    public static String getProperty(String property) {
        return System.getProperty(property);
    }

    /**
     * 改行コード取得(プラットフォームに依存しない)
     *
     * @return 改行コード
     */
    public static String getNewLine() {
        return getProperty(SystemProperty.LINE_SEPARATOR);
    }

    /**
     * 改行コード取得(プラットフォームに依存しない)<br>
     * 改行コードを付加したメッセージを取得
     *
     * @param msg
     *            メッセージ
     * @return メッセージ
     */
    public static String getNewLine(String msg) {
        return msg + getNewLine();
    }

    /**
     * パスセパレーター取得(プラットフォームに依存しない)
     *
     * @return パスセパレーター
     */
    public static String getSeparator() {
        return File.separator;
    }

    /**
     * 実行中のプラットフォーム名取得
     *
     * @return プラットフォーム名
     */
    public static String getRunPlatformName() {
        KindOfPlatform p = getRunPlatform();
        return getPlatformName(p);
    }

    /**
     * OS名取得
     *
     * @return OS名
     */
    public static String getOSName() {
        return getProperty(SystemProperty.OS_NAME);
    }

    /**
     * プラットフォーム識別
     *
     * @return プラットフォームID
     */
    public static KindOfPlatform getRunPlatform() {
        KindOfPlatform os = KindOfPlatform.OTHER;
        String osName = getOSName().toLowerCase();
        if (osName.startsWith(GlobalConfig.OS_IdentificationStr_LINUX)) {
            os = KindOfPlatform.LINUX;
        }
        else if (osName.startsWith(GlobalConfig.OS_IdentificationStr_MAC)) {
            os = KindOfPlatform.MAC;
        }
        else if (osName.startsWith(GlobalConfig.OS_IdentificationStr_WINDOWS)) {
            os = KindOfPlatform.WINDOWS;
        }
        else if (osName.startsWith(GlobalConfig.OS_IdentificationStr_SUNOS)) {
            os = KindOfPlatform.SUN_OS;
        }
        else {
            os = KindOfPlatform.OTHER;
        }
        return os;
    }

    /**
     * プラットフォーム名取得
     *
     * @param platform
     *            プラットフォームID
     * @return プラットフォーム名
     */
    public static String getPlatformName(KindOfPlatform platform) {
        String osName = "unknownOS";
        if (platformToStr.containsKey(platform)) {
            osName = platformToStr.get(platform);
        }
        return osName;
    }

    /**
     * アプリケーションのカレントパス取得
     *
     * @param isAddingSeparator
     *            セパレータを追加するか
     * @return アプリケーションのカレントパス
     */
    public static String getCurrentPath(boolean isAddingSeparator) {
        String ret = "";
        String classPath = getProperty(SystemProperty.CLASS_PATH);

        // 開発環境での実行時に環境変数の区切りで複数のbinのパスが取得される
        String classPathSeparater = "";
        if (getRunPlatform() == KindOfPlatform.MAC) {
            classPathSeparater = ":";
        }
        else {
            classPathSeparater = ";";
        }
        String[] classPaths = classPath.split(classPathSeparater);
        classPath = classPaths[0];

        String[] sStr = classPath.split("\\" + getSeparator());
        if (sStr.length == 1) {
            return "";
        }

        File f = new File(classPath);
        ret = (isJarFile(f)) ? f.getParent() : f.getParent(); // TODO
                                                              // classもjarも同じ?
        return (isAddingSeparator == false) ? ret : (ret + getSeparator());
    }

    public static String getCurrentPath() {
        return getCurrentPath(true);
    }

    /**
     * jarファイルか判定
     *
     * @param file
     *            判定するファイル
     * @return 結果
     */
    public static boolean isJarFile(File file) {
        boolean ret = false;

        // ファイル判定
        if (file.isFile() && file.canRead()) {
            // .jar判定
            if (file.getPath().endsWith(".jar")) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * jarファイルか判定
     *
     * @param path
     *            パス
     * @return 結果
     */
    public static boolean isJarFile(String path) {
        return isJarFile(new File(path));
    }

    /**
     * ガーベジコレクションを明示的に実施
     *
     * @return freeMemory
     */
    public static long requestGarbageCollection() {
        long ret = 0;
        try {
            // ランタイム取得
            Runtime rt = Runtime.getRuntime();

            // ガーベジコレクションを実施
            rt.gc();

            // 参照を破棄してガーベジコレクション
            @SuppressWarnings("unused")
            Integer dummy = new Integer(0);
            dummy = null;
            rt.gc();

            ret = rt.freeMemory();
        }
        catch (Exception e) {
            ret = 0;
        }
        return ret;
    }

    /**
     * Javaバージョン文字列取得
     *
     * @return Javaバージョン
     */
    public static String getJavaVersion() {
        return getProperty(SystemProperty.VERSION);
    }
}
