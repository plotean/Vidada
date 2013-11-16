package vidada.viewmodel.media;

import java.util.List;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.ITagStatesVMProvider;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.util.Lists;

/**
 * Media Detail Adapter for multiple medias
 * @author IsNull
 *
 */
public class MultiMediaDetailModel implements IMediaViewModel{

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
	private final List<MediaItem> modelMedias;



	/**
	 * 
	 * @param selectedMedias
	 */
	public MultiMediaDetailModel(List<MediaItem> selectedMedias) {
		modelMedias = Lists.newList(selectedMedias);

		// redirect events to the tags changed event
		for (MediaItem mediaData : modelMedias) {
			//TagsChanged.registerEvent(mediaData.getTagAddedEvent());
			//TagsChanged.registerEvent(mediaData.getTagRemovedEvent());
		}
	}

	@Override
	public ITagStatesVMProvider getTagsVM() {
		throw new NotImplementedException("getTagsVM");
	}


	/**
	 * Gets the state for the given tag
	 * 
	 * The state is checked if all media data have the given Tag
	 * The state is unchecked if all media data don't have the Tag
	 * The state is Indeterminate if the media data are not consistent
	 */
	public TagState getTagState(Tag tag) {

		TagState currentState = TagState.Indeterminate;

		for (MediaItem media : modelMedias) {
			if(media.getTags().contains(tag)){
				if(currentState == TagState.Allowed)
				{
					currentState = TagState.Indeterminate;
					break; 
				}else{
					currentState = TagState.Required;
				}
			}else{
				if(currentState == TagState.Required)
				{
					currentState = TagState.Indeterminate;
					break; 
				}else{
					currentState = TagState.Allowed;
				}
			}
		}
		return currentState;
	}



	public void setTagState(Tag tag, TagState newState) {

		if(newState == TagState.Required || newState == TagState.Allowed)
		{
			for (MediaItem media : modelMedias) {
				if(newState == TagState.Required)
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
			return MediaDetailViewModel.getResolutionString(modelMedias.get(0));
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
