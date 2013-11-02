package vidada.controller.filters;

import java.util.ArrayList;
import java.util.List;

import vidada.model.ServiceProvider;
import vidada.model.filters.AsyncFetchData;
import vidada.model.filters.AsyncFetchData.CancelTokenEventArgs;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaBrowserModel;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.media.QueryBuilder;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.threading.CancellationTokenSource;

import com.db4o.query.Query;

public class MediaFilterController {

	private final IFilterView view;
	private final MediaBrowserModel mediaBrowserModel;

	private final IMediaLibraryService mediaLibraryService = ServiceProvider.Resolve(IMediaLibraryService.class);
	private final QueryBuilder queryBuilder = new QueryBuilder();

	public MediaFilterController(IFilterView view, MediaBrowserModel mediaBrowserModel){
		this.view = view;
		this.mediaBrowserModel = mediaBrowserModel;

		view.getFilterChangedEvent().add(new EventListenerEx<EventArgs>() {
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
		Query query = buildCriteria();

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

			datafetcher = new AsyncFetchData<MediaItem>(query, ctx.getToken());
			datafetcher.setPostFilter(getPostFilter());
			datafetcher.getFetchingCompleteEvent().add(dataFetchedListener);
			datafetcher.execute();
		}
	}

	transient private final EventListenerEx<CancelTokenEventArgs<List<MediaItem>>> dataFetchedListener 
	= new EventListenerEx<CancelTokenEventArgs<List<MediaItem>>>(){
		//
		// As this event is fired by an async running thread, this method will therefore be async too.
		//
		@Override
		public void eventOccured(Object sender,final CancelTokenEventArgs<List<MediaItem>> eventArgs) {

			List<MediaItem> medias = eventArgs.getValue();
			System.out.println("Data async fetched! " + medias.size());
			mediaBrowserModel.addAll(medias);
		}
	};



	/**
	 * Build the criteria query from the users selection in the view
	 * @return
	 */
	private Query buildCriteria(){

		MediaType selectedtype = (MediaType)view.getSelectedMediaType();
		List<Tag> requiredTags = view.getTagsWithState(TagFilterState.Required);
		OrderProperty selectedOrder = (OrderProperty)view.getSelectedOrder();
		List<MediaLibrary> requiredMediaLibs = new ArrayList<MediaLibrary>();
		boolean reverse = view.isReverseOrder();
		boolean onlyAvaiable = view.isOnlyShowAvaiable();
		String queryString = view.getQueryString();


		requiredMediaLibs = mediaLibraryService.getAllLibraries();
		if(onlyAvaiable)
		{
			for (MediaLibrary mediaLibrary : new ArrayList<MediaLibrary>(requiredMediaLibs)) {
				if(!mediaLibrary.isAvailable())
				{
					requiredMediaLibs.remove(mediaLibrary);
				}
			}
		}

		Query mediaDataCriteria = queryBuilder.buildMediadataCriteria(
				selectedtype,
				queryString,
				selectedOrder,
				requiredTags,
				requiredMediaLibs,
				reverse);

		return mediaDataCriteria;
	}

	private Predicate<MediaItem> getPostFilter(){

		boolean onlyAvaiable = view.isOnlyShowAvaiable();

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
}