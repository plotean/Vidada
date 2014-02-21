package vidada.services;

import vidada.model.jobs.Job;

public interface IMediaImportService {

	/**
	 * Synchronizes / imports all local media libraries
	 */
	Job synchronizeAll();

}
