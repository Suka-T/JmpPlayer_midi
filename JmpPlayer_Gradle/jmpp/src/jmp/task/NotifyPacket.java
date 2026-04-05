package jmp.task;

import jmp.task.TaskOfNotify.NotifyID;

public class NotifyPacket extends TaskPacket {

    private NotifyID notifyId;

    public NotifyPacket(NotifyID notifyId, Object... d) {
        super(PacketType.Notify, d);
        this.notifyId = notifyId;
    }

    public NotifyID getId() {
        return notifyId;
    }

}
