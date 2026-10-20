
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

    private final Color darkGreen = new Color(48, 82, 66);
    private final Color sageGreen = new Color(225, 235, 224);
    private final Color background = new Color(246, 248, 245);
    private final Color white = Color.WHITE;
    private final Color textColor = new Color(48, 60, 51);

    private JLabel totalValue;
    private JLabel pendingValue;
    private JLabel approvedValue;
    private JLabel completedValue;

    private DefaultTableModel requestModel;

    public RoleDashboard(String role, String email) {
        this.role = role;
        this.email = email;

        setTitle("CampusCare | " + role + " Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);

        refreshDashboard();
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(darkGreen);
        sidebar.setPreferredSize(new Dimension(225, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 18, 20, 18));

        JLabel logo = new JLabel("CampusCare");
        logo.setFont(new Font("SansSerif", Font.BOLD, 25));
        logo.setForeground(white);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("COLLEGE MAINTENANCE");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(new Color(215, 228, 215));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));

        addSidebarButton(sidebar, "Dashboard", this::showDashboard);

        if (role.equalsIgnoreCase("Faculty")) {
            addSidebarButton(sidebar, "My Requests", () -> {
                dispose();
                new StudentDashboard(email, "Faculty");
            });
        } else if (role.equalsIgnoreCase("HOD")) {
            addSidebarButton(sidebar, "Review Requests", () -> {
                dispose();
                new HodReviewFrame(email);
            });
        } else if (role.equalsIgnoreCase("Maintenance Staff")) {
            addSidebarButton(sidebar, "Assigned Work", () -> {
                dispose();
                new MaintenanceStaffFrame(email);
            });
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel account = new JLabel(
                "<html><b>" + role + "</b><br>" + email + "</html>");
        account.setForeground(white);
        account.setFont(new Font("SansSerif", Font.PLAIN, 12));
        account.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(account);
        sidebar.add(Box.createVerticalStrut(18));

        addSidebarButton(sidebar, "Log Out", () -> {
            dispose();
            new LoginFrame();
        });

        return sidebar;
    }

    private void addSidebarButton(
            JPanel sidebar, String text, Runnable action) {

        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(white);
        button.setBackground(darkGreen);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 43));
        button.setPreferredSize(new Dimension(190, 43));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 5));

        button.addActionListener(e -> action.run());
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(8));
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(background);
        main.setBorder(
                BorderFactory.createEmptyBorder(28, 30, 25, 30));

        JPanel header = new JPanel();
        header.setBackground(background);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 29));
        heading.setForeground(textColor);

        JLabel welcome = new JLabel(
                "Welcome back! Here's your maintenance overview.");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcome.setForeground(new Color(105, 115, 105));

        header.add(heading);
        header.add(Box.createVerticalStrut(7));
        header.add(welcome);

        JPanel content = new JPanel();
        content.setBackground(background);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(background);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        totalValue = new JLabel("0");
        pendingValue = new JLabel("0");
        approvedValue = new JLabel("0");
        completedValue = new JLabel("0");

        cards.add(createStatCard("Total Requests", totalValue));
        cards.add(createStatCard("Pending", pendingValue));
        cards.add(createStatCard("Approved", approvedValue));
        cards.add(createStatCard("Completed", completedValue));

        JPanel tablePanel = createRecentRequestsPanel();

        content.add(cards);
        content.add(Box.createVerticalStrut(25));
        content.add(tablePanel);

        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        return main;
    }

    private JPanel createStatCard(String title, JLabel value) {
        JPanel card = new JPanel();
        card.setBackground(white);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 224)),
                BorderFactory.createEmptyBorder(17, 17, 15, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 110, 100));

        value.setFont(new Font("SansSerif", Font.BOLD, 27));
        value.setForeground(darkGreen);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(value);

        return card;
    }

    private JPanel createRecentRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(white);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Recent Maintenance Requests");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(textColor);

        String[] columns = {
                "Request ID", "Department", "Category", "Status"
        };

        requestModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(requestModel);
        table.setRowHeight(35);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(sageGreen);
        table.getTableHeader().setForeground(darkGreen);
        table.setBackground(white);
        table.setForeground(textColor);
        table.setSelectionBackground(sageGreen);
        table.setSelectionForeground(textColor);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Load request counts from the database.
    private void refreshDashboard() {
        String sql =
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'SUBMITTED' THEN 1 ELSE 0 END) AS pending, " +
                "SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) AS approved, " +
                "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed " +
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
            showDatabaseError("Could not load dashboard statistics.", ex);
        }
    }

    // Load the latest requests rather than the sample RequestStore data.
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

                    requestModel.addRow(new Object[]{
                            result.getInt("request_id"),
                            result.getString("department"),
                            result.getString("category"),
                            formatStatus(status)
                    });
                }
            }

        } catch (SQLException ex) {
            showDatabaseError("Could not load recent requests.", ex);
        }
    }

    private String formatStatus(String status) {
        if (status == null) {
            return "Unknown";
        }

        if (status.equalsIgnoreCase("SUBMITTED")) {
            return "PENDING";
        }

        return status.replace('_', ' ');
    }

    private void showDashboard() {
        refreshDashboard();

        JOptionPane.showMessageDialog(
                this,
                "Dashboard refreshed from the database.",
                "CampusCare",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showDatabaseError(String message, SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\nCheck your database connection.\n\n"
                        + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
