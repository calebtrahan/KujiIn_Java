package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditReferenceFiles extends Stage implements Initializable {

    public ChoiceBox CutNamesChoiceBox;
    public ChoiceBox CutVariationsChoiceBox;
    public TextField CurrentlyEditingNameTextField;
    public TextField CurrentlyEditingVariationTextField;
    public TextArea MainTextArea;
    public Button CloseButton;
    public Button LoadButton;

    public EditReferenceFiles(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/EditReferenceFiles.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void LoadReferenceFile(Event event) {

    }

    public void closewindow(Event event) {this.close();}
}
