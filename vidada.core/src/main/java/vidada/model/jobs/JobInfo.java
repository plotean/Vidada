package vidada.model.jobs;

import java.util.List;

public class JobInfo {

	private final JobState state;
	private final int progress;
	private final String[] subJobs;

	public JobInfo(JobState state, int progress, List<String> subJobs){
		this.state = state;
		this.progress = progress;
		this.subJobs = subJobs.toArray(new String[0]);
	}

	public JobState getState(){
		return state;
	}

	public int getProgress(){
		return progress;
	}

	public String[] getSubJobs(){
		return subJobs;
	}
}
