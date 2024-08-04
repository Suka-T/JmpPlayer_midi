package jlib.player;

public interface IPlayerListener {
    abstract void startSequencer();

    abstract void stopSequencer();

    abstract void updateTickPosition(long before, long after);
}
