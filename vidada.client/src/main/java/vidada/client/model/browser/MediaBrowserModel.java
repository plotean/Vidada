package vidada.client.model.browser;

import vidada.client.viewmodel.MediaViewModel;
import vidada.client.viewmodel.browser.BrowserFolderItemVM;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.media.MediaItem;
import vidada.model.pagination.DataProviderTransformer;
import vidada.model.pagination.DataProviderTransformer.ITransform;
import vidada.model.pagination.IDataProvider;
import vidada.model.pagination.IDeferLoaded;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import archimedes.core.services.ISelectionManager;
import archimedes.core.services.SelectionManager;


/**
 * MediaBrowserModel for the MediaBrowserFX
 * @author IsNull
 *
 */
public class MediaBrowserModel {

	private final ISelectionManager<IBrowserItem> selectionManager = new SelectionManager<IBrowserItem>();

	private IDataProvider<IDeferLoaded<BrowserItemVM>> mediasDataProvider;

	//
	// Events
	//
	private final EventHandlerEx<EventArgs> mediasChangedEvent = new EventHandlerEx<EventArgs>();
	/**
	 * Raised when the media model has changed
	 */
	public IEvent<EventArgs> getMediaChangedEvent() {return mediasChangedEvent;}


	public MediaBrowserModel(){

	}

	public ISelectionManager<IBrowserItem> getSelectionManager() {
		return selectionManager;
	}

	public void setMedias(IDataProvider<IDeferLoaded<MediaItem>> mediaProvider){

		selectionManager.clear();

		if(mediaProvider != null){

			System.out.println("MediaBrowserModel:setMedias size(" + mediaProvider.size() + ")");

			ITransform<
			IDeferLoaded<BrowserItemVM>,
			IDeferLoaded<MediaItem>> transformer = new ITransform<IDeferLoaded<BrowserItemVM>, IDeferLoaded<MediaItem>>(){
				@Override
				public IDeferLoaded<BrowserItemVM> transform(IDeferLoaded<MediaItem> source) {
					return new DerefBrowserItemVM(source);
				}
			};

			mediasDataProvider = new DataProviderTransformer<IDeferLoaded<BrowserItemVM>, IDeferLoaded<MediaItem>>(mediaProvider, transformer);
		}else {
			System.out.println("MediaBrowserModel:setMedias mediaProvider := NULL");
		}

		mediasChangedEvent.fireEvent(this, EventArgs.Empty);
	}


	public IDataProvider<IDeferLoaded<BrowserItemVM>> getMedias(){
		return mediasDataProvider;
	}

	public void clearMedias() {
		setMedias(null);
	}

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

	private BrowserItemVM previousSelection;

	transient private final EventListenerEx<EventArgs> VMselectionChangedListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {

			System.out.println("MediaBrowserModel: VMselectionChangedListener ...");

			BrowserItemVM vm = (BrowserItemVM)sender;

			if(previousSelection != null) previousSelection.setSelected(false);

			selectionManager.trySelect(vm.getModel());

			previousSelection = vm;
		}
	};



}
