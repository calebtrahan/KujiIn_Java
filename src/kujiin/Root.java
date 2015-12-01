package kujiin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.dialogs.AddAmbienceDialog;
import kujiin.dialogs.ChangeAlertDialog;
import kujiin.dialogs.EditAmbienceDialog;
import kujiin.dialogs.EditReferenceFiles;
import kujiin.util.xml.Session;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.GoalsWidget;
import kujiin.widgets.PlayerWidget;
import kujiin.widgets.ProgressTrackerWidget;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Root implements Initializable {
    // TODO Add Audio Filters To Entrainment + Ambience (They Are Fucking Up Saying They Can't Find The Codec Mp3)
    // TODO Check Ambience List Generators To Make Sure There Are No Repeats
        // Random Is Throwing java.lang.IllegalArgumentException: bound must be positive
        // Set Really High Durations, So You Can See If It Works For Really Long Sessions
    // TODO Finish Pause() And Stop()
        // Maybe Set An Enum For What The Player Is Doing To Avoid A Million If Else Statements In Pause And Play?
    // TODO Find Out Why Goals Set Only Has 1 Column
    // TODO Set Play Button To Resume This_Session When Paused
    // TODO Make The 'Currently Loaded This_Session' Widget Box Into The This_Session Confirmation Dialog In The Python Version
    // TODO On Startup See If This_Session Is Created (From Previous Run) If It Ask The User If They Want To Load This This_Session


    public Button EditValuesButton;
    public Label StatusBar;
    public Button ExportButton;
    public Button PlayButton;
    public Button ListOfSessionsButton;
    public Button PrematureEndingsButton;
    public CheckBox ReferenceFilesOption;
    public ProgressBar goalsprogressbar;
    public Label goalscurrrentvalueLabel;
    public Label goalssettimeLabel;
    public Button newgoalButton;
    public Button goalpacingButton;
    public Button viewcurrrentgoalsButton;
    public Button viewcompletedgoalsButton;
    public Button PauseButton;
    public Button StopButton;
    public TextField AmbienceEnabledTextField;
    public TextField TotalSessionTimeTextField;
    public TextField PreTime;
    public TextField RinTime;
    public TextField KyoTime;
    public TextField TohTime;
    public TextField ShaTime;
    public TextField KaiTime;
    public TextField JinTime;
    public TextField RetsuTime;
    public TextField ZaiTime;
    public TextField ZenTime;
    public TextField PostTime;
    public ProgressBar CutProgressBar;
    public ProgressBar TotalProgressBar;
    public Label CutProgressLabelCurrent;
    public Label TotalProgressLabelCurrent;
    public Label CutProgressLabelTotal;
    public Label TotalProgressLabelTotal;
    public Label CutProgressTopLabel;
    public Button VolumeButton;
    public Label TotalSessionLabel;
    public Button ShowCutProgressButton;
    public TextField AverageSessionDuration;
    public TextField TotalTimePracticed;
    public TextField NumberOfSessionsPracticed;
    public CheckBox PrePostSwitch;
    public Button LoadPresetButton;
    public Button SavePresetButton;
    private This_Session this_session;
    public ChangeListener statusbarautoofflistener;
    private GoalsWidget goalsWidget;
    private CreatorAndExporterWidget creatorAndExporterWidget;
    private PlayerWidget playerWidget;
    private ProgressTrackerWidget progressTrackerWidget;
    public static final double ENTRAINMENTVOLUME = 0.5;
    public static final double AMBIENCEVOLUME = 1.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressTrackerWidget = new ProgressTrackerWidget(TotalTimePracticed, NumberOfSessionsPracticed, AverageSessionDuration, PrePostSwitch, ShowCutProgressButton,
                ListOfSessionsButton, PrematureEndingsButton);
        this_session = new This_Session(progressTrackerWidget.getSessions(), CutProgressLabelCurrent, CutProgressLabelTotal, TotalProgressLabelCurrent, TotalProgressLabelTotal,
                CutProgressBar, TotalProgressBar, CutProgressTopLabel, TotalSessionLabel, StatusBar);
        statusbarautoofflistener = (observable, oldValue, newValue) -> new Timeline(new KeyFrame(Duration.millis(3000), ae -> StatusBar.setText(""))).play();
        StatusBar.textProperty().addListener(statusbarautoofflistener);
        goalsWidget = new GoalsWidget(newgoalButton, viewcurrrentgoalsButton, viewcompletedgoalsButton, goalscurrrentvalueLabel, goalssettimeLabel, goalsprogressbar);
        creatorAndExporterWidget = new CreatorAndExporterWidget(EditValuesButton, ExportButton, AmbienceEnabledTextField, TotalSessionTimeTextField, PreTime, RinTime, KyoTime,
                TohTime, ShaTime, KaiTime, JinTime, RetsuTime, ZaiTime, ZenTime, PostTime, this_session);
        playerWidget = new PlayerWidget(VolumeButton, PlayButton, PauseButton, StopButton, CutProgressTopLabel, TotalSessionLabel, CutProgressLabelCurrent, CutProgressLabelTotal,
                TotalProgressLabelCurrent, TotalProgressLabelTotal, CutProgressBar, TotalProgressBar, ReferenceFilesOption, StatusBar, goalsWidget);
    }

