package gameLib.task;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameLib.GameObject;
import gameLib.GameObject.ObjectType;

public class TaskController {

    private List<GameTask> tasks = null;

    public TaskController() {
        tasks = new ArrayList<GameTask>();
    }

    public void init() {
        long cyclic = 20;
        tasks.add(new GameTask(ObjectType.PLAYER, cyclic));
        tasks.add(new GameTask(ObjectType.ENEMY, cyclic));
        tasks.add(new GameTask(ObjectType.OBJECT, cyclic));
        tasks.add(new GameTask(ObjectType.BULLET, cyclic));
        tasks.add(new GameTask(ObjectType.OTHER, cyclic));
    }

    public void start() {
        for (GameTask t : tasks) {
            t.start();
        }
    }

    public void exit() {
        for (GameTask t : tasks) {
            t.exitTask();
        }
        for (GameTask t : tasks) {
            try {
                t.join();
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void drawObjects(Graphics g) {
        for (GameTask t : tasks) {
            for (GameObject obj : t.getObjects()) {
                obj.draw(g);
            }
        }
    }

    public boolean add(GameObject obj) {
        boolean ret = true;
        GameTask task = getGameTask(obj.getType());
        if (task != null) {
            task.add(obj);
        }
        else {
            ret = false;
        }
        return ret;
    }

    public boolean remove(GameObject obj) {
        boolean ret = true;
        GameTask task = getGameTask(obj.getType());
        if (task != null) {
            task.remove(obj);
        }
        else {
            ret = false;
        }
        return ret;
    }

    public GameTask getGameTask(ObjectType type) {
        for (GameTask t : tasks) {
            if (t.getTargetType() == type) {
                return t;
            }
        }
        return null;
    }

}
