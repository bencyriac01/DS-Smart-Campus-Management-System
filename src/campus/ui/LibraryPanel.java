
package campus.ui;

import campus.db.BookRepository;
import campus.ds.BookLinkedList;
import campus.ds.BookRequestQueue;
import campus.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryPanel extends JPanel {

    private final BookRepository repository;
    private final BookLinkedList books = new BookLinkedList();
    private final BookRequestQueue requestQueue = new BookRequestQueue();

    private final JTextField idField = new JTextField(12);
    private final JTextField titleField = new JTextField(16);
    private final JTextField authorField = new JTextField(16);
    private final JTextField quantityField = new JTextField(8);

    private final JTextField issueBookField = new JTextField(10);
    private final JTextField studentField = new JTextField(12);
    private final JTextField returnIssueField = new JTextField(8);

    private final JTextField requestBookField = new JTextField(12);
    private final JTextField requestStudentField = new JTextField(12);

    private final DefaultTableModel bookModel = new DefaultTableModel(
            new String[] { "Book ID", "Title", "Author", "Available" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel issueModel = new DefaultTableModel(
            new String[] { "Issue ID", "Book ID", "Title",
                    "Student ID", "Issue Date" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel requestModel = new DefaultTableModel(
            new String[] { "Position", "Request" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable bookTable = new JTable(bookModel);
    private final JTable issueTable = new JTable(issueModel);
    private final JTable requestTable = new JTable(requestModel);

    public LibraryPanel() {
        try {
            repository = new BookRepository();

            for (Book book : repository.getAll()) {
                books.add(book);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Could not initialize the library database.", ex);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel("Library Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksTab());
        tabs.addTab("Issue / Return", createIssueTab());
        tabs.addTab("Request Queue", createQueueTab());

        add(tabs, BorderLayout.CENTER);

        refreshBooks();
        refreshIssues();
        refreshQueue();
    }

    private JPanel createBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Book Details"));

        form.add(new JLabel("Book ID:"));
        form.add(idField);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);
        form.add(new JLabel("Total Available Copies:"));
        form.add(quantityField);

        JButton addButton = new JButton("Add Book");
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(searchButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addBook());
        searchButton.addActionListener(e -> searchBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearBookFields());

        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() >= 0) {
                int row = bookTable.convertRowIndexToModel(
                        bookTable.getSelectedRow());

                idField.setText(bookModel.getValueAt(row, 0).toString());
                titleField.setText(bookModel.getValueAt(row, 1).toString());
                authorField.setText(bookModel.getValueAt(row, 2).toString());
                quantityField.setText(bookModel.getValueAt(row, 3).toString());
            }
        });

        return panel;
    }

    private JPanel createIssueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel issueForm = new JPanel(new GridLayout(0, 2, 8, 8));
        issueForm.setBorder(BorderFactory.createTitledBorder("Issue a Book"));

        issueForm.add(new JLabel("Book ID:"));
        issueForm.add(issueBookField);
        issueForm.add(new JLabel("Student Register Number:"));
        issueForm.add(studentField);

        JButton issueButton = new JButton("Issue Book");
        issueButton.addActionListener(e -> issueBook());

        JPanel issueSection = new JPanel(new BorderLayout(5, 5));
        issueSection.add(issueForm, BorderLayout.CENTER);
        issueSection.add(issueButton, BorderLayout.SOUTH);

        JPanel returnForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        returnForm.setBorder(BorderFactory.createTitledBorder("Return a Book"));
        returnForm.add(new JLabel("Issue ID:"));
        returnForm.add(returnIssueField);

        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> returnBook());
        returnForm.add(returnButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(issueSection, BorderLayout.NORTH);
        top.add(returnForm, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(issueTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQueueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("New Book Request"));

        form.add(new JLabel("Book ID or Title:"));
        form.add(requestBookField);
        form.add(new JLabel("Student Register Number:"));
        form.add(requestStudentField);

        JButton addRequestButton = new JButton("Add Request");
        JButton nextButton = new JButton("Process Next Request");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addRequestButton);
        buttons.add(nextButton);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        addRequestButton.addActionListener(e -> addRequest());
        nextButton.addActionListener(e -> processNextRequest());

        return panel;
    }

    private void addBook() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()
                || quantityField.getText().trim().isEmpty()) {
            showMessage("Please fill in all book fields.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity <= 0) {
                showMessage("Quantity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Quantity must be a whole number.");
            return;
        }

        if (books.findById(id) != null) {
            showMessage("That Book ID already exists.");
            return;
        }

        Book book = new Book(id, title, author, quantity);

        try {
            repository.insert(book);
            books.add(book);
            refreshBooks();
            clearBookFields();
            showMessage("Book added successfully.");
        } catch (SQLException ex) {
            showMessage("Could not save book: " + ex.getMessage());
        }
    }

    private void searchBook() {
        String query = idField.getText().trim();

        if (query.isEmpty()) {
            query = titleField.getText().trim();
        }

        if (query.isEmpty()) {
            showMessage("Enter a Book ID or title to search.");
            return;
        }

        for (Book book : books.getAll()) {
            if (book.getBookId().equalsIgnoreCase(query)
                    || book.getTitle().equalsIgnoreCase(query)) {
                idField.setText(book.getBookId());
                titleField.setText(book.getTitle());
                authorField.setText(book.getAuthor());
                quantityField.setText(String.valueOf(book.getQuantity()));

                selectBookRow(book.getBookId());
                return;
            }
        }

        showMessage("No matching book found.");
    }

    private void selectBookRow(String bookId) {
        for (int i = 0; i < bookModel.getRowCount(); i++) {
            if (bookModel.getValueAt(i, 0).toString()
                    .equalsIgnoreCase(bookId)) {
                int viewRow = bookTable.convertRowIndexToView(i);
                bookTable.setRowSelectionInterval(viewRow, viewRow);
                return;
            }
        }
    }

    private void deleteBook() {
        int row = bookTable.getSelectedRow();

        if (row < 0) {
            showMessage("Select a book from the table first.");
            return;
        }

        int modelRow = bookTable.convertRowIndexToModel(row);
        String id = bookModel.getValueAt(modelRow, 0).toString();

        try {
            if (repository.delete(id)) {
                books.delete(id);
                refreshBooks();
                clearBookFields();
                showMessage("Book deleted successfully.");
            } else {
                showMessage(
                        "Cannot delete this book while it has an active issue.");
            }
        } catch (SQLException ex) {
            showMessage("Could not delete book: " + ex.getMessage());
        }
    }

    private void issueBook() {
        String bookId = issueBookField.getText().trim();
        String studentId = studentField.getText().trim();

        if (bookId.isEmpty() || studentId.isEmpty()) {
            showMessage("Enter both Book ID and Student Register Number.");
            return;
        }

        try {
            boolean studentExists = false;

            for (campus.model.Student student : new campus.db.StudentRepository().getAll()) {
                if (student.getRegisterNumber()
                        .equalsIgnoreCase(studentId)) {
                    studentExists = true;
                    break;
                }
            }

            if (!studentExists) {
                showMessage("Student not found. Add the student first.");
                return;
            }

            if (books.findById(bookId) == null) {
                showMessage("Book not found.");
                return;
            }

            if (repository.issueBook(bookId, studentId)) {
                refreshBooksFromDatabase();
                refreshIssues();
                issueBookField.setText("");
                studentField.setText("");
                showMessage("Book issued successfully.");
            } else {
                showMessage("No available copies of this book.");
            }
        } catch (SQLException ex) {
            showMessage("Could not issue book: " + ex.getMessage());
        }
    }

    private void returnBook() {
        String value = returnIssueField.getText().trim();

        if (value.isEmpty()) {
            showMessage("Enter the Issue ID from the table.");
            return;
        }

        try {
            int issueId = Integer.parseInt(value);

            if (repository.returnBook(issueId)) {
                refreshBooksFromDatabase();
                refreshIssues();
                returnIssueField.setText("");
                showMessage("Book returned successfully.");
            } else {
                showMessage("Active issue not found for that Issue ID.");
            }
        } catch (NumberFormatException ex) {
            showMessage("Issue ID must be a whole number.");
        } catch (SQLException ex) {
            showMessage("Could not return book: " + ex.getMessage());
        }
    }

    private void addRequest() {
        String book = requestBookField.getText().trim();
        String student = requestStudentField.getText().trim();

        if (book.isEmpty() || student.isEmpty()) {
            showMessage("Enter both book details and student ID.");
            return;
        }

        String request = "Book: " + book + " | Student: " + student;
        requestQueue.enqueue(request);

        requestBookField.setText("");
        requestStudentField.setText("");
        refreshQueue();
    }

    private void processNextRequest() {
        String request = requestQueue.dequeue();

        if (request == null) {
            showMessage("The request queue is empty.");
            return;
        }

        refreshQueue();
        showMessage("Next request processed:\n" + request);
    }

    private void refreshBooks() {
        bookModel.setRowCount(0);

        for (Book book : books.getAll()) {
            bookModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getQuantity()
            });
        }
    }

    private void refreshBooksFromDatabase() throws SQLException {
        List<Book> savedBooks = repository.getAll();

        for (Book book : savedBooks) {
            Book existing = books.findById(book.getBookId());

            if (existing == null) {
                books.add(book);
            } else {
                existing.setTitle(book.getTitle());
                existing.setAuthor(book.getAuthor());
                existing.setQuantity(book.getQuantity());
            }
        }

        refreshBooks();
    }

    private void refreshIssues() {
        issueModel.setRowCount(0);

        try {
            for (String[] issue : repository.getActiveIssues()) {
                issueModel.addRow(issue);
            }
        } catch (SQLException ex) {
            showMessage("Could not load active issues: " + ex.getMessage());
        }
    }

    private void refreshQueue() {
        requestModel.setRowCount(0);
        int position = 1;

        for (String request : requestQueue.getAll()) {
            requestModel.addRow(new Object[] { position++, request });
        }
    }

    private void clearBookFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        quantityField.setText("");
        bookTable.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
