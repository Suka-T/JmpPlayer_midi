package jmp.util.toolkit;

import java.util.ArrayList;
import java.util.List;

import jlib.util.IUtilityToolkit;

public class UtilityToolkitManager {

    public static final String DEFAULT_UTIL_TOOLKIT_NAME = DefaultUtilityToolkit.class.getSimpleName();

    private static DefaultUtilityToolkit DefaultUtilToolkit = null;
    private List<IUtilityToolkit> lst = null;

    private static UtilityToolkitManager instance = null;

    public static UtilityToolkitManager getInstance() {
        if (instance == null) {
            DefaultUtilToolkit = new DefaultUtilityToolkit();

            instance = new UtilityToolkitManager();
            instance.addUtilityToolkit(DefaultUtilToolkit);
        }
        return instance;
    }

    private UtilityToolkitManager() {
        lst = new ArrayList<IUtilityToolkit>();
    }

    public void addUtilityToolkit(IUtilityToolkit kit) {
        lst.add(kit);
    }

    public IUtilityToolkit getUtilityToolkit(String className) {
        for (IUtilityToolkit kit : lst) {
            if (kit.getClass().getSimpleName().equals(className)) {
                return kit;
            }
        }
        return DefaultUtilToolkit;
    }

}
