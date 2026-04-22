package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Modify based on your lab_db setup
    static final String DB_URL = "jdbc:mysql://localhost:3306/lab_db";
    static final String USER = "root";
    static final String PASS = "password";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Users table
            String createUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(100) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)";
            stmt.executeUpdate(createUsers);

            // Create Transactions table
            String createTransactions = "CREATE TABLE IF NOT EXISTS Transactions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "text VARCHAR(255) NOT NULL, " +
                    "amount DOUBLE NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "category VARCHAR(100), " +
                    "date VARCHAR(100), " +
                    "FOREIGN KEY (user_id) REFERENCES Users(id))";
            stmt.executeUpdate(createTransactions);

            System.out.println("Database Tables Initialized and Ready.");
        } catch (SQLException e) {
            System.err.println("Database Setup Failed: " + e.getMessage());
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> list = new ArrayList<>();
        // In a real app we'd filter by logged-in user instead of hardcoding user_id=1
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Transactions WHERE user_id=1 ORDER BY id DESC")) {
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("id"),
                    rs.getString("text"),
                    rs.getDouble("amount"),
                    rs.getString("type"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addTransaction(String text, double amount, String type, String category, String date) {
        String sql = "INSERT INTO Transactions (user_id, text, amount, type, category, date) VALUES (1, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, type);
            pstmt.setString(4, category);
            pstmt.setString(5, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTransaction(int id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Transactions WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
