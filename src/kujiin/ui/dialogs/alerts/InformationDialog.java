package kujiin.ui.dialogs.alerts;

import javafx.scene.control.Alert;
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
        a.getDialogPane().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
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
        a.getDialogPane().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
        a.showAndWait();
    }
}
