package hjzhhhj;

import java.sql.*;
import java.util.*;

public class FileHandler {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/zombie_go";
    private static final String DB_USER = "zombie_user";
    private static final String DB_PASSWORD = "password";

    public FileHandler() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS scores (" +
                "school_id VARCHAR(255) PRIMARY KEY," +
                "score INTEGER" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public int getScore(String schoolId) {
        String sql = "SELECT score FROM scores WHERE school_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schoolId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            System.err.println("Error getting score: " + e.getMessage());
        }
        return 0;
    }

    public void updateScore(String schoolId, int score) {
        String sql = "INSERT INTO scores (school_id, score) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = GREATEST(score, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // 🔥 트랜잭션 시작 (자동 커밋 비활성화)

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println("Updating score for school ID: " + schoolId + " with score: " + score);

                pstmt.setString(1, schoolId);
                pstmt.setInt(2, score);
                pstmt.setInt(3, score);
                int rowsAffected = pstmt.executeUpdate();

                System.out.println("DB Update Success. Rows affected: " + rowsAffected);

                conn.commit(); // 🔥 명시적으로 커밋
                System.out.println("✅ Commit successful!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
        }
    }


    public List<Map.Entry<String, Integer>> getTopScores(int count) {
        String sql = "SELECT school_id, score FROM scores ORDER BY score DESC LIMIT ?";
        List<Map.Entry<String, Integer>> topScores = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, count);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String schoolId = rs.getString("school_id");
                int score = rs.getInt("score");
                topScores.add(new AbstractMap.SimpleEntry<>(schoolId, score));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top scores: " + e.getMessage());
        }
        return topScores;
    }

    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        fileHandler.updateScore("12345", 100);
        System.out.println("Score for 12345: " + fileHandler.getScore("12345"));
        List<Map.Entry<String, Integer>> topScores = fileHandler.getTopScores(3);
        System.out.println("Top scores: " + topScores);
    }
}
