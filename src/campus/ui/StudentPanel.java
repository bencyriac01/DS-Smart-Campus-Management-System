package campus.ui;

import campus.ds.StudentLinkedList;
import campus.model.Student;
import campus.db.StudentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class StudentPanel extends JPanel {

    private final StudentLinkedList students = new StudentLinkedList();
    private final StudentRepository repository;

    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField departmentField = new JTextField(15);
    private final JTextField semesterField = new JTextField(15);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Register No.", "Name", "Department", "Semester" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public StudentPanel() {
        try {
            repository = new StudentRepository();

            for (Student student : repository.getAll()) {
                students.add(student);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Could not initialize the student database.", ex);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createForm(), BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });

        refreshTable();
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 5));

        form.add(new JLabel("Register Number"));
        form.add(new JLabel("Student Name"));
        form.add(new JLabel("Department"));
        form.add(new JLabel("Semester"));

        form.add(idField);
        form.add(nameField);
        form.add(departmentField);
        form.add(semesterField);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton searchButton = new JButton("Search by ID");
        JButton sortButton = new JButton("Sort by Name");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        sortButton.addActionListener(e -> {
            students.sortByName();
            refreshTable();
            clearFields();
        });
        clearButton.addActionListener(e -> {
            table.clearSelection();
            clearFields();
        });

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(searchButton);
        buttons.add(sortButton);
        buttons.add(clearButton);

        return buttons;
    }

    private void addStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (id.isEmpty() || name.isEmpty()
                || department.isEmpty() || semesterText.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        int semester;

        try {
            semester = Integer.parseInt(semesterText);

            if (semester < 1 || semester > 12) {
                showMessage("Semester must be between 1 and 12.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Semester must be a valid number.");
            return;
        }

        Student student = new Student(id, name, department, semester);

        if (!students.add(student)) {
            showMessage("Register number already exists.");
            return;
        }

        try {
            // Changed from repository.add(student) to repository.insert(student)
            repository.insert(student);
        } catch (SQLException ex) {
            showMessage("Database error while adding student: " + ex.getMessage());
            return;
        }

        refreshTable();
        clearFields();
        showMessage("Student added successfully.");
    }

    private void searchStudent() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showMessage("Enter a register number to search.");
            return;
        }

        Student student = students.findById(id);

        if (student == null) {
            showMessage("Student not found.");
            return;
        }

        nameField.setText(student.getName());
        departmentField.setText(student.getDepartment());
        semesterField.setText(String.valueOf(student.getSemester()));

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString()
                    .equalsIgnoreCase(id)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void updateStudent() {
        String id = idField.getText().trim();
        Student student = students.findById(id);

        if (student == null) {
            showMessage("Search for an existing student first.");
            return;
        }

        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (name.isEmpty() || department.isEmpty()
                || semesterText.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        try {
            int semester = Integer.parseInt(semesterText);

            if (semester < 1 || semester > 12) {
                showMessage("Semester must be between 1 and 12.");
                return;
            }

            student.setName(name);
            student.setDepartment(department);
            student.setSemester(semester);

            repository.update(student);

            refreshTable();
            clearFields();
            showMessage("Student updated successfully.");

        } catch (NumberFormatException ex) {
            showMessage("Semester must be a valid number.");
        } catch (SQLException ex) {
            showMessage("Database error while updating student: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showMessage("Enter a register number to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete student " + id + "?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        if (students.delete(id)) {
            try {
                repository.delete(id);
                refreshTable();
                clearFields();
                showMessage("Student deleted successfully.");
            } catch (SQLException ex) {
                showMessage("Database error while deleting student: " + ex.getMessage());
            }
        } else {
            showMessage("Student not found.");
        }
    }

    private void loadSelectedStudent() {
        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        departmentField.setText(tableModel.getValueAt(row, 2).toString());
        semesterField.setText(tableModel.getValueAt(row, 3).toString());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Student student : students.getAll()) {
            tableModel.addRow(new Object[] {
                    student.getRegisterNumber(),
                    student.getName(),
                    student.getDepartment(),
                    student.getSemester()
            });
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        departmentField.setText("");
        semesterField.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}