package jmp.skin;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import function.Utility;

public class ResourceUtility {

    public static URL getResource(Class<?> c, String name) {
        return c.getClassLoader().getResource(name);
    }

    public static Image readImage(String path, boolean toTransparencyBlack, Color transColor) {
        Image img = null;
        try {
            File file = new File(path);
            img = ImageIO.read(file);

            if (toTransparencyBlack == true) {
                int w = img.getWidth(null);
                int h = img.getHeight(null);
                BufferedImage bi = Utility.imageToBufferedImage(img, w, h);
                img = Utility.transformImageToTransparency(bi, transColor);
            }
        }
        catch (Exception e) {
            img = null;
            System.out.println("null");
        }
        return img;
    }

}
