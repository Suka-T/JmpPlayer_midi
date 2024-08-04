package jmp.task;

import jmp.core.JMPCore;
import jmp.task.TaskPacket.PacketType;

public class TaskOfNotify extends TaskOfBase {

    /** Notify ID */
    public static enum NotifyID {
        UPDATE_CONFIG, UPDATE_SYSCOMMON, FILE_RESULT_BEGIN, FILE_RESULT_END
    }

    public TaskOfNotify() {
        super(100, true);
    }

    @Override
    protected void interpret(TaskPacket obj) {
        if (obj.getType() == PacketType.Notify) {
            NotifyPacket notify = (NotifyPacket) obj;
            JMPCore.parseNotifyPacket(notify.getId(), notify.getDatas());
        }
    }

    @Override
    protected void begin() {
    }

    @Override
    protected void loop() {
    }

    @Override
    protected void end() {
    }
}
