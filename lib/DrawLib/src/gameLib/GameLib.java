package gameLib;

import java.awt.Graphics;

import gameLib.GameObject.ObjectType;
import gameLib.scene.Scene;
import gameLib.scene.SceneManager;
import gameLib.task.GameTask;
import gameLib.task.TaskController;

public class GameLib {

    private SceneManager sceneManager = null;
    private TaskController taskController = null;

    private static GameLib instance = new GameLib();

    private GameLib() {
        sceneManager = new SceneManager();
        taskController = new TaskController();
    }

    public static GameLib getInstance() {
        return instance;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void registerScene(String name, Scene scene) {
        sceneManager.registerScene(name, scene);
    }

    public void changeScene(String name) {
        sceneManager.setCurrentScene(name);

        // シーンが管理するオブジェクトをタスクシステムに登録しなおす
        registerTaskObject(ObjectType.PLAYER);
        registerTaskObject(ObjectType.ENEMY);
        registerTaskObject(ObjectType.OBJECT);
        registerTaskObject(ObjectType.BULLET);
        registerTaskObject(ObjectType.OTHER);
    }

    private void registerTaskObject(ObjectType type) {
        taskController.getGameTask(type).setObjects(sceneManager.getCurrentScene().getObjects(type));
    }

    public void init() {
        taskController.init();
        taskController.start();
    }

    public void exit() {
        taskController.exit();
    }

    public void drawScene(Graphics g) {
        Scene scene = sceneManager.getCurrentScene();
        scene.paintScene(g);
    }

    public void add(String sceneName, GameObject obj) {
        Scene scene = sceneManager.getScene(sceneName);
        scene.add(obj);
    }

    public void add(GameObject obj) {
        Scene scene = sceneManager.getCurrentScene();
        scene.add(obj);
    }

    public void remove(String sceneName, GameObject obj) {
        Scene scene = sceneManager.getScene(sceneName);
        scene.remove(obj);
    }

    public void remove(GameObject obj) {
        Scene scene = sceneManager.getCurrentScene();
        scene.remove(obj);
    }

    public int getObjectCount(ObjectType type) {
        GameTask task = taskController.getGameTask(type);
        if (task == null) {
            return 0;
        }
        return task.getObjectCount();
    }

    public void setCycleTime(ObjectType type, long cyclicTime) {
        GameTask task = taskController.getGameTask(type);
        if (task != null) {
            task.setCyclicTime(cyclicTime);
        }
    }

    public long getCycleTime(ObjectType type) {
        GameTask task = taskController.getGameTask(type);
        if (task != null) {
            return task.getCyclicTime();
        }
        return 0;
    }
}
