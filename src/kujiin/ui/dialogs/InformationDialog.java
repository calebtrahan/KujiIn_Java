package kujiin.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import kujiin.xml.Preferences;

public class InformationDialog {

    public InformationDialog(Preferences preferences, String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        a.showAndWait();
    }
}
