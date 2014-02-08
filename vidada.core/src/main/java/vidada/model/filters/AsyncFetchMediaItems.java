package vidada.model.filters;

import java.util.Collection;

import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import archimedesJ.threading.CancellationTokenSource.CancellationToken;
import archimedesJ.threading.CancellationTokenSource.OperationCanceledException;

public class AsyncFetchMediaItems extends AsyncFetchData<MediaItem> {

	private final IMediaService mediaService;
	private final MediaQuery query;

	public AsyncFetchMediaItems(IMediaService mediaService,  MediaQuery query, CancellationToken token) {
		super(token);
		this.mediaService = mediaService;
		this.query = query;
	}

	@Override
	protected Collection<MediaItem> fetchData(CancellationToken token)
			throws OperationCanceledException {

		Collection<MediaItem> fetchedData = null;
		System.out.println("AsyncFetchMediaItems: fetching media datas...");
		fetchedData = mediaService.query(query);
		System.out.println("AsyncFetchMediaItems:: fetched: " + fetchedData.size() + " items!" );

		return fetchedData;
	}

}