// Top Menu Actions
    public void changealertfile(ActionEvent actionEvent) {
        ChangeAlertDialog a = new ChangeAlertDialog(null);
        a.showAndWait();
        if (a.getAlertfilechanged()) {
            StatusBar.setText("Alert File Changed Successfully");
        } else {
            StatusBar.setText("Changing The Alert File Failed");
        }
    }
    public void addambiencetoprogram(ActionEvent actionEvent) {
        AddAmbienceDialog a = new AddAmbienceDialog(null);
        a.showAndWait();
    }
    public void editprogramsambience(ActionEvent actionEvent) {
        EditAmbienceDialog a = new EditAmbienceDialog(null);
        a.showAndWait();
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        EditReferenceFiles a = new EditReferenceFiles(null);
        a.showAndWait();
    }
    public void howtouseprogram(ActionEvent actionEvent) {Tools.howtouseprogram();}
    public void aboutthisprogram(ActionEvent actionEvent) {Tools.aboutthisprogram();}
    public void contactme(ActionEvent actionEvent) {Tools.contactme();}

// Database And Total Progress Widget
    public void updatetotalprogresswidget(ActionEvent actionEvent) {progressTrackerWidget.updateui();}
    public void displaylistofsessions(Event event) {progressTrackerWidget.getSessions().displaylistofsessions();}
    public void displayprematureendings(Event event) {progressTrackerWidget.getSessions().displayprematureendings();}
    public void showcutprogress(Event event) {progressTrackerWidget.getSessions().displaycutprogress();}

