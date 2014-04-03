package vidada.server.services;


import vidada.model.jobs.Job;
import vidada.model.jobs.JobServiceProgressListener;
import vidada.model.media.importer.IMediaImportStrategy;
import vidada.model.media.importer.MediaImportStrategy;
import vidada.server.VidadaServer;
import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;

public class MediaImportService extends VidadaServerService implements IMediaImportService {

	private final Object importLock = new Object();
	private Thread importThread = null;
	private volatile Job currentImportJob;

	public MediaImportService(VidadaServer server) {
		super(server);
	}

    /** {@inheritDoc} */
    @Override
    public Job synchronizeAll(final IProgressListener userListener) {
        synchronized ( importLock ) {

            if(currentImportJob == null){

                final IJobService jobService = getServer().getJobService();
                currentImportJob = jobService.create("Synchronize and importing all libraries...");

                Runnable importTask = new Runnable() {
                    @Override
                    public void run() {

                        // The asynchronous import task runs as one unit of work

                        runUnitOfWork(new Runnable() {

                            @Override
                            public void run() {

                                final IMediaImportStrategy importStrategy = new MediaImportStrategy(
                                        getServer().getMediaService(),
                                        getServer().getTagService(),
                                        getServer().getLibraryService().getAllLibraries());

                                final IProgressListener progressListener = new JobServiceProgressListener(jobService, currentImportJob){
                                    public void currentProgress(ProgressEventArgs progressInfo) {
                                        super.currentProgress(progressInfo);
                                        if(userListener != null) {
                                            userListener.currentProgress(progressInfo);
                                        }
                                    }
                                };

                                try{
                                    importStrategy.synchronize(progressListener);
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

                importThread = new Thread(importTask);
                importThread.start();

                return currentImportJob;

            }else {
                System.err.println("Import is already running, returning current job.");
                return currentImportJob;
            }
        }
    }

    /** {@inheritDoc} */
	@Override
	public Job synchronizeAll() {
        return synchronizeAll(null);
	}

}
