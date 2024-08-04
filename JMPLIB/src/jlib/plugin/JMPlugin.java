package jlib.plugin;

import java.io.File;

import jlib.core.JMPCoreAccessor;

public abstract class JMPlugin implements IPlugin {

    public JMPlugin() {
    }

    /**
     * プラグイン名を取得する
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return JMPCoreAccessor.getSystemManager().getPluginName(this);
    }

    @Override
    public void update() {
        /* オーバーライドして処理を記述する */
    }

    @Override
    public boolean isEnable() {
        /* オーバーライドして処理を記述する */
        return true;
    }

    @Override
    public void loadFile(File file) {
        /* オーバーライドして処理を記述する */
    }

    @Override
    public void notifyUpdateCommonRegister(String key) {
        /* オーバーライドして処理を記述する */
    }

    @Override
    public void notifyUpdateConfig(String key) {
        /* オーバーライドして処理を記述する */
    }
}
