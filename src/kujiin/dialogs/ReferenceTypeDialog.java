package kujiin.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import kujiin.ReferenceType;

import java.io.IOException;

public class ReferenceTypeDialog extends Stage {
    public Button AcceptButton;
    public RadioButton HTMLOption;
    public RadioButton TextOption;
    public Button CancelButton;
    public CheckBox FullScreenOption;
    private ReferenceType referenceType = null;
    private Boolean fullscreen = null;
    private Boolean enabled;

    public ReferenceTypeDialog (ReferenceType referenceType, Boolean fullscreenoption) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceTypeDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Reference Type Variation");}
        catch (IOException e) {e.printStackTrace();}
        if (referenceType != null) {
            if (referenceType == ReferenceType.txt) {TextOption.setSelected(true);}
            else if (referenceType == ReferenceType.html) {HTMLOption.setSelected(true);}
        }
        if (fullscreenoption != null) {FullScreenOption.setSelected(fullscreenoption);}
    }

// Getters And Setters
    public ReferenceType getReferenceType() {
        return referenceType;
    }
    public Boolean getFullscreen() {return fullscreen;}
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

// Button Actions
    public void selecthtml(ActionEvent actionEvent) {
        if (HTMLOption.isSelected()) {TextOption.setSelected(false);}
    }
    public void selecttxt(ActionEvent actionEvent) {
        if (TextOption.isSelected()) {HTMLOption.setSelected(false);}
    }
    public void accept(ActionEvent actionEvent) {
        if (HTMLOption.isSelected()) {referenceType = ReferenceType.html;}
        else if (TextOption.isSelected()) {referenceType = ReferenceType.txt;}
        setFullScreen(FullScreenOption.isSelected());
        setEnabled(true);
        this.close();
    }
    public void cancel(ActionEvent actionEvent) {
        setEnabled(false);
        this.close();
    }

}