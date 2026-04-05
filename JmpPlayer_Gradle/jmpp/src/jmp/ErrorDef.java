package jmp;

import java.util.HashMap;
import java.util.Map;

import function.Platform;
import jmp.core.JMPCore;
import jmp.lang.DefineLanguage.LangID;

public class ErrorDef {
    /* エラーID定義 */
    // NoError
    public static final int ERROR_ID_NOERROR = 0;

    public static final int UNIT_OF_ERROR_ID = 100;
    // UNKNOWN区分
    public static final int ERROR_ID_UNKNOWN = (UNIT_OF_ERROR_ID * 1);
    public static final int ERROR_ID_UNKNOWN_EXIT_APPLI = (ERROR_ID_UNKNOWN + 1);
    public static final int ERROR_ID_UNKNOWN_FAIL_LOAD_PLAYER = (ERROR_ID_UNKNOWN_EXIT_APPLI + 1);
    // SYSTEM区分
    public static final int ERROR_ID_SYSTEM = (UNIT_OF_ERROR_ID * 2);
    public static final int ERROR_ID_SYSTEM_FAIL_INIT_FUNC = (ERROR_ID_SYSTEM + 1);
    public static final int ERROR_ID_SYSTEM_FAIL_END_FUNC = (ERROR_ID_SYSTEM_FAIL_INIT_FUNC + 1);
    // SOUND区分
    public static final int ERROR_ID_SOUND = (UNIT_OF_ERROR_ID * 3);
    // FILE区分
    public static final int ERROR_ID_FILE = (UNIT_OF_ERROR_ID * 4);
    // PLUGIN区分
    public static final int ERROR_ID_PLUGIN = (UNIT_OF_ERROR_ID * 5);
    public static final int ERROR_ID_PLUGIN_FAIL_LOAD = (ERROR_ID_PLUGIN + 1);

    // エラーIDとエラーメッセージの対応表
    private static final Map<Integer, LangID> errorSubMsgTable = new HashMap<Integer, LangID>() {
        {
            // UNKNOWN区分
            put(ERROR_ID_UNKNOWN_EXIT_APPLI, LangID.Exit_the_application);
            put(ERROR_ID_UNKNOWN_FAIL_LOAD_PLAYER, LangID.There_was_a_problem_preparing_the_music_player);
            // SYSTEM区分
            put(ERROR_ID_SYSTEM_FAIL_INIT_FUNC, LangID.Failed_to_initialize_the_application);
            put(ERROR_ID_SYSTEM_FAIL_END_FUNC, LangID.The_application_could_not_be_terminated_successfully);
            // PLUGIN区分
            put(ERROR_ID_PLUGIN_FAIL_LOAD, LangID.PLUGIN_LOAD_ERROR);
        }
    };

    public static boolean isVisibleErrorID(int errorID) {
        boolean visible = false;
        if (ERROR_ID_UNKNOWN <= errorID && errorID < ERROR_ID_UNKNOWN + UNIT_OF_ERROR_ID) {
            /* unknown */
            visible = true;
        }
        else if (ERROR_ID_SYSTEM <= errorID && errorID < ERROR_ID_SYSTEM + UNIT_OF_ERROR_ID) {
            /* system */
            visible = true;
        }
        else if (ERROR_ID_SOUND <= errorID && errorID < ERROR_ID_SOUND + UNIT_OF_ERROR_ID) {
            /* sound */
            visible = false;
        }
        else if (ERROR_ID_FILE <= errorID && errorID < ERROR_ID_FILE + UNIT_OF_ERROR_ID) {
            /* file */
            visible = false;
        }
        else if (ERROR_ID_PLUGIN <= errorID && errorID < ERROR_ID_PLUGIN + UNIT_OF_ERROR_ID) {
            /* plugin */
            visible = false;
        }

        if (JMPFlags.DebugMode == true) {
            visible = true;
        }
        return visible;
    }

    public static String getPrimMsg(int errorID) {
        String msg = "";
        if (ERROR_ID_UNKNOWN <= errorID && errorID < ERROR_ID_UNKNOWN + UNIT_OF_ERROR_ID) {
            /* unknown */
            msg = JMPCore.getLanguageManager().getLanguageStr(LangID.An_unexpected_error_has_occurred);
        }
        else if (ERROR_ID_SYSTEM <= errorID && errorID < ERROR_ID_SYSTEM + UNIT_OF_ERROR_ID) {
            /* system */
        }
        else if (ERROR_ID_SOUND <= errorID && errorID < ERROR_ID_SOUND + UNIT_OF_ERROR_ID) {
            /* sound */
        }
        else if (ERROR_ID_FILE <= errorID && errorID < ERROR_ID_FILE + UNIT_OF_ERROR_ID) {
            /* file */
        }
        else if (ERROR_ID_PLUGIN <= errorID && errorID < ERROR_ID_PLUGIN + UNIT_OF_ERROR_ID) {
            /* plugin */
        }
        return msg;
    }

    public static String getSubMsg(int errorID) {
        String msg = "";
        if (errorSubMsgTable.containsKey(errorID) == true) {
            msg = JMPCore.getLanguageManager().getLanguageStr(errorSubMsgTable.get(errorID));
        }
        return msg;
    }

    public static String getTotalMsg(int errorID) {
        String errorMsg = "";
        String primMsg = ErrorDef.getPrimMsg(errorID);
        String subMsg = ErrorDef.getSubMsg(errorID);
        String sErrorID = "";
        if (ErrorDef.isVisibleErrorID(errorID) == true) {
            sErrorID = String.format("(ErrorID = %d)", errorID);
        }

        if (primMsg.isEmpty() == false) {
            if (errorMsg.isEmpty() == false) {
                errorMsg += Platform.getNewLine();
            }
            errorMsg += primMsg;
        }
        if (subMsg.isEmpty() == false) {
            if (errorMsg.isEmpty() == false) {
                errorMsg += Platform.getNewLine();
            }
            errorMsg += subMsg;
        }
        if (sErrorID.isEmpty() == false) {
            if (errorMsg.isEmpty() == false) {
                errorMsg += Platform.getNewLine();
            }
            errorMsg += sErrorID;
        }
        return errorMsg;
    }
}
