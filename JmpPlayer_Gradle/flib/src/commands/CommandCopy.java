package commands;

import function.Error;
import function.Utility;

public class CommandCopy extends CommandFunc {

    public CommandCopy() {
        super();

        setInfo(Cmd.COMMAND_COPY, 2, new String[] { "<SrcPath>", "<DstPath>" }, "ファイルコピー");
    }

    public boolean exec(String[] params) {
        /* copy */
        boolean ret = false;

        try {
            String src = params[0];
            String dst = params[1];
            System.out.println(src + " >> " + dst);
            ret = Utility.copyFile(src, dst);
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }
}
