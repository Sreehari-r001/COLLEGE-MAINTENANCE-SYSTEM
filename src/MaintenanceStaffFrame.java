
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MaintenanceStaffFrame extends JFrame {

    private String staffEmail;
    private JTable workTable;
    private DefaultTableModel model;

    public MaintenanceStaffFrame(String email) {
        staffEmail = email;

        setTitle("Maintenance Staff - Assigned Work");
        setSize(800, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Assigned Maintenance Work");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        mainPanel.add(heading, BorderLayout.NORTH);

        String[] columns = {
            "Request ID",
            "Location",
            "Category",
            "Description",
            "Status"
        };

        Object[][] data = {
            {101, "Room 101", "Electrical",
                "Fan not working", "APPROVED"},

            {102, "Library", "Furniture",
                "Broken chair", "IN_PROGRESS"},

            {103, "Lab 2", "Plumbing",
                "Leaking tap", "COMPLETED"}
        };

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workTable = new JTable(model);
        workTable.setRowHeight(28);

        int[] widths = {90, 130, 120, 250, 130};

        for (int i = 0; i < widths.length; i++) {
            workTable.getColumnModel()
                .getColumn(i)
                .setPreferredWidth(widths[i]);
        }

        mainPanel.add(
            new JScrollPane(workTable),
            BorderLayout.CENTER
        );

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton detailsButton = new JButton("View Details");
        JButton updateButton = new JButton("Update Work Status");
        JButton logoutButton = new JButton("Logout");

        detailsButton.addActionListener(e -> viewDetails());
        updateButton.addActionListener(e -> updateStatus());

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        buttonPanel.add(detailsButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void viewDetails() {
        int row = workTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a work request.",
                "No Request Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = workTable.convertRowIndexToModel(row);

        String details =
            "Request ID: " + model.getValueAt(modelRow, 0)
            + "\nLocation: " + model.getValueAt(modelRow, 1)
            + "\nCategory: " + model.getValueAt(modelRow, 2)
            + "\nDescription: " + model.getValueAt(modelRow, 3)
            + "\nStatus: " + model.getValueAt(modelRow, 4);

        JOptionPane.showMessageDialog(
            this,
            details,
            "Work Request Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateStatus() {
        int row = workTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a work request first.",
                "No Request Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = workTable.convertRowIndexToModel(row);

        String currentStatus =
            model.getValueAt(modelRow, 4).toString();

        if (currentStatus.equals("COMPLETED")) {
            JOptionPane.showMessageDialog(
                this,
                "This work is already completed.",
                "No Update Needed",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String nextStatus;

        if (currentStatus.equals("APPROVED")) {
            nextStatus = "IN_PROGRESS";
        } else if (currentStatus.equals("IN_PROGRESS")) {
            nextStatus = "COMPLETED";
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Only approved requests can be updated by staff.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Change status from " + currentStatus
                + " to " + nextStatus + "?",
            "Confirm Status Update",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        model.setValueAt(nextStatus, modelRow, 4);

        JOptionPane.showMessageDialog(
            this,
            "Work status updated to " + nextStatus
                + ".\n\nThis is a UI demonstration only.",
            "Status Updated",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}