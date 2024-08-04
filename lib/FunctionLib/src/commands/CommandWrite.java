package commands;

import function.Error;
import function.Utility;

public class CommandWrite extends CommandFunc {

    public CommandWrite() {
        super();
        setInfo(Cmd.COMMAND_WRITE, 3, new String[] { "<Path>", "<Text>", "{ <CharSet> }" }, "テキストファイルを出力する。");
    }

    public boolean exec(String[] params) {

        boolean ret = false;

        try {
            if (params.length == 2) {
                String path = params[0];
                String text = params[1];
                System.out.println("Write >> " + path);
                Utility.outputTextFile(path, text);
            }
            else {
                String path = params[0];
                String text = params[1];
                String charSet = params[2];
                System.out.println("Write >> " + path);
                System.out.println("CharSet >> " + charSet);
                Utility.outputTextFile(path, text, charSet);
            }
            ret = true;
        }
        catch (Exception e) {
            ret = false;
            System.out.println(Error.getMsg(e));
        }
        return ret;
    }

}
