package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kujiin.ReferenceType;
import kujiin.This_Session;
import kujiin.util.interfaces.Widget;
import kujiin.util.lib.GuiUtils;
import kujiin.util.xml.Sessions;

public class PlayerWidget implements Widget {
    private CheckBox onOffSwitch;
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
    private CreatorAndExporterWidget creatorAndExporterWidget;
    private ReferenceType referenceType;
    // TODO Figure Out Where To Encapsulate The Play Logic (Here Or In Session)
        // So That Only One Session Is Actived At A Time, And We Don't Have Duplicates
    // TODO !!!!!!!!!!!!!!!!!!!!! Set On (Ambience/Entrainment) Media Error
        // Pause Whichever (ambience/entrainment) Is Working
        // -> Alert Confirmation Dialog
            //  (Ambience/Entrainment For $Cutname Failed)
            //  Cannot Play $Songname
            //  Retry Playing (Song name) [Start Over Playing The Cut Again] || Stop Playback Completely

    public PlayerWidget(CheckBox OnOffSwitch, Button adjustVolumeButton, Button playButton, Button pauseButton, Button stopButton,
                        Label cutPlayingText, Label sessionPlayingText, Label cutCurrentTime, Label cutTotalTime,
                        Label sessionCurrentTime, Label sessionTotalTime, ProgressBar cutProgress,
                        ProgressBar totalProgress, CheckBox referenceFileCheckbox,
                        Label statusbar, GoalsWidget goalsWidget, This_Session Session, CreatorAndExporterWidget creatorAndExporterWidget) {
        onOffSwitch = OnOffSwitch;
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
        this.Session = Session;
        this.creatorAndExporterWidget = creatorAndExporterWidget;
    }

// Getters And Setters
    public ReferenceType getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }
    public boolean isEnabled() {return onOffSwitch.isSelected();}

// Button Actions
    public void play(Sessions sessions) {
        Session.setGoalsWidget(GoalsWidget);
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
    public void adjustvolume() {Session.adjustvolume();}
    public void displayreferencefile() {Session.togglereferencedisplay(ReferenceFileCheckbox);}
    public void statusSwitch() {
        if (onOffSwitch.isSelected()) {enable();
        } else {disable();}
    }

// Widget Implementation
    @Override
    public void disable() {
        switch (Session.getPlayerState()) {
            case PLAYING:
                if (! GuiUtils.getanswerdialog("Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
                    onOffSwitch.setSelected(true);
                    onOffSwitch.setText("ON");
                    return;
                }
            case PAUSED:
                if (! GuiUtils.getanswerdialog("Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
                    onOffSwitch.setSelected(true);
                    onOffSwitch.setText("ON");
                    return;
                }
            case TRANSITIONING:
                StatusBar.setText("Transitioning, Please Wait Till The Next Cut To Turn Off The Player");
                onOffSwitch.setSelected(true);
                onOffSwitch.setText("ON");
                return;
        }
        resetallvalues();
        Session.resetthissession();
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
        onOffSwitch.setText("OFF");
    }
    @Override
    public void enable() {
        if (! creatorAndExporterWidget.createsession()) {return;}
        if (! Session.isValid()) {
            GuiUtils.showinformationdialog("Information", "Cannot Enable Session Player", "Session (Above) Isn't Valid, All Cut Values Are 0");
            onOffSwitch.setSelected(false);
            onOffSwitch.setText("OFF");
            return;
        }
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
        onOffSwitch.setText("ON");
        readytoplay();
    }
    @Override
    public void resetallvalues() {
        CutPlayingText.setText("Player Disabled");
        SessionPlayingText.setText("Player Disabled");
        CutCurrentTime.setText("--:--");
        CutTotalTime.setText("--:--");
        SessionCurrentTime.setText("--:--");
        SessionTotalTime.setText("--:--");
        CutProgress.setProgress(0.0);
        TotalProgress.setProgress(0.0);
        ReferenceFileCheckbox.setText("Reference Display Disabled");
    }
    public void readytoplay() {

        CutPlayingText.setText("Ready To Play");
        SessionPlayingText.setText("Ready To Play");
        ReferenceFileCheckbox.setText("Reference Display Disabled");
        GuiUtils.showtimedmessage(StatusBar, "Turn Session Player Off To Edit This Session", 6000);
    }

// Other methods

}
