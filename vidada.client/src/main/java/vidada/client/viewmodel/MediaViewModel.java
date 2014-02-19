package vidada.client.viewmodel;

import java.lang.ref.WeakReference;

import org.joda.time.format.DateTimeFormat;

import vidada.client.VidadaClientManager;
import vidada.client.model.browser.BrowserMediaItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.services.IMediaClientService;
import vidada.client.services.IThumbnailClientService;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.source.MediaSource;
import vidada.model.system.ISystemService;
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
	private final IThumbnailClientService thumbService = VidadaClientManager.instance().getThumbnailClientService();
	private final IMediaClientService mediaClientService = VidadaClientManager.instance().getMediaClientService();

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


	@Override
	public boolean open(){
		if(mediaData != null){
			MediaSource source = mediaData.getSource();
			if(source != null){
				ResourceLocation resource = source.getResourceLocation();
				if(systemService.open(resource)){
					mediaData.setOpened(mediaData.getOpened() + 1);
					persist();
				}
			}
		}
		return false;
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

		// we have to notify the image container
		// that the image is no longer required

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
