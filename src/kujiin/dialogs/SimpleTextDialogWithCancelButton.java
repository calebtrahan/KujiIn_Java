package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleTextDialogWithCancelButton extends Stage {
    public Button CancelButton;
    public Label Message;
    public Label TopTitle;

    public SimpleTextDialogWithCancelButton(String titletext, String toptitletext, String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SimpleTextDialogWithCancelButton.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle(titletext);}
        catch (IOException e) {e.printStackTrace();}
        Message.setText(message);
        TopTitle.setText(toptitletext);
    }
}
