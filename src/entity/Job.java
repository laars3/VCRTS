package entity;

// Only submission metadata belongs here, not private files or results.
public final class Job {
    private final String id;
    private final String ownerId;
    private final String type;
    private final double duration;
    private final String deadline;
    private final String status;
    public Job(String id, String ownerId, String type, double duration, String deadline, String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
    }
    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getType() { return type; }
    public double getDuration() { return duration; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
}
