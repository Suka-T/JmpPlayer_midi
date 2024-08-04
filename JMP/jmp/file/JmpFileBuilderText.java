package jmp.file;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jmp.core.JMPCore;
import jmp.util.JmpUtil;

public class JmpFileBuilderText implements IJmpFileBuilder {

    private static final String KER_SEPARATOR = "<->";
    private static final String SPECIAL_TAG = "@SPEC@";
    private static final String SPECIAL_KEY_APPNAME = "APP_NAME";
    private static final String SPECIAL_KEY_VERSION = "APP_VERSION";

    private String[] keyset = null;
    private Map<String, String> database = null;

    private String appName = "Unknown";
    private String version = "Unknown";

    private class MapElement {
        public String key;
        public String value;

        public MapElement(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public JmpFileBuilderText(Map<String, String> database, String[] keyset) {
        this.database = database;
        this.keyset = keyset;
    }

    private MapElement parseElement(String line) {
        String[] sLine = line.split(KER_SEPARATOR);
        String key = sLine[0].trim();
        String value = (sLine.length >= 2) ? sLine[1] : "";
        return new MapElement(key, value);
    }

    public void readSpecialKey(MapElement ele) {
        if (ele.key.equalsIgnoreCase(SPECIAL_KEY_APPNAME) == true) {
            appName = new String(ele.value);
        }
        else if (ele.key.equalsIgnoreCase(SPECIAL_KEY_VERSION) == true) {
            version = new String(ele.value);
        }
    }

    @Override
    public boolean read(File file) {
        boolean ret = true;
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(file);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    MapElement element;
                    if (line.startsWith(SPECIAL_TAG) == true) {
                        String spLine = line.substring(SPECIAL_TAG.length());
                        element = parseElement(spLine);
                        readSpecialKey(element);
                        continue;
                    }
                    else {
                        element = parseElement(line);
                    }

                    boolean isContainsKey = false;
                    if (keyset == null) {
                        // キーセット未指定の場合は全て追加
                        isContainsKey = true;
                    }
                    else {
                        for (String ckey : keyset) {
                            if (element.key.equals(ckey) == true) {
                                isContainsKey = true;
                                break;
                            }
                        }
                    }

                    if (isContainsKey == true) {
                        database.put(element.key, element.value);
                    }
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean write(File file) {
        boolean ret = true;

        try {
            List<String> textContents = new LinkedList<String>();

            // Special key
            textContents.add(SPECIAL_TAG + SPECIAL_KEY_APPNAME + KER_SEPARATOR + JMPCore.APPLICATION_NAME);
            textContents.add(SPECIAL_TAG + SPECIAL_KEY_VERSION + KER_SEPARATOR + JMPCore.APPLICATION_VERSION);

            for (String key : database.keySet()) {
                String value = this.database.get(key);
                textContents.add(key + KER_SEPARATOR + value);
            }
            JmpUtil.writeTextFile(file, textContents);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public String getAppName() {
        return new String(appName);
    }

    @Override
    public String getVersion() {
        return new String(version);
    }
}
