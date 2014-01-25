package vidada.viewmodel;

import java.util.ArrayList;
import java.util.List;

import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.tags.TagViewModel;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

public class FilterModel {

	private final ITagStatesVMProvider tagStatesModel;
	private final IMediaService mediaService;

	// filter state

	private List<Tag> requiredTags = new ArrayList<Tag>();
	private MediaType mediatype = MediaType.ANY;
	private OrderProperty order = OrderProperty.FILENAME;
	private List<MediaLibrary> requiredMediaLibs = new ArrayList<MediaLibrary>();
	private boolean reverse = false;
	private boolean onlyAvaiable = false;
	private String queryString = "";

	//
	// Events
	//
	private final EventHandlerEx<EventArgs> filterChangedEvent = new EventHandlerEx<EventArgs>();
	public IEvent<EventArgs> getFilterChangedEvent() { return filterChangedEvent; }




	public FilterModel(ITagStatesVMProvider tagStatesModel, IMediaService mediaService){
		this.tagStatesModel = tagStatesModel;
		this.mediaService = mediaService;

		tagStatesModel.getTagStateChangedEvent().add(new EventListenerEx<EventArgsG<TagViewModel>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
				onFilterChanged();
			}
		});

		mediaService.getMediasChangedEvent().add(new EventListenerEx<CollectionEventArg<MediaItem>>() {
			@Override
			public void eventOccured(Object sender, CollectionEventArg<MediaItem> eventArgs) {		
				onFilterChanged();
			}
		});

	}


	public List<Tag> getRequiredTags() {
		return tagStatesModel.getTagsWithState(TagState.Required);
	}

	public List<Tag> getBlockedTags() {
		return tagStatesModel.getTagsWithState(TagState.Blocked);
	}



	public MediaType getMediaType() {
		return mediatype;
	}


	public void setMediaType(MediaType mediaType) {
		this.mediatype = mediaType;
		onFilterChanged();
	}


	public OrderProperty getOrder() {
		return order;
	}


	public void setOrder(OrderProperty order) {
		this.order = order;
		onFilterChanged();
	}


	public List<MediaLibrary> getRequiredMediaLibs() {
		return requiredMediaLibs;
	}


	public void setRequiredMediaLibs(List<MediaLibrary> requiredMediaLibs) {
		this.requiredMediaLibs = requiredMediaLibs;
	}


	public boolean isReverse() {
		return reverse;
	}


	public void setReverse(boolean reverse) {
		this.reverse = reverse;
		onFilterChanged();
	}


	public boolean isOnlyAvaiable() {
		return onlyAvaiable;
	}


	public void setOnlyAvaiable(boolean onlyAvaiable) {
		this.onlyAvaiable = onlyAvaiable;
		onFilterChanged();
	}


	public String getQueryString() {
		return queryString;
	}


	public void setQueryString(String queryString) {
		this.queryString = queryString;
		onFilterChanged();
	}



	public ITagStatesVMProvider getTagStatesModel(){
		return tagStatesModel;
	}


	private void onFilterChanged(){
		filterChangedEvent.fireEvent(this, EventArgs.Empty);
	}

}
