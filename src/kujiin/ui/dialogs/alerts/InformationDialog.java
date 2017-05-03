package kujiin.ui.dialogs.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import kujiin.xml.Preferences;

public class InformationDialog {

    public InformationDialog(Preferences preferences, String titletext, String headertext, String contexttext, boolean modal) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences.getUserInterfaceOptions().getThemefile());
        if (modal) {a.initModality(Modality.APPLICATION_MODAL);}
        a.showAndWait();
    }
    public InformationDialog(Preferences preferences, String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences.getUserInterfaceOptions().getThemefile());
        a.showAndWait();
    }
}
