package jmp.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class TaskOfBase implements ITask, Runnable {

    class TaskQueue {
        protected List<TaskPacket> lst = null;

        public TaskQueue() {
            createInstance();
        }

        protected void createInstance() {
            this.lst = new LinkedList<TaskPacket>();
        }

        public boolean push(TaskPacket o) {
            lst.add(o);
            return true;
        }

        public TaskPacket pop() {
            TaskPacket ret = null;
            Iterator<TaskPacket> i = lst.iterator();
            while (i.hasNext()) {
                ret = i.next();
                i.remove();

                break;
            }
            return ret;
        }

        public void clear() {
            lst.clear();
        }

        public boolean isEmpty() {
            return lst.isEmpty();
        }

    }

    class SynchronizedTaskQueue extends TaskQueue {
        private Object mutex = new Object();
        
        @Override
        protected void createInstance() {
            this.lst = new LinkedList<TaskPacket>();
            //this.lst = Collections.synchronizedList(new LinkedList<TaskPacket>());
        }

        @Override
        public TaskPacket pop() {
            TaskPacket ret = null;
            synchronized (mutex) {
                ret = super.pop();
            }
            return ret;
        }

        @Override
        public boolean push(TaskPacket o) {
            boolean ret = false;
            synchronized (mutex) {
                ret = super.push(o);
            }
            return ret;
        }

        @Override
        public void clear() {
            synchronized (mutex) {
                super.clear();
            }
        }
    }

    private TaskQueue queue = null;
    private boolean isRunnable = true;
    private Thread thread = null;
    private long sleepTime = 100;
    private long waitTime = 0;

    public TaskOfBase(long sleepTime, boolean isSyncQueue) {
        if (isSyncQueue == true) {
            this.queue = new SynchronizedTaskQueue();
        }
        else {
            this.queue = new TaskQueue();
        }
        this.sleepTime = sleepTime;
        this.isRunnable = true;
        this.waitTime = 0;
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        begin();

        while (isRunnable) {
            long pastTime = System.currentTimeMillis();

            if (queue.isEmpty() == false) {
                TaskPacket obj = pop();
                if (obj != null) {
                    interpret(obj);
                }
            }

            loop();

            long newTime = System.currentTimeMillis();
            long pSleepTime = sleepTime - (newTime - pastTime);
            if (pSleepTime < 1) {
                pSleepTime = 1;
            }
            notifySleepTimeCalc(pSleepTime);

            if (this.waitTime > 0) {
                // 待ち時間を加算
                pSleepTime += this.waitTime;
                this.waitTime = 0;
            }

            try {
                Thread.sleep(pSleepTime);
            }
            catch (Exception e) {
            }
        }

        end();

        // コールバック関数のクリア
        clearQue();
    }

    /**
     * スリープタイム取得
     *
     * @return スリープタイム
     */
    public long getSleepTime() {
        return sleepTime;
    }

    @Override
    public void startTask() {
        this.thread.start();
    }

    @Override
    public void joinTask() {
        try {
            this.thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void waitTask(long sleepTime) {
        this.waitTime = sleepTime;
    }

    @Override
    public void queuing(TaskPacket packet) {
        push(packet);
    }

    @Override
    public void clearQue() {
        queue.clear();
    }

    protected void interpret(TaskPacket obj) {
    }

    protected boolean push(TaskPacket o) {
        return queue.push(o);
    }

    protected TaskPacket pop() {
        return queue.pop();
    }

    /**
     * タスク開始時に1度だけコールされるメソッド
     */
    abstract void begin();

    /**
     * タスク本処理
     */
    abstract void loop();

    /**
     * タスク終了時に1度だけコールされるメソッド
     */
    abstract void end();

    /** sleeptimeデバッグ用 */
    protected void notifySleepTimeCalc(long sleepTime) {
    }

    @Override
    public void exitTask() {
        isRunnable = false;
    }

    @Override
    public boolean isRunnable() {
        return isRunnable;
    }

}
