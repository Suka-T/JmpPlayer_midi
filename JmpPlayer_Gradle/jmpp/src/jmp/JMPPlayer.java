package jmp;

import java.io.File;
import java.net.URISyntaxException;

import function.Platform;

public class JMPPlayer {
    public static void main(String[] args) {
        
        // クラスの場所を取得
        File path;
        try {
            path = new File(JMPPlayer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        }
        catch (URISyntaxException e) {
            path = new File("");
        }
        Platform.setExecutionPath(path.isFile() ? path.getParentFile() : path.getParentFile());
        
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        // ただしfalse起動失敗は常に終了する
        if (res == 1) {
            System.exit(res);
        }
    }
}
