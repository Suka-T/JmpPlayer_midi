package jmp.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonRegister {

    private static final String BUILDER_TYPE = JmpFileBuilderFactory.BUILDER_TYPE_TEXT;

    private List<CommonRegisterINI> iniList = null;

    public CommonRegister() {
        iniList = new ArrayList<CommonRegisterINI>();
        iniList.clear();
    }

    public CommonRegister(CommonRegisterINI... inis) {
        this();

        for (int i = 0; i < inis.length; i++) {
            add(inis[i]);
        }
    }

    private CommonRegisterINI getIni(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return ini;
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        for (CommonRegisterINI ini : iniList) {
            if (ini.key.equalsIgnoreCase(key) == true) {
                return true;
            }
        }
        return false;
    }

    public void add(String key, String value) {
        add(key, value, false);
    }

    public void add(String key, String value, boolean isWrite) {
        if (containsKey(key) == true) {
            setValue(key, value);
            return;
        }
        iniList.add(new CommonRegisterINI(key, value, isWrite));
    }

    public void add(CommonRegisterINI ini) {
        if (containsKey(ini.key) == true) {
            setValue(ini.key, ini.value);
            return;
        }
        iniList.add(ini);
    }

    public boolean setValue(String key, String value) {
        if (containsKey(key) == true) {
            getIni(key).value = value;
            return true;
        }
        return false;
    }

    public String getValue(String key) {
        if (containsKey(key) == true) {
            return new String(getIni(key).value);
        }
        return "";
    }

    public String[] getKeySet() {
        int len = iniList.size();
        String[] lst = new String[len];
        for (int i = 0; i < len; i++) {
            lst[i] = iniList.get(i).key;
        }
        return lst;
    }

    public void read(String path) {
        File file = new File(path);
        if (file.exists() == false) {
            return;
        }
        if (file.canRead() == false) {
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        String[] keyset = getKeySet();

        JmpFileBuilderFactory fc = new JmpFileBuilderFactory(BUILDER_TYPE);
        IJmpFileBuilder builder = fc.createFileBuilder(map, keyset);
        builder.read(file);

        for (String key : map.keySet()) {
            setValue(key, map.get(key));
        }
    }

    public void write(String path) {
        File file = new File(path);
        if (file != null && file.getParentFile() != null && file.getParentFile().exists() == false) {
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        String[] keyset = getKeySet();
        for (CommonRegisterINI ini : iniList) {
            if (ini.isWrite == true) {
                map.put(ini.key, ini.value);
            }
        }

        JmpFileBuilderFactory fc = new JmpFileBuilderFactory(BUILDER_TYPE);
        IJmpFileBuilder builder = fc.createFileBuilder(map, keyset);
        builder.write(file);
    }
}
