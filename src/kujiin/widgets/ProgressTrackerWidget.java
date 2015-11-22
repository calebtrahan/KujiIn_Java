package kujiin.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import kujiin.util.interfaces.Widget;

public class ProgressTrackerWidget implements Widget {
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button DetailedCutProgressButton;
    private Button SessionListButton;
    private Button PrematureEndingsButton;

    public ProgressTrackerWidget(TextField totalTimePracticed, TextField numberOfSessionsPracticed,
                                 TextField averageSessionDuration, CheckBox preAndPostOption,
                                 Button detailedCutProgressButton, Button sessionListButton,
                                 Button prematureEndingsButton) {
        TotalTimePracticed = totalTimePracticed;
        NumberOfSessionsPracticed = numberOfSessionsPracticed;
        AverageSessionDuration = averageSessionDuration;
        PreAndPostOption = preAndPostOption;
        DetailedCutProgressButton = detailedCutProgressButton;
        SessionListButton = sessionListButton;
        PrematureEndingsButton = prematureEndingsButton;
    }

// Button Actions
    public void displaydetailedcutprogress() {}
    public void displaysessionlist() {}
    public void displayprematureendings() {}
    public void toggleprepostsessionswitch() {}

// Widget Implementation
    @Override
    public void disable() {
        TotalTimePracticed.setDisable(true);
        NumberOfSessionsPracticed.setDisable(true);
        AverageSessionDuration.setDisable(true);
        PreAndPostOption.setDisable(true);
        DetailedCutProgressButton.setDisable(true);
        SessionListButton.setDisable(true);
        PrematureEndingsButton.setDisable(true);
    }
    @Override
    public void enable() {
        TotalTimePracticed.setDisable(false);
        NumberOfSessionsPracticed.setDisable(false);
        AverageSessionDuration.setDisable(false);
        PreAndPostOption.setDisable(false);
        DetailedCutProgressButton.setDisable(false);
        SessionListButton.setDisable(false);
        PrematureEndingsButton.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        TotalTimePracticed.setText("No Sessions Practiced");
        NumberOfSessionsPracticed.setText("0");
        AverageSessionDuration.setText("No Sessions Practiced");
    }

}
