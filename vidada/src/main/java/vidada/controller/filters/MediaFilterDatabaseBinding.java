package vidada.controller.filters;

import archimedes.core.data.pagination.IPageLoader;
import archimedes.core.data.pagination.ListPage;
import archimedes.core.data.pagination.VirtualPagedList;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.threading.CancellationTokenSource;
import archimedes.core.threading.CancellationTokenSource.CancellationToken;
import archimedes.core.threading.CancellationTokenSource.OperationCanceledException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.IVidadaClientManager;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.services.IMediaClientService;
import vidada.client.viewmodel.FilterModel;
import vidada.model.filters.AsyncFetchData;
import vidada.model.filters.AsyncFetchData.CancelTokenEventArgs;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;

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
	public static MediaFilterDatabaseBinding bind(IVidadaClientManager serverClientService,IMediaClientService mediaClientService, FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		return new MediaFilterDatabaseBinding(serverClientService, mediaClientService, filterModel, mediaBrowserModel);
	}



	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaFilterDatabaseBinding.class.getName());

	private final FilterModel filterModel;
	private final MediaBrowserModel mediaBrowserModel;
	private final IMediaClientService mediaClientService;
	private final IVidadaClientManager serverClientService;

	transient private AsyncFetchData<ListPage<MediaItem>> datafetcher;
	transient private final Object datafetcherLock = new Object();
	transient private CancellationTokenSource ctx;

	private int maxPageSize = 50;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	protected MediaFilterDatabaseBinding(IVidadaClientManager serverClientService, IMediaClientService mediaClientService, FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
		this.serverClientService = serverClientService;
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

		mediaBrowserModel.clearMedias();
		final MediaQuery query = buildQuery();

		// Since executing the query may take some time
		// we run it in a background thread

		synchronized (datafetcherLock) {
			if(datafetcher != null && !datafetcher.isCompleted() && ctx != null && !ctx.isCancellationRequested())
			{
				ctx.cancel();
			}

			ctx = new CancellationTokenSource();

			datafetcher = new AsyncFetchMediaItems(mediaClientService, query, ctx.getToken());
			datafetcher.getFetchingCompleteEvent().add(new EventListenerEx<CancelTokenEventArgs<ListPage<MediaItem>>>(){
				//
				// As this event is fired by an async running thread, this method will therefore be async too.
				//
				@Override
				public void eventOccured(Object sender,final CancelTokenEventArgs<ListPage<MediaItem>> eventArgs) {

					ListPage<MediaItem> firstPage = eventArgs.getValue();
                    logger.info("MediaFilterDatabaseBinding: Data async fetched: " + firstPage);

					if(firstPage != null){
                        logger.debug("Creating VirtualPagedList...");
						try{
							VirtualPagedList<MediaItem> pagedList = new VirtualPagedList<MediaItem>(new IPageLoader<MediaItem>() {
								@Override
								public ListPage<MediaItem> load(int pageIndex) {
									return mediaClientService.query(query, pageIndex, maxPageSize);
								}
							},firstPage);

                            logger.debug("Setting paged list to browser model.");
							mediaBrowserModel.setMedias(pagedList);

						}catch(Throwable e){
							e.printStackTrace();
						}
					}

				}
			});
			datafetcher.execute();
		}
	}


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
	class AsyncFetchMediaItems extends AsyncFetchData<ListPage<MediaItem>> {

        private final Logger logger = LogManager.getLogger(AsyncFetchMediaItems.class.getName());


        private final IMediaClientService mediaClientService;
		private final MediaQuery query;

		public AsyncFetchMediaItems(IMediaClientService mediaClientService, MediaQuery query, CancellationToken token) {
			super(token);
			this.mediaClientService = mediaClientService;
			this.query = query;
		}

		@Override
		protected ListPage<MediaItem> fetchData(CancellationToken token)
				throws OperationCanceledException {

			ListPage<MediaItem> page = null;
			logger.debug("AsyncFetchMediaItems: fetching media datas...");
			page = mediaClientService.query(query, 0, maxPageSize);
            logger.debug("AsyncFetchMediaItems:: fetched: " + page );

			return page;
		}

	}
}
