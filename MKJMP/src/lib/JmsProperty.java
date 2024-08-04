package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import function.Platform;
import function.Utility;

/**
 * JMSファイルのプロパティクラス
 *
 * @author akkut
 *
 */
public class JmsProperty {
    private File data;
    private File res;
    private File jar;
    private String version = "";
    private String versionName = "";
    private boolean isDeleteRequest = false;

    private String pluginDir = "";
    private String dataDir = "";
    private String resDir = "";

    public JmsProperty(String pluginDir, String dataDir, String resDir, File jar, File data, File res, String version, String versionName) {
        setPluginDir(pluginDir);
        setDataDir(dataDir);
        setResDir(resDir);
        setRes(res);
        setData(data);
        setJar(jar);
        setVersion(version);
        setVersionName(versionName);
        this.isDeleteRequest = false;
    }

    public static JmsProperty getJmsProparty(String pluginDir, String dataDir, String resDir, File jmsFile) {
        JmsProperty jms = null;

        BufferedReader reader;
        String line = "";
        try {
            File pluginFile = null;
            File dataFile = null;
            File resFile = null;

            FileInputStream fs = new FileInputStream(jmsFile);
            InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
            reader = new BufferedReader(isr);

            boolean isData = false;
            boolean isRes = false;
            String version = "";
            String versionName = "";

            while ((line = reader.readLine()) != null) {
                /* コメントを除外 */
                String[] comment = line.split("#", 0);
                if (comment.length > 0) {
                    line = comment[0];
                }

                String[] sLine = line.split("=", 0);
                if (sLine.length >= 2) {
                    String key = sLine[0].trim();
                    String param = sLine[1].trim();
                    if (key.equalsIgnoreCase(MakeJmpLib.JMS_KEY_PLUGIN) == true) {
                        String plgPath = Utility.stringsCombin(pluginDir, Platform.getSeparator(), param);

                        // プラグインファイルを保持
                        pluginFile = new File(plgPath);
                    }
                    else if (key.equalsIgnoreCase(MakeJmpLib.JMS_KEY_DATA) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isData = true;
                        }
                    }
                    else if (key.equalsIgnoreCase(MakeJmpLib.JMS_KEY_RES) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isRes = true;
                        }
                    }
                    else if (key.equalsIgnoreCase(MakeJmpLib.JMS_KEY_VERSION) == true) {
                        version = param;
                    }
                    else if (key.equalsIgnoreCase(MakeJmpLib.JMS_KEY_VERSION_NAME) == true) {
                        versionName = param;
                    }
                }
            }

            if (isData == true) {
                String dataPath = Utility.stringsCombin(dataDir, Platform.getSeparator(), Utility.getFileNameNotExtension(pluginFile));

                dataFile = new File(dataPath);
            }
            if (isRes == true) {
                String resPath = Utility.stringsCombin(resDir, Platform.getSeparator(), Utility.getFileNameNotExtension(pluginFile));

                resFile = new File(resPath);
            }

            jms = new JmsProperty(pluginDir, dataDir, resDir, pluginFile, dataFile, resFile, version, versionName);

            String fileName = Utility.getFileNameAndExtension(jmsFile);
            if (fileName.startsWith(MakeJmpLib.JMS_REMOVE_TAG) == true) {
                jms.setDeleteRequest(true);
            }
            reader.close();
        }
        catch (Exception e) {
            jms = null;
        }
        finally {
            reader = null;
        }
        return jms;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public File getJar() {
        return jar;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public boolean isDeleteRequest() {
        return isDeleteRequest;
    }

    public void setDeleteRequest(boolean isDeleteRequest) {
        this.isDeleteRequest = isDeleteRequest;
    }

    public File getRes() {
        return res;
    }

    public void setRes(File res) {
        this.res = res;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getPluginDir() {
        return pluginDir;
    }

    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    public String getResDir() {
        return resDir;
    }

    public void setResDir(String resDir) {
        this.resDir = resDir;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
