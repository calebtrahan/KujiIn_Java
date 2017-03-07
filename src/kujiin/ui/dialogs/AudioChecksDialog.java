package kujiin.ui.dialogs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.AudioChecks;

import java.io.IOException;

public class AudioChecksDialog extends Stage {
    private MainController root;
    public ProgressBar Progress;
    public Label ProgressPercentage;
    public Label StatusBar;
    private AudioChecks audioChecks;

    public AudioChecksDialog(MainController Root) {
        try {
            audioChecks = new AudioChecks(Root);
            root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AudioChecksDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Starting Up");
            setOnShowing(event -> {
                audioChecks.setOnRunning(event1 -> {
                    StatusBar.textProperty().bind(audioChecks.messageProperty());
                    Progress.progressProperty().bind(audioChecks.progressProperty());
                });
                audioChecks.progressProperty().addListener((observable, oldValue, newValue) -> {
                    ProgressPercentage.setText(new Double(newValue.doubleValue() * 100).intValue() + "%");
                    if (newValue.doubleValue() >= 1.0) {
                        StatusBar.textProperty().unbind();
                        Progress.progressProperty().unbind();
                        StatusBar.setText("Audio Checks Completed!");
                        new Timeline(new KeyFrame(Duration.millis(5000), ae -> close())).play();
                    }
                });
                audioChecks.run();
            });
        } catch (IOException e) {e.printStackTrace();}
    }

}