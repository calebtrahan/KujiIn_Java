package kujiin.ui.playback;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import kujiin.ui.boilerplate.StyledStage;

import java.io.IOException;

public class LoadingDialog extends StyledStage {
    public Label MessageLabel;

    public LoadingDialog(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/LoadingDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            MessageLabel.setText(message);
        } catch (IOException e) {e.printStackTrace();}
    }

}
