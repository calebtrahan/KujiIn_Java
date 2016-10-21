package kujiin.ui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.util.SessionPart;
import kujiin.util.enums.PlayerState;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Options;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DisplayReference extends Stage {
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
    private Boolean fullscreenoption;
    private Scene scene;
    private ReferenceType referenceType;
    private MainController Root;

    public DisplayReference(MainController Root, SessionPart sessionPart, boolean showsuggestions) {
        try {
            this.Root = Root;
            referenceType = Root.getOptions().getSessionOptions().getReferencetype();
            fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ReferenceDisplay.fxml"));
            fxmlLoader.setController(this);
            scene = new Scene(fxmlLoader.load());
            setScene(scene);
            Root.getOptions().setStyle(this);
//                this.setResizable(false);
            setTitle(sessionPart.name + "'s Reference");
            setsizing();
            loadcontent(sessionPart.reference_getFile());
            AmbienceVolumeSlider.setValue(Root.getSessionCreator().getPlayer().AmbienceVolume.getValue());
            AmbienceVolumePercentage.setText(Root.getSessionCreator().getPlayer().AmbienceVolumePercentage.getText());
            EntrainmentVolumeSlider.setValue(Root.getSessionCreator().getPlayer().EntrainmentVolume.getValue());
            EntrainmentVolumePercentage.setText(Root.getSessionCreator().getPlayer().EntrainmentVolumePercentage.getText());
            setOnCloseRequest(event -> untoggleplayerreference());
            if (showsuggestions) {setFullScreenExitHint("Press F11 To Toggle Fullscreen, ESC To Hide Reference");}
            else {setFullScreenExitHint("");}
            addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case ESCAPE:
//                                hide();
//                                untoggleplayerreference();
//                                break;
                    case F11:
                        if (Root.getSessionCreator().getPlayerState() == PlayerState.PLAYING) {
                            boolean fullscreen = this.isFullScreen();
                            fullscreenoption = !fullscreen;
                            Root.getOptions().getSessionOptions().setReferencefullscreen(fullscreenoption);
                            setsizing();
                            if (!fullscreen) {setFullScreenExitHint("");}
                            break;
                        }
                }
            });
        } catch (IOException ignored) {}
    }
    public DisplayReference(MainController Root, String htmlcontent) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ReferencePreview.fxml"));
            fxmlLoader.setController(this);
            scene = new Scene(fxmlLoader.load());
            setScene(scene);
            Root.getOptions().setStyle(this);
            this.setResizable(false);
            setTitle("Reference File Preview");
            fullscreenoption = false;
            setsizing();
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
            webEngine.loadContent(htmlcontent);
            ContentPane.setContent(browser);
        } catch (IOException ignored) {}
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
    public void loadcontent(File referencefile) {
        if (referencefile != null) {
            switch (referenceType) {
                case txt:
                    StringBuilder sb = new StringBuilder();
                    try (FileInputStream fis = new FileInputStream(referencefile);
                         BufferedInputStream bis = new BufferedInputStream(fis)) {
                        while (bis.available() > 0) {
                            sb.append((char) bis.read());
                        }
                    } catch (Exception ignored) {}
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
                    webEngine.setUserStyleSheetLocation(Options.REFERENCE_THEMEFILE.toURI().toString());
                    ContentPane.setContent(browser);
                    break;
                default:
                    break;
            }
        } else {System.out.println("Reference File Is Null");}
    }
    public void untoggleplayerreference() {
        Root.getSessionCreator().getPlayer().ReferenceCheckBox.setSelected(false);
        Root.getSessionCreator().getPlayer().togglereference();
    }

    public void play() {Root.getSessionCreator().getPlayer().play();}
    public void pause() {Root.getSessionCreator().getPlayer().pause();}
    public void stop() {Root.getSessionCreator().getPlayer().stop();}

}