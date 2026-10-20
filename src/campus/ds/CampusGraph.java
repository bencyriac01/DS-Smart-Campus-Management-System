
package campus.ds;

import java.util.*;

public class CampusGraph {

    private final Map<String, List<String>> adjacencyList = new LinkedHashMap<>();

    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addConnection(String first, String second) {
        addLocation(first);
        addLocation(second);

        if (!adjacencyList.get(first).contains(second)) {
            adjacencyList.get(first).add(second);
        }

        if (!adjacencyList.get(second).contains(first)) {
            adjacencyList.get(second).add(first);
        }
    }

    public Set<String> getLocations() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    // Breadth-First Search: finds a route with the fewest connections.
    public List<String> bfs(String start, String destination) {
        List<String> empty = new ArrayList<>();

        if (!adjacencyList.containsKey(start)
                || !adjacencyList.containsKey(destination)) {
            return empty;
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.remove();

            if (current.equals(destination)) {
                break;
            }

            for (String neighbour : adjacencyList.get(current)) {
                if (visited.add(neighbour)) {
                    parent.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }

        if (!visited.contains(destination)) {
            return empty;
        }

        List<String> path = new ArrayList<>();
        String current = destination;

        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    // Depth-First Search: explores one branch before another.
    public List<String> dfs(String start) {
        List<String> result = new ArrayList<>();

        if (!adjacencyList.containsKey(start)) {
            return result;
        }

        Set<String> visited = new LinkedHashSet<>();
        dfsHelper(start, visited);

        result.addAll(visited);
        return result;
    }

    private void dfsHelper(String current, Set<String> visited) {
        visited.add(current);

        for (String neighbour : adjacencyList.get(current)) {
            if (!visited.contains(neighbour)) {
                dfsHelper(neighbour, visited);
            }
        }
    }
}
