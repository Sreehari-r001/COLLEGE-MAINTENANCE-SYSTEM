package ui;

import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDashboard extends JFrame {

    private final String role;
    private final String email;

    private JLabel totalValue;
    private JLabel pendingValue;
    private JLabel approvedValue;
    private JLabel completedValue;

    private DefaultTableModel requestModel;

    public RoleDashboard(String role, String email) {
        this.role = role;
        this.email = email;

        setTitle("College Maintenance System - " + role);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshDashboard();
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 10, 15)
        );

        JLabel heading = new JLabel(
                "College Maintenance System - " + role + " Dashboard"
        );
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel userLabel = new JLabel("Logged in as: " + email);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(userLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 10, 15)
        );

        JPanel statistics = new JPanel(new GridLayout(1, 4, 10, 10));
        statistics.setBorder(
                BorderFactory.createTitledBorder("Request Statistics")
        );

        totalValue = new JLabel("0", SwingConstants.CENTER);
        pendingValue = new JLabel("0", SwingConstants.CENTER);
        approvedValue = new JLabel("0", SwingConstants.CENTER);
        completedValue = new JLabel("0", SwingConstants.CENTER);

        statistics.add(createStatPanel("Total Requests", totalValue));
        statistics.add(createStatPanel("Pending", pendingValue));
        statistics.add(createStatPanel("Approved", approvedValue));
        statistics.add(createStatPanel("Completed", completedValue));

        String[] columns = {
                "Request ID", "Department", "Category", "Status"
        };

        requestModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable requestTable = new JTable(requestModel);
        requestTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Recent Maintenance Requests")
        );

        panel.add(statistics, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatPanel(String title, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(5, 10));
        panel.setBorder(
                BorderFactory.createTitledBorder(title)
        );

        value.setFont(new Font("SansSerif", Font.BOLD, 24));

        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton dashboardButton = new JButton("Refresh Dashboard");
        JButton requestsButton = new JButton("My Requests / Role Menu");
        JButton logoutButton = new JButton("Log Out");

        dashboardButton.addActionListener(e -> refreshDashboard());

        requestsButton.addActionListener(e -> openRoleScreen());

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        panel.add(dashboardButton);
        panel.add(requestsButton);
        panel.add(logoutButton);

        return panel;
    }

    private void openRoleScreen() {
        if ("Faculty".equalsIgnoreCase(role)) {
            dispose();
            new StudentDashboard(email, "Faculty");

        } else if ("HOD".equalsIgnoreCase(role)) {
            dispose();
            new HodReviewFrame(email);

        } else if ("Maintenance Staff".equalsIgnoreCase(role)) {
            dispose();
            new MaintenanceStaffFrame(email);

        } else if ("Student".equalsIgnoreCase(role)) {
            dispose();
            new StudentDashboard(email, "Student");
        }
    }

    private void refreshDashboard() {
        String sql =
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status IN ('SUBMITTED', 'PENDING') " +
                "THEN 1 ELSE 0 END) AS pending, " +
                "SUM(CASE WHEN status = 'APPROVED' " +
                "THEN 1 ELSE 0 END) AS approved, " +
                "SUM(CASE WHEN status = 'COMPLETED' " +
                "THEN 1 ELSE 0 END) AS completed " +
                "FROM maintenance_requests";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    totalValue.setText(
                            String.valueOf(result.getInt("total")));
                    pendingValue.setText(
                            String.valueOf(result.getInt("pending")));
                    approvedValue.setText(
                            String.valueOf(result.getInt("approved")));
                    completedValue.setText(
                            String.valueOf(result.getInt("completed")));
                }
            }

            refreshRecentRequests();

        } catch (SQLException ex) {
            showDatabaseError(
                    "Could not load dashboard information.", ex
            );
        }
    }

    private void refreshRecentRequests() {
        requestModel.setRowCount(0);

        String sql =
                "SELECT request_id, department, category, status " +
                "FROM maintenance_requests " +
                "ORDER BY request_id DESC LIMIT 10";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    String status = result.getString("status");

                    if (status != null
                            && status.equalsIgnoreCase("SUBMITTED")) {
                        status = "PENDING";
                    }

                    requestModel.addRow(new Object[]{
                            result.getInt("request_id"),
                            result.getString("department"),
                            result.getString("category"),
                            status
                    });
                }
            }

        } catch (SQLException ex) {
            showDatabaseError(
                    "Could not load recent requests.", ex
            );
        }
    }

    private void showDatabaseError(
            String message, SQLException ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}