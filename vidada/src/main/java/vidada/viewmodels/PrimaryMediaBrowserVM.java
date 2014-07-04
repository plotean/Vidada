package vidada.viewmodels;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.services.ISelectionManager;
import vidada.client.IVidadaClientFacade;
import vidada.client.IVidadaClientManager;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.viewmodel.FilterModel;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.client.viewmodel.media.MediaDetailViewModel;
import vidada.services.ServiceProvider;

/**
 * The viewmodel for the primary media browser
 */
public class PrimaryMediaBrowserVM {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final IVidadaClientManager clientManager = ServiceProvider.Resolve(IVidadaClientManager.class);

    private final FilterModel filterModel;
    private final MediaBrowserModel browserModel;

    private IMediaViewModel mediaDetailViewModel = null;


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
        filterModel = new FilterModel();

        //MediaFilterDatabaseBinding.bind(mediaService, filterModel, browserModel);

        clientManager.getActiveClientChanged().add((sender,args) -> updateActiveClient());

        updateActiveClient();

        browserModel.getSelectionManager().getSelectionChanged().add((sender, eventArgs) -> {
            IMediaViewModel vm = createMediaDetailViewModel(browserModel.getSelectionManager());
            setMediaDetailViewModel(vm);
        });
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

    public FilterModel getFilterModel() {
        return filterModel;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private void updateActiveClient(){
        IVidadaClientFacade current = clientManager.getActive();
        // TODO
    }


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
}
