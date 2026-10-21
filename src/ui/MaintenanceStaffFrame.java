package ui;
import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaintenanceStaffFrame extends JFrame {

    private static final Color DARK_GREEN = new Color(48, 82, 66);
    private static final Color LIGHT_GREEN = new Color(225, 235, 224);
    private static final Color BACKGROUND = new Color(246, 248, 245);
    private static final Color TEXT = new Color(48, 60, 51);
    private static final Color WHITE = Color.WHITE;

    private final String email;
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel totalLabel;
    private final JLabel activeLabel;
    private final JLabel completedLabel;

    private int staffId = -1;

    public MaintenanceStaffFrame(String email) {
        this.email = email;

        setTitle("CampusCare | Maintenance Staff");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        staffId = getStaffId();

        add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BACKGROUND);
        main.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);

        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Assigned Work");
        heading.setFont(new Font("SansSerif", Font.BOLD, 27));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "View and manage maintenance requests assigned to you");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(105, 115, 105));

        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        JLabel profile = new JLabel("Maintenance Staff");
        profile.setFont(new Font("SansSerif", Font.BOLD, 13));
        profile.setForeground(DARK_GREEN);

        header.add(headingPanel, BorderLayout.WEST);
        header.add(profile, BorderLayout.EAST);

        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(BACKGROUND);

        totalLabel = new JLabel("0");
        activeLabel = new JLabel("0");
        completedLabel = new JLabel("0");

        summaryPanel.add(createSummaryCard(
                "Assigned / Available", totalLabel));
        summaryPanel.add(createSummaryCard(
                "In Progress", activeLabel));
        summaryPanel.add(createSummaryCard(
                "Completed", completedLabel));

        String[] columns = {
                "Request ID",
                "Department",
                "Category",
                "Description",
                "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(LIGHT_GREEN);
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(232, 236, 230));
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(LIGHT_GREEN);
        table.getTableHeader().setForeground(DARK_GREEN);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(4)
                .setCellRenderer(new StatusRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(105);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(115);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(225, 231, 224)));
        scrollPane.getViewport().setBackground(WHITE);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton refreshButton = createButton(
                "Refresh", new Color(100, 115, 100));

        JButton detailsButton = createButton(
                "View Details", new Color(100, 115, 100));

        JButton updateButton = createButton(
                "Update Status", DARK_GREEN);

        refreshButton.addActionListener(e -> refreshWorkTable());
        detailsButton.addActionListener(e -> viewDetails());
        updateButton.addActionListener(e -> updateStatus());

        buttonPanel.add(refreshButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(updateButton);

        JPanel centerPanel = new JPanel(
                new BorderLayout(15, 18));
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        main.add(header, BorderLayout.NORTH);
        main.add(centerPanel, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        if (staffId != -1) {
            refreshWorkTable();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not find a maintenance staff account for:\n"
                            + email,
                    "Staff Account Not Found",
                    JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private int getStaffId() {
        String sql =
                "SELECT user_id FROM users "
                + "WHERE email = ? AND role = 'MAINTENANCE_STAFF'";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt("user_id");
                    }
                }
            }
        } catch (SQLException ex) {
            showDatabaseError(
                    "Could not identify the maintenance staff account.", ex);
        }

        return -1;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(DARK_GREEN);
        sidebar.setPreferredSize(new Dimension(225, 700));
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 18, 20, 18));

        JLabel logo = new JLabel("CampusCare");
        logo.setForeground(WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("MAINTENANCE PANEL");
        subtitle.setForeground(LIGHT_GREEN);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton dashboardButton = createButton(
                "Dashboard", DARK_GREEN);
        dashboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardButton.addActionListener(e -> {
            dispose();
            new RoleDashboard("Maintenance Staff", email);
        });

        JButton workButton = createButton(
                "Assigned Work", DARK_GREEN);
        workButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        workButton.addActionListener(e -> refreshWorkTable());

        JButton logoutButton = createButton(
                "Log Out", DARK_GREEN);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));
        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(workButton);
        sidebar.add(Box.createVerticalGlue());

        JLabel signedIn = new JLabel("Signed in as Staff");
        signedIn.setForeground(WHITE);
        signedIn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signedIn.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(signedIn);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JPanel createSummaryCard(
            String title, JLabel valueLabel) {

        JPanel card = new JPanel();
        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 224)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(
                new Color(105, 115, 105));

        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 27));
        valueLabel.setForeground(DARK_GREEN);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    // Load only work assigned to the logged-in staff member.
    private void refreshWorkTable() {
        if (staffId == -1) {
            return;
        }

        model.setRowCount(0);

        String sql =
                "SELECT request_id, department, category, "
                + "description, status "
                + "FROM maintenance_requests "
                + "WHERE assigned_to = ? "
                + "ORDER BY request_id DESC";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, staffId);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        model.addRow(new Object[]{
                                result.getInt("request_id"),
                                result.getString("department"),
                                result.getString("category"),
                                result.getString("description"),
                                formatStatus(result.getString("status"))
                        });
                    }
                }
            }

            updateSummaryCards();

        } catch (SQLException ex) {
            showDatabaseError("Could not load assigned work.", ex);
        }
    }

    private void updateSummaryCards() {
        if (staffId == -1) {
            return;
        }

        String sql =
                "SELECT "
                + "SUM(CASE WHEN status = 'ASSIGNED' "
                + "THEN 1 ELSE 0 END) AS available, "
                + "SUM(CASE WHEN status = 'IN_PROGRESS' "
                + "THEN 1 ELSE 0 END) AS in_progress, "
                + "SUM(CASE WHEN status = 'COMPLETED' "
                + "THEN 1 ELSE 0 END) AS completed "
                + "FROM maintenance_requests "
                + "WHERE assigned_to = ?";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, staffId);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        totalLabel.setText(
                                String.valueOf(result.getInt("available")));
                        activeLabel.setText(
                                String.valueOf(result.getInt("in_progress")));
                        completedLabel.setText(
                                String.valueOf(result.getInt("completed")));
                    }
                }
            }

        } catch (SQLException ex) {
            showDatabaseError("Could not load work statistics.", ex);
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a request first.",
                    "No Request Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);

        String details =
                "Request ID: " + model.getValueAt(row, 0)
                + "\nDepartment: " + model.getValueAt(row, 1)
                + "\nCategory: " + model.getValueAt(row, 2)
                + "\nDescription: " + model.getValueAt(row, 3)
                + "\nStatus: " + model.getValueAt(row, 4);

        JOptionPane.showMessageDialog(
                this,
                details,
                "Maintenance Request Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Update the request and record its status change in request_history.
    private void updateStatus() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a request first.",
                    "No Request Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);
        int requestId =
                Integer.parseInt(model.getValueAt(row, 0).toString());

        String currentStatus =
                model.getValueAt(row, 4).toString()
                        .toUpperCase().replace(' ', '_');

        String nextStatus;

        if (currentStatus.equals("ASSIGNED")) {
            nextStatus = "IN_PROGRESS";
        } else if (currentStatus.equals("IN_PROGRESS")) {
            nextStatus = "COMPLETED";
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Only assigned work or work in progress "
                            + "can be updated.",
                    "Status Cannot Be Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Change request " + requestId
                        + " to " + nextStatus.replace('_', ' ') + "?",
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection connection = null;

        try {
            connection =
                    DatabaseConnection.getInstance().getConnection();

            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                String updateSql =
                        "UPDATE maintenance_requests "
                        + "SET status = ? "
                        + "WHERE request_id = ? "
                        + "AND assigned_to = ? AND status = ?";

                int updated;

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSql)) {

                    statement.setString(1, nextStatus);
                    statement.setInt(2, requestId);
                    statement.setInt(3, staffId);
                    statement.setString(4, currentStatus);

                    updated = statement.executeUpdate();
                }

                if (updated == 0) {
                    connection.rollback();
                    connection.setAutoCommit(originalAutoCommit);

                    JOptionPane.showMessageDialog(
                            this,
                            "The request is no longer assigned to you "
                                    + "or its status has changed. Refresh "
                                    + "the table and try again.",
                            "Request Not Updated",
                            JOptionPane.WARNING_MESSAGE);

                    refreshWorkTable();
                    return;
                }

                String historySql =
                        "INSERT INTO request_history "
                        + "(request_id, old_status, new_status, "
                        + "changed_by, remarks) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement statement =
                             connection.prepareStatement(historySql)) {

                    statement.setInt(1, requestId);
                    statement.setString(2, currentStatus);
                    statement.setString(3, nextStatus);
                    statement.setInt(4, staffId);
                    statement.setString(
                            5, "Status updated by maintenance staff");

                    statement.executeUpdate();
                }

                connection.commit();
                connection.setAutoCommit(originalAutoCommit);

            } catch (SQLException ex) {
                connection.rollback();
                connection.setAutoCommit(originalAutoCommit);
                throw ex;
            }

            refreshWorkTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Request " + requestId
                            + " updated to "
                            + nextStatus.replace('_', ' ') + ".");

        } catch (SQLException ex) {
            showDatabaseError(
                    "Could not update the request status.", ex);
        }
    }

    private String formatStatus(String status) {
        if (status == null) {
            return "UNKNOWN";
        }

        return status.replace('_', ' ');
    }

    private void showDatabaseError(String message, SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\nCheck your database connection.\n\n"
                        + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component component =
                    super.getTableCellRendererComponent(
                            table, value, isSelected,
                            hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 11));

            if (isSelected) {
                component.setBackground(LIGHT_GREEN);
                component.setForeground(TEXT);
            } else {
                setBackground(WHITE);
                setForeground(TEXT);

                if (value != null) {
                    switch (value.toString()) {
                        case "ASSIGNED":
                            setBackground(new Color(225, 239, 225));
                            setForeground(new Color(45, 105, 60));
                            break;

                        case "IN PROGRESS":
                            setBackground(new Color(255, 241, 205));
                            setForeground(new Color(145, 100, 20));
                            break;

                        case "COMPLETED":
                            setBackground(new Color(220, 235, 245));
                            setForeground(new Color(40, 90, 130));
                            break;

                        default:
                            break;
                    }
                }
            }

            return component;
        }
    }
}
