public class Main {
    public static void main(String[] args) {
        Student s1 = new Student(
            "Rahul", "rahulcollege@gmail.com", "CSE"
        );
        Faculty f1 = new Faculty(
            "Sam", "sam@college.edu", "CSE"
        );
        HOD h1 = new HOD("Meera", "meera@college.edu");
        MaintenanceStaff m1 = new MaintenanceStaff(
        "Arun", "arun@college.edu", "Electrical"
        );

         MaintenanceRequest r1 = new MaintenanceRequest(
        101,"CSE","204","Electrical","Fan is not working"
        );
       

        System.out.println("STUDENT DETAILS");
        s1.displayDetails();
        System.out.println("\nFACULTY DETAILS");
        f1.displayDetails();
        System.out.println("\nHOD DETAILS");
        h1.displayDetails();

        
        System.out.println("\nMAINTENANCE STAFF DETAILS");
        m1.displayStaffDetails();
        
System.out.println("\nBEFORE ASSIGNMENT");
r1.displayRequest();

System.out.println("\nAPPROVING COMPLAINT");
h1.approveComplaint(r1);

System.out.println("\nASSIGNING STAFF");
h1.assignComplaint(r1, "Arun");

System.out.println("\nUPDATED COMPLAINT");
r1.displayRequest();

System.out.println("\nSTARTING MAINTENANCE WORK");
m1.startWork(r1);
r1.displayRequest();

System.out.println("\nCOMPLETING MAINTENANCE WORK");
m1.completeWork(r1);
r1.displayRequest();

    }
}