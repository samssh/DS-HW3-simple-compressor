package ir.sharif.math.ds_hw3.logic;

import ir.sharif.math.ds_hw3.FXMLController;
import ir.sharif.math.ds_hw3.util.LList;
import ir.sharif.math.ds_hw3.util.Pair;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.ListIterator;

public class Zipper {
    private static final int BUFFER_SIZE = 10 * 1024 * 1024;
    private final Label label;
    private final ProgressBar progressBar;
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;
    private final DataHolder dataHolder;
    private final byte password;
    private long lastIndex = 0;
    private final long length;
    private final boolean[] a = new boolean[10];

    public Zipper(int v, int b, File src, File dest, String password, Label label, ProgressBar progressBar) throws FileNotFoundException {
        inputStream = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
        outputStream = new BufferedOutputStream(new FileOutputStream(dest), BUFFER_SIZE);
        length = src.length();
        this.label = label;
        this.progressBar = progressBar;
        dataHolder = new DataHolder(b, v);
        byte result = 0;
        for (byte by : password.getBytes()) result = (byte) (result ^ by);
        this.password = result;
    }

    private void zip() throws Exception {
        Platform.runLater(() -> label.setText("start..."));
        fillBuffer();
        writeBAndV();
        while (dataHolder.getBuffer().size() != 0) {
            Pair matchResult = findMatch();
            if (matchResult == null) {
                Data data = dataHolder.getBuffer().remove(0);
                dataHolder.addToWindow(data);
                write(data.getValue());
            } else {
                long a = matchResult.getEnd().getValue().getIndex() - matchResult.getStart().getValue().getIndex();
                long t = dataHolder.getWindow().get(dataHolder.getWindow().size() - 1)
                        .getIndex() - matchResult.getStart().getValue().getIndex();
                for (int i = 0; i < a + 1; i++) {
                    Data data = dataHolder.getBuffer().remove(0);
                    dataHolder.addToWindow(data);
                }
                writeTAndA(t, a);
            }
            fillBuffer();
            double s = (lastIndex - 1) * 1.0 / length;
            if (!a[(int) (s * a.length)]) {
                Platform.runLater(() -> progressBar.setProgress(s));
                a[(int) (s * a.length)] = true;
            }
        }
        outputStream.close();
        inputStream.close();
        Platform.runLater(() -> label.setText("done"));
        Platform.runLater(() -> progressBar.setProgress(1));
        FXMLController.running = false;
    }

    private void writeBAndV() throws IOException {
        int result = dataHolder.getB() + (dataHolder.getV() << 3);
        result = result ^ password;
        write((byte) result);
    }

    private void writeTAndA(long t, long a) throws IOException {
        int shift = (dataHolder.getLowerBoundForMatch() - 1) * 8 - dataHolder.getV();
        long temp = a + (t << shift);
        byte[] result = new byte[dataHolder.getLowerBoundForMatch()];
        result[0] = 0;
        for (int i = result.length - 1; i >= 1; i--) {
            result[i] = (byte) (temp);
            temp = temp >>> 8;
        }
        write(result);
    }

    private void fillBuffer() throws Exception {
        while (dataHolder.getBuffer().size() < dataHolder.getC()) {
            Data data = read();
            if (data == null) return;
            dataHolder.getBuffer().addLast(data);
        }
    }

    private Data read() throws Exception {
        int d = inputStream.read();
        if (d == -1) return null;
        if (d == 0) throw new Exception("file has zero byte");
        return new Data((byte) d, lastIndex++);
    }

    private void write(byte... b) throws IOException {
        outputStream.write(b);
    }

    public void start() {
        new Thread(() -> {
            try {
                zip();
            } catch (Exception e) {
                Platform.runLater(() -> label.setText("problem...\n" + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    private Pair findMatch() {
        LList<Data> buffer = dataHolder.getBuffer();
        long[] hashes = new long[buffer.size()];
        ListIterator<Data> iterator = buffer.iterator();
        for (int i = 0; i < buffer.size(); i++) {
            long lastHash = i == 0 ? 0 : hashes[i - 1];
            hashes[i] = lastHash * DataHolder.P + iterator.next().getIntegerValue();
        }
        for (int i = buffer.size() - 1; i + 1 > dataHolder.getLowerBoundForMatch(); i--) {
            Pair result = dataHolder.searchHash(hashes[i], i + 1);
            if (result != null)
                return result;
        }
        return null;
    }
}
