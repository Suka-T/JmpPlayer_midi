package jmp.task;

import java.util.ArrayList;

public class CallbackPackage {
    private ArrayList<ICallbackFunction> callbackFunctions = new ArrayList<ICallbackFunction>();
    private int currentCount = 0;
    private int callMillsCount = -1;

    public CallbackPackage(long callMills, long sleepTime) {
        this.callMillsCount = (int) (callMills / sleepTime);
    }

    public void setCallMillsCount(int callMillsCount) {
        this.callMillsCount = callMillsCount;
    }

    public void addCallbackFunction(ICallbackFunction callbackFunction) {
        this.callbackFunctions.add(callbackFunction);
    }

    public void callback() {
        currentCount++;
        if (currentCount >= callMillsCount) {
            // コールバック
            for (int i = 0; i < callbackFunctions.size(); i++) {
                ICallbackFunction func = callbackFunctions.get(i);
                func.callback();

                if (func.isDeleteConditions(currentCount) == true) {
                    // 削除
                    callbackFunctions.remove(i);
                }
            }

            currentCount = 0;
        }
    }

    public boolean isDeleteConditions() {
        if (callbackFunctions == null) {
            return true;
        }
        return (callbackFunctions.isEmpty() == true) || (callbackFunctions.size() == 0);
    }
}
