package entity;

import java.time.Duration;
import java.time.LocalDateTime;

// A job submitted by a client: type, duration, deadline, and its lifecycle status (queued -> completed/cancelled).
public class Job {

    public enum JobType {TRANSLATION, THUMBNAIL}
    public enum Status { QUEUED, ASSIGNED, RUNNING, COMPLETED, CANCELLED}

    private final String jobOwnerId;
    private final String jobId;
    private final JobType jobType;
    private final int estimatedDurationMinutes;

    private final LocalDateTime deadline;
    private final String inputFilePath;
    private Status status;

    private String assignedVehicleId;
    private LocalDateTime completionTime;

    public Job(String jobOwnerId, String jobId, JobType jobType, int estimatedDurationMinutes, LocalDateTime deadline, String inputFilePath) {

        if (jobOwnerId == null || jobOwnerId.isBlank())
            throw new IllegalArgumentException("jobOwnerId is required");

        if (jobId == null || jobId.isBlank())
            throw new IllegalArgumentException("jobId is required");

        if (estimatedDurationMinutes <= 0)
            throw new IllegalArgumentException("estimatedDurationMinutes is required");

        this.jobOwnerId = jobOwnerId;
        this.jobId = jobId;
        this.jobType = jobType;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.deadline = deadline;
        this.inputFilePath = inputFilePath;
        this.status = Status.QUEUED;

    }

        public void assignTo(String vehicleId){
            this.assignedVehicleId = vehicleId;
            this.status = Status.ASSIGNED;
        }

        public void markRunning() {
            this.status = Status.RUNNING;

        }

        public void markCompleted() {
            this.status = Status.COMPLETED;
            this.completionTime = LocalDateTime.now();

        }

        public void markCancelled() {
            this.status = Status.CANCELLED;
        }

        public Long getMinutesToCompletion() {
            if (status == Status.RUNNING || status == Status.ASSIGNED) {
                return (long) estimatedDurationMinutes;
            }
            return null;
    }

    public String getJobOwnerId() {
        return jobOwnerId;
    }

    public String getJobId() {
        return jobId;
    }

    public JobType getJobType() {
        return jobType;
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
    public String getInputFilePath() {
        return inputFilePath;
    }
    public Status getStatus() {
        return status;
    }
    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

}
