
package campus.ds;

import java.util.ArrayList;
import java.util.List;

public class BookRequestQueue {

    private static class Node {
        String request;
        Node next;

        Node(String request) {
            this.request = request;
        }
    }

    private Node front;
    private Node rear;

    public void enqueue(String request) {
        Node newNode = new Node(request);

        if (rear == null) {
            front = rear = newNode;
            return;
        }

        rear.next = newNode;
        rear = newNode;
    }

    public String dequeue() {
        if (front == null) {
            return null;
        }

        String request = front.request;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        return request;
    }

    public List<String> getAll() {
        List<String> requests = new ArrayList<>();
        Node current = front;

        while (current != null) {
            requests.add(current.request);
            current = current.next;
        }

        return requests;
    }

    public boolean isEmpty() {
        return front == null;
    }
}
