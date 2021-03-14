 package ir.sharif.math.ds_hw3.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class MyIterator<T> implements ListIterator<T> {
    private Node<T> lastReturned, next;
    private int nextIndex;
    private final LList<T> list;

    MyIterator(int index, LList<T> list) {
        this.list = list;
        next = (index == list.size()) ? null : list.getNode(index);
        nextIndex = index;
    }

    public boolean hasNext() {
        return nextIndex < list.size();
    }

    public T next() {
        if (!hasNext())
            throw new NoSuchElementException();
        lastReturned = next;
        next = next.next;
        nextIndex++;
        return lastReturned.value;
    }

    public boolean hasPrevious() {
        return nextIndex > 0;
    }

    public T previous() {
        if (!hasPrevious())
            throw new NoSuchElementException();
        lastReturned = next = (next == null) ? list.getNode(list.size() - 1) : next.prev;
        nextIndex--;
        return lastReturned.value;
    }

    public int nextIndex() {
        return nextIndex;
    }

    public int previousIndex() {
        return nextIndex - 1;
    }

    public void remove() {
        if (lastReturned == null)
            throw new IllegalStateException();
        Node<T> lastNext = lastReturned.next;
        list.remove(lastReturned);
        if (next == lastReturned)
            next = lastNext;
        else
            nextIndex--;
        lastReturned = null;
    }

    public void set(T e) {
        if (lastReturned == null)
            throw new IllegalStateException();
        lastReturned.value = e;
    }

    public void add(T t) {
        lastReturned = null;
        if (next == null)
            list.addLast(t);
        else
            list.addBefore(t, next);
        nextIndex++;
    }

    public void forEachRemaining(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        while (nextIndex < list.size()) {
            action.accept(next.value);
            lastReturned = next;
            next = next.next;
            nextIndex++;
        }
    }
}
