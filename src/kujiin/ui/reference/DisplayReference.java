package kujiin.ui.reference;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import kujiin.ui.boilerplate.StyledStage;

import java.io.IOException;

import static kujiin.xml.Preferences.PROGRAM_ICON;

public class DisplayReference extends StyledStage {
    public ScrollPane ReferencePreviewScrollPane;

    public DisplayReference(String html) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/reference/ReferencePreview.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            initModality(Modality.WINDOW_MODAL);
            setTitle("Reference File Preview");
            WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.loadContent(html);
            webEngine.setUserStyleSheetLocation(kujiin.xml.Preferences.REFERENCE_THEMEFILE.toURI().toString());
            ReferencePreviewScrollPane.setContent(browser);
            ReferencePreviewScrollPane.setFitToHeight(true);
            ReferencePreviewScrollPane.setFitToWidth(true);
        } catch (IOException ignored) {}
    }
}