
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

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

    public MaintenanceStaffFrame(String email) {
        this.email = email;

        setTitle("CampusCare | Maintenance Staff");
        setSize(1150, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
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
                "View and manage approved maintenance requests");
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

        // Summary cards
        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(BACKGROUND);

        totalLabel = new JLabel("0");
        activeLabel = new JLabel("0");
        completedLabel = new JLabel("0");

        summaryPanel.add(createSummaryCard(
                "Available Work", totalLabel));
        summaryPanel.add(createSummaryCard(
                "In Progress", activeLabel));
        summaryPanel.add(createSummaryCard(
                "Completed", completedLabel));

        // Requests table
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

        // Action buttons
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

        // Main content
        JPanel centerPanel = new JPanel(
                new BorderLayout(15, 18));
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        main.add(header, BorderLayout.NORTH);
        main.add(centerPanel, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

        refreshWorkTable();
        setVisible(true);
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

    // Show all requests approved by the HOD, in progress, or completed.
    private void refreshWorkTable() {
        model.setRowCount(0);

        for (RequestStore.RequestItem request
                : RequestStore.getRequests()) {

            if (request.status == null) {
                continue;
            }

            String status = request.status.trim().toUpperCase();

            if (status.equals("APPROVED")
                    || status.equals("IN_PROGRESS")
                    || status.equals("COMPLETED")) {

                model.addRow(new Object[]{
                    request.id,
                    request.department,
                    request.category,
                    request.description,
                    status
                });
            }
        }

        updateSummaryCards();
    }

    private void updateSummaryCards() {
        int available = RequestStore.countStatus("APPROVED");
        int inProgress = RequestStore.countStatus("IN_PROGRESS");
        int completed = RequestStore.countStatus("COMPLETED");

        totalLabel.setText(String.valueOf(available));
        activeLabel.setText(String.valueOf(inProgress));
        completedLabel.setText(String.valueOf(completed));
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

        int modelRow = table.convertRowIndexToModel(selectedRow);

        String id = model.getValueAt(modelRow, 0).toString();
        String department = model.getValueAt(modelRow, 1).toString();
        String category = model.getValueAt(modelRow, 2).toString();
        String description = model.getValueAt(modelRow, 3).toString();
        String status = model.getValueAt(modelRow, 4).toString();

        String details =
                "Request ID: " + id
                + "\nDepartment: " + department
                + "\nCategory: " + category
                + "\nDescription: " + description
                + "\nStatus: " + status;

        JOptionPane.showMessageDialog(
                this,
                details,
                "Maintenance Request Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

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

        int modelRow = table.convertRowIndexToModel(selectedRow);
        String requestId = model.getValueAt(modelRow, 0).toString();

        RequestStore.RequestItem request =
                RequestStore.findById(requestId);

        if (request == null) {
            JOptionPane.showMessageDialog(
                    this, "Request not found.");
            refreshWorkTable();
            return;
        }

        String nextStatus;

        if ("APPROVED".equalsIgnoreCase(request.status)) {
            nextStatus = "IN_PROGRESS";
        } else if ("IN_PROGRESS".equalsIgnoreCase(request.status)) {
            nextStatus = "COMPLETED";
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This request is already completed.",
                    "No Further Update",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Change request " + request.id
                        + " to " + nextStatus + "?",
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        request.status = nextStatus;

        refreshWorkTable();

        JOptionPane.showMessageDialog(
                this,
                "Request " + request.id
                        + " updated to " + nextStatus + ".");
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

            if (!isSelected && value != null) {
                String status = value.toString();

                switch (status) {
                    case "APPROVED":
                        component.setBackground(
                                new Color(225, 239, 225));
                        component.setForeground(
                                new Color(45, 105, 60));
                        break;

                    case "IN_PROGRESS":
                        component.setBackground(
                                new Color(255, 241, 205));
                        component.setForeground(
                                new Color(145, 100, 20));
                        break;

                    case "COMPLETED":
                        component.setBackground(
                                new Color(220, 235, 245));
                        component.setForeground(
                                new Color(40, 90, 130));
                        break;

                    default:
                        component.setBackground(WHITE);
                        component.setForeground(TEXT);
                }
            } else if (isSelected) {
                component.setBackground(LIGHT_GREEN);
                component.setForeground(TEXT);
            }

            return component;
        }
    }
}
