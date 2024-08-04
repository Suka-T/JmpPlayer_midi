package commands;

import function.Error;
import function.Utility;

public class CommandDelete extends CommandFunc {

    public CommandDelete() {
        super();
        setInfo(Cmd.COMMAND_DELETE, 1, new String[] { "<Path>" }, "ファイル・ディレクトリ削除");
    }

    public boolean exec(String[] params) {
        boolean ret = false;

        try {
            String path = params[0];
            System.out.println("Delete : " + path);
            ret = Utility.deleteFileDirectory(path);
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }

}
