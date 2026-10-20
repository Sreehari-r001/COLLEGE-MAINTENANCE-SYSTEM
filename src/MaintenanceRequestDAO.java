import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class MaintenanceRequestDAO {

	// ---------- CREATE ----------

	// Inserts a new complaint. Returns the new request_id, or -1 if it failed.
	public int insertRequest(RequestRecord r) {

		String sql = "INSERT INTO maintenance_requests "
		           + "(submitted_by, department, room, category, description) "
		           + "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet keys = null;
		int newId = -1;

		try {
			// Step 1: get connection
			con = DatabaseConnection.getInstance().getConnection();

			// Step 2: create statement
			ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, r.getSubmittedBy());
			ps.setString(2, r.getDepartment());
			ps.setString(3, r.getRoom());
			ps.setString(4, r.getCategory());

			// Description is optional: store NULL if empty
			if (r.getDescription() == null || r.getDescription().trim().isEmpty()) {
				ps.setNull(5, Types.VARCHAR);
			} else {
				ps.setString(5, r.getDescription());
			}

			// Step 3: execute
			ps.executeUpdate();

			// Step 4: read the ID that MySQL generated
			keys = ps.getGeneratedKeys();
			if (keys.next()) {
				newId = keys.getInt(1);
				r.setRequestId(newId);
			}

		} catch (SQLException e) {
			System.out.println("Could not insert request: " + e.getMessage());
		} finally {
			// Step 5: close
			closeAll(con, ps, keys);
		}

		// First history entry: no old status, new status SUBMITTED
		if (newId > 0) {
			writeHistory(newId, null, "SUBMITTED", r.getSubmittedBy(), "Complaint submitted");
		}
		return newId;
	}

	// ---------- READ ----------

	// Finds one complaint by ID. Returns null if it does not exist.
	public RequestRecord getRequestById(int requestId) {

		String sql = "SELECT request_id, submitted_by, department, room, category, "
		           + "description, status, assigned_to "
		           + "FROM maintenance_requests WHERE request_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		RequestRecord r = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, requestId);
			rs = ps.executeQuery();

			// Only one row is possible, so "if" is enough
			if (rs.next()) {
				r = makeRecord(rs);
			}

		} catch (SQLException e) {
			System.out.println("Could not get request: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return r;
	}

	// Gets ALL complaints. Returns an empty list if there are none.
	public ArrayList<RequestRecord> getAllRequests() {

		String sql = "SELECT request_id, submitted_by, department, room, category, "
		           + "description, status, assigned_to "
		           + "FROM maintenance_requests ORDER BY request_id";

		ArrayList<RequestRecord> list = new ArrayList<RequestRecord>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			// Display values from table
			while (rs.next()) {
				list.add(makeRecord(rs));
			}

		} catch (SQLException e) {
			System.out.println("Could not get all requests: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return list;
	}

	// Gets all complaints submitted by one user
	public ArrayList<RequestRecord> getRequestsBySubmitter(int submitterId) {

		String sql = "SELECT request_id, submitted_by, department, room, category, "
		           + "description, status, assigned_to "
		           + "FROM maintenance_requests WHERE submitted_by = ? "
		           + "ORDER BY request_id";

		ArrayList<RequestRecord> list = new ArrayList<RequestRecord>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, submitterId);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(makeRecord(rs));
			}

		} catch (SQLException e) {
			System.out.println("Could not get requests by submitter: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return list;
	}

	// Gets all complaints with a given status, like "SUBMITTED"
	public ArrayList<RequestRecord> getRequestsByStatus(String status) {

		String sql = "SELECT request_id, submitted_by, department, room, category, "
		           + "description, status, assigned_to "
		           + "FROM maintenance_requests WHERE status = ? "
		           + "ORDER BY request_id";

		ArrayList<RequestRecord> list = new ArrayList<RequestRecord>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, status);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(makeRecord(rs));
			}

		} catch (SQLException e) {
			System.out.println("Could not get requests by status: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return list;
	}

	// ---------- UPDATE ----------

	// Changes the status and records it in request_history.
	// changedBy = user_id of the person making the change.
	// Returns true if it worked, false if not.
	public boolean updateStatus(int requestId, String newStatus,
	                            int changedBy, String remarks) {

		// Check first, so we never change the status and then fail on history
		if (!userExists(changedBy)) {
			System.out.println("No user with id " + changedBy);
			return false;
		}

		String oldStatus = readStatus(requestId);
		if (oldStatus == null) {
			System.out.println("No complaint with id " + requestId);
			return false;
		}

		String sql = "UPDATE maintenance_requests SET status = ? WHERE request_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		boolean done = false;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, newStatus);
			ps.setInt(2, requestId);
			ps.executeUpdate();
			done = true;

		} catch (SQLException e) {
			System.out.println("Could not update status: " + e.getMessage());
		} finally {
			closeAll(con, ps, null);
		}

		if (done) {
			done = writeHistory(requestId, oldStatus, newStatus, changedBy, remarks);
		}
		return done;
	}

	// Assigns a staff member. The status becomes ASSIGNED and history is recorded.
	public boolean assignStaff(int requestId, int staffId,
	                           int changedBy, String remarks) {

		if (!userExists(changedBy)) {
			System.out.println("No user with id " + changedBy);
			return false;
		}

		String oldStatus = readStatus(requestId);
		if (oldStatus == null) {
			System.out.println("No complaint with id " + requestId);
			return false;
		}

		String sql = "UPDATE maintenance_requests "
		           + "SET assigned_to = ?, status = 'ASSIGNED' WHERE request_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		boolean done = false;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, staffId);
			ps.setInt(2, requestId);
			ps.executeUpdate();
			done = true;

		} catch (SQLException e) {
			System.out.println("Could not assign staff: " + e.getMessage());
		} finally {
			closeAll(con, ps, null);
		}

		if (done) {
			done = writeHistory(requestId, oldStatus, "ASSIGNED", changedBy, remarks);
		}
		return done;
	}

	// ---------- HISTORY ----------

	// Gets the history of one complaint as text lines, oldest first
	public ArrayList<String> getHistoryForRequest(int requestId) {

		String sql = "SELECT old_status, new_status, changed_by, changed_at, remarks "
		           + "FROM request_history WHERE request_id = ? ORDER BY history_id";

		ArrayList<String> list = new ArrayList<String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, requestId);
			rs = ps.executeQuery();

			while (rs.next()) {
				String oldS = rs.getString("old_status");
				if (oldS == null) {
					oldS = "(none)";
				}
				String remarks = rs.getString("remarks");
				if (remarks == null) {
					remarks = "";
				}
				list.add(oldS + " -> " + rs.getString("new_status")
				    + " | by user " + rs.getInt("changed_by")
				    + " | " + rs.getTimestamp("changed_at")
				    + " | " + remarks);
			}

		} catch (SQLException e) {
			System.out.println("Could not get history: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return list;
	}

	// ---------- HELPER METHODS (private: only used inside this class) ----------

	// Adds one row to request_history. Returns true if it worked.
	private boolean writeHistory(int requestId, String oldStatus, String newStatus,
	                             int changedBy, String remarks) {

		String sql = "INSERT INTO request_history "
		           + "(request_id, old_status, new_status, changed_by, remarks) "
		           + "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement ps = null;
		boolean done = false;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, requestId);

			if (oldStatus == null) {
				ps.setNull(2, Types.VARCHAR);
			} else {
				ps.setString(2, oldStatus);
			}

			ps.setString(3, newStatus);
			ps.setInt(4, changedBy);

			if (remarks == null || remarks.trim().isEmpty()) {
				ps.setNull(5, Types.VARCHAR);
			} else {
				ps.setString(5, remarks);
			}

			ps.executeUpdate();
			done = true;

		} catch (SQLException e) {
			System.out.println("Could not write history: " + e.getMessage());
		} finally {
			closeAll(con, ps, null);
		}
		return done;
	}

	// Reads the current status of a complaint. Returns null if it does not exist.
	private String readStatus(int requestId) {

		String sql = "SELECT status FROM maintenance_requests WHERE request_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String status = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, requestId);
			rs = ps.executeQuery();

			if (rs.next()) {
				status = rs.getString("status");
			}

		} catch (SQLException e) {
			System.out.println("Could not read status: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return status;
	}

	// Checks whether a user_id exists in the users table
	private boolean userExists(int userId) {

		String sql = "SELECT user_id FROM users WHERE user_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean found = false;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			found = rs.next();

		} catch (SQLException e) {
			System.out.println("Could not check user: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return found;
	}

	// Turns the current row of a ResultSet into a RequestRecord object
	private RequestRecord makeRecord(ResultSet rs) throws SQLException {
		RequestRecord r = new RequestRecord(
			rs.getInt("submitted_by"),
			rs.getString("department"),
			rs.getString("room"),
			rs.getString("category"),
			rs.getString("description")   // stays null if the column is NULL
		);
		r.setRequestId(rs.getInt("request_id"));
		r.setStatus(rs.getString("status"));
		r.setAssignedTo(rs.getInt("assigned_to"));   // gives 0 when NULL
		return r;
	}

	// Closes the ResultSet, statement and connection (any of them can be null)
	private void closeAll(Connection con, PreparedStatement ps, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("Could not close ResultSet: " + e.getMessage());
		}
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			System.out.println("Could not close statement: " + e.getMessage());
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("Could not close connection: " + e.getMessage());
		}
	}

		// Gets all complaints assigned to one maintenance staff member
	public ArrayList<RequestRecord> getRequestsByAssignedStaff(int staffId) {

		String sql = "SELECT request_id, submitted_by, department, room, category, "
		           + "description, status, assigned_to "
		           + "FROM maintenance_requests WHERE assigned_to = ? "
		           + "ORDER BY request_id";

		ArrayList<RequestRecord> list = new ArrayList<RequestRecord>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DatabaseConnection.getInstance().getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, staffId);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(makeRecord(rs));
			}

		} catch (SQLException e) {
			System.out.println("Could not get requests by assigned staff: " + e.getMessage());
		} finally {
			closeAll(con, ps, rs);
		}
		return list;
	}
	        // Finds a user's database ID using their email address
        public int getUserIdByEmail(String email) {
                String sql = "SELECT user_id FROM users WHERE email = ?";

                try (Connection con = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setString(1, email);

                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        return rs.getInt("user_id");
                                }
                        }

                } catch (SQLException e) {
                        System.out.println(
                                "Could not find user by email: " + e.getMessage()
                        );
                }

                return -1;
        }
}