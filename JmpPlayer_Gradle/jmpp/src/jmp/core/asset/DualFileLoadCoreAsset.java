package jmp.core.asset;

import java.io.File;

import jmp.file.FileResult;

public class DualFileLoadCoreAsset extends FileLoadCoreAsset {

    public File subFile;

    public DualFileLoadCoreAsset(File file, FileResult result, File subFile) {
        super(file, result);

        this.opeType = OperateType.DualFileLoad;
        this.subFile = subFile;
    }
}
