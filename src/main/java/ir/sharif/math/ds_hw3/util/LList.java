package ir.sharif.math.ds_hw3.util;


import java.util.Comparator;
import java.util.function.BiPredicate;

public class LList<T> implements Iterable<T> {
    private Node<T> first, last;
    private int size;

    public LList() {
        size = 0;
        first = last = null;
    }

    public int size() {
        return size;
    }

    public void addLast(T t) {
        Node<T> node = new Node<>(t);
        if (size == 0) {
            first = last = node;
        } else {
            node.prev = last;
            last = last.next = node;
        }
        size++;
    }

    void addBefore(T t, Node<T> node) {
        Node<T> newNode = new Node<>(t, node, node.prev);
        if (node.prev == null)
            first = newNode;
        else
            node.prev.next = newNode;
        node.prev = newNode;
        size++;
    }

    void remove(Node<T> node) {
        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }
        size--;
    }

    public T remove(int index) {
        Node<T> node = getNode(index);
        remove(node);
        return node.getValue();
    }


    public T first() {
        return first.value;
    }

    public T get(int i) {
        if (i < 0 || size <= i) throw new IndexOutOfBoundsException();
        return getNode(i).value;
    }

    public Node<T> getNode(int index) {
        if (index < 0 || size <= index) throw new IndexOutOfBoundsException();
        Node<T> x;
        if (index < (size >> 1)) {
            x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
        } else {
            x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
        }
        return x;
    }

    public <R> boolean contains(BiPredicate<T, R> predicate, R r) {
        for (T t : this) {
            if (predicate.test(t, r)) return true;
        }
        return false;
    }

    public boolean contains(T t) {
        return contains(Object::equals, t);
    }

    @Override
    public MyIterator<T> iterator() {
        return iterator(0);
    }

    public MyIterator<T> iterator(int index) {
        return new MyIterator<>(index, this);
    }

    public void addAll(LList<T> lList) {
        for (T t : lList) this.addLast(t);
    }

    public void sort(Comparator<T> comparator) {
        first = sort(first, comparator);
        size = 0;
        for (Node<T> i = first; i != null; i = i.next) {
            last = i;
            size++;
        }
    }

    Node<T> sort(Node<T> first, Comparator<T> comparator) {
        if (first == null) return null;
        if (first.next == null) return first;
        Node<T> middle = split(first);
        first = sort(first, comparator);
        middle = sort(middle, comparator);
        first = merge(first, middle, comparator);
        return first;
    }

    private Node<T> split(Node<T> first) {
        assert first != null;
        Node<T> middle = first;
        boolean a = false;
        for (Node<T> last = first; last != null; last = last.next) {
            if (a) {
                middle = middle.next;
            }
            a = !a;
        }
        middle.prev = middle.prev.next = null;
        return middle;
    }

    private Node<T> merge(Node<T> first, Node<T> middle, Comparator<T> comparator) {
        Node<T> i = first, j = middle;
        while (j != null) {
            Node<T> temp = j.next;
            if (comparator.compare(i.value, j.value) > 0) {
                if (i.prev != null) {
                    i.prev.next = j;
                } else {
                    first = j;
                }
                j.prev = i.prev;
                j.next = i;
                i.prev = j;
            } else {
                if (i.next != null) {
                    i = i.next;
                    continue;
                } else {
                    j.next = null;
                    j.prev = i;
                    i.next = j;
                }
            }
            j = temp;
        }
        return first;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (T t : this) {
            builder.append(t);
            builder.append(", ");
        }
        if (builder.length() > 1)
            builder.delete(builder.length() - 2, builder.length());
        builder.append("}");
        return builder.toString();
    }
}