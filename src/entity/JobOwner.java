package entity;

import java.util.ArrayList;
import java.util.List;

public class JobOwner extends User {

    // Unique ID for the job owner
    private String clientId;

    // Jobs submitted by this owner
    private List<Job> submittedJobs = new ArrayList<>();

    // Constructor
    public JobOwner(String clientId, String username,
                    String password) {
        super(username, password, "JOB_OWNER");
        this.clientId = clientId;
    }
}
