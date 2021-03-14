package ir.sharif.math.ds_hw3.logic;

import ir.sharif.math.ds_hw3.util.LList;
import ir.sharif.math.ds_hw3.util.Node;
import ir.sharif.math.ds_hw3.util.Pair;

import java.util.*;

public class DataHolder {
    public static final long P = 1669L;
    private final int c, w, b, v, lowerBoundForMatch;
    private final HashMap<Long, NavigableSet<Pair>> hashes;
    private final LList<Data> window, buffer;

    public DataHolder(int b, int v) {
        this.b = b;
        this.v = v;
        this.c = (int) Math.pow(2, b);
        this.w = (int) Math.pow(2, v);
        this.lowerBoundForMatch = 1 + (b + v + 7) / 8;
        hashes = new HashMap<>();
        window = new LList<>();
        buffer = new LList<>();
    }

    public Pair searchHash(long hash, int size) {
        NavigableSet<Pair> result = hashes.get(hash);
        if (result != null) {
            Iterator<Pair> iterator = result.descendingIterator();
            while (iterator.hasNext()) {
                Pair dataPair = iterator.next();
                boolean b = checkEquality(dataPair, buffer.getNode(0), size);
                if (b)
                    return dataPair;
            }
        }
        return null;
    }

    private boolean checkEquality(Pair first, Node<Data> start2, int size) {
        Node<Data> start1 = first.getStart();
        if (first.getEnd().getValue().getIndex() - start1.getValue().getIndex() + 1 != size) return false;
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(start1.getValue(), start2.getValue())) return false;
            start1 = start1.getNext();
            start2 = start2.getNext();
        }
        return true;
    }

    public void addToWindow(Data data) {
        window.addLast(data);
        updateForwardHashes();
        if (window.size() == w + 1) {
            updateBackwardHashes();
            window.remove(0);
        }
    }

    private void updateForwardHashes() {
        long hash = 0, x = 1;
        Node<Data> last = window.getNode(window.size() - 1);
        int i = 0;
        Node<Data> node = last;
        while (i + 1 <= c && node != null) {
            hash = hash + node.getValue().getIntegerValue() * x;
            if (i + 1 > lowerBoundForMatch) {
                NavigableSet<Pair> set = hashes.computeIfAbsent(hash, k -> new TreeSet<>());
                set.add(new Pair(node, last));
            }
            x *= P;
            i++;
            node = node.getPrev();
        }
    }

    private void updateBackwardHashes() {
        long hash = 0;
        Node<Data> first = window.getNode(0);
        int i = 0;
        Node<Data> node = first;
        while (i + 1 <= c && node != null) {
            hash = hash * P + node.getValue().getIntegerValue();
            if (i + 1 > lowerBoundForMatch) {
                NavigableSet<Pair> set = hashes.get(hash);
                set.pollFirst();
                if (set.size() == 0) hashes.remove(hash);
            }
            i++;
            node = node.getNext();
        }
    }

    public int getC() {
        return c;
    }

    public int getW() {
        return w;
    }

    public int getB() {
        return b;
    }

    public int getV() {
        return v;
    }

    public HashMap<Long, NavigableSet<Pair>> getHashes() {
        return hashes;
    }

    public LList<Data> getWindow() {
        return window;
    }

    public LList<Data> getBuffer() {
        return buffer;
    }

    public int getLowerBoundForMatch() {
        return lowerBoundForMatch;
    }
}
