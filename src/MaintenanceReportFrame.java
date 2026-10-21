
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaintenanceReportFrame extends JFrame {

    private final Color darkGreen = new Color(48, 82, 66);
    private final Color sageGreen = new Color(225, 235, 224);
    private final Color background = new Color(246, 248, 245);
    private final Color textColor = new Color(48, 60, 51);

    private final JLabel totalValue = new JLabel("0");
    private final JLabel completedValue = new JLabel("0");
    private final JLabel rateValue = new JLabel("0%");
    private final JTextArea details = new JTextArea();

    private final MaintenanceRequestDAO dao =
            new MaintenanceRequestDAO();

    public MaintenanceReportFrame() {
        setTitle("CampusCare | Maintenance Reports");
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel();
        main.setBackground(background);
        main.setBorder(new EmptyBorder(25, 30, 25, 30));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Maintenance Reports");
        heading.setFont(new Font("SansSerif", Font.BOLD, 27));
        heading.setForeground(darkGreen);

        JLabel subtitle = new JLabel(
                "Live overview of maintenance requests");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(textColor);

        JButton refreshButton = new JButton("Refresh Report");
        refreshButton.setBackground(darkGreen);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadReport());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(background);
        header.add(heading, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);
        header.add(refreshButton, BorderLayout.EAST);

        main.add(header);
        main.add(Box.createVerticalStrut(25));

        JPanel summary = new JPanel(new GridLayout(1, 3, 12, 0));
        summary.setBackground(background);

        summary.add(createCard("Total Requests", totalValue));
        summary.add(createCard("Completed", completedValue));
        summary.add(createCard("Completion Rate", rateValue));

        main.add(summary);
        main.add(Box.createVerticalStrut(25));

        details.setEditable(false);
        details.setFont(new Font("Monospaced", Font.PLAIN, 14));
        details.setBackground(Color.WHITE);
        details.setForeground(textColor);
        details.setBorder(new EmptyBorder(15, 15, 15, 15));
        details.setText("Loading report...");

        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(
                BorderFactory.createLineBorder(sageGreen));

        main.add(scroll);
        add(main);

        loadReport();

        setVisible(true);
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(sageGreen),
                new EmptyBorder(15, 15, 15, 10)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(textColor);
        titleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12));

        valueLabel.setForeground(darkGreen);
        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 25));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private void loadReport() {
        try {
            ArrayList<RequestRecord> requests =
                    dao.getAllRequests();

            Map<String, Integer> statuses =
                    new LinkedHashMap<>();

            Map<String, Integer> categories =
                    new LinkedHashMap<>();

            int completed = 0;

            for (RequestRecord request : requests) {
                String status = request.getStatus();
                String category = request.getCategory();

                if (status == null || status.trim().isEmpty()) {
                    status = "UNKNOWN";
                } else {
                    status = status.trim().toUpperCase();
                }

                if (category == null ||
                        category.trim().isEmpty()) {
                    category = "UNCATEGORIZED";
                } else {
                    category = category.trim().toUpperCase();
                }

                statuses.put(status,
                        statuses.getOrDefault(status, 0) + 1);

                categories.put(category,
                        categories.getOrDefault(category, 0) + 1);

                if ("COMPLETED".equals(status)) {
                    completed++;
                }
            }

            int total = requests.size();

            double rate = total == 0
                    ? 0.0
                    : completed * 100.0 / total;

            totalValue.setText(String.valueOf(total));
            completedValue.setText(String.valueOf(completed));
            rateValue.setText(String.format("%.1f%%", rate));

            StringBuilder report = new StringBuilder();

            report.append("REQUESTS BY STATUS\n");
            report.append("------------------------------\n");

            if (statuses.isEmpty()) {
                report.append("No maintenance requests found.\n");
            } else {
                for (Map.Entry<String, Integer> entry
                        : statuses.entrySet()) {
                    report.append(String.format(
                            "%-22s %d%n",
                            entry.getKey(), entry.getValue()));
                }
            }

            report.append("\nREQUESTS BY CATEGORY\n");
            report.append("------------------------------\n");

            if (categories.isEmpty()) {
                report.append("No category data available.\n");
            } else {
                for (Map.Entry<String, Integer> entry
                        : categories.entrySet()) {
                    report.append(String.format(
                            "%-22s %d%n",
                            entry.getKey(), entry.getValue()));
                }
            }

            details.setText(report.toString());
            details.setCaretPosition(0);

        } catch (Exception ex) {
            details.setText(
                    "Unable to load the maintenance report.\n\n"
                    + "Check the database connection and try again.\n"
                    + "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                MaintenanceReportFrame::new);
    }
}
