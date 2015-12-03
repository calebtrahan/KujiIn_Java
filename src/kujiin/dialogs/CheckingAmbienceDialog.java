package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CheckingAmbienceDialog extends Stage {
    public Button CancelButton;
    public Label Message;

    public CheckingAmbienceDialog() {
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CheckingAmbienceDialog.fxml"));
         fxmlLoader.setController(this);
         try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Checking Ambience");}
         catch (IOException e) {e.printStackTrace();}
     }
}
