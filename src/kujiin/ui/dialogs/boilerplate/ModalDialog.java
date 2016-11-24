package kujiin.ui.dialogs.boilerplate;

import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.ui.MainController;

import static kujiin.xml.Preferences.PROGRAM_ICON;

public class ModalDialog extends Stage {

    public ModalDialog(MainController Root, Stage stage, boolean minimizeparent) {
        if (minimizeparent) {stage.setIconified(true);}
        getIcons().clear();
        getIcons().add(PROGRAM_ICON);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(stage);
        setOnShowing(event -> {
            String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
        });

    }

}
