
package campus.ds;

import campus.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookLinkedList {

    private static class Node {
        Book data;
        Node next;

        Node(Book data) {
            this.data = data;
        }
    }

    private Node head;

    public boolean add(Book book) {
        if (findById(book.getBookId()) != null) {
            return false;
        }

        Node newNode = new Node(book);

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

    public Book findById(String id) {
        Node current = head;

        while (current != null) {
            if (current.data.getBookId().equalsIgnoreCase(id)) {
                return current.data;
            }

            current = current.next;
        }

        return null;
    }

    public boolean delete(String id) {
        if (head == null) {
            return false;
        }

        if (head.data.getBookId().equalsIgnoreCase(id)) {
            head = head.next;
            return true;
        }

        Node current = head;

        while (current.next != null) {
            if (current.next.data.getBookId().equalsIgnoreCase(id)) {
                current.next = current.next.next;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        Node current = head;

        while (current != null) {
            books.add(current.data);
            current = current.next;
        }

        return books;
    }
}
