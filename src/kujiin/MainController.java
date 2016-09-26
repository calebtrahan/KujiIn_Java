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
import javafx.concurrent.Task;
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
import javafx.scene.paint.Paint;
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

import static kujiin.util.Util.AnswerType.YES;
// TODO Bugs To Fix
    // TODO Entrainment Always Calculates Duration Instead Of Just Using Previously Calculated XML Values
    // TODO Ambience Shuffle Algorithm Doesn't Add Last Actual Ambience File
    // TODO Preferences Dialog Doesn't Initially Populate With Options From XML (Check If It Saves As Well?)
    // TODO Find Out Why Displaying Some Dialogs Makes Root Uniconified
    // TODO Closing Reference Display With 'ESC' Is Crashing The Whole App

// TODO Test

// TODO Additional Features To Definitely Add
    // TODO Create A Custom Ambience Selection Wizard To Add Ambience Individually To Each Session Part In Session
    // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
    // TODO Exporter

// TODO Optional Additional Features
    // TODO Refactor Freq Files So There Can Be 2 or 3 Different Frequency Octaves For The Same Session Part (Use enum FreqType)
    // TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)
    // TODO Add Tooltips To Cuts Saying A One Word Brief Summary (Rin -> Strength, Kyo -> Control, Toh->Harmony)
    // TODO Put Add A Japanese Character Symbol Picture (Representing Each Cut) To Creator Cut Labels (With Tooltips Displaying Names)
    // TODO Set Font Size, So The Program Looks Universal And Text Isn't Oversized Cross-Platform

// TODO Mind Workstation
    // TODO Add Low (And Possibly Medium) Variations Of All Session Parts
    // TODO Add Ramps To Connect Low (And Possibly Medium) Variations Of Session Parts With Each Other

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
    public TextField ApproximateEndTime;
    public Button ChangeAllCutsButton;
    public TextField TotalSessionTime;
    public ComboBox<String> GoalSessionPartComboBox;
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
    private SoundFile ambiencetestsoundfile;
    private List<SoundFile> ambienceplaybackhistory = new ArrayList<>();
    private Scene Scene;
    private Stage Stage;
    private StartupChecks startupChecks;

