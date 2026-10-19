
import javax.swing.*;
import java.awt.*;

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

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel heading = new JLabel("College Maintenance System");
        heading.setFont(new Font("Arial", Font.BOLD, 15));

        emailField = new JTextField();
        passwordField = new JPasswordField();

        String[] roles = {
            "Student",
            "Faculty",
            "HOD",
            "Maintenance Staff"
        };

        roleBox = new JComboBox<>(roles);

        JButton loginButton = new JButton("Login");

        panel.add(heading);
        panel.add(new JLabel(""));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Login as:"));
        panel.add(roleBox);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    
private void login() {
    String email = emailField.getText().trim();
    String password =
        new String(passwordField.getPassword()).trim();

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

    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        JOptionPane.showMessageDialog(
            this,
            "Please enter a valid email address.",
            "Invalid Email",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    dispose();

    if ("Student".equals(role)) {
        new StudentDashboard(email);
    } else {
        new RoleDashboard(role, email);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}