package drawLib.animation;

public class AnimationInfo {

    public int x = 0;
    public int y = 0;
    public int numOfCount = 0;
    public long cycleTime = -1;

    public AnimationInfo(int x, int y, long totalTime, long cycleTime) {
        int numOfCount = (int) (totalTime / cycleTime);
        setInfo(x, y, numOfCount, cycleTime);
    }

    public AnimationInfo(int x, int y, int numOfCount, long cycleTime) {
        setInfo(x, y, numOfCount, cycleTime);
    }

    public AnimationInfo(int x, int y, int numOfCount) {
        setInfo(x, y, numOfCount, -1);
    }

    private void setInfo(int x, int y, int numOfCount, long cycleTime) {
        this.x = x;
        this.y = y;
        this.numOfCount = numOfCount;
        this.cycleTime = cycleTime;
    }
}
