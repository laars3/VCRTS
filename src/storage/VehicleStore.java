package storage;

import entity.Vehicle;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// In-memory registry of all registered vehicles, keyed by vehicleId.
public class VehicleStore {
    private static final Map<String, Vehicle> vehicles = new LinkedHashMap<>();

    public static void add(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleId(), vehicle);
    }

    public static Vehicle get(String vehicleId) {
        return vehicles.get(vehicleId);
    }
    public static Collection<Vehicle> getAll() {
        return vehicles.values();
    }


}
