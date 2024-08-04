package gameLib.task;

import java.util.ArrayList;
import java.util.List;

import gameLib.GameObject;
import gameLib.GameObject.ObjectType;

public class GameTask extends Thread {

    private long cyclicTime = 100;

    private boolean runnable = true;

    private List<GameObject> objs = null;

    private ObjectType targetType = ObjectType.OTHER;

    public GameTask(ObjectType targetType, long cyclicTime) {
        objs = new ArrayList<GameObject>();
        setTargetType(targetType);
        setCyclicTime(cyclicTime);
    }

    public void setObjects(List<GameObject> objs) {
        this.objs = objs;
    }

    public List<GameObject> getObjects() {
        return objs;
    }

    public int getObjectCount() {
        if (objs == null) {
            return 0;
        }
        return objs.size();
    }

    @Override
    public void run() {
        while (runnable) {
            long pastTime = System.currentTimeMillis() << 16;

            update();

            long newTime = System.currentTimeMillis() << 16;
            long sleepTime = cyclicTime - ((newTime - pastTime) >> 16);
            if (sleepTime < 0) {
                sleepTime = 0;
            }

            try {
                GameTask.sleep(sleepTime);
            }
            catch (Exception e) {
            }
        }
        return;
    }

    public void add(GameObject obj) {
        List<GameObject> lst = getObjects();
        if (lst == null) {
            return;
        }

        lst.add(obj);
    }

    private void removeImpl(GameObject obj) {
        List<GameObject> lst = getObjects();
        if (lst == null) {
            return;
        }

        lst.remove(obj);
    }

    public void remove(GameObject obj) {
        obj.remove();
    }

    private void update() {
        List<GameObject> lst = getObjects();
        if (lst == null) {
            return;
        }

        for (int i = 0; i < lst.size(); i++) {
            GameObject src = lst.get(i);
            if (src != null) {
                src.callUpdateFunc();
            }
        }

        for (int i = 0; i < lst.size(); i++) {
            GameObject src = lst.get(i);
            if (src != null) {
                if (src.isRequestRemove() == true) {
                    removeImpl(src);
                }
            }
        }
    }

    public void exitTask() {
        runnable = false;
    }

    public void setCyclicTime(long cyclicTime) {
        this.cyclicTime = cyclicTime;
    }

    public long getCyclicTime() {
        return cyclicTime;
    }

    public ObjectType getTargetType() {
        return targetType;
    }

    public void setTargetType(ObjectType targetType) {
        this.targetType = targetType;
    }
}
