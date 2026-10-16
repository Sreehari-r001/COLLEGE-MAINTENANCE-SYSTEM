
public class MaintenanceStaff extends User {
    private String assignedSection;

    public MaintenanceStaff(String name, String email, String assignedSection) {
        super(name, email, "Maintenance Staff");
        this.assignedSection = assignedSection;
    }

    public void displayStaffDetails() {
        displayDetails();
        System.out.println("Assigned Section: " + assignedSection);
    }
    
    public void startWork(MaintenanceRequest request) {
        request.updateStatus("In Progress");
        System.out.println("Maintenance work started.");
    }

    public void completeWork(MaintenanceRequest request) {
        request.updateStatus("Completed");
        System.out.println("Maintenance work completed.");
    }

}
