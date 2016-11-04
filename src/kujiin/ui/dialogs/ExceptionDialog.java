package kujiin.ui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import kujiin.util.Util;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.time.LocalDate;

import static kujiin.xml.Preferences.PROGRAM_ICON;

public class ExceptionDialog extends Stage {
    public TextArea StackTraceTextField;
    public Button CloseButton;
    public Button ContinueButton;
    public CheckBox NotifyMeCheckbox;
    public Label TopText;

    public ExceptionDialog(Preferences preferences, Exception exception) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExceptionDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            String themefile = preferences.getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
            System.out.println(String.format("Time %s Encountered: %s", exception.getClass().getName(), LocalDate.now()));
            exception.printStackTrace();
            setTitle("Program Error Occured");
            TopText.setText(exception.getClass().getName() + " Occured");
            StackTraceTextField.setText(exception.getMessage());
            StackTraceTextField.setWrapText(true);
        } catch (IOException ignored) {}

    }

    public void exit(ActionEvent actionEvent) {
        if (NotifyMeCheckbox.isSelected()) {
            Util.sendstacktracetodeveloper(StackTraceTextField.getText());
        }
        this.close();
        System.exit(1);
    }
    public void continueprogram(ActionEvent actionEvent) {
        if (NotifyMeCheckbox.isSelected()) {
            Util.sendstacktracetodeveloper(StackTraceTextField.getText());
        }
        this.close();
    }

}