package jmp.file;

import java.io.File;

public interface IJmpFileBuilder {
    abstract String getAppName();

    abstract String getVersion();

    abstract boolean read(File file);

    abstract boolean write(File file);
}
