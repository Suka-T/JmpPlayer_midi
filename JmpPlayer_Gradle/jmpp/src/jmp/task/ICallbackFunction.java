package jmp.task;

public interface ICallbackFunction {
    default void preCall() {
    };

    default void postCall() {
    };

    abstract void callback();

    default boolean isDeleteConditions(int count) {
        return false;
    }
}
