package kujiin.ui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.Util;

import java.io.File;
import java.io.IOException;

public class PreviewFile extends Stage {
    public Label CurrentTime;
    public Slider ProgressSlider;
    public Label TotalTime;
    public Button PlayButton;
    public Button PauseButton;
    public Button StopButton;
    public Slider VolumeSlider;
    public Label VolumePercentage;
    private Media Mediatopreview;
    private File Filetopreview;
    private MediaPlayer PreviewPlayer;

    public PreviewFile(File filetopreview, MainController Root) {
        if (Util.audio_isValid(filetopreview)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                Filetopreview = filetopreview;
                setTitle("Preview: " + Filetopreview.getName().substring(0, Filetopreview.getName().lastIndexOf(".")));
                Mediatopreview = new Media(Filetopreview.toURI().toString());
                PreviewPlayer = new MediaPlayer(Mediatopreview);
                PlayButton.setDisable(true);
                PauseButton.setDisable(true);
                StopButton.setDisable(true);
                PreviewPlayer.setOnReady(() -> {
                    CurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(0)));
                    TotalTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(PreviewPlayer.getTotalDuration().toSeconds() * 1000)));
                    PlayButton.setDisable(false);
                });
                setOnHidden(event -> {if (PreviewPlayer != null) {PreviewPlayer.dispose();}});
                VolumeSlider.setValue(0.0);
                VolumePercentage.setText("0%");
            } catch (IOException ignored) {}
        } else {new InformationDialog(Root.getOptions(), "Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
    }

    public void play(ActionEvent actionEvent) {
        if (PreviewPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            PreviewPlayer.play();
            VolumeSlider.setValue(1.0);
            VolumePercentage.setText("100%");
            ProgressSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                Duration seektothis = PreviewPlayer.getTotalDuration().multiply(ProgressSlider.getValue());
                PreviewPlayer.seek(seektothis);
            });
            PreviewPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                CurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(newValue));
                updatePositionSlider(PreviewPlayer.getCurrentTime());
            });
            VolumeSlider.valueProperty().bindBidirectional(PreviewPlayer.volumeProperty());
            VolumeSlider.setOnMouseDragged(event -> {
                Double value = VolumeSlider.getValue() * 100;
                VolumePercentage.setText(value.intValue() + "%");
            });
            VolumeSlider.setOnScroll(event -> {
                double newvalue = PreviewPlayer.getVolume();
                if (event.getDeltaY() < 0) {newvalue -= (5.0 / 100.0);}
                else {newvalue += (5.0 / 100.0);}
                if (newvalue <= 1.0 && newvalue >= 0.0) {
                    Double roundedvalue = Util.round_nearestmultipleof5(newvalue * 100);
                    VolumePercentage.setText(roundedvalue.intValue() + "%");
                    VolumeSlider.valueProperty().unbindBidirectional(PreviewPlayer.volumeProperty());
                    VolumeSlider.setValue(roundedvalue / 100);
                    PreviewPlayer.setVolume(roundedvalue / 100);
                    VolumeSlider.valueProperty().bindBidirectional(PreviewPlayer.volumeProperty());
                }
            });
            PreviewPlayer.setOnPlaying(this::syncbuttons);
            PreviewPlayer.setOnPaused(this::syncbuttons);
            PreviewPlayer.setOnStopped(this::syncbuttons);
            PreviewPlayer.setOnEndOfMedia(this::reset);
        }
    }
    public void updatePositionSlider(Duration currenttime) {
        if (ProgressSlider.isValueChanging()) {return;}
        Duration total = PreviewPlayer.getTotalDuration();
        if (total == null || currenttime == null) {ProgressSlider.setValue(0);}
        else {ProgressSlider.setValue(currenttime.toMillis() / total.toMillis());}
    }
    public void pause(ActionEvent actionEvent) {
        if (PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING) {PreviewPlayer.pause();}
        syncbuttons();
    }
    public void stop(ActionEvent actionEvent) {
        if (PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING || PreviewPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            PreviewPlayer.stop();
        }
        syncbuttons();
    }
    public void syncbuttons() {
        MediaPlayer.Status status = PreviewPlayer.getStatus();
        PlayButton.setDisable(status == MediaPlayer.Status.PLAYING);
        PauseButton.setDisable(status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.READY);
        StopButton.setDisable(status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.READY);
        ProgressSlider.setDisable(status != MediaPlayer.Status.PLAYING);
        CurrentTime.setDisable(status != MediaPlayer.Status.PLAYING);
        TotalTime.setDisable(status != MediaPlayer.Status.PLAYING);
        VolumeSlider.setDisable(status != MediaPlayer.Status.PLAYING);
        VolumePercentage.setDisable(status != MediaPlayer.Status.PLAYING);
    }
    public void reset() {
        if (Mediatopreview != null) {PreviewPlayer.stop();}
        VolumeSlider.valueProperty().unbindBidirectional(PreviewPlayer.volumeProperty());
        CurrentTime.setText("--:--");
        ProgressSlider.setValue(0);
        VolumeSlider.setValue(0);
        syncbuttons();
    }
}
