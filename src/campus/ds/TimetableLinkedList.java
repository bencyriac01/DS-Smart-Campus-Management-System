
package campus.ds;

import campus.model.TimetableEntry;
import java.util.ArrayList;
import java.util.List;

public class TimetableLinkedList {

    private static class Node {
        TimetableEntry data;
        Node next;

        Node(TimetableEntry data) {
            this.data = data;
        }
    }

    private Node head;

    // Returns a conflict message, or null if no conflict exists.
    public String findConflict(TimetableEntry entry) {
        Node current = head;

        while (current != null) {
            TimetableEntry existing = current.data;

            boolean sameDay = existing.getDay()
                    .equalsIgnoreCase(entry.getDay());

            boolean sameTime = existing.getTimeSlot()
                    .equalsIgnoreCase(entry.getTimeSlot());

            if (sameDay && sameTime) {
                if (existing.getRoomNumber()
                        .equalsIgnoreCase(entry.getRoomNumber())) {
                    return "This classroom is already booked at that time.";
                }

                if (existing.getFaculty()
                        .equalsIgnoreCase(entry.getFaculty())) {
                    return "This faculty member already has a class at that time.";
                }
            }

            current = current.next;
        }

        return null;
    }

    public void add(TimetableEntry entry) {
        Node newNode = new Node(entry);

        if (head == null) {
            head = newNode;
            return;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    public boolean delete(int id) {
        if (head == null) {
            return false;
        }

        if (head.data.getId() == id) {
            head = head.next;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            if (current.next.data.getId() == id) {
                current.next = current.next.next;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public List<TimetableEntry> getAll() {
        List<TimetableEntry> entries = new ArrayList<>();
        Node current = head;

        while (current != null) {
            entries.add(current.data);
            current = current.next;
        }

        return entries;
    }
}
