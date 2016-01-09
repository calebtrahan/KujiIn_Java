package kujiin.widgets;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.MainController;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.Sessions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayerWidget implements Widget {
    public static Double ENTRAINMENTVOLUME = 0.6;
    public static Double AMBIENCEVOLUME = 1.0;
    public static Double FADEOUTDURATION = 10.0;
    public static Double FADEINDURATION = 10.0;
    private MainController Root;
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

    public PlayerWidget(MainController mainController) {
        Root = mainController;
        onOffSwitch = Root.SessionPlayerOnOffSwitch;
        AdjustVolumeButton = Root.VolumeButton;
        PlayButton = Root.PlayButton;
        PauseButton = Root.PauseButton;
        StopButton = Root.StopButton;
        CutPlayingText = Root.CutProgressTopLabel;
        SessionPlayingText = Root.TotalSessionLabel;
        CutCurrentTime = Root.CutProgressLabelCurrent;
        CutTotalTime = Root.CutProgressLabelTotal;
        SessionCurrentTime = Root.TotalProgressLabelCurrent;
        SessionTotalTime = Root.TotalProgressLabelTotal;
        CutProgress = Root.CutProgressBar;
        TotalProgress = Root.TotalProgressBar;
        ReferenceFileCheckbox = Root.ReferenceFilesOption;
        GoalsWidget = Root.getGoalsWidget();
        StatusBar = Root.StatusBar;
        Session = Root.getThis_session();
        creatorAndExporterWidget = Root.getCreatorAndExporterWidget();
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
        Tools.showtimedmessage(StatusBar, Session.play(), 3000);
    }
    public void pause() {
        if (Session != null) {
            Tools.showtimedmessage(StatusBar, Session.pause(), 3000);}
        else {
            Tools.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void stop(Sessions Sessions) {
        if (Session != null) {
            String message = Session.stop();
            Tools.showtimedmessage(StatusBar, message, 3000);
            if (message.equals("Session Stopped")) {resetallvalues();}
        }
        else {
            Tools.showtimedmessage(StatusBar, "No Session Playing", 3000);}
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
                if (! Tools.getanswerdialog("Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
                    onOffSwitch.setSelected(true);
                    onOffSwitch.setText("ON");
                    return;
                }
            case PAUSED:
                if (! Tools.getanswerdialog("Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
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
            Tools.showinformationdialog("Information", "Cannot Enable Session Player", "Session (Above) Isn't Valid, All Cut Values Are 0");
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
        Tools.showtimedmessage(StatusBar, "Turn Session Player Off To Edit This Session", 6000);
    }

// Subclasses/Dialogs
    public static class AdjustVolume extends Stage {
        public Slider EntrainmentSlider;
        public Slider AmbienceSlider;

        public AdjustVolume(Cut currentcut) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AdjustSessionVolume.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Adjust Session Volume");}
            catch (IOException e) {e.printStackTrace();}
            if (currentcut.getCurrentAmbiencePlayer() != null) {currentcut.getCurrentAmbiencePlayer().volumeProperty().bind(AmbienceSlider.valueProperty());}
            else {AmbienceSlider.setDisable(true);}
            if (currentcut.getCurrentEntrainmentPlayer() != null) {currentcut.getCurrentEntrainmentPlayer().volumeProperty().bind(EntrainmentSlider.valueProperty());}
            else {EntrainmentSlider.setDisable(true);}
            AmbienceSlider.setValue(AMBIENCEVOLUME);
            EntrainmentSlider.setValue(ENTRAINMENTVOLUME);
        }

        public Double getEntrainmentVolume() {return EntrainmentSlider.getValue();}
        public Double getAmbienceVolume() {return AmbienceSlider.getValue();}

    }
    public static class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        private Cut currentcut;
        private ReferenceType referenceType;

        public DisplayReference(Cut currentcut, ReferenceType referenceType, Boolean fullscreenoption) {
            this.currentcut = currentcut;
            this.referenceType = referenceType;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle(currentcut.name + "'s Reference");}
            catch (IOException e) {e.printStackTrace();}
            System.out.println(fullscreenoption);
            // TODO FullScreenOption Is null. Why?
            setsizing(true);
    //        } else {
    //            // TODO Set Adjusted Height And Width Here
    //        }
            loadcontent();
        }

        public DisplayReference(String htmlcontent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Reference File Preview");}
            catch (IOException e) {e.printStackTrace();}
            setsizing(false);
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            ContentPane.setContent(browser);
            webEngine.loadContent(htmlcontent);
        }

        public void setsizing(boolean fullscreen) {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            double width = primaryScreenBounds.getWidth();
            if (! fullscreen) {height -= 200; width -= 200;}
            this.setFullScreen(fullscreen);
            this.setHeight(height);
            this.setWidth(width);
            ContentPane.setFitToWidth(true);
            ContentPane.setFitToHeight(true);
            ContentPane.setStyle("-fx-background-color: #212526");
        }

        public void loadcontent() {
            File referencefile = currentcut.getReferenceFile(referenceType);
            System.out.println("Passing " + referencefile.getAbsolutePath() + " Into The Content Pane");
            if (referenceType == ReferenceType.txt) {
                StringBuilder sb = new StringBuilder();
                try (FileInputStream fis = new FileInputStream(referencefile);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    while (bis.available() > 0) {sb.append((char) bis.read());}
                } catch (Exception e) {e.printStackTrace();}
                TextArea ta = new TextArea();
                ta.setText(sb.toString());
                ta.setWrapText(true);
                ContentPane.setContent(ta);
            } else if (referenceType == ReferenceType.html) {
                WebView browser = new WebView();
                WebEngine webEngine = browser.getEngine();
                ContentPane.setContent(browser);
                webEngine.load(referencefile.toURI().toString());
            }
        }
    }
    public static class ReferenceTypeDialog extends Stage {
        public Button AcceptButton;
        public RadioButton HTMLOption;
        public RadioButton TextOption;
        public Button CancelButton;
        public CheckBox FullScreenOption;
        private ReferenceType referenceType = null;
        private Boolean fullscreen = null;
        private Boolean enabled;

        public ReferenceTypeDialog (ReferenceType referenceType, Boolean fullscreenoption) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceTypeDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Reference Type Variation");}
            catch (IOException e) {e.printStackTrace();}
            if (referenceType != null) {
                if (referenceType == ReferenceType.txt) {TextOption.setSelected(true);}
                else if (referenceType == ReferenceType.html) {HTMLOption.setSelected(true);}
            }
            if (fullscreenoption != null) {FullScreenOption.setSelected(fullscreenoption);}
        }

    // Getters And Setters
        public ReferenceType getReferenceType() {
            return referenceType;
        }
        public Boolean getFullscreen() {return fullscreen;}
        public Boolean getEnabled() {
            return enabled;
        }
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

    // Button Actions
        public void selecthtml(ActionEvent actionEvent) {
            if (HTMLOption.isSelected()) {TextOption.setSelected(false);}
        }
        public void selecttxt(ActionEvent actionEvent) {
            if (TextOption.isSelected()) {HTMLOption.setSelected(false);}
        }
        public void accept(ActionEvent actionEvent) {
            if (HTMLOption.isSelected()) {referenceType = ReferenceType.html;}
            else if (TextOption.isSelected()) {referenceType = ReferenceType.txt;}
            setFullScreen(FullScreenOption.isSelected());
            setEnabled(true);
            this.close();
        }
        public void cancel(ActionEvent actionEvent) {
            setEnabled(false);
            this.close();
        }

    }

// Enums
    public enum ReferenceType {
        html, txt
    }
    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE
    }

}
