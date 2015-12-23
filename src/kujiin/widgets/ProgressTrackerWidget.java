package kujiin.widgets;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import kujiin.Tools;
import kujiin.util.interfaces.Widget;
import kujiin.util.xml.Sessions;

import javax.xml.bind.JAXBException;

public class ProgressTrackerWidget implements Widget {
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button DetailedCutProgressButton;
    private Button SessionListButton;
    private Button PrematureEndingsButton;
    private Sessions sessions;

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
        sessions = new Sessions();
        Service<Void> getsessions = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {sessions.populatefromxml();}
                        catch (JAXBException ignored) {}
                        return null;
                    }
                };
            }
        };
        getsessions.setOnRunning(event -> loadingui());
        getsessions.setOnFailed(event -> updateui());
        getsessions.setOnSucceeded(event -> updateui());
        getsessions.start();
    }

// Getters And Setters
    public Sessions getSessions() {return sessions;}


// Button Actions
    public void displaydetailedcutprogress() {}
    public void displaysessionlist() {}
    public void displayprematureendings() {}

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

// Other Methods
    public void loadingui() {
        AverageSessionDuration.setText("Loading...");
        TotalTimePracticed.setText("Loading...");
        NumberOfSessionsPracticed.setText("Loading...");
    }
    public void updateui() {
        sessions.deletenonvalidsessions();
        int averagesessionduration = (int) sessions.getaveragesessiontimeinminutes(PreAndPostOption.isSelected());
        int totalminutespracticed = sessions.getgrandtotaltimepracticedinminutes(PreAndPostOption.isSelected());
        int numberofsessionspracticed = sessions.getsessioncount();
        String nonetext = "No Sessions Practiced";
        if (averagesessionduration != 0) {AverageSessionDuration.setText(Tools.minutestoformattedhoursandmins(averagesessionduration));}
        else {AverageSessionDuration.setText(nonetext);}
        if (totalminutespracticed != 0) {TotalTimePracticed.setText(Tools.minutestoformattedhoursandmins(totalminutespracticed));}
        else {TotalTimePracticed.setText(nonetext);}
        if (numberofsessionspracticed != 0) {NumberOfSessionsPracticed.setText(Integer.toString(numberofsessionspracticed));}
        else {NumberOfSessionsPracticed.setText(nonetext);}
    }
}
