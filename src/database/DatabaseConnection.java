
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // Singleton instance
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    // Private constructor
    private DatabaseConnection() {
        url = "jdbc:mysql://localhost:3306/college_maintenance";
        user = "root";

        // Read the password from the environment variable
        password = System.getenv("DB_PASSWORD");
    }

    // Get the single instance
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Establish and return a database connection
    public Connection getConnection() throws SQLException {

        if (password == null || password.isEmpty()) {
            throw new SQLException(
                "DB_PASSWORD environment variable is not set."
            );
        }

        Connection con = DriverManager.getConnection(
            url, user, password
        );

        // Print connection details for troubleshooting
        System.out.println("========== DATABASE DEBUG ==========");
        System.out.println("JDBC URL: " +
            con.getMetaData().getURL());
        System.out.println("Database: " +
            con.getCatalog());

        String diagnosticSQL =
            "SELECT @@hostname, @@port, @@datadir, VERSION()";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(diagnosticSQL)) {

            if (rs.next()) {
                System.out.println("SERVER HOST: " +
                    rs.getString(1));
                System.out.println("SERVER PORT: " +
                    rs.getString(2));
                System.out.println("DATA DIRECTORY: " +
                    rs.getString(3));
                System.out.println("MYSQL VERSION: " +
                    rs.getString(4));
            }
        } catch (SQLException e) {
            System.out.println(
                "Could not retrieve server diagnostics: "
                + e.getMessage()
            );
        }

        System.out.println("====================================");

        return con;
    }
}
