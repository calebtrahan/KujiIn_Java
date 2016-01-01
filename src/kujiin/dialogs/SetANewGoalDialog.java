package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.Tools;
import kujiin.util.lib.GuiUtils;
import kujiin.util.lib.TimeUtils;

import java.io.IOException;
import java.time.LocalDate;

public class SetANewGoalDialog extends Stage {
    public Spinner<Integer> GoalHoursSpinner;
    public DatePicker GoalDatePicker;
    public Button CancelButton;
    public Button OKButton;
    public Button CurrentGoalsButton;
    public Spinner<Integer> GoalMinutesSpinner;
    public Label InformationLabel;
    private LocalDate goaldate;
    private Double goalhours;

    public SetANewGoalDialog(Double alreadypracticedhours) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
        catch (IOException e) {e.printStackTrace();}
        int hours;
        int minutes;
        if (alreadypracticedhours != 0.0) {
            InformationLabel.setText("Current Practiced Hours: " + alreadypracticedhours + " Hours");
            int totalminutes = TimeUtils.convertdecimalhourstominutes(alreadypracticedhours);
            hours = totalminutes / 60;
            minutes = totalminutes % 60;
        } else {
            InformationLabel.setText("Current Practiced Hours: 0.0 Hours");
            hours = 0;
            minutes = 0;
        }
        GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(hours, Integer.MAX_VALUE, 0, 1));
        GoalHoursSpinner.setEditable(true);
        GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minutes, 59, 0, 15));
        GoalMinutesSpinner.setEditable(true);
        GoalDatePicker.setValue(LocalDate.now());
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
    public boolean isAccepted() {return getGoalhours() != null && getGoaldate() != null;}

// Button Actions
    public void cancelgoalsetting(Event event) {this.close();}
    public void Accept(Event event) {
        if (GoalMinutesSpinner.getValue() > 59) {GuiUtils.showinformationdialog("Information", "Minutes Cannot Be Greater Than 59", "Select A Value Less Than 59"); return;}
        boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
        if (dategood) {
            int hours = GoalHoursSpinner.getValue();
            int minutes = GoalMinutesSpinner.getValue();
            double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
            setGoalhours(newhours);
            setGoaldate(GoalDatePicker.getValue());
            super.close();
        } else {
            GuiUtils.showinformationdialog("Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
            setGoalhours(null);
            setGoaldate(null);
        }
    }
    public void viewcurrentgoals(Event event) {

    }


}
