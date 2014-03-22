package vidada.viewsFX.mediabrowsers;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.viewer.IImageViewerService;
import archimedesJ.images.viewer.ISmartImage;
import archimedesJ.images.viewer.SmartImageLazy;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.FileSupport;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.Rating;
import org.controlsfx.dialog.Dialogs;
import vidada.client.viewmodel.MediaViewModel;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.media.MediaType;
import vidada.services.ServiceProvider;
import vidada.viewsFX.player.IMediaPlayerBehavior;
import vidada.viewsFX.player.IMediaPlayerService;
import vidada.viewsFX.player.IMediaPlayerService.IMediaPlayerComponent;
import vidada.viewsFX.player.MediaPlayerFx;
import vidada.viewsFX.player.MediaPlayerSeekBehaviour;
import vidada.viewsFX.util.AsyncImageProperty;

/**
 * View of a single media item in the MediaBrowser
 * @author IsNull
 *
 */
public class MediaItemView extends BrowserCellView {

	private final IMediaPlayerService mediaPlayerService;

	private final StackPane primaryContent = new StackPane();
	private final AsyncImageProperty imageProperty = new AsyncImageProperty();
	private final ImagePane imagePane = new ImagePane(imageProperty);
	private final Rating rating = new Rating();
	private final Label description = new Label("<no description>");

	private final Button openButton = new Button();

	private static double dpiMultiplier = 2.0;
	private IMediaPlayerComponent player = null;

	// desired width of the thumb image panel
	// this is calculated dynamically
	private double oldThumbWidth=0;
	private double oldThumbHeight=0;

	// Current DataContext
	private MediaViewModel mediaViewModel = null;


