package vidada.client.viewmodel;

import archimedes.core.events.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import vidada.client.IVidadaClientFacade;
import vidada.client.IVidadaClientManager;
import vidada.client.services.ITagClientService;
import vidada.client.viewmodel.tags.TagViewModel;
import vidada.model.media.MediaLibrary;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.model.tags.TagState;
import vidada.services.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Model for all query filter options.
 */
public class FilterViewModel {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final IVidadaClientManager clientManager = ServiceProvider.Resolve(IVidadaClientManager.class);


    private boolean autoFireEnabled = true;

	// filter state

    private final ObservableList<TagViewModel> tags = FXCollections.observableArrayList();


	private MediaType mediatype = MediaType.ANY;
	private OrderProperty order = OrderProperty.ADDEDDATE;
	private List<MediaLibrary> requiredMediaLibs = new ArrayList<MediaLibrary>();
	private boolean reverse = false;
	private boolean onlyAvaiable = false;
	private String queryString = "";

    Collection<TagViewModel> availableTags = null;

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/


	private final EventHandlerEx<EventArgs> filterChangedEvent = new EventHandlerEx<EventArgs>();
    private final EventHandlerEx<EventArgs> availableTagsChanged = new EventHandlerEx<EventArgs>();

    /**
     * Raised when the filter has been changed. (This includes all filter properties)
     * @return
     */
    public IEvent<EventArgs> getFilterChangedEvent() { return filterChangedEvent; }

    /**
     * Raised when the available tags have changed.
     * @return
     */
    public IEvent<EventArgs> getAvailableTagsChanged() {return availableTagsChanged; }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a FilterViewModel
     */
	public FilterViewModel(){

		tags.addListener(new ListChangeListener<TagViewModel>() {
            @Override
            public void onChanged(Change<? extends TagViewModel> c) {
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

        clientManager.getActiveClientChanged().add(new EventListenerEx<EventArgs>() {
            @Override
            public void eventOccured(Object o, EventArgs args) {
                updateAvailableTagsAsync();
            }
        });

        updateAvailableTagsAsync();
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Build the criteria query from the current values in this viewmodel
     * @return
     */
    public MediaQuery buildQuery(){

        MediaQuery query = new MediaQuery(
                this.getMediaType(),
                this.getQueryString(),
                this.getOrder(),
                this.getRequiredTags(),
                this.getBlockedTags(),
                this.isOnlyAvaiable(),
                this.isReverse());

        return query;
    }

    /***************************************************************************
     *                                                                         *
     * Private Properties                                                      *
     *                                                                         *
     **************************************************************************/

    private List<Tag> getRequiredTags() {
        List<Tag> requiredTags = new ArrayList<Tag>();

        for(TagViewModel tagViewModel : tags){
            if(!tagViewModel.getState().equals(TagState.Blocked)){
                requiredTags.add(tagViewModel.getModel());
            }
        }
        return requiredTags;
    }

    private List<Tag> getBlockedTags() {
        List<Tag> blockedTags = new ArrayList<Tag>();

        for(TagViewModel tagViewModel : tags){
            if(tagViewModel.getState().equals(TagState.Blocked)){
                blockedTags.add(tagViewModel.getModel());
            }
        }
        return blockedTags;
    }

    private void setAvailableTags(Collection<TagViewModel> tags){
        availableTags =  tags != null ? tags : new ArrayList<TagViewModel>();
        availableTagsChanged.fireEvent(this, EventArgs.Empty);
    }


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the filter tags
     * @return
     */
    public ObservableList<TagViewModel> getTags(){
        return tags;
    }

    /**
     * Gets all available (known) tags
     * @return
     */
    public Collection<TagViewModel> getAvailableTags(){
        return availableTags;
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


    public TagViewModel createTag(String name){
        Tag tag = TagFactory.instance().createTag(name);
        return createVM(tag);
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Occurs when the filter settings have been changed.
     */
	private void onFilterChanged(){
		if(isAutoFireEnabled())
            fireFilterChanged();
	}

    private void updateAvailableTagsAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Since we call a client service we have to expect long delays over network

                Collection<TagViewModel> availableTags = new ArrayList<TagViewModel>();

                IVidadaClientFacade currentClient = clientManager.getActive();
                if(currentClient != null){
                    ITagClientService tagClientService = currentClient.getTagClientService();
                    for(Tag t : tagClientService.getUsedTags())       {
                        availableTags.add(createVM(t));
                    }
                }

                setAvailableTags(availableTags);
            }
        }).start();
    }

    private TagViewModel createVM(Tag tag){
        TagViewModel vm = null;
        if(tag != null) {
            vm = new TagViewModel(tag, TagState.Allowed, TagState.Blocked);
            vm.getTagStateChangedEvent().add(new EventListenerEx<EventArgsG<TagViewModel>>() {
                @Override
                public void eventOccured(Object o, EventArgsG<TagViewModel> tagViewModelEventArgsG) {
                    fireFilterChanged();
                }
            });
        }
        return vm;
    }

    /**
     * Fires the filter changed event
     */
    private void fireFilterChanged(){
        filterChangedEvent.fireEvent(this, EventArgs.Empty);
    }

}
