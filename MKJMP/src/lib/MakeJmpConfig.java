package lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import function.Platform;
import function.Utility;
import jlib.JMPLIB;

public class MakeJmpConfig {

    public static final String KEY_PLGNAME = "plg_name";
    public static final String KEY_JAR = "jar_path";
    public static final String KEY_RES = "res_path";
    public static final String KEY_DATA = "data_path";
    public static final String KEY_ADD_DATA = "data_add";
    public static final String KEY_OUTPUT = "output_path";

    protected String pluginName = "";
    protected String jar = "";
    protected String res = "";
    protected String data = "";
    protected boolean addData = false;
    protected String output = "";
    protected String version = "";
    protected String versionName = "";

    public MakeJmpConfig(String pluginName, String jar, String res, boolean addData, String data, String output, String version, String versionName) {
        this.pluginName = pluginName;
        this.jar = jar;
        this.res = res;
        this.data = data;
        this.addData = addData;
        this.output = output;
        this.version = version;
        this.versionName = versionName;
    }

    public MakeJmpConfig(File file) throws IOException {
        read(file);
    }

    public MakeJmpConfig() {
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getJar() {
        return jar;
    }

    public String getRes() {
        return res;
    }

    public String getData() {
        return data;
    }

    public boolean isAddData() {
        return addData;
    }

    public String getOutput() {
        return output;
    }

    public String getVersion() {
        return version;
    }
    
    public String getVersionName() {
        return versionName;
    }

    public void write(File file) throws FileNotFoundException, UnsupportedEncodingException {
        String contents = "";

        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_PLGNAME, "=", getPluginName(), Platform.getNewLine()));
        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_JAR, "=", getJar(), Platform.getNewLine()));
        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_RES, "=", getRes(), Platform.getNewLine()));
        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_DATA, "=", getData(), Platform.getNewLine()));
        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_ADD_DATA, "=", (isAddData() ? "TRUE" : "FALSE"), Platform.getNewLine()));
        contents = Utility.stringsCombin(contents, Utility.stringsCombin(KEY_OUTPUT, "=", getOutput(), Platform.getNewLine()));

        Utility.outputTextFile(file.getPath(), contents);
    }

    public void read(File file) throws IOException {
        List<String> contents = Utility.getTextFileContents(file.getPath());

        for (String line : contents) {
            String[] sLine = line.split("=");
            if (sLine.length >= 1) {
                String key = sLine[0];
                String value = (sLine.length >= 2) ? sLine[1] : "";
                if (key.equalsIgnoreCase(KEY_PLGNAME) == true) {
                    pluginName = value;
                }
                else if (key.equalsIgnoreCase(KEY_JAR) == true) {
                    jar = value;
                }
                else if (key.equalsIgnoreCase(KEY_RES) == true) {
                    res = value;
                }
                else if (key.equalsIgnoreCase(KEY_DATA) == true) {
                    data = value;
                }
                else if (key.equalsIgnoreCase(KEY_ADD_DATA) == true) {
                    addData = (value.equalsIgnoreCase("TRUE") == true) ? true : false;
                }
                else if (key.equalsIgnoreCase(KEY_OUTPUT) == true) {
                    output = value;
                }
            }
        }

        // version新規発行
        version = JMPLIB.BUILD_VERSION;
        versionName = JMPLIB.VERSION_NAME;
    }

}
