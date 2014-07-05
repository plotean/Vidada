package vidada.viewsFX.images;

import archimedes.core.images.ImageContainer;
import javafx.beans.property.ObjectProperty;

/**
 * A dynamic pane which displays a asynchronous loaded image and uses the available space for displaying.
 * The image is being displayed with a fade-in effect.
 *
 */
public class ImageContainerPane extends ImagePane{

    private final AsyncImageProperty asyncImageProperty = new AsyncImageProperty();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ImageContainerPane
     */
    public ImageContainerPane(){
        imageProperty().bind(asyncImageProperty);
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * The {@link ImageContainer} from which the image is being displayed.
     * @return
     */
    public ObjectProperty<ImageContainer> imageContainerProperty() {
        return asyncImageProperty.imageContainerProperty();
    }

    public void setImageContainer(ImageContainer imageContainer){
        asyncImageProperty.setImageContainer(imageContainer);
    }

    public ImageContainer getImageContainer(ImageContainer imageContainer){
        return asyncImageProperty.getImageContainer();
    }
}
