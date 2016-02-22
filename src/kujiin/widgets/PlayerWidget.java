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
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayerWidget implements Widget {
    public CheckBox onOffSwitch;
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
    private Slider EntrainmentVolume;
    private Slider AmbienceVolume;
    private Label EntrainmentPercentage;
    private Label AmbiencePercentage;
    private Label StatusBar;
    private Label VolumeEntrainmentLabel;
    private Label VolumeAmbienceLabel;
    private Label VolumeTopLabel;
    private This_Session Session;
    private ReferenceType referenceType;
    private MainController Root;

    public PlayerWidget(MainController root) {
        Root = root;
        onOffSwitch = root.SessionPlayerOnOffSwitch;
        PlayButton = root.PlayButton;
        PauseButton = root.PauseButton;
        StopButton = root.StopButton;
        CutPlayingText = root.CutProgressTopLabel;
        SessionPlayingText = root.TotalSessionLabel;
        CutCurrentTime = root.CutProgressLabelCurrent;
        CutTotalTime = root.CutProgressLabelTotal;
        SessionCurrentTime = root.TotalProgressLabelCurrent;
        SessionTotalTime = root.TotalProgressLabelTotal;
        CutProgress = root.CutProgressBar;
        TotalProgress = root.TotalProgressBar;
        ReferenceFileCheckbox = root.ReferenceFilesOption;
        EntrainmentVolume = root.EntrainmentVolume;
        AmbienceVolume = root.AmbienceVolume;
        EntrainmentPercentage = root.EntrainmentVolumePercentage;
        AmbiencePercentage = root.AmbienceVolumePercentage;
        StatusBar = root.PlayerStatusBar;
        VolumeAmbienceLabel = root.VolumeAmbienceLabel;
        VolumeEntrainmentLabel = root.VolumeEntrainmentLabel;
        VolumeTopLabel = root.VolumeTopLabel;
        Session = root.getSession();
        EntrainmentVolume.setOnMouseClicked(event -> {
            try {
                Double value = EntrainmentVolume.getValue() * 100;
                EntrainmentVolume.setTooltip(new Tooltip(value.intValue() + "%"));
                Session.Root.getOptions().getSessionOptions().setEntrainmentvolume(EntrainmentVolume.getValue());
            } catch (Exception ignored) {Tools.showtimedmessage(StatusBar, "No Session Playing", 2000);}
        });
        AmbienceVolume.setOnMouseClicked(event -> {
            try {
                Double value = AmbienceVolume.getValue() * 100;
                AmbienceVolume.setTooltip(new Tooltip(value.intValue() + "%"));
                Session.Root.getOptions().getSessionOptions().setAmbiencevolume(AmbienceVolume.getValue());
            } catch(Exception ignored) {Tools.showtimedmessage(StatusBar, "No Session Playing", 2000);}
        });
        onOffSwitch.setDisable(true);
        Tools.showtimedmessage(StatusBar, "Player Disabled Till Session Is Created Or Loaded", 10000);
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
    public void play() {
        Tools.showtimedmessage(StatusBar, Session.play(), 3000);
    }
    public void pause() {
        if (Session != null) {
            Tools.showtimedmessage(StatusBar, Session.pause(), 3000);}
        else {
            Tools.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void stop() {
        if (Session != null) {
            String message = Session.stop();
            Tools.showtimedmessage(StatusBar, message, 3000);
            if (message.equals("Session Stopped")) {resetallvalues();}
        }
        else {
            Tools.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void displayreferencefile() {Session.togglereferencedisplay(ReferenceFileCheckbox);}
    public void statusSwitch() {
        if (onOffSwitch.isSelected()) {enable();
        } else {disable();}
    }

// Widget Implementation
    @Override
    public void disable() {
        PlayerState playerState = Session.getPlayerState();
        if (playerState == PlayerState.PLAYING) {
            if (! Tools.getanswerdialog(Root, "Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
                onOffSwitch.setSelected(true);
                onOffSwitch.setText("ON");
                return;
            }
        } else if (playerState == PlayerState.PAUSED) {
            if (! Tools.getanswerdialog(Root, "Confirmation", "Disable Session Player", "This Will Stop And Reset The Playing Session")) {
                onOffSwitch.setSelected(true);
                onOffSwitch.setText("ON");
                return;
            }
        } else if (playerState == PlayerState.TRANSITIONING) {
            StatusBar.setText("Transitioning, Please Wait Till The Next Cut To Turn Off The Player");
            onOffSwitch.setSelected(true);
            onOffSwitch.setText("ON");
            return;
        }
        resetallvalues();
        Session.resetthissession();
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
        EntrainmentVolume.setDisable(true);
        AmbienceVolume.setDisable(true);
        VolumeTopLabel.setDisable(true);
        VolumeEntrainmentLabel.setDisable(true);
        VolumeAmbienceLabel.setDisable(true);
        EntrainmentPercentage.setText("-");
        AmbiencePercentage.setText("-");
        onOffSwitch.setText("OFF");
    }
    @Override
    public void enable() {
//        if (! creatorAndExporterWidget.createsession()) {return;}
//        if (! Session.isValid()) {
//            Tools.showinformationdialog("Information", "Cannot Enable Session Player", "Session (Above) Isn't Valid, All Cut Values Are 0");
//            onOffSwitch.setSelected(false);
//            onOffSwitch.setText("OFF");
//            return;
//        }
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
        VolumeTopLabel.setDisable(false);
        VolumeEntrainmentLabel.setDisable(false);
        VolumeAmbienceLabel.setDisable(false);
        if (Session.getAmbienceenabled()) {AmbienceVolume.setDisable(false);}
        else {AmbienceVolume.setDisable(true);}
        EntrainmentVolume.setDisable(false);
        EntrainmentPercentage.setText("-");
        AmbiencePercentage.setText("-");
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
        ReferenceFileCheckbox.setText("Reference Display");
    }
    @Override
    public boolean cleanup() {
        Session.stop();
        return Session.getPlayerState() != PlayerState.PLAYING;
    }

    public void readytoplay() {
        CutPlayingText.setText("Ready To Play");
        SessionPlayingText.setText("Ready To Play");
        ReferenceFileCheckbox.setText("Reference Display");
    }

// Dialogs
    public static class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        private MainController Root;
        private Cut currentcut;
        private ReferenceType referenceType;
        private Boolean fullscreenoption;

        public DisplayReference(MainController root, Cut currentcut) {
            Root = root;
            this.currentcut = currentcut;
            referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(currentcut.name + "'s Reference");
            setsizing();
            loadcontent();
        }
        public DisplayReference(MainController root, String htmlcontent) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Reference File Preview");}
            catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            fullscreenoption = false;
            setsizing();
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            ContentPane.setContent(browser);
            webEngine.loadContent(htmlcontent);
        }

        public void setsizing() {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            double width = primaryScreenBounds.getWidth();
            if (! fullscreenoption) {height -= 200; width -= 200;}
            this.setFullScreen(fullscreenoption);
            this.setHeight(height);
            this.setWidth(width);
            ContentPane.setFitToWidth(true);
            ContentPane.setFitToHeight(true);
            ContentPane.setStyle("-fx-background-color: #212526");
        }
        public void loadcontent() {
            File referencefile = currentcut.getReferenceFile();
            if (referencefile != null) {
                if (referenceType == ReferenceType.txt) {
                    StringBuilder sb = new StringBuilder();
                    try (FileInputStream fis = new FileInputStream(referencefile);
                         BufferedInputStream bis = new BufferedInputStream(fis)) {
                        while (bis.available() > 0) {sb.append((char) bis.read());}
                    } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
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

    }
    public static class ReferenceTypeDialog extends Stage {
        private MainController Root;
        public Button AcceptButton;
        public RadioButton HTMLOption;
        public RadioButton TextOption;
        public Button CancelButton;
        public CheckBox FullScreenOption;
        private ReferenceType referenceType;
        private Boolean fullscreen;
        private Boolean enabled;

        public ReferenceTypeDialog (MainController root) {
            Root = root;
            referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            fullscreen = Root.getOptions().getSessionOptions().getReferencefullscreen();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceTypeDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference Type Select");
            if (referenceType != null) {
                if (referenceType == ReferenceType.txt) {TextOption.setSelected(true);}
                else if (referenceType == ReferenceType.html) {HTMLOption.setSelected(true);}
            }
            FullScreenOption.setSelected(fullscreen);
        }

    // Getters And Setters
        public ReferenceType getReferenceType() {
            return referenceType;
        }
        public Boolean getFullscreen() {return fullscreen;}
        public void setFullscreen(Boolean fullscreen) {
            this.fullscreen = fullscreen;
        }
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
            setFullscreen(FullScreenOption.isSelected());
            setEnabled(true);
            this.close();
        }
        public void cancel(ActionEvent actionEvent) {
            setEnabled(false);
            this.close();
        }
    }
    public static class SessionFinishedDialog extends Stage {
        public TextField TotalPracticeDuration;
        public TextField SessionDuration;
        public Button CloseButton;
        private MainController Root;

        public SessionFinishedDialog(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionFinishedDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
                Sessions currentsessions = Root.getProgressTracker().getSessions();
                Session thissession = currentsessions.getsession(currentsessions.totalsessioncount() - 1);
                int thisessionminutes = thissession.getTotal_Session_Duration();
                SessionDuration.setText(Tools.minstoformattedabbreviatedhoursandminutes(thisessionminutes));
                SessionDuration.setOnKeyTyped(root.NONEDITABLETEXTFIELD);
                int totalsessionminutes = currentsessions.getpracticedtimeinminutesforallsessions(11, true);
                TotalPracticeDuration.setText(Tools.minstoformattedabbreviatedhoursandminutes(totalsessionminutes));
                TotalPracticeDuration.setOnKeyTyped(root.NONEDITABLETEXTFIELD);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Session Completed");
        }

        public void closedialog(ActionEvent actionEvent) {close();}
    }

// Enumerators
    public enum ReferenceType {
        html, txt
    }
    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE
    }
}
