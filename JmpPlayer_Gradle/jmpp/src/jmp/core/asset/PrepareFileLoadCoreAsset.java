package jmp.core.asset;

import java.io.File;

public class PrepareFileLoadCoreAsset extends AbstractCoreAsset {

    public File file;
    
    public PrepareFileLoadCoreAsset(File file) {
        super(OperateType.PrepareFileLoad);
        this.file = file;
    }

}
