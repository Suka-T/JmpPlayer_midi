package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jmp.util.JmpUtil;

public class JMPCommand {
    private static class Mnemonic {
        public String mnemonic = "";
        public Mnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        };
        
        public boolean check(String cmd) {
            if (cmd.startsWith(mnemonic) == true) {
                return true;
            }
            return false;
        }
        
        public String getOption(String cmd) {
            String opt = "";
            if (cmd.length() >= mnemonic.length() + 1) {
                opt = cmd.substring(mnemonic.length() + 1);
            }
            return opt;
        }
    }
    
    public static List<String> SCommandLog = new ArrayList<String>();
    
    public static String PRIME_MNEMONIC_SOUND = "sound";
    public static String PRIME_MNEMONIC_DATA = "data";
    public static String PRIME_MNEMONIC_FILE = "file";

    // sound 
    private static Mnemonic mnemonicSoundPlay = new Mnemonic(PRIME_MNEMONIC_SOUND + " play");
    private static Mnemonic mnemonicSoundStop = new Mnemonic(PRIME_MNEMONIC_SOUND + " stop");
    
    // data 
    private static Mnemonic mnemonicDataCfg = new Mnemonic(PRIME_MNEMONIC_DATA + " cfg");
    
    // file
    private static Mnemonic mnemonicFileList = new Mnemonic(PRIME_MNEMONIC_FILE + " list");
    private static Mnemonic mnemonicFileListLoad = new Mnemonic(PRIME_MNEMONIC_FILE + " list load");
    
    public static final List<String> SMnemonicList = new ArrayList<String>() {
        {
            add(mnemonicSoundPlay.mnemonic);
            add(mnemonicSoundStop.mnemonic);
            add(mnemonicDataCfg.mnemonic);
            add(mnemonicFileList.mnemonic);
            add(mnemonicFileListLoad.mnemonic);
        }
    };
    
    private static void print(String str) {
        SystemManager system = JMPCore.getSystemManager();
        system.consoleOutln(">> " + str);
    }
    
    public static boolean parse(String cmd) {
        
        SoundManager sound = JMPCore.getSoundManager();
        DataManager data = JMPCore.getDataManager();
        FileManager fileMgr = JMPCore.getFileManager();
        
        print(cmd);
        
        boolean result = true;
        
        // sound
        if (mnemonicSoundPlay.check(cmd) == true) {
            sound.play();
        }
        else if (mnemonicSoundStop.check(cmd) == true) {
            sound.stop();
        }
        // data
        else if (mnemonicDataCfg.check(cmd) == true) {
            String opt = mnemonicDataCfg.getOption(cmd);
            if (opt.isEmpty() == false) {
                String[] sOpt = opt.split(" ");
                if (sOpt.length >= 2) {
                    if (sOpt[0].isEmpty() == false) {
                        data.setConfigParam(sOpt[0], sOpt[1]);
                    }
                }
                else {
                    print(sOpt[0] + "," + data.getConfigParam(sOpt[0]));
                }
            }
            else {
                for (String key : data.getKeySet()) {
                    print(key + "," + data.getConfigParam(key));
                }
            }
        }
        else if (mnemonicSoundStop.check(cmd) == true) {
            sound.stop();
        }
        // file
        else if (mnemonicFileListLoad.check(cmd) == true) {
            File f = new File(data.getPlayListPath());
            Map<String, File> fileMap = fileMgr.getFileMap(f);
            Object[] keys = fileMap.keySet().toArray();
            Arrays.sort(keys);
            
            String opt = mnemonicFileListLoad.getOption(cmd);
            if (opt.isEmpty() == false) {
                int no = JmpUtil.toInt(opt);
                if (no > 0) {
                    if (no - 1 < keys.length) {
                        File loadFile = fileMap.get(keys[no - 1]);
                        if (loadFile.isFile()) {
                            fileMgr.loadFile(loadFile);
                        }
                        else if (loadFile.isDirectory()) {
                            data.setPlayListPath(loadFile.getAbsolutePath());
                        }
                    }
                }
                
            }
        }
        else if (mnemonicFileList.check(cmd) == true) {
            File f = new File(data.getPlayListPath());
            Map<String, File> fileMap = fileMgr.getFileMap(f);
            Object[] keys = fileMap.keySet().toArray();
            Arrays.sort(keys);
            
            String opt = mnemonicFileList.getOption(cmd);
            if (opt.isEmpty() == false) {
                
            }
            else {
                int no = 1;
                for (Object s : keys) {
                    File loadFile = fileMap.get(keys[no - 1]);
                    String fType = "?";
                    if (loadFile.isFile()) {
                        fType = "F";
                    } else if (loadFile.isDirectory()) {
                        fType = "D";
                    }
                    print("[" + no + " " + fType + "]" + s.toString());
                    no++;
                }
                
            }
        }
        else {
            result = false;
        }
        return result;
        
    }
}
