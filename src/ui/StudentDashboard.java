
package ui;

import dao.MaintenanceRequestDAO;
import model.RequestRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final String userEmail;
    private final String userRole;

    private DefaultTableModel tableModel;
    private JLabel summaryLabel;

    public StudentDashboard(String email) {
        this(email, "Student");
    }

    public StudentDashboard(String email, String role) {
        userEmail = email;
        userRole = role;

        setTitle(userRole + " Dashboard");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        JLabel heading = new JLabel(
                userRole + " Dashboard - " + userEmail
        );
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(heading, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton submitButton = new JButton("Submit Request");
        JButton refreshButton = new JButton("Refresh Requests");
        JButton logoutButton = new JButton("Logout");

        submitButton.addActionListener(e -> submitRequest());
        refreshButton.addActionListener(e -> refreshDashboard());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(submitButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        String[] columns = {
                "Request ID",
                "Location",
                "Category",
                "Description",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable requestTable = new JTable(tableModel);
        requestTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(requestTable);

        summaryLabel = new JLabel("Loading requests...");

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(summaryLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        refreshDashboard();
        setVisible(true);
    }

    private void submitRequest() {
        JTextField departmentField = new JTextField();
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

        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));

        form.add(new JLabel("Department:"));
        form.add(departmentField);

        form.add(new JLabel("Location (room or building):"));
        form.add(locationField);

        form.add(new JLabel("Category:"));
        form.add(categoryBox);

        form.add(new JLabel("Describe the problem:"));
        form.add(new JScrollPane(descriptionArea));

        int choice = JOptionPane.showConfirmDialog(
                this,
                form,
                "Submit Maintenance Request",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String department = departmentField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = (String) categoryBox.getSelectedItem();

        if (department.isEmpty()
                || location.isEmpty()
                || description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        int userId = dao.getUserIdByEmail(userEmail);

        if (userId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Your email was not found in the database.",
                    "User Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // The database category enum does not include CLEANING.
        String databaseCategory = category.toUpperCase();

        if ("CLEANING".equals(databaseCategory)) {
            databaseCategory = "OTHER";
        }

        RequestRecord request = new RequestRecord(
                userId,
                department,
                location,
                databaseCategory,
                description
        );

        int requestId = dao.insertRequest(request);

        if (requestId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save the request to MySQL.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Request submitted successfully!"
                        + "\nRequest ID: REQ-"
                        + String.format("%03d", requestId)
                        + "\nStatus: PENDING"
                        + "\nYour request is awaiting HOD review.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        refreshDashboard();
    }

    private void refreshDashboard() {
        tableModel.setRowCount(0);

        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        int userId = dao.getUserIdByEmail(userEmail);

        if (userId == -1) {
            summaryLabel.setText(
                    "Could not find your user account."
            );
            return;
        }

        int total = 0;
        int pending = 0;
        int completed = 0;

        for (RequestRecord request :
                dao.getRequestsBySubmitter(userId)) {

            total++;

            String status = request.getStatus();

            if ("SUBMITTED".equalsIgnoreCase(status)) {
                status = "PENDING";
            }

            tableModel.addRow(new Object[]{
                    "REQ-" + String.format(
                            "%03d", request.getRequestId()),
                    request.getRoom(),
                    request.getCategory(),
                    request.getDescription(),
                    status
            });

            if ("PENDING".equalsIgnoreCase(status)) {
                pending++;
            }

            if ("COMPLETED".equalsIgnoreCase(status)) {
                completed++;
            }
        }

        summaryLabel.setText(
                "Total requests: " + total
                        + "    Pending: " + pending
                        + "    Completed: " + completed
        );
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
