package entity;

import java.util.ArrayList;
import java.util.List;

// A client who submits computational jobs; tracks the IDs of the jobs they've submitted.
public class JobOwner extends User {

    private final List<String> jobIds = new ArrayList<>();

    public JobOwner(String userId, String name, String email){
        super(userId, name, email);
    }

    @Override
    public String getRole(){
        return "JOB_OWNER";
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public void addJobId(String jobId) {
        jobIds.add(jobId);
    }

}
