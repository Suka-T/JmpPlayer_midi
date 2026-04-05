package jlib.plugin;

import jlib.core.JMPCoreAccessor;

public interface ISupportExtensionConstraints {
    /**
     * 許可する拡張子を指定
     *
     * @return 許可する拡張子（ , 区切りで指定）
     */
    abstract String allowedExtensions();

    default String[] allowedExtensionsArray() {
        String ae = allowedExtensions();
        return ae.split(",");
    }

    default boolean isEnableCurrentPlayer() {
        boolean isSupportEx = false;
        for (String ex : allowedExtensionsArray()) {
            if (JMPCoreAccessor.getSoundManager().isSupportedExtension(ex) == true) {
                isSupportEx = true;
                break;
            }
        }
        return isSupportEx;
    }
}
