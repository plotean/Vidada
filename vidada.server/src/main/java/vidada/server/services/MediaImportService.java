package vidada.server.services;


import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.jobs.JobId;
import vidada.model.jobs.JobServiceProgressListener;
import vidada.model.jobs.JobState;
import vidada.model.media.importer.IMediaImportStrategy;
import vidada.model.media.importer.MediaImportStrategy;
import vidada.server.VidadaServer;

public class MediaImportService extends VidadaServerService implements IMediaImportService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaImportService.class.getName());

	private final Object importLock = new Object();
	private Thread importThread = null;
	private volatile JobId currentImportJobId;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/


    public MediaImportService(VidadaServer server) {
		super(server);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /** {@inheritDoc} */
    @Override
    public JobId synchronizeAll(final IProgressListener userListener) {
        synchronized ( importLock ) {

            if(currentImportJobId == null){

                final IJobService jobService = getServer().getJobService();
                currentImportJobId = jobService.create("MediaLibrary Importer");

                Runnable importTask = () -> runUnitOfWork(() -> {

                    final IMediaImportStrategy importStrategy = new MediaImportStrategy(
                            getServer().getMediaService(),
                            getServer().getTagService(),
                            getServer().getLibraryService().getAllLibraries());

                    final IProgressListener progressListener = new JobServiceProgressListener(jobService, currentImportJobId) {
                        public void currentProgress(ProgressEventArgs progressInfo) {
                            super.currentProgress(progressInfo);
                            if (userListener != null) {
                                userListener.currentProgress(progressInfo);
                            }
                        }
                    };

                    try {
                        importStrategy.synchronize(progressListener);
                        progressListener.currentProgress(ProgressEventArgs.COMPLETED);
                    } catch (Exception e) {
                        progressListener.currentProgress(ProgressEventArgs.FAILED);
                    } finally {
                        currentImportJobId = null;
                    }
                });

                importThread = new Thread(importTask);
                importThread.start();

                jobService.notifyState(currentImportJobId, JobState.Running);

                return currentImportJobId;

            }else {
                logger.info("Import is already running, returning current job.");
                return currentImportJobId;
            }
        }
    }

    /** {@inheritDoc} */
	@Override
	public JobId synchronizeAll() {
        return synchronizeAll(null);
	}

}
