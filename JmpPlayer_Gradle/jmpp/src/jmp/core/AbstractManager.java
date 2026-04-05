package jmp.core;

import jlib.core.IManager;
import jmp.core.ManagerInstances.TypeOfKey;
import jmp.core.asset.AbstractCoreAsset;
import jmp.file.IJmpConfigDatabase;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public abstract class AbstractManager implements IManager, IJmpConfigDatabase {

    protected String name = "";
    private boolean initializeFlag = false;

    AbstractManager(String name) {
        this.name = name;

        // Coreに登録
        ManagerInstances.register(this);
    }

    public String getName() {
        return name;
    }

    protected boolean initFunc() {
        initializeFlag = true;
        return true;
    }

    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    // Config更新通知
    protected void notifyUpdateConfig(String key) {
    }

    // cReg更新通知
    protected void notifyUpdateCommonRegister(String key) {
    }

    public boolean isFinishedInitialize() {
        return initializeFlag;
    }

    /**
     * 全管理クラス共通の実行処理
     * 
     * @param asset
     *            実行資産
     * @return 実行の成否(falseの場合は処理を中断する)
     */
    protected boolean operate(AbstractCoreAsset asset) {
        return true;
    }

    @Override
    public void setConfigParam(String key, String value) {
        if (key.equals(ManagerInstances.CFG_KEY_INITIALIZE) == false) {
            if (ManagerInstances.CFG_INIT_TABLE.containsKey(key) == false) {
                return;
            }

            String newValue = new String(value);
            TypeOfKey type = ManagerInstances.CFG_INIT_TABLE.get(key).type;
            if (type == TypeOfKey.BOOL) {
                // BOOLの表記を統一
                boolean b = JmpUtil.toBoolean(newValue, false);
                if (b == true) {
                    newValue = new String(IJmpConfigDatabase.IJ_TRUE);
                }
                else {
                    newValue = new String(IJmpConfigDatabase.IJ_FALSE);
                }
            }
            ManagerInstances.setConfigParam(key, newValue);
        }
        // 設定変更通知
        ManagerInstances.sendNotify(NotifyID.UPDATE_CONFIG, key);
    }

    @Override
    public String getConfigParam(String key) {
        return ManagerInstances.getConfigParam(key);
    }

}
