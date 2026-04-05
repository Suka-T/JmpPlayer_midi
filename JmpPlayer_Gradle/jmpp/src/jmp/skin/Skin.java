package jmp.skin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import function.Utility;
import jmp.core.JMPCore;
import jmp.core.SystemManager;

public class Skin {

    public static final String RSRC_FILE_ICON_OTEHER = "file_icon_OTHER.png";
    public static final String RSRC_FILE_ICON_MIDI = "file_icon_MIDI.png";
    public static final String RSRC_FILE_ICON_WAV = "file_icon_WAV.png";
    public static final String RSRC_FILE_ICON_XML = "file_icon_XML.png";
    public static final String RSRC_FILE_ICON_MUSIC = "file_icon_MUSIC.png";
    public static final String RSRC_FOLDER_ICON = "folder_icon.png";
    public static final String RSRC_BTN_ICON_PLAY = "btn_icon_PLAY.png";
    public static final String RSRC_BTN_ICON_STOP = "btn_icon_STOP.png";
    public static final String RSRC_BTN_ICON_NEXT = "btn_icon_NEXT.png";
    public static final String RSRC_BTN_ICON_NEXT2 = "btn_icon_NEXT2.png";
    public static final String RSRC_BTN_ICON_PREV = "btn_icon_PREV.png";
    public static final String RSRC_BTN_ICON_PREV2 = "btn_icon_PREV2.png";
    public static final String RSRC_BTN_ICON_AUTO = "btn_icon_AUTO.png";
    public static final String RSRC_BTN_ICON_LOOP = "btn_icon_LOOP.png";
    public static final String RSRC_BTN_ICON_LIST = "btn_icon_LIST.png";
    public static final String RSRC_BTN_ICON_VIEW = "btn_icon_VIEW.png";
    // ↓File名追加後、必ずLSTに追加すること!!
    private static final String[] LST = { RSRC_FILE_ICON_OTEHER, RSRC_FILE_ICON_MIDI, RSRC_FILE_ICON_WAV, RSRC_FILE_ICON_XML, RSRC_FOLDER_ICON,
            RSRC_BTN_ICON_PLAY, RSRC_BTN_ICON_STOP, RSRC_BTN_ICON_NEXT, RSRC_BTN_ICON_NEXT2, RSRC_BTN_ICON_PREV, RSRC_BTN_ICON_PREV2, RSRC_BTN_ICON_AUTO,
            RSRC_BTN_ICON_LOOP, RSRC_BTN_ICON_LIST, RSRC_FILE_ICON_MUSIC, RSRC_BTN_ICON_VIEW };

    private Map<String, Resource> rsrcMap = new HashMap<String, Resource>();

    private SkinGlobalConfig globalConfig = null;
    private SkinLocalConfig localConfig = null;

    public Skin(SkinGlobalConfig config) {
        globalConfig = config;
        localConfig = new SkinLocalConfig();
        for (String lstname : LST) {
            rsrcMap.put(lstname, null);
        }
        readLocalConfig();
        load();
    }

    public Skin() {
        globalConfig = null;
        localConfig = new SkinLocalConfig();
        for (String lstname : LST) {
            rsrcMap.put(lstname, null);
        }
    }

    private void load() {
        for (String lstname : LST) {
            createResource(lstname);
        }
    }

    private void readLocalConfig() {
        String skinPath = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SKIN_DIR);
        String path = Utility.pathCombin(skinPath, globalConfig.getName(), "skinconf.txt");
        try {
            localConfig.read(new File(path));
        }
        catch (IOException e) {
            localConfig.initialize();
        }
    }

    private void createResource(String name) {
        String skinPath = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SKIN_DIR);
        String path = Utility.pathCombin(skinPath, globalConfig.getName(), name);
        if (Utility.isExsistFile(path) == true) {
            if (Utility.checkExtension(path, "png") == true) {
                ImageResource rsrc = new ImageResource();
                rsrc.setImage(ResourceUtility.readImage(path, localConfig.isConvertTransparent(), localConfig.getTransparentColor()));
                if (rsrc.getImage() != null) {
                    rsrcMap.put(name, rsrc);
                }
            }
        }
    }

    public Resource getResource(String name) {
        if (rsrcMap.containsKey(name) == false) {
            return null;
        }
        return rsrcMap.get(name);
    }

    public SkinLocalConfig getLocalConfig() {
        return localConfig;
    }
}
