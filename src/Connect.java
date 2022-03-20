import java.sql.*;

public class Connect {
    public static void connect() {
        // connecting to local db
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:db/snp500db");
            System.out.println("Connection Established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
