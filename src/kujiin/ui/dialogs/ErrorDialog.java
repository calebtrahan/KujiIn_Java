package kujiin.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import kujiin.xml.Options;

public class ErrorDialog {

    public ErrorDialog(Options options, String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        if (headertext != null) {a.setHeaderText(headertext);}
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(options.getAppearanceOptions().getThemefile());
        a.showAndWait();
    }

}
