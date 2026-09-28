package entity;

// Base account for every VCRTS user (id, name, email); subclasses define their role.
public abstract class User {

    private final String userId;
    private String name;
    private String email;


    public User(String userId, String name, String email) {
        if(userId == null || userId.isBlank()){
            throw new IllegalArgumentException("userId is blank");
        }
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public abstract String getRole();
}
