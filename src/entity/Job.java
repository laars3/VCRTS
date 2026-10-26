
package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Job {

    // Basic job submission information
    private final String id;
    private final String ownerId;
    private final String type;
    private final double duration;
    private final String deadline;

    // Current job status
    private String status;

    // Job processing information
    private int redundancyLevel;
    private double completionPercent;
    private LocalDateTime completionTime;

    // Vehicles assigned to process this job
    private List<Vehicle> processingVehicles = new ArrayList<>();

    // Checkpoints saved during processing
    private List<JobProgressionCheckpoint> checkpoints =
            new ArrayList<>();

    // Constructor
    public Job(String id, String ownerId, String type,
               double duration, String deadline, String status) {

        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
    }

    // Returns the job ID
    public String getId() {
        return id;
    }

    // Returns the job owner's ID
    public String getOwnerId() {
        return ownerId;
    }

    // Returns the job type
    public String getType() {
        return type;
    }

    // Returns expected job duration
    public double getDuration() {
        return duration;
    }

    // Returns the job deadline
    public String getDeadline() {
        return deadline;
    }

    // Returns the current job status
    public String getStatus() {
        return status;
    }

    // Updates the job status
    public void setStatus(String status) {
        this.status = status;
    }

    // Returns the redundancy level
    public int getRedundancyLevel() {
        return redundancyLevel;
    }

    // Sets the redundancy level
    public void setRedundancyLevel(int level) {
        this.redundancyLevel = level;
    }

    // Returns job completion percentage
    public double getCompletionPercent() {
        return completionPercent;
    }

    // Returns completion time
    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    // Returns vehicles assigned to the job
    public List<Vehicle> getProcessingVehicles() {
        return new ArrayList<>(processingVehicles);
    }

    // Returns checkpoints belonging to the job
    public List<JobProgressionCheckpoint> getCheckpoints() {
        return new ArrayList<>(checkpoints);
    }

    // Assigns a vehicle to process the job
    public void assignVehicle(Vehicle vehicle) {
        // TODO: Check if the vehicle is available
        // TODO: Check if the vehicle is already assigned
        // TODO: Add the vehicle to processingVehicles
    }

    // Adds a checkpoint to the job
    public void addCheckpoint(
            JobProgressionCheckpoint checkpoint) {

        // TODO: Check that the checkpoint belongs to this job
        // TODO: Add the checkpoint to checkpoints
    }

    // Updates the progress of the job
    public void updateProgress(double percent) {
        // TODO: Check that percent is between 0 and 100
        // TODO: Update completionPercent
        // TODO: Update status when needed
    }

    // Marks the job as completed
    public void onCompletion() {
        // TODO: Set completionPercent to 100
        // TODO: Record completionTime
        // TODO: Change status to Completed
        // TODO: Keep private results in secure storage
    }

    // Marks the job for deletion
    public void markForDeletion() {
        // TODO: Check if the job can be deleted
        // TODO: Update the job status
        // TODO: Request removal of stored job data
    }
}
