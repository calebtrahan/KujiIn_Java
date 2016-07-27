package kujiin.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import kujiin.MainController;

import java.io.IOException;

public class SimpleTextDialogWithCancelButton extends Stage {
    public Button CancelButton;
    public Label Message;
    public Label TopTitle;
    private MainController Root;

    public SimpleTextDialogWithCancelButton(MainController root, String titletext, String toptitletext, String message) {
        Root = root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SimpleTextDialogWithCancelButton.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions().setStyle(this);
            this.setResizable(false);
        } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        setTitle(titletext);
        Message.setText(message);
        TopTitle.setText(toptitletext);
    }
}
