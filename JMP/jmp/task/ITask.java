package jmp.task;

public interface ITask {

    /**
     * タスク開始状態遷移
     */
    abstract void startTask();

    /**
     * タスク終了状態遷移
     */
    abstract void exitTask();

    /**
     * タスク待機状態遷移
     */
    abstract void joinTask();

    /**
     * キューを空にする
     */
    abstract void clearQue();

    /**
     * キューにパケットを追加
     * 
     * @param packet
     */
    abstract void queuing(TaskPacket packet);

    /**
     * タスク待ち状態遷移
     *
     * @param sleepTime
     */
    abstract void waitTask(long sleepTime);

    /**
     * タスクが実行状態か？
     *
     * @return
     */
    abstract boolean isRunnable();
}
