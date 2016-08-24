package kujiin;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.util.*;
import kujiin.xml.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
// Bugs
// TODO Preferences Dialog Doesn't Initially Populate With Options From XML (Check If It Saves As Well?)

// Refactor
// TODO Refactor Player
    // Use Animations To Fire Off Ramp In And Ramp Out
    // Repeat Entrainment File Indefinitely In Between Ramps (Use Animations To Terminate Entrainment And Start Ramp Or New Session)
    // Use 5 Minute Entrainment Freq Files And Kill 1 Minute Entrainment Freq Files
    // Maybe Use Random To Pull Next Random Ambience File Directly During Playback Instead Of Creating A List Beforehand
// TODO Place Ramps In Entrainment Duration (Don't Add On Like In Existing Qi-Gong)

// TODO Dialog Before Session Playback If Actual Ambience < Practiced Duration
    // Ambience Is Not Long Enough Select How You Would Like Me To Play Ambience
    // (Shuffle)    (Repeat, Back To Back In Order) {These Are Buttons}

// Mind Workstation
// TODO (MWS) ReCreate Ramps To 30 Seconds Across The Board (To Save Disk Space)
// TODO (MWS) ReCreate Ramps For All Cuts Connecting All Cuts And Elements

// Additional Features
// TODO Confirmation -> Alert File On LONG Sessions (Deep In Trance)
// TODO Design A 'Select Your Own Ambience' Wizard As An Alternative To Randomized Ambience During Session Creation
// TODO Redesign Goals Completed Dialog Using Bar Charts/Graphs
// TODO If Ramp Disabled (And No Pre/PostSession Set) Ask User If They Want TO Add A Ramp Into 1st Practiced Cut (2/3/5) Min, Then Update UI And Create Session
// TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)
// TODO Select Button On Options -> ChangeAlertFileDialog Instead Of Just A File Chooser
// TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog

// Optional Features
// TODO Set Font Size, So The Program Looks Universal And Text Isn't Oversized Cross-Platform
// TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
// TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
// TODO Fix Set Multiple Goal Minutes (And Add Check If Long Enough Logic On Accepting)

public class MainController implements Initializable {
    public Label CreatorStatusBar;
    public Label PlayerStatusBar;
    public Button ExportButton;
    public Button PlayButton;
    public Button ListOfSessionsButton;
    public ProgressBar goalsprogressbar;
    public Button newgoalButton;
    public Button viewcurrrentgoalsButton;
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
    public TextField AverageSessionDuration;
    public TextField TotalTimePracticed;
    public TextField NumberOfSessionsPracticed;
    public CheckBox PrePostSwitch;
    public Button LoadPresetButton;
    public Button SavePresetButton;
    public CheckBox AmbienceSwitch;
    public TextField ApproximateEndTime;
    public Button ChangeAllCutsButton;
    public TextField TotalSessionTime;
    public ComboBox<String> GoalMeditatableComboBox;
    public Label GoalTopLabel;
    public Label LengthLabel;
    public Label CompletionLabel;
    public Label GoalStatusBar;
    public ToggleButton RinSwitch;
    public ToggleButton KyoSwitch;
    public ToggleButton TohSwitch;
    public ToggleButton ShaSwitch;
    public ToggleButton KaiSwitch;
    public ToggleButton JinSwitch;
    public ToggleButton RetsuSwitch;
    public ToggleButton ZaiSwitch;
    public ToggleButton ZenSwitch;
    public ToggleButton EarthSwitch;
    public ToggleButton AirSwitch;
    public ToggleButton FireSwitch;
    public ToggleButton WaterSwitch;
    public ToggleButton VoidSwitch;
    public TextField EarthTime;
    public TextField AirTime;
    public TextField FireTime;
    public TextField WaterTime;
    public TextField VoidTime;
    public ToggleButton PreSwitch;
    public ToggleButton PostSwitch;
    public Button ChangeAllElementsButton;
    public Button ResetCreatorButton;
    public Label GoalProgressPercentageLabel;
    private Scene Scene;
    private Stage Stage;

// My Fields
    private This_Session Session;
    private Preset Preset;
    private Timeline creator_updateuitimeline;
    private Sessions Sessions;
    private Meditatable SessionsAndGoalsSelectedMeditatable;
    private Goals Goals;
    private Options Options;
    private Entrainments Entrainments;
    private Ambiences Ambiences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
    }
    public boolean cleanup() {
        System.out.println("Cleaning Up Program");
        Ambiences.marshall();
        Entrainments.marshall();
        Options.marshall();
        return creation_cleanup() && exporter_cleanup() && sessions_cleanup() && goals_cleanup();
    }
    public void close(ActionEvent actionEvent) {
        if (cleanup()) {System.exit(0);}
    }

// Getters And Setters
    public Entrainments getEntrainments() {
    return Entrainments;
}
    public void setEntrainments(kujiin.xml.Entrainments entrainments) {
        Entrainments = entrainments;
    }
    public void setAmbiences(kujiin.xml.Ambiences ambiences) {
        Ambiences = ambiences;
    }
    public Ambiences getAmbiences() {
        return Ambiences;
    }
    public This_Session getSession() {
        return Session;
    }
    public void setSession(This_Session session) {
        this.Session = session;
    }
    public Options getOptions() {
        return Options;
    }
    public void setOptions(Options options) {
        Options = options;
    }
    public kujiin.xml.Sessions getSessions() {
        return Sessions;
    }
    public kujiin.xml.Goals getGoals() {
        return Goals;
    }
    public javafx.scene.Scene getScene() {
        return Scene;
    }
    public void setScene(javafx.scene.Scene scene) {
        Scene = scene;
    }
    public javafx.stage.Stage getStage() {
        return Stage;
    }
    public void setStage(javafx.stage.Stage stage) {
        Stage = stage;
    }

// Top Menu Actions
    public void menu_changesessionoptions(ActionEvent actionEvent) {
        new ChangeProgramOptions().showAndWait();
        Options.marshall();
        sessions_gui_updateui();
        goals_gui_updateui();
    }
    public void menu_editprogramsambience(ActionEvent actionEvent) {
        getStage().setIconified(true);
        new SimpleAmbienceEditor().showAndWait();
        getStage().setIconified(false);
    }
    public void menu_opensimpleambienceeditor() {
        new SimpleAmbienceEditor().showAndWait();
    }
    public void menu_opensimpleambienceeditor(Meditatable meditatable) {
        new SimpleAmbienceEditor(meditatable).showAndWait();
    }
    public void menu_openadvancedambienceeditor() {}
    public void menu_openadvancedambienceeditor(Meditatable meditatable) {
        AdvancedAmbienceEditor sae = new AdvancedAmbienceEditor(meditatable);
        sae.showAndWait();
    }
    public void menu_editreferencefiles(ActionEvent actionEvent) {
        getStage().setIconified(true);
        new EditReferenceFiles(getSession().referenceType).showAndWait();
        getStage().setIconified(false);
    }
    public void menu_howtouseprogram(ActionEvent actionEvent) {
        Util.menu_howtouse(this);
    }
    public void menu_aboutthisprogram(ActionEvent actionEvent) {
        List<Integer> numbers = new ArrayList<>();
        numbers.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        System.out.println(numbers);
        numbers.set(2, 10);
        System.out.println(numbers);
//        Util.menu_aboutthisprogram();
    }
    public void menu_contactme(ActionEvent actionEvent) {
        Util.menu_contactme();}

