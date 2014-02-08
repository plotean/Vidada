package vidada.controller.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vidada.model.ServiceProvider;
import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.MediaBrowserModel;
import vidada.model.filters.AsyncFetchData;
import vidada.model.filters.AsyncFetchData.CancelTokenEventArgs;
import vidada.model.filters.AsyncFetchMediaItems;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.media.source.IMediaSource;
import vidada.model.tags.TagState;
import vidada.viewmodel.FilterModel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.threading.CancellationTokenSource;

/**
 * Represents a binding between a mediabrowser model an filter settings.
 * The datasource for the query which is internally performed is the primary database,
 * thus this binding is not really flexible. 
 * 
 * TODO: Refactor this binding to support arbitrary data sources
 * 
 * @author IsNull
 *
 */
public class MediaFilterDatabaseBinding {

	private final FilterModel filterModel;
	private final MediaBrowserModel mediaBrowserModel;
	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);


	public static MediaFilterDatabaseBinding bind(FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		return new MediaFilterDatabaseBinding(filterModel, mediaBrowserModel);
	}


	protected MediaFilterDatabaseBinding(FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		this.filterModel = filterModel;
		this.mediaBrowserModel = mediaBrowserModel;

		filterModel.getFilterChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				updateMediaBrowserModel();
			}
		});

		updateMediaBrowserModel();
	}



	transient private AsyncFetchData<MediaItem> datafetcher;
	transient private final Object datafetcherLock = new Object();
	transient private CancellationTokenSource ctx;


	/**
	 * Update the browser model according to the filter view
	 */
	private void updateMediaBrowserModel(){

		mediaBrowserModel.clear();
		MediaQuery query = buildQuery();

		// Since executing the query may take some time
		// we run it in a background thread

		synchronized (datafetcherLock) {
			if(datafetcher != null && !datafetcher.isDone() && ctx != null && !ctx.isCancellationRequested())
			{
				ctx.cancel();
			}

			if(datafetcher != null)
			{
				datafetcher.getFetchingCompleteEvent().remove(dataFetchedListener);
			}

			ctx = new CancellationTokenSource();

			datafetcher = new AsyncFetchMediaItems(mediaService, query, ctx.getToken());
			datafetcher.setPostFilter(getPostFilter());
			datafetcher.getFetchingCompleteEvent().add(dataFetchedListener);
			datafetcher.execute();
		}
	}

	transient private final EventListenerEx<CancelTokenEventArgs<Collection<MediaItem>>> dataFetchedListener 
	= new EventListenerEx<CancelTokenEventArgs<Collection<MediaItem>>>(){
		//
		// As this event is fired by an async running thread, this method will therefore be async too.
		//
		@Override
		public void eventOccured(Object sender,final CancelTokenEventArgs<Collection<MediaItem>> eventArgs) {

			Collection<MediaItem> medias = eventArgs.getValue();
			System.out.println("MediaFilterDatabaseBinding: Data async fetched: " + medias.size());

			List<IBrowserItem> browserItems = new ArrayList<IBrowserItem>(medias.size());
			for (MediaItem mediaItem : medias) {
				browserItems.add(new BrowserMediaItem(mediaItem));
			}

			mediaBrowserModel.addAll(browserItems);
		}
	};



	/**
	 * Build the criteria query from the users selection in the view
	 * @return
	 */
	private MediaQuery buildQuery(){

		MediaQuery query = new MediaQuery(
				(MediaType)filterModel.getMediaType(),
				filterModel.getQueryString(),
				(OrderProperty)filterModel.getOrder(),
				mediaBrowserModel.getTagStatesModel().getTagsWithState(TagState.Required),
				mediaBrowserModel.getTagStatesModel().getTagsWithState(TagState.Blocked),
				filterModel.isOnlyAvaiable(), 
				filterModel.isReverse());

		return query;
	}

	private Predicate<MediaItem> getPostFilter(){

		boolean onlyAvaiable = filterModel.isOnlyAvaiable();

		if(onlyAvaiable){

			return new Predicate<MediaItem>() {
				@Override
				public boolean where(MediaItem value) {
					// filter out medias which have a no available source
					for (IMediaSource s : value.getSources()) {
						if(s.isAvailable())
							return true;
					}
					return false;
				}
			};
		}

		return null;
	}
}
