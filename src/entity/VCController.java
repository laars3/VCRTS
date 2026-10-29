package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VCController extends User{

 private String controllerId;
    private final List<Job> activeJobs = new ArrayList<>();
    private final List<Job> completedJobs = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private Server server;

// Constructor
    public VCController(String controllerId,String username, String password) {

    super(username, password, "VCC");
      this.controllerId = controllerId;
    }

 // Returns the controller's ID.
    public String getControllerId() {
        return controllerId;
    }

 // Returns active jobs.
    public List<Job> getActiveJobs() {
        return Collections.unmodifiableList(activeJobs);
    }

 // Returns completed jobs.
    public List<Job> getCompletedJobs() {
        return Collections.unmodifiableList(completedJobs);
    }

// Returns vehicles managed by the controller.
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

// assigns the server.
    public void setServer(Server server){
    this.server = server;
    }

 // Registers a vehicle
    public void registerVehicle(Vehicle vehicle) {
        // TODO: Check if the vehicle is already registered
        // TODO: Add the vehicle to the controller's list
    }

    // Removes a registered vehicle
    public void removeVehicle(String vehicleId) {
        // TODO: Find the vehicle using its ID
        // TODO: Remove the vehicle from the list
    }

    // Updates vehicle availability
    public void updateVehicleAvailability(
            String vehicleId, boolean available) {
        // TODO: Find the vehicle
        // TODO: Update its availability
    }

    // Assigns a job to a vehicle
    public void assignJob(Job job, Vehicle vehicle) {
        // TODO: Check if the vehicle is available
        // TODO: Assign the job to the vehicle
        // TODO: Update the job status
    }

    // sorts jobs by priority, sets each job's wait and completion, returns the average wait
    public double computeCompletionTimes(List<Job> jobs) {
        if (jobs.isEmpty()) {
            return 0;
        }

        List<Job> order = mergeSort(jobs);
        double time = 0;
        double totalWait = 0;

        for (Job job : order) {
            job.setWaitTime(time);
            double completion = time + job.getDuration();
            job.setScheduledCompletion(completion);
            totalWait += time;
            time = completion;
        }

        return totalWait / jobs.size();
    }

    // splits the list in half, sorts each half, merges them
    private List<Job> mergeSort(List<Job> jobs) {
        if (jobs.size() <= 1) {
            return new ArrayList<>(jobs);
        }

        int mid = jobs.size() / 2;
        List<Job> left = mergeSort(new ArrayList<>(jobs.subList(0, mid)));
        List<Job> right = mergeSort(new ArrayList<>(jobs.subList(mid, jobs.size())));

        return merge(left, right);
    }

    // combines two sorted lists into one sorted list, ties stay in order
    private List<Job> merge(List<Job> left, List<Job> right) {
        List<Job> result = new ArrayList<>();
        int i = 0;
        int j = 0;

        // left job wins tie keeping order
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getPriority() <= right.get(j).getPriority()) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }

        return result;
    }

    // Adds a checkpoint to a job
    public void addCheckpoint(
            Job job, JobProgressionCheckpoint checkpoint) {
        // TODO: Check that the checkpoint belongs to the job
        // TODO: Save the checkpoint to the job
    }

    // Finds an available vehicle
    public Vehicle recruitVehicle(int requiredPower) {
        // TODO: Search for available vehicles
        // TODO: Check their computation power
        // TODO: Return a matching vehicle
        return null;
    }

    // Restores a job from a checkpoint
    public void restoreFromCheckpoint(Job job) {
        // TODO: Find the latest checkpoint
        // TODO: Restore the saved job state
        // TODO: Resume processing
    }
}