// Presets
    public void preset_initialize() {Preset = new Preset(this);}
    public void preset_load(ActionEvent actionEvent) {
        File presetfile = Preset.open();
        if (presetfile != null && Preset.hasvalidValues()) {
            preset_changecreationvaluestopreset(Preset.gettimes());
        } else {if (presetfile != null) dialog_Information("Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void preset_save(ActionEvent actionEvent) {
        ArrayList<Double> creatorvaluesinminutes = new ArrayList<>();
        boolean validsession = false;
        for (Meditatable i : getSession().getAllMeditatables()) {
            creatorvaluesinminutes.add(i.getduration().toMinutes());
            if (i instanceof Qi_Gong) {if (((Qi_Gong) i).getdurationwithoutramp().greaterThan(Duration.ZERO)) {validsession = true;}}
            else {if (i.getduration().greaterThan(Duration.ZERO)) {validsession = true;}}
        }
        if (validsession) {
            Preset.settimes(creatorvaluesinminutes);
            if (Preset.save()) {Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Preset Successfully Saved", 1500);}
            else {
                dialog_Error("Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
        }
        else {
            dialog_Information("Information", "Cannot Save Preset", "All Values Are 0");}
    }
    public void preset_changecreationvaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < getSession().getAllMeditatables().size(); i++) {
                getSession().getAllMeditatables().get(i).changevalue(presetvalues.get(i).intValue());
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            dialog_Error("Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }

// Creation
    // GUI
    public void creation_initialize() {
        creator_updateuitimeline = new Timeline(new KeyFrame(Duration.seconds(10), ae -> creation_gui_update()));
        creator_updateuitimeline.setCycleCount(Animation.INDEFINITE);
        if (getOptions().getProgramOptions().getTooltips()) {
            TotalSessionTime.setTooltip(new Tooltip("Total Session Time (Not Including Presession + Postsession Ramp, And Alert File)"));
            ApproximateEndTime.setTooltip(new Tooltip("Approximate Finish Time For This Session (Assuming You Start Now)"));
            AmbienceSwitch.setTooltip(new Tooltip("Check This After You Set All Values To Check For And Enable Ambience For This Session"));
            ChangeAllCutsButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
            ChangeAllElementsButton.setTooltip(new Tooltip("Change All Element Values Simultaneously"));
            LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
            SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
            ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
        } else {
            TotalSessionTime.setTooltip(null);
            ApproximateEndTime.setTooltip(null);
            AmbienceSwitch.setTooltip(null);
            ChangeAllCutsButton.setTooltip(null);
            ChangeAllElementsButton.setTooltip(null);
            LoadPresetButton.setTooltip(null);
            SavePresetButton.setTooltip(null);
            ExportButton.setTooltip(null);
        }
    }
    public void creation_gui_setDisable(boolean disabled) {
        ChangeAllCutsButton.setDisable(disabled);
        ChangeAllElementsButton.setDisable(disabled);
        LoadPresetButton.setDisable(disabled);
        SavePresetButton.setDisable(disabled);
        AmbienceSwitch.setDisable(disabled);
        ApproximateEndTime.setDisable(disabled);
        TotalSessionTime.setDisable(disabled);
        AmbienceSwitch.setDisable(disabled);
        PlayButton.setDisable(disabled);
        ExportButton.setDisable(disabled);
        ResetCreatorButton.setDisable(disabled);
        for (Meditatable i : getSession().getAllMeditatables()) {i.gui_setDisable(disabled);}
        if (disabled) {
            creator_updateuitimeline.stop();
            CreatorStatusBar.setText("Creator Disabled While Session Player Open");
        } else {
            creator_updateuitimeline.play();
            CreatorStatusBar.setText("");
        }
    }
    public void creation_gui_update() {
        boolean notallzero = false;
        try {for (Integer i : getSession().gui_getallsessionvalues()) {if (i > 0) {notallzero = true;}}} catch (NullPointerException ignored) {}
        if (notallzero) {
            Duration totalsessiontime = Duration.ZERO;
            for (Meditatable i : getSession().getAllMeditatables()) {totalsessiontime = totalsessiontime.add(i.getduration());}
            if (getOptions().getSessionOptions().getRampenabled() && getOptions().getProgramOptions().getTooltips()) {
                TotalSessionTime.setTooltip(new Tooltip("Duration Includes A Ramp Of " + getOptions().getSessionOptions().getRampduration() + "Mins. On Both Presession And Postsession"));}
            else {TotalSessionTime.setTooltip(null);}
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(totalsessiontime, TotalSessionTime.getLayoutBounds().getWidth()));
            if (getOptions().getProgramOptions().getTooltips()) {ApproximateEndTime.setTooltip(new Tooltip("Time You Finish Will Vary Depending On When You Start Playback"));}
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, new Double(totalsessiontime.toMillis()).intValue());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        } else {
            TotalSessionTime.setText("-");
            ApproximateEndTime.setText("-");
        }
    }
    public void creation_gui_toggleambience(ActionEvent actionEvent) {
        if (AmbienceSwitch.isSelected()) {
            if (creation_gui_allvaluesnotzero()) {
                for (Meditatable i : Session.getAllMeditatables()) {
                    if (i.gui_getvalue() > 0 && ! i.ambience_isReady()) {
                        dialog_Information("Cannot Add Ambience", "Still Background Checking Existing Ambience", "Please Try Again In A Few Moments");
                        Session.creation_reset(false);
                        AmbienceSwitch.setSelected(false);
                        return;
                    }
                }
                Session.creation_checkambience(AmbienceSwitch);
            } else {
                dialog_Information("Cannot Add Ambience", "All Durations Are Zero", "Nothing To Add Ambience For");
                AmbienceSwitch.setSelected(false);
            }
        } else {Session.creation_reset(false);}
    }
    public void creation_gui_resetallvalues(ActionEvent actionEvent) {
        Session.creation_reset(true);
    }
    public void creation_gui_changeallvalues_cuts(ActionEvent actionEvent) {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog("Change All Cut Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Cut i : Session.getallCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {Session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {Session.getPostsession().changevalue(min);}
        }
    }
    public void creation_gui_changeallvalues_elements(ActionEvent actionEvent) {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog("Change All Element Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Element i : Session.getallElements()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {Session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {Session.getPostsession().changevalue(min);}
        }
    }
    public boolean creation_gui_allvaluesnotzero() {
        for (Meditatable i : Session.getAllMeditatables()) {
            if (i.hasValidValue()) {return true;}
        }
        return false;
    }
    // Utility
    public boolean creation_util_isLongSession() {
        for (Integer i : Session.gui_getallsessionvalues()) {
            if (i >= kujiin.xml.Options.DEFAULT_LONG_MEDITATABLE_DURATION) {return true;}
        }
        return false;
    }
    public void creation_util_createsession() {
        for (Meditatable i : Session.getAllMeditatables()) {
            if (! i.ambience_isReady() || ! i.entrainment_isReady()) {
                System.out.println(i.name + " Isn't Ready");
                dialog_Information("Information", "Cannot Play Session Yet, Still Performing Background Checks For Entrainment/Ambience", "Please Try Again In A Few Moments");
                return;
            }
        }
        // TODO Check Exporter Here
        if (! creation_gui_allvaluesnotzero()) {
            dialog_Error("Error Creating Session", "At Least One Meditatable's Value Must Not Be 0", "Cannot Create Session");
            getSession().creatorState = This_Session.CreatorState.NOT_CREATED;
            return;
        }
        if (creation_util_isLongSession() && ! getOptions().getSessionOptions().getAlertfunction()) {
            if (dialog_YesNoConfirmation("Add Alert File", "I've Detected A Long Session. Long Sessions Can Make It Difficult To Hear " +
                    "The Subtle Transitions In Between Session Parts", "Add Alert File In Between Session Parts?")) {
                new ChangeAlertFile().showAndWait();
            }
        } else if (getOptions().getSessionOptions().getAlertfunction()) {
            if (dialog_YesNoConfirmation("Disable Alert File", "I've Detected A Relatively Short Session, And An Alert File Might Not Be Necessary",
                    "Turn Off Alert File Between Session Parts?")) {getOptions().getSessionOptions().setAlertfunction(false);}
        }
        Session.creation_createsession();
        creation_gui_setDisable(Session.creatorState != This_Session.CreatorState.NOT_CREATED);
    }
    public void creation_util_resetcreatedsession() {}
    public boolean creation_cleanup() {return true;}

// Export
    public void exporter_initialize() {}
    public void exporter_toggle(ActionEvent actionEvent) {
        switch (Session.exporterState) {
            case NOT_EXPORTED:
                break;
            case WORKING:
                break;
            case FAILED:
                break;
            case COMPLETED:
                break;
            case CANCELLED:
                break;
            default:
                break;
        }
    }
    public void exporter_exportsession(Event event) {
        //        CreatorAndExporter.startexport();}
        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
        //        if (creationchecks()) {
//            if (getExporterState() == ExporterState.NOT_EXPORTED) {
//                if (checkforffmpeg()) {
//                    if (session.exportfile() == null) {
//                        session.exporter_getnewexportsavefile();
//                    } else {
//                        if (session.getExportfile().exists()) {
//                            if (!Util.dialog_YesNoConfirmation(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
//                                session.exporter_getnewexportsavefile();
//                            }
//                        } else {session.exporter_getnewexportsavefile();}
//                    }
//                    if (session.getExportfile() == null) {Util.gui_showtimedmessageonlabel(StatusBar, "Export Session Cancelled", 3000); return;}
//                    exportserviceindex = 0;
//                    ArrayList<Cut> cutsinsession = session.getCutsinsession();
//                    for (Cut i : cutsinsession) {
//                        exportservices.add(i.getexportservice());
//                    }
//                    exportservices.add(session.exporter_getsessionexporter());
//                    exporterUI = new ExporterUI(Root);
//                    exporterUI.show();
//                    setExporterState(ExporterState.WORKING);
//                    exporter_util_movetonextexportservice();
//                } else {
//                    Util.dialog_Error(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
//                    // TODO Open A Browser Showing How To Install FFMPEG
//                }
//            } else if (getExporterState() == ExporterState.WORKING) {
//                Util.gui_showtimedmessageonlabel(StatusBar, "Session Currently Being Exported", 3000);
//            } else {
//                if (Util.dialog_YesNoConfirmation(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
//                    setExporterState(ExporterState.NOT_EXPORTED);
//                    startexport();
//                }
//            }
//        } else {Util.dialog_Information(Root, "Information", "Cannot Export", "No Cuts Selected");}
    }
    private void exporter_util_movetonextexportservice() {
//        System.out.println("Starting Next Export Service");
//        exporterUI.TotalProgress.setProgress((double) exportserviceindex / exportservices.size());
//        try {
//            currentexporterservice = exportservices.get(exportserviceindex);
//            currentexporterservice.setOnRunning(event -> {
//                exporterUI.CurrentProgress.progressProperty().bind(currentexporterservice.progressProperty());
//                exporterUI.StatusBar.textProperty().bind(currentexporterservice.messageProperty());
//                exporterUI.CurrentLabel.textProperty().bind(currentexporterservice.titleProperty());
//            });
//            currentexporterservice.setOnSucceeded(event -> {
//                exporterUI.unbindproperties(); exportserviceindex++; exporter_util_movetonextexportservice();});
//            currentexporterservice.setOnCancelled(event -> exporter_export_cancelled());
//            currentexporterservice.setOnFailed(event -> exporter_export_failed());
//            currentexporterservice.start();
//        } catch (ArrayIndexOutOfBoundsException ignored) {
//            exporter_export_finished();}
    }
    public void exporter_export_finished() {
//        System.out.println("Export Finished!");
//        exporterState = ExporterState.COMPLETED;
    }
    public void exporter_export_cancelled() {
//        System.out.println("Cancelled!");
//        exporterState = ExporterState.CANCELLED;}
    }
    public void exporter_export_failed() {
//        System.out.println(currentexporterservice.getException().getMessage());
//        System.out.println("Failed!");
//        exporterState = ExporterState.FAILED;
    }
    public boolean exporter_cleanup() {
//        boolean currentlyexporting = exporterState == ExporterState.WORKING;
//        if (currentlyexporting) {
//            dialog_Information(this, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
//        } else {This_Session.exporter_deleteprevioussession();}
//        return ! currentlyexporting;
        return true;
    }

// Sessions And Goals
    public void sessionsandgoals_meditatableselectionchanged(ActionEvent actionEvent) {
        try {
            int index = GoalMeditatableComboBox.getSelectionModel().getSelectedIndex();
            SessionsAndGoalsSelectedMeditatable = getSession().getAllMeditatablesincludingTotal().get(index);
            if (SessionsAndGoalsSelectedMeditatable instanceof Total) {PrePostSwitch.setSelected(true);}
            sessions_gui_updateui();
            goals_gui_updateui();
        } catch (NullPointerException ignored) {
            sessions_gui_resetallvalues();
            goals_gui_resetallvalues();
        }
    }
    public void sessionandgoals_forceselectmeditatable(int meditatableindex) {
        GoalMeditatableComboBox.getSelectionModel().select(meditatableindex);
    }
    public void sessionandgoals_forceselectmeditatable(Meditatable meditatable) {
        GoalMeditatableComboBox.getSelectionModel().select(meditatable.number);
    }

    // Sessions
        // GUI
    public void sessions_initialize() {
        Sessions = new Sessions(this);
        Sessions.unmarshall();
        GoalMeditatableComboBox.setItems(FXCollections.observableArrayList(getSession().getAllMeditablesincludingTotal_Names()));
        sessions_gui_updateui();
    }
    public void sessions_gui_updateui() {
        String averagesessiondurationtext;
        String totalminutespracticedtext;
        String numberofsessionspracticedtext;
        boolean disabled;
        int selectionindex = GoalMeditatableComboBox.getSelectionModel().getSelectedIndex();
        if (selectionindex == -1 || SessionsAndGoalsSelectedMeditatable == null) {
            averagesessiondurationtext = "No Sessions";
            totalminutespracticedtext = "No Sessions";
            numberofsessionspracticedtext = "No Sessions";
            disabled = true;
        } else {
            Double averagesessionduration = SessionsAndGoalsSelectedMeditatable.sessions_getAveragePracticeTime(PrePostSwitch.isSelected());
            Integer totalminutespracticed = SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(PrePostSwitch.isSelected());
            Integer numberofsessionspracticed = SessionsAndGoalsSelectedMeditatable.sessions_getNumberOfSessionsPracticed(PrePostSwitch.isSelected());
            if (numberofsessionspracticed > 0) {
                averagesessiondurationtext = Util.formatdurationtoStringSpelledOut(new Duration(averagesessionduration * 1000), AverageSessionDuration.getLayoutBounds().getWidth());
                totalminutespracticedtext = Util.formatdurationtoStringSpelledOut(new Duration(totalminutespracticed * 1000), TotalTimePracticed.getLayoutBounds().getWidth());
                numberofsessionspracticedtext = numberofsessionspracticed.toString();
                disabled = false;
            } else {
                averagesessiondurationtext = "No Sessions";
                totalminutespracticedtext = "No Sessions";
                numberofsessionspracticedtext = "No Sessions";
                disabled = true;
            }
        }
        TotalTimePracticed.setText(totalminutespracticedtext);
        NumberOfSessionsPracticed.setText(numberofsessionspracticedtext);
        AverageSessionDuration.setText(averagesessiondurationtext);
        TotalTimePracticed.setDisable(disabled);
        NumberOfSessionsPracticed.setDisable(disabled);
        AverageSessionDuration.setDisable(disabled);
        if (selectionindex == 0 || selectionindex == 15) {
            PrePostSwitch.setDisable(true);
            PrePostSwitch.setSelected(false);
        } else {PrePostSwitch.setDisable(disabled);}
    }
    public void sessions_gui_displaysessionlist(Event event) {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            dialog_Information("No Sessions", "No Practiced Sessions", "Cannot View Sessions");
        } else {new AllSessionsDetails().showAndWait();}
    }
    public void sessions_gui_togglepreandpost(ActionEvent actionEvent) {sessions_gui_updateui();}
    public void sessions_gui_resetallvalues() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
    }
    public void session_gui_opensessiondetailsdialog() {

    }
    public void session_gui_opensessiondetailsdialog(Session individualsession) {
        new SessionDetails(individualsession).showAndWait();
    }
        // Util
    public boolean sessions_cleanup() {Sessions.marshall(); return true;}

    // Goals
        // GUI
    public void goals_initialize() {
        Goals = new Goals(this);
        Goals.unmarshall();
        for (Meditatable i : getSession().getAllMeditatablesincludingTotal()) {i.setGoalsController(Goals);}
        goals_gui_updateui();
    }
    public void goals_gui_updateui() {
        boolean disabled = SessionsAndGoalsSelectedMeditatable == null || SessionsAndGoalsSelectedMeditatable.goals_getCurrent() == null;
        Tooltip goalprogresstooltip;
        String percentage;
        String toptext;
        Double progress;
        String newgoalbuttontext;
        Tooltip newgoalbuttontooltip;
        newgoalButton.setDisable(SessionsAndGoalsSelectedMeditatable == null);
        viewcurrrentgoalsButton.setDisable(disabled);
        goalsprogressbar.setDisable(disabled);
        GoalProgressPercentageLabel.setDisable(disabled);
        GoalTopLabel.setDisable(disabled);
        if (SessionsAndGoalsSelectedMeditatable == null) {
            toptext = "Goal Progress Tracker";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("");
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else if (SessionsAndGoalsSelectedMeditatable.goals_getCurrent() == null || SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false) == 0) {
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + SessionsAndGoalsSelectedMeditatable.name);
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else {
            toptext = "Current Goal Progress";
            Double goalminutes = SessionsAndGoalsSelectedMeditatable.goals_getCurrent().getGoal_Hours() * 60;
            progress = Util.convert_minstodecimalhours(SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false), 2) / (goalminutes / 60);
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s",
                    Util.formatdurationtoStringSpelledOut(new Duration(SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false) * 1000), null),
                    Util.formatdurationtoStringSpelledOut(new Duration(goalminutes * 1000), null))
            );
            percentage = new Double(progress * 100).intValue() + "%";
            newgoalbuttontext = kujiin.xml.Options.GOALPACINGTEXT;
            newgoalbuttontooltip = new Tooltip("Calculate Goal Pacing For This Goal");
        }
        if (SessionsAndGoalsSelectedMeditatable != null && getOptions().getProgramOptions().getTooltips()) {
            newgoalButton.setTooltip(new Tooltip("Set A New Goal"));
            viewcurrrentgoalsButton.setTooltip(new Tooltip("Edit " + SessionsAndGoalsSelectedMeditatable.name + "'s Goals"));
        }
        GoalProgressPercentageLabel.setText(percentage);
        goalsprogressbar.setProgress(progress);
        GoalTopLabel.setText(toptext);
        GoalProgressPercentageLabel.setTooltip(goalprogresstooltip);
        goalsprogressbar.setTooltip(goalprogresstooltip);
        newgoalButton.setText(newgoalbuttontext);
        newgoalButton.setTooltip(newgoalbuttontooltip);
        if (Session.playerUI != null && Session.playerUI.isShowing()) {
            Session.playerUI.GoalTopLabel.setDisable(disabled);
            Session.playerUI.GoalPercentageLabel.setDisable(disabled);
            Session.playerUI.GoalProgressBar.setDisable(disabled);
            Session.playerUI.GoalTopLabel.setText(toptext);
            Session.playerUI.GoalProgressBar.setProgress(progress);
            Session.playerUI.GoalPercentageLabel.setText(percentage);
            // String.format("%s hrs -> %s hrs (%d", practiceddecimalhours, goaldecimalhours, progress.intValue()) + "%)");
        }
    }
    public void goals_gui_setnewgoal(Event event) {
        if (newgoalButton.getText().equals(kujiin.xml.Options.NEWGOALTEXT)) {
            SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog();
            simpleGoalSetDialog.showAndWait();
            if (simpleGoalSetDialog.shouldSetgoal()) {
                SessionsAndGoalsSelectedMeditatable.goals_add(new Goals.Goal(simpleGoalSetDialog.getNewGoalHours(), SessionsAndGoalsSelectedMeditatable));
                goals_gui_updateui();
            }
        } else if (newgoalButton.getText().equals(kujiin.xml.Options.GOALPACINGTEXT)) {
            new GoalPacingDialog().showAndWait();
        }
    }
    public void goals_gui_viewcurrentgoals(Event event) {
        if (SessionsAndGoalsSelectedMeditatable.goals_getAll() == null || SessionsAndGoalsSelectedMeditatable.goals_getAll().size() == 0) {
            dialog_Information("Information", "No Goals Exist For " + SessionsAndGoalsSelectedMeditatable.name, "Please Add A Goal For " + SessionsAndGoalsSelectedMeditatable.name);
        } else {new AllMeditatablesGoalProgress().showAndWait();}
    }
    public void goals_gui_resetallvalues() {
        goalsprogressbar.setProgress(0.0);
    }
        // Util
    public ArrayList<Meditatable> goals_util_getmeditatableswithoutlongenoughgoals(List<Meditatable> meditatablesinsession) {
        return meditatablesinsession.stream().filter(i -> i.getduration().greaterThan(Duration.ZERO) && !i.goals_arelongenough()).collect(Collectors.toCollection(ArrayList::new));
    }
    public boolean goals_cleanup() {Goals.marshall(); return true;}