	public MediaItemView(IMediaPlayerService mediaPlayerService){ 

		this.mediaPlayerService = mediaPlayerService;

		rating.setScaleX(1/dpiMultiplier);
		rating.setScaleY(1/dpiMultiplier);

		openButton.setText("open");


		imagePane.setFadeDuration(600);

		primaryContent.setAlignment(Pos.TOP_LEFT);
		primaryContent.getChildren().add(imagePane);

		StackPane.setMargin(openButton, new Insets(10));
		StackPane.setAlignment(openButton, Pos.CENTER_RIGHT);
		primaryContent.getChildren().add(openButton);

		// primaryContent.getChildren().add(rating);  // some crazy bug with rating rendering in stackpane!

		description.setId("description"); // style id
		description.setAlignment(Pos.CENTER_LEFT);
		description.setPadding(new Insets(10));


		this.setCenter(primaryContent);
		this.setBottom(description);

		// event listener
		imagePane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseInspectHandler);
		openButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseOpenHandler);


		imagePane.widthProperty().addListener((arg0, old, newWidth) -> onItemImageChanged(false));
		imagePane.heightProperty().addListener((arg0, old, newHeight) -> onItemImageChanged(false));
	}

	@Override
	protected ContextMenu createContextMenu(){

		final ContextMenu cm = new ContextMenu();

		if(mediaViewModel.canOpenFolder()){
			MenuItem openContainingFolder = new MenuItem("Open Folder");
			openContainingFolder.setOnAction(e -> {
                mediaViewModel.openContainingFolder();
            });
			cm.getItems().add(openContainingFolder);
		}


		if(mediaViewModel.canNewRandomThumb()){
			MenuItem newRandomThumb = new MenuItem("Renew Thumb");
			newRandomThumb.setOnAction(e -> {
                mediaViewModel.newRandomThumb();
            });
			cm.getItems().add(newRandomThumb);
		}

		return cm;
	}

	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseOpenHandler = me -> {
        if(me.getButton().equals(MouseButton.PRIMARY)){
            onMediaOpenAction();
        }
    };

	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mousePrevClickedHandler = me -> {
        if(me.getButton().equals(MouseButton.PRIMARY)){
            if(me.getClickCount() > 1){
                onMediaOpenAction();
            }
        }
    };



	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseInspectHandler = me -> {
        if(me.isPrimaryButtonDown() && !me.isSecondaryButtonDown())
            onMediaInspectAction((float)(me.getX() / MediaItemView.this.getWidth()));
    };


	transient private final IImageViewerService imageViewer = ServiceProvider.Resolve(IImageViewerService.class);
	transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	/**
	 * 
	 */
	private void onMediaInspectAction(float relativePos){

		System.out.println("inspect media: " + mediaViewModel.getMediaType());
		if(mediaViewModel.getMediaType().equals(MediaType.MOVIE))
		{
			if(mediaPlayerService.isMediaPlayerAvaiable()){

				System.out.println("MediaView: Starting directplay:");

				MediaPlayerFx playerView = addMediaPlayer();

				// start playing and set initial position relative
				ResourceLocation mediaResource = mediaViewModel.getMediaResource();
				String mediaUri = mediaResource.getUriString();

				playerView.getMediaController().playMedia(mediaUri);
				playerView.getMediaController().setPosition(relativePos);

			}else {
				System.err.println("No MediaPlayer is avaible!");
			}
		}
	}

	private final static IMediaPlayerBehavior playerBehavior = new MediaPlayerSeekBehaviour();

	private MediaPlayerFx addMediaPlayer(){
		System.out.println("MediaItemView: adding MediaPlayer");

		player = mediaPlayerService.resolveMediaPlayer();
		player.getRequestReleaseEvent().add(playerReleaseListener);

		MediaPlayerFx playerView = player.getSharedPlayer();
		playerView.addBehavior(playerBehavior);

		primaryContent.getChildren().add(playerView);

		playerView.setWidth(primaryContent.getWidth());
		playerView.setHeight(primaryContent.getHeight());


		playerView.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedListener);
		playerView.addEventHandler(MouseEvent.MOUSE_CLICKED, mousePrevClickedHandler);
		playerView.addEventHandler(KeyEvent.KEY_PRESSED, playerKeyPressListener);

		return playerView;
	}

	private synchronized void removeMediaPlayer(){

		if(player != null){

			System.out.println("MediaItemView: removing MediaPlayer");

			MediaPlayerFx playerView = player.getSharedPlayer();
			playerView.getMediaController().stop();
			player.getRequestReleaseEvent().remove(playerReleaseListener);
			primaryContent.getChildren().remove(playerView);

			playerView.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedListener);
			playerView.removeEventHandler(MouseEvent.MOUSE_CLICKED, mousePrevClickedHandler);
			playerView.removeEventHandler(KeyEvent.KEY_PRESSED, playerKeyPressListener);

			player = null;
		}
	}

	private final EventListenerEx<EventArgs> playerReleaseListener = (sender, eventArgs) -> {
        removeMediaPlayer();
    };

	private final EventHandler<MouseEvent> mouseExitedListener = me -> {
        removeMediaPlayer();
    };

	private final EventHandler<KeyEvent> playerKeyPressListener = ke -> {
        System.out.println("keeeyy on player :D");
    };


	/**
	 * Occurs when a media is opened
	 */
	private void onMediaOpenAction(){
		if(mediaViewModel.getMediaType().equals(MediaType.IMAGE)){
			//
			// In case it is an image, show it in internal preview
			//
			ISmartImage smartImage;
			ResourceLocation resource = mediaViewModel.getMediaResource();
			smartImage = new SmartImageLazy(imageFactory, resource.getUri());
			imageViewer.showImage(smartImage);

		}else{
			if(!mediaViewModel.open()){
				Dialogs.create()
				.owner(null)
				.title("Can not open Media")
				.masthead("No media source found!")
				.message("The file " + mediaViewModel.getTitle() + 
						" could not be opened!" +
						FileSupport.NEWLINE + mediaViewModel.getModel().getData().getSource())
						.showWarning();
			}
		}
	}

	private void onItemImageChanged(boolean force){
		int desiredWidth = (int)imagePane.getWidth(); 
		int desiredHeight = (int)imagePane.getHeight();

		if(desiredWidth > 0 && desiredHeight > 0){

			// any change to previous setting?
			if(force || (desiredWidth != oldThumbWidth || desiredHeight != oldThumbHeight))
			{
				requestThumb(desiredWidth, desiredHeight);

				oldThumbWidth = desiredWidth;
				oldThumbHeight = desiredHeight;
			}
		}
	}

	private void requestThumb(final int width, final int height){
		if(mediaViewModel != null)
		{
			ImageContainer container = mediaViewModel.getThumbnail(
					(int)(width*dpiMultiplier),
					(int)(height*dpiMultiplier));

			imageProperty.imageContainerProperty().set(container);
		}
	}


	@Override
	public void setDataContext(BrowserItemVM viewmodel) {
		super.setDataContext(viewmodel);

		if(viewmodel == null || viewmodel instanceof MediaViewModel){
			this.mediaViewModel = (MediaViewModel)viewmodel;
			imagePane.stopAnimation();

			if(mediaViewModel != null){
				description.setText(mediaViewModel.getFilename());
				rating.setRating(mediaViewModel.getRating());
			}else{
				description.setText("<null>");
			}
			onItemImageChanged(true);
		}else
			throw new NotSupportedException("MediaItemView requires a datacontext of type MediaViewModel!");
	}

}