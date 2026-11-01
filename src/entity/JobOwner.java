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

    /**
     * - Submits a new job
     * - Takes an existing job, checks its ownership, and files it into submittedJobs
     */
    public void submitJob(Job job) {
        // Check that job is not null
        if (job == null) {
            return; // the job is empty
        }

        // Check that job.getOwnerId() matches clientId
        if (job.getOwnerId().equals(clientId))
            // Add the job to submittedJobs
            submittedJobs.add(job);

        // TODO: Send the job to the server or VCC (Waiting on team integration)
    }

    /**
     * Cancels an existing job if it's not already completed
     */
    public boolean cancelJob(String jobId) {
        for (Job currentJob : submittedJobs) {
            if (currentJob.getId().equals(jobId)) {
                // Completed jobs cannot be cancelled.
                if (!currentJob.getStatus().equals("Completed")) {
                    currentJob.setStatus("Cancelled");
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Returns the current status of a job
     */
    public String getJobStatus(String jobId) {
        // Search submittedJobs for a matching ID
        for (Job currentJob : submittedJobs) {
            if (currentJob.getId().equals(jobId)) {
                return currentJob.getStatus();
            }
        }
        return null;
    }

    /**
     * Returns the result of a completed job
     * - Finds the job using its ID
     * - Checks if the job is completed
     * - Retrieves the result from secure storage
     */
    public String getJobResult(String jobId)
    {
        for (Job currentJob : submittedJobs)
        {
            if (currentJob.getId().equals(jobId))
            {
                // Completed jobs cannot be cancelled.
                if (currentJob.getStatus().equals("Completed"))
                    return currentJob.getResult();
                else return "Job Incomplete. Try again.";
            }
        } return null; // handles edge case in case the job never finds the ID
    }

}