// Created Session Widget
    public boolean getsessioninformation() {
        try {
            boolean cutsinsession = this_session.getCutsinsession().size() != 0;
            if (cutsinsession) {
                Session session = new Session();
                ArrayList<Integer> cuttimes = new ArrayList<>(11);
                for (String i : This_Session.allnames) {
                    Integer duration = 0;
                    for (Cut x : this_session.getCutsinsession()) {if (x.name.equals(i)) {duration = x.getdurationinminutes();}}
                    cuttimes.add(duration);
                }
                session.updatecutduration(0, cuttimes.get(0));
                settextfieldvalue(PreTime, cuttimes.get(0));
                session.updatecutduration(1, cuttimes.get(1));
                settextfieldvalue(RinTime, cuttimes.get(1));
                session.updatecutduration(2, cuttimes.get(2));
                settextfieldvalue(KyoTime, cuttimes.get(2));
                session.updatecutduration(3, cuttimes.get(3));
                settextfieldvalue(TohTime, cuttimes.get(3));
                session.updatecutduration(4, cuttimes.get(4));
                settextfieldvalue(ShaTime, cuttimes.get(4));
                session.updatecutduration(5, cuttimes.get(5));
                settextfieldvalue(KaiTime, cuttimes.get(5));
                session.updatecutduration(6, cuttimes.get(6));
                settextfieldvalue(JinTime, cuttimes.get(6));
                session.updatecutduration(7, cuttimes.get(7));
                settextfieldvalue(RetsuTime, cuttimes.get(7));
                session.updatecutduration(8, cuttimes.get(8));
                settextfieldvalue(ZaiTime, cuttimes.get(8));
                session.updatecutduration(9, cuttimes.get(9));
                settextfieldvalue(ZenTime, cuttimes.get(9));
                session.updatecutduration(10, cuttimes.get(10));
                settextfieldvalue(PostTime, cuttimes.get(10));
                // TODO Get This_Session Information Here And Pass Into Root Boxes PRE-POSTTime
            }
            // Set Ambience Enabled And This_Session Total Time
            if (this_session.getAmbienceenabled()) {AmbienceEnabledTextField.setText("Yes"); AmbienceEnabledTextField.setDisable(false);}
            else {AmbienceEnabledTextField.setText("No"); AmbienceEnabledTextField.setDisable(true);}
            TotalSessionTimeTextField.setText(this_session.gettotalsessionduration());
            TotalSessionTimeTextField.setDisable(! cutsinsession);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    public void loadpreset(ActionEvent actionEvent) {
    }
    public void savepreset(ActionEvent actionEvent) {

    }
    public void editsessionvalues(ActionEvent actionEvent) {

    }
    public void createsession(Event event) {

//        if (creatorState == CreatorState.NOT_CREATED || creatorState == CreatorState.CREATED) {
//            if (creatorState == CreatorState.CREATED) {
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("This_Session Validation");
//                alert.setHeaderText("This_Session Is Already Created");
//                alert.setContentText("Overwrite Previous This_Session?");
//                Optional<ButtonType> result = alert.showAndWait();
//                if ((result.isPresent()) && (result.get() != ButtonType.OK)) {
//                    creatorState = CreatorState.NOT_CREATED; return;
//                }
//            }
//            creatorState = CreatorState.CREATION_IN_PROGRESS;
//            ChangeSessionValues createsession = new ChangeSessionValues(this_session);
//            createsession.showAndWait();
//            if (getsessioninformation()) {creatorState = CreatorState.CREATED;}
//            else {
//                // TODO Show Creation Failed Alert Here
//                creatorState = CreatorState.NOT_CREATED;
//            }
//        } else {
//            StatusBar.setText("Session Creation In Progress");
//        }
    }
    public void exportsession(Event event) {this_session.export();}
    public void settextfieldvalue(TextField textField, Integer value) {
        if (value > 0) {textField.setDisable(false); textField.setText(Integer.toString(value));}
        else {textField.setText("-"); textField.setDisable(true);}
    }

// Session Player Widget
    public void playsession(Event event) {
        if (this_session != null) {
            StatusBar.setText("Starting Session Playback...");
            this_session.play();
        } else {
            StatusBar.setText("No Session Created");
        }
    }
    public void pausesession(Event event) {
        try {
            this_session.pause();
        } catch (NullPointerException e) {
            StatusBar.setText("No This_Session Playing");
        }
    }
    public void stopsession(Event event) {
        try {
            this_session.stop();
        } catch (NullPointerException e) {
            StatusBar.setText("No This_Session Playing");
        }
    }
    public void setReferenceOption(ActionEvent actionEvent) {playerWidget.setreferencetype();}
    public void adjustvolume(ActionEvent actionEvent) {playerWidget.adjustvolume();}

// Goals Widget
    public void setnewgoal(Event event) {

    }
    public void getgoalpacing(Event event) {
//        this_session.sessiondb.goals.getgoalpacing();
    }
    public void viewcurrentgoals(Event event) {
//        this_session.sessiondb.goals.viewcurrentgoals();
    }
    public void viewcompletedgoals(Event event) {
//        this_session.sessiondb.goals.viewcompletedgoals();
    }


}
