package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AmbienceTypeDialog extends Stage {
    String[] ambiencetypes = {"general", "specific"};
    String ambienceselection;

    public AmbienceTypeDialog (Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AmbienceTypeDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
        catch (IOException e) {e.printStackTrace();}
    }

    public void selectspecificambience(Event event) {
        ambienceselection = ambiencetypes[1];
        this.close();
    }

    public void selectgeneralambience(Event event) {
        ambienceselection = ambiencetypes[0];
        this.close();
    }

    public void helpbuttonpressed(Event event) {
        // TODO Call (Selecting Ambience Type) Help File Here
    }

    public void cancelbuttonpressed(Event event) {
        this.close();
    }

    public String getambiencetype() {
        return ambienceselection;
    }
}
