package commands;

import function.Error;
import function.Utility;

public class CommandUnZip extends CommandFunc {

    public CommandUnZip() {
        super();
        setInfo(Cmd.COMMAND_UNZIP, 2, new String[] { "<ZipPath>", "<DstPath>" }, "ZIP解凍");
    }

    @Override
    public boolean exec(String[] params) {
        super.exec(params);

        boolean ret = false;
        try {
            String src = params[0];
            String dst = params[1];
            System.out.println(src + " >> " + dst);
            Utility.unZip(src, dst);
            ret = true;
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }
}
