package jmp;

import function.Platform;

public class JMPPlayer {
    public static void main(String[] args) {
        Platform.setExecutionMainClass(JMPPlayer.class);
        
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        // ただしfalse起動失敗は常に終了する
        if (res == 1) {
            System.exit(res);
        }
    }
}
