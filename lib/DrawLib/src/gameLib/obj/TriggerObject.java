package gameLib.obj;

import gameLib.GameObject;

public class TriggerObject extends GameObject {

    public TriggerObject() {
    }

    public TriggerObject(ObjectType type) {
        super(type);
    }

    protected boolean trigger() {
        /* 継承先に処理を記述 */
        return false;
    }

    protected void triggerEvent() {
        /* 継承先に処理を記述 */
    }

    @Override
    protected void update() {
        super.update();

        if (trigger() == true) {
            triggerEvent();
        }
    }

}
