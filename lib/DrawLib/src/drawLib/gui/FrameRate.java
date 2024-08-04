package drawLib.gui;

public class FrameRate {
    private long baseTime = 0;
    private int frameCount = 0;
    private float frameRate = 0;
    private long updateCycleTime = 200;

    public FrameRate() {
        baseTime = System.currentTimeMillis();
    }

    public float getFrameRate() {
        return frameRate;
    }

    public void frameCount() {
        frameCount++;
        long nowTime = System.currentTimeMillis();
        // 1秒周期更新
        if (nowTime - baseTime >= getUpdateCycleTime()) {
            // フレームレート
            frameRate = (float) (frameCount * 1000) / (float) (nowTime - baseTime);

            // クリア
            baseTime = nowTime;
            frameCount = 0;
        }
    }

    public long getUpdateCycleTime() {
        return updateCycleTime;
    }

    public void setUpdateCycleTime(long updateCycleTime) {
        this.updateCycleTime = updateCycleTime;
    }
}
