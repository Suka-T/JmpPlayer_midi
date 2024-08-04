package jmp.task;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * タイマータスク。 <br>
 * 時系列コールバック関数の登録が可能
 *
 * @author akkut
 *
 */
public class TaskOfTimer extends TaskOfBase {

    // public static final long CYCLIC_TIMER_TASK_TIME = 100;

    private ArrayList<CallbackPackage> callbackPackages = new ArrayList<CallbackPackage>();
    private Object mutex = new Object();

    public TaskOfTimer() {
        super(100, true);
    }

    public void addCallbackPackage(CallbackPackage pakage) {
        synchronized (mutex) {
            callbackPackages.add(pakage);
        }
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        synchronized (mutex) {
            // スタックされたコールバックを呼び出し
            Iterator<CallbackPackage> i = callbackPackages.iterator();
            while (i.hasNext()) {
                CallbackPackage cp = i.next();
                cp.callback();
                if (cp.isDeleteConditions() == true) {
                    // コールバック関数の削除
                    i.remove();
                }
            }
        }
    }

    @Override
    void end() {
        // コールバック関数のクリア
        synchronized (mutex) {
            callbackPackages.clear();
        }
    }
}
