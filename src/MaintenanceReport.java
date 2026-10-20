
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Member 4: Generates maintenance request statistics
 * using the existing database access class.
 */
public class MaintenanceReport {

    private final MaintenanceRequestDAO dao;

    public MaintenanceReport() {
        dao = new MaintenanceRequestDAO();
    }

    public void generateReport() {
        ArrayList<RequestRecord> requests = dao.getAllRequests();

        if (requests == null || requests.isEmpty()) {
            System.out.println("No maintenance requests found.");
            return;
        }

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        Map<String, Integer> categoryCounts = new LinkedHashMap<>();

        for (RequestRecord request : requests) {
            String status = request.getStatus();
            String category = request.getCategory();

            if (status == null || status.trim().isEmpty()) {
                status = "UNKNOWN";
            }

            if (category == null || category.trim().isEmpty()) {
                category = "UNCATEGORIZED";
            }

            statusCounts.put(
                status,
                statusCounts.getOrDefault(status, 0) + 1
            );

            categoryCounts.put(
                category,
                categoryCounts.getOrDefault(category, 0) + 1
            );
        }

        int total = requests.size();
        int completed = statusCounts.getOrDefault("COMPLETED", 0);
        double completionRate = (completed * 100.0) / total;

        System.out.println("\n==================================");
        System.out.println("   COLLEGE MAINTENANCE REPORT");
        System.out.println("==================================");

        System.out.println("\nTotal requests: " + total);

        System.out.println("\nRequests by status:");
        for (Map.Entry<String, Integer> entry
                : statusCounts.entrySet()) {
            System.out.println(
                entry.getKey() + ": " + entry.getValue()
            );
        }

        System.out.println("\nRequests by category:");
        for (Map.Entry<String, Integer> entry
                : categoryCounts.entrySet()) {
            System.out.println(
                entry.getKey() + ": " + entry.getValue()
            );
        }

        System.out.printf(
            "\nCompletion rate: %.2f%%%n",
            completionRate
        );

        System.out.println("==================================");
    }

    public static void main(String[] args) {
        MaintenanceReport report = new MaintenanceReport();
        report.generateReport();
    }
}

