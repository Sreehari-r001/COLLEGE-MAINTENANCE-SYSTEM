
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoleDashboard extends JFrame {

    private String role;
    private String email;

    public RoleDashboard(String role, String email) {
        this.role = role;
        this.email = email;

        setTitle(role + " Dashboard - College Maintenance System");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        JLabel title = new JLabel(role + " Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel welcome = new JLabel("Logged in as: " + email);

        header.add(title);
        header.add(welcome);

        mainPanel.add(header, BorderLayout.NORTH);

        // Sample request table
        String[] columns = {
            "Request ID",
            "Location",
            "Category",
            "Status"
        };

        Object[][] data = {
            {101, "Room 101", "Electrical", "PENDING"},
            {102, "Library", "Furniture", "IN_PROGRESS"},
            {103, "Lab 2", "Plumbing", "COMPLETED"}
        };

        DefaultTableModel model =
            new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        mainPanel.add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        // Role-specific navigation buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());

        if (role.equals("Faculty")) {
            JButton requestsButton =
                new JButton("Submit / View My Requests");

            requestsButton.addActionListener(e -> {
                dispose();
                new StudentDashboard(email, "Faculty");
            });

            bottomPanel.add(requestsButton);

        } else if (role.equals("HOD")) {
            JButton reviewButton =
                new JButton("Review Maintenance Requests");

            reviewButton.addActionListener(e -> {
                dispose();
                new HodReviewFrame(email);
            });

            bottomPanel.add(reviewButton);

        } else if (role.equals("Maintenance Staff")) {
            JButton workButton =
                new JButton("View Assigned Work");

            workButton.addActionListener(e -> {
                dispose();
                new MaintenanceStaffFrame(email);
            });

            bottomPanel.add(workButton);
        }

        // Logout with confirmation
        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        bottomPanel.add(logoutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}