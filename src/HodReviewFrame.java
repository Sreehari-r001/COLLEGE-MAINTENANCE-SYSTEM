
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HodReviewFrame extends JFrame {

    private String hodEmail;
    private JTable requestTable;
    private DefaultTableModel model;

    public HodReviewFrame(String email) {
        hodEmail = email;

        setTitle("HOD - Review Maintenance Requests");
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Review Maintenance Requests");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        mainPanel.add(heading, BorderLayout.NORTH);

        String[] columns = {
            "Request ID",
            "Submitted By",
            "Role",
            "Location",
            "Category",
            "Description",
            "Status"
        };

        Object[][] data = {
            {101, "student1@college.com", "Student",
                "Room 101", "Electrical",
                "Fan not working", "PENDING"},

            {102, "faculty1@college.com", "Faculty",
                "Library", "Furniture",
                "Broken chair", "PENDING"},

            {103, "student2@college.com", "Student",
                "Lab 2", "Plumbing",
                "Leaking tap", "APPROVED"}
        };

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(model);
        requestTable.setRowHeight(28);
        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {80, 165, 80, 100, 100, 180, 100};

        for (int i = 0; i < widths.length; i++) {
            requestTable.getColumnModel()
                .getColumn(i)
                .setPreferredWidth(widths[i]);
        }

        mainPanel.add(
            new JScrollPane(requestTable),
            BorderLayout.CENTER
        );

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton backButton = new JButton("Back");
        JButton logoutButton = new JButton("Logout");

        approveButton.addActionListener(e -> updateStatus("APPROVED"));
        rejectButton.addActionListener(e -> updateStatus("REJECTED"));

        backButton.addActionListener(e -> {
            dispose();
            new RoleDashboard("HOD", hodEmail);
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void updateStatus(String newStatus) {
        int row = requestTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a request first.",
                "No Request Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = requestTable.convertRowIndexToModel(row);
        String currentStatus =
            model.getValueAt(modelRow, 6).toString();

        if (!currentStatus.equals("PENDING")) {
            JOptionPane.showMessageDialog(
                this,
                "Only PENDING requests can be approved or rejected.",
                "Invalid Action",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to mark this request as "
                + newStatus + "?",
            "Confirm Decision",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        model.setValueAt(newStatus, modelRow, 6);

        JOptionPane.showMessageDialog(
            this,
            "Request status updated to " + newStatus + "."
                + "\n\nThis is a UI demonstration only.",
            "Status Updated",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}