package kujiin.ui.dialogs.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import kujiin.xml.Preferences;

public class ErrorDialog {

    public ErrorDialog(Preferences preferences, String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        if (headertext != null) {a.setHeaderText(headertext);}
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        a.showAndWait();
    }

}