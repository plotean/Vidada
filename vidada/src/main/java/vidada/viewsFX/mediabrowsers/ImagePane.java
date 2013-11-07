package vidada.viewsFX.mediabrowsers;

import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * A dynamic pane which displays a image and uses the available space for displaying.
 * 
 * Note: This is a little hack to workaround an Fx issue
 * which is caused by ImageViews and their enforcement to their parent size.
 * 
 * @author IsNull
 * @see https://gist.github.com/jewelsea/1680197
 */
public class ImagePane extends ScrollPane {

	private final FadeTransition fadeTransition;
	private final ImageView imageView; 

	/**
	 * 
	 * @param imageProperty
	 */
	public ImagePane(ObjectProperty<Image> imageProperty){
		setHbarPolicy(ScrollBarPolicy.NEVER);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		setPannable(false);


		imageView = new ImageView();
		imageView.imageProperty().bind(imageProperty);
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

		setContent(imageView);


		imageProperty.addListener(imageChangeListener);
	}

	private ImageView getImageView(){
		return imageView;
	}


	public void stopAnimation(){
		if(fadeTransition != null) 
			fadeTransition.stop();
	}

	transient private final ChangeListener<Image> imageChangeListener = new ChangeListener<Image>() {
		@Override
		public void changed(ObservableValue<? extends Image> value, Image oldImage, Image newImage ) {	
			if(fadeTransition != null) 
				fadeTransition.play();
		}
	};

}
