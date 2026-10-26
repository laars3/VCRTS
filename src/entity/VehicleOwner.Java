
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
