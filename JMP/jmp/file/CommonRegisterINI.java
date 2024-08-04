package jmp.file;

public class CommonRegisterINI {
    public String key, value;
    public boolean isWrite = false;

    public CommonRegisterINI(String key, String value, boolean isWrite) {
        this.key = key;
        this.value = value;
        this.isWrite = isWrite;
    }
}
