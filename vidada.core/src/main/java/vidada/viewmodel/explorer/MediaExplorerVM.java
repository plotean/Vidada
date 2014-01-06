package vidada.viewmodel.explorer;

import java.util.List;

import vidada.model.ServiceProvider;
import vidada.model.browser.BrowserFolderItem;
import vidada.model.browser.BrowserFolderItemLocation;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.IDataProvider;
import vidada.model.media.IMediaService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.DirectoryLocation;

public class MediaExplorerVM {

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
	private BrowserFolderItemLocation browserModel;

	private DirectoryLocation home;
	private EventHandlerEx<EventArgs> browserModelChangedEvent = new EventHandlerEx<EventArgs>();
	public IEvent<EventArgs> getBrowserModelChangedEvent() { return browserModelChangedEvent; }

	public MediaExplorerVM(){

	}

	public void setHomeLocation(DirectoryLocation location){
		home = location;
		setCurrentLocation(home);
	}

	public DirectoryLocation getHomeLocation(){
		return home;
	}

	public void setCurrentLocation(DirectoryLocation location){
		if(getCurrentDirectory() != location){
			setCurrentLocation(new BrowserFolderItemLocation(location, mediaService));
		}else{
			System.out.println("MediaExplorerVM: ignored setCurrentLocation!!");
		}
	}

	public void setCurrentLocation(BrowserFolderItemLocation newLocation){
		browserModel = newLocation;

		System.out.println("MediaExplorerVM: setCurrentLocation " + newLocation);

		List<IBrowserItem> children = browserModel.getChildren();
		for (IBrowserItem iBrowserItem : children) {
			if(iBrowserItem instanceof BrowserFolderItem){
				((BrowserFolderItem) iBrowserItem).getOpenRequestEvent().add(openRequestedListener);
			}
		}

		browserModelChangedEvent.fireEvent(this, EventArgs.Empty);
	}

	private final EventListenerEx<EventArgs> openRequestedListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			System.out.println("MediaExplorerVM: open folder requested " + sender);
			setCurrentLocation((BrowserFolderItemLocation)sender);
		}
	};

	public DirectoryLocation getCurrentDirectory(){
		return browserModel != null ? browserModel.getDirectoryLocation() : null;
	}

	/**
	 * Get the media browser model
	 * @return
	 */
	public IDataProvider<IBrowserItem> getBrowserModel(){
		return browserModel;
	}
}
