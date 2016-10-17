package kujiin.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import kujiin.xml.Options;

public class InformationDialog {

    public InformationDialog(Options options, String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(options. getUserInterfaceOptions().getThemefile());
        a.showAndWait();
    }
}
