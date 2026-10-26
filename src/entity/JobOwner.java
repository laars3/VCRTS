package entity;

import java.util.ArrayList;
import java.util.List;

public class JobOwner extends User {

    // Unique ID for the job owner
    private String clientId;

    // Jobs submitted by this owner
    private List<Job> submittedJobs = new ArrayList<>();

    // Constructor
    public JobOwner(String clientId, String username,
                    String password) {
        super(username, password, "JOB_OWNER");
        this.clientId = clientId;
    }
        
  // Returns the client ID
    public String getClientId() {
        return clientId;
    }

    // Returns submitted jobs
    public List<Job> getSubmittedJobs() {
        return new ArrayList<>(submittedJobs);
    }

    // Submits a new job
    public void submitJob(Job job) {
        // TODO: Check that job is not null
        // TODO: Check that job.getOwnerId() matches clientId
        // TODO: Add the job to submittedJobs
        // TODO: Send the job to the controller
    }

    // Cancels an existing job
    public void cancelJob(String jobId) {
        // TODO: Find the job using job.getId()
        // TODO: Check if the job can be cancelled
        // TODO: Update the job status to Cancelled
    }

    // Returns the current status of a job
    public String getJobStatus(String jobId) {
        // TODO: Search submittedJobs for a matching ID
        // TODO: Return the matching job's status
        return null;
    }

    // Returns the result of a completed job
    public String getJobResult(String jobId) {
        // TODO: Find the job using its ID
        // TODO: Check if the job is completed
        // TODO: Retrieve the result from secure storage
        return null;
    }
}
