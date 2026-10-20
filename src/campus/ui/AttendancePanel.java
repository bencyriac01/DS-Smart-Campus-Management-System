
package campus.ui;

import campus.db.AttendanceRepository;
import campus.db.StudentRepository;
import campus.ds.AttendanceLinkedList;
import campus.model.Attendance;
import campus.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final AttendanceRepository repository;
    private final StudentRepository studentRepository;
    private final AttendanceLinkedList attendanceList = new AttendanceLinkedList();

    private final JTextField studentIdField = new JTextField(12);
    private final JTextField subjectField = new JTextField(12);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 12);

    private final JComboBox<String> statusBox = new JComboBox<>(new String[] { "Present", "Absent" });

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "ID", "Register No.", "Student",
                    "Subject", "Date", "Status" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public AttendancePanel() {
        try {
            repository = new AttendanceRepository();
            studentRepository = new StudentRepository();

            for (Attendance attendance : repository.getAll()) {
                attendanceList.add(attendance);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Could not initialize attendance database.", ex);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Attendance Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder(
                "Record Attendance"));

        form.add(new JLabel("Student Register Number:"));
        form.add(studentIdField);

        form.add(new JLabel("Subject:"));
        form.add(subjectField);

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton markButton = new JButton("Save Attendance");
        JButton percentageButton = new JButton("Check Percentage");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(markButton);
        buttons.add(percentageButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        table.setPreferredScrollableViewportSize(new Dimension(700, 220));

        markButton.addActionListener(e -> markAttendance());
        percentageButton.addActionListener(e -> checkPercentage());
        clearButton.addActionListener(e -> clearFields());

        refreshTable();
    }

    private void markAttendance() {
        String studentId = studentIdField.getText().trim();
        String subject = subjectField.getText().trim();
        String date = dateField.getText().trim();
        String status = (String) statusBox.getSelectedItem();

        if (studentId.isEmpty() || subject.isEmpty() || date.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        try {
            LocalDate.parse(date);
        } catch (Exception ex) {
            showMessage("Enter a valid date in YYYY-MM-DD format.");
            return;
        }

        try {
            Student selectedStudent = null;

            for (Student student : studentRepository.getAll()) {
                if (student.getRegisterNumber()
                        .equalsIgnoreCase(studentId)) {
                    selectedStudent = student;
                    break;
                }
            }

            if (selectedStudent == null) {
                showMessage(
                        "Student not found. Add the student first.");
                return;
            }

            Attendance attendance = new Attendance(
                    0,
                    selectedStudent.getRegisterNumber(),
                    selectedStudent.getName(),
                    subject,
                    date,
                    status);

            if (attendanceList.getAll().stream().anyMatch(a -> a.getStudentId().equalsIgnoreCase(studentId)
                    && a.getSubject().equalsIgnoreCase(subject)
                    && a.getDate().equals(date))) {
                showMessage(
                        "Attendance is already recorded for this student, subject and date.");
                return;
            }

            int id = repository.insert(attendance);
            attendance.setId(id);
            attendanceList.add(attendance);

            refreshTable();
            showMessage("Attendance saved successfully.");

        } catch (SQLException ex) {
            showMessage(
                    "Could not save attendance. Check for duplicate entries.\n"
                            + ex.getMessage());
        }
    }

    private void checkPercentage() {
        String studentId = studentIdField.getText().trim();
        String subject = subjectField.getText().trim();

        if (studentId.isEmpty() || subject.isEmpty()) {
            showMessage(
                    "Enter the student register number and subject first.");
            return;
        }

        try {
            double percentage = repository.getPercentage(studentId, subject);

            if (percentage < 0) {
                showMessage("No attendance records found for this student and subject.");
            } else {
                showMessage(String.format(
                        "Attendance percentage: %.2f%%", percentage));
            }
        } catch (SQLException ex) {
            showMessage("Could not calculate percentage: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Attendance> records = attendanceList.getAll();

        for (Attendance attendance : records) {
            tableModel.addRow(new Object[] {
                    attendance.getId(),
                    attendance.getStudentId(),
                    attendance.getStudentName(),
                    attendance.getSubject(),
                    attendance.getDate(),
                    attendance.getStatus()
            });
        }
    }

    private void clearFields() {
        studentIdField.setText("");
        subjectField.setText("");
        dateField.setText(LocalDate.now().toString());
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
