package vidada.viewmodel;

import java.lang.ref.WeakReference;

import org.joda.time.format.DateTimeFormat;

import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.viewmodel.browser.BrowserItemVM;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;
import archimedesJ.images.LoadPriority;

/**
 * A simple view-model for a media item.
 * 
 * Note: If you need more control and or tag support, use MediaDetailViewModel
 * @author IsNull
 *
 */
public class MediaViewModel extends BrowserItemVM {
	private WeakReference<ImageContainer> imageContainerRef;
	private MediaItem mediaData;


	public MediaViewModel(BrowserMediaItem mediaItem){
		setModel(mediaItem);
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
		if(mediaData != null)
			mediaData.setRating(selection);
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
		if(mediaData != null)
			mediaData.setFilename(text);
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
			imageContainer = mediaData.getThumbnail(new Size(widthPxl, heightPxl));
			imageContainerRef = new WeakReference<ImageContainer>(imageContainer);
		}else{
			imageContainerRef = null;
		}
		return imageContainer;
	}


	@Override
	public boolean open(){
		if(mediaData != null)
			return mediaData.open();
		return false;
	}

	public MediaType getMediaType(){
		if(mediaData != null)
			return mediaData.getMediaType();
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


}