// Session Player Widget
    public void player_playthisession(ActionEvent actionEvent) {
        if (Session.playerUI != null && Session.playerUI.isShowing()) {return;}
        switch (Session.creatorState) {
            case CREATED:
                Session.creation_reset(false);
            case NOT_CREATED:
                creation_util_createsession();
                if (Session.creatorState == This_Session.CreatorState.CREATED) {Session.player_openplayer();}
                else {Session.creation_reset(false);}
                break;
            default:
                break;
        }
    }

// Gui Methods
    // TODO Find Out Why Displaying Some Dialogs Makes Root Uniconified (When It's Supposed To Be)
    public boolean dialog_YesNoConfirmation(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.OK;
    }
    public Util.AnswerType dialog_YesNoCancelConfirmation(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel");
        a.getButtonTypes().clear();
        a.getButtonTypes().add(yes);
        a.getButtonTypes().add(no);
        a.getButtonTypes().add(cancel);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {return Util.AnswerType.YES;}
            if (answer.get() == no) {return Util.AnswerType.NO;}
            if (answer.get() == cancel) {return Util.AnswerType.CANCEL;}
        }
        return Util.AnswerType.CANCEL;
    }
    public Util.AnswerType dialog_YesNoCancelConfirmation(String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext, String cancelbuttontext) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");} else {yes = new ButtonType(yesbuttontext);}
        if (nobuttontext != null) {no = new ButtonType("No");} else {no = new ButtonType(nobuttontext);}
        if (cancelbuttontext != null) {cancel = new ButtonType("Cancel");} else {cancel = new ButtonType(cancelbuttontext);}
        a.getButtonTypes().clear();
        a.getButtonTypes().add(yes);
        a.getButtonTypes().add(no);
        a.getButtonTypes().add(cancel);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {return Util.AnswerType.YES;}
            if (answer.get() == no) {return Util.AnswerType.NO;}
            if (answer.get() == cancel) {return Util.AnswerType.CANCEL;}
        }
        return Util.AnswerType.CANCEL;
    }
    public void dialog_Information(String titletext, String headertext, String contexttext) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contexttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        a.showAndWait();
    }
    public void dialog_Error(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        a.setHeaderText(headertext);
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        a.showAndWait();
    }

// Dialogs
    public class ChangeAlertFile extends Stage {
        public Button HelpButton;
        public Button AcceptButton;
        public Button CancelButton;
        public CheckBox AlertFileToggleButton;
        public TextField alertfileTextField;
        public Button openFileButton;
        public Button PreviewButton;
        private File alertfile;
        private final static String NO_ALERT_FILE_SELECTED_TEXT = "No Alert File Selected";
        private final static int SUGGESTED_ALERT_FILE_MAX_LENGTH = 10;
        private final static int ABSOLUTE_ALERT_FILE_MAX_LENGTH = 30;

        public ChangeAlertFile() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAlertDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setTitle("Alert File Editor");
                alertfileTextField.setEditable(false);
                AlertFileToggleButton.setSelected(getOptions().getSessionOptions().getAlertfunction());
                String alertfilelocation = getOptions().getSessionOptions().getAlertfilelocation();
                if (alertfilelocation != null) {alertfile = new File(getOptions().getSessionOptions().getAlertfilelocation());}
                alertfiletoggled(null);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

    // Button Actions
        public void accept(ActionEvent actionEvent) {
            if (AlertFileToggleButton.isSelected() && alertfile == null) {
                dialog_YesNoConfirmation("Confirmation", "No Alert File Selected And Alert Function Enabled", "Please Select An Alert File Or Turn Off Alert Function");
                return;
            }
            getOptions().getSessionOptions().setAlertfunction(AlertFileToggleButton.isSelected());
            if (alertfile != null) {getOptions().getSessionOptions().setAlertfilelocation(alertfile.toURI().toString());}
            else {getOptions().getSessionOptions().setAlertfilelocation(null);}
            getOptions().marshall();
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            close();
        }
        public void openandtestnewfile(ActionEvent actionEvent) {
            File testfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
            if (fileisgood(testfile)) {alertfile = testfile;}
            alertfiletoggled(null);
        }
        public void preview(ActionEvent actionEvent) {
            if (alertfile != null && alertfile.exists()) {
                PreviewFile previewFile = new PreviewFile(alertfile);
                previewFile.showAndWait();
            }
        }
        public void alertfiletoggled(ActionEvent actionEvent) {
            if (AlertFileToggleButton.isSelected()) {AlertFileToggleButton.setText("ON");}
            else {AlertFileToggleButton.setText("OFF");}
            PreviewButton.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
            openFileButton.setDisable(! AlertFileToggleButton.isSelected());
            alertfileTextField.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
            if (alertfile != null && alertfile.exists()) {
                Double duration = Util.audio_getduration(alertfile);
                Duration alertfileduration = new Duration(duration * 1000);
                if (duration >= SUGGESTED_ALERT_FILE_MAX_LENGTH && duration < ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                    if (! dialog_YesNoConfirmation("Confirmation",
                        String.format("Alert File Is %s Which Is Longer Than Suggested Duration: %s And May Break Immersion",
                            Util.formatdurationtoStringDecimalWithColons(alertfileduration),
                            Util.formatdurationtoStringDecimalWithColons(new Duration(SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000))
                        ), "Really Use " + alertfile.getName() + " As Your Alert File?")) {return;}
                } else if (duration >= ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                    dialog_Information("Cannot Add Alert File",
                            String.format("Alert File Is %s Which Is Too Long And Will Break Immersion", Util.formatdurationtoStringDecimalWithColons(alertfileduration)),
                            "Cannot Add Alert File");
                    return;
                }
                String durationtext = Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), alertfileTextField.getLayoutBounds().getWidth());
                String text = String.format("%s (%s)", alertfile.getName(), durationtext);
                alertfileTextField.setText(text);
            } else {
                if (alertfile != null) {alertfile = null; alertfiletoggled(null);}
                alertfileTextField.setText(NO_ALERT_FILE_SELECTED_TEXT);
            }
        }
        public void help(ActionEvent actionEvent) {
            dialog_Information("What Is An Alert File?", "", "The 'alert file' is a short audible warning\nthat is played in between parts of the session\nto inform you it's time to player_transition to the next\npart of the session");
        }

    // Utility Methods
        public boolean fileisgood(File testfile) {
        // Test If Valid Extension
            if (! Util.audio_isValid(testfile)) {
                dialog_Information("Information", "Invalid Audio Format", "Supported Audio Formats: " + Arrays.asList(Util.SUPPORTEDAUDIOFORMATS).toString());
                return false;
            }
            Double duration = Util.audio_getduration(testfile);
            if (duration == 0.0) {
                dialog_Information("Invalid File", "Invalid Audio File", "Audio File Has Zero Length Or Is Corrupt. Cannot Use As Alert File"); return false;}
            else if (duration >= (SUGGESTED_ALERT_FILE_MAX_LENGTH) && duration < (ABSOLUTE_ALERT_FILE_MAX_LENGTH)) {
                String confirmationtext = String.format("%s Is %s Which Is Longer Than The Suggested Maximum Duration %s", testfile.getName(),
                        Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000), null));
                return dialog_YesNoConfirmation("Alert File Too Long", confirmationtext, "This May Break Session Immersion. Really Use This File As Your Alert File?");
            } else if (duration >= ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                String errortext = String.format("%s Is Longer Than The Maximum Allowable Duration %s", Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(ABSOLUTE_ALERT_FILE_MAX_LENGTH * 1000), null));
                dialog_Information("Invalid File", errortext, "Cannot Use As Alert File As It Will Break Immersion");
                return false;
            } else {return true;}
        }

