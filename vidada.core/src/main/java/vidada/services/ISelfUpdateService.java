package vidada.services;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.IEvent;
import archimedes.core.services.IService;
import archimedes.core.threading.IAsyncTask;
import vidada.model.update.SelfUpdateState;
import vidada.model.update.UpdateInformation;

/**
 * This service handles the self-update capabilities.
 * 
 * @author IsNull
 *
 */
public interface ISelfUpdateService extends IService{

	/**
	 * Event raised when a new Update is available for download.
	 * 
	 * @return
	 */
	IEvent<EventArgsG<UpdateInformation>> getUpdateDownloadAvailableEvent();

	/**
	 * Event raised when a new update is ready to be installed.
	 * @return
	 */
	IEvent<EventArgs> getUpdateInstallAvailableEvent();


	/**
	 * Get the current state of the self update service
	 * @return
	 */
	SelfUpdateState getState();


	/**
	 * Checks for an update asynchronously
	 */
	IAsyncTask<UpdateInformation> checkForUpdateAsync();

	/**
	 * Downloads the current Update.
	 * @return
	 */
	IAsyncTask<Void> downloadUpdateAsync(); 

	/**
	 * Installs the update and restarts.
	 * 
	 * 1. This will rename the current running binary (moved to trash?).
	 * 2. Then the freshly loaded binary is copied from the cache with the original name.
	 * 3. Application is restarted.
	 * 
	 * TODO Additionally, it can also export information from the DB in JSON format
	 * in case database scheme has changed...
	 * 
	 */
	void installAndRestart();
}
