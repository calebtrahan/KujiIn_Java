package kujiin.widgets;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import kujiin.Root;
import kujiin.Tools;
import kujiin.dialogs.DisplayCutTotalsDialog;
import kujiin.dialogs.DisplayPrematureEndingsDialog;
import kujiin.dialogs.DisplaySessionListDialog;
import kujiin.util.interfaces.Widget;
import kujiin.util.lib.GuiUtils;
import kujiin.util.xml.Session;
import kujiin.util.xml.Sessions;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;

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
        TotalTimePracticed.setOnKeyTyped(Root.noneditabletextfield);
        NumberOfSessionsPracticed.setOnKeyTyped(Root.noneditabletextfield);
        AverageSessionDuration.setOnKeyTyped(Root.noneditabletextfield);
    }

// Getters And Setters
    public Sessions getSessions() {return sessions;}


// Button Actions
    public void displaydetailedcutprogress() {
        if (sessions.getSession() != null) {new DisplayCutTotalsDialog(sessions.getSession());}
        else {GuiUtils.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (sessions.getSession() == null || sessions.getSession().size() == 0) {
            GuiUtils.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {new DisplaySessionListDialog(null, sessions.getSession()).showAndWait();}
    }
    public void displayprematureendings() {
        ArrayList<Session> prematuresessionlist = sessions.getsessionswithprematureendings();
        if (prematuresessionlist.size() > 0) {
            DisplayPrematureEndingsDialog a = new DisplayPrematureEndingsDialog(null, prematuresessionlist);
            a.showAndWait();
        } else {GuiUtils.showinformationdialog("Cannot Display", "Nothing To Display", "No Premature Endings To Display");}
    }

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
