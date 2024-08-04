package process;

public interface IConsoleOutCallback {
    abstract void print(String s);

    abstract void println(String s);

    default void println() {
        println("");
    };
}
