package entity;

import java.util.ArrayList;
import java.util.List;

public class VehicleOwner extends User {

    // Unique ID for the vehicle owner
    private String ownerId;

    // Vehicles owned by this user
    private List<Vehicle> vehicles = new ArrayList<>();

    // Constructor
    public VehicleOwner(String ownerId,
                        String username,
                        String password) {
        super(username, password, "VEHICLE_OWNER");
        this.ownerId = ownerId;
    }
// Returns the owner's ID
    public String getOwnerId() {
        return ownerId;
    }

    // Returns the owner's vehicles
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    // Gets information about a vehicle
    public Vehicle getVehicleInfo(String vehicleId) {
        // TODO: Search the vehicle list using vehicleId
        // TODO: Return the matching vehicle
        return null;
    }
  // Registers a new vehicle
    public void registerVehicle(Vehicle vehicle) {
        // TODO: Check if the vehicle is already registered
        // TODO: Add the vehicle to the owner's list
    }

    // Removes a vehicle
    public void removeVehicle(String vehicleId) {
        // TODO: Find the vehicle using its ID
        // TODO: Remove it from the owner's list
    }

    // Updates vehicle availability
    public void updateVehicleAvailability(
            String vehicleId, boolean available) {
        // TODO: Find the vehicle
        // TODO: Update its availability status
    }

    // Erases stored vehicle data
    public void eraseData(String vehicleId) {
        // TODO: Find the vehicle
        // TODO: Remove its stored data safely
    }
}
