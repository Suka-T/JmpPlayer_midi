package function;

import java.util.ArrayList;

import commands.Cmd;
import commands.CommandCopy;
import commands.CommandDelete;
import commands.CommandFunc;
import commands.CommandGlobalSetting;
import commands.CommandMove;
import commands.CommandOs;
import commands.CommandRead;
import commands.CommandToExport;
import commands.CommandToZip;
import commands.CommandUnZip;
import commands.CommandUnitTest;
import commands.CommandWrite;

public class CallLibrary {

    private static final CommandUnitTest _UT_COMMAND = new CommandUnitTest();

    // メッセージ
    public static final String INVALID_COMMAND_MES = "##> Invalid command. <##";
    public static final String INVALID_ARGS_MES = "##> Invalid args. <##";
    public static final String SUCCESS_MES = "##> Success. <##";
    public static final String FAIL_MES = "##> Fail. <##";

    private static ArrayList<CommandFunc> aCommand = new ArrayList<CommandFunc>() {
        {
            add(new CommandOs());
            add(new CommandGlobalSetting());
            add(new CommandToZip());
            add(new CommandUnZip());
            add(new CommandCopy());
            add(new CommandMove());
            add(new CommandDelete());
            add(new CommandToExport());
            add(new CommandRead());
            add(new CommandWrite());
        }
    };

    public static void main(String[] args) {
        if (args == null) {
            System.out.println(INVALID_ARGS_MES);
            return;
        }

        String command = Cmd.COMMAND_MANUAL;
        String[] params = null;

        // コマンド取得
        if (args.length > 0) {
            command = args[0];
        }

        if (command.equals(Cmd._COMMAND_UT) == true) {
            // ユニットテスト用コマンド
            _UT_COMMAND.exec(args);
            return;
        }

        // 引数取得
        if (args.length > 1) {
            int len = args.length - 1;
            params = new String[len];
            for (int i = 0; i < len; i++) {
                params[i] = args[i + 1];
            }
        }
        else {
            params = new String[0];
        }

        exec(command, params);
    }

    public static void exec(String command, String... params) {

        // マニュアル
        if (command.equalsIgnoreCase(Cmd.COMMAND_MANUAL) == true) {
            manual();
            return;
        }

        // コマンド実行
        for (CommandFunc func : aCommand) {
            if (func.checkCommand(command) == true) {
                String mes = INVALID_ARGS_MES;
                if (func.isValidParams(params) == true) {
                    boolean result = func.exec(params);
                    mes = (result == true) ? SUCCESS_MES : FAIL_MES;
                }
                System.out.println(mes);
                break;
            }
        }
    }

    private static void manual() {
        // マニュアル
        for (CommandFunc func : aCommand) {
            func.outputManual();
        }
    }
}
