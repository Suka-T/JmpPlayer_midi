package jmp.file;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConfigDatabase {

    private static final String BUILDER_TYPE = JmpFileBuilderFactory.BUILDER_TYPE_XML;

    // 設定データベース
    private Map<String, String> database = null;

    private String[] keyset = null;

    private String appName = "None";
    private String version = "None";

    private ConfigDatabase() {
        setup(null);
    }

    public ConfigDatabase(String[] keys) {
        setup(keys);
    }

    public ConfigDatabase(Set<String> keys) {
        setup(keySetToArray(keys));
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private void setup(String[] keys) {
        database = new HashMap<String, String>();
        database.clear();

        keyset = keys;

        if (keyset != null) {
            for (String key : keyset) {
                database.put(key, "");
            }
        }
    }

    public static String[] keySetToArray(Set<String> keys) {
        String[] aKey = new String[keys.size()];
        int i = 0;
        Iterator<String> ite = keys.iterator();
        while (ite.hasNext()) {
            String s = ite.next();
            aKey[i] = s;
            i++;
        }
        return aKey;
    }

    public static ConfigDatabase create(String path) {
        File file = new File(path);
        if (file.exists() == false) {
            return null;
        }

        ConfigDatabase db = new ConfigDatabase();
        db.reading(file.getPath());
        return db;
    }

    public String[] getKeySet() {
        return keyset;
    }

    public void setConfigParam(String key, String value) {
        if (database.containsKey(key) == true) {
            database.put(key, value);
        }
    }

    public String getConfigParam(String key) {
        String ret = "";
        if (database.containsKey(key) == true) {
            ret = database.get(key);
        }
        return ret;
    }

    public boolean output(String path) {
        JmpFileBuilderFactory fc = new JmpFileBuilderFactory(BUILDER_TYPE);
        IJmpFileBuilder builder = fc.createFileBuilder(database, keyset);
        return builder.write(new File(path));
    }

    public boolean reading(String path) {
        setAppName("Unknown");
        setVersion("Unknown");

        JmpFileBuilderFactory fc = new JmpFileBuilderFactory(BUILDER_TYPE);
        IJmpFileBuilder builder = fc.createFileBuilder(database, keyset);

        boolean ret = builder.read(new File(path));
        appName = builder.getAppName();
        version = builder.getVersion();
        keyset = keySetToArray(database.keySet());
        return ret;
    }
}
