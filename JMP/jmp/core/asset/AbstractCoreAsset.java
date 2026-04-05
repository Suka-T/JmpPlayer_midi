package jmp.core.asset;

public abstract class AbstractCoreAsset {

    public enum OperateType {
        PrepareFileLoad, FileLoad, PrepareDualFileLoad, DualFileLoad,
    }

    protected OperateType opeType;

    public AbstractCoreAsset(OperateType opeType) {
        this.opeType = opeType;
    }

    public OperateType getOperateType() {
        return opeType;
    }

}
