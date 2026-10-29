package storage;

import entity.User;
import entity.Job;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

// holds submitted job while app runs
public final class JobStore {
    private final UserStore users;

    // job id -> job
    // linked so jobs come back in arrival order, priority ties depend on it
    private final HashMap<String, Job> jobs = new LinkedHashMap<String, Job>();

    public JobStore(UserStore users) {
        if (users == null) {
            throw new IllegalArgumentException("An account store is required.");
        }
        this.users = users;
    }

    // same id with different case or spaces counts as the same job
    private String key(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }

    // adds a new pending job owned by the logged in user
    public boolean submitJob(User user, String id, String type, String duration, String deadline, int priority) {
        if (!users.isAuthenticated(user)) {
            return false;
        }
        if (!AccessControl.canSubmitJob(user)) {
            return false;
        }
        if (!InputValidator.validJob(id, type, duration, deadline)) {
            return false;
        }
        if (jobs.containsKey(key(id))) {
            return false;
        }

        double hours = Double.parseDouble(duration.trim());
        Job job = new Job(id.trim(), user.getUsername(), type, hours, deadline, priority, "Pending");
        jobs.put(key(id), job);
        return true;
    }

    // owner can edit a job only while it is still pending
    public boolean updateJob(User user, String id, String type, String duration, String deadline) {
        if (!users.isAuthenticated(user)) {
            return false;
        }
        if (!InputValidator.validJob(id, type, duration, deadline)) {
            return false;
        }

        Job old = jobs.get(key(id));
        if (!AccessControl.canModifyJob(user, old)) {
            return false;
        }
        if (!"Pending".equals(old.getStatus())) {
            return false;
        }

        double hours = Double.parseDouble(duration.trim());
        Job updated = new Job(old.getId(), old.getOwnerId(), type, hours, deadline,
                old.getPriority(), old.getStatus());
        jobs.put(key(id), updated);
        return true;
    }

    // finds one job, null if not found or the user can't see it
    public Job getJob(User user, String id) {
        if (!users.isAuthenticated(user)) {
            return null;
        }
        if (!InputValidator.hasText(id)) {
            return null;
        }

        Job job = jobs.get(key(id));
        if (!AccessControl.canViewJob(user, job)) {
            return null;
        }
        return job;
    }

    // vcc sees every job, a job owner only sees their own
    public ArrayList<Job> getVisibleJobs(User user) {
        ArrayList<Job> visible = new ArrayList<Job>();
        if (!users.isAuthenticated(user)) {
            return visible;
        }

        for (Job job : jobs.values()) {
            if (AccessControl.canViewJob(user, job)) {
                visible.add(job);
            }
        }
        return visible;
    }

    // every job, vcc only
    public ArrayList<Job> getAllJobs(User user) {
        if (!users.isAuthenticated(user)) {
            return new ArrayList<Job>();
        }
        if (!AccessControl.canViewAllRecords(user)) {
            return new ArrayList<Job>();
        }
        return getVisibleJobs(user);
    }
}
