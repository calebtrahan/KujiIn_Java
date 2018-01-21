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
import kujiin.util.StartupAudioChecks;

import java.io.IOException;

public class AudioChecksDialog extends Stage {
    public ProgressBar Progress;
    public Label ProgressPercentage;
    public Label StatusBar;
    private StartupAudioChecks startupAudioChecks;

    public AudioChecksDialog(MainController Root) {
        try {
            startupAudioChecks = new StartupAudioChecks(Root);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AudioChecksDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Kuji-In Start Up");
            setOnShown(event -> {
                startupAudioChecks.setOnRunning(event1 -> {
                    StatusBar.textProperty().bind(startupAudioChecks.messageProperty());
                    Progress.progressProperty().bind(startupAudioChecks.progressProperty());
                });
                startupAudioChecks.progressProperty().addListener((observable, oldValue, newValue) -> {
                    ProgressPercentage.setText(new Double(newValue.doubleValue() * 100).intValue() + "%");
                    if (newValue.doubleValue() >= 1.0) {
                        StatusBar.textProperty().unbind();
                        Progress.progressProperty().unbind();
                        StatusBar.setText("Audio Checks Completed!");
                        new Timeline(new KeyFrame(Duration.millis(200), ae -> close())).play();
                    }
                });
                startupAudioChecks.run();
            });
        } catch (IOException e) {e.printStackTrace();}
    }

}