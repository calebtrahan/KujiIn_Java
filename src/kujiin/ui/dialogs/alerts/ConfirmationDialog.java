package kujiin.ui.dialogs.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import kujiin.xml.Preferences;

import java.util.Optional;

public class ConfirmationDialog {
    private boolean result;

    public ConfirmationDialog(Preferences preferences, String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext, boolean modal) {
        String yestext;
        String notext;
        if (yesbuttontext != null) {
            yestext = yesbuttontext;
        } else {
            yestext = "Yes";
        }
        if (nobuttontext != null) {
            notext = nobuttontext;
        } else {
            notext = "No";
        }
        ButtonType yes = new ButtonType(yestext, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(notext, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, contenttext, yes, no);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        if (modal) {a.initModality(Modality.APPLICATION_MODAL);}
        Optional<ButtonType> answer = a.showAndWait();
        result = answer.isPresent() && answer.get() == yes;
    }
    public ConfirmationDialog(Preferences preferences, String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext) {
        String yestext;
        String notext;
        if (yesbuttontext != null) {
            yestext = yesbuttontext;
        } else {
            yestext = "Yes";
        }
        if (nobuttontext != null) {
            notext = nobuttontext;
        } else {
            notext = "No";
        }
        ButtonType yes = new ButtonType(yestext, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(notext, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, contenttext, yes, no);
        a.setTitle(titletext);
        if (headertext != null) {
            a.setHeaderText(headertext);
        }
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        result = answer.isPresent() && answer.get() == yes;
    }
    public ConfirmationDialog(Preferences preferences, String title, String header, String content, boolean modal) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title);
        if (header != null) {a.setHeaderText(header);}
        if (content != null) {a.setContentText(content);}
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        if (modal) {a.initModality(Modality.APPLICATION_MODAL);}
        Optional<ButtonType> answer = a.showAndWait();
        result = answer.isPresent() && answer.get() == ButtonType.OK;
    }
    public ConfirmationDialog(Preferences preferences, String title, String header, String content) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title);
        if (header != null) {a.setHeaderText(header);}
        if (content != null) {a.setContentText(content);}
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(preferences. getUserInterfaceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        result = answer.isPresent() && answer.get() == ButtonType.OK;
    }

    public boolean getResult() {
        return result;
    }

}
