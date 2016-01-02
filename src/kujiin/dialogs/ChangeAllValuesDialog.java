package kujiin.dialogs;


import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kujiin.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeAllValuesDialog extends Stage implements Initializable {
    public Button AcceptButton;
    public Button CancelButton;
    public TextField changeAllValuesMinutesTextField;
    public CheckBox PresessionCheckbox;
    public CheckBox PostsessionCheckBox;

    public ChangeAllValuesDialog(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Change All Values To:");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tools.numericTextField(changeAllValuesMinutesTextField);
    }

    public void acceptbuttonpressed(Event event) {this.close();}
    public void cancelbuttonpressed(Event event) {this.close();}
    public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
    public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
    public Integer getminutes() {
        try {return Integer.parseInt(changeAllValuesMinutesTextField.getText());}
        catch (NumberFormatException e) {return 0;}
    }
}
