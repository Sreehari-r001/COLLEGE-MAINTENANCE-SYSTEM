// Carries one row of the maintenance_requests table between Java and MySQL.
// Separate from MaintenanceRequest so we don't change a teammate's class.

public class RequestRecord {
	private int requestId;
	private int submittedBy;      // user_id of the person who complained
	private String department;
	private String room;
	private String category;      // ELECTRICAL, PLUMBING, FURNITURE, EQUIPMENT, OTHER
	private String description;   // can be null
	private String status;        // SUBMITTED, APPROVED, ...
	private int assignedTo;       // user_id of staff, 0 means not assigned

	public RequestRecord(int submittedBy, String department, String room,
	                     String category, String description) {
		this.submittedBy = submittedBy;
		this.department = department;
		this.room = room;
		this.category = category;
		this.description = description;
		this.status = "SUBMITTED";
		this.assignedTo = 0;
	}

	// Getters
	public int getRequestId() { return requestId; }
	public int getSubmittedBy() { return submittedBy; }
	public String getDepartment() { return department; }
	public String getRoom() { return room; }
	public String getCategory() { return category; }
	public String getDescription() { return description; }
	public String getStatus() { return status; }
	public int getAssignedTo() { return assignedTo; }

	// Setters (used by the DAO to fill values that MySQL creates)
	public void setRequestId(int requestId) { this.requestId = requestId; }
	public void setStatus(String status) { this.status = status; }
	public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }
}