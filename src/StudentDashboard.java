
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {

    private String userEmail;
    private String userRole;

    private ArrayList<MaintenanceRequestUI> myRequests =
            new ArrayList<>();

    public StudentDashboard(String email) {
        this(email, "Student");
    }

    public StudentDashboard(String email, String role) {
        userEmail = email;
        userRole = role;

        setTitle(userRole + " Dashboard - College Maintenance System");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 10, 20)
        );

        JLabel title = new JLabel(userRole + " Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel welcome = new JLabel("Welcome, " + userEmail);

        header.add(title);
        header.add(welcome);

        mainPanel.add(header, BorderLayout.NORTH);

        // Main buttons
        JPanel buttons = new JPanel(new GridLayout(3, 1, 15, 15));
        buttons.setBorder(
                BorderFactory.createEmptyBorder(15, 40, 25, 40)
        );

        JButton submitButton =
                new JButton("Submit Maintenance Request");

        JButton viewButton =
                new JButton("View My Requests");

        JButton logoutButton =
                new JButton("Logout");

        buttons.add(submitButton);
        buttons.add(viewButton);
        buttons.add(logoutButton);

        mainPanel.add(buttons, BorderLayout.CENTER);

        submitButton.addActionListener(e -> submitRequest());
        viewButton.addActionListener(e -> viewRequests());

        // Logout confirmation
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

        add(mainPanel);
        setVisible(true);
    }

    // Submit a maintenance request
    private void submitRequest() {
        JTextField locationField = new JTextField();

        String[] categories = {
                "Electrical",
                "Plumbing",
                "Furniture",
                "Cleaning",
                "Other"
        };

        JComboBox<String> categoryBox =
                new JComboBox<>(categories);

        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));

        form.add(new JLabel("Location (room or building):"));
        form.add(locationField);

        form.add(new JLabel("Category:"));
        form.add(categoryBox);

        form.add(new JLabel("Describe the problem:"));
        form.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Submit Maintenance Request",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = (String) categoryBox.getSelectedItem();

        // Validate required fields
        if (location.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both the location and description.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int requestId = 1001 + myRequests.size();

        MaintenanceRequestUI request = new MaintenanceRequestUI(
                requestId,
                userEmail,
                userRole,
                location,
                category,
                description,
                "PENDING"
        );

        myRequests.add(request);

        JOptionPane.showMessageDialog(
                this,
                "Maintenance request submitted successfully!"
                        + "\n\nRequest ID: " + requestId
                        + "\nStatus: PENDING"
                        + "\n\nYour request is awaiting HOD review.",
                "Request Submitted",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // View requests submitted during this session
    private void viewRequests() {
        if (myRequests.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "You have not submitted any requests yet.",
                    "My Requests",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] columns = {
                "Request ID",
                "Location",
                "Category",
                "Description",
                "Status"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        for (MaintenanceRequestUI request : myRequests) {
            model.addRow(new Object[]{
                    request.id,
                    request.location,
                    request.category,
                    request.description,
                    request.status
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(190);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 180));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "My Maintenance Requests",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // Temporary request model for UI testing
    private static class MaintenanceRequestUI {

        int id;
        String email;
        String role;
        String location;
        String category;
        String description;
        String status;

        MaintenanceRequestUI(
                int id,
                String email,
                String role,
                String location,
                String category,
                String description,
                String status
        ) {
            this.id = id;
            this.email = email;
            this.role = role;
            this.location = location;
            this.category = category;
            this.description = description;
            this.status = status;
        }
    }
}