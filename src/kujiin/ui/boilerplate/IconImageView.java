package kujiin.ui.boilerplate;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconImageView extends ImageView {

    public IconImageView(Image image, double fitheight) {
        super(image);
        setFitHeight(fitheight);
        setPreserveRatio(true);
    }
}
