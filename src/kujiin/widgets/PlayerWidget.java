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
import kujiin.MainController;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
// TODO Refactor Into Meditation Program -> And Add 5 Elements To Be Practiced With or Without Cuts

// TODO Reference Display Isn't Switching Off If On When Checbox Unselected
// TODO Fix Set Multiple Goal Minutes (And Add Check If Long Enough Logic On Accepting)
// TODO Playback Problems
    // TOH Freq Is Way Too Loud!
// TODO Reference Display Isn't Displaying Text But Is Styled -> FIX!
// TODO Select Button On Options -> ChangeAlertFileDialog Instead Of Just A File Chooser

public class PlayerWidget extends Stage {
    public Button PlayButton;
    public Button PauseButton;
    public Button StopButton;
    public Label StatusBar;
    public CheckBox ReferenceSwitch;
    public RadioButton ReferenceHTMLButton;
    public RadioButton ReferenceTXTButton;
    public Slider EntrainmentVolume;
    public Label EntrainmentVolumePercentage;
    public Slider AmbienceVolume;
    public Label AmbienceVolumePercentage;
    public Label CurrentCutTopLabel;
    public Label CutCurrentLabel;
    public ProgressBar CurrentCutProgress;
    public Label CutTotalLabel;
    public Label TotalCurrentLabel;
    public ProgressBar TotalProgress;
    public Label TotalTotalLabel;
    public Label TotalSessionLabel;
    private This_Session Session;
    private MainController Root;

    public PlayerWidget(MainController root) {
        Root = root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayerDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions();
            Root.getOptions().setStyle(defaultscene);
        } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        setTitle("Session Player");
        Root = root;
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
        Tools.showtimedmessage(StatusBar, "Player Disabled Until Session Is Created Or Loaded", 10000);
    }

// Button Actions
    public void play() {
        Tools.showtimedmessage(StatusBar, Session.play(this), 3000);
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
//            if (message.equals("Session Stopped")) {resetallvalues();}
        }
        else {
            Tools.showtimedmessage(StatusBar, "No Session Playing", 3000);}
    }
    public void displayreferencefile() {
//        Session.togglereferencedisplay(ReferenceFileCheckbox);
    }
    public void statusSwitch() {
//        if (onOffSwitch.isSelected()) {enable();
//        } else {disable();}
    }

// Widget Implementation
    public boolean cleanup() {
        Session.stop();
        return Session.getPlayerState() != PlayerState.PLAYING;
    }
    public void readytoplay() {
//        CutPlayingText.setText("Ready To Play");
//        SessionPlayingText.setText("Ready To Play");
//        ReferenceFileCheckbox.setText("Reference");
    }

// Dialogs
    // TODO Style Reference Display
    public static class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        private MainController Root;
        private Playable currentcutorelement;
        private ReferenceType referenceType;
        private Boolean fullscreenoption;
        private Scene scene;

        public DisplayReference(MainController root, Object currentcutorelement) {
            Root = root;
            this.currentcutorelement = (Playable) currentcutorelement;
            referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(scene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(this.currentcutorelement.name + "'s Reference");
            setsizing();
            loadcontent();
        }
        public DisplayReference(MainController root, String htmlcontent) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            try {
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(scene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference File Preview");
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
            File referencefile = currentcutorelement.getReferenceFile();
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
                    Root.getOptions().setStyle(scene);
                } else if (referenceType == ReferenceType.html) {
                    // TODO Get Dark Theme For Webview Here For Reference Files
                    WebView browser = new WebView();
                    WebEngine webEngine = browser.getEngine();
//                    Root.getOptions().setStyle(scene);
//                    webEngine.setUserStyleSheetLocation(new File(Options.DIRECTORYSTYLES, "dark.css").toURI().toString());
                    webEngine.load(referencefile.toURI().toString());
                    ContentPane.setContent(browser);
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
