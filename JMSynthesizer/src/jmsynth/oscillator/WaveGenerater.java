package jmsynth.oscillator;

public class WaveGenerater {
    private WaveGenerater() {
    }

    private static double trimF(double f) {
        return trimF(f, 100.0);
    }

    private static double trimF(double f, double r) {
        return Math.round(f * r) / r;
    }

    /**
     * サイン波生成
     *
     * @param f
     * @param overallLeval
     * @param reverse
     * @return
     */
    public static byte makeSinWave(double f, int overallLeval, boolean reverse) {
        return makeSinWave(f, overallLeval, reverse, 100.0);
    }

    public static byte makeSinWaveForLowSampling(double f, int overallLeval, boolean reverse) {
        return makeSinWave(f, overallLeval, reverse, 10.0);
    }
    
    public static byte makeSinWave(double f, int overallLeval, boolean reverse, double samplingRate) {
        double ff = (reverse == false) ? (1.0 - trimF(f, samplingRate)) : trimF(f, samplingRate);
        return (byte) ((Math.sin(2.0 * Math.PI * ff) * (double)overallLeval));
    }

    /**
     * 矩形波生成
     *
     * @param f
     * @param overallLevel
     * @param reverse
     * @return
     */
    public static byte makeSquareWave(double f, int overallLevel, boolean reverse, boolean lowRes) {
        return makePulseWave(f, overallLevel, 0.5, reverse, lowRes);
    }

    // private static final double DUTY_THRESHOLD = 0.05;
    private static final double DUTY_THRESHOLD = 0.04;
    private static final double DUTY_OFFSET = 0.5;

    /**
     * パルス波生成
     *
     * @param f
     * @param overallLevel
     * @param duty
     * @param reverse
     * @return
     */
    public static byte makePulseWave(double f, int overallLevel, double duty, boolean reverse, boolean lowRes) {
        double ff = (reverse == true) ? (1.0 - trimF(f)) : trimF(f);
        double dutyThresh = (lowRes == true) ? DUTY_THRESHOLD : 0.00;
        byte y = 0;
        /* 矩形波を少しゆがませる */
        if (ff > duty) {
            if (((1.0 - dutyThresh) < ff) || (ff < (duty + dutyThresh))) {
                y = (byte) (overallLevel * DUTY_OFFSET);
            }
            else {
                if (lowRes == true) {
                    y = (byte) (overallLevel - ((ff - (duty + dutyThresh)) * 4.0));
                }
                else {
                    y = (byte) (overallLevel);
                }
            }
        }
        else {
            if (((duty - dutyThresh) < ff) || (ff < (0.0 + dutyThresh))) {
                y = (byte) (-overallLevel * DUTY_OFFSET);
            }
            else {
                if (lowRes == true) {
                    y = (byte) (-(overallLevel - ((ff - dutyThresh) * 4.0)));
                }
                else {
                    y = (byte) (-(overallLevel));
                }
            }
        }
        return y;
    }

    /**
     * のこぎり波生成
     *
     * @param f
     * @param overallLeval
     * @param reverse
     * @return
     */
    public static byte makeSawWave(double f, int overallLeval, boolean reverse) {
        double ff = (reverse == true) ? (1.0 - trimF(f)) : trimF(f);
        return (byte) (((2.0 * ff) * (double)overallLeval) - ((double)overallLeval));
    }

    /**
     * 三角波生成
     *
     * @param f
     * @param overallLevel
     * @param reverse
     * @return
     */
    public static byte makeTriangleWave(double f, int overallLevel, boolean reverse, boolean lowRes) {
        double r = 100.0;
        if (lowRes == true) {
            r = 25.0;
        }
        double ff = (reverse == true) ? (1.0 - trimF(f, r)) : trimF(f, r);

        byte y = 0;
        int slope = (int) ((ff / 0.25) + 1) % 2; // 0=上り, 1=下り
        double quarter = ff % 0.25;
        double sign = (ff < 0.5) ? -1.0 : 1.0;
        if (slope == 0) {
            // 上り
            y = (byte) (((overallLevel - (overallLevel * (quarter / 0.25)))) * sign);
        }
        else {
            // 下り
            y = (byte) ((overallLevel * (quarter / 0.25)) * sign);
        }
        return y;
    }
}
