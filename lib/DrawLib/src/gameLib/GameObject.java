package gameLib;

import java.util.ArrayList;
import java.util.List;

import drawLib.DrawObjectEx;
import gameLib.scene.Scene;

public class GameObject extends DrawObjectEx {

    private List<ObjectType> collisionTargets = null;

    private int taskCount = 0;
    private int taskMaxCount = 0;

    private boolean requestRemove = false;

    public void setUpdateCyclicTime(long time) {
        long taskCyclicTime = GameLib.getInstance().getCycleTime(getType());
        if (taskCyclicTime == 0) {
            taskMaxCount = 0;
            return;
        }

        if (time == 0) {
            taskMaxCount = 0;
            return;
        }

        taskMaxCount = (int) (time / taskCyclicTime);
    }

    public void callUpdateFunc() {
        taskCount++;
        if (taskMaxCount <= taskCount) {
            update();
            taskCount = 0;
        }
    }

    public static enum ObjectType {
        PLAYER, ENEMY, BULLET, OBJECT, OTHER,
    }

    private ObjectType type = ObjectType.OTHER;

    public GameObject() {
        setType(ObjectType.OTHER);
        collisionTargets = new ArrayList<ObjectType>();
    }

    public GameObject(ObjectType type) {
        setType(type);
        collisionTargets = new ArrayList<ObjectType>();
    }

    public void addCollisionTargets(ObjectType... type) {
        for (ObjectType t : type) {
            if (collisionTargets.contains(t) == false) {
                collisionTargets.add(t);
            }
        }
    }

    public void removeCollisionTargets(ObjectType... type) {
        for (ObjectType t : type) {
            if (collisionTargets.contains(t) == true) {
                collisionTargets.remove(t);
            }
        }
    }

    public List<ObjectType> getCollisionTarget() {
        return collisionTargets;
    }

    public boolean isCollisionTarget(ObjectType type) {
        if (collisionTargets.contains(type) == true) {
            return true;
        }
        return false;
    }

    protected void update() {
        /* 継承先で処理を記述 */
    }

    public void collision(GameObject dst) {
        /* 継承先で処理を記述 */
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    private void checkCollision(ObjectType type) {
        Scene scene = GameLib.getInstance().getSceneManager().getCurrentScene();
        if (scene == null) {
            return;
        }

        List<GameObject> lst = scene.getObjects(type);
        for (int i = 0; i < lst.size(); i++) {
            GameObject dst = lst.get(i);
            if (isCollision(dst) == true) {
                collision(dst);
                break;
            }
        }
    }

    @Override
    public void setX(int x) {
        boolean collisionFlag = false;
        if (x != getX()) {
            collisionFlag = true;
        }

        super.setX(x);

        if (collisionFlag == true) {
            for (ObjectType type : getCollisionTarget()) {
                checkCollision(type);
            }
        }
    }

    @Override
    public void setY(int y) {
        boolean collisionFlag = false;
        if (y != getY()) {
            collisionFlag = true;
        }

        super.setY(y);

        if (collisionFlag == true) {
            for (ObjectType type : getCollisionTarget()) {
                checkCollision(type);
            }
        }
    }

    public void add() {
        Scene scene = GameLib.getInstance().getSceneManager().getCurrentScene();
        if (scene != null) {
            scene.getObjects(getType()).add(this);
        }
    }

    public void remove() {
        requestRemove = true;
    }

    public boolean isRequestRemove() {
        return requestRemove;
    }

}
