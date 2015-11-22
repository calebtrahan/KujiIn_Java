package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kujiin.util.interfaces.Widget;
import kujiin.util.xml.CompletedGoals;
import kujiin.util.xml.CurrentGoals;

public class GoalsWidget implements Widget{
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private Button CompletedGoalsButton;
    private Label PracticedHours;
    private Label CurrentGoalHours;
    private ProgressBar CurrentGoalProgress;
    private CurrentGoals currentGoals;
    private CompletedGoals completedGoals;

    public GoalsWidget(Button newGoalButton, Button currentGoalsButton, Button completedGoalsButton, Label practicedHours, Label currentGoalHours,
                       ProgressBar currentGoalProgress) {
        NewGoalButton = newGoalButton;
        CurrentGoalsButton = currentGoalsButton;
        CompletedGoalsButton = completedGoalsButton;
        PracticedHours = practicedHours;
        CurrentGoalHours = currentGoalHours;
        CurrentGoalProgress = currentGoalProgress;
        currentGoals = new CurrentGoals();
        completedGoals = new CompletedGoals();
    }

// Button Actions
    public void setnewgoal() {}
    public void displaycurrentgoals() {}
    public void displaycompletedgoals() {}
    public void goalpacing() {}

// Widget Implementation
    @Override
    public void disable() {
        NewGoalButton.setDisable(true);
        CurrentGoalsButton.setDisable(true);
        CompletedGoalsButton.setDisable(true);
        PracticedHours.setDisable(true);
        CurrentGoalHours.setDisable(true);
        CurrentGoalProgress.setDisable(true);
    }
    @Override
    public void enable() {
        NewGoalButton.setDisable(false);
        CurrentGoalsButton.setDisable(false);
        CompletedGoalsButton.setDisable(false);
        PracticedHours.setDisable(false);
        CurrentGoalHours.setDisable(false);
        CurrentGoalProgress.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        PracticedHours.setText("-");
        CurrentGoalHours.setText("-");
        CurrentGoalProgress.setProgress(0.0);
    }

// Other Methods

}
