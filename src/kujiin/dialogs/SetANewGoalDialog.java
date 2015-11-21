package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.Tools;
import kujiin.util.GuiUtils;

import java.io.IOException;
import java.time.LocalDate;

public class SetANewGoalDialog extends Stage {
    public Label InformationLabel;
    public Spinner<Integer> GoalHoursSpinner;
    public DatePicker GoalDatePicker;
    public Button CancelButton;
    public Button OKButton;
    public Button CurrentGoalsButton;
    public Spinner<Integer> GoalMinutesSpinner;
    private LocalDate goaldate;
    private Double goalhours;
    private Double alreadypracticedhours;

    public SetANewGoalDialog(Parent parent, Double alreadypracticedhours) {
        this.alreadypracticedhours = alreadypracticedhours;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
        catch (IOException e) {e.printStackTrace();}
        GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(alreadypracticedhours.intValue(), Integer.MAX_VALUE, 0, 1));
        GoalHoursSpinner.setEditable(true);
        GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        GoalMinutesSpinner.setEditable(true);
        GoalDatePicker.setValue(LocalDate.now());
        InformationLabel.setText("You Have Practiced For " + alreadypracticedhours + " Hours. Please Set A New Goal");
    }

// Getters And Setters
    public LocalDate getGoaldate() {
        return goaldate;
    }
    public void setGoaldate(LocalDate goaldate) {
        this.goaldate = goaldate;
    }
    public Double getGoalhours() {
        return goalhours;
    }
    public void setGoalhours(Double goalhours) {
        this.goalhours = goalhours;
    }

// Button Actions
    public void cancelgoalsetting(Event event) {this.close();}
    public void Accept(Event event) {
        boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
        boolean goalhoursgood = GoalHoursSpinner.getValue() > alreadypracticedhours.intValue();
        if (dategood && goalhoursgood) {
            int hours = GoalHoursSpinner.getValue();
            int minutes = GoalMinutesSpinner.getValue();
            double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
            setGoalhours(newhours);
            setGoaldate(GoalDatePicker.getValue());
            super.close();
        } else {
            if (! dategood) {
                GuiUtils.showinformationdialog("Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
            } else {
                GuiUtils.showinformationdialog("Cannot Set Goal", "Cannot Set Goal", "Hours Must Be Greater Than Currently Practiced Hours (" + alreadypracticedhours + ")");
            }
            setGoalhours(null);
            setGoaldate(null);
        }
    }
    public void viewcurrentgoals(Event event) {
//        goals.viewcurrentgoals();
    }
}
