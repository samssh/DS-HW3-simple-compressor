package ir.sharif.math.ds_hw3.util;

public class Node<T> {
    T value;
    Node<T> next, prev;

    public Node(T value) {
        this.value = value;
    }

    public Node(T value, Node<T> next, Node<T> prev) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
