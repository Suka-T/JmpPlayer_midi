package jmp.file;

import jmp.core.ManagerInstances.TypeOfKey;

public class JmpConfigValueType {

    public String value = "";
    public jmp.core.ManagerInstances.TypeOfKey type = TypeOfKey.STRING;

    public JmpConfigValueType(String value, TypeOfKey type) {
        this.value = value;
        this.type = type;
    }

    public JmpConfigValueType(String value) {
        this.value = value;
        this.type = TypeOfKey.STRING;
    }
}
