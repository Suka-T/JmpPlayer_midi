package drawLib.gtoolkit;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class DrawUtil {

    public static void affineTransToRotation(Graphics2D g2d, double radian, double anchorX, double anchorY) {
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.toRadians(radian), anchorX, anchorY);
        g2d.setTransform(at);
    }

}
