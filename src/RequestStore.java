
import java.util.ArrayList;
import java.util.List;

public class RequestStore {

    public static class RequestItem {
        public String id;
        public String email;
        public String role;
        public String location;
        public String department;
        public String category;
        public String description;
        public String status;

        // Constructor for existing sample requests
        public RequestItem(
                String id,
                String department,
                String category,
                String description,
                String status
        ) {
            this(id, "-", "-", "-", department,
                    category, description, status);
        }

        // Constructor for newly submitted requests
        public RequestItem(
                String id,
                String email,
                String role,
                String location,
                String department,
                String category,
                String description,
                String status
        ) {
            this.id = id;
            this.email = email;
            this.role = role;
            this.location = location;
            this.department = department;
            this.category = category;
            this.description = description;
            this.status = status;
        }
    }

    private static final ArrayList<RequestItem> requests =
            new ArrayList<>();

    static {
        requests.add(new RequestItem(
                "REQ-001",
                "CSE",
                "Electrical",
                "Fan not working",
                "PENDING"
        ));

        requests.add(new RequestItem(
                "REQ-002",
                "ECE",
                "Plumbing",
                "Water leakage",
                "PENDING"
        ));

        requests.add(new RequestItem(
                "REQ-003",
                "Mechanical",
                "Furniture",
                "Broken chair",
                "APPROVED"
        ));
    }

    public static List<RequestItem> getRequests() {
        return requests;
    }

    public static void addRequest(RequestItem request) {
        requests.add(request);
    }

    public static RequestItem findById(String id) {
        for (RequestItem request : requests) {
            if (request.id.equals(id)) {
                return request;
            }
        }

        return null;
    }

    public static int countStatus(String status) {
        int count = 0;

        for (RequestItem request : requests) {
            if (request.status.equalsIgnoreCase(status)) {
                count++;
            }
        }

        return count;
    }

    public static int getTotalCount() {
        return requests.size();
    }
}
