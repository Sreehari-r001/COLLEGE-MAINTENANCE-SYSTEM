package model;
public class HOD extends User {

    public HOD(String name, String email) {
        super(name, email, "HOD");
    }

    public void approveComplaint(MaintenanceRequest request) {
        request.approveRequest();
        System.out.println("Complaint approved by HOD.");
    }

    public void rejectComplaint(MaintenanceRequest request) {
        request.rejectRequest();
        System.out.println("Complaint rejected by HOD.");
    }
    public void assignComplaint(MaintenanceRequest request,
                            String staffName) {
        request.assignStaff(staffName);
        System.out.println("Complaint assigned to " + staffName);
    }
}
