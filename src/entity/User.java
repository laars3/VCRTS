package entity;

public class User{

    // Holds one account's login details and role.
    private String username;
    private String password;
    private String role;

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
}