package jmp.core.asset;

import java.io.File;

public class PrepareDualFileLoadCoreAsset extends PrepareFileLoadCoreAsset {

    public File subFile;
    
    public PrepareDualFileLoadCoreAsset(File file, File subFile) {
        super(file);

        this.opeType = OperateType.PrepareDualFileLoad;
        this.subFile = subFile;
    }
}
