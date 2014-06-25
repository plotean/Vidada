package vidada.client.viewmodel.explorer;

import archimedes.core.data.IDataProvider;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.DirectoryLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.IVidadaServer;
import vidada.client.model.browser.BrowserFolderItem;
import vidada.client.model.browser.BrowserFolderItemLocation;
import vidada.client.model.browser.IBrowserItem;
import vidada.server.services.IMediaService;

import java.util.List;

public class MediaExplorerVM {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaExplorerVM.class.getName());


	private final IVidadaServer localServer = null; // TODO: VidadaClientManager.instance().getLocalServer();
	private final IMediaService localMediaService = null;//localServer.getMediaService();


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
			setCurrentLocation(new BrowserFolderItemLocation(location, localMediaService));
		}else{
            logger.debug("Ignored setCurrentLocation!");
		}
	}

	public void setCurrentLocation(BrowserFolderItemLocation newLocation){
		browserModel = newLocation;

        logger.debug("setCurrentLocation = " + newLocation);

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
            logger.debug("Open folder requested " + sender);
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
