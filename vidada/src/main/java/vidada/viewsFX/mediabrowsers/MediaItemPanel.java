package vidada.viewsFX.mediabrowsers;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import org.controlsfx.control.Rating;

import vidada.model.media.MediaItem;
import vidada.viewsFX.util.AsyncImageProperty;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;

public class MediaItemPanel extends StackPane {

	private final ImageView primaryContent;
	private final AsyncImageProperty imageProperty = new AsyncImageProperty();
	private final Rating rating = new Rating();
	private final Label description = new Label("<no description>");
	private final FadeTransition fadeTransition;

	private Size thumbSize = new Size(200, 140);
	private MediaItem media = null;

	public MediaItemPanel(){ 

		this.setAlignment(Pos.TOP_LEFT);

		HBox itemTopBar = new HBox();
		//description.setScaleX(0.5);
		//description.setScaleY(0.5);

		rating.setScaleX(0.5);
		rating.setScaleY(0.5);
		//itemTopBar.getChildren().add(new Button("H"));
		itemTopBar.getChildren().add(rating);



		//BorderPane.setAlignment(rating, Pos.TOP_RIGHT);


		this.setMinSize(thumbSize.width, thumbSize.height);
		this.setMaxSize(thumbSize.width, thumbSize.height);


		primaryContent =  new ImageView();
		primaryContent.imageProperty().bind(imageProperty);  // bind to the image property so any changes become visible

		// force 1/2 of image size to have crisp retina images
		primaryContent.setFitWidth(thumbSize.width);
		primaryContent.setFitHeight(thumbSize.height);

		fadeTransition = 
				new FadeTransition(Duration.millis(2000), primaryContent);
		fadeTransition.setFromValue(0.0f);
		fadeTransition.setToValue(1.0f);
		fadeTransition.setCycleCount(1);
		fadeTransition.setAutoReverse(false);
		fadeTransition.setCycleCount(1);

		imageProperty.addListener(new ChangeListener<Image>() {

			@Override
			public void changed(ObservableValue<? extends Image> arg0, Image arg1, Image arg2) {	
				fadeTransition.play();
				primaryContent.getParent().requestLayout();
			}
		});

		this.getChildren().add(primaryContent);

		BorderPane pane = new BorderPane();
		pane.setTop(itemTopBar);


		description.setBackground(getBackground());
		//description.setStyle("-fx-background-color: #C0C0C0;");
		description.setStyle("-fx-background-color: #FFFFFF;");
		description.setAlignment(Pos.CENTER_LEFT);
		description.setPadding(new Insets(10));
		pane.setBottom(description);

		this.getChildren().add(pane);


		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent me) {
				if(me.getClickCount() == 2){



					System.out.println("double clicked on " + media);
				}

			}});
	}

	static 	String imageUrl = "file:/Users/IsNull/Pictures/Girls/vidada.thumbs/scaled/400_280/4bc76107e6554df84d1b9c998a3d4efb.png";



	public void setDataContext(MediaItem media){
		this.media = media;
		fadeTransition.stop();
		if(media != null){

			ImageContainer container = media.getThumbnail(thumbSize.scaledCopy(2));

			imageProperty.imageUrlProperty().set(container);
			description.setText(media.getFilename());
			rating.setRating(1.0);

		}else{
			description.setText("");
		}
	}

}