
public class MaintenanceRequest {
    private int requestId;
    private String department;
    private String room;
    private String category;
    private String description;
    private String status;
    private String assignedStaff;

    public MaintenanceRequest(int requestId, String department, String room,
                              String category, String description) {
        this.requestId = requestId;
        this.department = department;
        this.room = room;
        this.category = category;
        this.description = description;
        this.status = "Submitted";
    }

    public void displayRequest() {
        System.out.println("Request ID: " + requestId);
        System.out.println("Department: " + department);
        System.out.println("Room: " + room);
        System.out.println("Category: " + category);

        if (description == null || description.trim().isEmpty()) {
            System.out.println("Description: Not provided");
        } else {
            System.out.println("Description: " + description);
        }

        System.out.println("Assigned Staff: " +
            (assignedStaff == null ? "Not assigned" : assignedStaff));

        System.out.println("Status: " + status);
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void approveRequest() {
        this.status = "Approved";
    }

    public void rejectRequest() {
        this.status = "Rejected";
    }

    public void assignStaff(String staffName) {
    if (status.equals("Approved")) {
        this.assignedStaff = staffName;
        this.status = "Assigned";
        System.out.println("Staff assigned successfully.");
    } else {
        System.out.println("Only approved requests can be assigned.");
    }
}

    public void startWork() {
        if (status.equals("Assigned")) {
            status = "In Progress";
            System.out.println("Maintenance work started.");
        } else {
            System.out.println("Request must be assigned first.");
        }
    }
}
