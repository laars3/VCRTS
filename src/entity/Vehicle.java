package entity;

public final class Vehicle {
    private final String make;
    private final String model;
    private final int year;
    private final String licensePlate;
    private final double computationPower;
    private final String ownerId;
    private final double residency;

    // Keeps the old constructor available.
    public Vehicle(String make, String model, int year, String licensePlate) {
        this(make, model, year, licensePlate, 0, null, 0);
    }
    public Vehicle(String make, String model, int year, String licensePlate,
            double computationPower, String ownerId, double residency) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.computationPower = computationPower;
        this.ownerId = ownerId;
        this.residency = residency;
    }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getLicensePlate() { return licensePlate; }
    public double getComputationPower() { return computationPower; }
    public String getOwnerId() { return ownerId; }
    public double getResidency() { return residency; }

    
    // Returns vehicle make
    public String getMake() {
        return make;
    }

    // Returns vehicle model
    public String getModel() {
        return model;
    }

    // Returns vehicle year
    public int getYear() {
        return year;
    }

    // Returns license plate
    public String getLicensePlate() {
        return licensePlate;
    }

    // Returns computation power
    public double getComputationPower() {
        return computationPower;
    }

    // Returns owner ID
    public String getOwnerId() {
        return ownerId;
    }

    // Returns expected residency time
    public double getResidency() {
        return residency;
    }

    // Returns vehicle ID
    public String getVehicleId() {
        return vehicleId;
    }

    // Sets vehicle ID
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    // Checks if vehicle is available
    public boolean isAvailable() {
        return available;
    }

    // Updates vehicle availability
    public void setAvailability(boolean available) {
        this.available = available;
    }

    // Checks if the vehicle schedule is known
    public boolean isScheduleKnown() {
        return scheduleKnown;
    }

    // Updates whether the schedule is known
    public void setScheduleKnown(boolean scheduleKnown) {
        this.scheduleKnown = scheduleKnown;
    }

    // Returns arrival time
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    // Returns departure time
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    // Updates arrival time
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // Updates departure time
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    // Returns jobs assigned to this vehicle
    public List<Job> getProcessingJobs() {
        return new ArrayList<>(processingJobs);
    }

    // Checks if a vehicle can process a job
    public boolean canProcess(Job job) {
        // TODO: Check if the vehicle is available
        // TODO: Check the computation power needed
        // TODO: Check if the vehicle has enough residency time
        // TODO: Return true if the vehicle can process the job
        return false;
    }

    // Assigns a job to the vehicle
    public void assignJob(Job job) {
        // TODO: Check if the job is valid
        // TODO: Check if the vehicle can process the job
        // TODO: Add the job to processingJobs
    }

    // Removes a job from the vehicle
    public void removeJob(String jobId) {
        // TODO: Find the job using its ID
        // TODO: Remove the job from processingJobs
    }
}
