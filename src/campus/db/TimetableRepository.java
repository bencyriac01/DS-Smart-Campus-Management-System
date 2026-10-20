
package campus.db;

import campus.model.TimetableEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {

    private static final String URL = "jdbc:sqlite:campus.db";

    public TimetableRepository() throws SQLException {
        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS timetable (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        subject TEXT NOT NULL,
                        faculty TEXT NOT NULL,
                        day TEXT NOT NULL,
                        time_slot TEXT NOT NULL,
                        room_number TEXT NOT NULL
                    )
                    """);
        }
    }

    public List<TimetableEntry> getAll() throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();

        String sql = "SELECT * FROM timetable ORDER BY id";

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new TimetableEntry(
                        rs.getInt("id"),
                        rs.getString("subject"),
                        rs.getString("faculty"),
                        rs.getString("day"),
                        rs.getString("time_slot"),
                        rs.getString("room_number")));
            }
        }

        return list;
    }

    public int insert(TimetableEntry entry) throws SQLException {
        String sql = """
                INSERT INTO timetable
                (subject, faculty, day, time_slot, room_number)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(URL)) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, entry.getSubject());
                ps.setString(2, entry.getFaculty());
                ps.setString(3, entry.getDay());
                ps.setString(4, entry.getTimeSlot());
                ps.setString(5, entry.getRoomNumber());
                ps.executeUpdate();
            }

            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT last_insert_rowid()")) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Could not retrieve timetable ID.");
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM timetable WHERE id = ?";

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
