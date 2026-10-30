package storage;

import entity.User;
import entity.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

public final class VehicleStore {
    private final UserStore users;
    private final HashMap<String, Vehicle> vehicles =
            new HashMap<String, Vehicle>();

    private static final String FILE_NAME = "vehicles.csv";

    public VehicleStore(UserStore users) {
        if (users == null) {
            throw new IllegalArgumentException(
                    "An account store is required.");
        }
        this.users = users;
        loadVehicles();
    }
    private void loadVehicles() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 7) continue;

                try {
                    String make = parts[0];
                    String model = parts[1];
                    String year = parts[2];
                    String vehicleId = parts[3];
                    String power = parts[4];
                    String ownerId = parts[5];
                    String residency = parts[6];

                    Vehicle v = new Vehicle(
                            make,
                            model,
                            Integer.parseInt(year),
                            vehicleId,
                            Double.parseDouble(power.trim()),
                            ownerId,
                            Double.parseDouble(residency.trim())
                    );

                    v.setVehicleId(vehicleId);
                    v.setAvailability(true);
                    vehicles.put(key(vehicleId), v);

                } catch (NumberFormatException e) {
                    System.err.println("Skipping corrupted data line (invalid numeric format)" + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading vehicles." + e.getMessage());
        }
    }

    private void saveVehicle(Vehicle v) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line = String.join(
                    ",",
                    v.getMake(),
                    v.getModel(),
                    String.valueOf(v.getYear()),
                    v.getVehicleId(),
                    String.valueOf(v.getComputationPower()),
                    v.getOwnerId(),
                    String.valueOf(v.getResidency())
            );

            out.write(line);
            out.newLine();


        } catch (IOException e) {
            System.err.println("Error saving vehicles." + e.getMessage());
        }
    }

        private String key(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }

    public boolean registerVehicle(User user, String id,
            String make, String model, String year,
            String power, String residency) {

        if (!users.isAuthenticated(user)
                || !AccessControl.canRegisterVehicle(user)
                || !InputValidator.validVehicle(
                        id, make, model, year, power, residency)) {
            return false;
        }

        if (vehicles.containsKey(key(id))) return false;

        // Create a real Vehicle instance owned by the logged-in user.
        Vehicle vehicle = new Vehicle(
                make.trim(),
                model.trim(),
                Integer.parseInt(year.trim()),
                id.trim(),
                Double.parseDouble(power.trim()),
                user.getUsername(),
                Double.parseDouble(residency.trim())
        );

        vehicle.setVehicleId(id.trim());
        vehicle.setAvailability(true);

        vehicles.put(key(id), vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean updateVehicle(User user, String id,
            String make, String model, String year,
            String power, String residency) {

        if (!users.isAuthenticated(user)
                || !InputValidator.validVehicle(
                        id, make, model, year, power, residency)) {
            return false;
        }

        Vehicle old = vehicles.get(key(id));

        if (!AccessControl.canModifyVehicle(user, old)) {
            return false;
        }

        Vehicle replacement = new Vehicle(
                make.trim(),
                model.trim(),
                Integer.parseInt(year.trim()),
                old.getLicensePlate(),
                Double.parseDouble(power.trim()),
                old.getOwnerId(),
                Double.parseDouble(residency.trim())
        );

        replacement.setVehicleId(old.getVehicleId());
        replacement.setAvailability(old.isAvailable());
        replacement.setScheduleKnown(old.isScheduleKnown());
        replacement.setArrivalTime(old.getArrivalTime());
        replacement.setDepartureTime(old.getDepartureTime());

        vehicles.put(key(id), replacement);
        return true;
    }

    public Vehicle getVehicle(User user, String id) {
        if (!users.isAuthenticated(user)
                || !InputValidator.hasText(id)) {
            return null;
        }

        Vehicle vehicle = vehicles.get(key(id));

        return AccessControl.canViewVehicle(user, vehicle)
                ? vehicle : null;
    }

    public ArrayList<Vehicle> getVisibleVehicles(User user) {
        ArrayList<Vehicle> visible = new ArrayList<Vehicle>();

        if (!users.isAuthenticated(user)) return visible;

        for (Vehicle vehicle : vehicles.values()) {
            if (AccessControl.canViewVehicle(user, vehicle)) {
                visible.add(vehicle);
            }
        }

        return visible;
    }

    public ArrayList<Vehicle> getAllVehicles(User user) {
        if (!users.isAuthenticated(user)
                || !AccessControl.canViewAllRecords(user)) {
            return new ArrayList<Vehicle>();
        }

        return getVisibleVehicles(user);
    }
}
