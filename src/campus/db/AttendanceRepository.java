
package campus.db;

import campus.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {

    private static final String URL = "jdbc:sqlite:campus.db";

    public AttendanceRepository() throws SQLException {
        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS attendance (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id TEXT NOT NULL,
                        subject TEXT NOT NULL,
                        attendance_date TEXT NOT NULL,
                        status TEXT NOT NULL CHECK (status IN ('Present', 'Absent')),
                        UNIQUE(student_id, subject, attendance_date)
                    )
                    """);
        }
    }

    public List<Attendance> getAll() throws SQLException {
        List<Attendance> list = new ArrayList<>();

        String sql = """
                SELECT a.id, a.student_id, s.name, a.subject,
                       a.attendance_date, a.status
                FROM attendance a
                JOIN students s ON a.student_id = s.register_number
                ORDER BY a.attendance_date DESC, a.id DESC
                """;

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("subject"),
                        rs.getString("attendance_date"),
                        rs.getString("status")));
            }
        }

        return list;
    }

    public int insert(Attendance attendance) throws SQLException {
        String sql = """
                INSERT INTO attendance
                (student_id, subject, attendance_date, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, attendance.getStudentId());
                ps.setString(2, attendance.getSubject());
                ps.setString(3, attendance.getDate());
                ps.setString(4, attendance.getStatus());
                ps.executeUpdate();
            }

            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Could not retrieve attendance ID.");
    }

    public double getPercentage(String studentId, String subject)
            throws SQLException {

        String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present
                FROM attendance
                WHERE student_id = ? AND subject = ?
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, subject);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");

                    if (total == 0) {
                        return -1;
                    }

                    return present * 100.0 / total;
                }
            }
        }

        return -1;
    }
}
