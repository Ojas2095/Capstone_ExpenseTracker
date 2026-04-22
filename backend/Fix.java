package backend;
import java.sql.*;
public class Fix {
    public static void main(String[] args) throws Exception {
        try(Connection c = DatabaseManager.getConnection(); 
            Statement s = c.createStatement()) {
            s.executeUpdate("INSERT IGNORE INTO Users (id, username, password) VALUES (1, 'admin', 'password')");
            System.out.println("User fixed");
        }
    }
}
