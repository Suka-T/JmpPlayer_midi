package commands;

import function.Platform;

public class CommandOs extends CommandFunc {

    public CommandOs() {
        super();
        setInfo(Cmd.COMMAND_OS, 0, null, "OS名を取得");
    }

    public boolean exec(String[] params) {
        System.out.println("OS name = " + Platform.getOSName());
        System.out.println("Platform = " + Platform.getRunPlatformName());
        return true;
    }
}
