package vidada.server.services;

import archimedes.core.threading.IProgressListener;
import vidada.model.jobs.Job;

public interface IMediaImportService {

    /**
     * Synchronizes / imports all local media libraries.
     *
     * @return Returns the current import job.
     */
	Job synchronizeAll();

    /**
     * Synchronizes / imports all local media libraries.
     * Progress is additionally reported to the given listener.
     *
     * @return Returns the current import job.
     */
    Job synchronizeAll(IProgressListener progressListener);

}
