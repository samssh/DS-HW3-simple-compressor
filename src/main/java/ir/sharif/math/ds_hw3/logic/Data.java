package ir.sharif.math.ds_hw3.logic;

import java.util.Objects;

public class Data {
    private final byte value;
    private final long index;

    public Data(byte value, long index) {
        this.value = value;
        this.index = index;
    }

    public byte getValue() {
        return value;
    }

    public int getIntegerValue() {
        return value & 0xff;
    }

    public long getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return value == data.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "{" + getIntegerValue() + ", " + index + '}';
    }
}
