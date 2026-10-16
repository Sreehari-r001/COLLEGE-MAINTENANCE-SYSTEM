
public class Student extends User {
    private String department;

    public Student(String name, String email, String department) {
        super(name, email, "Student");
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void displayDetails() {
        super.displayDetails();
        System.out.println("Department: " + department);
    }
}