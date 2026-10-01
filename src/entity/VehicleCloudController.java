package entity;

// The VCC admin user that manages vehicles and assigns jobs to them.
public class VehicleCloudController extends User {
    public VehicleCloudController(String userId, String name, String email) {
        super(userId, name, email);
    }

    @Override
    public String getRole() {
        return "VCC";
    }

}
