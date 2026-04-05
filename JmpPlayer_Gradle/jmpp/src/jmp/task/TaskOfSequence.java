package jmp.task;

import jmp.task.TaskPacket.PacketType;

/**
 * シーケンスタスク。 <br>
 * コールバックを実行する
 *
 * @author akkut
 *
 */
public class TaskOfSequence extends TaskOfBase {
    public TaskOfSequence(int priority) {
        super(100, priority, true);
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
    }

    @Override
    void end() {
    }

    @Override
    protected void interpret(TaskPacket obj) {
        if (obj.getType() == PacketType.Callback) {
            ((CallbackPacket) obj).exec();
        }
    }
}
