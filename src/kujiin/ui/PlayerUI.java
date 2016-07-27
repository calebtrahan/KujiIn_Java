package kujiin.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.util.Meditatable;
import kujiin.util.This_Session;
import kujiin.util.Util;
import kujiin.xml.Options;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
// TODO Fix Set Multiple Goal Minutes (And Add Check If Long Enough Logic On Accepting)
// TODO Select Button On Options -> ChangeAlertFileDialog Instead Of Just A File Chooser
// TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)
// TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog

// TODO Confirmation -> Alert File On LONG Sessions (Deep In Trance)

public class PlayerUI extends Stage {
    public Button PlayButton;
    public Button PauseButton;
    public Button StopButton;
    public Label StatusBar;
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
    public Label GoalTopLabel;
    public ProgressBar GoalProgressBar;
    public ToggleButton ReferenceToggleButton;
    public Label GoalPercentageLabel;
    private This_Session Session;
    private MainController Root;
    public boolean displaynormaltime = true;

    public PlayerUI(MainController root) {
        try {
            Root = root;
            Session = Root.getSession();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayerDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions();
            Root.getOptions().setStyle(this);
            setTitle("Session Player");
            reset();
            boolean referenceoption = Root.getOptions().getSessionOptions().getReferenceoption();
            ReferenceType referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            if (referenceoption && referenceType != null && Session.checkallreferencefilesforsession(referenceType, false)) {ReferenceToggleButton.setSelected(true);}
            else {ReferenceToggleButton.setSelected(false);}
            togglereference(null);
            ReferenceToggleButton.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
            setResizable(false);
            CutTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
            TotalTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
            setOnCloseRequest(event -> {
                if (Session.getPlayerState() == PlayerState.PLAYING || Session.getPlayerState() == PlayerState.STOPPED || Session.getPlayerState() == PlayerState.PAUSED || Session.getPlayerState() == PlayerState.IDLE) {
                    if (Session.endsessionprematurely()) {close(); cleanupPlayer();} else {play(); event.consume();}
                } else {
                    Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                    new Timeline(new KeyFrame(Duration.millis(400), ae -> Session.getCurrentmeditatable().toggleplayerbuttons()));
                    event.consume();
                }
            });
        } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
    }

// Button Actions
    public void play() {Session.play(this);}
    public void pause() {Session.pause();}
    public void stop() {Session.stop();}
    public void togglereference(ActionEvent actionEvent) {
        boolean buttontoggled = ReferenceToggleButton.isSelected();
        Root.getOptions().getSessionOptions().setReferenceoption(buttontoggled);
        ReferenceHTMLButton.setDisable(! buttontoggled);
        ReferenceTXTButton.setDisable(! buttontoggled);
        if (! buttontoggled) {
            ReferenceHTMLButton.setSelected(false);
            ReferenceTXTButton.setSelected(false);
            Root.getOptions().getSessionOptions().setReferencetype(null);
            Session.closereferencefile();
            Session.togglevolumebinding();
        } else {
            if (Root.getOptions().getSessionOptions().getReferencetype() == null) {Root.getOptions().getSessionOptions().setReferencetype(Options.DEFAULT_REFERENCE_TYPE_OPTION);}
            switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                case html:
                    ReferenceHTMLButton.setSelected(true);
                    htmlreferenceoptionselected(null);
                    break;
                case txt:
                    ReferenceTXTButton.setSelected(true);
                    txtreferenceoptionselected(null);
                    break;
            }
            if (! Session.checkallreferencefilesforsession(Root.getOptions().getSessionOptions().getReferencetype(), true)) {
                ReferenceToggleButton.setSelected(false);
                togglereference(null);
            }
            if (Session.getPlayerState() == PlayerState.PLAYING) {
                Session.displayreferencefile();
                Session.togglevolumebinding();
            }
        }
    }
    public void htmlreferenceoptionselected(ActionEvent actionEvent) {
        if (ReferenceToggleButton.isSelected()) {
            ReferenceTXTButton.setSelected(! ReferenceHTMLButton.isSelected());
            if (ReferenceHTMLButton.isSelected()) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.html);}
            else {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.txt);}
        } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
    }
    public void txtreferenceoptionselected(ActionEvent actionEvent) {
        if (ReferenceToggleButton.isSelected()) {
            ReferenceHTMLButton.setSelected(! ReferenceTXTButton.isSelected());
            if (ReferenceTXTButton.isSelected()) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.txt);}
            else {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.html);}
        } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
    }
    public void cleanupPlayer() {}
    public void reset() {
        CutCurrentLabel.setText("--:--");
        CurrentCutProgress.setProgress(0.0);
        CutTotalLabel.setText("--:--");
        TotalCurrentLabel.setText("--:--");
        TotalProgress.setProgress(0.0);
        TotalTotalLabel.setText("--:--");
        EntrainmentVolume.setDisable(true);
        EntrainmentVolumePercentage.setText("0%");
        AmbienceVolume.setDisable(true);
        AmbienceVolumePercentage.setText("0%");
        // TODO Reset Goal UI Here
        PlayButton.setText("Start");
        PauseButton.setDisable(true);
        StopButton.setDisable(true);
    }

    // Dialogs
    public static class DisplayReference extends Stage {
        public ScrollPane ContentPane;
        public Slider EntrainmentVolumeSlider;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolumeSlider;
        public Label AmbienceVolumePercentage;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public ProgressBar TotalProgress;
        public ProgressBar CurrentProgress;
        public Label CurrentName;
        public Label CurrentPercentage;
        public Label TotalPercentage;
        private MainController Root;
        private Meditatable currentmeditatable;
        private ReferenceType referenceType;
        private Boolean fullscreenoption;
        private Scene scene;

        public DisplayReference(MainController root, Meditatable currentmeditatable) {
            try {
                Root = root;
                this.currentmeditatable = currentmeditatable;
                referenceType = Root.getOptions().getSessionOptions().getReferencetype();
                fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
                fxmlLoader.setController(this);
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(this);
//                this.setResizable(false);
                setTitle(this.currentmeditatable.name + "'s Reference");
                setsizing();
                loadcontent();
                AmbienceVolumeSlider.setValue(Root.getPlayer().AmbienceVolume.getValue());
                AmbienceVolumePercentage.setText(Root.getPlayer().AmbienceVolumePercentage.getText());
                EntrainmentVolumeSlider.setValue(Root.getPlayer().EntrainmentVolume.getValue());
                EntrainmentVolumePercentage.setText(Root.getPlayer().EntrainmentVolumePercentage.getText());
                setOnCloseRequest(event -> untoggleplayerreference());
                if (Root.getSession().getCurrentindexofplayingelement() == 0) {
                    setFullScreenExitHint("Press F11 To Toggle Fullscreen, ESC To Hide Reference");
                } else {setFullScreenExitHint("");}
                addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        switch (event.getCode()) {
                            case ESCAPE:
                                // TODO Closing Reference Display On Escape Is Crashing The Whole App
//                                hide();
//                                untoggleplayerreference();
//                                break;
                            case F11:
                                if (Root.getSession().getPlayerState() == PlayerState.PLAYING) {
                                    boolean fullscreen = this.isFullScreen();
                                    fullscreenoption = !fullscreen;
                                    Root.getOptions().getSessionOptions().setReferencefullscreen(fullscreenoption);
                                    setsizing();
                                    if (!fullscreen) {setFullScreenExitHint("");}
                                    break;
                                }
                        }
                });
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }
        public DisplayReference(MainController root, String htmlcontent) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferencePreview.fxml"));
            fxmlLoader.setController(this);
            try {
                scene = new Scene(fxmlLoader.load());
                setScene(scene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference File Preview");
            fullscreenoption = false;
            setsizing();
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.setUserStyleSheetLocation(new File(Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
            webEngine.loadContent(htmlcontent);
            ContentPane.setContent(browser);
        }

        public void setsizing() {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double height = primaryScreenBounds.getHeight();
            double width = primaryScreenBounds.getWidth();
            if (! fullscreenoption) {height -= 100; width -= 100;}
            this.setFullScreen(fullscreenoption);
            this.setHeight(height);
            this.setWidth(width);
            this.centerOnScreen();
            ContentPane.setFitToWidth(true);
            ContentPane.setFitToHeight(true);
            ContentPane.setStyle("-fx-background-color: #212526");
        }
        public void loadcontent() {
            File referencefile = currentmeditatable.getReferenceFile();
            if (referencefile != null) {
                switch (referenceType) {
                    case txt:
                        StringBuilder sb = new StringBuilder();
                        try (FileInputStream fis = new FileInputStream(referencefile);
                             BufferedInputStream bis = new BufferedInputStream(fis)) {
                            while (bis.available() > 0) {
                                sb.append((char) bis.read());
                            }
                        } catch (Exception e) {
                            new MainController.ExceptionDialog(Root, e).showAndWait();
                        }
                        TextArea ta = new TextArea();
                        ta.setText(sb.toString());
                        ta.setWrapText(true);
                        ContentPane.setContent(ta);
                        Root.getOptions().setStyle(this);
                        break;
                    case html:
                        WebView browser = new WebView();
                        WebEngine webEngine = browser.getEngine();
                        webEngine.load(referencefile.toURI().toString());
                        webEngine.setUserStyleSheetLocation(new File(Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
                        ContentPane.setContent(browser);
                        break;
                    default:
                        break;
                }
            } else {System.out.println("Reference File Is Null");}
        }
        public void untoggleplayerreference() {
            Root.getPlayer().ReferenceToggleButton.setSelected(false);
            Root.getPlayer().togglereference(null);
        }

        public void play(ActionEvent actionEvent) {Root.getSession().play(Root.getPlayer());}
        public void pause(ActionEvent actionEvent) {Root.getSession().pause();}
        public void stop(ActionEvent actionEvent) {Root.getSession().stop();}

    }

// Enumerators
    public enum ReferenceType {
        html, txt
    }
    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE, FADING_PLAY, FADING_RESUME, FADING_PAUSE, FADING_STOP
    }

}
