
package campus.ds;

import campus.model.Attendance;
import java.util.ArrayList;
import java.util.List;

public class AttendanceLinkedList {

    private static class Node {
        Attendance data;
        Node next;

        Node(Attendance data) {
            this.data = data;
        }
    }

    private Node head;

    public boolean add(Attendance attendance) {
        Node current = head;

        while (current != null) {
            Attendance existing = current.data;

            if (existing.getStudentId().equalsIgnoreCase(
                    attendance.getStudentId())
                    && existing.getSubject().equalsIgnoreCase(
                            attendance.getSubject())
                    && existing.getDate().equals(
                            attendance.getDate())) {
                return false;
            }

            current = current.next;
        }

        Node newNode = new Node(attendance);

        if (head == null) {
            head = newNode;
        } else {
            current = head;

            while (current.next != null) {
                current = current.next;
            }

            current.next = newNode;
        }

        return true;
    }

    public List<Attendance> getAll() {
        List<Attendance> list = new ArrayList<>();
        Node current = head;

        while (current != null) {
            list.add(current.data);
            current = current.next;
        }

        return list;
    }
}
