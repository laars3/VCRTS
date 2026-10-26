package entity;

import javautil.Arraylist;
import javautil.Collections;
import javautil.List;

/**
* Controls vechiles and jobs in vehicular cloud
* inheirits from user account info from User
*/

public class VVController extends User{

 private String controllerId;
    private final List<Job> activeJobs = new ArrayList<>();
    private final List<Job> completedJobs = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private Server server;

// Constructor
    public VCController(String controllerId,
    String username,
    String password) {
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
