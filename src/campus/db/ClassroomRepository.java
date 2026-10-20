
package campus.db;

import campus.model.Classroom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomRepository {

    private static final String URL = "jdbc:sqlite:campus.db";

    public ClassroomRepository() throws SQLException {
        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS classrooms (
                        room_number TEXT PRIMARY KEY,
                        building TEXT NOT NULL,
                        capacity INTEGER NOT NULL,
                        room_type TEXT NOT NULL
                    )
                    """);
        }
    }

    public List<Classroom> getAll() throws SQLException {
        List<Classroom> list = new ArrayList<>();

        String sql = "SELECT * FROM classrooms ORDER BY rowid";

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Classroom(
                        rs.getString("room_number"),
                        rs.getString("building"),
                        rs.getInt("capacity"),
                        rs.getString("room_type")));
            }
        }

        return list;
    }

    public void insert(Classroom c) throws SQLException {
        String sql = """
                INSERT INTO classrooms
                (room_number, building, capacity, room_type)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getRoomNumber());
            ps.setString(2, c.getBuilding());
            ps.setInt(3, c.getCapacity());
            ps.setString(4, c.getRoomType());
            ps.executeUpdate();
        }
    }

    public boolean delete(String roomNumber) throws SQLException {
        String sql = "DELETE FROM classrooms WHERE room_number = ?";

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            return ps.executeUpdate() > 0;
        }
    }
}
