package vidada.server.services;

import vidada.model.jobs.JobId;
import vidada.model.jobs.JobInfo;
import vidada.model.jobs.JobState;

/**
 * The JobService provides a high level overview for long running tasks.
 * I.e. clients can request progress information for Jobs running on a remote server.
 *
 * The API is designed stateless to support SOA over REST.
 * 
 * @author IsNull
 *
 */
public interface IJobService {

	/**
	 * Creates a new JobId with the given name.
	 * @param name
	 * @return
	 */
	JobId create(String name);

	/**
	 * Gets the current job information for the given job id.
	 * Clients are supposed to poll this information frequently.
	 * 
	 * @param jobId
	 * @return
	 */
	JobInfo pollProgress(JobId jobId);


    /**
     * Add the given progress info to the job
     * @param jobId
     * @param currentTask
     * @param progress
     */
	void notifyProgress(JobId jobId, String currentTask, float progress);

    /**
     * Set the state for the given job
     * @param jobId
     * @param state
     */
	void notifyState(JobId jobId, JobState state);
}
