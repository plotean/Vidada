package vidada.client.viewmodel;

import java.lang.ref.WeakReference;

import org.joda.time.format.DateTimeFormat;

import vidada.client.IVidadaClientManager;
import vidada.client.facade.IThumbConatinerService;
import vidada.client.model.browser.BrowserMediaItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.services.IMediaClientService;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.MovieMediaItem;
import vidada.model.system.ISystemService;
import vidada.services.IMediaPresenterService;
import vidada.services.ServiceProvider;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.LoadPriority;
import archimedesJ.io.locations.ResourceLocation;

/**
 * A simple view-model for a media item.
 * 
 * Note: If you need more control and or tag support, use MediaDetailViewModel
 * @author IsNull
 *
 */
public class MediaViewModel extends BrowserItemVM {

	private final ISystemService systemService = ServiceProvider.Resolve(ISystemService.class);
	private final IMediaPresenterService mediaPresenter = ServiceProvider.Resolve(IMediaPresenterService.class);

	private final IVidadaClientManager clientManager = ServiceProvider.Resolve(IVidadaClientManager.class);
	private final IThumbConatinerService thumbService = clientManager.getActive().getThumbConatinerService();
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
			return mediaData.getAddedDate().toString(DateTimeFormat.shortDateTime());
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
		return res != null ? res.width + "x" + res.height : "unknown"; 
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


}
