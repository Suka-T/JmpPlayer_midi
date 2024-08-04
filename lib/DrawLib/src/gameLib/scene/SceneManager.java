package gameLib.scene;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private String currentSceneName = "";
    private Map<String, Scene> sceneRegistry = null;

    public SceneManager() {
        sceneRegistry = new HashMap<String, Scene>();
    };

    public void registerScene(String name, Scene scene) {
        sceneRegistry.put(name, scene);
    }

    public void setCurrentScene(String name) {
        currentSceneName = name;
    }

    public Scene getCurrentScene() {
        return getScene(currentSceneName);
    }

    public Scene getScene(String name) {
        return sceneRegistry.get(name);
    }

}
