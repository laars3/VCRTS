package storage;

import entity.User;
import entity.Job;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

public final class JobStore {
    private final UserStore users;
    private final HashMap<String, Job> jobs = new HashMap<String, Job>();
    public JobStore(UserStore users) {
        if (users == null) throw new IllegalArgumentException("An account store is required.");
        this.users = users;
    }
    private String key(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }
    public boolean submitJob(User user, String id, String type, String duration, String deadline) {
        if (!users.isAuthenticated(user) || !AccessControl.canSubmitJob(user)
                || !InputValidator.validJob(id, type, duration, deadline)) return false;
        if (jobs.containsKey(key(id))) return false;
        // New jobs start pending and belong to the logged-in user.
        jobs.put(key(id), new Job(id.trim(), user.getUsername(), type,
                Double.parseDouble(duration.trim()), deadline, 1, "Pending"));
        return true;
    }
    public boolean updateJob(User user, String id, String type, String duration, String deadline) {
        if (!users.isAuthenticated(user) || !InputValidator.validJob(id, type, duration, deadline)) return false;
        Job old = jobs.get(key(id));
        if (!AccessControl.canModifyJob(user, old)) return false;
        // Jobs can only be edited before they start.
        if (!"Pending".equals(old.getStatus())) return false;
        jobs.put(key(id), new Job(old.getId(), old.getOwnerId(), type,
                Double.parseDouble(duration.trim()), deadline, old.getPriority(), old.getStatus()));
        return true;
    }
    public Job getJob(User user, String id) {
        if (!users.isAuthenticated(user) || !InputValidator.hasText(id)) return null;
        Job job = jobs.get(key(id));
        return AccessControl.canViewJob(user, job) ? job : null;
    }
    public ArrayList<Job> getVisibleJobs(User user) {
        ArrayList<Job> visible = new ArrayList<Job>();
        if (!users.isAuthenticated(user)) return visible;
        for (Job job : jobs.values()) {
            if (AccessControl.canViewJob(user, job)) visible.add(job);
        }
        return visible;
    }
    public ArrayList<Job> getAllJobs(User user) {
        // These records contain metadata only, so monitoring cannot reveal results.
        if (!users.isAuthenticated(user) || !AccessControl.canViewAllRecords(user)) {
            return new ArrayList<Job>();
        }
        return getVisibleJobs(user);
    }
}
