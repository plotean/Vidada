package vidada.controller.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vidada.client.model.browser.BrowserMediaItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.services.IMediaClientService;
import vidada.client.viewmodel.FilterModel;
import vidada.model.filters.AsyncFetchData;
import vidada.model.filters.AsyncFetchData.CancelTokenEventArgs;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.media.source.MediaSource;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.threading.CancellationTokenSource;
import archimedesJ.threading.CancellationTokenSource.CancellationToken;
import archimedesJ.threading.CancellationTokenSource.OperationCanceledException;

/**
 * Represents a binding between a mediabrowser model an filter settings.
 * 
 * @author IsNull
 *
 */
public class MediaFilterDatabaseBinding {


	/***************************************************************************
	 *                                                                         *
	 * Static methods                                                          *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a binding between the filter-model and browser-model using the given mediaClientService
	 * @param mediaClientService
	 * @param filterModel
	 * @param mediaBrowserModel
	 * @return
	 */
	public static MediaFilterDatabaseBinding bind(IMediaClientService mediaClientService, FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		return new MediaFilterDatabaseBinding(mediaClientService, filterModel, mediaBrowserModel);
	}



	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final FilterModel filterModel;
	private final MediaBrowserModel mediaBrowserModel;
	private final IMediaClientService mediaClientService;

	transient private AsyncFetchData<MediaItem> datafetcher;
	transient private final Object datafetcherLock = new Object();
	transient private CancellationTokenSource ctx;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	protected MediaFilterDatabaseBinding(IMediaClientService mediaClientService, FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		this.mediaClientService = mediaClientService;
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

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/


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

			datafetcher = new AsyncFetchMediaItems(mediaClientService, query, ctx.getToken());
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
				filterModel.getRequiredTags(),
				filterModel.getBlockedTags(),
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
					for (MediaSource s : value.getSources()) {
						if(s.isAvailable())
							return true;
					}
					return false;
				}
			};
		}

		return null;
	}


	/***************************************************************************
	 *                                                                         *
	 * Inner classes                                                           *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Represents a Task which fetches MediaItems asynchronously
	 * @author IsNull
	 *
	 */
	class AsyncFetchMediaItems extends AsyncFetchData<MediaItem> {

		private final IMediaClientService mediaClientService;
		private final MediaQuery query;

		public AsyncFetchMediaItems(IMediaClientService mediaClientService,  MediaQuery query, CancellationToken token) {
			super(token);
			this.mediaClientService = mediaClientService;
			this.query = query;
		}

		@Override
		protected Collection<MediaItem> fetchData(CancellationToken token)
				throws OperationCanceledException {

			Collection<MediaItem> fetchedData = null;
			System.out.println("AsyncFetchMediaItems: fetching media datas...");
			fetchedData = mediaClientService.query(query);
			System.out.println("AsyncFetchMediaItems:: fetched: " + fetchedData.size() + " items!" );

			return fetchedData;
		}

	}
}
