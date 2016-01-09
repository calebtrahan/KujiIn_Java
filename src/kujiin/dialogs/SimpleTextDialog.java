package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleTextDialog extends Stage {
    public Label Message;

    public SimpleTextDialog(String toptitle, String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleTextDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle(toptitle);}
        catch (IOException e) {e.printStackTrace();}
        Message.setText(message);
        Message.setWrapText(true);
    }
}
