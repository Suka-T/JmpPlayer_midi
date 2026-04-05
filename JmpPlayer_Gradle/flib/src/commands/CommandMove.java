package commands;

import function.Error;
import function.Utility;

public class CommandMove extends CommandFunc {

    public CommandMove() {
        super();
        setInfo(Cmd.COMMAND_MOVE, 2, new String[] { "<SrcPath>", "<DstPath>" }, "ファイル移動");
    }

    public boolean exec(String[] params) {
        boolean ret = false;

        try {
            String src = params[0];
            String dst = params[1];
            System.out.println(src + " >> " + dst);
            ret = Utility.moveFile(src, dst);
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }

}
