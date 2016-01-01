package kujiin.dialogs;


import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kujiin.util.xml.CurrentGoal;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GoalPacingDialog extends Stage implements Initializable{

    public GoalPacingDialog(Parent parent, CurrentGoal currentGoal) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
