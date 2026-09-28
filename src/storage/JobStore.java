package storage;

import entity.Job;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// In-memory registry of all submitted jobs, keyed by jobId.
public class JobStore {
    private static final Map<String, Job> jobs = new LinkedHashMap<>();
    public static void add(Job job){
        jobs.put(job.getJobId(), job);
    }
    public static Job get(String jobId) {
        return jobs.get(jobId);
    }

    public static Collection<Job> all(){
        return jobs.values();
    }


}
