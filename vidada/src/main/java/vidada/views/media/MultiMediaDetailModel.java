package vidada.views.media;

import java.util.List;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import archimedesJ.events.EventAggregate;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;
import archimedesJ.util.Lists;

/**
 * 
 * @author IsNull
 *
 */
public class MultiMediaDetailModel implements IMediaDetailModel{

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
	private final List<MediaItem> modelMedias;


	private final EventAggregate<EventArgsG<Tag>> TagsChanged = new EventAggregate<EventArgsG<Tag>>();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IEvent<EventArgs> getTagsChanged() { return (IEvent)TagsChanged; }


	/**
	 * 
	 * @param selectedMedias
	 */
	public MultiMediaDetailModel(List<MediaItem> selectedMedias) {
		modelMedias = Lists.newList(selectedMedias);

		// redirect events to the tags changed event
		for (MediaItem mediaData : modelMedias) {
			TagsChanged.registerEvent(mediaData.getTagAddedEvent());
			TagsChanged.registerEvent(mediaData.getTagRemovedEvent());
		}
	}


	/**
	 * Gets the state for the given tag
	 * 
	 * The state is checked if all media data have the given Tag
	 * The state is unchecked if all media data don't have the Tag
	 * The state is Indeterminate if the media data are not consistent
	 */
	@Override
	public MultiCheckState getState(Tag tag) {

		MultiCheckState currentState = MultiCheckState.Indeterminate;

		for (MediaItem media : modelMedias) {
			if(media.getTags().contains(tag)){
				if(currentState == MultiCheckState.Unchecked)
				{
					currentState = MultiCheckState.Indeterminate;
					break; 
				}else{
					currentState = MultiCheckState.Checked;
				}
			}else{
				if(currentState == MultiCheckState.Checked)
				{
					currentState = MultiCheckState.Indeterminate;
					break; 
				}else{
					currentState = MultiCheckState.Unchecked;
				}
			}
		}
		return currentState;
	}


	@Override
	public void setState(Tag tag, MultiCheckState newState) {

		if(newState == MultiCheckState.Checked || newState == MultiCheckState.Unchecked)
		{
			for (MediaItem media : modelMedias) {
				if(newState == MultiCheckState.Checked)
				{
					media.addTag(tag);
				}else{
					media.removeTag(tag);
				}
			}
		}
	}

	@Override
	public void setRating(int selection) {
		for (MediaItem media : modelMedias) {
			media.setRating(selection);
		}
	}

	@Override
	public int getRating() {
		int rating = -1;
		for (MediaItem media : modelMedias) {
			rating = media.getRating();
			break;
		}
		return rating;
	}

	@Override
	public String getFilename() {
		return "<multiple>";
	}

	@Override
	public void setFileName(String text) {
		//Igonre - we do not set multiple names
	}

	@Override
	public int getOpened() {
		return -1;
	}

	@Override
	public String getResolution() {
		if(modelMedias.size() == 1)
			return MediaDetailModel.getResolutionString(modelMedias.get(0));
		else return "unknown";
	}


	@Override
	public void persist() {
		for (MediaItem media : modelMedias) {
			mediaService.update(media);
		}
	}


	@Override
	public String getTitle() {
		return "(" + modelMedias.size() + ")";
	}


	@Override
	public String getAddedDate() {
		return "-";
	}




}
