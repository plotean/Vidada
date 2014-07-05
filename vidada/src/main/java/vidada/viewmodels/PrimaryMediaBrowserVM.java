package vidada.viewmodels;

import archimedes.core.data.pagination.IPageLoader;
import archimedes.core.data.pagination.ListPage;
import archimedes.core.data.pagination.VirtualPagedList;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.services.ISelectionManager;
import archimedes.core.threading.CancellationTokenSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.IVidadaClientFacade;
import vidada.client.IVidadaClientManager;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.services.IMediaClientService;
import vidada.client.viewmodel.FilterViewModel;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.client.viewmodel.media.MediaDetailViewModel;
import vidada.model.filters.AsyncFetchData;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.services.ServiceProvider;

/**
 * The ViewModel for the primary media browser. It has a filter, a browser view and
 * a detail view.
 */
public class PrimaryMediaBrowserVM {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(PrimaryMediaBrowserVM.class.getName());
    private static final int maxPageSize = 50;

    private final IVidadaClientManager clientManager = ServiceProvider.Resolve(IVidadaClientManager.class);
    private final FilterViewModel filterViewModel;
    private final MediaBrowserModel browserModel;


    private IMediaViewModel mediaDetailViewModel = null;

    private AsyncFetchData<ListPage<MediaItem>> datafetcher;
    private final Object datafetcherLock = new Object();
    private CancellationTokenSource ctx;


    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

    private final EventHandlerEx<EventArgs> mediaDetailVMChanged = new EventHandlerEx<>();

    /**
     * Raised when the MediaDetailViewModel Property has changed.
     * This usually happens when the user has changed hes current selection.
     * @return
     */
    public IEvent<EventArgs> getMediaDetailVMChanged() { return mediaDetailVMChanged; }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new PrimaryMediaBrowser ViewModel
     */
    public PrimaryMediaBrowserVM() {
        browserModel = new MediaBrowserModel();
        filterViewModel = new FilterViewModel();

        clientManager.getActiveClientChanged().add((sender, args) -> onActiveClientChanged());

        browserModel.getSelectionManager().getSelectionChanged().add((sender, eventArgs) -> {
            IMediaViewModel vm = createMediaDetailViewModel(browserModel.getSelectionManager());
            setMediaDetailViewModel(vm);
        });

        filterViewModel.getFilterChangedEvent().add((sender, args) -> updateMediaBrowserModel());

        onActiveClientChanged();
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    public IMediaViewModel getMediaDetailViewModel(){
        return mediaDetailViewModel;
    }

    public void setMediaDetailViewModel(IMediaViewModel mediaViewModel){
        mediaDetailViewModel = mediaViewModel;
        mediaDetailVMChanged.fireEvent(this, EventArgs.Empty);
    }

    public MediaBrowserModel getBrowserModel() {
        return browserModel;
    }

    public FilterViewModel getFilterViewModel() {
        return filterViewModel;
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Occurs when the active Vidada client has been changed
     */
    private void onActiveClientChanged(){
        IVidadaClientFacade current = clientManager.getActive();

        if(current != null){
            current.getMediaClientService().getMediasChanged().add(mediaChangedEventListener);
        }

        updateMediaBrowserModel();
    }

    private final EventListenerEx<EventArgs> mediaChangedEventListener = (sender, args) -> {
        updateMediaBrowserModel();
    };

    /**
     * Creates a Media-Detail ViewModel from the given selection.
     * This will handle multi-selection.
     * @param mediaSelection
     * @return
     */
    private IMediaViewModel createMediaDetailViewModel(ISelectionManager<IBrowserItem> mediaSelection){

        IMediaViewModel mediaDetailVM = null;
        IVidadaClientFacade current = clientManager.getActive();

        if(current != null) {
            if (!mediaSelection.hasSelection()) {
                // ignore
            } else if (mediaSelection.getSelection().size() <= 1) {
                MediaDetailViewModel vm = new MediaDetailViewModel(current.getTagClientService());
                vm.setModel(mediaSelection.getFirstSelected());
                mediaDetailVM = vm;
            } else {
                mediaDetailVM = null;

                // currently only single selection supported
                throw new NotImplementedException("MainViewFx: Multiple Selection not supported!");
            }
        }
        return mediaDetailVM;
    }

    /**
     * Update the browser model according to the filter view
     */
    private void updateMediaBrowserModel(){

        logger.debug("Updating MediaBrowserModel, creating query and async fetcher...");

        browserModel.clearMedias();
        final MediaQuery query = filterViewModel.buildQuery();

        // Since executing the query may take some time
        // we run it in a cancelable background thread

        synchronized (datafetcherLock) {
            if(datafetcher != null && !datafetcher.isCompleted() && ctx != null && !ctx.isCancellationRequested())
            {
                ctx.cancel();
            }

            IVidadaClientFacade currentClient = clientManager.getActive();
            if(currentClient != null){
                IMediaClientService mediaClientService = currentClient.getMediaClientService();

                ctx = new CancellationTokenSource();
                datafetcher = new AsyncFetchMediaItems(mediaClientService, query, maxPageSize, ctx.getToken());
                datafetcher.getFetchingCompleteEvent().add((sender, eventArgs) -> {
                    // Occurs when the media fetching has completed
                    ListPage<MediaItem> firstPage = eventArgs.getValue();

                    IPageLoader<MediaItem> pageLoader = pageIndex -> mediaClientService.query(query, pageIndex, maxPageSize);
                    setMediaBrowserContent(firstPage, pageLoader);
                });

                datafetcher.execute();
            }
        }
        logger.debug("Updating MediaBrowserModel done.");
    }

    /**
     * Sets the media browser content
     * @param firstPage The first loaded page
     * @param pageLoader A {@link IPageLoader} to fetch additional pages if required.
     */
    private void setMediaBrowserContent(ListPage<MediaItem> firstPage, IPageLoader<MediaItem> pageLoader){

        if(firstPage != null){
            logger.info("First page async fetched:" + firstPage);
            logger.debug("Creating VirtualPagedList...");
            try{
                VirtualPagedList<MediaItem> pagedList =
                        new VirtualPagedList<>(pageLoader, firstPage);

                logger.debug("Setting paged list to browser model.");
                browserModel.setMedias(pagedList);
            }catch(Throwable e){
                logger.error(e);
            }
        }else{
            logger.warn("Could not fetch first page of media query!");
        }
    }
}
