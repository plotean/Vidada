package vidada.viewsFX.mediabrowsers;

import java.io.File;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.controlsfx.control.Rating;

import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.views.mediabrowsers.imageviewer.ImageViewerDialog;
import vidada.viewsFX.util.AsyncImageProperty;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.swing.components.imageviewer.FileSmartImage;
import archimedesJ.swing.components.imageviewer.StaticImageProvider;
import archimedesJ.util.FileSupport;

/**
 * View of a single media item in the MediaBrowser
 * @author IsNull
 *
 */
public class MediaItemView extends BorderPane {

	private ImageView thumb;


	final StackPane primaryContent = new StackPane();
	private final AsyncImageProperty imageProperty = new AsyncImageProperty();
	private final ImagePane imagePane = new ImagePane(imageProperty);
	private final Rating rating = new Rating();
	private final Label description = new Label("<no description>");




	private Size thumbSize = new Size(200, 140);
	private MediaItem media = null;
	private final DoubleProperty desiredWidth;
	private final DoubleProperty desiredHeight;


	public MediaItemView(DoubleProperty desiredWidth, DoubleProperty desiredHeight){ 

		setId("media-cell");
		this.desiredWidth = desiredWidth;
		this.desiredHeight = desiredHeight;

		this.prefWidthProperty().bind(desiredWidth);

		this.prefHeightProperty().bind(desiredHeight);
		this.maxWidthProperty().bind(desiredWidth);
		this.maxHeightProperty().bind(desiredHeight);
		this.minWidthProperty().bind(desiredWidth);
		this.minHeightProperty().bind(desiredHeight);

		// TODO: Refactor this fix to render crisp for retina display
		rating.setScaleX(0.5);
		rating.setScaleY(0.5);


		primaryContent.setAlignment(Pos.TOP_LEFT);
		primaryContent.getChildren().add(imagePane);
		// primaryContent.getChildren().add(rating);  // some crazy bug with rating rendering in stackpane!

		description.setId("description"); // style id
		description.setAlignment(Pos.CENTER_LEFT);
		description.setPadding(new Insets(10));

		this.setCenter(primaryContent);
		this.setBottom(description);

		// event listener
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
	}


	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseClickedHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getClickCount() == 2){
				onMediaDefaultAction();
			}
		}
	};


	private void onMediaDefaultAction(){
		if(media instanceof ImageMediaItem){

			JDialog viewerDialog = new ImageViewerDialog(new StaticImageProvider(new FileSmartImage(new File(media.getSource().getPath()))));
			viewerDialog.setVisible(true);
		}else{
			// default OS action
			if(!media.open()){
				JOptionPane.showMessageDialog(null, "The file " + media.getTitle() + 
						" could not be opened!" + FileSupport.NEWLINE + media.getSource() ,
						"File not found!",JOptionPane.ERROR_MESSAGE );
			}
		}
	}



	public void setDataContext(MediaItem media){
		this.media = media;
		imagePane.stopAnimation();
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