package hjzhhhj;

import java.sql.*;

public class DatabaseTest {

    public static void main(String[] args) {
        // DB 연결 확인
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zombie_go", "root", "1225");
            System.out.println("DB connection successful!");

            // 사용자 데이터 삽입
            String userId = "user123";
            int userScore = 100;

            // UPDATE 쿼리 (기존에 있는 데이터 업데이트)
            String updateQuery = "UPDATE scores SET score = ? WHERE school_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);

            // 값 세팅
            updateStmt.setInt(1, userScore);
            updateStmt.setString(2, userId);

            // 쿼리 실행 후 결과 확인
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data updated successfully.");
            } else {
                System.out.println("No data updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 연결 종료
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
