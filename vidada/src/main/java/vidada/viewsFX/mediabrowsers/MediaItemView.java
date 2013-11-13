package vidada.viewsFX.mediabrowsers;

import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import org.controlsfx.control.Rating;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.viewsFX.player.IMediaPlayerService;
import vidada.viewsFX.player.IMediaPlayerService.IMediaPlayerComponent;
import vidada.viewsFX.player.MediaPlayerFx;
import vidada.viewsFX.util.AsyncImageProperty;
import vlcj.fx.MediaPlayerSeekBehaviour;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.geometry.Size;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.viewer.IImageViewerService;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.images.viewer.SmartImageLazy;
import archimedesJ.util.FileSupport;

/**
 * View of a single media item in the MediaBrowser
 * @author IsNull
 *
 */
public class MediaItemView extends BorderPane {

	private final IMediaPlayerService mediaPlayerService;

	private final StackPane primaryContent = new StackPane();
	private final AsyncImageProperty imageProperty = new AsyncImageProperty();
	private final ImagePane imagePane = new ImagePane(imageProperty);
	private final Rating rating = new Rating();
	private final Label description = new Label("<no description>");

	private IMediaPlayerComponent player = null;
	private ImageView thumb;
	// desired width of the thumb image panel
	// this is calculated dynamically
	private double oldThumbWidth=0;
	private double oldThumbHeight=0;

	// Current DataContext
	private MediaItem media = null;


	public MediaItemView(IMediaPlayerService mediaPlayerService){ 

		this.mediaPlayerService = mediaPlayerService;

		setId("media-cell");

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

		imagePane.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number old, Number newWidth) {
				onItemImageChanged();
			}
		});
		imagePane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number old, Number newHeight) {
				onItemImageChanged();
			}
		});

	}


	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseClickedHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				if(me.getClickCount() == 2){
					onMediaOpenAction();
				}else if(me.getClickCount() == 1){
					onMediaInspectAction();
				}
			}
		}
	};

	transient private final IImageViewerService imageViewer = ServiceProvider.Resolve(IImageViewerService.class);
	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	/**
	 * 
	 */
	private void onMediaInspectAction(){

		/*
		Action response = Dialogs.create()
				.owner(null)
				.title("Test")
				.masthead("On Media Inspect Action!")
				.message("lets preview this")
				.showWarning();*/
		if(mediaPlayerService.isMediaPlayerAvaiable()){
			addMediaPlayer();
		}else {
			System.err.println("No MediaPlayer is avaible!");
		}

	}



	private void addMediaPlayer(){
		player = mediaPlayerService.resolveMediaPlayer();
		player.getRequestReleaseEvent().add(playerReleaseListener);

		MediaPlayerFx playerView = player.getSharedPlayer();
		playerView.addBehavior(MediaPlayerSeekBehaviour.Instance);

		primaryContent.getChildren().add(playerView);

		playerView.setWidth(primaryContent.getWidth());
		playerView.setHeight(primaryContent.getHeight());
		playerView.getMediaController().playMedia(media.getSource().getPath());
	}

	private void removeMediaPlayer(){
		player.getSharedPlayer().getMediaController().stop();
		player.getRequestReleaseEvent().remove(playerReleaseListener);
		primaryContent.getChildren().remove(player.getSharedPlayer());
		player = null;
	}

	private final EventListenerEx<EventArgs> playerReleaseListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			removeMediaPlayer();
		}
	};


	/**
	 * Occurs when a media is opened
	 */
	private void onMediaOpenAction(){
		if(media instanceof ImageMediaItem){
			//
			// In case it is an image, show it in internal preview
			//
			ISmartImage smartImage;
			try {
				smartImage = new SmartImageLazy(imageFactory, new URI(media.getSource().getPath()));
				imageViewer.showImage(smartImage);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}else{
			if(!media.open()){
				Action response = Dialogs.create()
						.owner(null)
						.title("Can not open Media")
						.masthead("No media source found!")
						.message("The file " + media.getTitle() + 
								" could not be opened!" + FileSupport.NEWLINE + media.getSource())
								.showWarning();
			}
		}
	}

	private void onItemImageChanged(){
		int desiredWidth = (int)imagePane.getWidth(); 
		int desiredHeight = (int)imagePane.getHeight();

		if(desiredWidth > 0 && desiredHeight > 0){

			// any change to previous setting?
			if(desiredWidth != oldThumbWidth || desiredHeight != oldThumbHeight)
			{
				requestThumb(desiredWidth, desiredHeight);

				oldThumbWidth = desiredWidth;
				oldThumbHeight = desiredHeight;
			}
		}

		System.out.println("Image Size" + desiredWidth + " x " + desiredHeight);
	}

	private static double dpiMultiplier = 2.0;

	private void requestThumb(int width, int height){
		ImageContainer container = media.getThumbnail(
				new Size((int)(width*dpiMultiplier), (int)(height*dpiMultiplier))
				);
		imageProperty.imageUrlProperty().set(container);
	}

	public void setDataContext(MediaItem media){
		this.media = media;
		imagePane.stopAnimation();
		if(media != null){
			description.setText(media.getFilename());
			rating.setRating(1.0);
			onItemImageChanged();
		}else{
			description.setText("");
		}
	}

}