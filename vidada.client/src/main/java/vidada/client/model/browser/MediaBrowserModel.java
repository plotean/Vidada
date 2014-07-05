package vidada.client.model.browser;

import archimedes.core.data.DataProviderTransformer;
import archimedes.core.data.IDataProvider;
import archimedes.core.data.IDeferLoaded;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import archimedes.core.services.ISelectionManager;
import archimedes.core.services.SelectionManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.ThreadUtil;
import vidada.client.viewmodel.MediaViewModel;
import vidada.client.viewmodel.browser.BrowserFolderItemVM;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.media.MediaItem;


/**
 * MediaBrowserModel for the MediaBrowserFX
 * @author IsNull
 *
 */
public class MediaBrowserModel {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaBrowserModel.class.getName());

    private final ISelectionManager<IBrowserItem> selectionManager = new SelectionManager<IBrowserItem>();

    private final BooleanProperty loadingInProgress = new SimpleBooleanProperty();

	private IDataProvider<IDeferLoaded<BrowserItemVM>> mediasDataProvider;
    private BrowserItemVM previousSelection;

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

	private final EventHandlerEx<EventArgs> mediasChangedEvent = new EventHandlerEx<EventArgs>();
	/**
	 * Raised when the media model has changed
	 */
	public IEvent<EventArgs> getMediaChangedEvent() {return mediasChangedEvent;}


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaBrowserModel
     */
	public MediaBrowserModel(){
        setLoadingInProgress(false);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the selection manager
     * @return
     */
    public ISelectionManager<IBrowserItem> getSelectionManager() {
		return selectionManager;
	}

    /**
     * Sets the media data provider
     * @param mediaProvider
     */
	public void setMedias(final IDataProvider<IDeferLoaded<MediaItem>> mediaProvider){

        ThreadUtil.runUIThread(new Runnable() {
            @Override
            public void run() {
                logger.info("Setting media DataProvider...");

                selectionManager.clear();

                if(mediaProvider != null){
                    logger.debug("Setting " + mediaProvider.size() + " medias in MediaBrowserModel...");
                    mediasDataProvider = new DataProviderTransformer<IDeferLoaded<BrowserItemVM>, IDeferLoaded<MediaItem>>(mediaProvider, transformer);
                }else {
                    logger.debug("Setting NULL medias in MediaBrowserModel.");
                }
                mediasChangedEvent.fireEvent(this, EventArgs.Empty);
            }
        });
	}

    /**
     * Gets the media data provider
     * @return
     */
	public IDataProvider<IDeferLoaded<BrowserItemVM>> getMedias(){
		return mediasDataProvider;
	}

    /**
     * Clears all medias in this model
     */
	public void clearMedias() {
		setMedias(null);
	}

    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

    public BooleanProperty loadingInProgressProperty(){
        return loadingInProgress;
    }

    public void setLoadingInProgress(final boolean state){
        ThreadUtil.runUIThread(new Runnable() {
            @Override
            public void run() {
                loadingInProgress.set(state);
            }
        });
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Event handler invoked when the selection has changed
     */
    transient private final EventListenerEx<EventArgs> VMselectionChangedListener = new EventListenerEx<EventArgs>() {
        @Override
        public void eventOccured(Object sender, EventArgs eventArgs) {

            logger.debug("MediaBrowserModel: VMselectionChangedListener ...");

            BrowserItemVM vm = (BrowserItemVM)sender;

            if(previousSelection != null) previousSelection.setSelected(false);

            selectionManager.trySelect(vm.getModel());

            previousSelection = vm;
        }
    };

    /**
     * Media transformer / adapter
     */
    private final DataProviderTransformer.ITransform<
            IDeferLoaded<BrowserItemVM>,
            IDeferLoaded<MediaItem>> transformer = new DataProviderTransformer.ITransform<IDeferLoaded<BrowserItemVM>, IDeferLoaded<MediaItem>>(){
        @Override
        public IDeferLoaded<BrowserItemVM> transform(IDeferLoaded<MediaItem> source) {
            return new DerefBrowserItemVM(source);
        }
    };

    /***************************************************************************
     *                                                                         *
     * Internal Classes                                                        *
     *                                                                         *
     **************************************************************************/

	private class DerefBrowserItemVM implements IDeferLoaded<BrowserItemVM>{

		private BrowserItemVM browserItem;
		private IDeferLoaded<MediaItem> source;


		@Override
		public IEvent<EventArgs> getLoadedEvent() { return source.getLoadedEvent(); }


		public DerefBrowserItemVM(IDeferLoaded<MediaItem> source){
			this.source = source;
		}

		@Override
		public BrowserItemVM getLoadedItem() {
			if(browserItem == null){
				if(isLoaded()){
					browserItem = create( new BrowserMediaItem(source.getLoadedItem()));
				}
			}	
			return browserItem;
		}

		@Override
		public boolean isLoaded() {
			return source.isLoaded();
		}

		public BrowserItemVM create(IBrowserItem model) {
			BrowserItemVM vm;

			if(model instanceof BrowserFolderItem){
				BrowserFolderItem folder = (BrowserFolderItem)model;
				vm = new BrowserFolderItemVM(folder);
			}else if(model instanceof BrowserMediaItem){
				vm = new MediaViewModel();
				vm.setModel(model);
			}else{
				vm = new BrowserItemVM(model);
			}

			vm.SelectionChangedEvent.add(VMselectionChangedListener);
			return vm;
		}

	}

}
