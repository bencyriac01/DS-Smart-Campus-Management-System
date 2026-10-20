
package campus.ds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import campus.model.Student;

public class StudentLinkedList {

    private static class Node {
        Student data;
        Node next;

        Node(Student data) {
            this.data = data;
        }
    }

    private Node head;

    public boolean add(Student student) {
        if (findById(student.getRegisterNumber()) != null) {
            return false;
        }

        Node newNode = new Node(student);

        if (head == null) {
            head = newNode;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
        return true;
    }

    public Student findById(String registerNumber) {
        Node current = head;

        while (current != null) {
            if (current.data.getRegisterNumber()
                    .equalsIgnoreCase(registerNumber)) {
                return current.data;
            }

            current = current.next;
        }

        return null;
    }

    public boolean delete(String registerNumber) {
        if (head == null) {
            return false;
        }

        if (head.data.getRegisterNumber()
                .equalsIgnoreCase(registerNumber)) {
            head = head.next;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            if (current.next.data.getRegisterNumber()
                    .equalsIgnoreCase(registerNumber)) {
                current.next = current.next.next;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        Node current = head;

        while (current != null) {
            students.add(current.data);
            current = current.next;
        }

        return students;
    }

    public void sortByName() {
        List<Student> students = getAll();

        students.sort(Comparator.comparing(
                Student::getName,
                String.CASE_INSENSITIVE_ORDER));

        head = null;

        for (Student student : students) {
            add(student);
        }
    }
}
