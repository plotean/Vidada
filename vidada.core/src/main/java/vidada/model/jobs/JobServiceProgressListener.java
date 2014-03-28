package vidada.model.jobs;


import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;
import vidada.server.services.IJobService;

public class JobServiceProgressListener implements IProgressListener {

	private final Job job;
	private final IJobService jobService;


	public JobServiceProgressListener(IJobService jobService, Job job){
		this.job = job;
		this.jobService = jobService;
	}


	@Override
	public void currentProgress(ProgressEventArgs progressInfo) {
		if(progressInfo.isRunning()){
			jobService.notifiyProgress(job, progressInfo.getCurrentTask(), progressInfo.getProgressInPercent());
		}else {
			if(progressInfo.hasFailed()){
				jobService.notifyState(job, JobState.Failed);
			}else if(progressInfo.isCompleted()){
				jobService.notifyState(job, JobState.Completed);
			}else {
				throw new NotSupportedException();
			}
		}
	}

}
