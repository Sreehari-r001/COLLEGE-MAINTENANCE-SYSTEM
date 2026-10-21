
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HodReviewFrame extends JFrame {

    private final String hodEmail;

    private JTable requestTable;
    private DefaultTableModel model;

    private final Color DARK_GREEN = new Color(48, 76, 55);
    private final Color LIGHT_SAGE = new Color(232, 240, 232);
    private final Color BACKGROUND = new Color(247, 249, 246);
    private final Color TEXT_COLOR = new Color(45, 55, 46);
    private final Color BORDER_COLOR = new Color(215, 225, 215);
    private final Color WHITE = Color.WHITE;

    public HodReviewFrame(String email) {
        hodEmail = email;

        setTitle("CampusCare | HOD Review Requests");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // LEFT SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 18, 20, 18)
        );

        JLabel brand = new JLabel("CampusCare");
        brand.setFont(new Font("Arial", Font.BOLD, 25));
        brand.setForeground(WHITE);

        JLabel tagline = new JLabel("Campus Maintenance");
        tagline.setFont(new Font("Arial", Font.PLAIN, 11));
        tagline.setForeground(new Color(210, 225, 210));

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(tagline);
        sidebar.add(Box.createVerticalStrut(40));

        JLabel workspace = new JLabel("WORKSPACE");
        workspace.setFont(new Font("Arial", Font.BOLD, 11));
        workspace.setForeground(new Color(190, 210, 190));

        sidebar.add(workspace);
        sidebar.add(Box.createVerticalStrut(15));

        JButton dashboardButton =
                createSidebarButton("Dashboard", false);
        dashboardButton.addActionListener(e -> goToDashboard());
        sidebar.add(dashboardButton);

        sidebar.add(Box.createVerticalStrut(10));
     
JButton reviewButton =
        createSidebarButton("Review Requests", true);
reviewButton.addActionListener(e -> refreshTable());
sidebar.add(reviewButton);

sidebar.add(Box.createVerticalStrut(10));

JButton reportsButton =
        createSidebarButton("Maintenance Reports", false);
reportsButton.addActionListener(e ->
        new MaintenanceReportFrame()
);
sidebar.add(reportsButton);

sidebar.add(Box.createVerticalGlue());


        JLabel profile = new JLabel("Head of Department");
        profile.setFont(new Font("Arial", Font.BOLD, 13));
        profile.setForeground(WHITE);
        sidebar.add(profile);
        sidebar.add(Box.createVerticalStrut(8));

        JLabel emailLabel = new JLabel(hodEmail);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        emailLabel.setForeground(new Color(215, 230, 215));
        sidebar.add(emailLabel);
        sidebar.add(Box.createVerticalStrut(18));

        JButton logoutButton =
                createSidebarButton("Log Out", false);
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        // MAIN CONTENT
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BACKGROUND);
        content.setBorder(
                BorderFactory.createEmptyBorder(30, 30, 28, 30)
        );

        JLabel title = new JLabel("Review Maintenance Requests");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(TEXT_COLOR);

        JLabel subtitle = new JLabel(
                "Approve, reject, or assign campus maintenance requests."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(115, 125, 115));

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(BACKGROUND);
        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);

        content.add(heading, BorderLayout.NORTH);

        // REQUEST TABLE
        JPanel tablePanel = new JPanel(new BorderLayout(0, 15));
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel tableTitle = new JLabel("Submitted Requests");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {
                "Request ID",
                "Submitted By",
                "Role",
                "Location",
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

        requestTable = new JTable(model);
        requestTable.setRowHeight(38);
        requestTable.setFont(new Font("Arial", Font.PLAIN, 12));
        requestTable.setForeground(TEXT_COLOR);
        requestTable.setBackground(WHITE);
        requestTable.setGridColor(new Color(235, 239, 233));
        requestTable.setShowVerticalLines(false);
        requestTable.setSelectionBackground(LIGHT_SAGE);
        requestTable.setSelectionForeground(DARK_GREEN);
        requestTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        requestTable.setAutoCreateRowSorter(true);

        requestTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        requestTable.getTableHeader().setBackground(LIGHT_SAGE);
        requestTable.getTableHeader().setForeground(DARK_GREEN);
        requestTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );
        requestTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {90, 180, 90, 110, 110, 210, 100};

        for (int i = 0; i < widths.length; i++) {
            requestTable.getColumnModel()
                    .getColumn(i).setPreferredWidth(widths[i]);
        }

        // STATUS CELL COLOURS
        requestTable.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean selected,
                            boolean focus,
                            int row,
                            int column
                    ) {
                        JLabel label = (JLabel)
                                super.getTableCellRendererComponent(
                                        table, value, selected, focus,
                                        row, column
                                );

                        label.setHorizontalAlignment(
                                SwingConstants.CENTER
                        );
                        label.setFont(
                                new Font("Arial", Font.BOLD, 11)
                        );

                        if (!selected) {
                            String status = value == null
                                    ? "" : value.toString();

                            if (status.equalsIgnoreCase("PENDING")) {
                                label.setBackground(
                                        new Color(255, 244, 214)
                                );
                                label.setForeground(
                                        new Color(145, 100, 20)
                                );
                            } else if (
                                    status.equalsIgnoreCase("APPROVED")) {
                                label.setBackground(
                                        new Color(222, 240, 222)
                                );
                                label.setForeground(
                                        new Color(40, 105, 55)
                                );
                            } else if (
                                    status.equalsIgnoreCase("REJECTED")) {
                                label.setBackground(
                                        new Color(250, 225, 225)
                                );
                                label.setForeground(
                                        new Color(160, 45, 45)
                                );
                            } else {
                                label.setBackground(WHITE);
                                label.setForeground(TEXT_COLOR);
                            }
                        }

                        return label;
                    }
                }
        );

        tablePanel.add(
                new JScrollPane(requestTable),
                BorderLayout.CENTER
        );

        // ACTION BUTTONS
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        buttons.setBackground(WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton approveButton = new JButton("Approve Request");
        JButton rejectButton = new JButton("Reject Request");
        JButton assignButton = new JButton("Assign Staff");

        styleSecondaryButton(refreshButton);
        stylePrimaryButton(approveButton);
        styleRejectButton(rejectButton);
        stylePrimaryButton(assignButton);

        refreshButton.addActionListener(e -> refreshTable());
        approveButton.addActionListener(e -> updateStatus("APPROVED"));
        rejectButton.addActionListener(e -> updateStatus("REJECTED"));
        assignButton.addActionListener(e -> assignSelectedRequest());

        buttons.add(refreshButton);
        buttons.add(rejectButton);
        buttons.add(approveButton);
        buttons.add(assignButton);

        tablePanel.add(buttons, BorderLayout.SOUTH);
        content.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(content, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshTable();
        setVisible(true);
    }

    // Load actual requests from MySQL
    private void refreshTable() {
        model.setRowCount(0);

        String sql =
                "SELECT mr.request_id, u.email, u.role, " +
                "mr.department, mr.room, mr.category, " +
                "mr.description, mr.status " +
                "FROM maintenance_requests mr " +
                "JOIN users u ON mr.submitted_by = u.user_id " +
                "ORDER BY mr.request_id DESC";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String status = rs.getString("status");

                if ("SUBMITTED".equalsIgnoreCase(status)) {
                    status = "PENDING";
                }

                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("room"),
                        rs.getString("category"),
                        rs.getString("description"),
                        status
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load requests from MySQL.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Approve or reject a pending request
    private void updateStatus(String newStatus) {
        int selectedRow = requestTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a request first.",
                    "No Request Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow =
                requestTable.convertRowIndexToModel(selectedRow);

        int requestId = Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
        );

        String currentStatus =
                model.getValueAt(modelRow, 6).toString();

        if (!currentStatus.equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only pending requests can be approved or rejected.",
                    "Action Not Allowed",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String action = newStatus.equals("APPROVED")
                ? "approve" : "reject";

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to " + action
                        + " request REQ-" + requestId + "?",
                "Confirm Decision",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE maintenance_requests " +
                "SET status = ? " +
                "WHERE request_id = ? AND status = 'SUBMITTED'";

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Request REQ-" + requestId
                                + " is now " + newStatus + ".",
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The request may have already been updated. "
                                + "Refresh and try again.",
                        "Request Not Updated",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            refreshTable();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update the request status in MySQL.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Assign an approved request to a maintenance staff member
    private void assignSelectedRequest() {
        int selectedRow = requestTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a request first.",
                    "No Request Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int row = requestTable.convertRowIndexToModel(selectedRow);

        int requestId = Integer.parseInt(
                model.getValueAt(row, 0).toString()
        );

        String status = model.getValueAt(row, 6).toString();

        if (!status.equalsIgnoreCase("APPROVED")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only approved requests can be assigned to staff.",
                    "Assignment Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql =
                "SELECT user_id, name, email " +
                "FROM users " +
                "WHERE role = 'MAINTENANCE_STAFF' " +
                "ORDER BY name";

        List<Integer> staffIds = new ArrayList<>();
        List<String> staffLabels = new ArrayList<>();

        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                staffIds.add(rs.getInt("user_id"));

                staffLabels.add(
                        rs.getString("name") + " (" +
                        rs.getString("email") + ")"
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load maintenance staff.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (staffIds.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No maintenance staff accounts were found.",
                    "No Staff Available",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JComboBox<String> staffBox =
                new JComboBox<>(staffLabels.toArray(new String[0]));

        int choice = JOptionPane.showConfirmDialog(
                this,
                staffBox,
                "Assign Request REQ-" + requestId + " to Staff",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        int selectedStaffIndex = staffBox.getSelectedIndex();
        int staffId = staffIds.get(selectedStaffIndex);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Assign request REQ-" + requestId + " to "
                        + staffLabels.get(selectedStaffIndex) + "?",
                "Confirm Assignment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

            int hodId = dao.getUserIdByEmail(hodEmail);

            if (hodId == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not identify the logged-in HOD account.",
                        "Assignment Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            dao.assignStaff(
                    requestId,
                    staffId,
                    hodId,
                    "Assigned by HOD"
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Request REQ-" + requestId
                            + " has been assigned to "
                            + staffLabels.get(selectedStaffIndex) + ".",
                    "Assignment Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshTable();

        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not assign the request.\n"
                            + ex.getMessage(),
                    "Assignment Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Return to the HOD dashboard
    private void goToDashboard() {
        dispose();
        new RoleDashboard("HOD", hodEmail);
    }

    public static int getTotalRequestCount() {
        return getCount(
                "SELECT COUNT(*) FROM maintenance_requests"
        );
    }

    public static int getPendingRequestCount() {
        return getCount(
                "SELECT COUNT(*) FROM maintenance_requests " +
                "WHERE status = 'SUBMITTED'"
        );
    }

    public static int getApprovedRequestCount() {
        return getCount(
                "SELECT COUNT(*) FROM maintenance_requests " +
                "WHERE status = 'APPROVED'"
        );
    }

    public static int getCompletedRequestCount() {
        return getCount(
                "SELECT COUNT(*) FROM maintenance_requests " +
                "WHERE status = 'COMPLETED'"
        );
    }

    private static int getCount(String sql) {
        try (
                Connection con =
                        DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not retrieve request count: "
                            + e.getMessage()
            );
        }

        return 0;
    }

    private JButton createSidebarButton(
            String text,
            boolean active
    ) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(
                active ? new Color(75, 108, 80) : DARK_GREEN
        );
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(200, 42));
        button.setPreferredSize(new Dimension(190, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(DARK_GREEN);
        button.setForeground(WHITE);
        styleButton(button);
    }

    private void styleRejectButton(JButton button) {
        button.setBackground(new Color(165, 65, 65));
        button.setForeground(WHITE);
        styleButton(button);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(LIGHT_SAGE);
        button.setForeground(DARK_GREEN);
        styleButton(button);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(11, 16, 11, 16)
        );
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }
}
