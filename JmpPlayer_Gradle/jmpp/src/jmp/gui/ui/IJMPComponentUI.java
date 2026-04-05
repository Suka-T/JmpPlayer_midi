package jmp.gui.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Window;

import function.Utility;
import jlib.gui.IJmpMainWindow;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.WindowManager;

public interface IJMPComponentUI {
    public static final int DEFAULT_WINDOW_OFFSET_X = 70;
    public static final int DEFAULT_WINDOW_OFFSET_Y = 50;

    abstract void updateBackColor();

    default void updateDebugMenu() {
    };

    default void initComponentUI() {
    }

    default Color getJmpBackColor() {
        SystemManager sm = JMPCore.getSystemManager();
        if (sm == null) {
            return Color.WHITE;
        }

        String colorStr = sm.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_PLAYER_BACK_COLOR);
        if (colorStr.equalsIgnoreCase("") == false) {
            return Utility.convertCodeToHtmlColor(colorStr);
        }
        else {
            return Utility.convertCodeToHtmlColor("#404040");
        }
    }

    default Point getDefaultWindowLocation() {
        Point p = new Point(50, 50);

        WindowManager wm = JMPCore.getWindowManager();
        if (wm != null) {
            IJmpMainWindow mainWindow = wm.getMainWindow();
            if (mainWindow instanceof Window) {
                Window root = (Window) mainWindow;
                if (root != null) {
                    int offsetX, offsetY;
                    if (root.isAlwaysOnTop() == true) {
                        offsetX = root.getWidth();
                        offsetY = 0;
                    }
                    else {
                        offsetX = DEFAULT_WINDOW_OFFSET_X;
                        offsetY = DEFAULT_WINDOW_OFFSET_Y;
                    }
                    p = root.getLocation();
                    p.setLocation(p.getX() + offsetX, p.getY() + offsetY);
                }
            }
        }
        return p;
    }

}
