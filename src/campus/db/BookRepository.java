
package campus.db;

import campus.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    private static final String URL = "jdbc:sqlite:campus.db";

    public BookRepository() throws SQLException {
        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement()) {

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS books (
                        book_id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        quantity INTEGER NOT NULL CHECK (quantity >= 0)
                    )
                    """);

            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS book_issues (
                        issue_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        book_id TEXT NOT NULL,
                        student_id TEXT NOT NULL,
                        issue_date TEXT NOT NULL DEFAULT CURRENT_DATE,
                        return_date TEXT,
                        FOREIGN KEY (book_id) REFERENCES books(book_id)
                    )
                    """);
        }
    }

    public List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("quantity")));
            }
        }

        return books;
    }

    public void insert(Book book) throws SQLException {
        String sql = """
                INSERT INTO books (book_id, title, author, quantity)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getQuantity());
            ps.executeUpdate();
        }
    }

    public boolean delete(String bookId) throws SQLException {
        String sql = """
                DELETE FROM books
                WHERE book_id = ?
                  AND NOT EXISTS (
                      SELECT 1 FROM book_issues
                      WHERE book_issues.book_id = books.book_id
                        AND return_date IS NULL
                  )
                """;

        try (Connection con = DriverManager.getConnection(URL);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean issueBook(String bookId, String studentId)
            throws SQLException {

        String sql = """
                UPDATE books
                SET quantity = quantity - 1
                WHERE book_id = ? AND quantity > 0
                """;

        try (Connection con = DriverManager.getConnection(URL)) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, bookId);

                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO book_issues (book_id, student_id)
                    VALUES (?, ?)
                    """)) {

                ps.setString(1, bookId);
                ps.setString(2, studentId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        }
    }

    public boolean returnBook(int issueId) throws SQLException {
        String sql = """
                UPDATE book_issues
                SET return_date = CURRENT_DATE
                WHERE issue_id = ? AND return_date IS NULL
                """;

        try (Connection con = DriverManager.getConnection(URL)) {
            con.setAutoCommit(false);

            String bookId = null;

            try (PreparedStatement ps = con.prepareStatement("""
                    SELECT book_id FROM book_issues
                    WHERE issue_id = ? AND return_date IS NULL
                    """)) {

                ps.setInt(1, issueId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getString("book_id");
                    }
                }
            }

            if (bookId == null) {
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("""
                    UPDATE books SET quantity = quantity + 1
                    WHERE book_id = ?
                    """)) {

                ps.setString(1, bookId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        }
    }

    public List<String[]> getActiveIssues() throws SQLException {
        List<String[]> issues = new ArrayList<>();

        String sql = """
                SELECT i.issue_id, i.book_id, b.title, i.student_id, i.issue_date
                FROM book_issues i
                JOIN books b ON i.book_id = b.book_id
                WHERE i.return_date IS NULL
                ORDER BY i.issue_id
                """;

        try (Connection con = DriverManager.getConnection(URL);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                issues.add(new String[] {
                        String.valueOf(rs.getInt("issue_id")),
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("student_id"),
                        rs.getString("issue_date")
                });
            }
        }

        return issues;
    }
}
