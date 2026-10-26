package security;

import entity.User;
import entity.Vehicle;
import entity.Job;

// Stores check the login too; these methods only check roles and ownership.
public final class AccessControl {
    private AccessControl() { }
    public static boolean canRegisterVehicle(User user) {
        return user != null && "VEHICLE_OWNER".equals(user.getRole());
    }
    public static boolean canSubmitJob(User user) {
        return user != null && "JOB_OWNER".equals(user.getRole());
    }
    public static boolean canViewAllRecords(User user) {
        return user != null && "VCC".equals(user.getRole());
    }
    public static boolean canModifyVehicle(User user, Vehicle vehicle) {
        return canRegisterVehicle(user) && vehicle != null
                && user.getUsername().equals(vehicle.getOwnerId());
    }
    public static boolean canViewVehicle(User user, Vehicle vehicle) {
        return vehicle != null && (canViewAllRecords(user) || canModifyVehicle(user, vehicle));
    }
    public static boolean canModifyJob(User user, Job job) {
        return canSubmitJob(user) && job != null && user.getUsername().equals(job.getOwnerId());
    }
    public static boolean canViewJob(User user, Job job) {
        return job != null && (canViewAllRecords(user) || canModifyJob(user, job));
    }
}
