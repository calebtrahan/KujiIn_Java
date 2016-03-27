package kujiin.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import kujiin.MainController;

import java.io.IOException;

public class SimpleTextDialog extends Stage {
    public Label Message;
    private MainController Root;

    public SimpleTextDialog(MainController root, String toptitle, String message) {
        Root = root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleTextDialog.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions().setStyle(this);
        } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        setTitle(toptitle);
        Message.setText(message);
        Message.setWrapText(true);
    }
}
