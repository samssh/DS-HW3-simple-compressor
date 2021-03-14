package ir.sharif.math.ds_hw3.logic;

import ir.sharif.math.ds_hw3.FXMLController;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;

public class UnZipper {
    private static final int BUFFER_SIZE = 10 * 1024 * 1024;
    private final Label label;
    private final ProgressBar progressBar;
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;
    private final RandomAccessFile accessFile;
    private final byte password;
    private long lastIndex = 0, ll =0;
    private int b, v, lowerBoundForMatch;
    private final long length;
    private final boolean[] a = new boolean[10];


    public UnZipper(File src, File dest, String password, Label label, ProgressBar progressBar) throws FileNotFoundException {
        inputStream = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
        outputStream = new BufferedOutputStream(new FileOutputStream(dest), BUFFER_SIZE);
        accessFile = new RandomAccessFile(dest, "r");
        byte result = 0;
        for (byte by : password.getBytes()) result = (byte) (result ^ by);
        this.password = result;
        length = src.length();
        this.label = label;
        this.progressBar = progressBar;
    }

    private void unZip() throws Exception {
        Platform.runLater(() -> label.setText("start..."));
        int data = read();
        initialize(data);
        while (data != -1) {
            data = read();
            if (data > 0) write(data);
            else if (data == 0) {
                long at = 0;
                for (int i = 0; i < lowerBoundForMatch - 1; i++) {
                    at += read();
                    if (i + 1 != lowerBoundForMatch - 1)
                        at = at << 8;
                }
                long size = getA(at) + 1;
                long t = getT(at);
                outputStream.flush();
                long begin = lastIndex - t;
                accessFile.seek(begin - 1);
                byte[] bytes = new byte[(int) size];
                accessFile.read(bytes);
                write(bytes);
            }
            double s = (ll - 2) * 1.0 / length;
            System.out.println(ll);
            System.out.println(length);
            if (!a[(int) (s * a.length)]) {
                Platform.runLater(() -> progressBar.setProgress(s));
                a[(int) (s * a.length)] = true;
            }
        }
        accessFile.close();
        inputStream.close();
        outputStream.close();
        Platform.runLater(() -> progressBar.setProgress(1));
        Platform.runLater(() -> label.setText("done"));
        FXMLController.running = false;
    }


    private void initialize(int bv) {
        bv = bv ^ password;
        this.b = bv & (0b111);
        this.v = (bv >> 3) & (0b11111);
        this.lowerBoundForMatch = 1 + (b + v + 7) / 8;
    }

    private void write(int data) throws IOException {
        lastIndex++;
        outputStream.write(data);
    }

    private void write(byte[] bytes) throws IOException {
        lastIndex += bytes.length;
        outputStream.write(bytes);
    }

    private int read() throws IOException {
        ll++;
        return inputStream.read();
    }


    private long getA(long at) {
        return at & ((1L << b) - 1);
    }

    private long getT(long at) {
        int l = (lowerBoundForMatch - 1) * 8;
        long mask = 0;
        for (int i = l - v; i < l; i++) {
            mask += 1L << i;
        }

        return (mask & at) >>> (l - v);
    }

    public void start() {
        new Thread(() -> {
            try {
                unZip();
            } catch (Exception e) {
                Platform.runLater(() -> label.setText("problem...\n" + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

}
