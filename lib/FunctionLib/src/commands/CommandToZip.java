package commands;

import function.Error;
import function.Utility;

public class CommandToZip extends CommandFunc {

    public CommandToZip() {
        super();
        setInfo(Cmd.COMMAND_ZIP, 2, new String[] { "<DirectoryPath>", "<ZipPath>" }, "ZIP圧縮");
    }

    @Override
    public boolean exec(String[] params) {
        super.exec(params);

        boolean ret = false;
        try {
            String src = params[0];
            String dst = params[1];
            System.out.println(src + " >> " + dst);
            ret = Utility.toZip(src, dst);
        }
        catch (Exception e) {
            System.out.println(Error.getMsg(e));
            ret = false;
        }
        return ret;
    }

}
