package jmp.player;

public interface IMoviePlayerModel {

    abstract void setVisibleView(boolean visible);

    abstract boolean isValidView();

    abstract boolean isVisibleView();
}
