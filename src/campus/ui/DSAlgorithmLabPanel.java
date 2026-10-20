package campus.ui;

import campus.ds.DSAlgorithms;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class DSAlgorithmLabPanel extends JPanel {

    private final ArrayStack stack = new ArrayStack(20);
    private final CircularQueue queue = new CircularQueue(20);

    public DSAlgorithmLabPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel("DS Algorithm Lab");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sorting", createSortingPanel());
        tabs.addTab("Searching", createSearchingPanel());
        tabs.addTab("Stack & Queue", createStackQueuePanel());
        tabs.addTab("Hashing", createHashingPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createSortingPanel() {
        JPanel panel = createFormPanel();

        JTextField numbersField = new JTextField("45, 12, 78, 23, 9", 25);
        JComboBox<String> methodBox = new JComboBox<>(new String[] {
                "Bubble Sort", "Selection Sort",
                "Insertion Sort", "Quick Sort"
        });
        JButton sortButton = new JButton("Sort");
        JLabel result = new JLabel("Result will appear here.");

        addRow(panel, 0, "Enter numbers:", numbersField);
        addRow(panel, 1, "Algorithm:", methodBox);
        addRow(panel, 2, "", sortButton);
        addRow(panel, 3, "Sorted array:", result);

        sortButton.addActionListener(e -> {
            try {
                int[] values = parseNumbers(numbersField.getText());
                int[] sorted = DSAlgorithms.sort(
                        values, (String) methodBox.getSelectedItem());
                result.setText(Arrays.toString(sorted));
            } catch (Exception ex) {
                showError("Enter valid integers separated by commas.");
            }
        });

        return panel;
    }

    private JPanel createSearchingPanel() {
        JPanel panel = createFormPanel();

        JTextField numbersField = new JTextField("10, 4, 25, 8, 15", 25);
        JTextField targetField = new JTextField("8", 10);
        JComboBox<String> methodBox = new JComboBox<>(
                new String[] { "Linear Search", "Binary Search" });
        JButton searchButton = new JButton("Search");
        JLabel result = new JLabel("Search result will appear here.");

        addRow(panel, 0, "Enter numbers:", numbersField);
        addRow(panel, 1, "Search value:", targetField);
        addRow(panel, 2, "Algorithm:", methodBox);
        addRow(panel, 3, "", searchButton);
        addRow(panel, 4, "Result:", result);

        searchButton.addActionListener(e -> {
            try {
                int[] values = parseNumbers(numbersField.getText());
                int target = Integer.parseInt(targetField.getText().trim());
                int index;

                if ("Binary Search".equals(methodBox.getSelectedItem())) {
                    Arrays.sort(values);
                    index = DSAlgorithms.binarySearch(values, target);
                    result.setText(index == -1
                            ? "Not found. Sorted array: " + Arrays.toString(values)
                            : "Found at index " + index
                                    + " in sorted array " + Arrays.toString(values));
                } else {
                    index = DSAlgorithms.linearSearch(values, target);
                    result.setText(index == -1
                            ? "Value not found."
                            : "Found at index " + index
                                    + " in the original array.");
                }
            } catch (Exception ex) {
                showError("Enter valid integers and a search value.");
            }
        });

        return panel;
    }

    private JPanel createStackQueuePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Stack section
        JPanel stackPanel = createFormPanel();
        JTextField stackField = new JTextField(10);
        JLabel stackResult = new JLabel("Stack: empty");

        JButton pushButton = new JButton("Push");
        JButton popButton = new JButton("Pop");
        JButton peekButton = new JButton("Peek");

        addRow(stackPanel, 0, "Stack value:", stackField);
        addRow(stackPanel, 1, "", pushButton);
        addRow(stackPanel, 2, "", popButton);
        addRow(stackPanel, 3, "", peekButton);
        addRow(stackPanel, 4, "Contents:", stackResult);

        pushButton.addActionListener(e -> {
            try {
                int value = Integer.parseInt(stackField.getText().trim());
                if (!stack.push(value)) {
                    showError("Stack overflow: stack is full.");
                }
                stackResult.setText(stack.display());
            } catch (Exception ex) {
                showError("Enter a valid integer.");
            }
        });

        popButton.addActionListener(e -> {
            Integer value = stack.pop();
            if (value == null) {
                showError("Stack underflow: stack is empty.");
            } else {
                JOptionPane.showMessageDialog(this, "Popped: " + value);
            }
            stackResult.setText(stack.display());
        });

        peekButton.addActionListener(e -> {
            Integer value = stack.peek();
            JOptionPane.showMessageDialog(this,
                    value == null ? "Stack is empty." : "Top value: " + value);
        });

        // Queue section
        JPanel queuePanel = createFormPanel();
        JTextField queueField = new JTextField(10);
        JLabel queueResult = new JLabel("Queue: empty");

        JButton enqueueButton = new JButton("Enqueue");
        JButton dequeueButton = new JButton("Dequeue");

        addRow(queuePanel, 0, "Queue value:", queueField);
        addRow(queuePanel, 1, "", enqueueButton);
        addRow(queuePanel, 2, "", dequeueButton);
        addRow(queuePanel, 3, "Contents:", queueResult);

        enqueueButton.addActionListener(e -> {
            try {
                int value = Integer.parseInt(queueField.getText().trim());
                if (!queue.enqueue(value)) {
                    showError("Queue is full.");
                }
                queueResult.setText(queue.display());
            } catch (Exception ex) {
                showError("Enter a valid integer.");
            }
        });

        dequeueButton.addActionListener(e -> {
            Integer value = queue.dequeue();
            if (value == null) {
                showError("Queue is empty.");
            } else {
                JOptionPane.showMessageDialog(this, "Dequeued: " + value);
            }
            queueResult.setText(queue.display());
        });

        panel.add(stackPanel);
        panel.add(queuePanel);
        return panel;
    }

    private JPanel createHashingPanel() {
        JPanel panel = createFormPanel();

        JTextField keysField = new JTextField("39, 54, 88, 42, 64, 27", 25);
        JButton hashButton = new JButton("Insert using Linear Probing");
        JTextArea result = new JTextArea(14, 30);
        result.setEditable(false);
        result.setFont(new Font("Monospaced", Font.PLAIN, 13));

        addRow(panel, 0, "Enter keys:", keysField);
        addRow(panel, 1, "Table size:", new JLabel("13"));
        addRow(panel, 2, "", hashButton);
        addRow(panel, 3, "Hash table:", new JScrollPane(result));

        hashButton.addActionListener(e -> {
            try {
                int[] keys = parseNumbers(keysField.getText());
                int[] table = DSAlgorithms.linearProbing(keys, 13);

                StringBuilder output = new StringBuilder();
                output.append("Hash function: h(k) = k mod 13\n\n");

                for (int i = 0; i < table.length; i++) {
                    output.append("Index ")
                            .append(i)
                            .append(" : ")
                            .append(table[i] == -1 ? "Empty" : table[i])
                            .append('\n');
                }

                result.setText(output.toString());
            } catch (Exception ex) {
                showError("Enter valid integer keys separated by commas.");
            }
        });

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void addRow(JPanel panel, int row, String label, Component field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private int[] parseNumbers(String text) {
        String[] parts = text.trim().split("[,\\s]+");

        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException("No numbers entered.");
        }

        int[] values = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i]);
        }

        return values;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom array-based Stack: LIFO
    private static class ArrayStack {
        private final int[] data;
        private int top = -1;

        ArrayStack(int capacity) {
            data = new int[capacity];
        }

        boolean push(int value) {
            if (top == data.length - 1) {
                return false;
            }
            data[++top] = value;
            return true;
        }

        Integer pop() {
            if (top == -1) {
                return null;
            }
            return data[top--];
        }

        Integer peek() {
            return top == -1 ? null : data[top];
        }

        String display() {
            if (top == -1) {
                return "empty";
            }

            StringBuilder result = new StringBuilder("Top → ");
            for (int i = top; i >= 0; i--) {
                result.append(data[i]).append("  ");
            }
            return result.toString();
        }
    }

    // Custom circular array Queue: FIFO
    private static class CircularQueue {
        private final int[] data;
        private int front = 0;
        private int rear = -1;
        private int count = 0;

        CircularQueue(int capacity) {
            data = new int[capacity];
        }

        boolean enqueue(int value) {
            if (count == data.length) {
                return false;
            }

            rear = (rear + 1) % data.length;
            data[rear] = value;
            count++;
            return true;
        }

        Integer dequeue() {
            if (count == 0) {
                return null;
            }

            int value = data[front];
            front = (front + 1) % data.length;
            count--;
            return value;
        }

        String display() {
            if (count == 0) {
                return "empty";
            }

            StringBuilder result = new StringBuilder("Front → ");
            for (int i = 0; i < count; i++) {
                result.append(data[(front + i) % data.length]).append("  ");
            }
            return result.toString();
        }
    }
}