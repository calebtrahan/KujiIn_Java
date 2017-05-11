package kujiin.ui.dialogs.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.util.Util;
import kujiin.xml.Preferences;

import java.util.Optional;

public class AnswerDialog {
    private Util.AnswerType result;

    public AnswerDialog(Preferences preferences, Stage stage, String title, String header, String content, String yesbuttontext, String nobuttontext, String cancelbuttontext, boolean modal) {
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");} else {yes = new ButtonType(yesbuttontext);}
        if (nobuttontext != null) {no = new ButtonType("No");} else {no = new ButtonType(nobuttontext);}
        if (cancelbuttontext != null) {cancel = new ButtonType("Cancel");} else {cancel = new ButtonType(cancelbuttontext);}
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, content, yes, no, cancel);
        a.setTitle(title);
        if (header != null) {a.setHeaderText(header);}
        a.getDialogPane().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
        if (modal) {a.initModality(Modality.APPLICATION_MODAL);}
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            ButtonType buttonanswer = answer.get();
            if (buttonanswer == yes) {result = Util.AnswerType.YES;}
            else if (buttonanswer == no) {result = Util.AnswerType.NO;}
            else if (buttonanswer == cancel) {result = Util.AnswerType.CANCEL;}
        }
    }
    public AnswerDialog(Preferences preferences, Stage stage, String title, String header, String content, String yesbuttontext, String nobuttontext, String cancelbuttontext) {
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");} else {yes = new ButtonType(yesbuttontext);}
        if (nobuttontext != null) {no = new ButtonType("No");} else {no = new ButtonType(nobuttontext);}
        if (cancelbuttontext != null) {cancel = new ButtonType("Cancel");} else {cancel = new ButtonType(cancelbuttontext);}
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, content, yes, no, cancel);
        a.setTitle(title);
        if (header != null) {a.setHeaderText(header);}
        a.getDialogPane().getStylesheets().add(Preferences.DEFAULTSTYLESHEET.toURI().toString());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            ButtonType buttonanswer = answer.get();
            if (buttonanswer == yes) {result = Util.AnswerType.YES;}
            else if (buttonanswer == no) {result = Util.AnswerType.NO;}
            else if (buttonanswer == cancel) {result = Util.AnswerType.CANCEL;}
        }
    }

    public Util.AnswerType getResult() {
        return result;
    }

}
