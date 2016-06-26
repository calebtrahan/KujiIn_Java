package kujiin.ui;

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
import kujiin.util.Meditatable;
import kujiin.util.This_Session;
import kujiin.util.Util;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
// TODO Fix Set Multiple Goal Minutes (And Add Check If Long Enough Logic On Accepting)
// TODO Select Button On Options -> ChangeAlertFileDialog Instead Of Just A File Chooser
// TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)

// TODO Entrainment Volume Was Raised To 100% When Switching To TOH (And Also Broke Reference File)
// TODO NullPointerException in Meditatable:181 < Meditatable:256

// TODO !IMPORTANT Organize, Encapsulate And Fix This Player Binding/Unbinding Logic So It's Simple And Encapsulated

// TODO Volume:
//      Pre Ramp Is Too Loud

// TODO Confirmation -> Alert File On LONG Sessions (Deep In Trance)

// TODO Reference Turns Off After Each Cut Ending
// TODO While Fading In/Out Display Reference Selecting/Deselecting


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
    public Label GoalProgressLabel;
    private This_Session Session;
    private MainController Root;
    public boolean displaynormaltime;

    public PlayerUI(MainController root) {
        Root = root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionPlayerDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions();
            Root.getOptions().setStyle(this);
            this.setResizable(false);
            this.setOnCloseRequest(event -> {if (Session.endsessionprematurely()) {close(); cleanupPlayer();} else {play(); event.consume();}});
        } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        setTitle("Session Player");
        Root = root;
        Session = root.getSession();
        EntrainmentVolume.setDisable(true);
        EntrainmentVolumePercentage.setText("0%");
        AmbienceVolume.setDisable(true);
        AmbienceVolumePercentage.setText("0%");
        ReferenceToggleButton.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
        togglereference(null);
        displaynormaltime = true;
        CutTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
        TotalTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
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
        private Meditatable currentcutorelement;
        private ReferenceType referenceType;
        private Boolean fullscreenoption;
        private Scene scene;

        public DisplayReference(MainController root, Meditatable currentcutorelement) {
            Root = root;
            this.currentcutorelement = currentcutorelement;
            referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
            if (isValid()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
                fxmlLoader.setController(this);
                try {
                    scene = new Scene(fxmlLoader.load());
                    setScene(scene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                    setTitle(this.currentcutorelement.name + "'s Reference");
                    setsizing();
                    loadcontent();
                    AmbienceVolumeSlider.setValue(Root.getPlayer().AmbienceVolume.getValue());
                    AmbienceVolumePercentage.setText(Root.getPlayer().AmbienceVolumePercentage.getText());
                    EntrainmentVolumeSlider.setValue(Root.getPlayer().EntrainmentVolume.getValue());
                    EntrainmentVolumePercentage.setText(Root.getPlayer().EntrainmentVolumePercentage.getText());
                    this.setOnCloseRequest(event -> {
                        Root.getPlayer().ReferenceToggleButton.setSelected(false);
                        Root.getPlayer().togglereference(null);
                    });
                    this.setFullScreenExitHint("Press F11 To Exit FullScreen, ESC To Close Reference File");
                } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
                // TODO Find Out Why ESC Event Filter Is Crashing The Whole App
//                this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//                    if (Root.getSession().getPlayerState() == PlayerState.PLAYING) {
//                        switch (event.getCode()) {
//                            case ESCAPE:
//                                close();
//                                break;
//                            case UP:
//                                break;
//                            case DOWN:
//                                break;
//                        }
//                    }
//                });
            }
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
            }
        }
        public boolean isValid() {
            if (referenceType == null) {
                Util.gui_showinformationdialog(Root, "Cannot Display", "No Reference Type/Variation Selected", "Select A Type/Variation");
                return false;
            }
            File referencefile = currentcutorelement.getReferenceFile();
            if (! referencefile.exists()) {
                Util.gui_showinformationdialog(Root, "Cannot Display", "Cannot Display Reference File", "Non-Existent Reference File");
                return false;
            }
            String contents = Util.file_getcontents(referencefile);
            if (contents == null) {
                Util.gui_showinformationdialog(Root, "Cannot Display", "Cannot Display Empty Reference File", "Use The Reference Editor To Add New Reference Content");
                return false;
            }
            switch (referenceType) {
                case html:
                    return contents.length() > 0 && Util.String_validhtml(contents);
                case txt:
                    return contents.length() > 0;
                default:
                    return false;
            }
        }

        public void play(ActionEvent actionEvent) {Root.getSession().play(Root.getPlayer());}
        public void pause(ActionEvent actionEvent) {Root.getSession().pause();}
        public void stop(ActionEvent actionEvent) {Root.getSession().stop();}
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
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference Type Select");
            if (referenceType != null) {
                switch (referenceType) {
                    case txt:
                        TextOption.setSelected(true);
                        break;
                    case html:
                        HTMLOption.setSelected(true);
                        break;
                }
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
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                Sessions currentsessions = Root.getProgressTracker().getSessions();
                Session thissession = currentsessions.sessioninformation_getspecificsession(currentsessions.sessioninformation_totalsessioncount() - 1);
                int thisessionminutes = thissession.getTotal_Session_Duration();
                SessionDuration.setText(Util.format_minstohrsandmins_abbreviated(thisessionminutes));
                SessionDuration.setOnKeyTyped(root.NONEDITABLETEXTFIELD);
                int totalsessionminutes = currentsessions.sessioninformation_getallsessiontotals(11, true);
                TotalPracticeDuration.setText(Util.format_minstohrsandmins_abbreviated(totalsessionminutes));
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
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE, FADING_PLAY, FADING_RESUME, FADING_PAUSE, FADING_STOP
    }

}
