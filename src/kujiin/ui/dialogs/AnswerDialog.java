package kujiin.ui.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import kujiin.util.Util;
import kujiin.xml.Options;

import java.util.Optional;

public class AnswerDialog {
    private Util.AnswerType result;

    public AnswerDialog(Options options, String title, String header, String content, String yesbuttontext, String nobuttontext, String cancelbuttontext) {
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");
        } else {
            yes = new ButtonType(yesbuttontext);
        }
        if (nobuttontext != null) {
            no = new ButtonType("No");
        } else {
            no = new ButtonType(nobuttontext);
        }
        if (cancelbuttontext != null) {
            cancel = new ButtonType("Cancel");
        } else {
            cancel = new ButtonType(cancelbuttontext);
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, content, yes, no, cancel);
        a.setTitle(title);
        if (header != null) {a.setHeaderText(header);}
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(options.getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {
                result = Util.AnswerType.YES;
            } else if (answer.get() == no) {
                result = Util.AnswerType.NO;
            } else if (answer.get() == cancel) {
                result = Util.AnswerType.CANCEL;
            }
        }
    }

    public Util.AnswerType getResult() {
        return result;
    }

}
