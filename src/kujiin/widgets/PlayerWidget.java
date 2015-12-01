package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kujiin.ReferenceType;
import kujiin.This_Session;
import kujiin.dialogs.ReferenceTypeDialog;
import kujiin.util.interfaces.Widget;
import kujiin.util.lib.GuiUtils;
import kujiin.util.xml.Sessions;

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
    private GoalsWidget GoalsWidget;
    private Label StatusBar;
    private This_Session Session;
    private ReferenceType referenceType;
    // TODO Figure Out Where To Encapsulate The Play Logic (Here Or In Session)
        // So That Only One Session Is Actived At A Time, And We Don't Have Duplicates

    public PlayerWidget(Button adjustVolumeButton, Button playButton, Button pauseButton, Button stopButton,
                        Label cutPlayingText, Label sessionPlayingText, Label cutCurrentTime, Label cutTotalTime,
                        Label sessionCurrentTime, Label sessionTotalTime, ProgressBar cutProgress,
                        ProgressBar totalProgress, CheckBox referenceFileCheckbox,
                        Label statusbar, GoalsWidget goalsWidget) {
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
        GoalsWidget = goalsWidget;
        StatusBar = statusbar;
    }

// Getters And Setters
    public ReferenceType getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

// Button Actions
    public void play(Sessions sessions) {
        if (Session == null) {Session  = new This_Session(sessions, CutCurrentTime, CutTotalTime,
                SessionCurrentTime, SessionTotalTime, CutProgress, TotalProgress, CutPlayingText,
                SessionPlayingText, StatusBar);}
        GuiUtils.showtimedmessage(StatusBar, Session.play(), 3000);
    }
    public void pause() {
        if (Session != null) {GuiUtils.showtimedmessage(StatusBar, Session.pause(), 3000);}
        else {GuiUtils.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void stop(Sessions Sessions) {
        if (Session != null) {
            String message = Session.stop();
            GuiUtils.showtimedmessage(StatusBar, message, 3000);
            if (message.equals("Session Stopped")) {resetallvalues();}
        }
        else {GuiUtils.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void adjustvolume() {}
    public void setreferencetype() {
        ReferenceTypeDialog reftype = new ReferenceTypeDialog(null, referenceType);
        reftype.showAndWait();
        referenceType = reftype.getReferenceType();
    }

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

// Other methods

}
