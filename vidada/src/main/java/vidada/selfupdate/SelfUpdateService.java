package vidada.selfupdate;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.threading.CancelableTask;
import archimedes.core.threading.CancellationTokenSource.CancellationToken;
import archimedes.core.threading.CancellationTokenSource.OperationCanceledException;
import archimedes.core.threading.IAsyncTask;
import archimedes.core.threading.TaskState;
import archimedes.core.util.OSValidator;
import maven.client.autoupdate.MavenAutoUpdateClient;
import maven.client.autoupdate.MavenVersion;
import maven.client.autoupdate.MavenVersion.VersionFormatException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.Application;
import vidada.model.update.SelfUpdateState;
import vidada.model.update.UpdateInformation;
import vidada.services.ISelfUpdateService;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This service manages the self update of Vidada
 *
 */
public class SelfUpdateService implements ISelfUpdateService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(SelfUpdateService.class.getName());


    private File localUpdateCache;
    private MavenAutoUpdateClient mavenUpdateClient;

    private MavenVersion runningVersion = null;
    private MavenVersion latestVersion = null;

    private CancelableTask<UpdateInformation> updateCheckTask;
    private final Object updateCheckTaskLock = new Object();

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

	private EventHandlerEx<EventArgsG<UpdateInformation>> updateAvailableEvent = new EventHandlerEx<>();
	@Override
	public IEvent<EventArgsG<UpdateInformation>> getUpdateDownloadAvailableEvent() { return updateAvailableEvent; }

	private EventHandlerEx<EventArgs> updateInstallAvailableEvent = new EventHandlerEx<>();
	@Override
	public IEvent<EventArgs> getUpdateInstallAvailableEvent() { return updateInstallAvailableEvent; }

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	public SelfUpdateService(){

		File appData = OSValidator.defaultAppData();
		localUpdateCache = new File(appData, "Vidada/updates");

		try {
			mavenUpdateClient = new MavenAutoUpdateClient(
					new URI("http://dl.securityvision.ch/maven"),
					"ch.securityvision.vidada",
					"Vidada",
					localUpdateCache);
		} catch (URISyntaxException e) {
            logger.error(e);
		}
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	@Override
	public IAsyncTask<UpdateInformation> checkForUpdateAsync() {
		synchronized (updateCheckTaskLock) {
			if(updateCheckTask == null){
				// Create new task
				updateCheckTask = new CancelableTask<UpdateInformation>(null){
					@Override
					public UpdateInformation runCancelable(CancellationToken token)
							throws OperationCanceledException {

						UpdateInformation info = null;
						MavenVersion runningVersion = getRunningVersion();
						if(!runningVersion.equals(MavenVersion.INVLAID)){

							latestVersion = mavenUpdateClient.fetchLatestVersion();

							if(latestVersion.isNewerThan(runningVersion)){
								info = new UpdateInformation(true, latestVersion.toString());
								updateAvailableEvent.fireEvent(this, EventArgsG.build(info));
							}else {
								info = new UpdateInformation(false, latestVersion.toString());
							}
						}else{
                            logger.warn("Update-Check: Can not determine the running version, aborting.");
                        }
						updateCheckTask = null;
						return info;
					}
				};
				new Thread(updateCheckTask).run();
			}
			return updateCheckTask;
		}
	}

	private CancelableTask<Void> downloadUpdateTask;
	private final Object downloadUpdateTaskLock = new Object();

	@Override
	public IAsyncTask<Void> downloadUpdateAsync() {

		synchronized (downloadUpdateTaskLock) {
			if(downloadUpdateTask == null){
				// Create new task
				downloadUpdateTask = new CancelableTask<Void>(null){
					@Override
					public Void runCancelable(CancellationToken token)
							throws OperationCanceledException {

						latestVersion = mavenUpdateClient.fetchLatestVersion();
                        logger.info("Fetching latest version " + latestVersion);
						if(isUpdateAvailableToDownload()){
							// Download it...
                            logger.info("Downloading version "+latestVersion +" ...");
							File updateFile = mavenUpdateClient.fetchUpdate(latestVersion);
                            logger.info("Download completed: " + updateFile.toString());
							updateInstallAvailableEvent.fireEvent(this, EventArgs.Empty);
						}
						downloadUpdateTask = null;
						return null;
					}
				};
				new Thread(downloadUpdateTask).run();
			}
			return downloadUpdateTask;
		}
	}

	public boolean isDownloading(){
		synchronized (downloadUpdateTaskLock) {
            CancelableTask<Void> task = downloadUpdateTask;
			return task != null && task.getState() == TaskState.RUNNING;
		}
	}

	@Override
	public void installAndRestart() {
		if(isUpdateReadyToInstall()){
            MavenVersion latestReadyUpdate = mavenUpdateClient.getLatestCachedUpdate();
            File update = mavenUpdateClient.fetchCachedUpdate(latestReadyUpdate);

            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();

            logger.debug("Current Install locatino: " + url);

            // Remove current running .exe / jar / .app

            // Move (and unpack if necessary) update file to previous file

            // Restart

        }
	}


	@Override
	public SelfUpdateState getState() {
		MavenVersion runningVersion = getRunningVersion();
		if(runningVersion != null){

			// Check if there is an update ready to download
			if(isUpdateAvailableToDownload()){

				// TODO Check if we are currently downloading an update?
				if(isDownloading())
					return SelfUpdateState.UpdateDownloading;

				return SelfUpdateState.UpdateAvailableForDownload;
			}

			// Check if we have an update ready to install?
			if(isUpdateReadyToInstall()){
				return SelfUpdateState.UpdateAvailableForInstall;
			}
		}
		return SelfUpdateState.Unknown;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	private boolean isUpdateReadyToInstall(){
		MavenVersion runningVersion = getRunningVersion();
		MavenVersion latestReadyUpdate = mavenUpdateClient.getLatestCachedUpdate();
		return runningVersion != null && (latestReadyUpdate != null && latestReadyUpdate.isNewerThan(runningVersion));
	}

	private boolean isUpdateAvailableToDownload(){
		MavenVersion runningVersion = getRunningVersion();
		MavenVersion latestReadyUpdate = mavenUpdateClient.getLatestCachedUpdate();

		if(runningVersion == null) return false;
		if(latestVersion == null) return false;

		if((latestReadyUpdate != null) && latestReadyUpdate.isNewerThan(runningVersion)){
			return latestVersion.isNewerThan(latestReadyUpdate);
		}else{
			return latestVersion.isNewerThan(runningVersion);
		}
	}

	/**
	 * Gets the current version
	 * @return
	 */
	private synchronized MavenVersion getRunningVersion(){
		if(runningVersion == null){
            runningVersion = fetchCurrentVersion();

            // FIXME DEBUG ONLY --->
            if(runningVersion.equals(MavenVersion.INVLAID)){
                try {
                    runningVersion = MavenVersion.parse("0.1.2");
                } catch (VersionFormatException e) {
                    logger.error(e);
                }
            }
            // FIXME < ------------|
		}
		return runningVersion;
	}

    private MavenVersion fetchCurrentVersion(){
        MavenVersion runningVersion = MavenVersion.INVLAID;
        Package pack = Application.class.getPackage();
        String version = pack.getImplementationVersion();
        if(version != null && !version.isEmpty()){
            try {
                runningVersion = MavenVersion.parse(version);
            } catch (VersionFormatException e) {
                e.printStackTrace();
            }
        }
        return runningVersion;
    }

}
