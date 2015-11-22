package kujiin.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kujiin.util.xml.CurrentGoal;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class DisplayCurrentGoalsDialog extends Stage implements Initializable {
    public TableView currentgoaltable;
    public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
    public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
    public TableColumn<CurrentGoalBinding, String> DueDateColumn;
    public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
    public Button CloseButton;
    private ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();

    public DisplayCurrentGoalsDialog(Parent parent, List<CurrentGoal> currentGoalList, double currentpracticedhours) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCurrentGoals.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
        catch (IOException e) {e.printStackTrace();}
        currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
        NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
        GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
        DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
        PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
    }

    public void closeDialog(Event event) {this.close();}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public class CurrentGoalBinding {
        private IntegerProperty goalid;
        private StringProperty goalhours;
        private StringProperty duedate;
        private StringProperty percentcomplete;

        public CurrentGoalBinding(int id, String goalhours, String duedate, String percentcomplete) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.duedate = new SimpleStringProperty(duedate);
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
        }
    }
}
