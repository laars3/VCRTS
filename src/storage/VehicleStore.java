package storage;

import entity.User;
import entity.Vehicle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

public final class VehicleStore {
    private final UserStore users;
    private final HashMap<String, Vehicle> vehicles = new HashMap<String, Vehicle>();
    public VehicleStore(UserStore users) {
        if (users == null) throw new IllegalArgumentException("An account store is required.");
        this.users = users;
    }
    private String key(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }
    public boolean registerVehicle(User user, String id, String make, String model,
            String year, String power, String residency) {
        if (!users.isAuthenticated(user) || !AccessControl.canRegisterVehicle(user)
                || !InputValidator.validVehicle(id, make, model, year, power, residency)) return false;
        if (vehicles.containsKey(key(id))) return false;
        // The owner comes from the login, never from a form field.
        vehicles.put(key(id), new Vehicle(make.trim(), model.trim(), Integer.parseInt(year.trim()),
                id.trim(), Double.parseDouble(power.trim()), user.getUsername(), Double.parseDouble(residency.trim())));
        return true;
    }
    public boolean updateVehicle(User user, String id, String make, String model,
            String year, String power, String residency) {
        if (!users.isAuthenticated(user)
                || !InputValidator.validVehicle(id, make, model, year, power, residency)) return false;
        Vehicle old = vehicles.get(key(id));
        if (!AccessControl.canModifyVehicle(user, old)) return false;
        // Replace the record only after all checks pass.
        vehicles.put(key(id), new Vehicle(make.trim(), model.trim(), Integer.parseInt(year.trim()),
                old.getLicensePlate(), Double.parseDouble(power.trim()), old.getOwnerId(), Double.parseDouble(residency.trim())));
        return true;
    }
    public Vehicle getVehicle(User user, String id) {
        if (!users.isAuthenticated(user) || !InputValidator.hasText(id)) return null;
        Vehicle vehicle = vehicles.get(key(id));
        return AccessControl.canViewVehicle(user, vehicle) ? vehicle : null;
    }
    public ArrayList<Vehicle> getVisibleVehicles(User user) {
        ArrayList<Vehicle> visible = new ArrayList<Vehicle>();
        if (!users.isAuthenticated(user)) return visible;
        for (Vehicle vehicle : vehicles.values()) {
            if (AccessControl.canViewVehicle(user, vehicle)) visible.add(vehicle);
        }
        return visible;
    }
    public ArrayList<Vehicle> getAllVehicles(User user) {
        // Only the controller can request all vehicle metadata.
        if (!users.isAuthenticated(user) || !AccessControl.canViewAllRecords(user)) {
            return new ArrayList<Vehicle>();
        }
        return getVisibleVehicles(user);
    }
}
