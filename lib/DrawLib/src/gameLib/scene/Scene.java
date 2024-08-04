package gameLib.scene;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameLib.GameObject;
import gameLib.GameObject.ObjectType;

public abstract class Scene implements ComponentListener, MouseListener, MouseMotionListener, KeyListener {
    private Map<ObjectType, List<GameObject>> objsMap = null;

    public Scene() {
        objsMap = new HashMap<GameObject.ObjectType, List<GameObject>>();
        objsMap.put(ObjectType.PLAYER, new ArrayList<GameObject>());
        objsMap.put(ObjectType.ENEMY, new ArrayList<GameObject>());
        objsMap.put(ObjectType.OBJECT, new ArrayList<GameObject>());
        objsMap.put(ObjectType.BULLET, new ArrayList<GameObject>());
        objsMap.put(ObjectType.OTHER, new ArrayList<GameObject>());
    }

    public List<GameObject> getObjects(ObjectType type) {
        return objsMap.get(type);
    }

    public void add(GameObject obj) {
        objsMap.get(obj.getType()).add(obj);
    }

    public void remove(GameObject obj) {
        List<GameObject> lst = objsMap.get(obj.getType());
        if (lst.contains(obj) == true) {
            lst.remove(obj);
        }
    }

    public void remove(ObjectType type) {
        List<GameObject> lst = objsMap.get(type);
        if (lst.size() > 0) {
            lst.remove(lst.size() - 1);
        }
    }

    public void removeAll(ObjectType type) {
        List<GameObject> lst = objsMap.get(type);
        lst.clear();
    }

    public void drawObject(Graphics g) {
        for (ObjectType type : objsMap.keySet()) {
            List<GameObject> lst = objsMap.get(type);
            for (int i = 0; i < lst.size(); i++) {
                lst.get(i).draw(g);
            }
        }
    }

    public abstract void paintScene(Graphics g);

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
