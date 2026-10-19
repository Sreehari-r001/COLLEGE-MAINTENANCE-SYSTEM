package model;

public class Faculty extends User {
    private String department;

    public Faculty(String name, String email, String department) {
        super(name, email, "Faculty");
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