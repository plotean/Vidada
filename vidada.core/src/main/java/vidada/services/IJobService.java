package vidada.services;

import vidada.model.jobs.Job;
import vidada.model.jobs.JobInfo;
import vidada.model.jobs.JobState;

public interface IJobService {

	/**
	 * Creates a new Job
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
