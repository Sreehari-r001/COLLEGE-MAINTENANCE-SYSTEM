
package ui;

import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame() {
        setTitle("College Maintenance System");
        setSize(450, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Basic Java Swing layout
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 12));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Login as:");

        String[] roles = {
                "Student",
                "Faculty",
                "HOD",
                "Maintenance Staff"
        };

        roleBox = new JComboBox<>(roles);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        panel.add(emailLabel);
        panel.add(emailField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(roleLabel);
        panel.add(roleBox);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
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

            // Open the correct screen for the selected role.
            if ("Student".equalsIgnoreCase(role)
                    || "Faculty".equalsIgnoreCase(role)) {

                new StudentDashboard(email, role);

            } else if ("HOD".equalsIgnoreCase(role)) {

                new HodReviewFrame(email);

            } else if ("Maintenance Staff".equalsIgnoreCase(role)) {

                new MaintenanceStaffFrame(email);

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unknown user role.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
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
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}