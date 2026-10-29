package entity;

import java.util.ArrayList;
import java.util.List;

public class VehicleOwner extends User {

    // Unique ID for the vehicle owner
    private final String ownerId;

    // List of vehicles owned by this user
    private final List<Vehicle> vehicles = new ArrayList<Vehicle>();

    // Constructor
    public VehicleOwner(String ownerId, String username, String password) {
        super(username, password, "VEHICLE_OWNER");
        this.ownerId = ownerId;
    }

    // Returns the owner's ID
    public String getOwnerId() {
        return ownerId;
    }

    // Returns a copy of the owner's vehicles
    public List<Vehicle> getVehicles() {
        return new ArrayList<Vehicle>(vehicles);
    }

    // Finds a vehicle using its ID
    public Vehicle getVehicleInfo(String vehicleId) {
        if (!hasVehicleId(vehicleId)) {
            return null;
        }

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId() != null
                    && vehicle.getVehicleId().equalsIgnoreCase(vehicleId.trim())) {
                return vehicle;
            }
        }

        return null;
    }

    // Registers a new vehicle
    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null || !hasVehicleId(vehicle.getVehicleId())) {
            return;
        }

        // Check ownership and avoid duplicate vehicles
        if (!belongsToOwner(vehicle)
                || getVehicleInfo(vehicle.getVehicleId()) != null) {
            return;
        }

        vehicles.add(vehicle);
    }

    // Removes a registered vehicle
    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = getVehicleInfo(vehicleId);

        if (vehicle == null) {
            return;
        }

        // Mark the vehicle unavailable before removing it
        vehicle.setAvailability(false);
        vehicles.remove(vehicle);
    }

    // Updates vehicle availability
    public void updateVehicleAvailability(
            String vehicleId, boolean available) {

        Vehicle vehicle = getVehicleInfo(vehicleId);

        if (vehicle != null) {
            vehicle.setAvailability(available);
        }
    }

    // Clears changeable vehicle data and removes its registration
    public void eraseData(String vehicleId) {
        Vehicle vehicle = getVehicleInfo(vehicleId);

        if (vehicle == null) {
            return;
        }

        vehicle.setArrivalTime(null);
        vehicle.setDepartureTime(null);
        vehicle.setScheduleKnown(false);

        removeVehicle(vehicleId);
    }

    // Helper method to validate a vehicle ID
    private boolean hasVehicleId(String vehicleId) {
        return vehicleId != null && !vehicleId.trim().isEmpty();
    }

    // Helper method to check vehicle ownership
    private boolean belongsToOwner(Vehicle vehicle) {
        String vehicleOwnerId = vehicle.getOwnerId();

        if (vehicleOwnerId == null) {
            return false;
        }

        // VehicleStore uses the username as the owner ID
        return vehicleOwnerId.equalsIgnoreCase(getUsername())
                || (ownerId != null
                && vehicleOwnerId.equalsIgnoreCase(ownerId));
    }
}

