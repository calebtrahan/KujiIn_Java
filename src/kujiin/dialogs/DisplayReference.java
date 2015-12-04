package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import kujiin.Cut;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DisplayReference extends Stage implements Initializable {
    public ScrollPane ContentPane;

    public DisplayReference(Cut currentcut) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceDisplay.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Checking Ambience");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Set Height And Width Here
    }
}
