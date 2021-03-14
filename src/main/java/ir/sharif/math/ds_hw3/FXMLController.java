package ir.sharif.math.ds_hw3;

import ir.sharif.math.ds_hw3.logic.UnZipper;
import ir.sharif.math.ds_hw3.logic.Zipper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    public static boolean running = false;
    private File source, dst;
    @FXML
    private ProgressBar progress;
    @FXML
    private ChoiceBox<String> choice;
    @FXML
    private BorderPane upBorder;
    @FXML
    private PasswordField password;
    @FXML
    private Button src;
    @FXML
    private Button start;
    @FXML
    private Button dest;
    @FXML
    private Label label;
    @FXML
    private TextField enterV;
    @FXML
    private TextField enterB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choice.getItems().add("zip");
        choice.getItems().add("unzip");
        choice.setValue("zip");
        progress.setViewOrder(0.01);
    }

    public void start(ActionEvent actionEvent) {
        if (running){
            label.setText(label.getText()+"\n" + "another progress is running");
            return;
        }

        if (source == null) {
            label.setText("specify source file correctly");
            return;
        }
        if (dst == null) {
            label.setText("specify output file correctly");
            return;
        }
        if (choice.getValue().equals("zip")) {
            Optional<Integer> v = getNumber(enterV.getText());
            if (v.isEmpty() || v.get() < 1 || 31 < v.get()) {
                label.setText("specify v correctly");
                return;
            }
            Optional<Integer> b = getNumber(enterB.getText());
            if (b.isEmpty() || b.get() < 1 || 7 < b.get()) {
                label.setText("specify b correctly");
                return;
            }
            try {
                Zipper zipper = new Zipper(v.get(), b.get(), source, dst, password.getText(), label, progress);
                zipper.start();
                source = dst = null;
                src.setText("select source file");
                dest.setText("select output file");
                running = true;
            } catch (FileNotFoundException e) {
                label.setText("cant find file");
            }
        } else if (choice.getValue().equals("unzip")) {
            try {
                UnZipper unZipper = new UnZipper(source, dst, password.getText(), label, progress);
                unZipper.start();
                source = dst = null;
                src.setText("select source file");
                dest.setText("select output file");
                running = true;
            } catch (FileNotFoundException e) {
                label.setText("cant find file");
            }
        }
    }

    public Optional<Integer> getNumber(String s) {
        try {
            int result = Integer.parseInt(s);
            return Optional.of(result);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void chooseSrc(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choose source File");
        source = fileChooser.showOpenDialog(Main.stage);
        if (source != null)
            src.setText("source selected");
    }

    public void chooseDest(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choose output File");
        dst = fileChooser.showOpenDialog(Main.stage);
        if (dst != null)
            dest.setText("output file selected");
    }
}