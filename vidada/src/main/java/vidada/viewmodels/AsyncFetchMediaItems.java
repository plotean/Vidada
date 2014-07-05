package vidada.viewmodels;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.threading.CancellationTokenSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.services.IMediaClientService;
import vidada.model.filters.AsyncFetchData;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;



/**
 * Represents a Task which fetches MediaItems asynchronously
 * @author IsNull
 *
 */
public class AsyncFetchMediaItems extends AsyncFetchData<ListPage<MediaItem>> {

    private static final Logger logger = LogManager.getLogger(AsyncFetchMediaItems.class.getName());

    private final IMediaClientService mediaClientService;
    private final MediaQuery query;
    private final int maxPageSize;


    /**
     * Creates a new AsyncFetchMediaItems Task
     * @param mediaClientService
     * @param query
     * @param token
     */
    public AsyncFetchMediaItems(IMediaClientService mediaClientService, MediaQuery query, int maxPageSize, CancellationTokenSource.CancellationToken token) {
        super(token);
        this.mediaClientService = mediaClientService;
        this.query = query;
        this.maxPageSize = maxPageSize;
    }

    @Override
    protected ListPage<MediaItem> fetchData(CancellationTokenSource.CancellationToken token)
            throws CancellationTokenSource.OperationCanceledException {

        logger.debug("Async - executing media query for the initial page");

        ListPage<MediaItem> page = mediaClientService.query(query, 0, maxPageSize);

        logger.debug("Async - fetched first page:" + page );

        return page;
    }
}

