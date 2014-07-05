package vidada.viewsFX.images;

import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;


/**
 * A dynamic pane which displays a image and uses the available space for displaying.
 * The image is being displayed with a fade-in effect.
 * 
 * Note: This is a little hack to workaround an Fx issue
 * which is caused by ImageViews and their enforcement to their parent size.
 * 
 * @author IsNull
 * @see https://gist.github.com/jewelsea/1680197
 */
public class ImagePane extends BorderPane {

	private final FadeTransition fadeTransition;
	private final ImageView imageView;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new ImagePane
	 */
	public ImagePane(){

		imageView = new ImageView();
		//imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.fitWidthProperty().bind(widthProperty());
		imageView.fitHeightProperty().bind(heightProperty());

		fadeTransition = 
				new FadeTransition(Duration.millis(2000), imageView);
		fadeTransition.setFromValue(0.0f);
		fadeTransition.setToValue(1.0f);
		fadeTransition.setCycleCount(1);
		fadeTransition.setAutoReverse(false);
		fadeTransition.setCycleCount(1);

        setCenter(imageView);

        imageProperty().addListener(imageChangeListener);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the image property of the displayed image
     * @return
     */
    public ObjectProperty<Image> imageProperty(){
        return imageView.imageProperty();
    }

    /**
     * Set the displayed image
     * @param image
     */
    public void setImage(Image image){
        imageView.setImage(image);
    }

    /**
     * Sets the fade duration in milliseconds
     * @param durationMillis
     */
	public void setFadeDuration(long durationMillis){
		if(fadeTransition != null)
			fadeTransition.setDuration(Duration.millis(durationMillis));
	}

    /**
     * Sets if the image shall preserve its aspect-ratio
     * @param preserveRatio
     */
	public void setPreserveRatio(boolean preserveRatio){
		imageView.setPreserveRatio(preserveRatio);
	}

    /**
     * Gets if the image shall preserve its aspect-ratio
     * @return
     */
	public boolean isPreserveRatio(){
		return imageView.isPreserveRatio();
	}


    /**
     * Stops the fade-in animation
     */
	public void stopAnimation(){
		if(fadeTransition != null) 
			fadeTransition.stop();
	}

    /***************************************************************************
     *                                                                         *
     * Private  methods                                                        *
     *                                                                         *
     **************************************************************************/

    /**
     * Occurs when the
     */
	transient private final ChangeListener<Image> imageChangeListener = new ChangeListener<Image>() {
		@Override
		public void changed(ObservableValue<? extends Image> value, Image oldImage, Image newImage ) {	
			if(fadeTransition != null) 
				fadeTransition.play();
		}
	};

}
