package vidada.selfupdate;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.threading.CancelableTask;
import archimedesJ.threading.CancellationTokenSource.CancellationToken;
import archimedesJ.threading.CancellationTokenSource.OperationCanceledException;
import archimedesJ.threading.IAsyncTask;
import archimedesJ.threading.TaskState;
import archimedesJ.util.OSValidator;
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

public class SelfUpdateService implements ISelfUpdateService {

	private EventHandlerEx<EventArgsG<UpdateInformation>> updateAvailableEvent = new EventHandlerEx<EventArgsG<UpdateInformation>>();
	@Override
	public IEvent<EventArgsG<UpdateInformation>> getUpdateAvailableEvent() { return updateAvailableEvent; }

	private EventHandlerEx<EventArgs> updateInstallAvailableEvent = new EventHandlerEx<EventArgs>();
	@Override
	public IEvent<EventArgs> getUpdateInstallAvailableEvent() { return updateInstallAvailableEvent; }




	private File localUpdateCache;
	private MavenAutoUpdateClient mavenUpdateClient;


	private MavenVersion runningVersion = null;
	private MavenVersion latestVersion = null;



	public SelfUpdateService(){

		File appData = OSValidator.defaultAppData();
		localUpdateCache = new File(appData, "Vidada/updates");

		try {
			mavenUpdateClient = new MavenAutoUpdateClient(
					new URI("http://dl.securityvision.ch"),
					"Vidada",
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
			return downloadUpdateTask.getState() == TaskState.RUNNING;
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
			Package pack = Application.class.getPackage();
			String version = pack.getImplementationVersion();
			if(version == null || version.isEmpty()){
				runningVersion = MavenVersion.INVLAID;
			} else
				try {
					runningVersion = MavenVersion.parse(version);
				} catch (VersionFormatException e) {
					runningVersion = MavenVersion.INVLAID;
					e.printStackTrace();
				}
		}
		return runningVersion;
	}

}
