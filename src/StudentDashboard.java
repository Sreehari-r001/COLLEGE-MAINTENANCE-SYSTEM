
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private String userEmail;
    private String userRole;

    private final Color DARK_GREEN = new Color(48, 76, 55);
    private final Color SAGE_GREEN = new Color(91, 125, 97);
    private final Color LIGHT_SAGE = new Color(232, 240, 232);
    private final Color BACKGROUND = new Color(247, 249, 246);
    private final Color TEXT_COLOR = new Color(45, 55, 46);
    private final Color MUTED_TEXT = new Color(115, 125, 116);
    private final Color BORDER_COLOR = new Color(220, 228, 219);
    private final Color WHITE = Color.WHITE;

    private JLabel totalValue;
    private JLabel pendingValue;
    private JLabel completedValue;

    private DefaultTableModel tableModel;
    private JTable requestTable;

    private CardLayout contentLayout;
    private JPanel contentPanel;

    public StudentDashboard(String email) {
        this(email, "Student");
    }

    public StudentDashboard(String email, String role) {
        userEmail = email;
        userRole = role;

        setTitle(userRole + " Dashboard - CampusCare");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        root.add(createSidebar(), BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(createOverviewPanel(), "OVERVIEW");
        contentPanel.add(createRequestsPanel(), "REQUESTS");

        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);

        refreshDashboard();
        setVisible(true);
    }

    // ---------------- SIDEBAR ----------------

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBorder(new EmptyBorder(28, 18, 22, 18));

        JLabel brand = new JLabel("  CampusCare");
        brand.setFont(new Font("SansSerif", Font.BOLD, 21));
        brand.setForeground(WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSubtitle = new JLabel("  COLLEGE MAINTENANCE");
        brandSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 9));
        brandSubtitle.setForeground(new Color(202, 221, 202));
        brandSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(brandSubtitle);
        sidebar.add(Box.createVerticalStrut(45));

        JLabel menuLabel = new JLabel("  MAIN MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(new Color(190, 210, 190));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(12));

        JButton overviewButton =
                createSidebarButton("  Overview", true);

        JButton requestsButton =
                createSidebarButton("  My Requests", false);

        JButton submitButton =
                createSidebarButton("  +  Submit Request", false);

        overviewButton.addActionListener(e -> {
            refreshDashboard();
            contentLayout.show(contentPanel, "OVERVIEW");
        });

        requestsButton.addActionListener(e -> {
            refreshDashboard();
            contentLayout.show(contentPanel, "REQUESTS");
        });

        submitButton.addActionListener(e -> submitRequest());

        sidebar.add(overviewButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(requestsButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(submitButton);

        sidebar.add(Box.createVerticalGlue());

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(
                profilePanel, BoxLayout.Y_AXIS
        ));
        profilePanel.setBackground(new Color(58, 88, 64));
        profilePanel.setBorder(new EmptyBorder(13, 12, 13, 8));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(190, 75));

        JLabel profileRole = new JLabel(userRole);
        profileRole.setFont(new Font("SansSerif", Font.BOLD, 13));
        profileRole.setForeground(WHITE);

        JLabel profileEmail = new JLabel(
                shortenEmail(userEmail, 24)
        );
        profileEmail.setFont(new Font("SansSerif", Font.PLAIN, 10));
        profileEmail.setForeground(new Color(213, 227, 213));

        profilePanel.add(profileRole);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(profileEmail);

        sidebar.add(profilePanel);
        sidebar.add(Box.createVerticalStrut(12));

        JButton logoutButton =
                createSidebarButton("  Log Out", false);

        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createSidebarButton(
            String text, boolean selected
    ) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(WHITE);
        button.setBackground(
                selected ? SAGE_GREEN : DARK_GREEN
        );
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(12, 12, 12, 8));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(190, 45));
        button.setPreferredSize(new Dimension(190, 45));

        return button;
    }

    // ---------------- OVERVIEW ----------------

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(
                titlePanel, BoxLayout.Y_AXIS
        ));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Welcome back!");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Here is an overview of your maintenance requests."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_TEXT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(7));
        titlePanel.add(subtitle);

        JButton newRequestButton =
                createPrimaryButton("+ New Request");

        newRequestButton.addActionListener(e -> submitRequest());

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(newRequestButton, BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JPanel statsPanel = new JPanel(
                new GridLayout(1, 3, 15, 0)
        );
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 115)
        );

        totalValue = new JLabel("0");
        pendingValue = new JLabel("0");
        completedValue = new JLabel("0");

        statsPanel.add(createStatCard(
                "Total Requests",
                totalValue,
                "All requests submitted",
                SAGE_GREEN
        ));

        statsPanel.add(createStatCard(
                "Pending Requests",
                pendingValue,
                "Awaiting review",
                new Color(183, 137, 55)
        ));

        statsPanel.add(createStatCard(
                "Completed Requests",
                completedValue,
                "Work marked complete",
                new Color(65, 133, 91)
        ));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(24, 0, 12, 0));

        JLabel recentTitle = new JLabel("My Recent Requests");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        recentTitle.setForeground(DARK_GREEN);

        JButton viewAllButton = createTextButton("View all");

        viewAllButton.addActionListener(e -> {
            refreshDashboard();
            contentLayout.show(contentPanel, "REQUESTS");
        });

        tableHeader.add(recentTitle, BorderLayout.WEST);
        tableHeader.add(viewAllButton, BorderLayout.EAST);

        JScrollPane tableScroll = createTableScrollPane();
        tableScroll.setPreferredSize(new Dimension(600, 260));
        tableScroll.setMinimumSize(new Dimension(300, 160));

        body.add(statsPanel);
        body.add(tableHeader);
        body.add(tableScroll);

        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(
            String title,
            JLabel valueLabel,
            String description,
            Color accent
    ) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(15, 17, 13, 17)
        ));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(MUTED_TEXT);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(4, 18));

        heading.add(titleLabel, BorderLayout.CENTER);
        heading.add(accentBar, BorderLayout.WEST);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 29));
        valueLabel.setForeground(DARK_GREEN);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 10)
        );
        descriptionLabel.setForeground(MUTED_TEXT);

        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(descriptionLabel);

        return card;
    }

    // ---------------- REQUESTS PANEL ----------------

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(
                titlePanel, BoxLayout.Y_AXIS
        ));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("My Maintenance Requests");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Track the status of your submitted requests."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_TEXT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(7));
        titlePanel.add(subtitle);

        JButton newRequestButton =
                createPrimaryButton("+ New Request");

        newRequestButton.addActionListener(e -> submitRequest());

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(newRequestButton, BorderLayout.EAST);

        JScrollPane scrollPane = createTableScrollPane();

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createTableScrollPane() {
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

        requestTable = new JTable(tableModel);
        requestTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        requestTable.setRowHeight(38);
        requestTable.setForeground(TEXT_COLOR);
        requestTable.setBackground(WHITE);
        requestTable.setGridColor(BORDER_COLOR);
        requestTable.setShowVerticalLines(false);
        requestTable.setSelectionBackground(LIGHT_SAGE);
        requestTable.setSelectionForeground(DARK_GREEN);
        requestTable.setFillsViewportHeight(true);
        requestTable.setAutoCreateRowSorter(true);

        requestTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        requestTable.getTableHeader().setForeground(DARK_GREEN);
        requestTable.getTableHeader().setBackground(LIGHT_SAGE);
        requestTable.getTableHeader().setPreferredSize(
                new Dimension(0, 40)
        );
        requestTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        requestTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        requestTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new StatusRenderer());

        requestTable.getColumnModel().getColumn(0)
                .setPreferredWidth(100);
        requestTable.getColumnModel().getColumn(1)
                .setPreferredWidth(130);
        requestTable.getColumnModel().getColumn(2)
                .setPreferredWidth(110);
        requestTable.getColumnModel().getColumn(3)
                .setPreferredWidth(250);
        requestTable.getColumnModel().getColumn(4)
                .setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER_COLOR)
        );
        scrollPane.getViewport().setBackground(WHITE);

        return scrollPane;
    }

    // ---------------- STATUS RENDERER ----------------

    private class StatusRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component =
                    super.getTableCellRendererComponent(
                            table, value, isSelected,
                            hasFocus, row, column
                    );

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 11));

            if (!isSelected) {
                String status = value == null
                        ? ""
                        : value.toString().toUpperCase();

                switch (status) {
                    case "PENDING":
                    case "SUBMITTED":
                        setForeground(new Color(157, 112, 33));
                        setBackground(new Color(255, 247, 225));
                        break;

                    case "APPROVED":
                    case "ASSIGNED":
                        setForeground(new Color(48, 105, 72));
                        setBackground(new Color(230, 244, 233));
                        break;

                    case "IN_PROGRESS":
                        setForeground(new Color(47, 99, 155));
                        setBackground(new Color(229, 240, 253));
                        break;

                    case "COMPLETED":
                        setForeground(new Color(43, 117, 75));
                        setBackground(new Color(220, 243, 226));
                        break;

                    case "REJECTED":
                        setForeground(new Color(165, 54, 54));
                        setBackground(new Color(255, 232, 232));
                        break;

                    default:
                        setForeground(TEXT_COLOR);
                        setBackground(WHITE);
                        break;
                }
            }

            return component;
        }
    }

    // ---------------- SUBMIT REQUEST ----------------

    private void submitRequest() {
        JDialog dialog = new JDialog(
                this, "Submit Maintenance Request", true
        );

        dialog.setSize(540, 620);
        dialog.setMinimumSize(new Dimension(500, 570));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(24, 30, 24, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel heading = new JLabel("New Maintenance Request");
        heading.setFont(new Font("SansSerif", Font.BOLD, 21));
        heading.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Please provide the details of the issue."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED_TEXT);

        JComboBox<String> departmentBox = new JComboBox<>(
                new String[]{
                        "CSE",
                        "ECE",
                        "EEE",
                        "Civil",
                        "Mechanical",
                        "IT",
                        "Other"
                }
        );

        JTextField locationField = new JTextField();
        locationField.setToolTipText(
                "Example: Room 101 or Library"
        );
        styleInput(locationField);

        JComboBox<String> categoryBox = new JComboBox<>(
                new String[]{
                        "Electrical",
                        "Plumbing",
                        "Furniture",
                        "Cleaning",
                        "Other"
                }
        );

        categoryBox.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        categoryBox.setBackground(BACKGROUND);
        categoryBox.setForeground(TEXT_COLOR);
        categoryBox.setPreferredSize(new Dimension(400, 39));

        JTextArea descriptionArea = new JTextArea(5, 25);
        descriptionArea.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setBackground(BACKGROUND);
        descriptionArea.setBorder(new EmptyBorder(8, 9, 8, 9));

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(
                BorderFactory.createLineBorder(BORDER_COLOR)
        );

        int row = 0;

        addFormComponent(form, gbc, row++, heading);
        addFormComponent(form, gbc, row++, subtitle);

        addFormLabel(form, gbc, row++, "Department");
        addFormComponent(form, gbc, row++, departmentBox);

        addFormLabel(
                form, gbc, row++, "Location (room or building)"
        );
        addFormComponent(form, gbc, row++, locationField);

        addFormLabel(form, gbc, row++, "Category");
        addFormComponent(form, gbc, row++, categoryBox);

        addFormLabel(form, gbc, row++, "Describe the problem");
        addFormComponent(form, gbc, row++, descriptionScroll);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        buttonPanel.setOpaque(false);

        JButton cancelButton = createSecondaryButton("Cancel");
        JButton submitButton = createPrimaryButton("Submit Request");

        cancelButton.addActionListener(e -> dialog.dispose());

        submitButton.addActionListener(e -> {
            String department =
                    (String) departmentBox.getSelectedItem();

            String location = locationField.getText().trim();

            String description =
                    descriptionArea.getText().trim();

            String category =
                    (String) categoryBox.getSelectedItem();

            if (location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter both the location and description.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Save the request to MySQL instead of temporary memory.
            MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

            // Find the logged-in user's numeric ID in the users table.
            int userId = dao.getUserIdByEmail(userEmail);

            if (userId == -1) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Your email was not found in the database."
                                + "\nPlease use an email listed in the MySQL users table.",
                        "User Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // The database enum does not include CLEANING, so store it as OTHER.
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

            int databaseRequestId = dao.insertRequest(request);

            if (databaseRequestId == -1) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Could not save your request to MySQL."
                                + "\nPlease check the database connection and try again.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String requestId = "REQ-" + String.format("%03d", databaseRequestId);

            refreshDashboard();

            JOptionPane.showMessageDialog(
                    dialog,
                    "Maintenance request submitted successfully!"
                            + "\n\nRequest ID: " + requestId
                            + "\nDepartment: " + department
                            + "\nStatus: PENDING"
                            + "\n\nYour request is awaiting HOD review.",
                    "Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();
            contentLayout.show(contentPanel, "REQUESTS");
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        addFormComponent(form, gbc, row, buttonPanel);

        dialog.setContentPane(form);
        dialog.getRootPane().setDefaultButton(submitButton);
        dialog.setVisible(true);
    }

    private void addFormLabel(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String text
    ) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        addFormComponent(panel, gbc, row, label);
    }

    private void addFormComponent(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            JComponent component
    ) {
        GridBagConstraints copy =
                (GridBagConstraints) gbc.clone();

        copy.gridy = row;
        copy.weighty = 0;
        copy.insets = new Insets(5, 0, 5, 0);

        if (component instanceof JScrollPane) {
            copy.fill = GridBagConstraints.BOTH;
            copy.weighty = 1;
            component.setPreferredSize(new Dimension(400, 105));
        }

        panel.add(component, copy);
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(TEXT_COLOR);
        field.setBackground(BACKGROUND);
        field.setCaretColor(SAGE_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(9, 10, 9, 10)
        ));
        field.setPreferredSize(new Dimension(400, 39));
    }

    // ---------------- REFRESH DATA ----------------

    private void refreshDashboard() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        int pending = 0;
        int completed = 0;
        int total = 0;

        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        int userId = dao.getUserIdByEmail(userEmail);

        if (userId == -1) {
            // Do not show another user's requests if the email is not found.
            if (totalValue != null) {
                totalValue.setText("0");
                pendingValue.setText("0");
                completedValue.setText("0");
            }
            return;
        }

        // Load this user's requests from MySQL.
        for (RequestRecord request : dao.getRequestsBySubmitter(userId)) {
            total++;

            String status = request.getStatus();

            // The UI calls newly submitted requests PENDING; MySQL stores SUBMITTED.
            if ("SUBMITTED".equalsIgnoreCase(status)) {
                status = "PENDING";
            }

            String requestId = "REQ-" + String.format("%03d", request.getRequestId());

            tableModel.addRow(new Object[]{
                    requestId,
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

        if (totalValue != null) {
            totalValue.setText(String.valueOf(total));
            pendingValue.setText(String.valueOf(pending));
            completedValue.setText(String.valueOf(completed));
        }
    }

    // ---------------- BUTTONS AND HELPERS ----------------

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(SAGE_GREEN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(12, 17, 12, 17));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(DARK_GREEN);
        button.setBackground(LIGHT_SAGE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(SAGE_GREEN);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        return button;
    }

    private String shortenEmail(String email, int maxLength) {
        if (email == null) {
            return "";
        }

        if (email.length() <= maxLength) {
            return email;
        }

        return email.substring(0, maxLength - 3) + "...";
    }

    private void logout() {
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
    }
}