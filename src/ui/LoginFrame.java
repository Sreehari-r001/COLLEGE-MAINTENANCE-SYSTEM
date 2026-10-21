package ui;
import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private final Color DARK_GREEN = new Color(48, 76, 55);
    private final Color SAGE_GREEN = new Color(91, 125, 97);
    private final Color LIGHT_SAGE = new Color(232, 240, 232);
    private final Color BACKGROUND = new Color(247, 249, 246);
    private final Color TEXT_COLOR = new Color(45, 55, 46);
    private final Color BORDER_COLOR = new Color(215, 225, 215);

    public LoginFrame() {
        setTitle("College Maintenance System");
        setSize(850, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(BACKGROUND);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(
                new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(DARK_GREEN);
        welcomePanel.setBorder(
                new EmptyBorder(55, 35, 45, 30));

        JLabel logo = new JLabel("CAMPUSCARE");
        logo.setFont(new Font("SansSerif", Font.BOLD, 15));
        logo.setForeground(new Color(210, 228, 210));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeTitle = new JLabel("Welcome to");
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel campusCareTitle = new JLabel("CampusCare");
        campusCareTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        campusCareTitle.setForeground(Color.WHITE);
        campusCareTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea welcomeText = new JTextArea(
        "College Maintenance System\n\n"
        + "A simpler way to report, review,\n"
        + "and manage campus maintenance\n"
        + "requests."
);

        welcomeText.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        welcomeText.setForeground(new Color(225, 235, 225));
        welcomeText.setOpaque(false);
        welcomeText.setEditable(false);
        welcomeText.setFocusable(false);
        welcomeText.setBorder(null);
        welcomeText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footer = new JLabel(
                "A better campus starts here.");
        footer.setFont(
                new Font("SansSerif", Font.ITALIC, 12));
        footer.setForeground(new Color(210, 228, 210));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomePanel.add(logo);
        welcomePanel.add(Box.createVerticalStrut(65));
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(campusCareTitle);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeText);
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(footer);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(
                new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
                new EmptyBorder(55, 40, 45, 40));

        JLabel heading = new JLabel("Sign in");
        heading.setFont(
                new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(DARK_GREEN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Enter your details to continue.");
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        passwordField = new JPasswordField();

        String[] roles = {
                "Student",
                "Faculty",
                "HOD",
                "Maintenance Staff"
        };

        roleBox = new JComboBox<>(roles);
        roleBox.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        roleBox.setBackground(Color.WHITE);
        roleBox.setForeground(TEXT_COLOR);
        roleBox.setFocusable(false);

        styleField(emailField);
        styleField(passwordField);

        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(
                new Font("SansSerif", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(SAGE_GREEN);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44));
        loginButton.addActionListener(e -> login());

        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(30));

        addField(formPanel, "Email address", emailField);
        formPanel.add(Box.createVerticalStrut(18));

        addField(formPanel, "Password", passwordField);
        formPanel.add(Box.createVerticalStrut(18));

        addField(formPanel, "Login as", roleBox);
        formPanel.add(Box.createVerticalStrut(30));

        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalGlue());

        JLabel note = new JLabel(
                "College Maintenance System");
        note.setFont(
                new Font("SansSerif", Font.PLAIN, 11));
        note.setForeground(Color.GRAY);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(note);

        mainPanel.add(welcomePanel);
        mainPanel.add(formPanel);

        add(mainPanel);

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(BACKGROUND);
        field.setCaretColor(SAGE_GREEN);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        new EmptyBorder(10, 12, 10, 12)
                ));
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 43));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void addField(
            JPanel panel,
            String labelText,
            JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 43));

        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
        panel.add(field);
    }

    private void login() {
        String email = emailField.getText().trim();

        String password = new String(passwordField.getPassword());

        String role = (String) roleBox.getSelectedItem();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql =
                "SELECT role FROM users "
                        + "WHERE LOWER(email) = LOWER(?) "
                        + "AND password_hash = ?";

        try {
            Connection connection =
                    DatabaseConnection.getInstance().getConnection();

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email);
                statement.setString(2, password);

                try (ResultSet result = statement.executeQuery()) {

                    // Email and password must both match.
                    if (!result.next()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Invalid email or password.",
                                "Login Failed",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    String databaseRole = result.getString("role");

                    // The selected role must also match the account.
                    if (!matchesRole(role, databaseRole)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "This account is registered as "
                                        + databaseRole
                                        + ", not " + role + ".",
                                "Role Mismatch",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                }
            }


            if ("Student".equalsIgnoreCase(role)) {
                new StudentDashboard(email);
            } else {
                new RoleDashboard(role, email);
            }

            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to verify your account in the database.\n"
                            + "Please check the database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean matchesRole(
            String selectedRole, String databaseRole) {

        if (selectedRole == null || databaseRole == null) {
            return false;
        }

        String selected = selectedRole.trim().toUpperCase();
        String registered = databaseRole.trim()
                .toUpperCase()
                .replace('_', ' ');

        return selected.equals(registered);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
