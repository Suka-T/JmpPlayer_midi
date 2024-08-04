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
    public TaskOfSequence() {
        super(100, true);
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
