package vidada.server.services;

import vidada.model.jobs.JobId;
import vidada.model.jobs.JobInfo;
import vidada.model.jobs.JobState;
import vidada.server.VidadaServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic implementation of a JobService
 */
public class JobService extends VidadaServerService implements IJobService {

    // TODO: We keep all jobs in this map (actually a memory leak)
    private final Map<JobId, JobInfo> jobs = new HashMap<JobId, JobInfo>();

    private AtomicInteger idPool = new AtomicInteger(0);

    public JobService(VidadaServer server) {
        super(server);
    }

    @Override
    public JobId create(String name) {
        JobId jobId = nextJobId();
        JobInfo job = new JobInfo(jobId.getId(), name, JobState.Idle);
        jobs.put(jobId, job);
        return jobId;
    }

    @Override
    public JobInfo pollProgress(JobId jobId) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job = job.clone();
        }
        return job;
    }

    @Override
    public void notifyProgress(JobId jobId, String currentTask, float progress) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job.getSubTasks().add(currentTask);
            job.setProgress(progress);
        }
    }

    @Override
    public void notifyState(JobId jobId, JobState state) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job.setState(state);
        }
    }

    private JobId nextJobId(){
        return new JobId(idPool.incrementAndGet());
    }
}
