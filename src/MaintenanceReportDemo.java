
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaintenanceReportDemo {

    public static void main(String[] args) {

        // Sample maintenance requests for testing
        ArrayList<RequestRecord> requests = new ArrayList<>();

        RequestRecord r1 = new RequestRecord(
            1, "CSE", "Room 101",
            "ELECTRICAL", "Fan not working"
        );
        r1.setStatus("COMPLETED");

        RequestRecord r2 = new RequestRecord(
            2, "ECE", "Room 202",
            "PLUMBING", "Water tap leaking"
        );
        r2.setStatus("SUBMITTED");

        RequestRecord r3 = new RequestRecord(
            3, "CSE", "Lab 1",
            "ELECTRICAL", "Light not working"
        );
        r3.setStatus("IN_PROGRESS");

        RequestRecord r4 = new RequestRecord(
            4, "MECH", "Workshop",
            "FURNITURE", "Broken chair"
        );
        r4.setStatus("COMPLETED");

        requests.add(r1);
        requests.add(r2);
        requests.add(r3);
        requests.add(r4);

        // Count requests by status and category
        Map<String, Integer> statusCounts =
            new LinkedHashMap<>();

        Map<String, Integer> categoryCounts =
            new LinkedHashMap<>();

        for (RequestRecord request : requests) {

            String status = request.getStatus();
            String category = request.getCategory();

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
        int completed = statusCounts.getOrDefault(
            "COMPLETED", 0
        );

        double completionRate =
            completed * 100.0 / total;

        // Display the report
        System.out.println("\n================================");
        System.out.println(" COLLEGE MAINTENANCE REPORT");
        System.out.println("================================");

        System.out.println("Total requests: " + total);

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

        System.out.println("================================");
    }
}

