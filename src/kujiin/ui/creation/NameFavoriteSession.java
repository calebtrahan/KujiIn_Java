package kujiin.ui.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.FavoriteSession;
import kujiin.xml.FavoriteSessions;

import java.io.IOException;
import java.util.ArrayList;

public class NameFavoriteSession extends StyledStage {
    public TextField NameTextField;
    public Button AcceptButton;
    public Button CancelButton;
    private boolean accepted = false;
    private ArrayList<String> names = new ArrayList<>();
    private String name;

    public NameFavoriteSession(FavoriteSessions favoriteSessions) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/NameFavoriteSession.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Enter Name");
            if (favoriteSessions.getFavoriteSessions() != null) {
                for (FavoriteSession i : favoriteSessions.getFavoriteSessions()) {
                    names.add(i.getName());
                }
            }
            NameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.length() == 0) {AcceptButton.setDisable(true); return;}
                boolean validvalue = true;
                for (String i : names) {if (i.toLowerCase().equals(newValue.toLowerCase())) {validvalue = false; break;}}
                AcceptButton.setDisable(! validvalue);
                if (! validvalue) {
                    Tooltip tooltip = new Tooltip("There is Already A Favorite Session Named " + newValue + ". Select A Different Name");
                    AcceptButton.setTooltip(tooltip);
                    NameTextField.setTooltip(tooltip);
                } else {
                    AcceptButton.setTooltip(null);
                    NameTextField.setTooltip(null);
                }
            });
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public String getName() {
        return name;
    }

    // Button Actions
    public void accept() {
        name = NameTextField.getText();
        accepted = true;
        close();
    }
    public void cancel() {
        close();
    }

}
