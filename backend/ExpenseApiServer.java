package backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExpenseApiServer {

    public static void main(String[] args) throws Exception {
        // 1. Initialize the Database
        DatabaseManager.initializeDatabase();

        // 2. Start the HTTP Server on Port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Contexts (Routes)
        server.createContext("/api/transactions", new TransactionHandler());
        server.createContext("/api/auth", new AuthHandler());
        
        server.setExecutor(null); 
        server.start();
        System.out.println("Java Expense Backend running on port 8080...");
    }

    // Helper to send JSON responses and handle CORS
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static class TransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    sendResponse(exchange, 204, "");
                    return;
                }

                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    // Fetch all transactions (simplified for MVP: user_id = 1)
                    StringBuilder json = new StringBuilder("[");
                    try (Connection conn = DatabaseManager.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT * FROM Transactions ORDER BY id DESC")) {
                        
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) json.append(",");
                            json.append(String.format("{\"id\":\"%d\", \"text\":\"%s\", \"amount\":%f, \"type\":\"%s\", \"category\":\"%s\", \"date\":\"%s\"}",
                                rs.getInt("id"), rs.getString("text"), rs.getDouble("amount"), 
                                rs.getString("type"), rs.getString("category"), rs.getString("date")));
                            first = false;
                        }
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());

                } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    // Quick and dirty manual JSON parsing
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), "UTF-8");
                    
                    // Simple extraction logic for "key":"value" JSON format
                    String text = extractJsonValue(body, "text");
                    double amount = Double.parseDouble(extractJsonValue(body, "amount").replaceAll("[^\\d.]", ""));
                    String type = extractJsonValue(body, "type");
                    String category = extractJsonValue(body, "category");
                    String date = extractJsonValue(body, "date");

                    try (Connection conn = DatabaseManager.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(
                             "INSERT INTO Transactions (user_id, text, amount, type, category, date) VALUES (1, ?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, text);
                        pstmt.setDouble(2, amount);
                        pstmt.setString(3, type);
                        pstmt.setString(4, category);
                        pstmt.setString(5, date);
                        pstmt.executeUpdate();
                    }
                    sendResponse(exchange, 201, "{\"message\":\"Transaction Created\"}");
                    
                } else if (exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
                    // e.g. /api/transactions?id=4
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.split("=")[1]);
                        try (Connection conn = DatabaseManager.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Transactions WHERE id = ?")) {
                            pstmt.setInt(1, id);
                            pstmt.executeUpdate();
                        }
                        sendResponse(exchange, 200, "{\"message\":\"Transaction Deleted\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Missing id\"}");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                sendResponse(exchange, 204, "");
                return;
            }
            // Mock authentication endpoint for now
            sendResponse(exchange, 200, "{\"token\":\"mock-jwt-token-123\", \"userId\":1}");
        }
    }

    private static String extractJsonValue(String json, String key) {
        // Extremely simplified extractor for format: "key":"value" or "key":123.45
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "0";
        start += search.length();
        
        int end;
        if (json.charAt(start) == '"') {
            start++; // skip quote
            end = json.indexOf("\"", start);
        } else {
            end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
        }
        return json.substring(start, end).trim();
    }
}
