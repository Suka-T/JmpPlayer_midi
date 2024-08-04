package commands;

import function.GlobalConfig;

public class CommandGlobalSetting extends CommandFunc {

    public CommandGlobalSetting() {
        super();
        setInfo(Cmd.COMMAND_GSETTING, 0, null, "GlobalSetting取得");
    }

    public boolean exec(String[] params) {
        System.out.println(GlobalConfig.getGlobalConfigStr());
        return true;
    }

}
