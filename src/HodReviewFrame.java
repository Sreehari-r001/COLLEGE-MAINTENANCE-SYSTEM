
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

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

        // ================= LEFT SIDEBAR =================
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

        // Dashboard button
        JButton dashboardButton =
            createSidebarButton("Dashboard", false);

        dashboardButton.addActionListener(e -> goToDashboard());
        sidebar.add(dashboardButton);

        sidebar.add(Box.createVerticalStrut(10));

        // Current page
        JButton reviewButton =
            createSidebarButton("Review Requests", true);

        reviewButton.addActionListener(e -> refreshTable());
        sidebar.add(reviewButton);

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

        // ================= MAIN CONTENT =================
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BACKGROUND);
        content.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 28, 30)
        );

        JLabel title = new JLabel("Review Maintenance Requests");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(TEXT_COLOR);

        JLabel subtitle = new JLabel(
            "Approve or reject pending campus maintenance requests."
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

        // ================= REQUEST TABLE =================
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

        int[] widths = {90, 130, 75, 110, 110, 210, 100};

        for (int i = 0; i < widths.length; i++) {
            requestTable.getColumnModel()
                .getColumn(i).setPreferredWidth(widths[i]);
        }

        // Status cell colours
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
                            table, value, selected, focus, row, column
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
                            status.equalsIgnoreCase("APPROVED")
                        ) {
                            label.setBackground(
                                new Color(222, 240, 222)
                            );
                            label.setForeground(
                                new Color(40, 105, 55)
                            );
                        } else if (
                            status.equalsIgnoreCase("REJECTED")
                        ) {
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

        // ================= ACTION BUTTONS =================
        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttons.setBackground(WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton approveButton = new JButton("Approve Request");
        JButton rejectButton = new JButton("Reject Request");

        styleSecondaryButton(refreshButton);
        stylePrimaryButton(approveButton);
        styleRejectButton(rejectButton);

        refreshButton.addActionListener(e -> refreshTable());
        approveButton.addActionListener(
            e -> updateStatus("APPROVED")
        );
        rejectButton.addActionListener(
            e -> updateStatus("REJECTED")
        );

        buttons.add(refreshButton);
        buttons.add(rejectButton);
        buttons.add(approveButton);

        tablePanel.add(buttons, BorderLayout.SOUTH);
        content.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(content, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshTable();
        setVisible(true);
    }

    // Load requests from the shared RequestStore
    private void refreshTable() {
        model.setRowCount(0);

        for (RequestStore.RequestItem request
                : RequestStore.getRequests()) {

            model.addRow(new Object[] {
                request.id,
                "-",
                "-",
                request.department,
                request.category,
                request.description,
                request.status
            });
        }
    }

    // Approve or reject the selected request
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

        String requestId =
            model.getValueAt(modelRow, 0).toString();

        RequestStore.RequestItem request =
            RequestStore.findById(requestId);

        if (request == null) {
            JOptionPane.showMessageDialog(
                this,
                "The request could not be found.",
                "Request Not Found",
                JOptionPane.ERROR_MESSAGE
            );

            refreshTable();
            return;
        }

        if (!request.status.equalsIgnoreCase("PENDING")) {
            JOptionPane.showMessageDialog(
                this,
                "Only PENDING requests can be approved or rejected.",
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
                + " request " + request.id + "?",
            "Confirm Decision",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // Update the shared request object
        request.status = newStatus;

        refreshTable();

        JOptionPane.showMessageDialog(
            this,
            "Request " + request.id + " is now " + newStatus + ".",
            "Status Updated",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Return to the HOD dashboard
    private void goToDashboard() {
        dispose();
        new RoleDashboard("HOD", hodEmail);
    }

    public static int getTotalRequestCount() {
        return RequestStore.getTotalCount();
    }

    public static int getPendingRequestCount() {
        return RequestStore.countStatus("PENDING");
    }

    public static int getApprovedRequestCount() {
        return RequestStore.countStatus("APPROVED");
    }

    public static int getCompletedRequestCount() {
        return RequestStore.countStatus("COMPLETED");
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
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

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
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }
}
