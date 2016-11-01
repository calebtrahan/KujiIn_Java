package kujiin.ui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.util.Util;

import java.io.IOException;

import static kujiin.xml.Options.PROGRAM_ICON;

public class ChangeAllValues extends Stage {
    public Button AcceptButton;
    public Button CancelButton;
    public TextField MinutesTextField;
    public CheckBox PresessionCheckbox;
    public CheckBox PostsessionCheckBox;
    private Boolean accepted;
    private int minutes;

    public ChangeAllValues(MainController Root, String toptext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ChangeAllValuesDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            String themefile = Root.getOptions().getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
            this.setResizable(false);
            setTitle(toptext);
            setAccepted(false);
            MinutesTextField.setText("0");
            Util.custom_textfield_integer(MinutesTextField, 0, 600, 1);
            MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {setMinutes(Integer.parseInt(MinutesTextField.getText()));}
                catch (NumberFormatException ignored) {setMinutes(0);}
            });
        } catch (IOException ignored) {}
    }

    // Getters And Setters
    public Boolean getAccepted() {
        return accepted;
    }
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
    public int getMinutes() {
        return minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    // Button Actions
    public void acceptbuttonpressed(ActionEvent event) {setAccepted(true); close();}
    public void cancelbuttonpressed(ActionEvent event) {setAccepted(false); close();}
    public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
    public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}

}
