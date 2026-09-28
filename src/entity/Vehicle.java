package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

// A vehicle registered by an owner: its specs, residency window, computing power, and the job it's currently running.
public class Vehicle {

    public enum ComputingPowerCategory {LOW, MEDIUM, HIGH}
    public enum Status {AVAILABLE, RENTED, COMPUTING, UNAVAILABLE}

    private final String ownerId;
    private final String vehicleId;

    private final String make;
    private final String model;
    private final int year;
    private LocalDateTime arrivalTime;
    private LocalDateTime estimatedDepartureTime;
    private double computingPower;
    private Status status;
    private Job currentJob;

    public Vehicle(String ownerId, String vehicleId, String make, String model, int year, LocalDateTime arrivalTime, LocalDateTime estimatedDepartureTime, double computingPower) {

        if(ownerId == null || ownerId.isBlank())
            throw new IllegalArgumentException("ownerId is required");


        if(vehicleId == null || vehicleId.isBlank())
            throw new IllegalArgumentException("vehicleId is required");

        if(year < 1900)
            throw new IllegalArgumentException("Invalid year: " + year);

        if(computingPower < 0)
            throw new IllegalArgumentException("ComputingPower cannot be negative");

        if (arrivalTime != null && estimatedDepartureTime != null
                && estimatedDepartureTime.isBefore(arrivalTime))
            throw new IllegalArgumentException("departure cannot be before arrival");


            this.ownerId = ownerId;
            this.vehicleId = vehicleId;
            this.make = make;
            this.model = model;
            this.year = year;
            this.arrivalTime = arrivalTime;
            this.estimatedDepartureTime = estimatedDepartureTime;
            this.computingPower = computingPower;
            this.status = Status.AVAILABLE;
    }

    //Spec #8: Categorizing vehicles by available computing power

    public ComputingPowerCategory getComputingPowerCategory() {
        if(computingPower < 1_000) return ComputingPowerCategory.LOW;
        if(computingPower < 10_000) return ComputingPowerCategory.MEDIUM;
        return ComputingPowerCategory.HIGH;

    }

    //Spec #9: cancel job or give the vehicle back before completion, no compensation

    public void cancelCurrentJob(){
        if(currentJob != null) {
            currentJob.markCancelled();
            currentJob = null;

        }
        status = Status.AVAILABLE;
    }

    public void assignJob(Job job) {
        this.currentJob = job;
        this.status = Status.COMPUTING;
    }

    public Long getMinutesToCompletion(){
        return currentJob == null ? null : currentJob.getMinutesToCompletion();
    }

    public String getOwnerId() {
        return ownerId;
    }
    public String getVehicleId() {
        return vehicleId;
    }

    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;
    }
    public int getYear() {
        return year;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    public LocalDateTime getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }
    public double getComputingPower() {
        return computingPower;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Job getCurrentJob(){
        return currentJob;
    }
    // make get funcs
}