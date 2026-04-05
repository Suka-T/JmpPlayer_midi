package jmp.task;

public class CallbackPacket extends TaskPacket {

    private static final int OBJ_TYPE_RUNNABLE = 0;
    private static final int OBJ_TYPE_CALLBACK = 1;

    private int type = OBJ_TYPE_RUNNABLE;

    public CallbackPacket(Runnable runnable) {
        super(PacketType.Callback, runnable);
        type = OBJ_TYPE_RUNNABLE;
    }

    public CallbackPacket(ICallbackFunction callback) {
        super(PacketType.Callback, callback);
        type = OBJ_TYPE_CALLBACK;
    }

    public void exec() {
        if (type == OBJ_TYPE_RUNNABLE) {
            Runnable obj = (Runnable) getData();
            obj.run();
        }
        else if (type == OBJ_TYPE_CALLBACK) {
            ICallbackFunction obj = (ICallbackFunction) getData();
            obj.preCall();
            obj.callback();
            obj.postCall();
        }
    }

}
