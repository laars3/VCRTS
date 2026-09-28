package entity;

import java.util.ArrayList;
import java.util.List;

// A user who rents out vehicles; tracks the IDs of the vehicles they've registered.
public class VehicleOwner extends User {
    private final List<String> vehicleIds = new ArrayList<>();

    public VehicleOwner(String userId, String name, String email) {
        super(userId, name, email);

    }

    @Override
    public String getRole() {
        return "VEHICLE_OWNER";
    }

    public List<String> getVehicleIds() {
        return vehicleIds;
    }

    public void addVehicleId(String vehicleId) {
        vehicleIds.add(vehicleId);
    }

}
