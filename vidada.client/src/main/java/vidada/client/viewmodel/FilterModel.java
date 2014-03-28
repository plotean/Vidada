package vidada.client.viewmodel;

import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.data.observable.IObservableList;
import archimedes.core.data.observable.ObservableArrayList;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaLibrary;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for all query filter options.
 */
public class FilterModel {

    private boolean autoFireEnabled = true;

	// filter state

	private final IObservableList<Tag> requiredTags = new ObservableArrayList<Tag>();
	private final IObservableList<Tag> blockedTags = new ObservableArrayList<Tag>();


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


	public FilterModel(IMediaClientService mediaService){

		requiredTags.getChangeEvent().add(new EventListenerEx<CollectionEventArg<Tag>>() {
			@Override
			public void eventOccured(Object sender, CollectionEventArg<Tag> eventArgs) {
				onFilterChanged();
			}
		});

		blockedTags.getChangeEvent().add(new EventListenerEx<CollectionEventArg<Tag>>() {
			@Override
			public void eventOccured(Object sender, CollectionEventArg<Tag> eventArgs) {
				onFilterChanged();
			}
		});

		/*
		mediaService.getMediasChangedEvent().add(new EventListenerEx<CollectionEventArg<MediaItem>>() {
			@Override
			public void eventOccured(Object sender, CollectionEventArg<MediaItem> eventArgs) {		
				onFilterChanged();
			}
		});*/
	}


	public IObservableList<Tag> getRequiredTags() {
		return requiredTags;
	}

	public IObservableList<Tag> getBlockedTags() {
		return blockedTags;
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

    // Filter behaviour

    public boolean isAutoFireEnabled() {
        return autoFireEnabled;
    }

    /**
     * Auto-fire will cause a change event (and implicitly a requery of the data)
     * This can improve user experience at cost of higher bandwidth and processing costs.
     *
     * @param autoFireEnabled
     */
    public void setAutoFireEnabled(boolean autoFireEnabled) {
        this.autoFireEnabled = autoFireEnabled;
    }

    /**
     * Fires the filter changed event
     */
    public void fireFilterChanged(){
        filterChangedEvent.fireEvent(this, EventArgs.Empty);
    }


    /**
     * Occurs when the filter settings have been changed.
     */
	private void onFilterChanged(){
		if(isAutoFireEnabled())
            fireFilterChanged();
	}



}
