
package campus.ui;

import campus.ds.CampusGraph;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CampusNavigationPanel extends JPanel {

    private final CampusGraph graph = new CampusGraph();

    private final JComboBox<String> startBox = new JComboBox<>();
    private final JComboBox<String> destinationBox = new JComboBox<>();

    private final JTextArea resultArea = new JTextArea();

    public CampusNavigationPanel() {
        buildCampusGraph();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Campus Navigation");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                "Choose Locations"));

        for (String location : graph.getLocations()) {
            startBox.addItem(location);
            destinationBox.addItem(location);
        }

        selectionPanel.add(new JLabel("Starting Location:"));
        selectionPanel.add(startBox);

        selectionPanel.add(new JLabel("Destination:"));
        selectionPanel.add(destinationBox);

        JButton bfsButton = new JButton("Find Shortest Route (BFS)");
        JButton dfsButton = new JButton("Explore Locations (DFS)");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(bfsButton);
        buttons.add(dfsButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(selectionPanel, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setText(
                "Choose your starting location and destination.\n"
                        + "BFS finds a route with the fewest connections.\n"
                        + "DFS displays the order in which locations are explored.");

        add(top, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        resultArea.setPreferredSize(new Dimension(600, 220));

        bfsButton.addActionListener(e -> findRoute());
        dfsButton.addActionListener(e -> exploreLocations());
    }

    private void buildCampusGraph() {
        graph.addLocation("Main Block");
        graph.addLocation("Library");
        graph.addLocation("Computer Lab");
        graph.addLocation("Canteen");
        graph.addLocation("Auditorium");
        graph.addLocation("Hostel");
        graph.addLocation("Sports Ground");

        graph.addConnection("Main Block", "Library");
        graph.addConnection("Main Block", "Computer Lab");
        graph.addConnection("Library", "Canteen");
        graph.addConnection("Computer Lab", "Auditorium");
        graph.addConnection("Canteen", "Hostel");
        graph.addConnection("Auditorium", "Sports Ground");
        graph.addConnection("Hostel", "Sports Ground");
    }

    private void findRoute() {
        String start = (String) startBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();

        if (start == null || destination == null) {
            return;
        }

        List<String> route = graph.bfs(start, destination);

        if (route.isEmpty()) {
            resultArea.setText("No route found between these locations.");
            return;
        }

        StringBuilder result = new StringBuilder();
        result.append("SHORTEST ROUTE USING BFS\n\n");

        for (int i = 0; i < route.size(); i++) {
            result.append(route.get(i));

            if (i < route.size() - 1) {
                result.append("\n   ↓\n");
            }
        }

        result.append("\n\nNumber of connections: ")
                .append(route.size() - 1);

        resultArea.setText(result.toString());
    }

    private void exploreLocations() {
        String start = (String) startBox.getSelectedItem();

        if (start == null) {
            return;
        }

        List<String> traversal = graph.dfs(start);

        StringBuilder result = new StringBuilder();
        result.append("DFS EXPLORATION ORDER\n\n");

        for (int i = 0; i < traversal.size(); i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(traversal.get(i))
                    .append("\n");
        }

        result.append("\nTotal locations visited: ")
                .append(traversal.size());

        resultArea.setText(result.toString());
    }
}
