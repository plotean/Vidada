package vidada.server.services;


import vidada.model.jobs.Job;
import vidada.model.jobs.JobServiceProgressListener;
import vidada.model.media.store.local.IMediaImportStrategy;
import vidada.model.media.store.local.MediaImportStrategy;
import vidada.server.VidadaServer;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;

public class MediaImportService extends VidadaServerService implements IMediaImportService {

	private final Object importLock = new Object();
	private Thread imporThread = null;
	private volatile Job currentImportJob;

	public MediaImportService(VidadaServer server) {
		super(server);
	}

	@Override
	public Job synchronizeAll() {

		synchronized ( importLock ) {

			if(currentImportJob == null){

				final IJobService jobService = getServer().getJobService();
				currentImportJob = jobService.create("Synchronize and importing all libraries...");

				Runnable importTask = new Runnable() {
					@Override
					public void run() {

						// The asynchronous import task  runs 
						// also as unit of work

						runUnitOfWork(new Runnable() {

							@Override
							public void run() {

								final IMediaImportStrategy importStrategy = new MediaImportStrategy(
										getServer().getMediaService(),
										getServer().getTagService(),
										getServer().getLibraryService());

								IProgressListener progressListener = new JobServiceProgressListener(jobService, currentImportJob);

								try{
									importStrategy.scanAndUpdateDatabases(progressListener);
									progressListener.currentProgress(ProgressEventArgs.COMPLETED);
								}catch(Exception e){
									progressListener.currentProgress(ProgressEventArgs.FAILED);
								}finally{
									currentImportJob = null;
								}
							}
						});
					}
				};

				imporThread = new Thread(importTask);
				imporThread.start();

				return currentImportJob;

			}else {
				System.err.println("Import is already running, returning current job.");
				return currentImportJob;
			}
		}
	}

}
