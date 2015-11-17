package kujiin.dialogs;


import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kujiin.Goals;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GoalPacingDialog extends Stage implements Initializable{
    private Goals goals;

    public GoalPacingDialog(Parent parent, Goals goals) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
        catch (IOException e) {e.printStackTrace();}
        this.goals = goals;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
