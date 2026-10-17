/* From the CMS folder:
   javac --release 21 -cp "lib\mysql-connector-j-26.7.0.jar" -d out src\*.java
   java -cp "out;lib\mysql-connector-j-26.7.0.jar" DatabaseTest */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseTest {

	// Prints PASSED or FAILED for one check
	static void check(String name, boolean ok) {
		if (ok) {
			System.out.println("PASSED: " + name);
		} else {
			System.out.println("FAILED: " + name);
		}
	}

	public static void main(String[] args) {

		// TEST 1: Connection
		System.out.println("TEST 1: Connect to MySQL");
		try {
			Connection con = DatabaseConnection.getInstance().getConnection();
			System.out.println("Database connection successful!");

			PreparedStatement ps = con.prepareStatement(
				"SELECT user_id, name, role FROM users");
			ResultSet rs = ps.executeQuery();

			// Display values from table
			while (rs.next()) {
				System.out.println("Id: " + rs.getInt("user_id")
					+ " Name: " + rs.getString("name")
					+ " Role: " + rs.getString("role"));
			}
			rs.close();
			ps.close();
			con.close();

		} catch (SQLException e) {
			System.out.println("Database connection failed!");
			e.printStackTrace();
		}

		MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

		// TEST 2: Insert (user 1 = Rahul, user 2 = Sam)
		System.out.println("\nTEST 2: Insert complaints");
		RequestRecord r = new RequestRecord(1, "CSE", "204", "ELECTRICAL",
		                                    "Fan is not working");
		int newId = dao.insertRequest(r);
		check("insert with description, new id = " + newId, newId > 0);

		RequestRecord r2 = new RequestRecord(2, "CSE", "Lab 3", "PLUMBING", null);
		int newId2 = dao.insertRequest(r2);
		check("insert without description, new id = " + newId2, newId2 > 0);

		RequestRecord bad = new RequestRecord(999, "CSE", "100", "OTHER", "test");
		check("bad user (999) rejected", dao.insertRequest(bad) == -1);

		// TEST 3: Get by ID
		System.out.println("\nTEST 3: Get complaint by ID");
		RequestRecord found = dao.getRequestById(newId);
		if (found != null) {
			System.out.println("Id: " + found.getRequestId()
				+ " Room: " + found.getRoom()
				+ " Category: " + found.getCategory()
				+ " Description: " + found.getDescription()
				+ " Status: " + found.getStatus()
				+ " Assigned: " + found.getAssignedTo());
		}
		check("complaint found with status SUBMITTED",
			found != null && found.getStatus().equals("SUBMITTED"));

		RequestRecord found2 = dao.getRequestById(newId2);
		check("empty description is kept as null",
			found2 != null && found2.getDescription() == null);

		check("invalid ID returns null", dao.getRequestById(99999) == null);

		// TEST 4: Get all
		System.out.println("\nTEST 4: Get all complaints");
		ArrayList<RequestRecord> all = dao.getAllRequests();
		for (int i = 0; i < all.size(); i++) {
			RequestRecord x = all.get(i);
			System.out.println("Id: " + x.getRequestId() + " Room: " + x.getRoom()
				+ " Status: " + x.getStatus() + " By user: " + x.getSubmittedBy());
		}
		check("at least 2 complaints found", all.size() >= 2);

		// TEST 5: By submitter
		System.out.println("\nTEST 5: Get complaints by submitter");
		ArrayList<RequestRecord> mine = dao.getRequestsBySubmitter(1);
		boolean allMine = true;
		for (int i = 0; i < mine.size(); i++) {
			if (mine.get(i).getSubmittedBy() != 1) {
				allMine = false;
			}
		}
		check("all complaints belong to user 1", mine.size() > 0 && allMine);
		check("user with no complaints gives empty list",
			dao.getRequestsBySubmitter(4).size() == 0);

		// TEST 6: By status
		System.out.println("\nTEST 6: Get complaints by status");
		ArrayList<RequestRecord> submitted = dao.getRequestsByStatus("SUBMITTED");
		boolean allSubmitted = true;
		for (int i = 0; i < submitted.size(); i++) {
			if (!submitted.get(i).getStatus().equals("SUBMITTED")) {
				allSubmitted = false;
			}
		}
		check("all returned complaints are SUBMITTED", submitted.size() > 0 && allSubmitted);

		// TEST 7: Update status (user 3 = Meera the HOD)
		System.out.println("\nTEST 7: Update status");
		boolean ok = dao.updateStatus(newId, "APPROVED", 3, "Approved by HOD");
		RequestRecord after = dao.getRequestById(newId);
		check("status is now APPROVED", ok && after.getStatus().equals("APPROVED"));

		// TEST 8: Assign staff (user 4 = Arun)
		System.out.println("\nTEST 8: Assign staff");
		ok = dao.assignStaff(newId, 4, 3, "Assigned to Arun");
		after = dao.getRequestById(newId);
		check("status ASSIGNED and staff is user 4",
			ok && after.getStatus().equals("ASSIGNED") && after.getAssignedTo() == 4);

		dao.updateStatus(newId, "IN_PROGRESS", 4, "Work started");
		dao.updateStatus(newId, "COMPLETED", 4, "Work done");
		after = dao.getRequestById(newId);
		check("status is now COMPLETED", after.getStatus().equals("COMPLETED"));

		// TEST 9: History
		System.out.println("\nTEST 9: History");
		ArrayList<String> history = dao.getHistoryForRequest(newId);
		for (int i = 0; i < history.size(); i++) {
			System.out.println(history.get(i));
		}
		check("5 history rows", history.size() == 5);

		// TEST 10: Bad inputs must fail safely
		System.out.println("\nTEST 10: Bad inputs");
		check("unknown complaint rejected",
			!dao.updateStatus(99999, "APPROVED", 3, null));
		check("invalid status rejected",
			!dao.updateStatus(newId2, "FINISHED", 3, null));
		check("unknown staff rejected",
			!dao.assignStaff(newId2, 999, 3, null));
		check("unknown changing user rejected",
			!dao.updateStatus(newId2, "APPROVED", 999, null));
		check("status stayed SUBMITTED after all those failures",
			dao.getRequestById(newId2).getStatus().equals("SUBMITTED"));
	
	
		// TEST 11: Complaints assigned to a staff member (user 4 = Arun)
		System.out.println("\nTEST 11: Get complaints by assigned staff");
		ArrayList<RequestRecord> work = dao.getRequestsByAssignedStaff(4);
		boolean allArun = true;
		for (int i = 0; i < work.size(); i++) {
			RequestRecord x = work.get(i);
			System.out.println("Id: " + x.getRequestId() + " Room: " + x.getRoom()
				+ " Status: " + x.getStatus() + " Assigned: " + x.getAssignedTo());
			if (x.getAssignedTo() != 4) {
				allArun = false;
			}
		}
		check("all returned complaints are assigned to user 4", work.size() > 0 && allArun);
		check("user with no assigned work gives empty list",
			dao.getRequestsByAssignedStaff(1).size() == 0);
	}
}