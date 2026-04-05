package jmp.skin;

import java.awt.Image;

public class ImageResource extends Resource {

    private Image image = null;

    public ImageResource() {
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image img) {
        image = img;
    }

}
