package campus;

import java.awt.*;
import javax.swing.*;
import campus.ui.StudentPanel;
import campus.ui.ClassroomPanel;
import campus.ui.TimetablePanel;
import campus.ui.LibraryPanel;
import campus.ui.AttendancePanel;
import campus.ui.CampusNavigationPanel;
import campus.ui.DSAlgorithmLabPanel;

public class Main extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public Main() {
        setTitle("DS Smart Campus Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 5, 10));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(30, 41, 59));

        String[] modules = {
                "Dashboard",
                "Student Management",
                "Classroom & Timetable",
                "Library Management",
                "Attendance Management",
                "Campus Navigation",
                "DS Algorithm Lab"
        };

        // Main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        addPage("Dashboard", createDashboard());

        for (int i = 1; i < modules.length; i++) {
            if (modules[i].equals("Student Management")) {
                addPage(modules[i], new StudentPanel());

            } else if (modules[i].equals("Classroom & Timetable")) {
                JTabbedPane tabs = new JTabbedPane();
                tabs.addTab("Classrooms", new ClassroomPanel());
                tabs.addTab("Timetable", new TimetablePanel());
                addPage(modules[i], tabs);

            } else if (modules[i].equals("Library Management")) {
                addPage(modules[i], new LibraryPanel());

            } else if (modules[i].equals("Attendance Management")) {
                addPage(modules[i], new AttendancePanel());

            } else if (modules[i].equals("Campus Navigation")) {
                addPage(modules[i], new CampusNavigationPanel());

            } else if (modules[i].equals("DS Algorithm Lab")) {
                addPage(modules[i], new DSAlgorithmLabPanel());

            } else {
                addPage(modules[i], createPlaceholder(modules[i]));
            }
        }

        // Sidebar buttons
        for (String module : modules) {
            JButton button = new JButton(module);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(30, 41, 59));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setMargin(new Insets(10, 15, 10, 5));

            button.addActionListener(e -> cardLayout.show(contentPanel, module));

            sidebar.add(button);
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addPage(String name, JComponent panel) {
        contentPanel.add(panel, name);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        panel.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel("Smart Campus Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitle = new JLabel(
                "<html>Manage students, classrooms, library records,<br>" +
                        "attendance and campus locations from one place.</html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel top = new JPanel(new GridLayout(2, 1, 5, 10));
        top.setOpaque(false);
        top.add(heading);
        top.add(subtitle);

        JPanel cards = new JPanel(new GridLayout(0, 2, 15, 15));
        cards.setOpaque(false);

        String[] features = {
                "Student Management",
                "Classroom & Timetable",
                "Library Management",
                "Attendance Management",
                "Campus Navigation",
                "DS Algorithm Lab"
        };

        for (String feature : features) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            new Color(220, 225, 232)),
                    BorderFactory.createEmptyBorder(20, 15, 20, 15)));

            JLabel label = new JLabel(feature);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            card.add(label, BorderLayout.CENTER);
            cards.add(card);
        }

        panel.add(top, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlaceholder(String module) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        JLabel label = new JLabel(
                "<html><center><h1>" + module + "</h1>" +
                        "<p>This module will be implemented in the next stages.</p>" +
                        "</center></html>");

        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}