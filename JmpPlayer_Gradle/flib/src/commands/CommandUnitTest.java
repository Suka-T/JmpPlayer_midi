package commands;

import function.Utility;

public class CommandUnitTest extends CommandFunc {

    public CommandUnitTest() {
        super();

        setInfo(Cmd._COMMAND_UT, 0, null, "Unit Test");
    }

    public boolean exec(String[] params) {
        boolean ret = false;
        boolean yFlag = false;
        int opt = Utility.openPlainDialog(null, "test", "test", true);
        switch (opt) {
            case Utility.CONFIRM_RESULT_YES:
                System.out.println("YES");
                yFlag = true;
                break;
            case Utility.CONFIRM_RESULT_NO:
                System.out.println("NO");
                break;
            case Utility.CONFIRM_RESULT_CANCEL:
                System.out.println("CANCEL");
                break;
            default:
                System.out.println(":" + opt);
                break;
        }

        if (yFlag == true) {
            /* テストコード */
        }
        return ret;
    }
}
