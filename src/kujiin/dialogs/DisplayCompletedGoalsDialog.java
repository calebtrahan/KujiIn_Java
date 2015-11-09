package kujiin.dialogs;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.Database;
import kujiin.Goals;

import java.io.IOException;

public class DisplayCompletedGoalsDialog extends Stage {

    public TableView<Goals.CompletedGoal> currentgoaltable;
    public TableColumn<Goals.CompletedGoal, Integer> NumberColumn;
    public TableColumn<Goals.CompletedGoal, String> GoalTimeColumn;
    public TableColumn<Goals.CompletedGoal, String> CompletedOnColumn;
    public Button CloseButton;
    private Goals goals;
    ObservableList<Goals.CompletedGoal> completedGoals = FXCollections.observableArrayList();

    public DisplayCompletedGoalsDialog(Parent parent, Database sessiondb, Goals goals) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
        catch (IOException e) {e.printStackTrace();}
        this.goals = goals;
        completedGoals.addAll(goals.completedgoals);
        NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
        GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
        CompletedOnColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
        currentgoaltable.setItems(completedGoals);
    }

    public void closeDialog(Event event) {this.close();}

}
