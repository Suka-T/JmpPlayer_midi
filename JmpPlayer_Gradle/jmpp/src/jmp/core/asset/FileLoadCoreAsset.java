package jmp.core.asset;

import java.io.File;

import jmp.file.FileResult;

public class FileLoadCoreAsset extends AbstractCoreAsset {

    public File file;
    public FileResult result;

    public FileLoadCoreAsset(File file, FileResult result) {
        super(OperateType.FileLoad);
        this.file = file;
        this.result = result;
    }

}
