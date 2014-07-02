package vidada.controller.filters;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.data.pagination.VirtualPagedList;
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
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;

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

    /**
     * Creates a new MediaFilterDatabaseBinding
     * @param serverClientService
     * @param mediaClientService
     * @param filterModel
     * @param mediaBrowserModel
     */
    protected MediaFilterDatabaseBinding(IVidadaClientManager serverClientService, IMediaClientService mediaClientService, FilterModel filterModel, MediaBrowserModel mediaBrowserModel){
        this.serverClientService = serverClientService;
        this.mediaClientService = mediaClientService;
        this.filterModel = filterModel;
        this.mediaBrowserModel = mediaBrowserModel;

        filterModel.getFilterChangedEvent().add((sender, eventArgs) -> updateMediaBrowserModel());

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

        logger.debug("Updating MediaBrowserModel, creating query and async fetcher...");

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

            datafetcher.getFetchingCompleteEvent().add((sender, eventArgs) -> {
                ListPage<MediaItem> firstPage = eventArgs.getValue();

                if(firstPage != null){
                    logger.info("First page async fetched:" + firstPage);
                    logger.debug("Creating VirtualPagedList...");
                    try{
                        VirtualPagedList<MediaItem> pagedList =
                                new VirtualPagedList<>(
                                        pageIndex -> mediaClientService.query(query, pageIndex, maxPageSize), //TODO Why query here??
                                        firstPage);

                        logger.debug("Setting paged list to browser model.");
                        mediaBrowserModel.setMedias(pagedList);

                    }catch(Throwable e){
                        logger.error(e);
                    }
                }else{
                    logger.warn("Could not fetch first page of media query!");
                }
            });

            datafetcher.execute();
        }
        logger.debug("Updating MediaBrowserModel done.");
    }


    /**
     * Build the criteria query from the users selection in the view
     * @return
     */
    private MediaQuery buildQuery(){

        MediaQuery query = new MediaQuery(
                filterModel.getMediaType(),
                filterModel.getQueryString(),
                filterModel.getOrder(),
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

            logger.debug("Async - executing media query for the initial page");

            ListPage<MediaItem> page = mediaClientService.query(query, 0, maxPageSize);

            logger.debug("Async - fetched first page:" + page );

            return page;
        }

    }
}
