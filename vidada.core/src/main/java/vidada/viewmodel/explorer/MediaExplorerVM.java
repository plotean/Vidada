package vidada.viewmodel.explorer;

import vidada.model.ServiceProvider;
import vidada.model.browser.BrowserFolderItem;
import vidada.model.browser.BrowserFolderItemLocation;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.IDataProvider;
import vidada.model.media.IMediaService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.io.locations.DirectoryLocation;

public class MediaExplorerVM {

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
	private  IDataProvider<IBrowserItem> browserModel;

	private EventHandlerEx<EventArgs> browserModelChangedEvent = new EventHandlerEx<EventArgs>();
	public IEvent<EventArgs> getBrowserModelChangedEvent() { return browserModelChangedEvent; }

	public MediaExplorerVM(){

	}

	public void setCurrentLocation(DirectoryLocation location){
		setCurrentLocation(new BrowserFolderItemLocation(location, mediaService));
	}

	public void setCurrentLocation(BrowserFolderItem newLocation){
		browserModel = newLocation;
		browserModelChangedEvent.fireEvent(this, EventArgs.Empty);
	}

	/**
	 * Get the media browser model
	 * @return
	 */
	public IDataProvider<IBrowserItem> getBrowserModel(){
		return browserModel;
	}
}
