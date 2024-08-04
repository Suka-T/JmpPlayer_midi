package jmp;

public class JMPPlayer {
    public static void main(String[] args) {
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        // ただしfalse起動失敗は常に終了する
        if (res == 1) {
            System.exit(res);
        }
    }
}
