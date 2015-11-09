package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.Database;
import kujiin.Goals;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class DisplayCurrentGoalsDialog extends Stage implements Initializable {
    public TableView currentgoaltable;
    public TableColumn NumberColumn;
    public TableColumn GoalTimeColumn;
    public TableColumn DueDateColumn;
    public TableColumn PercentCompleteColumn;
    public Button CloseButton;
    private Goals goals;

    public DisplayCurrentGoalsDialog(Parent parent, Goals sessiondb) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCurrentGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
        catch (IOException e) {e.printStackTrace();}
        this.goals = sessiondb;
    }

    public void closeDialog(Event event) {this.close();}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