//        public void alertfiletoggle() {
//            if (AlertSwitch.isSelected()) {
//                if (Options.getSessionOptions().getAlertfilelocation() == null) {
//                    AlertFile = getnewalertfile();
//                    checkalertfile();
//                }
//            } else {
//                if (Util.dialog_YesNoConfirmation(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
//                    AlertFile = null;
//                    checkalertfile();
//                } else {
//                    AlertSwitch.setSelected(true);
//                }
//            }
//        }
//        public File getnewalertfile() {
//            File newfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
//            if (newfile != null) {
//                if (Util.audio_isValid(newfile)) {
//                    double duration = Util.audio_getduration(newfile);
//                    if (duration > 10000) {
//                        if (!Util.dialog_YesNoConfirmation(Root, "Validation", "Alert File Is longer Than 10 Seconds",
//                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
//                                        "Really Use It?", duration))) {newfile = null;}
//                    }
//                } else {
//                    Util.dialog_Information(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Util.audio_getsupportedText());
//                    newfile = null;
//                }
//            }
//            return newfile;
//        }
//        public boolean checkalertfile() {
//            boolean good;
//            if (AlertFile != null && Util.audio_isValid(AlertFile)) {
//                good = true;
//                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
//                String audioduration = Util.format_secondsforplayerdisplay((int) Util.audio_getduration(AlertFile));
//                AlertFileTextField.setText(String.format("%s (%s)", AlertFile.getName(), audioduration));
//            } else {
//                good = false;
//                AlertFileTextField.setText("Alert Feature Disabled");
//                Options.getSessionOptions().setAlertfilelocation(null);
//            }
//            Options.getSessionOptions().setAlertfunction(good);
//            AlertFileEditButton.gui_setDisable(! good);
//            AlertFileTextField.gui_setDisable(! good);
//            AlertSwitch.setSelected(good);
//            return good;
//        }

    }
    public class EditReferenceFiles extends Stage {
        public ChoiceBox<String> MeditatableNamesChoiceBox;
        public TextArea MainTextArea;
        public Button CloseButton;
        public Label StatusBar;
        public Button SaveButton;
        public Button PreviewButton;
        public RadioButton HTMLVariation;
        public RadioButton TEXTVariation;
        private File selectedfile;
        private String selectedmeditatable;
        private MainController Root;
        private ArrayList<Integer> userselectedindexes;
        private This_Session.ReferenceType referenceType;

        public EditReferenceFiles(This_Session.ReferenceType referenceType) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
            setTitle("Reference Files Editor");
            ObservableList<String> meditatablenames = FXCollections.observableArrayList();
            meditatablenames.addAll(kujiin.xml.Options.ALLNAMES);
            userselectedindexes = new ArrayList<>();
            MeditatableNamesChoiceBox.setItems(meditatablenames);
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            MeditatableNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
            HTMLVariation.setDisable(MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            if (referenceType == null) {referenceType = kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION;}
            HTMLVariation.setSelected(referenceType == This_Session.ReferenceType.html);
            TEXTVariation.setSelected(referenceType == This_Session.ReferenceType.txt);
            this.referenceType = referenceType;
            PreviewButton.setDisable(true);
            SaveButton.setDisable(true);
            String referencename = referenceType.name();
            this.setOnCloseRequest(event -> {
                if (unsavedchanges()) {
                    switch (dialog_YesNoCancelConfirmation("Confirmation", MeditatableNamesChoiceBox.getValue() + " " + referencename + " Variation Has Unsaved Changes", "Save Changes Before Exiting?")) {
                        case YES:
                            saveselectedfile(null);
                            break;
                        case NO:
                            break;
                        case CANCEL:
                            event.consume();
                    }
                }
            });
        }

    // Getters And Setters

    // Text Area Methods
        private boolean unsavedchanges() {
            try {
                return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
            } catch (Exception e) {return false;}
        }
        public void newmeditatableselected(ActionEvent actionEvent) {
            HTMLVariation.setDisable(MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            if (userselectedindexes.size() > 0 && selectedfile != null && unsavedchanges()) {
                Util.AnswerType answerType = dialog_YesNoCancelConfirmation("Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading A Different Meditatable");
                switch (answerType) {
                    case YES:
                        saveselectedfile(null);
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        MeditatableNamesChoiceBox.getSelectionModel().select(userselectedindexes.get(userselectedindexes.size() - 1));
                        return;
                }
            }
            loadselectedfile();
        }
        private void textchanged() {
            if (referenceType != null && selectedmeditatable != null && selectedfile != null) {
                boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
                PreviewButton.setDisable(! hasvalidtext || referenceType == This_Session.ReferenceType.txt);
                SaveButton.setDisable(MainTextArea.getText() == null || Util.file_getcontents(selectedfile).equals(MainTextArea.getText().toCharArray()));
                switch (referenceType) {
                    case html:
                        if (MainTextArea.getText() != null && Util.String_validhtml(MainTextArea.getText())) {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                        else {StatusBar.setTextFill(Color.RED); StatusBar.setText("Not Valid .html");}
                        break;
                    case txt:
                        if (MainTextArea.getText() != null && MainTextArea.getText().length() == 0) {StatusBar.setTextFill(Color.RED); StatusBar.setText("No Text Entered");}
                        else {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                        break;
                }
            } else {
                MainTextArea.clear();
                StatusBar.setTextFill(Color.RED);
                Util.gui_showtimedmessageonlabel(StatusBar, "No Meditatable Selected", 3000);
            }
        }

    // Other Methods
        public void saveselectedfile(ActionEvent actionEvent) {
            if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
                String text = selectedmeditatable + "'s Reference File (" + referenceType.toString() + " Variation) Has Been Saved";
                dialog_Information("Changes Saved", text, "");
            } else {
                dialog_Error("Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
        }
        public void loadselectedfile() {
            if (MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
                selectedmeditatable = MeditatableNamesChoiceBox.getSelectionModel().getSelectedItem();
                selectnewfile();
                String contents = Util.file_getcontents(selectedfile);
                MainTextArea.setText(contents);
                PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
                StatusBar.setTextFill(Color.BLACK);
                StatusBar.setText("");
                SaveButton.setDisable(true);
            } else {
                if (MeditatableNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1) {
                    dialog_Information("Information", "No Meditatable Selected", "Select A Meditatable To Load");}
                else {
                    dialog_Information("Information", "No Variation Selected", "Select A Variation To Load");}
                PreviewButton.setDisable(true);
            }
        }
        public void selectnewfile() {
            if (referenceType == null || selectedmeditatable == null) {selectedfile = null; return;}
            switch (referenceType) {
                case html:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html"), selectedmeditatable + ".html");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(e);}}
                    break;
                case txt:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt"), selectedmeditatable + ".txt");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(e);}}
                    break;
            }
        }
        public void htmlselected(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                Util.AnswerType answerType = dialog_YesNoCancelConfirmation("Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading HTML Variation");
                switch (answerType) {
                    case YES:
                        saveselectedfile(null);
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        HTMLVariation.setSelected(false);
                        TEXTVariation.setSelected(true);
                        return;
                }
            }
            // Test If Unsaved Changes Here
            TEXTVariation.setSelected(false);
            PreviewButton.setDisable(! HTMLVariation.isSelected());
            referenceType = This_Session.ReferenceType.html;
            selectnewfile();
            loadselectedfile();
        }
        public void textselected(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                Util.AnswerType answerType = dialog_YesNoCancelConfirmation("Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading TXT Variation");
                switch (answerType) {
                    case YES:
                        saveselectedfile(null);
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        HTMLVariation.setSelected(true);
                        TEXTVariation.setSelected(false);
                        return;
                }
            }
            // Test If Unsaved Changes Here
            HTMLVariation.setSelected(false);
            PreviewButton.setDisable(! HTMLVariation.isSelected());
            referenceType = This_Session.ReferenceType.txt;
            selectnewfile();
            loadselectedfile();
        }
        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && referenceType == This_Session.ReferenceType.html) {
                if (! Util.String_validhtml(MainTextArea.getText())) {
                    if (! dialog_YesNoConfirmation("Confirmation", "Html Code In Text Area Is Not Valid HTML", "Preview Anyways?")) {return;}
                }
                Session.player_displayreferencepreview(MainTextArea.getText());
            }
        }

    // Dialog Methods
        public void closewindow(Event event) {
        // Check If Unsaved Text
        this.close();
    }

    }
    public class PreviewFile extends Stage {
        public Label CurrentTime;
        public Slider ProgressSlider;
        public Label TotalTime;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public Slider VolumeSlider;
        public Label VolumePercentage;
        public Label TopLabel;
        private Media Mediatopreview;
        private File Filetopreview;
        private MediaPlayer PreviewPlayer;

        public PreviewFile(File filetopreview) {
            if (Util.audio_isValid(filetopreview)) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                fxmlLoader.setController(this);
                setOnHidden(event -> {
                    if (PreviewPlayer != null) {PreviewPlayer.dispose();}
                    close();
                });
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    getOptions().setStyle(this);
                    this.setResizable(false);
                    Filetopreview = filetopreview;
                    TopLabel.setText(Filetopreview.getName().substring(0, Filetopreview.getName().lastIndexOf(".")));
                    Mediatopreview = new Media(Filetopreview.toURI().toString());
                    PreviewPlayer = new MediaPlayer(Mediatopreview);
                    PlayButton.setDisable(true);
                    PauseButton.setDisable(true);
                    StopButton.setDisable(true);
                    PreviewPlayer.setOnReady(() -> {
                        CurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(0)));
                        TotalTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(PreviewPlayer.getTotalDuration().toSeconds() * 1000)));
                        PlayButton.setDisable(false);
                        PlayButton.setOnAction(event -> syncbuttons());
                        PauseButton.setDisable(false);
                        PauseButton.setOnAction(event -> syncbuttons());
                        StopButton.setDisable(false);
                        StopButton.setOnAction(event -> syncbuttons());
                    });
                    VolumeSlider.setValue(0.0);
                    VolumePercentage.setText("0%");
                } catch (IOException ignored) {}
            } else {dialog_Information("Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
        }

        public void play(ActionEvent actionEvent) {
            if (PreviewPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                PreviewPlayer.play();
                VolumeSlider.setValue(1.0);
                VolumePercentage.setText("100%");
                ProgressSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                    Duration seektothis = PreviewPlayer.getTotalDuration().multiply(ProgressSlider.getValue());
                    PreviewPlayer.seek(seektothis);
                });
                PreviewPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    CurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(newValue));
                    updatePositionSlider(PreviewPlayer.getCurrentTime());
                });
                VolumeSlider.valueChangingProperty().unbind();
                VolumeSlider.setOnMouseDragged(event -> {
                    Double value = VolumeSlider.getValue() * 100;
                    VolumePercentage.setText(value.intValue() + "%");
                });
                VolumeSlider.valueProperty().bindBidirectional(PreviewPlayer.volumeProperty());
                PreviewPlayer.setOnPlaying(this::syncbuttons);
                PreviewPlayer.setOnPaused(this::syncbuttons);
                PreviewPlayer.setOnStopped(this::syncbuttons);
            }
        }
        public void updatePositionSlider(Duration currenttime) {
            if (ProgressSlider.isValueChanging()) {return;}
            Duration total = PreviewPlayer.getTotalDuration();
            if (total == null || currenttime == null) {ProgressSlider.setValue(0);}
            else {ProgressSlider.setValue(currenttime.toMillis() / total.toMillis());}
        }
        public void pause(ActionEvent actionEvent) {
            if (PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING) {PreviewPlayer.pause();}
            syncbuttons();
        }
        public void stop(ActionEvent actionEvent) {
            if (PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING || PreviewPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                PreviewPlayer.stop();
            }
            syncbuttons();
        }
        public void syncbuttons() {
            PlayButton.setDisable(PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING);
            PauseButton.setDisable(PreviewPlayer.getStatus() == MediaPlayer.Status.PAUSED || PreviewPlayer.getStatus() == MediaPlayer.Status.STOPPED);
            StopButton.setDisable(PreviewPlayer.getStatus() == MediaPlayer.Status.STOPPED);
        }
        public void reset() {
            if (Mediatopreview != null) {PreviewPlayer.stop(); PreviewPlayer.dispose();}
            TopLabel.setText("No File Selected");
            TotalTime.setText("--:--");
            CurrentTime.setText("--:--");
            ProgressSlider.setValue(0);
            VolumeSlider.setValue(0);
        }
    }
    public class ExceptionDialog extends Stage {
        public TextArea StackTraceTextField;
        public Button CloseButton;
        public Button ContinueButton;
        public CheckBox NotifyMeCheckbox;
        public Label TopText;

        public ExceptionDialog(Exception exception) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExceptionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
            } catch (IOException ignored) {}
            System.out.println(String.format("Time %s Encountered: %s", exception.getClass().getName(), LocalDate.now()));
            exception.printStackTrace();
            setTitle("Program Error Occured");
            TopText.setText(exception.getClass().getName() + " Occured");
            StackTraceTextField.setText(exception.getMessage());
            StackTraceTextField.setWrapText(true);
        }

        public void exit(ActionEvent actionEvent) {
            if (NotifyMeCheckbox.isSelected()) {
                Util.sendstacktracetodeveloper(StackTraceTextField.getText());
            }
            this.close();
            System.exit(1);
        }
        public void continueprogram(ActionEvent actionEvent) {
            if (NotifyMeCheckbox.isSelected()) {
                Util.sendstacktracetodeveloper(StackTraceTextField.getText());
            }
            this.close();
        }

    }
    public class ChangeProgramOptions extends Stage {
        public CheckBox TooltipsCheckBox;
        public CheckBox HelpDialogsCheckBox;
        public TextField AlertFileTextField;
        public Button AlertFileEditButton;
        public TextField FadeInValue;
        public TextField FadeOutValue;
        public TextField EntrainmentVolumePercentage;
        public TextField AmbienceVolumePercentage;
        public ChoiceBox<String> ProgramThemeChoiceBox;
        public Button CloseButton;
        public Button DeleteAllGoalsButton;
        public Button DeleteAllSessionsProgressButton;
        public Button DefaultsButton;
        public CheckBox ReferenceSwitch;
        public RadioButton ReferenceHTMLRadioButton;
        public RadioButton ReferenceTXTRadioButton;
        public CheckBox FullscreenCheckbox;
        public CheckBox RampSwitch;
        public Button AddNewThemeButton;
        public Label ProgramOptionsStatusBar;
        private kujiin.xml.Options Options;
        private This_Session.ReferenceType tempreferencetype;

        public ChangeProgramOptions() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeProgramOptions.fxml"));
                fxmlLoader.setController(this);
                Options = getOptions();
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setResizable(false);
                setTitle("Preferences");
                AlertFileTextField.setEditable(false);
                setuplisteners();
                setuptooltips();
                populatefromxml();
                referencetoggle();
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

    // Setup Methods
        public void populatefromxml() {
        // Program Options
            TooltipsCheckBox.setSelected(getOptions().getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(getOptions().getProgramOptions().getHelpdialogs());
        // Session Options
            alertfiletoggled();
        // Playback Options
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            EntrainmentVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getEntrainmentvolume() * 100).intValue()));
            AmbienceVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getAmbiencevolume() * 100).intValue()));
        // Appearance Options
            populateappearancecheckbox();
        }
        public void setuptooltips() {
            TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
            HelpDialogsCheckBox.setTooltip(new Tooltip("Display Help Dialogs"));
            AlertFileTextField.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
            AlertFileEditButton.setTooltip(new Tooltip("Edit Alert File"));
            RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
            FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
            FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
            EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
            AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
            DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
            DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
        }
        public void setuplisteners() {
            Util.custom_textfield_double(FadeInValue, 0.0, 60.0, 1, 1);
            Util.custom_textfield_double(FadeOutValue, 0.0, 60.0, 1, 1);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            CloseButton.setOnAction(event -> close());
            ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
            ReferenceHTMLRadioButton.setOnAction(event1 -> HTMLTypeSelected());
            ReferenceTXTRadioButton.setOnAction(event1 -> TXTTypeSelected());
            ProgramThemeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectnewtheme());
        }

    // Alert File Methods
        public void editalertfile(ActionEvent actionEvent) {
            new ChangeAlertFile().showAndWait();
            alertfiletoggled();
        }
        public void alertfiletoggled() {
            boolean enabled = getOptions().getSessionOptions().getAlertfunction();
            if (enabled) {
                if (Options.getSessionOptions().getAlertfilelocation() != null) {
                    File alertfile = new File(Options.getSessionOptions().getAlertfilelocation());
                    if (! alertfile.exists()) {Options.getSessionOptions().setAlertfilelocation(null); alertfiletoggled();}
                    String duration = Util.formatdurationtoStringSpelledOut(new Duration(Util.audio_getduration(alertfile) * 1000), AlertFileTextField.getLayoutBounds().getWidth() - (alertfile.getName().length()) + 3);
                    String text = String.format("%s (%s)", alertfile.getName(), duration);
                    AlertFileTextField.setText(text);
                    AlertFileTextField.setDisable(false);
                    AlertFileEditButton.setText("Edit");
                } else {
                    AlertFileTextField.setText("Alert File Disabled");
                    AlertFileTextField.setDisable(true);
                    AlertFileEditButton.setText("Add");
                }
            } else {
                AlertFileTextField.setText("Alert File Disabled");
                AlertFileTextField.setDisable(true);
                AlertFileEditButton.setText("Add");
            }
        }

    // Reference Methods
        public void referencetoggle() {
            boolean enabled = ReferenceSwitch.isSelected();
            ReferenceHTMLRadioButton.setDisable(! enabled);
            ReferenceTXTRadioButton.setDisable(! enabled);
            FullscreenCheckbox.setDisable(! enabled);
            if (! enabled) {
                tempreferencetype = null;
                ReferenceHTMLRadioButton.setSelected(false);
                ReferenceTXTRadioButton.setSelected(false);
            }
        }
        public void HTMLTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(true);
            ReferenceTXTRadioButton.setSelected(false);
            tempreferencetype = This_Session.ReferenceType.html;
        }
        public void TXTTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(false);
            ReferenceTXTRadioButton.setSelected(true);
            tempreferencetype = This_Session.ReferenceType.txt;
        }

    // Appearance Methods
        public void populateappearancecheckbox() {
            ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(getOptions().getAppearanceOptions().getThemefilenames()));
            try {
                int index = getOptions().getAppearanceOptions().getThemefiles().indexOf(getOptions().getAppearanceOptions().getThemefile());
                ProgramThemeChoiceBox.getSelectionModel().select(index);
            } catch (Exception ignored) {}
        }
        public void addnewtheme(ActionEvent actionEvent) {
            File newfile = new FileChooser().showOpenDialog(this);
            if (newfile == null) {return;}
            Options.addthemefile(newfile.getName().substring(0, newfile.getName().lastIndexOf(".")), newfile.toURI().toString());
            populateappearancecheckbox();
        }
        public void selectnewtheme() {
            int index = ProgramThemeChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Options.getAppearanceOptions().setThemefile(getOptions().getAppearanceOptions().getThemefiles().get(index));
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(Options.getAppearanceOptions().getThemefile());
            }
        }

    // Button Actions
        public void resettodefaults(ActionEvent actionEvent) {
            if (dialog_YesNoConfirmation("Reset To Defaults", "Reset All Values To Defaults?", "You Will Lose Any Unsaved Changes")) {
                Options.resettodefaults();
                populatefromxml();
            }
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (dialog_YesNoConfirmation("Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    dialog_Error("Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {
                    dialog_Information("Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (dialog_YesNoConfirmation("Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    dialog_Error("Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {
                    dialog_Information("Success", "Successfully Deleted All Practiced Sessions, Resetting All Progress", "");}
            }
        }

        @Override
        public void close() {
            Options.getProgramOptions().setTooltips(TooltipsCheckBox.isSelected());
            Options.getProgramOptions().setHelpdialogs(HelpDialogsCheckBox.isSelected());
            Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setFadeoutduration(new Double(FadeOutValue.getText()));
            Options.getSessionOptions().setFadeinduration(new Double(FadeInValue.getText()));
            Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
            Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
            Options.getSessionOptions().setReferencetype(tempreferencetype);
            Options.getSessionOptions().setReferencefullscreen(FullscreenCheckbox.isSelected());
            Options.marshall();
            super.close();
        }
    }
    public class ChangeAllValuesDialog extends Stage {
            public Button AcceptButton;
            public Button CancelButton;
            public TextField MinutesTextField;
            public CheckBox PresessionCheckbox;
            public CheckBox PostsessionCheckBox;
            private Boolean accepted;
            private int minutes;

            public ChangeAllValuesDialog(String toptext) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAllValuesDialog.fxml"));
                    fxmlLoader.setController(this);
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    getOptions().setStyle(this);
                    this.setResizable(false);
                    setTitle(toptext);
                    setAccepted(false);
                    MinutesTextField.setText("0");
                    Util.custom_textfield_integer(MinutesTextField, 0, 600, 1);
                    MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        try {setMinutes(Integer.parseInt(MinutesTextField.getText()));}
                        catch (NumberFormatException ignored) {setMinutes(0);}
                    });
                } catch (IOException e) {}
            }

        // Getters And Setters
            public Boolean getAccepted() {
            return accepted;
        }
            public void setAccepted(Boolean accepted) {
                this.accepted = accepted;
            }
            public int getMinutes() {
                return minutes;
            }
            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

        // Button Actions
            public void acceptbuttonpressed(ActionEvent event) {setAccepted(true); close();}
            public void cancelbuttonpressed(ActionEvent event) {setAccepted(false); close();}
            public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
            public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
    }
    public class AllMeditatablesGoalProgress extends Stage {
        public TableView<GoalProgressBinding> GoalsTable;
        public TableColumn<GoalProgressBinding, String> NameColumn;
        public TableColumn<GoalProgressBinding, String> PracticedTimeColumn;
        public TableColumn<GoalProgressBinding, String> CurrentGoalColumn;
        public TableColumn<GoalProgressBinding, String> PercentCompletedColumn;
        public TableColumn<GoalProgressBinding, String> NumberGoalsCompletedColumn;
        public Button SetCurrentGoalButton;
        public Button ViewCompletedGoalsButton;
        public Button CloseButton;
        private ObservableList<GoalProgressBinding> allgoalsdetails = FXCollections.observableArrayList();
        private String setgoaltext = "Set Goal";
        private String goalpacingtext = "Current Goal Pacing";

        public AllMeditatablesGoalProgress() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AllMeditatablesGoalProgress.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Goal Progress");
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                PracticedTimeColumn.setCellValueFactory(cellData -> cellData.getValue().practicedtime);
                CurrentGoalColumn.setCellValueFactory(cellData -> cellData.getValue().currentgoaltime);
                PercentCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().percentcompleted);
                NumberGoalsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().numbergoalscompleted);
                NameColumn.setStyle("-fx-alignment: CENTER;");
                PracticedTimeColumn.setStyle("-fx-alignment: CENTER;");
                CurrentGoalColumn.setStyle("-fx-alignment: CENTER;");
                PercentCompletedColumn.setStyle("-fx-alignment: CENTER;");
                NumberGoalsCompletedColumn.setStyle("-fx-alignment: CENTER;");
                GoalsTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> newrowselected());
                populatetable();
                newrowselected();
                if (SessionsAndGoalsSelectedMeditatable != null) {GoalsTable.getSelectionModel().select(SessionsAndGoalsSelectedMeditatable.number);}
                this.setOnCloseRequest(event -> {
                    ArrayList<Meditatable> meditatablesmissingcurrentgoals = getSession().getAllMeditatablesincludingTotal().stream().filter(i -> i.goals_getCurrent() == null).collect(Collectors.toCollection(ArrayList::new));
                    if (meditatablesmissingcurrentgoals.size() > 0) {
                        if (! dialog_YesNoConfirmation("Confirmation", "Missing Current Goals For " + meditatablesmissingcurrentgoals.size() + " Meditatables", "Really Close Without Setting A Current Goal For All Meditatables?")) {
                            event.consume();
                        }
                    }
                });
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        public void populatetable() {
            allgoalsdetails.clear();
            for (Meditatable i : getSession().getAllMeditatablesincludingTotal()) {
                String practicedtime = Util.formatdurationtoStringSpelledOut(new Duration(i.sessions_getTotalMinutesPracticed(false)), null);
                if (practicedtime.equals("0 Minutes")) {
                    // TODO None Text Here! No Practiced Time Set
                }
                String currentgoaltime;
                String percentcompleted ;
                try {
                    currentgoaltime = Util.formatdurationtoStringSpelledOut(new Duration(i.goals_getCurrent().getGoal_Hours() * 3_600_000), null);
                    percentcompleted = String.valueOf(new Double((i.sessions_getTotalMinutesPracticed(false) / (i.goals_getCurrent().getGoal_Hours() * 60)) * 100).intValue()) + "%";
                } catch (NullPointerException ignored) {
                    currentgoaltime = "No Goal Set";
                    percentcompleted = "No Goal Set";
                }
                allgoalsdetails.add(new GoalProgressBinding(i.name, practicedtime, currentgoaltime, percentcompleted, i.goals_getCompletedGoalCount()));
            }
            GoalsTable.setItems(allgoalsdetails);
        }
        public void newrowselected() {
            if (GoalsTable.getSelectionModel().getSelectedIndex() == -1) {SessionsAndGoalsSelectedMeditatable = null;}
            else {SessionsAndGoalsSelectedMeditatable = getSession().getAllMeditatablesincludingTotal().get(GoalsTable.getSelectionModel().getSelectedIndex());}
            if (SessionsAndGoalsSelectedMeditatable == null) {
                SetCurrentGoalButton.setDisable(true);
                ViewCompletedGoalsButton.setDisable(true);
            } else {
                SetCurrentGoalButton.setDisable(false);
                if (SessionsAndGoalsSelectedMeditatable.goals_getCurrent() == null) {SetCurrentGoalButton.setText(setgoaltext);}
                else {SetCurrentGoalButton.setText(goalpacingtext);}
                ViewCompletedGoalsButton.setDisable(SessionsAndGoalsSelectedMeditatable.goals_getCompletedGoalCount() == 0);
            }
        }
        public void setcurrentgoal(ActionEvent actionEvent) {
            if (SessionsAndGoalsSelectedMeditatable != null) {
                SimpleGoalSetDialog setDialog = new SimpleGoalSetDialog();
                setDialog.showAndWait();
                if (setDialog.shouldSetgoal()) {
                    SessionsAndGoalsSelectedMeditatable.goals_add(new Goals.Goal(setDialog.getNewGoalHours(), SessionsAndGoalsSelectedMeditatable));
                    populatetable();
                }
            }
        }
        public void viewcompletedgoals(ActionEvent actionEvent) {
            if (SessionsAndGoalsSelectedMeditatable != null) {

            }
        }

        class GoalProgressBinding {
            private StringProperty name;
            private StringProperty practicedtime;
            private StringProperty currentgoaltime;
            private StringProperty percentcompleted;
            private StringProperty numbergoalscompleted;

            public GoalProgressBinding(String name, String practicedtime, String currentgoaltime, String percentcompleted, int numbergoalscompleted) {
                this.name = new SimpleStringProperty(name);
                this.practicedtime = new SimpleStringProperty(practicedtime);
                this.currentgoaltime = new SimpleStringProperty(currentgoaltime);
                this.percentcompleted = new SimpleStringProperty(percentcompleted);
                this.numbergoalscompleted = new SimpleStringProperty(String.valueOf(numbergoalscompleted));
            }
        }
    }
    public class SimpleGoalSetDialog extends Stage {
        public MainController Root;
        public Label TopLabel;
        public Spinner<Integer> HoursSpinner;
        public Spinner<Integer> MinutesSpinner;
        public TextField DecimalHoursTextField;
        public Label StatusBar;
        public Button AcceptButton;
        public Button CancelButton;
        private boolean setgoal = false;
        private int practicedminutes;

        public SimpleGoalSetDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SetGoalDialog_Simple.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Set A New Goal");
                HoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                MinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                HoursSpinner.getValueFactory().setValue(SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false) / 60);
                MinutesSpinner.getValueFactory().setValue(SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false) % 60);
                Util.custom_spinner_integer(HoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                Util.custom_spinner_integer(MinutesSpinner, 0, 59, 1, false);
                practicedminutes = SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false);
                DecimalHoursTextField.setText(Util.convert_minstodecimalhours(practicedminutes, 1).toString());
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        public boolean shouldSetgoal() {
            return setgoal;
        }
        public void checkvalue() {
            try {
                int value = (HoursSpinner.getValue() * 60) + MinutesSpinner.getValue();
                if (value < practicedminutes) {
                    HoursSpinner.getValueFactory().setValue(practicedminutes / 60);
                    MinutesSpinner.getValueFactory().setValue(practicedminutes % 60);
                    StatusBar.setText("Cannot Set Goal Lower Than Practiced Hours");
                } else {StatusBar.setText("");}
                DecimalHoursTextField.setText(Util.convert_minstodecimalhours((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue(), 1).toString());
            } catch (NullPointerException ignored) {}
        }
        public Double getNewGoalHours() {
            try {
                return Util.convert_minstodecimalhours((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue(), 2);
            } catch (NullPointerException e) {return null;}
        }
        public void accept(ActionEvent actionEvent) {
            if (((HoursSpinner.getValue() * 60) + MinutesSpinner.getValue()) <= practicedminutes) {
                dialog_Information("Cannot Accept", "Goal Is Less Than Or Equal To Practiced Minutes", "Goal Must Be Greater Than Practiced Minutes");
                return;
            }
            setgoal = true;
        }

    }
    public class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField TotalPracticedTime;
        public Label TopLabel;
        private Double practicedhours;
        private Double goalhours;
        private Double hoursleft;

        public GoalPacingDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/GoalPacingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Goal Pacing");
                practicedhours = (double) (SessionsAndGoalsSelectedMeditatable.sessions_getTotalMinutesPracticed(false) / 60);
                goalhours = SessionsAndGoalsSelectedMeditatable.goals_getCurrent().getGoal_Hours();
                GoalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(goalhours * 3_600_000), GoalDuration.getLayoutBounds().getWidth()));
                TotalPracticedTime.setText(Util.formatdurationtoStringSpelledOut(new Duration(practicedhours * 3600000), TotalPracticedTime.getLayoutBounds().getWidth()));
                hoursleft = goalhours - practicedhours;
                GoalTimeLeft.setText(Util.formatdurationtoStringSpelledOut(new Duration(hoursleft * 3600000), GoalTimeLeft.getLayoutBounds().getWidth()));
                Util.custom_spinner_integer(PracticeDays, 1, Integer.MAX_VALUE, 1, false);
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> calculate());
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
                TopLabel.setText("Goal Pacing For " + SessionsAndGoalsSelectedMeditatable.name + " Current Goal");
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        // Other Methods
        public void calculate() {
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = hoursleft.floatValue() / days.floatValue();
            int minsaday = Util.convert_decimalhourstominutes(hourstopractice.doubleValue());
            PracticeTimeADay.setText(Util.formatdurationtoStringSpelledOut(new Duration((minsaday * 60) * 1000), PracticeTimeADay.getLayoutBounds().getWidth()));
        }
    }
    public class AllSessionsDetails extends Stage {
        // All Session Totals Tab Fields
        public BarChart<String, Number> SessionTotalsBarGraph;
        public javafx.scene.chart.CategoryAxis CategoryAxis;
        public NumberAxis NumbersAxis;
        // Session Details List Tab Fields
        // Filter Accordion
        // Date
        public CheckBox FilterByDateSwitch;
        public DatePicker Filter_DateRange_From;
        public DatePicker Filter_DateRange_To;
        // Selected Meditatables
        public CheckBox FilterBySelectedSwitch;
        public CheckBox Filter_PresessionCheckbox;
        public CheckBox Filter_PostsessionCheckbox;
        public CheckBox Filter_RinCheckbox;
        public CheckBox Filter_KyoCheckbox;
        public CheckBox Filter_TohCheckbox;
        public CheckBox Filter_ShaCheckbox;
        public CheckBox Filter_KaiCheckbox;
        public CheckBox Filter_JinCheckbox;
        public CheckBox Filter_RetsuCheckbox;
        public CheckBox Filter_ZaiCheckbox;
        public CheckBox Filter_ZenCheckbox;
        public CheckBox Filter_EarthCheckbox;
        public CheckBox Filter_AirCheckbox;
        public CheckBox Filter_FireCheckbox;
        public CheckBox Filter_WaterCheckbox;
        public CheckBox Filter_VoidCheckbox;
        // Optional Threshhold
        public CheckBox Filter_DurationThresholdCheckbox;
        public TextField Filter_ThresholdMinutesTextField;
        // Session List Table
        public TableView<SessionRow> sessionsTableView;
        public TableColumn<SessionRow, String> DateColumn;
        public TableColumn<SessionRow, String> PreColumn;
        public TableColumn<SessionRow, String> RinColumn;
        public TableColumn<SessionRow, String> KyoColumn;
        public TableColumn<SessionRow, String> TohColumn;
        public TableColumn<SessionRow, String> ShaColumn;
        public TableColumn<SessionRow, String> KaiColumn;
        public TableColumn<SessionRow, String> JinColumn;
        public TableColumn<SessionRow, String> RetsuColumn;
        public TableColumn<SessionRow, String> ZaiColumn;
        public TableColumn<SessionRow, String> ZenColumn;
        public TableColumn<SessionRow, String> EarthColumn;
        public TableColumn<SessionRow, String> AirColumn;
        public TableColumn<SessionRow, String> FireColumn;
        public TableColumn<SessionRow, String> WaterColumn;
        public TableColumn<SessionRow, String> VoidColumn;
        public TableColumn<SessionRow, String> PostColumn;
        public TableColumn<SessionRow, String> TotalColumn;
        public Button ViewDetailsButton;
        public TableView<TotalProgressRow> TotalProgressTableView;
        public TableColumn<TotalProgressRow, String> NameColumn;
        public TableColumn<TotalProgressRow, String> CurrentPracticeTimeColumn;
        public PieChart SessionBalancePieChart;

        // My Fields
        private List<Session> allsessionslist;
        private List<Session> filteredsessionlist;
        private ObservableList<SessionRow> sessionrowlist = FXCollections.observableArrayList();

        public AllSessionsDetails() {
            try {
                allsessionslist = getSessions().getSession();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_All.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
            setTitle("Session List");
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            RinColumn.setCellValueFactory(cellData -> cellData.getValue().rin);
            KyoColumn.setCellValueFactory(cellData -> cellData.getValue().kyo);
            TohColumn.setCellValueFactory(cellData -> cellData.getValue().toh);
            ShaColumn.setCellValueFactory(cellData -> cellData.getValue().sha);
            KaiColumn.setCellValueFactory(cellData -> cellData.getValue().kai);
            JinColumn.setCellValueFactory(cellData -> cellData.getValue().jin);
            RetsuColumn.setCellValueFactory(cellData -> cellData.getValue().retsu);
            ZaiColumn.setCellValueFactory(cellData -> cellData.getValue().zai);
            ZenColumn.setCellValueFactory(cellData -> cellData.getValue().zen);
            EarthColumn.setCellValueFactory(cellData -> cellData.getValue().earth);
            AirColumn.setCellValueFactory(cellData -> cellData.getValue().air);
            FireColumn.setCellValueFactory(cellData -> cellData.getValue().fire);
            WaterColumn.setCellValueFactory(cellData -> cellData.getValue().water);
            VoidColumn.setCellValueFactory(cellData -> cellData.getValue().Void);
            PostColumn.setCellValueFactory(cellData -> cellData.getValue().postsession);
            TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total);
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            CurrentPracticeTimeColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
            ViewDetailsButton.setDisable(true);
            sessionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ViewDetailsButton.setDisable(sessionsTableView.getSelectionModel().getSelectedIndex() == -1));
            Util.custom_textfield_integer(Filter_ThresholdMinutesTextField, 0, Integer.MAX_VALUE, 1);
            populatetotalsbargraphandtable();
            populatetable(null);
        }

        public CheckBox getcheckbox(int meditatableindex) {
            switch (meditatableindex) {
                case 0: return Filter_PresessionCheckbox;
                case 1: return Filter_RinCheckbox;
                case 2: return Filter_KyoCheckbox;
                case 3: return Filter_TohCheckbox;
                case 4: return Filter_ShaCheckbox;
                case 5: return Filter_KaiCheckbox;
                case 6: return Filter_JinCheckbox;
                case 7: return Filter_RetsuCheckbox;
                case 8: return Filter_ZaiCheckbox;
                case 9: return Filter_ZenCheckbox;
                case 10: return Filter_EarthCheckbox;
                case 11: return Filter_AirCheckbox;
                case 12: return Filter_FireCheckbox;
                case 13: return Filter_WaterCheckbox;
                case 14: return Filter_VoidCheckbox;
                case 15: return Filter_PostsessionCheckbox;
                default: return null;
            }
        }

        public void populatetotalsbargraphandtable() {
            ObservableList<TotalProgressRow> totalprogressrows = FXCollections.observableArrayList();
            ObservableList<PieChart.Data> piecesofthepie = FXCollections.observableArrayList();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Meditatable i : getSession().getAllMeditatablesincludingTotal()) {
                if (! (i instanceof Total)) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), Util.convert_minstodecimalhours(i.sessions_getTotalMinutesPracticed(false), 1)));
                    piecesofthepie.add(new PieChart.Data(i.getNameForChart(), Util.convert_minstodecimalhours(i.sessions_getTotalMinutesPracticed(false), 1)));
                }
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.sessions_getTotalMinutesPracticed(false) * 60000))));
            }
            SessionBalancePieChart.getData().addAll(piecesofthepie);
            TotalProgressTableView.setItems(totalprogressrows);
            SessionTotalsBarGraph.getData().add(series);
        }
        public void populatetable(ActionEvent actionEvent) {
            filteredsessionlist = new ArrayList<>();
            ObservableList<SessionRow> rowlist = FXCollections.observableArrayList();
            int count = 1;
            for (Session i : allsessionslist) {
                if (FilterByDateSwitch.isSelected()) {
                    LocalDate sessiondate = Util.convert_stringtolocaldate(i.getDate_Practiced());
                    if (Filter_DateRange_From.getValue() != null) {if (sessiondate.isBefore(Filter_DateRange_From.getValue())) {continue;}}
                    if (Filter_DateRange_To.getValue() != null) {if (sessiondate.isAfter(Filter_DateRange_To.getValue())) {continue;}}
                }
                if (FilterBySelectedSwitch.isSelected()) {
                    boolean validsession = true;
                    for (int j = 0; j < 16; j++) {
                        if (! validsession) {break;}
                        if (getcheckbox(j).isSelected()) {
                            if (Filter_DurationThresholdCheckbox.isSelected()) {
                                try {
                                    if (i.getmeditatableduration(j) <= Integer.parseInt(Filter_ThresholdMinutesTextField.getText())) {validsession = false;}
                                } catch (NumberFormatException | NullPointerException ignored) {validsession = i.getmeditatableduration(j) > 0;}
                            } else {if (i.getmeditatableduration(j) == 0) {validsession = false;}}
                        }
                    }
                    if (! validsession) {continue;}
                }
                rowlist.add(new SessionRow(count, i.getDate_Practiced(), i.getPresession_Duration(), i.getRin_Duration(),
                        i.getKyo_Duration(), i.getToh_Duration(), i.getSha_Duration(), i.getKai_Duration(), i.getJin_Duration(),
                        i.getRetsu_Duration(), i.getZai_Duration(), i.getZen_Duration(), i.getEarth_Duration(), i.getAir_Duration(),
                        i.getFire_Duration(), i.getWater_Duration(), i.getVoid_Duration(), i.getPostsession_Duration(),
                        i.getTotal_Session_Duration()));
                filteredsessionlist.add(i);
                count++;
            }
            sessionsTableView.setItems(rowlist);
        }
        public void viewsessiondetails(ActionEvent actionEvent) {
            if (sessionsTableView.getSelectionModel().getSelectedIndex() != -1) {
                new SessionDetails(filteredsessionlist.get(sessionsTableView.getSelectionModel().getSelectedIndex())).showAndWait();
            }
        }
        public void filterbydateselected(ActionEvent actionEvent) {
            if (! FilterByDateSwitch.isSelected()) {
                Filter_DateRange_From.setValue(null);
                Filter_DateRange_To.setValue(null);
            } else {
                try {
                    Filter_DateRange_From.setValue(Util.convert_stringtolocaldate(allsessionslist.get(0).getDate_Practiced()));
                } catch (NullPointerException | IndexOutOfBoundsException ignored) {
                    Filter_DateRange_From.setValue(LocalDate.now());
                }
                Filter_DateRange_To.setValue(LocalDate.now());
            }
        }

        public class SessionRow {
            public IntegerProperty id;
            public StringProperty datepracticed;
            public StringProperty presession;
            public StringProperty rin;
            public StringProperty kyo;
            public StringProperty toh;
            public StringProperty sha;
            public StringProperty kai;
            public StringProperty jin;
            public StringProperty retsu;
            public StringProperty zai;
            public StringProperty zen;
            public StringProperty earth;
            public StringProperty air;
            public StringProperty fire;
            public StringProperty water;
            public StringProperty Void;
            public StringProperty postsession;
            public StringProperty total;

            public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu,
                              int zai, int zen, int postsession, int earth, int air, int fire, int water, int Void, int total) {
                this.id = new SimpleIntegerProperty(id);
                this.datepracticed = new SimpleStringProperty(datepracticed);
                this.presession = new SimpleStringProperty(String.valueOf(presession));
                this.rin = new SimpleStringProperty(String.valueOf(rin));
                this.kyo = new SimpleStringProperty(String.valueOf(kyo));
                this.toh = new SimpleStringProperty(String.valueOf(toh));
                this.sha = new SimpleStringProperty(String.valueOf(sha));
                this.kai = new SimpleStringProperty(String.valueOf(kai));
                this.jin = new SimpleStringProperty(String.valueOf(jin));
                this.retsu = new SimpleStringProperty(String.valueOf(retsu));
                this.zai = new SimpleStringProperty(String.valueOf(zai));
                this.zen = new SimpleStringProperty(String.valueOf(zen));
                this.earth = new SimpleStringProperty(String.valueOf(earth));
                this.air = new SimpleStringProperty(String.valueOf(air));
                this.fire = new SimpleStringProperty(String.valueOf(fire));
                this.water = new SimpleStringProperty(String.valueOf(water));
                this.Void = new SimpleStringProperty(String.valueOf(Void));
                this.postsession = new SimpleStringProperty(String.valueOf(postsession));
                this.total = new SimpleStringProperty(Util.formatdurationtoStringDecimalWithColons(new Duration(total * 1000)));
            }

            public String toString() {
                return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                        id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                        toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                        zen.getValue(), postsession.getValue(), total.getValue());
            }

            public StringProperty getDatepracticed() {
                return datepracticed;
            }
        }
        public class TotalProgressRow {
            private StringProperty name;
            private StringProperty formattedduration;

            public TotalProgressRow(String name, String formattedduration) {
                this.name = new SimpleStringProperty(name);
                this.formattedduration = new SimpleStringProperty(formattedduration);
            }
        }
    }
    public class AdvancedAmbienceEditor extends Stage implements Initializable {
        public Button RightArrow;
        public Button LeftArrow;
        public ChoiceBox<String> MeditatableSelectionBox;
        public TableView<AmbienceSong> Actual_Table;
        public TableColumn<AmbienceSong, String> Actual_NameColumn;
        public TableColumn<AmbienceSong, String> Actual_DurationColumn;
        public TextField Actual_TotalDuration;
        public Button Actual_AddButton;
        public Button Actual_RemoveButton;
        public Button Actual_PreviewButton;
        public TableView<AmbienceSong> Temp_Table;
        public TableColumn<AmbienceSong, String> Temp_NameColumn;
        public TableColumn<AmbienceSong, String> Temp_DurationColumn;
        public TextField Temp_TotalDuration;
        public Button Temp_AddButton;
        public Button Temp_RemoveButton;
        public Button Temp_PreviewButton;
        public Button SaveButton;
        public Button CloseButton;
        public Button SwitchToSimpleEditor;
        private ObservableList<AmbienceSong> actual_ambiencesonglist = FXCollections.observableArrayList();
        private ArrayList<SoundFile> actual_soundfilelist;
        private ObservableList<AmbienceSong> temp_ambiencesonglist = FXCollections.observableArrayList();
        private ArrayList<SoundFile> temp_soundfilelist;
        private AmbienceSong selected_temp_ambiencesong;
        private AmbienceSong selected_actual_ambiencesong;
        private double temptotalduration;
        private double actualtotalduration;
        private Meditatable selectedmeditatable;
        private File tempdirectory;
        private PreviewFile previewdialog;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            Temp_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            Temp_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            Actual_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            Actual_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            Temp_Table.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> tempselectionchanged(newValue));
            Actual_Table.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> actualselectionchanged(newValue));
            ObservableList<String> allnames = FXCollections.observableArrayList();
            allnames.addAll(kujiin.xml.Options.ALLNAMES);
            MeditatableSelectionBox.setItems(allnames);
            Actual_TotalDuration.setEditable(false);
            Temp_TotalDuration.setEditable(false);
        }

        public AdvancedAmbienceEditor() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> {

                });
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            MeditatableSelectionBox.setOnAction(event -> selectandloadmeditatable());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(Meditatable meditatable) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            MeditatableSelectionBox.setOnAction(event -> selectandloadmeditatable());
            MeditatableSelectionBox.getSelectionModel().select(meditatable.number);
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }

        // Transfer Methods
        // TODO Add Check Duplicates Before Moving Over (Or Ask Allow Duplicates?)
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_temp_ambiencesong != null && selectedmeditatable != null) {
                if (! Actual_Table.getItems().contains(selected_temp_ambiencesong)) {
                    int tempindex = Temp_Table.getItems().indexOf(selected_actual_ambiencesong);
                    actual_ambiencesonglist.add(temp_ambiencesonglist.get(tempindex));
                    actual_soundfilelist.add(temp_soundfilelist.get(tempindex));
                    Actual_Table.getItems().add(selected_temp_ambiencesong);
                    calculateactualtotalduration();
                }
            } else {
                if (selected_temp_ambiencesong == null) {
                    dialog_Information("Information", "Cannot Transfer", "Nothing Selected");}
                else {
                    dialog_Information("Information", "Cannot Transfer", "No Meditatable Selected");}
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_actual_ambiencesong != null && selectedmeditatable != null) {
                if (! Temp_Table.getItems().contains(selected_actual_ambiencesong)) {
                    int actualindex = Actual_Table.getItems().indexOf(selected_actual_ambiencesong);
                    temp_ambiencesonglist.add(actual_ambiencesonglist.get(actualindex));
                    temp_soundfilelist.add(actual_soundfilelist.get(actualindex));
                    Temp_Table.getItems().add(selected_actual_ambiencesong);
                    calculatetemptotalduration();
                }
            }
        }

        // Temp Ambience Methods
        public void addtotempambience(ActionEvent actionEvent) {
            if (temp_soundfilelist == null) {temp_soundfilelist = new ArrayList<>();}
            if (temp_ambiencesonglist == null) {temp_ambiencesonglist = FXCollections.observableArrayList();}
            addto(Temp_Table, temp_soundfilelist, temp_ambiencesonglist);}
        public void removefromtempambience(ActionEvent actionEvent) {removefrom(Temp_Table, temp_soundfilelist, temp_ambiencesonglist);}
        public void previewtempambience(ActionEvent actionEvent) {preview(selected_temp_ambiencesong);}
        public void tempselectionchanged(AmbienceSong ambiencesong) {
            selected_temp_ambiencesong = ambiencesong;
        }
        public void calculatetemptotalduration() {
            temptotalduration = 0.0;
            for (AmbienceSong i : Temp_Table.getItems()) {
                temptotalduration += i.getDuration();
            }
            Temp_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(temptotalduration), Temp_TotalDuration.getLayoutBounds().getWidth()));
        }
        public void deletetempambiencefromdirectory() {
            try {FileUtils.cleanDirectory(tempdirectory);} catch (IOException ignored) {}
        }

        // Actual Ambience Methods
        public void addtoactualambience(ActionEvent actionEvent) {
            if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
            if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
            addto(Actual_Table, actual_soundfilelist, actual_ambiencesonglist);}
        public void removeactualambience(ActionEvent actionEvent) {removefrom(Actual_Table, actual_soundfilelist, actual_ambiencesonglist);}
        public void previewactualambience(ActionEvent actionEvent) {preview(selected_actual_ambiencesong);}
        public void actualselectionchanged(AmbienceSong ambiencesong) {selected_actual_ambiencesong = ambiencesong;}
        public void calculateactualtotalduration() {
            actualtotalduration = 0.0;
            for (AmbienceSong i : Actual_Table.getItems()) {
                actualtotalduration += i.getDuration();
            }
            Actual_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(actualtotalduration), Actual_TotalDuration.getLayoutBounds().getWidth()));
        }

        // Table Methods
        public void selectandloadmeditatable() {
            int index = MeditatableSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
                else {actual_ambiencesonglist.clear();}
                if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
                else {actual_soundfilelist.clear();}
                Actual_Table.getItems().clear();
                selectedmeditatable = Session.getAllMeditatables().get(index);
                if (populateactualambiencetable()) {Actual_Table.setItems(actual_ambiencesonglist);}
                calculateactualtotalduration();
            }
        }
        private void addto(TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
            List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    SoundFile soundFile = new SoundFile(i);
                    if (soundFile.isValid()) {
                        addandcalculateduration(soundFile, table, soundfilelist, songlist);
                    } else {notvalidfilenames.add(i);}
                }
                if (notvalidfilenames.size() > 0) {
                    dialog_Information("Files Couldn't Be Added", "These Files Couldn't Be Added", notvalidfilenames.toString());}
            }
        }
        public void addandcalculateduration(SoundFile soundFile, TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
            MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
            calculatedurationplayer.setOnReady(() -> {
                soundFile.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
                calculatedurationplayer.dispose();
                soundfilelist.add(soundFile);
                AmbienceSong tempsong = new AmbienceSong(soundFile);
                songlist.add(tempsong);
                table.getItems().add(tempsong);
                calculateactualtotalduration();
                calculatetemptotalduration();
            });
        }
        private void removefrom(TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
            int index = table.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                SoundFile soundFile = soundfilelist.get(index);
                if (dialog_YesNoConfirmation("Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                table.getItems().remove(index);
                soundfilelist.remove(index);
                songlist.remove(index);
                calculateactualtotalduration();
                calculatetemptotalduration();
            }
            else {
                dialog_Information("Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        private void preview(AmbienceSong selectedsong) {
            if (selectedsong != null && selectedsong.getFile() != null && selectedsong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(selectedsong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }
        private boolean populateactualambiencetable() {
            actual_ambiencesonglist.clear();
            if (selectedmeditatable != null) {
                try {
                    if (selectedmeditatable.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedmeditatable.getAmbience().getAmbience()) {
                        actual_soundfilelist.add(i);
                        actual_ambiencesonglist.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog_Information("Information", selectedmeditatable + " Has No Ambience", "Please Add Ambience To " + selectedmeditatable);
                    return false;
                }
            } else {
                dialog_Information("Information", "No Meditatable Loaded", "Load A Meditatable's Ambience First");
                return false;
            }
        }

        // Dialog Methods
        public boolean unsavedchanges() {
            if (MeditatableSelectionBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
            try {
                List<SoundFile> ambiencelist = selectedmeditatable.getAmbience().getAmbience();
                if (actual_soundfilelist.size() != ambiencelist.size()) {return true;}
                for (SoundFile x : actual_soundfilelist) {
                    if (! ambiencelist.contains(x)) {return true;}
                }
                return false;
            } catch (NullPointerException ignored) {return false;}
        }
        public void save(ActionEvent actionEvent) {
            int index = MeditatableSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : actual_soundfilelist) {
                    if (! selectedmeditatable.getAmbience().ambienceexistsinActual(i)) {selectedmeditatable.getAmbience().actual_add(i);}
                }
                Ambiences.setmeditatableAmbience(selectedmeditatable.number, selectedmeditatable.getAmbience());
                Ambiences.marshall();
                dialog_Information("Saved", "Ambience Saved To " + selectedmeditatable, "");
            } else {dialog_Information("Cannot Save", "No Meditatable Selected", "Cannot Save");}
        }
        public void closebuttonpressed(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (dialog_YesNoConfirmation("Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Closing?")) {save(null);}
                else {return;}
            }
            close();
        }
        public void switchtosimple(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (dialog_YesNoConfirmation("Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Switching To Simple Mode?")) {save(null);}
            }
            this.close();
            deletetempambiencefromdirectory();
            if (selected_temp_ambiencesong != null && selectedmeditatable != null) {
                new SimpleAmbienceEditor(selectedmeditatable).show();
            } else {new SimpleAmbienceEditor().show();}
        }
    }
    public class SimpleAmbienceEditor extends Stage implements Initializable {
        public TableView<AmbienceSong> AmbienceTable;
        public TableColumn<AmbienceSong, String> NameColumn;
        public TableColumn<AmbienceSong, String> DurationColumn;
        public ChoiceBox<String> MeditatableChoiceBox;
        public Button SaveButton;
        public Button CloseButton;
        public Button AddButton;
        public Button RemoveButton;
        public Button PreviewButton;
        public TextField TotalDuration;
        public Button AdvancedButton;
        private ObservableList<AmbienceSong> AmbienceList;
        private ArrayList<SoundFile> SoundList;
        private AmbienceSong selectedambiencesong;
        private Meditatable selectedmeditatable;
        private PreviewFile previewdialog;
        private double totalselectedduration;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            AmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> tableselectionchanged(newValue));
            ObservableList<String> allnames = FXCollections.observableArrayList();
            allnames.addAll(kujiin.xml.Options.ALLNAMES);
            MeditatableChoiceBox.setItems(allnames);
        }

        public SimpleAmbienceEditor() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Simple Ambience Editor");
            } catch (IOException ignored) {}
            MeditatableChoiceBox.setOnAction(event -> selectandloadmeditatable());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
        }
        public SimpleAmbienceEditor(Meditatable meditatable) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Simple Ambience Editor");
            } catch (IOException ignored) {}
            setOnShowing(event -> {
                MeditatableChoiceBox.getSelectionModel().select(meditatable.number);
                selectandloadmeditatable();
            });
            MeditatableChoiceBox.setOnAction(event -> selectandloadmeditatable());
        }

        // Table Methods
        public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
        public void selectandloadmeditatable() {
            int index = MeditatableChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                if (AmbienceList == null) {AmbienceList = FXCollections.observableArrayList();}
                else {AmbienceList.clear();}
                if (SoundList == null) {SoundList = new ArrayList<>();}
                else {SoundList.clear();}
                AmbienceTable.getItems().clear();
                selectedmeditatable = Session.getAllMeditatables().get(index);
                if (populateactualambiencetable()) {
                    AmbienceTable.setItems(AmbienceList);
                }
                calculatetotalduration();
            }
        }
        public void add() {
            List<File> filesselected = new FileChooser().showOpenMultipleDialog(null);
            List<File> notvalidfilenames = new ArrayList<>();
            if (filesselected == null || filesselected.size() == 0) {return;}
            for (File i : filesselected) {
                for (String x : Util.SUPPORTEDAUDIOFORMATS) {
                    if (i.getName().endsWith(x)) {
                        if (Util.audio_getduration(i) != 0.0) {AmbienceList.add(new AmbienceSong(new SoundFile(i))); break;}
                    }
                }
                if (! i.equals(AmbienceList.get(AmbienceList.size() - 1).getFile())) {notvalidfilenames.add(i);}
            }
            if (notvalidfilenames.size() > 0) {
                dialog_Information("Information", notvalidfilenames.size() + " Files Weren't Added Because They Are Unsupported", "");
            }
        }
        public void addfiles(ActionEvent actionEvent) {
            List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    SoundFile soundFile = new SoundFile(i);
                    if (soundFile.isValid()) {
                        addandcalculateduration(soundFile);
                    }
                    else {notvalidfilenames.add(i);}
                }
                if (notvalidfilenames.size() > 0) {
                    dialog_Information("Couldn't Add Files", "Supported Audio Formats: " + Util.audio_getsupportedText(), "Couldn't Add " + notvalidfilenames.size() + "Files");
                }
            }
            if (AmbienceList.size() > 0) {AmbienceTable.setItems(AmbienceList);}
        }
        public void addandcalculateduration(SoundFile soundFile) {
            MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
            calculatedurationplayer.setOnReady(() -> {
                soundFile.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
                calculatedurationplayer.dispose();
                SoundList.add(soundFile);
                AmbienceSong tempsong = new AmbienceSong(soundFile);
                AmbienceList.add(tempsong);
                AmbienceTable.getItems().add(tempsong);
                selectedmeditatable.getAmbience().actual_add(soundFile);
                calculatetotalduration();
            });
        }
        public void remove(ActionEvent actionEvent) {
            int index = AmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                SoundFile soundFile = SoundList.get(index);
                selectedmeditatable.getAmbience().actual_remove(soundFile);
                if (dialog_YesNoConfirmation("Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                AmbienceTable.getItems().remove(index);
                AmbienceList.remove(index);
                SoundList.remove(index);
                calculatetotalduration();
            }
            else {
                dialog_Information("Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        public void preview(ActionEvent actionEvent) {
            if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(selectedambiencesong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }
        public boolean populateactualambiencetable() {
            AmbienceList.clear();
            if (selectedmeditatable != null) {
                try {
                    if (selectedmeditatable.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedmeditatable.getAmbience().getAmbience()) {
                        SoundList.add(i);
                        AmbienceList.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog_Information("Information", selectedmeditatable + " Has No Ambience", "Please Add Ambience To " + selectedmeditatable);
                    return false;
                }
            } else {
                dialog_Information("Information", "No Meditatable Loaded", "Load A Meditatable's Ambience First");
                return false;
            }
        }
        public void calculatetotalduration() {
            totalselectedduration = 0.0;
            for (AmbienceSong i : AmbienceTable.getItems()) {
                totalselectedduration += i.getDuration();
            }
            TotalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(totalselectedduration), TotalDuration.getLayoutBounds().getWidth()));
        }
        public boolean unsavedchanges() {
            if (MeditatableChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
            try {
                List<SoundFile> ambiencelist = selectedmeditatable.getAmbience().getAmbience();
                if (SoundList.size() != ambiencelist.size()) {return true;}
                for (SoundFile x : SoundList) {
                    if (! ambiencelist.contains(x)) {return true;}
                }
                return false;
            } catch (NullPointerException ignored) {return false;}
        }

        // Dialog Methods
        public void advancedmode(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (dialog_YesNoConfirmation("Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Switching To Advanced Mode?")) {save(null);}
            }
            this.close();
            if (selectedmeditatable != null) {
                new AdvancedAmbienceEditor(selectedmeditatable).show();
            } else {new AdvancedAmbienceEditor().show();}
        }
        public void save(ActionEvent actionEvent) {
            int index = MeditatableChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : SoundList) {
                    if (! selectedmeditatable.getAmbience().ambienceexistsinActual(i)) {selectedmeditatable.getAmbience().actual_add(i);}
                }
                Ambiences.setmeditatableAmbience(selectedmeditatable.number, selectedmeditatable.getAmbience());
                Ambiences.marshall();
                dialog_Information("Saved", "Ambience Saved To " + selectedmeditatable, "");
            } else {
                dialog_Information("Cannot Save", "No Meditatable Selected", "Cannot Save");}
        }
        public void closedialog(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (dialog_YesNoConfirmation("Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Closing?")) {save(null);}
                else {return;}
            }
            close();
        }
    }
    public class SessionDetails extends Stage {
        public BarChart<String, java.lang.Number> SessionBarChart;
        public CategoryAxis SessionCategoryAxis;
        public NumberAxis SessionNumbersAxis;
        public TextField DatePracticedTextField;
        public TextField SessionDurationTextField;
        public Button CloseButton;
        public Label GoalsCompletedTopLabel;
        public ListView<String> GoalsCompletedListView;

        public SessionDetails() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionCompleteDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                SessionNumbersAxis.setLabel("Minutes");
                setTitle("Session Details");
                XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
                Duration totalsessionduration = new Duration(0);
                ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
                for (Meditatable i : getSession().getallitemsinSession()) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getduration().toMinutes()));
                    totalsessionduration.add(i.getduration());
                    for (Goals.Goal x : i.goals_getGoalsCompletedThisSession()) {
                        completedgoalsitems.add(String.format("%s: %s Hours Completed (%s Current)", i.name, x.getGoal_Hours(), i.getduration().toHours()));
                    }
                }
                if (completedgoalsitems.size() > 0) {
                    GoalsCompletedTopLabel.setText(completedgoalsitems.size() + " Goals Completed This Session");
                    GoalsCompletedListView.setItems(completedgoalsitems);
                }
                else {GoalsCompletedTopLabel.setText("No Goals Completed This Session");}
                SessionBarChart.getData().add(series);
                SessionBarChart.setLegendVisible(false);
                SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(totalsessionduration, SessionDurationTextField.getLayoutBounds().getWidth()));
                SessionDurationTextField.setEditable(false);
                SessionBarChart.requestFocus();
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }
        public SessionDetails(kujiin.xml.Session session) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_Individual.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                SessionNumbersAxis.setLabel("Minutes");
                setTitle("Session Details");
                DatePracticedTextField.setText(session.getDate_Practiced());
                DatePracticedTextField.setEditable(false);
                XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i < 16; i++) {
                    int duration = session.getmeditatableduration(i);
                    values.add(duration);
                    String name;
                    if (i == 0) {name = "Pre";}
                    else if (i == 15) {name = "Post";}
                    else {name = kujiin.xml.Options.ALLNAMES.get(i);}
                    series.getData().add(new XYChart.Data<>(name, duration));
                }
                SessionBarChart.getData().add(series);
                SessionBarChart.setLegendVisible(false);
                SessionNumbersAxis.setUpperBound(Util.list_getmaxintegervalue(values));
                SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(new Duration((session.getTotal_Session_Duration() * 60) * 1000), SessionDurationTextField.getLayoutBounds().getWidth()));
                SessionDurationTextField.setEditable(false);
                SessionBarChart.requestFocus();
            } catch (IOException | NullPointerException e) {new ExceptionDialog(e).showAndWait();}
        }

        public void closeDialog(ActionEvent actionEvent) {
            close();
        }

        class CompletedGoalsAtEndOfSessionBinding {
            private StringProperty meditatablename;
            private StringProperty practicedhours;
            private StringProperty goalhours;
            private StringProperty dateset;
            private IntegerProperty daysittooktocomplete;
            private StringProperty datecompleted;

            public CompletedGoalsAtEndOfSessionBinding(String meditatablename, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
                this.meditatablename = new SimpleStringProperty(meditatablename);
                this.practicedhours = new SimpleStringProperty(practicedhours);
                this.goalhours = new SimpleStringProperty(goalhours);
                this.dateset = new SimpleStringProperty(dateset);
                this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
                this.datecompleted = new SimpleStringProperty(datecompleted);
            }
        }
    }

