package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import kujiin.util.interfaces.Widget;
import kujiin.util.lib.TimeUtils;
import kujiin.util.xml.CompletedGoals;
import kujiin.util.xml.CurrentGoals;
import kujiin.util.xml.Sessions;

public class GoalsWidget implements Widget{
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private Button CompletedGoalsButton;
    private Label PracticedHours;
    private Label CurrentGoalHours;
    private ProgressBar CurrentGoalProgress;
    private Sessions allpracticedsessions;
    private CurrentGoals currentGoals;
    private CompletedGoals completedGoals;

    public GoalsWidget(Button newGoalButton, Button currentGoalsButton, Button completedGoalsButton, Label practicedHours, Label currentGoalHours,
                       ProgressBar currentGoalProgress, Sessions Allpracticedsessions) {
        NewGoalButton = newGoalButton;
        CurrentGoalsButton = currentGoalsButton;
        CompletedGoalsButton = completedGoalsButton;
        PracticedHours = practicedHours;
        CurrentGoalHours = currentGoalHours;
        CurrentGoalProgress = currentGoalProgress;
        allpracticedsessions = Allpracticedsessions;
        currentGoals = new CurrentGoals();
        completedGoals = new CompletedGoals();
    }

// Button Actions
    public void setnewgoal() {
        currentGoals.setnewgoal(TimeUtils.convertminutestodecimalhours(allpracticedsessions.getgrandtotaltimepracticedinminutes(false)));
    }
    public void displaycurrentgoals() {
        currentGoals.displaycurrentgoals(TimeUtils.convertminutestodecimalhours(allpracticedsessions.getgrandtotaltimepracticedinminutes(false)));
    }
    public void displaycompletedgoals() {
        completedGoals.displaycompletedgoals();
    }
    public void goalpacing() {
        currentGoals.currentgoalpacing(TimeUtils.convertminutestodecimalhours(allpracticedsessions.getgrandtotaltimepracticedinminutes(false)));
    }

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
    public void update() {
        try {
            Double practiced = TimeUtils.convertminutestodecimalhours(allpracticedsessions.getgrandtotaltimepracticedinminutes(false));
            Double goal = currentGoals.getfirstcurrentgoal().getGoal_Hours();
            PracticedHours.setText(practiced.toString());
            CurrentGoalHours.setText(goal.toString());
            CurrentGoalProgress.setProgress(practiced / goal);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void updatewhilesessionplaying(double practicedhours) {
        PracticedHours.setText(Double.toString(practicedhours));
        CurrentGoalProgress.setProgress(getpercentage(practicedhours, currentGoals.getfirstcurrentgoal().getGoal_Hours()));
    }
    public float getpercentage(double practicedhours, double goalhours) {
        return (float) practicedhours / (float) goalhours;
    }

}
