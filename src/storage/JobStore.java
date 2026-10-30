package storage;

import entity.User;
import entity.Job;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import security.AccessControl;
import validation.InputValidator;

public final class JobStore {
    private final UserStore users;
    private static final String FILE_NAME = "jobs.csv";

//    public JobStore() {
//        users = new UserStore();
//    }

    private final HashMap<String, Job> jobs = new HashMap<String, Job>();
    public JobStore(UserStore users) {
        if (users == null) throw new IllegalArgumentException("An account store is required.");
        this.users = users;
        loadJobs();
    }
    private String key(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }

    public boolean submitJob(User user, String id, String type, String duration, String deadline) {
        if (!users.isAuthenticated(user) || !AccessControl.canSubmitJob(user)
                || !InputValidator.validJob(id, type, duration, deadline)) return false;
        if (jobs.containsKey(key(id))) return false;
        // New jobs start pending and belong to the logged-in user.

//        jobs.put(key(id), new Job(id.trim(), user.getUsername(), type,
//                Double.parseDouble(duration.trim()), deadline, "Pending"));

        Job job = new Job(id.trim(), user.getUsername(), type,
                Double.parseDouble(duration.trim()), deadline, "Pending");
        jobs.put(key(id), job);
        saveJob(job);
        return true;
    }

    private void loadJobs() {
        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 6) continue;

                try{
                    String id = parts[0].trim();
                    String ownerId = parts[1].trim();
                    String type = parts[2].trim();
                    String duration = parts[3].trim();
                    String deadline = parts[4].trim();
                    String status = parts[5].trim();

                    Job job = new Job(id, ownerId, type, Double.parseDouble(duration), deadline, status);
                    jobs.put(key(id), job);

                } catch (NumberFormatException e) {
                    System.err.println("Skipping corrupted data line (invalid numeric format)" + line);
                }

            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + FILE_NAME + e.getMessage());
        }
    }

    private void saveJob(Job job) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line = String.join(
                    ",",
                    job.getId(),
                    job.getOwnerId(),
                    job.getType(),
                    String.valueOf(job.getDuration()),
                    job.getDeadline(),
                    job.getStatus()
                    );
            out.write(line);
            out.newLine();
        } catch (IOException e) {
           System.err.println("Error writing file: " + FILE_NAME + e.getMessage());
        }
    }


    public boolean updateJob(User user, String id, String type, String duration, String deadline) {
        if (!users.isAuthenticated(user) || !InputValidator.validJob(id, type, duration, deadline)) return false;
        Job old = jobs.get(key(id));
        if (!AccessControl.canModifyJob(user, old)) return false;
        // Jobs can only be edited before they start.
        if (!"Pending".equals(old.getStatus())) return false;
        jobs.put(key(id), new Job(old.getId(), old.getOwnerId(), type,
                Double.parseDouble(duration.trim()), deadline, old.getStatus()));
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
