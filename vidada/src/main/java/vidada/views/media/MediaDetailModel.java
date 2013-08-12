package vidada.views.media;

import org.joda.time.format.DateTimeFormat;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.geometry.Size;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;

public class MediaDetailModel implements IMediaDetailModel {

	private transient final MediaItem mediaData;
	private transient final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private transient final EventHandlerEx<EventArgs> TagsChanged = new EventHandlerEx<EventArgs>();

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#getTagsChanged()
	 */
	@Override
	public IEvent<EventArgs> getTagsChanged(){ return TagsChanged; }


	/**
	 * Creates a new media detail model form the given MediaData
	 * @param mediaData
	 */
	public MediaDetailModel(MediaItem mediaData){

		assert mediaData != null;
		this.mediaData = mediaData;

		this.mediaData.getTagAddedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				TagsChanged.fireEvent(sender, eventArgs);
			}
		});

		this.mediaData.getTagRemovedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				TagsChanged.fireEvent(sender, eventArgs);
			}
		});
	}



	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#getState(vidada.model.tags.Tag)
	 */
	@Override
	public MultiCheckState getState(Tag tag){
		return this.mediaData.hasTag(tag) ? MultiCheckState.Checked : MultiCheckState.Unchecked;
	}

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#setState(vidada.model.tags.Tag, archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState)
	 */
	@Override
	public void setState(Tag tag, MultiCheckState newState){

		switch (newState) {
		case Checked:
			this.mediaData.addTag(tag);
			break;

		case Unchecked:
			this.mediaData.removeTag(tag);
			break;

		default:
			throw new NotSupportedException("Can not set state " + newState + " to a MediaData!");
		}

		System.out.println("MediaDetailModel.setState( "+ tag + ","  + newState + " )result:-->" + this.mediaData.getTagsString());
	}


	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#setRating(int)
	 */
	@Override
	public void setRating(int selection) {
		mediaData.setRating(selection);
	}

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#getRating()
	 */
	@Override
	public int getRating() {
		return mediaData.getRating();
	}

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#getFilename()
	 */
	@Override
	public String getFilename() {
		return mediaData.getFilename();
	}

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#setFileName(java.lang.String)
	 */
	@Override
	public void setFileName(String text) {
		mediaData.setFilename(text);
	}

	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#getOpened()
	 */
	@Override
	public int getOpened() {
		return mediaData.getOpened();
	}

	@Override
	public String getAddedDate() {
		return mediaData.getAddedDate().toString(DateTimeFormat.shortDateTime());
	}



	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#persist()
	 */
	@Override
	public void persist(){
		mediaService.update(mediaData);
	}


	@Override
	public String getTitle() {
		return "\"" + getFilename() + "\"";
	}

	@Override
	public String getResolution() {
		return getResolutionString(mediaData);
	}

	public static String getResolutionString(MediaItem media){
		Size res = media.getResolution();
		return res != null ? res.width + "x" + res.height : "unknown"; 
	}




}
