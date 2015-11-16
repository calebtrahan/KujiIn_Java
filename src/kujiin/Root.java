package kujiin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import kujiin.dialogs.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Root implements Initializable {
    // TODO Add Audio Filters To Entrainment + Ambience (They Are Fucking Up Saying They Can't Find The Codec Mp3)
    // TODO Check Ambience List Generators To Make Sure There Are No Repeats
        // Random Is Throwing java.lang.IllegalArgumentException: bound must be positive
        // Set Really High Durations, So You Can See If It Works For Really Long Sessions
    // TODO Finish Pause() And Stop()
        // Maybe Set An Enum For What The Player Is Doing To Avoid A Million If Else Statements In Pause And Play?
    // TODO Find Out Why Goals Set Only Has 1 Column
    // TODO Set Play Button To Resume Session When Paused
    // TODO Make The 'Currently Loaded Session' Widget Box Into The Session Confirmation Dialog In The Python Version

    // TODO On Startup See If Session Is Created (From Previous Run) If It Ask The User If They Want To Load This Session

    public TableView<Database.TotalProgressRow> progresstable;
    public Button CreateButton;
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
    public Label PlayercurrentcutprogressLabel;
    public Label PlayertotalsessionprogressLabel;
    public Slider EntrainmentVolumeSlider;
    public Slider AmbienceVolumeSlider;
    public TableColumn<Database.TotalProgressRow, String> NameColumn;
    public TableColumn<Database.TotalProgressRow, String> ProgressColumn;
    public Label PlayercurrentlyplayingLabelWithProgressPrefix;
    public TableColumn<Database.TotalProgressRow, Integer> NumberColumn;
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
    Session thissession;
//    Goals sessiongoals;
    Database sessiondatabase;
    Boolean readytoplay;
    CreateANewSession createsession;
    Boolean sessioncurrentlybeingcreated;
    ReferenceType referenceType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thissession = new Session(this);
//        sessiongoals = new Goals(goalsprogressbar, goalscurrrentvalueLabel, goalssettimeLabel);
//        sessiongoals.populategoalwidget();
        readytoplay = false;
        sessioncurrentlybeingcreated = false;
        StatusBar.textProperty().addListener((observable, oldValue, newValue) -> {
            new Timeline(new KeyFrame(Duration.millis(3000), ae -> StatusBar.setText(""))).play();
        });
        AmbienceEnabledTextField.setText("No Session Created");
        TotalSessionTimeTextField.setText("No Session Created");
        settextfieldvalue(PreTime, 0);
        settextfieldvalue(RinTime, 0);
        settextfieldvalue(KyoTime, 0);
        settextfieldvalue(TohTime, 0);
        settextfieldvalue(ShaTime, 0);
        settextfieldvalue(KaiTime, 0);
        settextfieldvalue(JinTime, 0);
        settextfieldvalue(RetsuTime, 0);
        settextfieldvalue(ZaiTime, 0);
        settextfieldvalue(ZenTime, 0);
        settextfieldvalue(PostTime, 0);
    }

    // <----------------------------------------- TOP MENU ACTIONS ---------------------------------------> //

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

    // <-------------------------- DATABASE AND TOTAL PROGRESS WIDGET ------------------------------------> //

    public void gettotalprogress(SortEvent<TableView<?>> tableViewSortEvent) {
//        try {
//            sessiondatabase.getdetailedprogress();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
    public void displaylistofsessions(Event event) {thissession.sessiondb.displaylistofsession();}
    public void displayprematureendings(Event event) {thissession.sessiondb.displayprematureendings();}

    // <-------------------------- CREATED SESSION DETAILS WIDGET ---------------------------------------> //

    public void getsessioninformation() {
        try {
            if (thissession.cutsinsession.size() != 0) {
                ArrayList<Integer> cuttimes = new ArrayList<>(11);
                for (String i : Session.allnames) {
                    Integer duration = 0;
                    for (Cut x : thissession.cutsinsession) {if (x.name.equals(i)) {duration = x.getdurationinminutes();}}
                    cuttimes.add(duration);
                }
                settextfieldvalue(PreTime, cuttimes.get(0));
                settextfieldvalue(RinTime, cuttimes.get(1));
                settextfieldvalue(KyoTime, cuttimes.get(2));
                settextfieldvalue(TohTime, cuttimes.get(3));
                settextfieldvalue(ShaTime, cuttimes.get(4));
                settextfieldvalue(KaiTime, cuttimes.get(5));
                settextfieldvalue(JinTime, cuttimes.get(6));
                settextfieldvalue(RetsuTime, cuttimes.get(7));
                settextfieldvalue(ZaiTime, cuttimes.get(8));
                settextfieldvalue(ZenTime, cuttimes.get(9));
                settextfieldvalue(PostTime, cuttimes.get(10));
                // TODO Get Session Information Here And Pass Into Root Boxes PRE-POSTTime
            }
            // Set Ambience Enabled And Session Total Time
            if (thissession.getAmbienceenabled()) {AmbienceEnabledTextField.setText("Yes");}
            else {AmbienceEnabledTextField.setText("No");}
            TotalSessionTimeTextField.setText(thissession.gettotalsessionduration());
            readytoplay = true;
        } catch (ArrayIndexOutOfBoundsException e) {
            readytoplay = false;
        }
    }
    public void createsession(Event event) {
        if (! sessioncurrentlybeingcreated) {
            sessioncurrentlybeingcreated = true;
            if (thissession.getCreated()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Session Validation");
                alert.setHeaderText("Session Is Already Created");
                alert.setContentText("Overwrite Previous Session?");
                Optional<ButtonType> result = alert.showAndWait();
                if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                    Session.deleteprevioussession();
                } else {
                    sessioncurrentlybeingcreated = false;
                    return;
                }
            }
            createsession = new CreateANewSession(null, thissession);
            createsession.showAndWait();
            sessioncurrentlybeingcreated = false;
            if (thissession.getCreated()) {
                getsessioninformation();
            }
        } else {
            StatusBar.setText("Session Already Being Created");
        }
    }
    public void exportsession(Event event) {thissession.export();}
    public void settextfieldvalue(TextField textField, Integer value) {
        if (value > 0) {textField.setDisable(false); textField.setText(Integer.toString(value));}
        else {textField.setText("-"); textField.setDisable(true);}
    }

    // <----------------------------------- SESSION PLAYER WIDGET ---------------------------------------> //

    public void playsession(Event event) {
        if (thissession.getCreated()) {
            try {
                StatusBar.setText("Session Playing...");
                thissession.play();
            } catch (NullPointerException e) {
                StatusBar.setText("No Session Created. Create A Session First");
            }
        } else {
            StatusBar.setText("No Session Created");
        }
    }
    public void pausesession(Event event) {
        try {
            thissession.pause();
        } catch (NullPointerException e) {
            StatusBar.setText("No Session Playing");
        }
    }
    public void stopsession(Event event) {
        try {
            thissession.stop();
        } catch (NullPointerException e) {
            StatusBar.setText("No Session Playing");
        }
    }
    public void setReferenceOption(ActionEvent actionEvent) {
        ReferenceTypeDialog reftype = new ReferenceTypeDialog(null, referenceType);
        reftype.showAndWait();
        referenceType = reftype.getReferenceType();
    }

    // <-----------------------------------   GOALS WIDGET   --------------------------------------------> //

    public void setnewgoal(Event event) {thissession.sessiondb.goals.setnewgoal();}
    public void getgoalpacing(Event event) {thissession.sessiondb.goals.getgoalpacing();}
    public void viewcurrentgoals(Event event) {thissession.sessiondb.goals.viewcurrentgoals();}
    public void viewcompletedgoals(Event event) {thissession.sessiondb.goals.viewcompletedgoals();}

}
