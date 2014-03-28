package vidada.client.viewmodel.media;

import archimedes.core.data.observable.IObservableList;
import archimedes.core.util.Lists;
import vidada.client.IVidadaClientManager;
import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import vidada.services.ServiceProvider;

import java.util.List;

/**
 * Media Detail Adapter for multiple medias
 * @author IsNull
 *
 */
public class MultiMediaDetailModel implements IMediaViewModel{

	transient private final IVidadaClientManager clientManager  = ServiceProvider.Resolve(IVidadaClientManager.class);
	transient private final IMediaClientService mediaClientService = clientManager.getActive().getMediaClientService();
	private final List<MediaItem> modelMedias;


	public MultiMediaDetailModel(List<MediaItem> selectedMedias) {
		modelMedias = Lists.newList(selectedMedias);

		// redirect events to the tags changed event
		for (MediaItem mediaData : modelMedias) {
			//TagsChanged.registerEvent(mediaData.getTagAddedEvent());
			//TagsChanged.registerEvent(mediaData.getTagRemovedEvent());
		}
	}

	/*
	 * @Override
	public ITagStatesVMProvider getTagsVM() {
		throw new NotImplementedException("getTagsVM");
	}
	 */


	/**
	 * Gets the state for the given tag
	 * 
	 * The state is checked if all media data have the given Tag
	 * The state is unchecked if all media data don't have the Tag
	 * The state is Indeterminate if the media data are not consistent

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
	}*/

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


	protected void persist() {
		for (MediaItem media : modelMedias) {
			mediaClientService.update(media);
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

	@Override
	public IObservableList<Tag> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tag createTag(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tag> getAvailableTags() {
		// TODO Auto-generated method stub
		return null;
	}


}
