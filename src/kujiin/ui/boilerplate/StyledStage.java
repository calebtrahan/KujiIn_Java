package kujiin.ui.boilerplate;

import javafx.stage.Stage;
import kujiin.xml.Preferences;

public class StyledStage extends Stage {

    public StyledStage() {
        setOnShowing(event -> {
            getScene().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
            getIcons().add(Preferences.PROGRAM_ICON);
        });

    }

}
