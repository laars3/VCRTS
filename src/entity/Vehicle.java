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
}
