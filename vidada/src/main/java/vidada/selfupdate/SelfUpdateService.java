package vidada.selfupdate;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.threading.CancelableTask;
import archimedes.core.threading.CancellationTokenSource.CancellationToken;
import archimedes.core.threading.CancellationTokenSource.OperationCanceledException;
import archimedes.core.threading.IAsyncTask;
import archimedes.core.threading.TaskState;
import archimedes.core.util.OSValidator;
import maven.client.autoupdate.MavenAutoUpdateClient;
import maven.client.autoupdate.MavenVersion;
import maven.client.autoupdate.MavenVersion.VersionFormatException;
import vidada.Application;
import vidada.model.update.SelfUpdateState;
import vidada.model.update.UpdateInformation;
import vidada.services.ISelfUpdateService;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This service manages the self update of Vidada
 *
 */
public class SelfUpdateService implements ISelfUpdateService {

    private File localUpdateCache;
    private MavenAutoUpdateClient mavenUpdateClient;

    private MavenVersion runningVersion = null;
    private MavenVersion latestVersion = null;


	private EventHandlerEx<EventArgsG<UpdateInformation>> updateAvailableEvent = new EventHandlerEx<>();
	@Override
	public IEvent<EventArgsG<UpdateInformation>> getUpdateDownloadAvailableEvent() { return updateAvailableEvent; }

	private EventHandlerEx<EventArgs> updateInstallAvailableEvent = new EventHandlerEx<>();
	@Override
	public IEvent<EventArgs> getUpdateInstallAvailableEvent() { return updateInstallAvailableEvent; }


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
			e.printStackTrace();
		}
	}

	private CancelableTask<UpdateInformation> updateCheckTask;
	private final Object updateCheckTaskLock = new Object();

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
                            System.err.println("Auto-Update: Update-Check: Can not determine the running version, aborting.");
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
						System.out.println("SelfUpdateService: Fetching latest version " + latestVersion);
						if(isUpdateAvailableToDownload()){
							// Download it...
							System.out.println("SelfUpdateService: Downloading version "+latestVersion +" ...");
							File updateFile = mavenUpdateClient.fetchUpdate(latestVersion);
							System.out.println("SelfUpdateService: Download completed: " + updateFile.toString());
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
		throw new NotImplementedException();
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

            // FIXME DEBUG ONLY
            if(runningVersion.equals(MavenVersion.INVLAID)){
                try {
                    runningVersion = MavenVersion.parse("0.1.2");
                } catch (VersionFormatException e) {
                    e.printStackTrace();
                }
            }
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
