
package campus.ui;

import campus.db.ClassroomRepository;
import campus.db.TimetableRepository;
import campus.ds.TimetableLinkedList;
import campus.model.Classroom;
import campus.model.TimetableEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TimetablePanel extends JPanel {

    private final TimetableLinkedList schedules = new TimetableLinkedList();

    private final TimetableRepository repository;
    private final ClassroomRepository classroomRepository;

    private final JTextField subjectField = new JTextField(15);
    private final JTextField facultyField = new JTextField(15);
    private final JTextField roomField = new JTextField(15);

    private final JComboBox<String> dayBox = new JComboBox<>(
            new String[] { "Monday", "Tuesday", "Wednesday",
                    "Thursday", "Friday", "Saturday" });

    private final JComboBox<String> timeBox = new JComboBox<>(
            new String[] { "09:00-10:00", "10:00-11:00",
                    "11:15-12:15", "12:15-13:15",
                    "14:00-15:00", "15:00-16:00" });

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "ID", "Subject", "Faculty",
                    "Day", "Time Slot", "Room" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public TimetablePanel() {
        try {
            repository = new TimetableRepository();
            classroomRepository = new ClassroomRepository();

            for (TimetableEntry entry : repository.getAll()) {
                schedules.add(entry);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Could not initialize timetable database.", ex);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Timetable Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder(
                "Schedule a Class"));

        form.add(new JLabel("Subject:"));
        form.add(subjectField);

        form.add(new JLabel("Faculty:"));
        form.add(facultyField);

        form.add(new JLabel("Day:"));
        form.add(dayBox);

        form.add(new JLabel("Time Slot:"));
        form.add(timeBox);

        form.add(new JLabel("Classroom Number:"));
        form.add(roomField);

        JButton addButton = new JButton("Add Schedule");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttons, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        table.setPreferredScrollableViewportSize(new Dimension(700, 220));
        table.setFillsViewportHeight(true);

        addButton.addActionListener(e -> addSchedule());
        deleteButton.addActionListener(e -> deleteSchedule());
        clearButton.addActionListener(e -> clearFields());

        refreshTable();
    }

    private void addSchedule() {
        String subject = subjectField.getText().trim();
        String faculty = facultyField.getText().trim();
        String day = (String) dayBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();
        String room = roomField.getText().trim();

        if (subject.isEmpty() || faculty.isEmpty() || room.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        try {
            boolean roomExists = false;

            for (Classroom classroom : classroomRepository.getAll()) {
                if (classroom.getRoomNumber()
                        .equalsIgnoreCase(room)) {
                    roomExists = true;
                    break;
                }
            }

            if (!roomExists) {
                showMessage(
                        "Classroom not found. Add it in the Classrooms tab first.");
                return;
            }

            TimetableEntry entry = new TimetableEntry(
                    0, subject, faculty, day, time, room);

            String conflict = schedules.findConflict(entry);

            if (conflict != null) {
                showMessage(conflict);
                return;
            }

            int id = repository.insert(entry);
            entry.setId(id);
            schedules.add(entry);

            refreshTable();
            clearFields();
            showMessage("Timetable entry added successfully.");

        } catch (SQLException ex) {
            showMessage("Could not save schedule: " + ex.getMessage());
        }
    }

    private void deleteSchedule() {
        int row = table.getSelectedRow();

        if (row == -1) {
            showMessage("Please select a timetable entry.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        try {
            if (repository.delete(id)) {
                schedules.delete(id);
                refreshTable();
                clearFields();
                showMessage("Schedule deleted successfully.");
            } else {
                showMessage("Schedule not found.");
            }
        } catch (SQLException ex) {
            showMessage("Could not delete schedule: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (TimetableEntry entry : schedules.getAll()) {
            tableModel.addRow(new Object[] {
                    entry.getId(),
                    entry.getSubject(),
                    entry.getFaculty(),
                    entry.getDay(),
                    entry.getTimeSlot(),
                    entry.getRoomNumber()
            });
        }
    }

    private void clearFields() {
        subjectField.setText("");
        facultyField.setText("");
        roomField.setText("");
        dayBox.setSelectedIndex(0);
        timeBox.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
