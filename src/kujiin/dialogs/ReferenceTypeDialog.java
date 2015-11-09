package kujiin.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import kujiin.ReferenceType;

import java.io.IOException;

public class ReferenceTypeDialog extends Stage {
    public Button AcceptButton;
    public ChoiceBox<String> ReferenceTypeChoiceBox;
    public Button HelpButton;
    String[] referencetypes = {"html", "txt"};
    private ReferenceType referenceType = null;

    public ReferenceTypeDialog (Parent parent, ReferenceType reftype) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferenceTypeDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Reference Type Variation");}
        catch (IOException e) {e.printStackTrace();}
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(referencetypes);
        ReferenceTypeChoiceBox.setItems(items);
    }

    public void help(Event event) {
        // TODO Reference Help Button Call Goes Here
    }

    public void accept(Event event) {
        if (ReferenceTypeChoiceBox.getValue().equals(referencetypes[0])) {
            referenceType = ReferenceType.html;
        } else if (ReferenceTypeChoiceBox.getValue().equals(referencetypes[1])) {
            referenceType = ReferenceType.txt;
        } else {
            referenceType = null;
        }
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
}