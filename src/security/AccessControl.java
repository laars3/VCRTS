package security;

import entity.User;
import entity.Vehicle;
import entity.Job;
import entity.JobOwner;

// Stores check the login too; these methods only check roles and ownership.
public final class AccessControl {
    private AccessControl() { }
    public static boolean canRegisterVehicle(User user) {
        return user != null && "VEHICLE_OWNER".equals(user.getRole());
    }
    public static boolean canSubmitJob(User user) {
        return user instanceof JobOwner
                && "JOB_OWNER".equals(user.getRole());
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
        if (!canSubmitJob(user) || job == null) return false;

        JobOwner owner = (JobOwner) user;
        String ownerId = job.getOwnerId();
        return ownerId != null
                && (ownerId.equalsIgnoreCase(owner.getClientId())
                || ownerId.equalsIgnoreCase(user.getUsername()));
    }
    public static boolean canViewJob(User user, Job job) {
        return job != null && (canViewAllRecords(user) || canModifyJob(user, job));
    }
}
