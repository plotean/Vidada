package vidada.client.viewmodel;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.images.ImageContainer;
import archimedes.core.images.LoadPriority;
import archimedes.core.io.locations.ResourceLocation;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.joda.time.format.DateTimeFormat;
import vidada.client.IVidadaClientManager;
import vidada.client.facade.IThumbContainerService;
import vidada.client.model.browser.BrowserMediaItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.services.IMediaClientService;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.handlers.IMediaHandler;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.MovieMediaItem;
import vidada.model.system.ISystemService;
import vidada.services.IMediaPresenterService;
import vidada.services.ServiceProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple view-model for a media item.
 * 
 * Note: If you need more control and or tag support, use MediaDetailViewModel
 * @author IsNull
 *
 */
public class MediaViewModel extends BrowserItemVM  {

	private final ISystemService systemService = ServiceProvider.Resolve(ISystemService.class);
	private final IMediaPresenterService mediaPresenter = ServiceProvider.Resolve(IMediaPresenterService.class);

	private final IVidadaClientManager clientManager = ServiceProvider.Resolve(IVidadaClientManager.class);
	private final IThumbContainerService thumbService = clientManager.getActive().getThumbConatinerService();
	private final IMediaClientService mediaClientService = clientManager.getActive().getMediaClientService();

	private WeakReference<ImageContainer> imageContainerRef;
	private MediaItem mediaData;


	public MediaViewModel(){
		// Do not call any virtual methods here such as setModel!
	}


	@Override
	public BrowserMediaItem getModel() {
		return (BrowserMediaItem)super.getModel();
	}

	@Override
	public void setModel(IBrowserItem model) {
		if(model == null || model instanceof BrowserMediaItem){
			super.setModel(model);
			mediaData = model != null ? model.getData() : null;
		}else
			throw new NotSupportedException("Parameter model must be of type BrowserMediaItem");
	}


	public void setRating(int selection) {
		if(mediaData != null){
			mediaData.setRating(selection);
			persist();
		}
	}


	public int getRating() {
		if(mediaData != null)
			return mediaData.getRating();
		else
			return 0;
	}

	public String getFilename() {
		if(mediaData != null)
			return mediaData.getFilename();
		else
			return "";
	}


	public void setFileName(String text) {
		if(mediaData != null){
			mediaData.setFilename(text);
			persist();
		}
	}


	public int getOpened() {
		if(mediaData != null)
			return mediaData.getOpened();
		return 0;
	}

	public String getAddedDate() {
		if(mediaData != null)
			return mediaData.getAddedDate().toString(
                    DateTimeFormat
                            .longDate()
                            .withLocale(Locale.GERMANY)
            ); // TODO Localize
		return "";
	}

	public String getTitle() {
		if(mediaData != null)
			return "\"" + getFilename() + "\"";
		return "";
	}


	public String getResolution() {
		if(mediaData != null)
			return getResolutionString(mediaData);
		return "";
	}

	public static String getResolutionString(MediaItem media){
		Size res = media.getResolution();
		return (res != null && !res.isEmpty()) ? res.width + "x" + res.height : "unknown";
	}


    public List<Runnable> getActions(){
        List<Runnable> actions = new ArrayList<Runnable>();
        for(IMediaHandler handler : mediaPresenter.getAllMediaHandlers()){
            actions.add(new MediaHandlerAction(this, handler));
        }

        actions.add(new NamedAction("Copy Link",new Runnable() {
            @Override
            public void run() {
                ResourceLocation mediaResource = getMediaResource();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(mediaResource.getPath());
                clipboard.setContent(content);
            }
        }));

        return actions;
    }


	@Override
	public boolean open(){
		ResourceLocation resourceLocation = getMediaResource();
		if(resourceLocation != null)
			return mediaPresenter.showMedia(mediaData, resourceLocation);
		return false;
	}


	public boolean canOpenFolder() {
		return true;
	}

	/**
	 * Open the containing folder of this media.
	 * (Only possible for local files)
	 */
	public void openContainingFolder(){
		MediaItem media = getModel().getData();
		if(media != null && media.getSource() != null)
			systemService.showResourceHome(media.getSource().getResourceLocation());
	}


	public boolean canNewRandomThumb() {
		return MediaType.MOVIE.equals(getMediaType());
	}

	public void newRandomThumb() {
		newThumbnailAt(MovieMediaItem.randomRelativePos());
	}

	public void newThumbnailAt(float pos) {
		MediaItem media = getModel().getData();
		thumbService.renewThumbImage(media, MovieMediaItem.randomRelativePos());
	}

	public ImageContainer getThumbnail(int widthPxl, int heightPxl){
		ImageContainer imageContainer = null;
		if(mediaData != null){

			imageContainer = thumbService.retrieveThumbnail(
					mediaData,
					new Size(widthPxl, heightPxl));

			imageContainerRef = new WeakReference<ImageContainer>(imageContainer);
		}else{
			imageContainerRef = null;
		}
		return imageContainer;
	}

	/**
	 * Returns a ResourceLocation with points to the raw media data.
	 * Depending on the media origin, this might point to 
	 * 	- a local file
	 *  - a computer in your network
	 *  - any other URI to the web 
	 * @return
	 */
	public ResourceLocation getMediaResource(){
		ResourceLocation resourceLocation = null;
		if(mediaData != null){
			resourceLocation = mediaClientService.openResource(mediaData);
		}
		return resourceLocation;
	}

	public MediaType getMediaType(){
		if(mediaData != null)
			return mediaData.getType();
		return MediaType.NONE;
	}



	/**
	 * Method is called when this media item is outside the view port
	 */
	@Override
	public void outsideViewPort() {

		// We have to notify the image container
		// that the image is no longer required.
		// The idea is that if loading has not yet
		// started, this image can be completely skipped.

		if(imageContainerRef != null){
			ImageContainer container = imageContainerRef.get();
			if(container != null){
				container.setLoadPriority(LoadPriority.Skip);
			}
		}
	}

	protected void persist(){
		if(mediaData != null)
			mediaClientService.update(mediaData);
	}




    private static class NamedAction implements Runnable{

        private final String name;
        private final Runnable action;

        protected NamedAction(String name, Runnable action) {
            this.action = action;
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }

        @Override
        public void run() {
            action.run();
        }
    }


    private static class MediaHandlerAction implements Runnable{

        private final MediaViewModel vm;
        private final IMediaHandler handler;

        public MediaHandlerAction(MediaViewModel vm, IMediaHandler handler){
            this.vm = vm;
            this.handler = handler;
        }

        @Override
        public void run() {
            ResourceLocation resourceLocation = vm.getMediaResource();
            if(resourceLocation != null)
                handler.handle(vm.mediaData, resourceLocation);
        }

        @Override
        public String toString(){
            return "Open with " + handler.getName();
        }
    }


}
