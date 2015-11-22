package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kujiin.util.interfaces.Widget;
import kujiin.util.states.PlayerState;

public class PlayerWidget implements Widget {
    private Button AdjustVolumeButton;
    private Button PlayButton;
    private Button PauseButton;
    private Button StopButton;
    private Label CutPlayingText;
    private Label SessionPlayingText;
    private Label CutCurrentTime;
    private Label CutTotalTime;
    private Label SessionCurrentTime;
    private Label SessionTotalTime;
    private ProgressBar CutProgress;
    private ProgressBar TotalProgress;
    private CheckBox ReferenceFileCheckbox;
    private PlayerState playerState;

    public PlayerWidget(Button adjustVolumeButton, Button playButton, Button pauseButton, Button stopButton,
                        Label cutPlayingText, Label sessionPlayingText, Label cutCurrentTime, Label cutTotalTime,
                        Label sessionCurrentTime, Label sessionTotalTime, ProgressBar cutProgress,
                        ProgressBar totalProgress, CheckBox referenceFileCheckbox) {
        AdjustVolumeButton = adjustVolumeButton;
        PlayButton = playButton;
        PauseButton = pauseButton;
        StopButton = stopButton;
        CutPlayingText = cutPlayingText;
        SessionPlayingText = sessionPlayingText;
        CutCurrentTime = cutCurrentTime;
        CutTotalTime = cutTotalTime;
        SessionCurrentTime = sessionCurrentTime;
        SessionTotalTime = sessionTotalTime;
        CutProgress = cutProgress;
        TotalProgress = totalProgress;
        ReferenceFileCheckbox = referenceFileCheckbox;
        playerState = PlayerState.IDLE;
    }

// Button Actions
    public void play() {}
    public void pause() {}
    public void stop() {}
    public void adjustvolume() {}

// Widget Implementation
    @Override
    public void disable() {
        AdjustVolumeButton.setDisable(true);
        PlayButton.setDisable(true);
        PauseButton.setDisable(true);
        StopButton.setDisable(true);
        CutPlayingText.setDisable(true);
        SessionPlayingText.setDisable(true);
        CutCurrentTime.setDisable(true);
        CutTotalTime.setDisable(true);
        SessionCurrentTime.setDisable(true);
        SessionTotalTime.setDisable(true);
        CutProgress.setDisable(true);
        TotalProgress.setDisable(true);
        ReferenceFileCheckbox.setDisable(true);
    }
    @Override
    public void enable() {
        AdjustVolumeButton.setDisable(false);
        PlayButton.setDisable(false);
        PauseButton.setDisable(false);
        StopButton.setDisable(false);
        CutPlayingText.setDisable(false);
        SessionPlayingText.setDisable(false);
        CutCurrentTime.setDisable(false);
        CutTotalTime.setDisable(false);
        SessionCurrentTime.setDisable(false);
        SessionTotalTime.setDisable(false);
        CutProgress.setDisable(false);
        TotalProgress.setDisable(false);
        ReferenceFileCheckbox.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        CutPlayingText.setText("No Session Created");
        SessionPlayingText.setText("No Session Created");
        CutCurrentTime.setText("--:--");
        CutTotalTime.setText("--:--");
        SessionCurrentTime.setText("--:--");
        SessionTotalTime.setText("--:--");
        CutProgress.setProgress(0.0);
        TotalProgress.setProgress(0.0);
        ReferenceFileCheckbox.setText("No Session Playing");
    }
}
