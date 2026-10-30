package storage;

import entity.User;
import entity.Job;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

// holds submitted job while app runs
public final class JobStore {
    private final UserStore users;
    private static final String FILE_NAME = "jobs.csv";

    // job id -> job
    // linked so jobs come back in arrival order, priority ties depend on it
    private final HashMap<String, Job> jobs = new LinkedHashMap<String, Job>();

    public JobStore(UserStore users) {
        if (users == null) {
            throw new IllegalArgumentException("An account store is required.");
        }
        this.users = users;
        loadJobs();
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
        saveJob(job);
        return true;
    }

    // reads jobs.csv: id,owner,type,duration,deadline,priority,status
    // lines are in arrival order, which the LinkedHashMap keeps
    private void loadJobs() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 7) continue;

                try {
                    String id = parts[0].trim();
                    String ownerId = parts[1].trim();
                    String type = parts[2].trim();
                    double duration = Double.parseDouble(parts[3].trim());
                    String deadline = parts[4].trim();
                    int priority = Integer.parseInt(parts[5].trim());
                    String status = parts[6].trim();

                    Job job = new Job(id, ownerId, type, duration, deadline, priority, status);
                    jobs.put(key(id), job);

                } catch (NumberFormatException e) {
                    System.err.println("Skipping corrupted data line (invalid numeric format): " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + FILE_NAME + " " + e.getMessage());
        }
    }

    // adds one job to the end of jobs.csv
    private void saveJob(Job job) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line = String.join(
                    ",",
                    job.getId(),
                    job.getOwnerId(),
                    job.getType(),
                    String.valueOf(job.getDuration()),
                    job.getDeadline(),
                    String.valueOf(job.getPriority()),
                    job.getStatus()
            );
            out.write(line);
            out.newLine();
        } catch (IOException e) {
            System.err.println("Error writing file: " + FILE_NAME + " " + e.getMessage());
        }
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
