package vidada.model.jobs;


import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;
import vidada.server.services.IJobService;

public class JobServiceProgressListener implements IProgressListener {

	private final JobId jobId;
	private final IJobService jobService;


	public JobServiceProgressListener(IJobService jobService, JobId jobId){
		this.jobId = jobId;
		this.jobService = jobService;
	}


	@Override
	public void currentProgress(ProgressEventArgs progressInfo) {
		if(progressInfo.isRunning()){
			jobService.notifyProgress(
                    jobId,
                    progressInfo.getCurrentTask(),
                    progressInfo.getProgressInPercent());
		}else {
			if(progressInfo.hasFailed()){
				jobService.notifyState(jobId, JobState.Failed);
			}else if(progressInfo.isCompleted()){
				jobService.notifyState(jobId, JobState.Completed);
			}else {
				throw new NotSupportedException();
			}
		}
	}

}
