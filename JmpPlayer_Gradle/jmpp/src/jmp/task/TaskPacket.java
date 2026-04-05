package jmp.task;

public class TaskPacket {

    public enum PacketType {
        Notify, Callback, Object, RequestUpdate, // Window更新
    }

    private Object[] data;
    private PacketType type;

    public TaskPacket(PacketType type, Object... d) {
        this.type = type;
        this.data = d;
    }

    public Object[] getDatas() {
        return data;
    }

    public Object getData() {
        return getData(0);
    }

    public Object getData(int index) {
        if (this.data == null) {
            return null;
        }
        if (index >= this.data.length) {
            return null;
        }
        return this.data[index];
    }

    public PacketType getType() {
        return type;
    }

}
