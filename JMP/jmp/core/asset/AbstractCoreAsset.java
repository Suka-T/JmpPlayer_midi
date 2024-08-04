package jmp.core.asset;

public abstract class AbstractCoreAsset {

    public enum OperateType {
        FileLoad,
    }

    protected OperateType opeType;

    public AbstractCoreAsset(OperateType opeType) {
        this.opeType = opeType;
    }

    public OperateType getOperateType() {
        return opeType;
    }

}
