package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.ReferenceType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DisplayReference extends Stage {
    public ScrollPane ContentPane;
    private Cut currentcut;
    private ReferenceType referenceType;

    public DisplayReference(Cut currentcut, ReferenceType referenceType, Boolean fullscreenoption) {
        this.currentcut = currentcut;
        this.referenceType = referenceType;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Checking Ambience");}
        catch (IOException e) {e.printStackTrace();}
        // TODO FullScreenOption Is null. Why?
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double height = primaryScreenBounds.getHeight();
        double width = primaryScreenBounds.getWidth();
        this.setHeight(height);
        this.setWidth(width);
        this.setFullScreen(true);
        ContentPane.setFitToWidth(true);
        ContentPane.setFitToHeight(true);
//        } else {
//            // TODO Set Adjusted Height And Width Here
//        }
        loadcontent();
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
