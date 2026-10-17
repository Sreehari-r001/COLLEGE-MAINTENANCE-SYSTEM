import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	// Singleton: only one object of this class will ever exist
	private static DatabaseConnection instance;

	private String url;
	private String user;
	private String password;

	// Private constructor, so other classes cannot use "new DatabaseConnection()"
	private DatabaseConnection() {
		url = "jdbc:mysql://localhost:3306/college_maintenance";
		user = "root";

		// Password is read from a Windows environment variable, not written in code
		password = System.getenv("DB_PASSWORD");
	}

	// The only way to get the single object
	public static DatabaseConnection getInstance() {
		if (instance == null) {
			instance = new DatabaseConnection();
		}
		return instance;
	}

	// Gives a NEW connection every time. The caller must close it.
	public Connection getConnection() throws SQLException {
		if (password == null || password.isEmpty()) {
			throw new SQLException("DB_PASSWORD environment variable is not set.");
		}
		return DriverManager.getConnection(url, user, password);
	}
}