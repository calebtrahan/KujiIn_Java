package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.Goals;
import kujiin.Tools;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class SetANewGoalDialog extends Stage implements Initializable {
    public Label InformationLabel;
    public Spinner<Integer> GoalHoursSpinner;
    public DatePicker GoalDatePicker;
    public Button CancelButton;
    public Button OKButton;
    public Button CurrentGoalsButton;
    public Spinner<Integer> GoalMinutesSpinner;
    private Date goaldate;
    private double alreadypracticedhours;
    private int hours;
    private int minutes;
    private Goals goals;

    public SetANewGoalDialog(Parent parent, Goals goals) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
        catch (IOException e) {e.printStackTrace();}
//        alreadypracticedhours = sessiondb.gettotalpracticedhours();
        InformationLabel.setText("You Have Practiced For " + alreadypracticedhours + " Hours. Please Set A New Goal");
        this.goals = goals;
    }

    public void cancelgoalsetting(Event event) {this.close();}

    public void Accept(Event event) {
        // TODO Continue Right Here Working On Adding New Goals Getting Working
        hours = GoalHoursSpinner.getValue();
        minutes = GoalMinutesSpinner.getValue();
        double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
        LocalDate goaldate = GoalDatePicker.getValue();
//        boolean goalgood = goals.checkgoal(sessiondb.dateTimeFormatter.format(goaldate), alreadypracticedhours, newhours);
//        if (goalgood) {
//            boolean goalinserted = goals.insertgoal(sessiondb.dateTimeFormatter.format(goaldate), newhours);
//            if (goalinserted) {
//                Alert b = new Alert(Alert.AlertType.INFORMATION);
//                b.setTitle("Goal Added");
//                String s = String.format("Added A Goal For %s Hours Due On %s", newhours, sessiondb.dateTimeFormatter.format(goaldate));
//                b.setHeaderText(s);
//                b.setContentText("Goal Added!");
//                b.showAndWait();
//                sessiondb.populategoalwidget();
//                this.close();
//            }
//        }
    }

    public void viewcurrentgoals(Event event) {goals.viewcurrentgoals();}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999, 0, 1));
        GoalHoursSpinner.setEditable(true);
        GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));
        GoalMinutesSpinner.setEditable(true);
        GoalDatePicker.setValue(LocalDate.now());
    }
}
