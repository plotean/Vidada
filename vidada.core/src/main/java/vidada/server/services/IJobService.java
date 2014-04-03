package vidada.server.services;

import vidada.model.jobs.Job;
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
	 * Creates a new Job with the given name.
	 * @param name
	 * @return
	 */
	Job create(String name);

	/**
	 * Gets the current job information. 
	 * Clients are supposed to poll this information frequently.
	 * 
	 * @param job
	 * @return
	 */
	JobInfo pollProgress(Job job);


	void notifiyProgress(Job job, String currentTask, float progress);

	void notifyState(Job job, JobState state);
}