// Table Classes
    public class AmbienceSong {
    private StringProperty name;
    private StringProperty length;
    private File file;
    private double duration;

    public AmbienceSong(SoundFile soundFile) {
        this.name = new SimpleStringProperty(soundFile.getName());
        this.file = soundFile.getFile();
        duration = soundFile.getDuration();
        this.length = new SimpleStringProperty(Util.formatdurationtoStringSpelledOut(new Duration(duration), null));
    }

    public String getName() {
        return name.getValue();
    }
    public File getFile() {
        return file;
    }
    public double getDuration() {return duration;}
}

// Boilerplate Dialogs
    public static class SimpleTextDialogWithCancelButton extends Stage {
        public Button CancelButton;
        public Label Message;
        public Label TopTitle;

        public SimpleTextDialogWithCancelButton(Options options, String titletext, String toptitletext, String message) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleTextDialogWithCancelButton.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setTitle(titletext);
            Message.setText(message);
            TopTitle.setText(toptitletext);
        }
    }
    public static class LoadingDialog extends Stage {
        public Label Message;
        public ProgressIndicator Progress;

        public LoadingDialog(Options options, String titletext, String message) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/LoadingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(Event::consume);
            } catch (IOException e) {}
            setTitle(titletext);
            Message.setText(message);
        }
    }
    public static class TaskProgressDialog extends Stage {
        public Label TopLabel;
        public Label ProgressLabel;
        public ProgressBar Progress;
        public Button CancelButton;

        public TaskProgressDialog(Options options) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ComplexTaskProgressDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                options.setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(Event::consume);
            } catch (IOException e) {}
        }

    }

}