// My Fields
    private This_Session Session;
    private Preset Preset;
    private Timeline creator_updateuitimeline;
    private Sessions Sessions;
    private SessionPart sessionsAndGoalsSelectedSessionPart;
    private Goals Goals;
    private Options Options;
    private Entrainments Entrainments;
    private Ambiences Ambiences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
    }
    public void startupchecks() {
        creation_gui_setDisable(true, "");
        startupChecks = new StartupChecks(getSession().getAllSessionParts());
        startupChecks.run();
        startupChecks.setOnRunning(event -> CreatorStatusBar.textProperty().bind(startupChecks.messageProperty()));
    }
    public void startupcheckscompleted() {
        CreatorStatusBar.textProperty().unbind();
        creation_gui_setDisable(false, "");
        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Startup Checks Completed", 3000);
    }
    public boolean cleanup() {
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
    public void menu_opensimpleambienceeditor(SessionPart sessionPart) {
        new SimpleAmbienceEditor(sessionPart).showAndWait();
    }
    public void menu_openadvancedambienceeditor() {}
    public void menu_openadvancedambienceeditor(SessionPart sessionPart) {
        AdvancedAmbienceEditor sae = new AdvancedAmbienceEditor(sessionPart);
        sae.showAndWait();
    }
    public void menu_editreferencefiles(ActionEvent actionEvent) {
        getStage().setIconified(true);
        new EditReferenceFiles(getSession().referenceType).showAndWait();
        getStage().setIconified(false);
    }
    public void menu_howtouseprogram(ActionEvent actionEvent) {}
    public void menu_aboutthisprogram(ActionEvent actionEvent) {
        for (SessionPart x : Session.getAllSessionParts()) {
            if (x instanceof Cut || x instanceof Qi_Gong) {
                Ambience ambience = x.getAmbience();
                int count = 1;
                for (int i = 0; i < 100; i++) {
                    ambiencetestsoundfile = ambience.ambiencegenerator(This_Session.AmbiencePlaybackType.SHUFFLE, ambienceplaybackhistory, ambiencetestsoundfile);
                    ambienceplaybackhistory.add(ambiencetestsoundfile);
//                System.out.println(count + ": Sound File: " + ambiencetestsoundfile.getFile().getAbsolutePath());
                    count++;
                }
            }
        }
    }
    public void menu_contactme(ActionEvent actionEvent) {}

// Presets
    public void preset_initialize() {Preset = new Preset(this);}
    public void preset_load(ActionEvent actionEvent) {
        File presetfile = Preset.open();
        if (presetfile != null && Preset.hasvalidValues()) {
            preset_changecreationvaluestopreset(Preset.gettimes());
        } else {if (presetfile != null) dialog_displayInformation("Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void preset_save(ActionEvent actionEvent) {
        ArrayList<Double> creatorvaluesinminutes = new ArrayList<>();
        boolean validsession = false;
        for (SessionPart i : getSession().getAllSessionParts()) {
            creatorvaluesinminutes.add(i.getduration().toMinutes());
            if (i.getduration().greaterThan(Duration.ZERO)) {validsession = true;}
        }
        if (validsession) {
            Preset.settimes(creatorvaluesinminutes);
            if (Preset.save()) {Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Preset Successfully Saved", 1500);}
            else {
                dialog_displayError("Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
        }
        else {
            dialog_displayInformation("Information", "Cannot Save Preset", "All Values Are 0");}
    }
    public void preset_changecreationvaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < getSession().getAllSessionParts().size(); i++) {
                getSession().getAllSessionParts().get(i).changevalue(presetvalues.get(i).intValue());
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            dialog_displayError("Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
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
            ChangeAllCutsButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
            ChangeAllElementsButton.setTooltip(new Tooltip("Change All Element Values Simultaneously"));
            LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
            SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
            ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
        } else {
            TotalSessionTime.setTooltip(null);
            ApproximateEndTime.setTooltip(null);
            ChangeAllCutsButton.setTooltip(null);
            ChangeAllElementsButton.setTooltip(null);
            LoadPresetButton.setTooltip(null);
            SavePresetButton.setTooltip(null);
            ExportButton.setTooltip(null);
        }
    }
    public void creation_gui_setDisable(boolean disabled, String msg) {
        ChangeAllCutsButton.setDisable(disabled);
        ChangeAllElementsButton.setDisable(disabled);
        LoadPresetButton.setDisable(disabled);
        SavePresetButton.setDisable(disabled);
        ApproximateEndTime.setDisable(disabled);
        TotalSessionTime.setDisable(disabled);
        PlayButton.setDisable(disabled);
        ExportButton.setDisable(disabled);
        ResetCreatorButton.setDisable(disabled);
        for (SessionPart i : getSession().getAllSessionParts()) {i.gui_setDisable(disabled);}
        if (disabled) {creator_updateuitimeline.stop();}
        else {creator_updateuitimeline.play();}
        CreatorStatusBar.setText(msg);
    }
    public void creation_gui_update() {
        boolean notallzero = false;
        try {for (Integer i : getSession().gui_getallsessionvalues()) {if (i > 0) {notallzero = true;}}} catch (NullPointerException ignored) {}
        if (notallzero) {
            Duration totalsessiontime = Duration.ZERO;
            for (SessionPart i : getSession().getAllSessionParts()) {totalsessiontime = totalsessiontime.add(i.getduration());}
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
        for (SessionPart i : Session.getAllSessionParts()) {
            if (i.hasValidValue()) {return true;}
        }
        return false;
    }
    // Utility
    public boolean creation_prechecks() {
    // TODO Check If Exporter Working/Open Here
    // Check If Valid GUI Values
        if (! creation_gui_allvaluesnotzero()) {
            dialog_displayError("Error Creating Session", "At Least One SessionPart's Value Must Not Be 0", "Cannot Create Session"); return false;}
    // Check Entrainment Ready
//        for (SessionPart i : Session.getAllSessionParts()) {if (! i.entrainment_isReady()) {
//            System.out.println(i.name + "'s Entrainment Isn't Ready");
//            dialog_displayInformation("Cannot Play Session Yet", "Still Background Checking Entrainment", "Please Try Again In A Few Moments"); return false;}}
//    // Check Ambience Ready
//        if (AmbienceSwitch.isSelected()) {
//            for (SessionPart i : Session.getAllSessionParts()) {if (! i.ambience_isReady()) {
//                dialog_displayInformation("Cannot Play Session Yet", "Still Background Checking Ambience", "Please Try Again In A Few Moments"); return false;}}
//        }

        Session.creation_populateitemsinsession();
        if (Session.player_confirmOverview()) {return true;
        } else {Session.creation_clearitemsinsession(); return false;}
        // Add Pre/Post Ramp If Duration Is Zero || Ramp Is Disabled
//    // Check Session Well Formed
//        if (! Session.creation_checksessionwellformed()) {return false;}
//    // Check Alert File Needed/Not Needed
//        boolean longsession = false;
//        for (Integer i : Session.gui_getallsessionvalues()) {if (i >= kujiin.xml.Options.DEFAULT_LONG_SESSIONPART_DURATION) {longsession = true; break;}}
//        if (longsession && ! getOptions().getSessionOptions().getAlertfunction()) {
//            switch (dialog_getAnswer("Add Alert File", null, "I've Detected A Long Session. Add Alert File In Between Session Parts?",
//                    "Add Alert File", "Continue Without Alert File", "Cancel Playback")) {
//                case YES: new SelectAlertFile().showAndWait(); break;
//                case CANCEL: return false;
//            }
//        } else if (getOptions().getSessionOptions().getAlertfunction()) {
//            switch (dialog_getAnswer("Disable Alert File", null, "I've Detected A Relatively Short Session With Alert File Enabled",
//                    "Disable Alert File", "Leave Alert File Enabled", "Cancel Playback")) {
//                case YES: getOptions().getSessionOptions().setAlertfunction(false); break;
//                case CANCEL: return false;
//            }
//        }
//    // Check Goals
//        Session.creation_checkgoals();
    }
    public void creation_util_createsession() {
        Session.creation_createsession();
        creation_gui_setDisable(Session.creatorState != This_Session.CreatorState.NOT_CREATED, "Creator Disabled While Playing A Session");
    }
    public boolean creation_cleanup() {return true;}

// Session Player Widget
    public void player_playthisession(ActionEvent actionEvent) {
        if (Session.playerUI != null && Session.playerUI.isShowing()) {return;}
        switch (Session.creatorState) {
            case CREATED:
                Session.creation_reset(false);
            case NOT_CREATED:
                if (creation_prechecks()) {creation_util_createsession();}
                if (Session.creatorState == This_Session.CreatorState.CREATED) {Session.player_openplayer();}
                else {Session.creation_reset(false);}
                break;
            default:
                break;
        }
    }
    public void player_endofsession() {
        new SessionDetails().show();
    }

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
//                            if (!Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
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
//                    Util.dialog_displayError(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
//                    // TODO Open A Browser Showing How To Install FFMPEG
//                }
//            } else if (getExporterState() == ExporterState.WORKING) {
//                Util.gui_showtimedmessageonlabel(StatusBar, "Session Currently Being Exported", 3000);
//            } else {
//                if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
//                    setExporterState(ExporterState.NOT_EXPORTED);
//                    startexport();
//                }
//            }
//        } else {Util.dialog_displayInformation(Root, "Information", "Cannot Export", "No Cuts Selected");}
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
//            dialog_displayInformation(this, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
//        } else {This_Session.exporter_deleteprevioussession();}
//        return ! currentlyexporting;
        return true;
    }

// Sessions And Goals
    public void sessionsandgoals_sessionpartselectionchanged(ActionEvent actionEvent) {
        try {
            int index = GoalSessionPartComboBox.getSelectionModel().getSelectedIndex();
            sessionsAndGoalsSelectedSessionPart = getSession().getAllSessionPartsincludingTotal().get(index);
            if (sessionsAndGoalsSelectedSessionPart instanceof Total) {PrePostSwitch.setSelected(true);}
            sessions_gui_updateui();
            goals_gui_updateui();
        } catch (NullPointerException ignored) {
            sessions_gui_resetallvalues();
            goals_gui_resetallvalues();
        }
    }
    public void sessionandgoals_forceselectsessionpart(int sessionpartindex) {
        GoalSessionPartComboBox.getSelectionModel().select(sessionpartindex);
    }
    public void sessionandgoals_forceselectsessionpart(SessionPart sessionPart) {
        GoalSessionPartComboBox.getSelectionModel().select(sessionPart.number);
    }

    // Sessions
        // GUI
    public void sessions_initialize() {
        Sessions = new Sessions(this);
        Sessions.unmarshall();
        GoalSessionPartComboBox.setItems(FXCollections.observableArrayList(getSession().getAllSessionPartsincludingTotal_Names()));
        sessions_gui_updateui();
    }
    public void sessions_gui_updateui() {
        String averagesessiondurationtext;
        String totalminutespracticedtext;
        String numberofsessionspracticedtext;
        boolean disabled;
        int selectionindex = GoalSessionPartComboBox.getSelectionModel().getSelectedIndex();
        if (selectionindex == -1 || sessionsAndGoalsSelectedSessionPart == null) {
            averagesessiondurationtext = "No Sessions";
            totalminutespracticedtext = "No Sessions";
            numberofsessionspracticedtext = "No Sessions";
            disabled = true;
        } else {
            if (sessionsAndGoalsSelectedSessionPart.sessions_getPracticedSessionCount(null) > 0) {
                averagesessiondurationtext = sessionsAndGoalsSelectedSessionPart.sessions_ui_getAverageSessionLength();
                totalminutespracticedtext = sessionsAndGoalsSelectedSessionPart.sessions_ui_getPracticedDuration();
                numberofsessionspracticedtext = sessionsAndGoalsSelectedSessionPart.sessions_ui_getPracticedSessionCount();
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
            dialog_displayInformation("No Sessions", "No Practiced Sessions", "Cannot View Sessions");
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
        for (SessionPart i : getSession().getAllSessionPartsincludingTotal()) {i.setGoalsController(Goals); i.goals_unmarshall();}
        goals_gui_updateui();
    }
    public void goals_gui_updateui() {
        boolean disabled = sessionsAndGoalsSelectedSessionPart == null || sessionsAndGoalsSelectedSessionPart.goals_getCurrent() == null;
        Tooltip goalprogresstooltip;
        String toptext;
        String newgoalbuttontext;
        String percentage;
        Double progress;
        Tooltip newgoalbuttontooltip;
        newgoalButton.setDisable(sessionsAndGoalsSelectedSessionPart == null);
        viewcurrrentgoalsButton.setDisable(disabled);
        goalsprogressbar.setDisable(disabled);
        GoalProgressPercentageLabel.setDisable(disabled);
        GoalTopLabel.setDisable(disabled);
        if (sessionsAndGoalsSelectedSessionPart == null) {
            toptext = "Goal Progress Tracker";
            goalprogresstooltip = new Tooltip("");
            percentage = "";
            progress = 0.0;
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else if (sessionsAndGoalsSelectedSessionPart.goals_getCurrent() == null || sessionsAndGoalsSelectedSessionPart.sessions_getPracticedDuration(null).lessThanOrEqualTo(Duration.ZERO)) {
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = sessionsAndGoalsSelectedSessionPart.goals_ui_getcurrentgoalpercentage(2);
            progress = sessionsAndGoalsSelectedSessionPart.goals_ui_getcurrentgoalprogress();
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + sessionsAndGoalsSelectedSessionPart.name);
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else {
            toptext = "Current Goal Progress";
            percentage = sessionsAndGoalsSelectedSessionPart.goals_ui_getcurrentgoalpercentage(2);
            progress = sessionsAndGoalsSelectedSessionPart.goals_ui_getcurrentgoalprogress();
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s",
                    Util.formatdurationtoStringSpelledOut(sessionsAndGoalsSelectedSessionPart.sessions_getPracticedDuration(null), null),
                    Util.formatdurationtoStringSpelledOut(Duration.hours(sessionsAndGoalsSelectedSessionPart.goals_getCurrent().getGoal_Hours()), null))
            );
            newgoalbuttontext = kujiin.xml.Options.GOALPACINGTEXT;
            newgoalbuttontooltip = new Tooltip("Calculate Goal Pacing For This Goal");
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
        if (sessionsAndGoalsSelectedSessionPart != null && getOptions().getProgramOptions().getTooltips()) {
            newgoalButton.setTooltip(new Tooltip("Set A New Goal"));
            viewcurrrentgoalsButton.setTooltip(new Tooltip("Edit " + sessionsAndGoalsSelectedSessionPart.name + "'s Goals"));
        }
    }
    public void goals_gui_setnewgoal(Event event) {
        if (newgoalButton.getText().equals(kujiin.xml.Options.NEWGOALTEXT)) {
            SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(sessionsAndGoalsSelectedSessionPart);
            simpleGoalSetDialog.showAndWait();
            if (simpleGoalSetDialog.shouldSetgoal()) {
                sessionsAndGoalsSelectedSessionPart.goals_add(new Goals.Goal(simpleGoalSetDialog.getNewGoalHours(), sessionsAndGoalsSelectedSessionPart));
                goals_gui_updateui();
            }
        } else if (newgoalButton.getText().equals(kujiin.xml.Options.GOALPACINGTEXT)) {
            new GoalPacingDialog().showAndWait();
        }
    }
    public void goals_gui_setnewgoal(SessionPart sessionPart) {
        SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(sessionPart);
        simpleGoalSetDialog.showAndWait();
        if (simpleGoalSetDialog.shouldSetgoal()) {
            sessionPart.goals_add(new Goals.Goal(simpleGoalSetDialog.getNewGoalHours(), sessionPart));
        }
    }

    public void goals_gui_viewcurrentgoals(Event event) {
        if (sessionsAndGoalsSelectedSessionPart.getGoals() == null || sessionsAndGoalsSelectedSessionPart.getGoals().isEmpty()) {
            dialog_displayInformation("Information", "No Goals Exist For " + sessionsAndGoalsSelectedSessionPart.name, "Please Add A Goal For " + sessionsAndGoalsSelectedSessionPart.name);
        } else {new AllSessionPartsGoalProgress().showAndWait();}
    }
    public void goals_gui_resetallvalues() {
        goalsprogressbar.setProgress(0.0);
    }
        // Util
    public boolean goals_cleanup() {Goals.marshall(); return true;}

// Gui Methods
    public void dialog_displayInformation(String titletext, String headertext, String contexttext) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setTitle(titletext);
    if (headertext != null) {a.setHeaderText(headertext);}
    a.setContentText(contexttext);
    DialogPane dialogPane = a.getDialogPane();
    dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
    a.showAndWait();
}
    public void dialog_displayError(String titletext, String headertext, String contenttext) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titletext);
        if (headertext != null) {a.setHeaderText(headertext);}
        a.setContentText(contenttext);
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        a.showAndWait();
    }
    public boolean dialog_getConfirmation(String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext) {
        String yestext;
        String notext;
        if (yesbuttontext != null) {yestext = yesbuttontext;} else {yestext = "Yes";}
        if (nobuttontext != null) {notext = nobuttontext;} else {notext = "No";}
        ButtonType yes = new ButtonType(yestext, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType(notext, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, contenttext, yes, no);
        a.setTitle(titletext);
        if (headertext != null) {a.setHeaderText(headertext);}
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        return answer.isPresent() && answer.get() == yes;
    }
    public Util.AnswerType dialog_getAnswer(String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext, String cancelbuttontext) {
        ButtonType yes;
        ButtonType no;
        ButtonType cancel;
        if (yesbuttontext != null) {yes = new ButtonType("Yes");} else {yes = new ButtonType(yesbuttontext);}
        if (nobuttontext != null) {no = new ButtonType("No");} else {no = new ButtonType(nobuttontext);}
        if (cancelbuttontext != null) {cancel = new ButtonType("Cancel");} else {cancel = new ButtonType(cancelbuttontext);}
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, contenttext, yes, no, cancel);
        a.setTitle(titletext);
        if (headertext != null) {a.setHeaderText(headertext);}
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add(getOptions().getAppearanceOptions().getThemefile());
        Optional<ButtonType> answer = a.showAndWait();
        if (answer.isPresent()) {
            if (answer.get() == yes) {return YES;}
            else if (answer.get() == no) {return Util.AnswerType.NO;}
            else if (answer.get() == cancel) {return Util.AnswerType.CANCEL;}
        }
        return Util.AnswerType.CANCEL;
    }

// Dialogs
    public class SelectAlertFile extends Stage {
        public Button HelpButton;
        public Button AcceptButton;
        public Button CancelButton;
        public CheckBox AlertFileToggleButton;
        public TextField alertfileTextField;
        public Button openFileButton;
        public Button PreviewButton;
        private File alertfile;

        public SelectAlertFile() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAlertDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setTitle("Alert File Editor");
                AlertFileToggleButton.setSelected(getOptions().getSessionOptions().getAlertfunction());
                String alertfilelocation = getOptions().getSessionOptions().getAlertfilelocation();
                if (alertfilelocation != null) {alertfile = new File(getOptions().getSessionOptions().getAlertfilelocation());}
                alertfiletoggled(null);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

    // Button Actions
        public void accept(ActionEvent actionEvent) {
            if (AlertFileToggleButton.isSelected() && alertfile == null) {
                dialog_displayInformation("No Alert File Selected", "No Alert File Selected And Alert Function Enabled", "Please Select An Alert File Or Turn Off Alert Function");
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
                PreviewFile previewFile = new PreviewFile(alertfile, MainController.this);
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
                if (duration >= kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH && duration < kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                    switch (dialog_getAnswer("Alert File Longer Than Suggested Duration", null,
                            String.format("Alert File Is %s Which Is Longer Than Suggested Duration: %s And May Break Immersion",
                            Util.formatdurationtoStringDecimalWithColons(alertfileduration),
                            Util.formatdurationtoStringDecimalWithColons(new Duration(kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000))),
                            "Use As Alert File", "Don't Use As Alert File", "Cancel"
                    )) {
                        case YES:
                            if (dialog_getConfirmation("Really Use " + alertfile.getName() + " As Your Alert File?", null, "Really Use This As Your Alert File? This May Break Immersion", null, null)) {break;}
                            else {return;}
                        case CANCEL: return;
                    }
                } else if (duration >= kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                    dialog_displayInformation("Cannot Add Alert File", null,
                            String.format("Alert File Is %s Which Is Too Long And Will Break Immersion", Util.formatdurationtoStringDecimalWithColons(alertfileduration)));
                    return;
                }
                String durationtext = Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), alertfileTextField.getLayoutBounds().getWidth());
                String text = String.format("%s (%s)", alertfile.getName(), durationtext);
                alertfileTextField.setText(text);
            } else {
                if (alertfile != null) {alertfile = null; alertfiletoggled(null);}
                alertfileTextField.setText(kujiin.xml.Options.NO_ALERT_FILE_SELECTED_TEXT);
            }
        }
        public void help(ActionEvent actionEvent) {
            dialog_displayInformation("What Is An Alert File?", "", "The 'alert file' is a short audible warning\nthat is played in between parts of the session\nto inform you it's time to player_transition to the next\npart of the session");
        }

    // Utility Methods
        public boolean fileisgood(File testfile) {
        // Test If Valid Extension
            if (! Util.audio_isValid(testfile)) {
                dialog_displayInformation("Information", "Invalid Audio Format", "Supported Audio Formats: " + Collections.singletonList(Util.SUPPORTEDAUDIOFORMATS).toString());
                return false;
            }
            Double duration = Util.audio_getduration(testfile);
            if (duration == 0.0) {
                dialog_displayInformation("Invalid File", "Invalid Audio File", "Audio File Has Zero Length Or Is Corrupt. Cannot Use As Alert File"); return false;}
            else if (duration >= (kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH) && duration < (kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH)) {
                String confirmationtext = String.format("%s Is %s Which Is Longer Than The Suggested Maximum Duration %s. This May Break Session Immersion", testfile.getName(),
                        Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000), null));
                return dialog_getConfirmation("Alert File Too Long", null, confirmationtext, "Use As Alert File", "Cancel");
            } else if (duration >= kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                String errortext = String.format("%s Is Longer Than The Maximum Allowable Duration %s", Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH * 1000), null));
                dialog_displayInformation("Invalid File", errortext, "Cannot Use As Alert File As It Will Break Immersion");
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
//                if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
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
//                        if (!Util.dialog_OKCancelConfirmation(Root, "Validation", "Alert File Is longer Than 10 Seconds",
//                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
//                                        "Really Use It?", duration))) {newfile = null;}
//                    }
//                } else {
//                    Util.dialog_displayInformation(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Util.audio_getsupportedText());
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
        public ChoiceBox<String> SessionPartNamesChoiceBox;
        public TextArea MainTextArea;
        public Button CloseButton;
        public Label StatusBar;
        public Button SaveButton;
        public Button PreviewButton;
        public RadioButton HTMLVariation;
        public RadioButton TEXTVariation;
        private File selectedfile;
        private String selectedsessionpart;
        private ArrayList<Integer> userselectedindexes;
        private This_Session.ReferenceType referenceType;

        public EditReferenceFiles(This_Session.ReferenceType referenceType) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/EditReferenceFiles.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setTitle("Reference Files Editor");
                ObservableList<String> sessionpartnames = FXCollections.observableArrayList();
                sessionpartnames.addAll(kujiin.xml.Options.ALLNAMES);
                userselectedindexes = new ArrayList<>();
                SessionPartNamesChoiceBox.setItems(sessionpartnames);
                MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
                SessionPartNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
                HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
                TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
                if (referenceType == null) {referenceType = kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION;}
                HTMLVariation.setSelected(referenceType == This_Session.ReferenceType.html);
                TEXTVariation.setSelected(referenceType == This_Session.ReferenceType.txt);
                this.referenceType = referenceType;
                PreviewButton.setDisable(true);
                SaveButton.setDisable(true);
                String referencename = referenceType.name();
                this.setOnCloseRequest(event -> {
                    if (unsavedchanges()) {
                        switch (dialog_getAnswer("Confirmation", null, SessionPartNamesChoiceBox.getValue() + " " + referencename + " Variation Has Unsaved Changes",
                                "Save And Close", "Close Without Saving", "Cancel")) {
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
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

    // Getters And Setters

    // Text Area Methods
        private boolean unsavedchanges() {
            try {
                return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
            } catch (Exception e) {return false;}
        }
        public void newsessionpartselected(ActionEvent actionEvent) {
            HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            if (userselectedindexes.size() > 0 && selectedfile != null && unsavedchanges()) {
                Util.AnswerType answerType = dialog_getAnswer("Confirmation", null, "Previous Reference File Has Unsaved Changes",
                        "Save And Close", "Close Without Saving", "Cancel");
                switch (answerType) {
                    case YES:
                        saveselectedfile(null);
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        SessionPartNamesChoiceBox.getSelectionModel().select(userselectedindexes.get(userselectedindexes.size() - 1));
                        return;
                }
            }
            loadselectedfile();
        }
        private void textchanged() {
            if (referenceType != null && selectedsessionpart != null && selectedfile != null) {
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
                Util.gui_showtimedmessageonlabel(StatusBar, "No SessionPart Selected", 3000);
            }
        }

    // Other Methods
        public void saveselectedfile(ActionEvent actionEvent) {
            if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
                String text = selectedsessionpart + "'s Reference File (" + referenceType.toString() + " Variation) Has Been Saved";
                dialog_displayInformation("Changes Saved", text, "");
            } else {
                dialog_displayError("Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
        }
        public void loadselectedfile() {
            if (SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
                selectedsessionpart = SessionPartNamesChoiceBox.getSelectionModel().getSelectedItem();
                selectnewfile();
                String contents = Util.file_getcontents(selectedfile);
                MainTextArea.setText(contents);
                PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
                StatusBar.setTextFill(Color.BLACK);
                StatusBar.setText("");
                SaveButton.setDisable(true);
            } else {
                if (SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1) {
                    dialog_displayInformation("Information", "No SessionPart Selected", "Select A SessionPart To Load");}
                else {
                    dialog_displayInformation("Information", "No Variation Selected", "Select A Variation To Load");}
                PreviewButton.setDisable(true);
            }
        }
        public void selectnewfile() {
            if (referenceType == null || selectedsessionpart == null) {selectedfile = null; return;}
            switch (referenceType) {
                case html:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html"), selectedsessionpart + ".html");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(e);}}
                    break;
                case txt:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt"), selectedsessionpart + ".txt");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(e);}}
                    break;
            }
        }
        public void htmlselected(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                Util.AnswerType answerType = dialog_getAnswer("Confirmation", null, "Previous Reference File Has Unsaved Changes",
                        "Save And Close", "Close Without Saving", "Cancel");
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
                Util.AnswerType answerType = dialog_getAnswer("Confirmation", null, "Previous Reference File Has Unsaved Changes",
                        "Save And Close", "Close Without Saving", "Cancel");
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
                    if (! dialog_getConfirmation("Confirmation", null, "Html Code In Text Area Is Not Valid HTML", "Preview Anyways", "Cancel")) {return;}
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
    public static class PreviewFile extends Stage {
        public Label CurrentTime;
        public Slider ProgressSlider;
        public Label TotalTime;
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public Slider VolumeSlider;
        public Label VolumePercentage;
        private Media Mediatopreview;
        private File Filetopreview;
        private MediaPlayer PreviewPlayer;

        public PreviewFile(File filetopreview, MainController Root) {
            if (Util.audio_isValid(filetopreview)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                    fxmlLoader.setController(this);
                    setOnHidden(event -> {
                        if (PreviewPlayer != null) {PreviewPlayer.dispose();}
                        close();
                    });
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                    Filetopreview = filetopreview;
                    setTitle("Preview: " + Filetopreview.getName().substring(0, Filetopreview.getName().lastIndexOf(".")));
                    Mediatopreview = new Media(Filetopreview.toURI().toString());
                    PreviewPlayer = new MediaPlayer(Mediatopreview);
                    PlayButton.setDisable(true);
                    PauseButton.setDisable(true);
                    StopButton.setDisable(true);
                    PreviewPlayer.setOnReady(() -> {
                        System.out.println("Preview Player Ready");
                        CurrentTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(0)));
                        TotalTime.setText(Util.formatdurationtoStringDecimalWithColons(new Duration(PreviewPlayer.getTotalDuration().toSeconds() * 1000)));
                        PlayButton.setDisable(false);
                    });
                    VolumeSlider.setValue(0.0);
                    VolumePercentage.setText("0%");
                } catch (IOException ignored) {}
            } else {Root.dialog_displayInformation("Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
        }

        public void play(ActionEvent actionEvent) {
            if (PreviewPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                System.out.println("Should Be Playing");
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
                PreviewPlayer.setOnEndOfMedia(this::reset);
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
            MediaPlayer.Status status = PreviewPlayer.getStatus();
            PlayButton.setDisable(status == MediaPlayer.Status.PLAYING);
            PauseButton.setDisable(status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.READY);
            StopButton.setDisable(status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.READY);
            ProgressSlider.setDisable(status != MediaPlayer.Status.PLAYING);
            CurrentTime.setDisable(status != MediaPlayer.Status.PLAYING);
            TotalTime.setDisable(status != MediaPlayer.Status.PLAYING);
            VolumeSlider.setDisable(status != MediaPlayer.Status.PLAYING);
            VolumePercentage.setDisable(status != MediaPlayer.Status.PLAYING);
        }
        public void reset() {
            if (Mediatopreview != null) {PreviewPlayer.stop(); PreviewPlayer.dispose();}
            TotalTime.setText("--:--");
            CurrentTime.setText("--:--");
            ProgressSlider.setValue(0);
            VolumeSlider.setValue(0);
            syncbuttons();
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
        public CheckBox FadeSwitch;
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
        public CheckBox RampSwitch;
        public Button AddNewThemeButton;
        public Label ProgramOptionsStatusBar;
        public Label DescriptionBoxTopLabel;
        public TextArea DescriptionTextField;
        public CheckBox AlertFileSwitch;
        public CheckBox PrePostRamp;
        private kujiin.xml.Options Options;
        private ArrayList<ItemWithDescription> descriptionitems = new ArrayList<>();

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
                setuplisteners();
                setuptooltips();
                setupdescriptions();
                populatefromxml();
                referencetoggle();
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

    // Setup Methods
        public void populatefromxml() {
        // Program Options
            TooltipsCheckBox.setSelected(getOptions().getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(getOptions().getProgramOptions().getHelpdialogs());
        // Session & Playback Options
            if (getOptions().getSessionOptions().getAlertfunction()) {
                AlertFileSwitch.setSelected(getOptions().hasValidAlertFile());
                if (! AlertFileSwitch.isSelected()) {getOptions().getSessionOptions().setAlertfunction(false); getOptions().getSessionOptions().setAlertfilelocation(null);}
            } else {AlertFileSwitch.setSelected(false);}
            AlertFileSwitch.setOnAction(event -> alertfiletoggled());
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            PrePostRamp.setSelected(Options.getSessionOptions().getPrepostrampenabled());
            FadeSwitch.setSelected(Options.getSessionOptions().getFadeenabled());
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeInValue.setDisable(! FadeSwitch.isSelected());
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            FadeOutValue.setDisable(! FadeSwitch.isSelected());
            EntrainmentVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getEntrainmentvolume() * 100).intValue()));
            AmbienceVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getAmbiencevolume() * 100).intValue()));
        // Appearance Options
            populateappearancecheckbox();
        }
        public void setuptooltips() {
            TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
            HelpDialogsCheckBox.setTooltip(new Tooltip("Display Help Dialogs"));
            AlertFileSwitch.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
            RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
            FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
            FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
            EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
            AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
            DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
            DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
        }
        public void setuplisteners() {
            Util.custom_textfield_double(FadeInValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
            Util.custom_textfield_double(FadeOutValue, 0.0, kujiin.xml.Options.FADE_VALUE_MAX_DURATION, 1, 1);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            FadeSwitch.setOnAction(event -> togglefade());
            RampSwitch.setOnAction(event -> toggleramp());
            PrePostRamp.setOnAction(event -> toggleprepostramp());
            CloseButton.setOnAction(event -> close());
            ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
            ProgramThemeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectnewtheme());
        }

    // Description Box Methods
        public void setupdescriptions() {
            descriptionitems.add(new ItemWithDescription("Tool Tips Checkbox", "Display/Don't Display Description Messages When Hovering Over Program Controls"));
            descriptionitems.add(new ItemWithDescription("Help Dialogs Checkbox", "Display/Don't Display Additional Dialogs Explaining Various Features Of The Program"));
            descriptionitems.add(new ItemWithDescription("Fade Checkbox", "Fade In/Out Volume Of Each Session Part To Make For A Smoother Playback Experience"));
            descriptionitems.add(new ItemWithDescription("Fade In", "Seconds To Fade In From Silent Into Each Session Part"));
            descriptionitems.add(new ItemWithDescription("Fade Out", "Seconds To Fade Out To Silent Into Each Session Part"));
            descriptionitems.add(new ItemWithDescription("Entrainment Volume", "Default Volume Percentage For Entrainment\n(Can Be Adjusted In Session)"));
            descriptionitems.add(new ItemWithDescription("Ambience Volume", "Default Volume Percentage For Ambience\n(Can Be Adjusted In Session)"));
            descriptionitems.add(new ItemWithDescription("Alert File", "An Alert File Is An Optional Sound File Played In Between Session Elements"));
            descriptionitems.add(new ItemWithDescription("Display Reference", "Default To Display/Don't Display Reference Files During Session Playback\n(Can Be Changed In Session)"));
            descriptionitems.add(new ItemWithDescription("Ramp Checkbox", "Enable/Disable A Ramp In Session Parts To Smooth Mental Transition"));
            descriptionitems.add(new ItemWithDescription("Pre/Post Ramp Checkbox", "Add A Ramp For Pre And Post Even If They Are Not In Session"));
            descriptionitems.add(new ItemWithDescription("Delete Session Button", "This Button Will Permanently Delete All Session Progress And Reset All Cut/Elements Progress"));
            descriptionitems.add(new ItemWithDescription("Delete Goal Button", "This Button Will Permanently Delete All Current And Completed Goals"));
            descriptionitems.add(new ItemWithDescription("Appearance Selection", "List Of The Available Appearance Themes For The Program"));
            descriptionitems.add(new ItemWithDescription("Add New Theme Button", "Add A New Theme To The List Of Available Themes"));
            TooltipsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(0));
            TooltipsCheckBox.setOnMouseExited(event -> cleardescription());
            HelpDialogsCheckBox.setOnMouseEntered(event -> populatedescriptionbox(1));
            HelpDialogsCheckBox.setOnMouseExited(event -> cleardescription());
            FadeSwitch.setOnMouseEntered(event -> populatedescriptionbox(2));
            FadeSwitch.setOnMouseExited(event -> cleardescription());
            FadeInValue.setOnMouseEntered(event -> populatedescriptionbox(3));
            FadeInValue.setOnMouseExited(event -> cleardescription());
            FadeOutValue.setOnMouseEntered(event -> populatedescriptionbox(4));
            FadeOutValue.setOnMouseExited(event -> cleardescription());
            EntrainmentVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(5));
            EntrainmentVolumePercentage.setOnMouseExited(event -> cleardescription());
            AmbienceVolumePercentage.setOnMouseEntered(event -> populatedescriptionbox(6));
            AmbienceVolumePercentage.setOnMouseExited(event -> cleardescription());
            AlertFileSwitch.setOnMouseEntered(event -> populatedescriptionbox(6));
            AlertFileSwitch.setOnMouseExited(event -> cleardescription());
            ReferenceSwitch.setOnMouseEntered(event -> populatedescriptionbox(8));
            ReferenceSwitch.setOnMouseExited(event -> cleardescription());
            RampSwitch.setOnMouseEntered(event -> populatedescriptionbox(9));
            RampSwitch.setOnMouseExited(event -> cleardescription());
            PrePostRamp.setOnMouseEntered(event -> populatedescriptionbox(10));
            PrePostRamp.setOnMouseExited(event -> cleardescription());
            DeleteAllSessionsProgressButton.setOnMouseEntered(event -> populatedescriptionbox(11));
            DeleteAllSessionsProgressButton.setOnMouseExited(event -> cleardescription());
            DeleteAllGoalsButton.setOnMouseEntered(event -> populatedescriptionbox(12));
            DeleteAllGoalsButton.setOnMouseExited(event -> cleardescription());
            ProgramThemeChoiceBox.setOnMouseEntered(event -> populatedescriptionbox(13));
            ProgramThemeChoiceBox.setOnMouseExited(event -> cleardescription());
            AddNewThemeButton.setOnMouseEntered(event -> populatedescriptionbox(14));
            AddNewThemeButton.setOnMouseExited(event -> cleardescription());
        }
        public void populatedescriptionbox(int index) {
            ItemWithDescription item = descriptionitems.get(index);
            DescriptionBoxTopLabel.setText(item.getName());
            DescriptionTextField.setText(item.getDescription());
        }
        public void cleardescription() {
            DescriptionBoxTopLabel.setText("Description");
            DescriptionTextField.setText("");
        }

    // Alert File Methods
        public void alertfiletoggled() {
            if (AlertFileSwitch.isSelected()) {
                SelectAlertFile selectAlertFile = new SelectAlertFile();
                selectAlertFile.showAndWait();
                AlertFileSwitch.setSelected(Options.getSessionOptions().getAlertfunction() && Options.hasValidAlertFile());
            }
        }

    // Reference Methods
        public void referencetoggle() {
            Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
            if (ReferenceSwitch.isSelected()) {
                This_Session.SelectReferenceType selectReferenceType = new This_Session.SelectReferenceType(MainController.this);
                selectReferenceType.showAndWait();
                if (selectReferenceType.getResult()) {
                    Options.getSessionOptions().setReferencetype(selectReferenceType.getReferenceType());
                    Options.getSessionOptions().setReferencefullscreen(selectReferenceType.getFullScreen());
                }
            }
        }

    // Ramp Methods
        public void toggleramp() {
            PrePostRamp.setSelected(RampSwitch.isSelected());
            Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
            if (RampSwitch.isSelected()) {toggleprepostramp();}
        }
        public void toggleprepostramp() {
            Options.getSessionOptions().setPrepostrampenabled(PrePostSwitch.isSelected());
        }

    // Fade Methods
        public void togglefade() {
            Options.getSessionOptions().setFadeenabled(FadeSwitch.isSelected());
            FadeInValue.setDisable(! FadeSwitch.isSelected());
            FadeOutValue.setDisable(! FadeSwitch.isSelected());
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
            if (dialog_getConfirmation("Reset To Defaults", null, "Reset All Values To Defaults? You Will Lose Any Unsaved Changes", "Reset", "Cancel")) {
                Options.resettodefaults();
                populatefromxml();
            }
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (dialog_getConfirmation("Confirmation", null, "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Delete?", "Cancel")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {dialog_displayError("Error", "Couldn't Delete Sessions File", "Check File Permissions For: " + kujiin.xml.Options.SESSIONSXMLFILE.getAbsolutePath());}
                else {dialog_displayInformation("Success", null, "Successfully Delete Sessions And Reset All Progress");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (dialog_getConfirmation("Confirmation", null, "This Will Permanently And Irreversible Delete All Goals Completed And Current", "Delete", "Cancel")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {dialog_displayError("Error", "Couldn't Delete Goals File", "Check File Permissions For: " + kujiin.xml.Options.GOALSXMLFILE.getAbsolutePath());}
                else {dialog_displayInformation("Success", null, "Successfully Deleted All Goals");}
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
            Options.marshall();
            super.close();
        }

        class ItemWithDescription {
            private final String name;
            private final String description;

            public ItemWithDescription(String name, String description) {
                this.name = name + " Description";
                this.description = description;
            }

            public String getName() {
                return name;
            }
            public String getDescription() {
                return description;
            }
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
    public class AllSessionPartsGoalProgress extends Stage {
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

        public AllSessionPartsGoalProgress() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AllSessionPartsGoalProgress.fxml"));
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
                if (sessionsAndGoalsSelectedSessionPart != null) {GoalsTable.getSelectionModel().select(sessionsAndGoalsSelectedSessionPart.number);}
                this.setOnCloseRequest(event -> {
                    ArrayList<SessionPart> sessionpartsmissingcurrentgoals = getSession().getAllSessionPartsincludingTotal().stream().filter(i -> i.goals_getCurrent() == null).collect(Collectors.toCollection(ArrayList::new));
                    if (sessionpartsmissingcurrentgoals.size() > 0) {
                        if (! dialog_getConfirmation("Confirmation", null, "Missing Current Goals For " + sessionpartsmissingcurrentgoals.size() + " Session Parts. Close Without Setting Goals", "Close", "Cancel")) {
                            event.consume();
                        }
                    }
                });
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        public void populatetable() {
            allgoalsdetails.clear();
            for (SessionPart i : getSession().getAllSessionPartsincludingTotal()) {
                Duration practicedtime = i.sessions_getPracticedDuration(false);
                String practicedtext;
                if (practicedtime.lessThanOrEqualTo(Duration.ZERO)) {practicedtext = "No Practiced Time";}
                else {practicedtext = Util.formatdurationtoStringSpelledOut(i.sessions_getPracticedDuration(false), null);}
                String currentgoaltime;
                String percentcompleted ;
                if (i.goals_ui_currentgoalisset()) {
                    currentgoaltime = i.goals_ui_getcurrentgoalDuration(null);
                    percentcompleted = i.goals_ui_getcurrentgoalpercentage(0);
                } else  {
                    currentgoaltime = "No Goal Set";
                    percentcompleted = "No Goal Set";
                }
                allgoalsdetails.add(new GoalProgressBinding(i.name, practicedtext, currentgoaltime, percentcompleted, i.goals_getCompletedCount()));
            }
            GoalsTable.setItems(allgoalsdetails);
        }
        public void newrowselected() {
            if (GoalsTable.getSelectionModel().getSelectedIndex() == -1) {
                sessionsAndGoalsSelectedSessionPart = null;}
            else {
                sessionsAndGoalsSelectedSessionPart = getSession().getAllSessionPartsincludingTotal().get(GoalsTable.getSelectionModel().getSelectedIndex());}
            if (sessionsAndGoalsSelectedSessionPart == null) {
                SetCurrentGoalButton.setDisable(true);
                ViewCompletedGoalsButton.setDisable(true);
            } else {
                SetCurrentGoalButton.setDisable(false);
                if (sessionsAndGoalsSelectedSessionPart.goals_getCurrent() == null) {SetCurrentGoalButton.setText(setgoaltext);}
                else {SetCurrentGoalButton.setText(goalpacingtext);}
                ViewCompletedGoalsButton.setDisable(sessionsAndGoalsSelectedSessionPart.goals_getCompletedCount() == 0);
            }
        }
        public void setcurrentgoal(ActionEvent actionEvent) {
            if (sessionsAndGoalsSelectedSessionPart != null) {
                SimpleGoalSetDialog setDialog = new SimpleGoalSetDialog(sessionsAndGoalsSelectedSessionPart);
                setDialog.showAndWait();
                if (setDialog.shouldSetgoal()) {
                    sessionsAndGoalsSelectedSessionPart.goals_add(new Goals.Goal(setDialog.getNewGoalHours(), sessionsAndGoalsSelectedSessionPart));
                    populatetable();
                }
            }
        }
        public void viewcompletedgoals(ActionEvent actionEvent) {
            if (sessionsAndGoalsSelectedSessionPart != null) {

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
        public Label TopLabel;
        public TextField HoursSpinner;
        public TextField MinutesSpinner;
        public Label StatusBar;
        public Button AcceptButton;
        public Button CancelButton;
        private boolean setgoal = false;
        private Duration practicedduration;

        public SimpleGoalSetDialog(SessionPart sessionPart) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SetGoalDialog_Simple.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setResizable(false);
                setTitle("Set A New Goal For " + sessionPart.name);
                practicedduration = sessionPart.sessions_getPracticedDuration(false);
                HoursSpinner.setText(String.valueOf((int) practicedduration.toMinutes() / 60));
                MinutesSpinner.setText(String.valueOf((int) practicedduration.toMinutes() % 60));
                Util.custom_textfield_integer(HoursSpinner, 0, Integer.MAX_VALUE, 1);
                Util.custom_textfield_integer(MinutesSpinner, 0, 59, 5);
                HoursSpinner.textProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                MinutesSpinner.textProperty().addListener((observable, oldValue, newValue) -> checkvalue());
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        private Duration getPotentialGoalDuration() {
            try {return Duration.hours(Integer.parseInt(HoursSpinner.getText())).add(Duration.minutes(Integer.parseInt(MinutesSpinner.getText())));}
            catch (NumberFormatException | NullPointerException ignored) {return Duration.ZERO;}
        }
        private void checkvalue() {
            Paint color;
            String text;
            boolean disabled;
            if (getPotentialGoalDuration().lessThanOrEqualTo(practicedduration)) {
                color = Color.RED;
                text = "Goal Value Less Than Practiced";
                disabled = true;
            } else {
                color = Color.BLACK;
                text = "";
                disabled = false;
            }
            StatusBar.setTextFill(color);
            StatusBar.setText(text);
            AcceptButton.setDisable(disabled);
        }

        public boolean shouldSetgoal() {
            return setgoal;
        }
        public Double getNewGoalHours() {
            try {
                Duration duration = Duration.hours(Double.parseDouble(HoursSpinner.getText())).add(Duration.minutes(Double.parseDouble(MinutesSpinner.getText())));
                return duration.toHours();
            } catch (NullPointerException e) {return null;}
        }

        public void accept(ActionEvent actionEvent) {
            setgoal = true;
            close();
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
        private Duration practicedduration;
        private Duration goalduration;
        private Duration durationleft;

        public GoalPacingDialog() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/GoalPacingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                setResizable(false);
                setTitle("Goal Pacing");
                practicedduration = sessionsAndGoalsSelectedSessionPart.sessions_getPracticedDuration(false);
                goalduration = Duration.hours(sessionsAndGoalsSelectedSessionPart.goals_getCurrent().getGoal_Hours());
                GoalDuration.setText(Util.formatdurationtoStringSpelledOut(goalduration, GoalDuration.getLayoutBounds().getWidth()));
                TotalPracticedTime.setText(Util.formatdurationtoStringSpelledOut(practicedduration, TotalPracticedTime.getLayoutBounds().getWidth()));
                durationleft = goalduration.subtract(practicedduration);
                GoalTimeLeft.setText(Util.formatdurationtoStringSpelledOut(durationleft, GoalTimeLeft.getLayoutBounds().getWidth()));
                Util.custom_spinner_integer(PracticeDays, 1, Integer.MAX_VALUE, 1, 1, false);
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> calculate());
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
                TopLabel.setText("Goal Pacing For " + sessionsAndGoalsSelectedSessionPart.name + " Current Goal");
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        // Other Methods
        public void calculate() {
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = (float) durationleft.toHours() / days.floatValue();
            int minsaday = (int) Duration.hours(hourstopractice.doubleValue()).toMinutes();
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
        // Selected Session Parts
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
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
        }

        public CheckBox getcheckbox(int sessionpartindex) {
            switch (sessionpartindex) {
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
            for (SessionPart i : getSession().getAllSessionPartsincludingTotal()) {
                if (! (i instanceof Total)) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.sessions_getPracticedDuration(false).toHours()));
                    piecesofthepie.add(new PieChart.Data(i.getNameForChart(), i.sessions_getPracticedDuration(false).toHours()));
                }
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.formatdurationtoStringDecimalWithColons(i.sessions_getPracticedDuration(false))));
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
                                    if (i.getsessionpartduration(j) <= Integer.parseInt(Filter_ThresholdMinutesTextField.getText())) {validsession = false;}
                                } catch (NumberFormatException | NullPointerException ignored) {validsession = i.getsessionpartduration(j) > 0;}
                            } else {if (i.getsessionpartduration(j) == 0) {validsession = false;}}
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
        public ChoiceBox<String> SessionPartSelectionBox;
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
        private Duration temptotalduration;
        private Duration actualtotalduration;
        private SessionPart selectedsessionpart;
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
            SessionPartSelectionBox.setItems(allnames);
            Actual_TotalDuration.setEditable(false);
            Temp_TotalDuration.setEditable(false);
        }

        public AdvancedAmbienceEditor() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxmldd/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> {
                    if (unsavedchanges()) {
                        switch (dialog_getAnswer("Save Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save And Close", "Close", "Cancel")) {
                            case YES:
                                save(null);
                                break;
                            case CANCEL: event.consume();
                        }
                    }
                });

            } catch (IOException e) {new org.controlsfx.dialog.ExceptionDialog(e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            SessionPartSelectionBox.setOnAction(event -> selectandloadsessionpart());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(SessionPart sessionPart) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            SessionPartSelectionBox.setOnAction(event -> selectandloadsessionpart());
            SessionPartSelectionBox.getSelectionModel().select(sessionPart.number);
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }

        // Transfer Methods
        // TODO Add Check Duplicates Before Moving Over (Or Ask Allow Duplicates?)
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_temp_ambiencesong != null && selectedsessionpart != null) {
                if (! Actual_Table.getItems().contains(selected_temp_ambiencesong)) {
                    int tempindex = Temp_Table.getItems().indexOf(selected_actual_ambiencesong);
                    actual_ambiencesonglist.add(temp_ambiencesonglist.get(tempindex));
                    actual_soundfilelist.add(temp_soundfilelist.get(tempindex));
                    Actual_Table.getItems().add(selected_temp_ambiencesong);
                    calculateactualtotalduration();
                }
            } else {
                if (selected_temp_ambiencesong == null) {
                    dialog_displayInformation("Information", "Cannot Transfer", "Nothing Selected");}
                else {
                    dialog_displayInformation("Information", "Cannot Transfer", "No SessionPart Selected");}
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_actual_ambiencesong != null && selectedsessionpart != null) {
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
            temptotalduration = Duration.ZERO;
            for (AmbienceSong i : Temp_Table.getItems()) {temptotalduration = temptotalduration.add(Duration.millis(i.getDuration()));}
            Temp_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(temptotalduration, Temp_TotalDuration.getLayoutBounds().getWidth()));
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
            actualtotalduration = Duration.ZERO;
            for (AmbienceSong i : Actual_Table.getItems()) {actualtotalduration = actualtotalduration.add(Duration.millis(i.getDuration()));}
            Actual_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(actualtotalduration, Actual_TotalDuration.getLayoutBounds().getWidth()));
        }

        // Table Methods
        public void selectandloadsessionpart() {
            int index = SessionPartSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
                else {actual_ambiencesonglist.clear();}
                if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
                else {actual_soundfilelist.clear();}
                Actual_Table.getItems().clear();
                selectedsessionpart = Session.getAllSessionParts().get(index);
                if (populateactualambiencetable()) {Actual_Table.setItems(actual_ambiencesonglist);}
                calculateactualtotalduration();
            }
        }
        private void addto(TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
            List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
            if (files == null || files.isEmpty()) {return;}
            int notvalidfilecount = 0;
            int validfilecount = 0;
            for (File t : files) {
                if (! Util.audio_isValid(t)) {notvalidfilecount++;}
                else {validfilecount++;}
            }
            if (validfilecount > 0) {
                if (Util.list_hasduplicates(files)) {
                    if (! dialog_getConfirmation("Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard")) {
                        files = Util.list_removeduplicates(files);
                    }
                }
            }
            for (File i : files) {
                SoundFile soundFile = new SoundFile(i);
                addandcalculateduration(soundFile, table, soundfilelist, songlist);
            }
            if (notvalidfilecount > 0) {dialog_displayInformation("Information", notvalidfilecount + " Files Were Not Valid And Weren't Added", "");}
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
                switch (dialog_getAnswer("Removing File", null, "Removing Ambience From Table. Also Delete File " + soundFile.getName() + " From Disk? (This Cannot Be Undone)",
                        "Remove And Delete File", "Remove But Keep File", "Cancel")) {
                    case YES: soundFile.getFile().delete(); break;
                    case CANCEL: return;
                }
                table.getItems().remove(index);
                soundfilelist.remove(index);
                songlist.remove(index);
                calculateactualtotalduration();
                calculatetemptotalduration();
            }
            else {
                dialog_displayInformation("Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        private void preview(AmbienceSong selectedsong) {
            if (selectedsong != null && selectedsong.getFile() != null && selectedsong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(selectedsong.getFile(), MainController.this);
                    previewdialog.showAndWait();
                }
            }
        }
        private boolean populateactualambiencetable() {
            actual_ambiencesonglist.clear();
            if (selectedsessionpart != null) {
                try {
                    if (selectedsessionpart.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedsessionpart.getAmbience().getAmbience()) {
                        actual_soundfilelist.add(i);
                        actual_ambiencesonglist.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog_displayInformation("Information", selectedsessionpart + " Has No Ambience", "Please Add Ambience To " + selectedsessionpart);
                    return false;
                }
            } else {
                dialog_displayInformation("Information", "No SessionPart Loaded", "Load A SessionPart's Ambience First");
                return false;
            }
        }

        // Dialog Methods
        public boolean unsavedchanges() {
            if (SessionPartSelectionBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
            try {
                List<SoundFile> ambiencelist = selectedsessionpart.getAmbience().getAmbience();
                if (actual_soundfilelist.size() != ambiencelist.size()) {return true;}
                for (SoundFile x : actual_soundfilelist) {
                    if (! ambiencelist.contains(x)) {return true;}
                }
                return false;
            } catch (NullPointerException ignored) {return false;}
        }
        public void save(ActionEvent actionEvent) {
            int index = SessionPartSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                actual_soundfilelist.stream().filter(i -> !selectedsessionpart.getAmbience().ambienceexistsinActual(i)).forEach(i -> selectedsessionpart.getAmbience().add(i));
                Ambiences.setsessionpartAmbience(selectedsessionpart.number, selectedsessionpart.getAmbience());
                Ambiences.marshall();
                dialog_displayInformation("Saved", "Ambience Saved To " + selectedsessionpart, "");
            } else {
                dialog_displayInformation("Cannot Save", "No SessionPart Selected", "Cannot Save");}
        }
        public void switchtosimple(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                switch (dialog_getAnswer("Switch To Simple Mode", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save Changes", "Switch Without Saving", "Cancel")) {
                    case YES: save(null); break;
                    case CANCEL: return;
                }
            }
            this.close();
            deletetempambiencefromdirectory();
            if (selected_temp_ambiencesong != null && selectedsessionpart != null) {
                new SimpleAmbienceEditor(selectedsessionpart).show();
            } else {new SimpleAmbienceEditor().show();}
        }
    }
    public class SimpleAmbienceEditor extends Stage implements Initializable {
        public TableView<AmbienceSong> AmbienceTable;
        public TableColumn<AmbienceSong, String> NameColumn;
        public TableColumn<AmbienceSong, String> DurationColumn;
        public ChoiceBox<String> SessionPartChoiceBox;
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
        private SessionPart selectedsessionpart;
        private PreviewFile previewdialog;
        private Duration totalselectedduration;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            AmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> tableselectionchanged(newValue));
            ObservableList<String> allnames = FXCollections.observableArrayList();
            allnames.addAll(kujiin.xml.Options.ALLNAMES);
            SessionPartChoiceBox.setItems(allnames);
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
                setOnCloseRequest(event -> closedialog(null));
            } catch (IOException ignored) {}
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
        }
        public SimpleAmbienceEditor(SessionPart sessionPart) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Simple Ambience Editor");
                setOnCloseRequest(event -> closedialog(null));
            } catch (IOException ignored) {}
            setOnShowing(event -> {
                SessionPartChoiceBox.getSelectionModel().select(sessionPart.number);
                selectandloadsessionpart();
            });
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
        }

        // Table Methods
        public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
        public void selectandloadsessionpart() {
            int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                if (AmbienceList == null) {AmbienceList = FXCollections.observableArrayList();}
                else {AmbienceList.clear();}
                if (SoundList == null) {SoundList = new ArrayList<>();}
                else {SoundList.clear();}
                AmbienceTable.getItems().clear();
                selectedsessionpart = Session.getAllSessionParts().get(index);
                if (populateactualambiencetable()) {
                    AmbienceTable.setItems(AmbienceList);
                }
                calculatetotalduration();
            }
        }
        public void addfiles(ActionEvent actionEvent) {
            List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
            if (files == null || files.isEmpty()) {return;}
            int notvalidfilecount = 0;
            int validfilecount = 0;
            for (File t : files) {
                if (! Util.audio_isValid(t)) {notvalidfilecount++;}
                else {validfilecount++;}
            }
            if (validfilecount > 0) {
                if (Util.list_hasduplicates(files)) {
                    if (! dialog_getConfirmation("Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard")) {
                        files = Util.list_removeduplicates(files);
                    }
                }
            }
            for (File i : files) {
                SoundFile soundFile = new SoundFile(i);
                addandcalculateduration(soundFile);
            }
            if (notvalidfilecount > 0) {dialog_displayInformation("Information", notvalidfilecount + " Files Were Not Valid And Weren't Added", "");}
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
                selectedsessionpart.getAmbience().add(soundFile);
                calculatetotalduration();
            });
        }
        public void remove(ActionEvent actionEvent) {
            int index = AmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                SoundFile soundFile = SoundList.get(index);
                selectedsessionpart.getAmbience().remove(soundFile);
                if (dialog_getConfirmation("Confirmation", null, "Also Delete File " + soundFile.getName() + " From Hard Drive? This Cannot Be Undone", "Delete File", "Keep File")) {
                    if (! soundFile.getFile().delete()) {
                        dialog_displayError("Couldn't Delete", null, "Couldn't Delete " + soundFile.getFile().getAbsolutePath() + " Check File Permissions");
                    }
                }
                AmbienceTable.getItems().remove(index);
                AmbienceList.remove(index);
                SoundList.remove(index);
                calculatetotalduration();
            }
            else {
                dialog_displayInformation("Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        public void preview(ActionEvent actionEvent) {
            if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(selectedambiencesong.getFile(), MainController.this);
                    previewdialog.showAndWait();
                }
            }
        }
        public boolean populateactualambiencetable() {
            AmbienceList.clear();
            if (selectedsessionpart != null) {
                try {
                    if (selectedsessionpart.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedsessionpart.getAmbience().getAmbience()) {
                        SoundList.add(i);
                        AmbienceList.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog_displayInformation("Information", selectedsessionpart + " Has No Ambience", "Please Add Ambience To " + selectedsessionpart);
                    return false;
                }
            } else {
                dialog_displayInformation("Information", "No SessionPart Loaded", "Load A SessionPart's Ambience First");
                return false;
            }
        }
        public void calculatetotalduration() {
            totalselectedduration = Duration.ZERO;
            for (AmbienceSong i : AmbienceTable.getItems()) {
                totalselectedduration = totalselectedduration.add(Duration.millis(i.getDuration()));
            }
            TotalDuration.setText(Util.formatdurationtoStringSpelledOut(totalselectedduration, TotalDuration.getLayoutBounds().getWidth()));
        }
        public boolean unsavedchanges() {
            if (SessionPartChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
            try {
                List<SoundFile> ambiencelist = selectedsessionpart.getAmbience().getAmbience();
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
                if (dialog_getConfirmation("Unsaved Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save Changes", "Discard")) {save(null);}
            }
            this.close();
            if (selectedsessionpart != null) {
                new AdvancedAmbienceEditor(selectedsessionpart).show();
            } else {new AdvancedAmbienceEditor().show();}
        }
        public void save(ActionEvent actionEvent) {
            int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : SoundList) {
                    if (! selectedsessionpart.getAmbience().ambienceexistsinActual(i)) {
                        selectedsessionpart.getAmbience().add(i);}
                }
                Ambiences.setsessionpartAmbience(selectedsessionpart.number, selectedsessionpart.getAmbience());
                Ambiences.marshall();
                dialog_displayInformation("Saved", "Ambience Saved To " + selectedsessionpart, "");
            } else {
                dialog_displayInformation("Cannot Save", "No SessionPart Selected", "Cannot Save");}
        }
        public void closedialog(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                switch (dialog_getAnswer("Unsaved Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save", "Discard", "Cancel")) {
                    case YES: save(null);
                    case NO: close(); break;
                }
            } else {close();}
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
        public TextField MostProgressTextField;
        public TextField AverageDurationTextField;

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
                Duration highestduration = Duration.ZERO;
                ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
                for (SessionPart i : getSession().getallitemsinSession()) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getduration().toMinutes()));
                    totalsessionduration = totalsessionduration.add(i.getduration());
                    if (i.getduration().greaterThan(highestduration)) {highestduration = i.getduration();}
                    completedgoalsitems.addAll(i.getGoalscompletedthissession().stream().map(x -> String.format("%s: %s Hours Completed (%s Current)", i.name, x.getGoal_Hours(), i.getduration().toHours())).collect(Collectors.toList()));
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
                AverageDurationTextField.setText(Util.formatdurationtoStringSpelledOut(Duration.millis(highestduration.toMillis() / getSession().getallitemsinSession().size()), AverageSessionDuration.getLayoutBounds().getWidth()));
                MostProgressTextField.setText(Util.formatdurationtoStringSpelledOut(highestduration, MostProgressTextField.getLayoutBounds().getWidth()));
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
                    int duration = session.getsessionpartduration(i);
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
            private StringProperty sessionpartname;
            private StringProperty practicedhours;
            private StringProperty goalhours;
            private StringProperty dateset;
            private IntegerProperty daysittooktocomplete;

            public CompletedGoalsAtEndOfSessionBinding(String sessionpartname, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
                this.sessionpartname = new SimpleStringProperty(sessionpartname);
                this.practicedhours = new SimpleStringProperty(practicedhours);
                this.goalhours = new SimpleStringProperty(goalhours);
                this.dateset = new SimpleStringProperty(dateset);
                this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
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

// Startup
    class StartupChecks extends Task {
        private SessionPart selectedsessionpart;
        private Entrainment selectedentrainment;
        private Ambience selectedambience;
        private List<SessionPart> sessionPartList;
        private final int[] startupcheck_count = {0, 0, 0};
        private MediaPlayer startupcheckplayer;
        private ArrayList<SessionPart> partswithnoambience = new ArrayList<>();
        private ArrayList<SessionPart> partswithmissingentrainment = new ArrayList<>();
        private boolean firstcall = true;
        private final double[] workcount = {0, 0};

        public StartupChecks(List<SessionPart> allsessionparts) {
            sessionPartList = allsessionparts;
        }

        // Getters And Setters
        public ArrayList<SessionPart> getPartswithnoambience() {
            return partswithnoambience;
        }
        public ArrayList<SessionPart> getPartswithmissingentrainment() {
            return partswithmissingentrainment;
        }

        // Method Overrides
        @Override
        protected Object call() throws Exception {
            if (firstcall) {populateambiencefromfiles(); calculatetotalworktodo(); firstcall = false;}
            if (selectedsessionpart == null) {selectedsessionpart = getnextsessionpart();}
            File file = null;
            SoundFile soundFile = null;
            try {
                file = getnextentrainmentfile();
                soundFile = getnextentraimentsoundfile();
                if (soundFile == null) {soundFile = new SoundFile(file);}
            } catch (IndexOutOfBoundsException ignored) {
                try {
                    if (selectedsessionpart.getAmbience().hasAnyAmbience()) {
                        if (startupcheck_count[1] == 0) {
                            selectedambience.startup_addambiencefromdirectory(selectedsessionpart);
                            selectedambience.startup_checkfordeletedfiles();
                        }
                        soundFile = getnextambiencesoundfile();
                        file = soundFile.getFile();
                    } else {
                        partswithnoambience.add(selectedsessionpart);
                        throw new IndexOutOfBoundsException();}
                } catch (IndexOutOfBoundsException ignore) {
                    try {
                        selectedsessionpart.setEntrainment(selectedentrainment);
                        selectedsessionpart.setAmbience(selectedambience);
                        selectedsessionpart = getnextsessionpart();
                        startupcheck_count[0] = 0;
                        startupcheck_count[1] = 0;
                        call();
                    } catch (IndexOutOfBoundsException e) {
                        getEntrainments().marshall();
                        getAmbiences().marshall();
                        startupcheckscompleted();
                        return null;
                    }
                }
            }
            if (file != null) {
                if (file.exists()) {
                    if (soundFile == null) {soundFile = new SoundFile(file);}
                    if (! soundFile.isValid()) {
                        startupcheckplayer = new MediaPlayer(new Media(file.toURI().toString()));
                        SoundFile finalSoundFile = soundFile;
                        startupcheckplayer.setOnReady(() -> {
                            if (startupcheckplayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                                finalSoundFile.setDuration(startupcheckplayer.getTotalDuration().toMillis());
                                startupcheckplayer.dispose();
                                startupcheckplayer = null;
                                if (startupcheck_count[0] < selectedsessionpart.entrainmentpartcount()) {
                                    if (startupcheck_count[0] == 0) {selectedentrainment.setFreq(finalSoundFile);}
                                    else {selectedentrainment.ramp_add(finalSoundFile);}
                                    startupcheck_count[0]++;
                                } else {
                                    selectedambience.setoraddsoundfile(finalSoundFile);
                                    startupcheck_count[1]++;
                                }
                                workcount[0]++;
                                updateProgress(workcount[0], workcount[1]);
                                updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                                try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                            } else {
                                startupcheckplayer.dispose();
                                startupcheckplayer = null;
                                try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                            }
                        });
                    } else {
                        if (startupcheck_count[0] < selectedsessionpart.entrainmentpartcount()) {
                            if (startupcheck_count[0] == 0) {
                                selectedentrainment.setFreq(soundFile);}
                            else {
                                selectedentrainment.ramp_add(soundFile);
                            }
                            startupcheck_count[0]++;
                        } else {
                            selectedambience.setoraddsoundfile(soundFile);
                            startupcheck_count[1]++;
                        }
                        workcount[0]++;
                        updateProgress(workcount[0], workcount[1]);
                        updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                        try {call();} catch (Exception ignored) {ignored.printStackTrace();}
                    }
                } else {
                    if (startupcheck_count[0] < selectedsessionpart.entrainmentpartcount()) {
                        if (! partswithmissingentrainment.contains(selectedsessionpart)) {partswithmissingentrainment.add(selectedsessionpart);}
                        startupcheck_count[0]++;
                    }
                    else {startupcheck_count[1]++;}
                    workcount[0]++;
                    updateProgress(workcount[0], workcount[1]);
                    updateMessage("Performing Startup Checks. Please Wait (" + new Double(getProgress() * 100).intValue() + "%)");
                    try {call();} catch (Exception ignored) {}
                }
            } else {try {call();} catch (Exception ignored) {ignored.printStackTrace();}}
            return null;
        }

        // Generators
        protected void calculatetotalworktodo() {
            for (SessionPart i : sessionPartList) {
                workcount[1] += i.entrainmentpartcount();
                if (i.getAmbience().hasAnyAmbience()) {workcount[1] += i.getAmbience().getAmbience().size();}
            }
        }
        protected void populateambiencefromfiles() {
            for (SessionPart sessionPart : sessionPartList) {
                sessionPart.getAmbience().startup_addambiencefromdirectory(sessionPart);
                sessionPart.getAmbience().startup_checkfordeletedfiles();
            }
        }
        protected SoundFile getnextentraimentsoundfile() throws IndexOutOfBoundsException {
            if (selectedsessionpart instanceof Qi_Gong || selectedsessionpart instanceof Element) {
                try {
                    if (startupcheck_count[0] == 0) {
                        return selectedsessionpart.getEntrainment().getFreq();
                    } else {
                        return selectedsessionpart.getEntrainment().ramp_get(startupcheck_count[0]);
                    }
                } catch (Exception i) {i.printStackTrace(); return null;}
            } else {
                switch (startupcheck_count[0]) {
                    case 0:
                        return selectedsessionpart.getEntrainment().getFreq();
                    case 1:
                        return selectedsessionpart.getEntrainment().ramp_get(0);
                    case 2:
                        return selectedsessionpart.getEntrainment().ramp_get(1);
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }
        protected File getnextentrainmentfile() throws IndexOutOfBoundsException {
            if (selectedsessionpart instanceof Qi_Gong || selectedsessionpart instanceof Element) {
                if (startupcheck_count[0] == 0) {return new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, selectedsessionpart.getNameForFiles().toUpperCase() + ".mp3");}
                else {return new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" + selectedsessionpart.getThisession().getallCutNames().get(startupcheck_count[0] - 1).toLowerCase() + ".mp3");}
            } else {
                switch (startupcheck_count[0]) {
                    case 0:
                        return new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, selectedsessionpart.getNameForFiles().toUpperCase() + ".mp3");
                    case 1:
                        return new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "to" +
                                selectedsessionpart.getThisession().getallCutNames().get(selectedsessionpart.getThisession().getallCutNames().
                                        indexOf(selectedsessionpart.name) + 1).toLowerCase() + ".mp3");
                    case 2:
                        return new File(kujiin.xml.Options.DIRECTORYENTRAINMENT, "ramp/" + selectedsessionpart.getNameForFiles() + "toqi.mp3");
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }
        protected SoundFile getnextambiencesoundfile() throws IndexOutOfBoundsException {
            return selectedsessionpart.getAmbience().get(startupcheck_count[1]);
        }
        protected SessionPart getnextsessionpart() throws IndexOutOfBoundsException {
            SessionPart sessionpart;
            if (selectedsessionpart == null) {sessionpart = sessionPartList.get(0);}
            else {
                startupcheck_count[2] = sessionPartList.indexOf(selectedsessionpart) + 1;
                sessionpart = sessionPartList.get(startupcheck_count[2]);
            }
            selectedentrainment = sessionpart.getEntrainment();
            selectedambience = sessionpart.getAmbience();
            return sessionpart;
        }
    }

}