package kujiin.ui.goals;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.AllGoals;
import kujiin.xml.Goal;
import kujiin.xml.PlaybackItemGoals;

import java.io.IOException;

public class SetNewGoalDialog extends StyledStage {
    public Label TopLabel;
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    private Goal setgoal;

    public SetNewGoalDialog(AllGoals allGoals, int playbackitemindex, String playbackitemname) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/goals/SetGoalDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            PlaybackItemGoals playbackitemgoals = allGoals.getplaybackItemGoals(playbackitemindex);
            Goal currrentgoal = playbackitemgoals.getCurrentGoal();
            Duration minduration = null;
            int initialhours = 0;
            int initialminutes = 0;
            if (currrentgoal != null) {
                minduration = currrentgoal.getDuration();
                double minutes = minduration.toMinutes();
                double hours = minutes / 60;
                minutes = minutes % 60;
                initialhours = (int) hours;
                initialminutes = (int) minutes;
            }
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, initialhours));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, initialminutes));
            Duration finalMinduration = minduration;
            HoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (finalMinduration != null) {
                    Duration newduration = Duration.hours(newValue).add(Duration.minutes(MinutesSpinner.getValue()));
                    AcceptButton.setDisable(newduration.lessThanOrEqualTo(finalMinduration));
                }
            });
            HoursSpinner.setOnScroll(event -> {
                Integer newvalue = HoursSpinner.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                HoursSpinner.getValueFactory().setValue(newvalue);
            });
            MinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (finalMinduration != null) {
                    Duration newduration = Duration.minutes(newValue).add(Duration.hours(HoursSpinner.getValue()));
                    AcceptButton.setDisable(newduration.lessThanOrEqualTo(finalMinduration));
                }
            });
            MinutesSpinner.setOnScroll(event -> {
                Integer newvalue = MinutesSpinner.getValue();
                if (event.getDeltaY() < 0) {newvalue -= 1;} else {newvalue += 1;}
                MinutesSpinner.getValueFactory().setValue(newvalue);
            });
            TopLabel.setText("Set A New Goal For " + playbackitemname);
        } catch (IOException e) {e.printStackTrace();}
    }

    public Goal getSetgoal() {
        return setgoal;
    }

    // Button Methods
    public void accept() {
        Duration goalduration = Duration.hours(HoursSpinner.getValue());
        goalduration = goalduration.add(Duration.minutes(MinutesSpinner.getValue()));
        setgoal = new Goal(goalduration);
        close();
    }

}