package campus.ui;

import campus.ds.ClassroomLinkedList;
import campus.model.Classroom;
import campus.db.ClassroomRepository;

import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClassroomPanel extends JPanel {

    private final ClassroomLinkedList classrooms = new ClassroomLinkedList();
    private final ClassroomRepository repository;

    private final JTextField roomField = new JTextField(12);
    private final JTextField buildingField = new JTextField(12);
    private final JTextField capacityField = new JTextField(12);

    private final JComboBox<String> typeBox = new JComboBox<>(new String[] {
            "Classroom", "Laboratory", "Seminar Hall"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {
                    "Room Number", "Building", "Capacity", "Room Type"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public ClassroomPanel() {
        try {
            repository = new ClassroomRepository();

            for (Classroom c : repository.getAll()) {
                classrooms.add(c);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Could not initialize the classroom database.", ex);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createForm(), BorderLayout.NORTH);

        table.setRowHeight(25);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(createButtons(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedClassroom();
            }
        });

        refreshTable();
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridLayout(2, 4, 10, 5));

        form.add(new JLabel("Room Number"));
        form.add(new JLabel("Building"));
        form.add(new JLabel("Capacity"));
        form.add(new JLabel("Room Type"));

        form.add(roomField);
        form.add(buildingField);
        form.add(capacityField);
        form.add(typeBox);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Classroom");
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addClassroom());
        searchButton.addActionListener(e -> searchClassroom());
        deleteButton.addActionListener(e -> deleteClassroom());
        clearButton.addActionListener(e -> clearFields());

        buttons.add(addButton);
        buttons.add(searchButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        return buttons;
    }

    private void addClassroom() {
        String room = roomField.getText().trim();
        String building = buildingField.getText().trim();
        String capacityText = capacityField.getText().trim();
        String type = (String) typeBox.getSelectedItem();

        if (room.isEmpty() || building.isEmpty()
                || capacityText.isEmpty()) {
            showMessage("Please fill in all fields.");
            return;
        }

        int capacity;

        try {
            capacity = Integer.parseInt(capacityText);

            if (capacity <= 0) {
                showMessage("Capacity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Capacity must be a valid number.");
            return;
        }

        if (classrooms.find(room) != null) {
            showMessage("Room number already exists.");
            return;
        }

        Classroom classroom = new Classroom(room, building, capacity, type);

        try {
            repository.insert(classroom);
            classrooms.add(classroom);
        } catch (SQLException ex) {
            showMessage("Could not save classroom: " + ex.getMessage());
            return;
        }

        refreshTable();
        clearFields();
        showMessage("Classroom added successfully.");
    }

    private void searchClassroom() {
        String room = roomField.getText().trim();

        if (room.isEmpty()) {
            showMessage("Enter a room number to search.");
            return;
        }

        Classroom c = classrooms.find(room);

        if (c == null) {
            showMessage("Classroom not found.");
            return;
        }

        buildingField.setText(c.getBuilding());
        capacityField.setText(String.valueOf(c.getCapacity()));
        typeBox.setSelectedItem(c.getRoomType());

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString()
                    .equalsIgnoreCase(room)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void deleteClassroom() {
        String room = roomField.getText().trim();

        if (room.isEmpty()) {
            showMessage("Enter a room number to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete classroom " + room + "?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (repository.delete(room)) {
                classrooms.delete(room);
                refreshTable();
                clearFields();
                showMessage("Classroom deleted successfully.");
            } else {
                showMessage("Classroom not found.");
            }
        } catch (SQLException ex) {
            showMessage("Could not delete classroom: " + ex.getMessage());
        }
    }

    private void loadSelectedClassroom() {
        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        roomField.setText(model.getValueAt(row, 0).toString());
        buildingField.setText(model.getValueAt(row, 1).toString());
        capacityField.setText(model.getValueAt(row, 2).toString());
        typeBox.setSelectedItem(model.getValueAt(row, 3).toString());
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Classroom c : classrooms.getAll()) {
            model.addRow(new Object[] {
                    c.getRoomNumber(),
                    c.getBuilding(),
                    c.getCapacity(),
                    c.getRoomType()
            });
        }
    }

    private void clearFields() {
        table.clearSelection();
        roomField.setText("");
        buildingField.setText("");
        capacityField.setText("");
        typeBox.setSelectedIndex(0);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}