package commands;

import function.Error;
import function.Utility;

public class CommandToExport extends CommandFunc {

    public CommandToExport() {
        super();
        setInfo(Cmd.COMMAND_TO_EXPORT, 3, new String[] { "<Extension>", "<SrcDirectoryPath>", "<DstPath>" }, "ディレクトリから指定拡張子を取得する");
    }

    public boolean exec(String[] params) {

        boolean ret = false;

        try {
            String ex = params[0];
            String src = params[1];
            String dst = params[2];
            System.out.println(src + "[." + ex + "]" + " >> " + dst);
            Utility.toExport(ex, src, dst);
            ret = true;
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }
}
