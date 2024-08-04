package commands;

import java.util.List;

import function.Error;
import function.Utility;

public class CommandRead extends CommandFunc {

    public CommandRead() {
        super();
        setInfo(Cmd.COMMAND_READ, 2, new String[] { "<Path>", "{ <CharSet> }" }, "テキストファイルを読み込む。");
    }

    public boolean exec(String[] params) {

        boolean ret = false;

        try {
            List<String> res;
            if (params.length == 1) {
                String path = params[0];
                System.out.println("Read >> " + path);
                res = Utility.getTextFileContents(path);
            }
            else {
                String path = params[0];
                String charSet = params[1];
                System.out.println("Read >> " + path);
                System.out.println("CharSet >> " + charSet);
                res = Utility.getTextFileContents(path, charSet);
            }

            if (res != null) {
                System.out.println();
                System.out.println("##> Read Contents \"START\" <##");
                for (String line : res) {
                    System.out.println(line);
                }
                System.out.println("##> Read Contents \"END\" <##");
                System.out.println();
                ret = true;
            }
            else {
                ret = false;
            }
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }

}
