package jmp.gui.ui;

import java.io.File;
import java.util.List;

public interface IDropFileCallback {
    abstract void catchDropFile(File file);
    default void catchDropMultiFile(List<File> files) {
        if ((files != null) && (files.size() > 0)) {
            String path = files.get(0).getPath();
            File file = new File(path);
            catchDropFile(file);
        }
    };
}
