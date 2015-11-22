package kujiin.dialogs;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import java.io.IOException;
import java.util.List;

public class DisplayCompletedGoalsDialog extends Stage {

    public TableView<CompletedGoal> currentgoaltable;
    public TableColumn<CompletedGoal, Integer> NumberColumn;
    public TableColumn<CompletedGoal, String> GoalTimeColumn;
    public TableColumn<CompletedGoal, String> CompletedOnColumn;
    public Button CloseButton;
    private ObservableList<CompletedGoal> completedGoals = FXCollections.observableArrayList();

    public DisplayCompletedGoalsDialog(Parent parent, List<kujiin.util.xml.CompletedGoal> completedgoals) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Completed Goals");}
        catch (IOException e) {e.printStackTrace();}
        for (kujiin.util.xml.CompletedGoal i : completedgoals) {
            completedGoals.add(new CompletedGoal(completedgoals.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Completed()));
        }
        NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
        GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
        CompletedOnColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
        currentgoaltable.setItems(completedGoals);
    }

    public void closeDialog(Event event) {this.close();}


    public class CompletedGoal {
        public IntegerProperty goalid;
        public StringProperty goalhours;
        public StringProperty datecompleted;

        public CompletedGoal(int goalid, String goalhours, String datecompleted) {
            this.goalid = new SimpleIntegerProperty(goalid);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }
}
