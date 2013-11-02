package vidada.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

public class FilterModel {

	private final TagStatesModel tagStatesModel;

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




	public FilterModel(TagStatesModel tagStatesModel){
		this.tagStatesModel = tagStatesModel;

		tagStatesModel.getTagStateChangedEvent().add(new EventListenerEx<EventArgsG<TagViewModel>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
				onFilterChanged();
			}
		});
	}



	public List<Tag> getRequiredTags() {
		return tagStatesModel.getTagsWithState(TagFilterState.Required);
	}

	public List<Tag> getBlockedTags() {
		return tagStatesModel.getTagsWithState(TagFilterState.Blocked);
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



	public TagStatesModel getTagStatesModel(){
		return tagStatesModel;
	}


	private void onFilterChanged(){
		filterChangedEvent.fireEvent(this, EventArgs.Empty);
	}

}
