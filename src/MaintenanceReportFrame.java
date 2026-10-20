
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaintenanceReportFrame extends JFrame {

    private final Color darkGreen = new Color(48, 82, 66);
    private final Color sageGreen = new Color(225, 235, 224);
    private final Color background = new Color(246, 248, 245);
    private final Color textColor = new Color(48, 60, 51);

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
            "Overview of maintenance requests"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(textColor);

        main.add(heading);
        main.add(Box.createVerticalStrut(8));
        main.add(subtitle);
        main.add(Box.createVerticalStrut(25));

        // Sample statistics for the presentation demo
        Map<String, Integer> statuses = new LinkedHashMap<>();
        statuses.put("COMPLETED", 2);
        statuses.put("SUBMITTED", 1);
        statuses.put("IN_PROGRESS", 1);

        Map<String, Integer> categories = new LinkedHashMap<>();
        categories.put("ELECTRICAL", 2);
        categories.put("PLUMBING", 1);
        categories.put("FURNITURE", 1);

        int total = 4;
        int completed = statuses.get("COMPLETED");
        double rate = completed * 100.0 / total;

        JPanel summary = new JPanel(new GridLayout(1, 3, 12, 0));
        summary.setBackground(background);

        summary.add(createCard("Total Requests", String.valueOf(total)));
        summary.add(createCard("Completed", String.valueOf(completed)));
        summary.add(createCard("Completion Rate",
                               String.format("%.0f%%", rate)));

        main.add(summary);
        main.add(Box.createVerticalStrut(25));

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("Monospaced", Font.PLAIN, 14));
        details.setBackground(Color.WHITE);
        details.setForeground(textColor);
        details.setBorder(new EmptyBorder(15, 15, 15, 15));

        StringBuilder report = new StringBuilder();
        report.append("REQUESTS BY STATUS\n");
        report.append("------------------------------\n");

        for (Map.Entry<String, Integer> entry : statuses.entrySet()) {
            report.append(String.format(
                "%-20s %d%n", entry.getKey(), entry.getValue()
            ));
        }

        report.append("\nREQUESTS BY CATEGORY\n");
        report.append("------------------------------\n");

        for (Map.Entry<String, Integer> entry : categories.entrySet()) {
            report.append(String.format(
                "%-20s %d%n", entry.getKey(), entry.getValue()
            ));
        }

        details.setText(report.toString());

        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(BorderFactory.createLineBorder(sageGreen));

        main.add(scroll);
        add(main);
        setVisible(true);
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(sageGreen),
            new EmptyBorder(15, 15, 15, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(textColor);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(darkGreen);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MaintenanceReportFrame::new);
    }
}

