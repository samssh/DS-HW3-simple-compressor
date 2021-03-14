package ir.sharif.math.ds_hw3.util;

import ir.sharif.math.ds_hw3.logic.Data;

public class Pair implements Comparable<Pair>{
    private final Node<Data> start, end;

    public Pair(Node<Data> start, Node<Data> end) {
        this.start = start;
        this.end = end;
    }

    public Node<Data> getStart() {
        return start;
    }

    public Node<Data> getEnd() {
        return end;
    }

    @Override
    public int compareTo(Pair o) {
        if (this.start.getValue().getIndex()==o.start.getValue().getIndex()){
            return Long.compare(this.end.getValue().getIndex(),o.end.getValue().getIndex());
        }else return Long.compare(this.start.getValue().getIndex(),o.start.getValue().getIndex());
    }

    @Override
    public String toString() {
        return "(" + start + ", " + end + ')';
    }
}
