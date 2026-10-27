package entity;

public class User{

    // Holds one account's login details and role.
    private String username;
    private String password;
    private String role;

    
    // Unique ID for the user
    private String userId;

    // User's personal information
    private String name;
    private String email;


    public User(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Gets the username.
    public String getUsername(){
        return username;
    }

    // Used to check a login.
    public String getPassword(){
        return password;
    }

    // Gets the account type.
    public String getRole(){
        return role;
    }
    
    // Returns user ID
    public String getUserId() {
        return userId;
    }

    // Sets user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Returns user's name
    public String getName() {
        return name;
    }

    // Updates user's name
    public void setName(String name) {
        this.name = name;
    }

    // Returns email
    public String getEmail() {
        return email;
    }

    // Updates email
    public void setEmail(String email) {
        this.email = email;
    }

}

