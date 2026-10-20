
package campus.ds;

import campus.model.Classroom;
import java.util.ArrayList;
import java.util.List;

public class ClassroomLinkedList {

    private static class Node {
        Classroom data;
        Node next;

        Node(Classroom data) {
            this.data = data;
        }
    }

    private Node head;

    public boolean add(Classroom classroom) {
        if (find(classroom.getRoomNumber()) != null) {
            return false;
        }

        Node node = new Node(classroom);

        if (head == null) {
            head = node;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = node;
        return true;
    }

    public Classroom find(String roomNumber) {
        Node current = head;

        while (current != null) {
            if (current.data.getRoomNumber()
                    .equalsIgnoreCase(roomNumber)) {
                return current.data;
            }

            current = current.next;
        }

        return null;
    }

    public boolean delete(String roomNumber) {
        if (head == null) {
            return false;
        }

        if (head.data.getRoomNumber()
                .equalsIgnoreCase(roomNumber)) {
            head = head.next;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            if (current.next.data.getRoomNumber()
                    .equalsIgnoreCase(roomNumber)) {
                current.next = current.next.next;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public List<Classroom> getAll() {
        List<Classroom> list = new ArrayList<>();
        Node current = head;

        while (current != null) {
            list.add(current.data);
            current = current.next;
        }

        return list;
    }
}
