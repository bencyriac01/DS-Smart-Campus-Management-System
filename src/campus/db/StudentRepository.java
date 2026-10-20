
package campus.db;

import campus.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private static final String URL = "jdbc:sqlite:campus.db";

    public StudentRepository() throws SQLException {
        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS students (
                        register_number TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        department TEXT NOT NULL,
                        semester INTEGER NOT NULL
                    )
                    """);
        }
    }

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY rowid";

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getString("register_number"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("semester")));
            }
        }

        return list;
    }

    public void insert(Student s) throws SQLException {
        String sql = """
                INSERT INTO students
                (register_number, name, department, semester)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getRegisterNumber());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDepartment());
            ps.setInt(4, s.getSemester());
            ps.executeUpdate();
        }
    }

    public boolean update(Student s) throws SQLException {
        String sql = """
                UPDATE students
                SET name = ?, department = ?, semester = ?
                WHERE register_number = ?
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getDepartment());
            ps.setInt(3, s.getSemester());
            ps.setString(4, s.getRegisterNumber());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM students WHERE register_number = ?";

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
