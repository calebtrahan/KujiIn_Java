package kujiin;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import kujiin.dialogs.ChangeAlertDialog;
import kujiin.dialogs.EditReferenceFiles;
import kujiin.dialogs.SessionAmbienceEditor;
import kujiin.util.lib.GuiUtils;
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
    public CheckBox SessionPlayerOnOffSwitch;
    public Label SessionPlayerTopLabel;
    private This_Session this_session;
    private GoalsWidget goalsWidget;
    private CreatorAndExporterWidget creatorAndExporterWidget;
    private PlayerWidget playerWidget;
    private ProgressTrackerWidget progressTrackerWidget;
    public static Double ENTRAINMENTVOLUME = 0.6;
    public static Double AMBIENCEVOLUME = 1.0;
    public static Double FADEOUTDURATION = 10.0;
    public static Double FADEINDURATION = 10.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressTrackerWidget = new ProgressTrackerWidget(TotalTimePracticed, NumberOfSessionsPracticed, AverageSessionDuration, PrePostSwitch, ShowCutProgressButton,
                ListOfSessionsButton, PrematureEndingsButton);
        this_session = new This_Session(progressTrackerWidget.getSessions(), CutProgressLabelCurrent, CutProgressLabelTotal, TotalProgressLabelCurrent, TotalProgressLabelTotal,
                CutProgressBar, TotalProgressBar, CutProgressTopLabel, TotalSessionLabel, StatusBar);
        goalsWidget = new GoalsWidget(newgoalButton, viewcurrrentgoalsButton, viewcompletedgoalsButton, goalscurrrentvalueLabel, goalssettimeLabel, goalsprogressbar, progressTrackerWidget.getSessions());
        creatorAndExporterWidget = new CreatorAndExporterWidget(EditValuesButton, ExportButton, LoadPresetButton, SavePresetButton, AmbienceEnabledTextField, TotalSessionTimeTextField, PreTime, RinTime, KyoTime,
                TohTime, ShaTime, KaiTime, JinTime, RetsuTime, ZaiTime, ZenTime, PostTime, this_session);
        playerWidget = new PlayerWidget(SessionPlayerOnOffSwitch, SessionPlayerTopLabel, VolumeButton, PlayButton, PauseButton, StopButton, CutProgressTopLabel, TotalSessionLabel, CutProgressLabelCurrent, CutProgressLabelTotal,
                TotalProgressLabelCurrent, TotalProgressLabelTotal, CutProgressBar, TotalProgressBar, ReferenceFilesOption, StatusBar, goalsWidget, this_session);
        sessionplayerswitch(null);
    }

// Top Menu Actions
    public void changealertfile(ActionEvent actionEvent) {
        ChangeAlertDialog a = new ChangeAlertDialog(null);
        a.showAndWait();
        if (a.getAlertfilechanged()) {
            GuiUtils.showtimedmessage(StatusBar, "Alert File Changed Successfully", 5000);
        } else {
            GuiUtils.showtimedmessage(StatusBar, "Changing The Alert File Failed", 5000);
        }
    }
    public void editprogramsambience(ActionEvent actionEvent) {
        SessionAmbienceEditor sae = new SessionAmbienceEditor();
        sae.showAndWait();
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        EditReferenceFiles a = new EditReferenceFiles();
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
        this_session.changesessionvalues();
        creatorAndExporterWidget.setSessionInformation(this_session);
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
    public void sessionplayerswitch(ActionEvent actionEvent) {
        playerWidget.statusSwitch();
        if (playerWidget.isEnabled()) {
            creatorAndExporterWidget.disable();
            creatorAndExporterWidget.disablebuttons();
        } else {
            creatorAndExporterWidget.enable();
            creatorAndExporterWidget.enablebuttons();
        }
    }
    public void playsession(Event event) {playerWidget.play(progressTrackerWidget.getSessions());}
    public void pausesession(Event event) {playerWidget.pause();}
    public void stopsession(Event event) {playerWidget.stop(progressTrackerWidget.getSessions());}
    public void setReferenceOption(ActionEvent actionEvent) {playerWidget.displayreferencefile();}
    public void adjustvolume(ActionEvent actionEvent) {playerWidget.adjustvolume();}

// Goals Widget
    public void setnewgoal(Event event) {goalsWidget.setnewgoal();}
    public void getgoalpacing(Event event) {goalsWidget.goalpacing();}
    public void viewcurrentgoals(Event event) {goalsWidget.displaycurrentgoals();}
    public void viewcompletedgoals(Event event) {goalsWidget.displaycompletedgoals();}

}
