package vidada.viewmodel;

import org.joda.time.format.DateTimeFormat;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.geometry.Size;
import archimedesJ.images.ImageContainer;

/**
 * A simple view-model for a media item.
 * 
 * Note: If you need more control and or tag support, use MediaDetailViewModel
 * @author IsNull
 *
 */
public class MediaViewModel implements IViewModel<MediaItem> {
	private MediaItem mediaData;
	private boolean isSelected = false;

	public final EventHandlerEx<EventArgs> SelectionChangedEvent = new EventHandlerEx<EventArgs>();

	public MediaViewModel(MediaItem media){
		setModel(media);
	}


	@Override
	public MediaItem getModel() {
		return mediaData;
	}

	@Override
	public void setModel(MediaItem model) {
		mediaData = model;
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
		if(mediaData != null)
			return mediaData.getThumbnail(new Size(widthPxl, heightPxl));
		return null;
	}

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


	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		if(this.isSelected != isSelected){
			this.isSelected = isSelected;
			SelectionChangedEvent.fireEvent(this, EventArgs.Empty);
		}
	}


}
