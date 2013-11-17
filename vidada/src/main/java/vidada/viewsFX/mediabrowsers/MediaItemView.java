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
import vidada.model.media.MediaType;
import vidada.viewmodel.MediaViewModel;
import vidada.viewsFX.player.IMediaPlayerService;
import vidada.viewsFX.player.IMediaPlayerService.IMediaPlayerComponent;
import vidada.viewsFX.player.MediaPlayerFx;
import vidada.viewsFX.util.AsyncImageProperty;
import vlcj.fx.IMediaPlayerBehavior;
import vlcj.fx.MediaPlayerSeekBehaviour;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
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
	private MediaViewModel media = null;


	public MediaItemView(IMediaPlayerService mediaPlayerService){ 

		this.mediaPlayerService = mediaPlayerService;

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
		imagePane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedSelectionHandler);


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
					onMediaInspectAction((float)(me.getX() / MediaItemView.this.getWidth()));
				}
			}
		}
	};

	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseClickedSelectionHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				if(me.getClickCount() == 1){
					media.setSelected(true);
				}
			}
		}
	};

	transient private final IImageViewerService imageViewer = ServiceProvider.Resolve(IImageViewerService.class);
	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	/**
	 * 
	 */
	private void onMediaInspectAction(float relativePos){

		System.out.println("inspect media: " + media.getMediaType());
		if(media.getMediaType().equals(MediaType.MOVIE))
		{
			if(mediaPlayerService.isMediaPlayerAvaiable()){

				System.out.println("MediaView: Starting directplay:");

				MediaPlayerFx playerView = addMediaPlayer();

				// start playing and set initial position relative
				playerView.getMediaController().playMedia(media.getModel().getSource().getPath());
				playerView.getMediaController().setPosition(relativePos);

			}else {
				System.err.println("No MediaPlayer is avaible!");
			}
		}
	}

	private final static IMediaPlayerBehavior playerBehavior = new MediaPlayerSeekBehaviour();

	private MediaPlayerFx addMediaPlayer(){
		player = mediaPlayerService.resolveMediaPlayer();
		player.getRequestReleaseEvent().add(playerReleaseListener);

		MediaPlayerFx playerView = player.getSharedPlayer();
		playerView.addBehavior(playerBehavior);

		primaryContent.getChildren().add(playerView);

		playerView.setWidth(primaryContent.getWidth());
		playerView.setHeight(primaryContent.getHeight());


		playerView.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedListener);
		return playerView;
	}

	private void removeMediaPlayer(){
		MediaPlayerFx playerView = player.getSharedPlayer();
		playerView.getMediaController().stop();
		player.getRequestReleaseEvent().remove(playerReleaseListener);
		primaryContent.getChildren().remove(playerView);
		playerView.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedListener);
		player = null;
	}

	private final EventListenerEx<EventArgs> playerReleaseListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			removeMediaPlayer();
		}
	};

	private final EventHandler<MouseEvent> mouseExitedListener = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent me) {
			removeMediaPlayer();
		}
	};


	/**
	 * Occurs when a media is opened
	 */
	private void onMediaOpenAction(){
		if(media.getMediaType().equals(MediaType.IMAGE)){
			//
			// In case it is an image, show it in internal preview
			//
			ISmartImage smartImage;
			try {
				smartImage = new SmartImageLazy(imageFactory, new URI(media.getModel().getSource().getPath()));
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
								" could not be opened!" + FileSupport.NEWLINE + media.getModel().getSource())
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

		//System.out.println("Image Size" + desiredWidth + " x " + desiredHeight);
	}

	private static double dpiMultiplier = 2.0;

	private void requestThumb(int width, int height){
		ImageContainer container = media.getThumbnail(
				(int)(width*dpiMultiplier),
				(int)(height*dpiMultiplier));
		imageProperty.imageUrlProperty().set(container);
	}

	public void setDataContext(MediaViewModel media){
		this.media = media;
		imagePane.stopAnimation();
		if(media != null){
			description.setText(media.getFilename());
			rating.setRating(media.getRating());
			onItemImageChanged();
		}else{
			description.setText("");
		}
	}

}