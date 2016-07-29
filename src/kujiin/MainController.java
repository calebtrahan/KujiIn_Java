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
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import kujiin.util.*;
import kujiin.xml.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
// TODO Set Font Size, So The Program Looks Universal And Text Isn't Oversized Cross-Platform

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
    private This_Session Session;
    private Preset Preset;

// My Fields
    // Creation
    private CreatorState creatorState;
    private Timeline creator_updateuitimeline;
    // Export
    private ExporterState exporterState;
    private ExportingSessionDialog exportingSessionDialog;
    private Integer exportserviceindex;
    private ArrayList<Service<Boolean>> exportservices;
    private Service<Boolean> currentexporterservice;
    // Sessions
    private Sessions Sessions;
    private Meditatable SessionsAndGoalsSelectedMeditatable;
    // Goals
    private Goals Goals;


    //
    private PlayerUI Player;
    private Options Options;

// Event Handlers
//    public final EventHandler<KeyEvent> NONEDITABLETEXTFIELD = event -> Util.gui_showinformationdialog(this, "Not Editable", "Non-Editable Text Field", "This Text Field Can't Be Edited");
//    public final EventHandler<ActionEvent> CHECKBOXONOFFLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("ON");} else {a.setText("OFF");}};
//    public final EventHandler<ActionEvent> CHECKBOXYESNOLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("YES");} else {a.setText("NO");}};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
        setSession(new This_Session(this));
        creation_initialize();
        exporter_initialize();
        sessions_initialize();
        goals_initialize();
    }
    public boolean cleanup() {
        getSession().getAmbiences().marshall();
        getSession().getEntrainments().marshall();
        getOptions().marshall();
        return creation_cleanup() && exporter_cleanup() && sessions_cleanup() && goals_cleanup();
    }

// Getters And Setters
    public This_Session getSession() {
        return Session;
    }
    public void setSession(This_Session session) {
        this.Session = session;
    }
    public PlayerUI getPlayer() {
        return Player;
    }
    public void setPlayer(PlayerUI player) {
        this.Player = player;
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
    public void changesessionoptions(ActionEvent actionEvent) {
        new ChangeProgramOptions(this).showAndWait();
        Options.marshall();
        sessions_gui_updateui();
        goals_gui_updateui();
    }
    public void editprogramsambience(ActionEvent actionEvent) {
        getStage().setIconified(true);
        SimpleAmbienceEditor sae = new SimpleAmbienceEditor(this, getSession().getAmbiences());
        sae.showAndWait();
        getStage().setIconified(false);
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        getStage().setIconified(true);
        EditReferenceFiles a = new EditReferenceFiles(this);
        a.showAndWait();
        getStage().setIconified(false);
    }
    public void howtouseprogram(ActionEvent actionEvent) {
        Util.menu_howtouse(this);
    }
    public void aboutthisprogram(ActionEvent actionEvent) {
        List<Integer> numbers = new ArrayList<>();
        numbers.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        System.out.println(numbers);
        numbers.set(2, 10);
        System.out.println(numbers);
//        Util.menu_aboutthisprogram();
    }
    public void contactme(ActionEvent actionEvent) {
        Util.menu_contactme();}
    public void close(ActionEvent actionEvent) {
        if (cleanup()) {System.exit(0);}
    }

// Presets
    public void preset_load(ActionEvent actionEvent) {
        File presetfile = Preset.openpreset();
        if (presetfile != null && Preset.validpreset()) {
            preset_changecreationvaluestopreset(Preset.getpresettimes());
        } else {if (presetfile != null) Util.gui_showinformationdialog(this, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void preset_save(ActionEvent actionEvent) {
        // TODO Saving Preset Is Broke!
        ArrayList<Double> creatorvalues = new ArrayList<>();
        for (Meditatable i : getSession().getAllMeditatables()) {creatorvalues.add(i.getduration().toMinutes());}
        Preset.setpresettimes(creatorvalues);
        if (! Preset.validpreset()) {Util.gui_showinformationdialog(this, "Information", "Cannot Save Preset", "All Values Are 0"); return;}
        if (Preset.savepreset()) {Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Preset Successfully Saved", 4000);}
        else {Util.gui_showerrordialog(this, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
    }
    public void preset_changecreationvaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < getSession().getAllMeditatables().size(); i++) {
                getSession().getAllMeditatables().get(i).setDuration(presetvalues.get(i));
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            Util.gui_showerrordialog(this, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }

// Creation
    // GUI
    public void creation_initialize() {
    creator_updateuitimeline = new Timeline(new KeyFrame(Duration.millis(10000), ae -> creation_gui_update()));
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
    }
}
    public void creation_gui_setDisable(boolean disable) {
        ChangeAllCutsButton.setDisable(disable);
        ChangeAllElementsButton.setDisable(disable);
        LoadPresetButton.setDisable(disable);
        SavePresetButton.setDisable(disable);
        AmbienceSwitch.setDisable(disable);
        ApproximateEndTime.setDisable(disable);
        TotalSessionTime.setDisable(disable);
        for (Meditatable i : getSession().getAllMeditatables()) {i.setDisable(disable);}
        if (! disable) {creator_updateuitimeline.play();}
        else {creator_updateuitimeline.stop();}
    }
    public void creation_gui_update() {
        boolean notallzero = false;
        try {
            for (Integer i : getSession().getallsessionvalues()) {if (i > 0) {notallzero = true;}}
        } catch (NullPointerException ignored) {}
        if (notallzero) {
            Integer totalsessiontime = 0;
            for (Integer i : getSession().getallsessionvalues()) {totalsessiontime += i;}
            int rampduration = getOptions().getSessionOptions().getRampduration();
            totalsessiontime += rampduration * 2;
            if (rampduration > 0) {TotalSessionTime.setTooltip(new Tooltip("Duration Includes A Ramp Of " + rampduration + "Mins. On Both Presession And Postsession"));}
            else {TotalSessionTime.setTooltip(null);}
            TotalSessionTime.setText(Util.formatdurationtoStringSpelledOut(new Duration((totalsessiontime * 60) * 1000), TotalSessionTime.getLayoutBounds().getWidth()));
            ApproximateEndTime.setTooltip(new Tooltip("Time You Finish Will Vary Depending On When You Start Playback"));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, totalsessiontime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        } else {
            TotalSessionTime.setText("");
            ApproximateEndTime.setText("");
        }
        if (AmbienceSwitch.isSelected()) {
            AmbienceSwitch.setSelected(false);
            Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Session Values Changed, Ambience Unselected", 5000);
        }
    }
    public void creation_gui_toggleambience(ActionEvent actionEvent) {
        if (AmbienceSwitch.isSelected()) {
            if (creation_gui_allvaluesnotzero()) {
                Session.checkambience(AmbienceSwitch);
            } else {
                Util.gui_showinformationdialog(this, "Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            Session.resetcreateditems();
        }
    }
    public void creation_gui_resetallvalues(ActionEvent actionEvent) {
        Session.resetcreateditems();
    }
    public void creation_gui_changeallvalues_cuts(ActionEvent actionEvent) {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(this, "Change All Cut Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Cut i : Session.getallCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {Session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {Session.getPostsession().changevalue(min);}
        }
    }
    public void creation_gui_changeallvalues_elements(ActionEvent actionEvent) {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(this, "Change All Element Values To: ");
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
        for (Integer i : Session.getallsessionvalues()) {
            if (i >= kujiin.xml.Options.DEFAULT_LONG_MEDITATABLE_DURATION) {return true;}
        }
        return false;
    }
    public void creation_util_createsession() {
        // TODO Check Exporter Here
        if (! creation_gui_allvaluesnotzero()) {
            Util.gui_showerrordialog(this, "Error Creating Session", "At Least One Cut Or Element's Value Must Not Be 0", "Cannot Create Session");
            creatorState = CreatorState.NOT_CREATED;
            return;
        }
        if (creation_util_isLongSession()) {
            if (! getOptions().getSessionOptions().getAlertfunction()) {
                if (Util.gui_getokcancelconfirmationdialog(this, "Add Alert File", "I've Detected A Long Session. Long Sessions Can Make It Difficult To Hear " +
                        "The Subtle Transitions In Between Session Parts", "Add Alert File In Between Session Parts?")) {
                    new ChangeAlertFile(this).showAndWait();
                }
            }
        }
        boolean creationstate = Session.createsession();
        if (creationstate) creatorState = CreatorState.CREATED;
        else {creatorState = CreatorState.NOT_CREATED;}
        creation_gui_setDisable(creatorState == CreatorState.NOT_CREATED);
    }
    public void creation_util_resetcreatedsession() {}
    public boolean creation_cleanup() {return true;}

// Export
    public void exporter_initialize() {}
    public void exporter_toggle(ActionEvent actionEvent) {
        switch (exporterState) {
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
//                        session.getnewexportsavefile();
//                    } else {
//                        // TODO Continue Fixing Logic Here
//                        if (session.getExportfile().exists()) {
//                            if (!Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
//                                session.getnewexportsavefile();
//                            }
//                        } else {session.getnewexportsavefile();}
//                    }
//                    if (session.getExportfile() == null) {Util.gui_showtimedmessageonlabel(StatusBar, "Export Session Cancelled", 3000); return;}
//                    exportserviceindex = 0;
//                    ArrayList<Cut> cutsinsession = session.getCutsinsession();
//                    for (Cut i : cutsinsession) {
//                        exportservices.add(i.getexportservice());
//                    }
//                    exportservices.add(session.getsessionexporter());
//                    exportingSessionDialog = new ExportingSessionDialog(Root);
//                    exportingSessionDialog.show();
//                    setExporterState(ExporterState.WORKING);
//                    exporter_util_movetonextexportservice();
//                } else {
//                    Util.gui_showerrordialog(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
//                    // TODO Open A Browser Showing How To Install FFMPEG
//                }
//            } else if (getExporterState() == ExporterState.WORKING) {
//                Util.gui_showtimedmessageonlabel(StatusBar, "Session Currently Being Exported", 3000);
//            } else {
//                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
//                    setExporterState(ExporterState.NOT_EXPORTED);
//                    startexport();
//                }
//            }
//        } else {Util.gui_showinformationdialog(Root, "Information", "Cannot Export", "No Cuts Selected");}
    }
    private void exporter_util_movetonextexportservice() {
//        System.out.println("Starting Next Export Service");
        exportingSessionDialog.TotalProgress.setProgress((double) exportserviceindex / exportservices.size());
        try {
            currentexporterservice = exportservices.get(exportserviceindex);
            currentexporterservice.setOnRunning(event -> {
                exportingSessionDialog.CurrentProgress.progressProperty().bind(currentexporterservice.progressProperty());
                exportingSessionDialog.StatusBar.textProperty().bind(currentexporterservice.messageProperty());
                exportingSessionDialog.CurrentLabel.textProperty().bind(currentexporterservice.titleProperty());
            });
            currentexporterservice.setOnSucceeded(event -> {exportingSessionDialog.unbindproperties(); exportserviceindex++; exporter_util_movetonextexportservice();});
            currentexporterservice.setOnCancelled(event -> exporter_export_cancelled());
            currentexporterservice.setOnFailed(event -> exporter_export_failed());
            currentexporterservice.start();
        } catch (ArrayIndexOutOfBoundsException ignored) {
            exporter_export_finished();}
    }
    public void exporter_export_finished() {
        System.out.println("Export Finished!");
        exporterState = ExporterState.COMPLETED;
    }
    public void exporter_export_cancelled() {
        System.out.println("Cancelled!");
        exporterState = ExporterState.CANCELLED;}
    public void exporter_export_failed() {
        System.out.println(currentexporterservice.getException().getMessage());
        System.out.println("Failed!");
        exporterState = ExporterState.FAILED;
    }
    public boolean exporter_cleanup() {
        boolean currentlyexporting = exporterState == ExporterState.WORKING;
        if (currentlyexporting) {
            Util.gui_showinformationdialog(this, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
        } else {This_Session.deleteprevioussession();}
        return ! currentlyexporting;
    }


// Sessions And Goals
    public void sessionsandgoals_meditatableselectionchanged(ActionEvent actionEvent) {
        try {
            int index = GoalMeditatableComboBox.getSelectionModel().getSelectedIndex();
            SessionsAndGoalsSelectedMeditatable = getSession().getAllMeditatablesincludingTotalforTracking().get(index);
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
        GoalMeditatableComboBox.setItems(FXCollections.observableArrayList(getSession().getAllMeditablesincludingTotalNames()));
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
            Double averagesessionduration = SessionsAndGoalsSelectedMeditatable.getAveragePracticeTime(PrePostSwitch.isSelected());
            Integer totalminutespracticed = SessionsAndGoalsSelectedMeditatable.getTotalMinutesPracticed(PrePostSwitch.isSelected());
            Integer numberofsessionspracticed = SessionsAndGoalsSelectedMeditatable.getNumberOfSessionsPracticed(PrePostSwitch.isSelected());
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
            Util.gui_showinformationdialog(this, "No Sessions", "No Practiced Sessions", "Cannot View Sessions");
        } else {new AllSessionsDetails(this).showAndWait();}
    }
    public void sessions_gui_togglepreandpost(ActionEvent actionEvent) {sessions_gui_updateui();}
    public void sessions_gui_resetallvalues() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
    }
        // Util
    public boolean sessions_cleanup() {Sessions.marshall(); return true;}


    // Goals
        // GUI
    public void goals_initialize() {
        Goals = new Goals(this);
        Goals.unmarshall();
        for (Meditatable i : getSession().getAllMeditatablesincludingTotalforTracking()) {i.setGoalsController(Goals);}
        goals_gui_updateui();
    }
    public void goals_gui_updateui() {
        boolean disabled = SessionsAndGoalsSelectedMeditatable == null || SessionsAndGoalsSelectedMeditatable.getCurrentGoal() == null;
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
        } else if (SessionsAndGoalsSelectedMeditatable.getCurrentGoal() == null || SessionsAndGoalsSelectedMeditatable.getTotalMinutesPracticed(false) == 0) {
            System.out.println(SessionsAndGoalsSelectedMeditatable.getCurrentGoal());
            // No Current Goal Set
            toptext = "No Current Goal";
            percentage = "";
            progress = 0.0;
            goalprogresstooltip = new Tooltip("No Current Goal Set For " + SessionsAndGoalsSelectedMeditatable.name);
            newgoalbuttontext = kujiin.xml.Options.NEWGOALTEXT;
            newgoalbuttontooltip = new Tooltip("Set A New Goal");
        } else {
            toptext = "Current Goal Progress";
            Double goalminutes = SessionsAndGoalsSelectedMeditatable.getCurrentGoal().getGoal_Hours() * 60;
            progress = Util.convert_minstodecimalhours(SessionsAndGoalsSelectedMeditatable.getTotalMinutesPracticed(false), 2) / (goalminutes / 60);
            goalprogresstooltip = new Tooltip(String.format("Currently Practiced: %s -> Goal: %s",
                    Util.formatdurationtoStringSpelledOut(new Duration(SessionsAndGoalsSelectedMeditatable.getTotalMinutesPracticed(false) * 1000), null),
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
        PlayerUI playerUI = getPlayer();
        if (playerUI != null && playerUI.isShowing()) {
            playerUI.GoalTopLabel.setDisable(disabled);
            playerUI.GoalPercentageLabel.setDisable(disabled);
            playerUI.GoalProgressBar.setDisable(disabled);
            playerUI.GoalTopLabel.setText(toptext);
            playerUI.GoalProgressBar.setProgress(progress);
            playerUI.GoalPercentageLabel.setText(percentage);
            // String.format("%s hrs -> %s hrs (%d", practiceddecimalhours, goaldecimalhours, progress.intValue()) + "%)");
        }
    }
    public void goals_gui_setnewgoal(Event event) {
        if (newgoalButton.getText().equals(kujiin.xml.Options.NEWGOALTEXT)) {
            SimpleGoalSetDialog simpleGoalSetDialog = new SimpleGoalSetDialog(this, SessionsAndGoalsSelectedMeditatable);
            simpleGoalSetDialog.showAndWait();
            if (simpleGoalSetDialog.shouldSetgoal()) {
                // TODO Set Goal Here
                SessionsAndGoalsSelectedMeditatable.addGoal(new Goals.Goal(simpleGoalSetDialog.getNewGoalHours(), SessionsAndGoalsSelectedMeditatable));
                goals_gui_updateui();
            }
        } else if (newgoalButton.getText().equals(kujiin.xml.Options.GOALPACINGTEXT)) {
            new GoalPacingDialog(this, SessionsAndGoalsSelectedMeditatable).showAndWait();
        }
    }
    public void goals_gui_viewcurrentgoals(Event event) {
        if (SessionsAndGoalsSelectedMeditatable.getAllGoals() == null || SessionsAndGoalsSelectedMeditatable.getAllGoals().size() == 0) {
            Util.gui_showinformationdialog(this, "Information", "No Goals Exist For " + SessionsAndGoalsSelectedMeditatable.name, "Please Add A Goal For " + SessionsAndGoalsSelectedMeditatable.name);
        } else {new AllMeditatablesGoalProgress(this, SessionsAndGoalsSelectedMeditatable).showAndWait();}
    }
    public void goals_gui_resetallvalues() {
        goalsprogressbar.setProgress(0.0);
    }
        // Util
    public ArrayList<Meditatable> goals_util_getmeditatableswithoutlongenoughgoals(List<Meditatable> cutsandelementsinsession) {
        return cutsandelementsinsession.stream().filter(i -> i.getduration().greaterThan(Duration.ZERO) && !i.goalsarelongenough()).collect(Collectors.toCollection(ArrayList::new));
    }
    public boolean goals_cleanup() {Goals.marshall(); return true;}

// Session Player Widget
    public void playthisession(ActionEvent actionEvent) {
        creation_util_createsession();
        if (creatorState == CreatorState.CREATED) {
            if (getPlayer() != null && getPlayer().isShowing()) {return;}
            getStage().setIconified(true);
            setPlayer(new PlayerUI(this));
            getPlayer().showAndWait();
            getStage().setIconified(false);
        }
    }

// Dialogs
    public static class ChangeAlertFile extends Stage {
        public Button HelpButton;
        public Button AcceptButton;
        public Button CancelButton;
        public CheckBox AlertFileToggleButton;
        public TextField alertfileTextField;
        public Button openFileButton;
        public Button PreviewButton;
        private MainController Root;
        private File alertfile;
        private final static String NO_ALERT_FILE_SELECTED_TEXT = "No Alert File Selected";
        private final static int SUGGESTED_ALERT_FILE_MAX_LENGTH = 10;
        private final static int ABSOLUTE_ALERT_FILE_MAX_LENGTH = 30;

        public ChangeAlertFile(MainController root) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAlertDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                setTitle("Alert File Editor");
                alertfileTextField.setEditable(false);
                AlertFileToggleButton.setSelected(Root.getOptions().getSessionOptions().getAlertfunction());
                String alertfilelocation = Root.getOptions().getSessionOptions().getAlertfilelocation();
                if (alertfilelocation != null) {alertfile = new File(Root.getOptions().getSessionOptions().getAlertfilelocation());}
                alertfiletoggled(null);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
        }

    // Button Actions
        public void accept(ActionEvent actionEvent) {
            if (AlertFileToggleButton.isSelected() && alertfile == null) {
                Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "No Alert File Selected And Alert Function Enabled", "Please Select An Alert File Or Turn Off Alert Function");
                return;
            }
            Root.getOptions().getSessionOptions().setAlertfunction(AlertFileToggleButton.isSelected());
            if (alertfile != null) {Root.getOptions().getSessionOptions().setAlertfilelocation(alertfile.toURI().toString());}
            else {Root.getOptions().getSessionOptions().setAlertfilelocation(null);}
            Root.getOptions().marshall();
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
                PreviewFile previewFile = new PreviewFile(Root, alertfile);
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
                if (duration < SUGGESTED_ALERT_FILE_MAX_LENGTH) {
                    // TODO Ask Confirmation Here
                } else {}
                String durationtext = Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), alertfileTextField.getLayoutBounds().getWidth());
                String text = String.format("%s (%s)", alertfile.getName(), durationtext);
                alertfileTextField.setText(text);
            } else {
                if (alertfile != null) {alertfile = null; alertfiletoggled(null);}
                alertfileTextField.setText(NO_ALERT_FILE_SELECTED_TEXT);
            }
        }
        public void help(ActionEvent actionEvent) {
            Util.gui_showinformationdialog(Root, "What Is An Alert File?", "", "The 'alert file' is a short audible warning\nthat is played in between parts of the session\nto inform you it's time to transition to the next\npart of the session");
        }

    // Utility Methods
        public boolean fileisgood(File testfile) {
        // Test If Valid Extension
            if (! Util.audio_isValid(testfile)) {
                Util.gui_showinformationdialog(Root, "Information", "Invalid Audio Format", "Supported Audio Formats: " + Arrays.asList(Util.SUPPORTEDAUDIOFORMATS).toString());
                return false;
            }
            Double duration = Util.audio_getduration(testfile);
            if (duration == 0.0) {Util.gui_showinformationdialog(Root, "Invalid File", "Invalid Audio File", "Audio File Has Zero Length Or Is Corrupt. Cannot Use As Alert File"); return false;}
            else if (duration >= (SUGGESTED_ALERT_FILE_MAX_LENGTH) && duration < (ABSOLUTE_ALERT_FILE_MAX_LENGTH)) {
                String confirmationtext = String.format("%s Is %s Which Is Longer Than The Suggested Maximum Duration %s", testfile.getName(),
                        Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000), null));
                return Util.gui_getokcancelconfirmationdialog(Root, "Alert File Too Long", confirmationtext, "This May Break Session Immersion. Really Use This File As Your Alert File?");
            } else if (duration >= ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                String errortext = String.format("%s Is Longer Than The Maximum Allowable Duration %s", Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(ABSOLUTE_ALERT_FILE_MAX_LENGTH * 1000), null));
                Util.gui_showinformationdialog(Root, "Invalid File", errortext, "Cannot Use As Alert File As It Will Break Immersion");
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
//                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
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
//                        if (!Util.gui_getokcancelconfirmationdialog(Root, "Validation", "Alert File Is longer Than 10 Seconds",
//                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
//                                        "Really Use It?", duration))) {newfile = null;}
//                    }
//                } else {
//                    Util.gui_showinformationdialog(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Util.audio_getsupportedText());
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
//            AlertFileEditButton.setDisable(! good);
//            AlertFileTextField.setDisable(! good);
//            AlertSwitch.setSelected(good);
//            return good;
//        }

    }
    public static class EditReferenceFiles extends Stage {
        public ChoiceBox<String> CutNamesChoiceBox;
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
        private MainController.ReferenceType referenceType;
        private ArrayList<Integer> userselectedindexes;

        public EditReferenceFiles(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference Files Editor");
            ObservableList<String> meditatablenames = FXCollections.observableArrayList();
            meditatablenames.addAll(kujiin.xml.Options.ALLNAMES);
            userselectedindexes = new ArrayList<>();
            CutNamesChoiceBox.setItems(meditatablenames);
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            CutNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
            HTMLVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            setReferenceType(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);
            HTMLVariation.setSelected(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION == MainController.ReferenceType.html);
            TEXTVariation.setSelected(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION == MainController.ReferenceType.txt);
            PreviewButton.setDisable(true);
            SaveButton.setDisable(true);
            this.setOnCloseRequest(event -> {
                if (unsavedchanges()) {
                    switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", CutNamesChoiceBox.getValue() + " " + getReferenceType().name() + " Variation Has Unsaved Changes", "Save Changes Before Exiting?")) {
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
        public MainController.ReferenceType getReferenceType() {
            return referenceType;
        }
        public void setReferenceType(MainController.ReferenceType referenceType) {
            this.referenceType = referenceType;
        }

    // Text Area Methods
        private boolean unsavedchanges() {
            try {
                return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
            } catch (Exception e) {return false;}
        }
        public void newmeditatableselected(ActionEvent actionEvent) {
            HTMLVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            if (userselectedindexes.size() > 0 && selectedfile != null && unsavedchanges()) {
                Util.AnswerType answerType = Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading A Different Cut/Element");
                switch (answerType) {
                    case YES:
                        saveselectedfile(null);
                        break;
                    case NO:
                        break;
                    case CANCEL:
                        CutNamesChoiceBox.getSelectionModel().select(userselectedindexes.get(userselectedindexes.size() - 1));
                        return;
                }
            }
            loadselectedfile();
        }
        private void textchanged() {
            if (getReferenceType() != null && selectedmeditatable != null && selectedfile != null) {
                boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
                PreviewButton.setDisable(! hasvalidtext || getReferenceType() == MainController.ReferenceType.txt);
                SaveButton.setDisable(MainTextArea.getText() == null || Util.file_getcontents(selectedfile).equals(MainTextArea.getText().toCharArray()));
                switch (getReferenceType()) {
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
                Util.gui_showtimedmessageonlabel(StatusBar, "No Cut Or Element Selected", 3000);
            }
        }

    // Other Methods
        public void saveselectedfile(ActionEvent actionEvent) {
            if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
                String text = selectedmeditatable + "'s Reference File (" + getReferenceType().toString() + " Variation) Has Been Saved";
                Util.gui_showinformationdialog(Root, "Changes Saved", text, "");
            } else {Util.gui_showerrordialog(Root, "Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
        }
        public void loadselectedfile() {
            if (CutNamesChoiceBox.getSelectionModel().getSelectedIndex() != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
                selectedmeditatable = CutNamesChoiceBox.getSelectionModel().getSelectedItem();
                selectnewfile();
                String contents = Util.file_getcontents(selectedfile);
                MainTextArea.setText(contents);
                PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
                StatusBar.setTextFill(Color.BLACK);
                StatusBar.setText("");
                SaveButton.setDisable(true);
            } else {
                if (CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1) {Util.gui_showinformationdialog(Root, "Information", "No Cut Selected", "Select A Cut To Load");}
                else {Util.gui_showinformationdialog(Root, "Information", "No Variation Selected", "Select A Variation To Load");}
                PreviewButton.setDisable(true);
            }
        }
        public void selectnewfile() {
            if (getReferenceType() == null || selectedmeditatable == null) {selectedfile = null; return;}
            switch (getReferenceType()) {
                case html:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html"), selectedmeditatable + ".html");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(Root, e);}}
                    break;
                case txt:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt"), selectedmeditatable + ".txt");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(Root, e);}}
                    break;
            }
        }
        public void htmlselected(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                Util.AnswerType answerType = Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading HTML Variation");
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
            setReferenceType(MainController.ReferenceType.html);
            selectnewfile();
            loadselectedfile();
        }
        public void textselected(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                Util.AnswerType answerType = Util.gui_getyesnocancelconfirmationdialog(Root, "Confirmation", "Previous Reference File Has Unsaved Changes", "Save Changes Before Loading TXT Variation");
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
            setReferenceType(MainController.ReferenceType.txt);
            selectnewfile();
            loadselectedfile();
        }
        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && getReferenceType() == MainController.ReferenceType.html) {
                if (! Util.String_validhtml(MainTextArea.getText())) {
                    if (! Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Html Code In Text Area Is Not Valid HTML", "Preview Anyways?")) {return;}
                }
                PlayerUI.DisplayReference dr = new PlayerUI.DisplayReference(Root, MainTextArea.getText());
                dr.showAndWait();
            }
        }

    // Dialog Methods
        public void closewindow(Event event) {
        // Check If Unsaved Text
        this.close();
    }

}
    public static class AdvancedAmbienceEditor extends Stage implements Initializable {
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
        private String selectedmeditatablename;
        private File tempdirectory;
        private MainController Root;
        private PreviewFile previewdialog;
        private Ambiences ambiences;
        private Ambience selectedambience;

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

        public AdvancedAmbienceEditor(MainController root, Ambiences ambiences) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> {

                });
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            MeditatableSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(MainController root, Ambiences ambiences, String cutname) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            MeditatableSelectionBox.setOnAction(event -> selectandloadcut());
            MeditatableSelectionBox.getSelectionModel().select(kujiin.xml.Options.ALLNAMES.indexOf(cutname));
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }

    // Transfer Methods
        // TODO Add Check Duplicates Before Moving Over (Or Ask Allow Duplicates?)
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_temp_ambiencesong != null && selectedmeditatablename != null) {
                if (! Actual_Table.getItems().contains(selected_temp_ambiencesong)) {
                    int tempindex = Temp_Table.getItems().indexOf(selected_actual_ambiencesong);
                    actual_ambiencesonglist.add(temp_ambiencesonglist.get(tempindex));
                    actual_soundfilelist.add(temp_soundfilelist.get(tempindex));
                    Actual_Table.getItems().add(selected_temp_ambiencesong);
                    calculateactualtotalduration();
                }
            } else {
                if (selected_temp_ambiencesong == null) {
                    Util.gui_showinformationdialog(Root, "Information", "Cannot Transfer", "Nothing Selected");}
                else {
                    Util.gui_showinformationdialog(Root, "Information", "Cannot Transfer", "No Cut Selected");}
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_actual_ambiencesong != null && selectedmeditatablename != null) {
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
        public void selectandloadcut() {
            int index = MeditatableSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                selectedambience = ambiences.getmeditatableAmbience(index);
                if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
                else {actual_ambiencesonglist.clear();}
                if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
                else {actual_soundfilelist.clear();}
                Actual_Table.getItems().clear();
                selectedmeditatablename = kujiin.xml.Options.ALLNAMES.get(index);
                if (populateactualambiencetable()) {
                    Actual_Table.setItems(actual_ambiencesonglist);
                }
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
                    Util.gui_showinformationdialog(Root, "Files Couldn't Be Added", "These Files Couldn't Be Added", notvalidfilenames.toString());}
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
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                table.getItems().remove(index);
                soundfilelist.remove(index);
                songlist.remove(index);
                calculateactualtotalduration();
                calculatetemptotalduration();
            }
            else {
                Util.gui_showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        private void preview(AmbienceSong selectedsong) {
            if (selectedsong != null && selectedsong.getFile() != null && selectedsong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(Root, selectedsong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }
        private boolean populateactualambiencetable() {
            actual_ambiencesonglist.clear();
            if (selectedmeditatablename != null) {
                try {
                    if (selectedambience.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedambience.getAmbience()) {
                        actual_soundfilelist.add(i);
                        actual_ambiencesonglist.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.gui_showinformationdialog(Root, "Information", selectedmeditatablename + " Has No Ambience", "Please Add Ambience To " + selectedmeditatablename);
                    return false;
                }
            } else {
                Util.gui_showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

    // Dialog Methods
        public boolean unsavedchanges() {
            if (MeditatableSelectionBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
            try {
                if (actual_soundfilelist.size() != selectedambience.getAmbience().size()) {return true;}
                List<SoundFile> ambiencelist = selectedambience.getAmbience();
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
                    if (! selectedambience.ambienceexistsinActual(i)) {selectedambience.actual_add(i);}
                }
                ambiences.setmeditatableAmbience(index, selectedambience);
                ambiences.marshall();
                Util.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedmeditatablename, "");
            } else {
                Util.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
        }
        public void closebuttonpressed(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedmeditatablename, "Save Changes Before Closing?")) {save(null);}
                else {return;}
            }
            close();
        }
        public void switchtosimple(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedmeditatablename, "Save Changes Before Switching To Simple Mode?")) {save(null);}
            }
            this.close();
            deletetempambiencefromdirectory();
            if (selected_temp_ambiencesong != null && kujiin.xml.Options.ALLNAMES.contains(selectedmeditatablename)) {
                new SimpleAmbienceEditor(Root, Root.getSession().getAmbiences(), selectedmeditatablename).show();
            } else {new SimpleAmbienceEditor(Root, Root.getSession().getAmbiences()).show();}
        }
    }
    public static class SimpleAmbienceEditor extends Stage implements Initializable {
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
        private MainController Root;
        private ObservableList<AmbienceSong> AmbienceList;
        private ArrayList<SoundFile> SoundList;
        private AmbienceSong selectedambiencesong;
        private String selectedmeditatable;
        private PreviewFile previewdialog;
        private Ambiences ambiences;
        private Ambience selectedambience;
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

        public SimpleAmbienceEditor(MainController root, Ambiences ambiences) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Simple Ambience Editor");
            } catch (IOException ignored) {}
            MeditatableChoiceBox.setOnAction(event -> selectandloadcut());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
        }
        public SimpleAmbienceEditor(MainController root, Ambiences ambiences, String cutorelementname) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Simple Ambience Editor");
            } catch (IOException ignored) {}
            setOnShowing(event -> {
                if (kujiin.xml.Options.ALLNAMES.contains(cutorelementname)) {
                    MeditatableChoiceBox.getSelectionModel().select(cutorelementname);
                    selectandloadcut();
                }
            });
            MeditatableChoiceBox.setOnAction(event -> selectandloadcut());
        }

    // Table Methods
        public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
        public void selectandloadcut() {
            int index = MeditatableChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                selectedambience = ambiences.getmeditatableAmbience(index);
                if (AmbienceList == null) {AmbienceList = FXCollections.observableArrayList();}
                else {AmbienceList.clear();}
                if (SoundList == null) {SoundList = new ArrayList<>();}
                else {SoundList.clear();}
                AmbienceTable.getItems().clear();
                selectedmeditatable = kujiin.xml.Options.ALLNAMES.get(index);
                if (populateactualambiencetable()) {
                    AmbienceTable.setItems(AmbienceList);
                }
                calculatetotalduration();
            }
        }
        public void add() {
            List<File> filesselected = new FileChooser().showOpenMultipleDialog(Root.Scene.getWindow());
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
                Util.gui_showinformationdialog(Root, "Information", notvalidfilenames.size() + " Files Weren't Added Because They Are Unsupported", "");
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
                    Util.gui_showinformationdialog(Root, "Couldn't Add Files", "Supported Audio Formats: " + Util.audio_getsupportedText(), "Couldn't Add " + notvalidfilenames.size() + "Files");
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
                calculatetotalduration();
            });
        }
        public void remove(ActionEvent actionEvent) {
            int index = AmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                SoundFile soundFile = SoundList.get(index);
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                AmbienceTable.getItems().remove(index);
                AmbienceList.remove(index);
                SoundList.remove(index);
                calculatetotalduration();
            }
            else {
                Util.gui_showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        public void preview(ActionEvent actionEvent) {
            if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(Root, selectedambiencesong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }
        public boolean populateactualambiencetable() {
            AmbienceList.clear();
            if (selectedmeditatable != null) {
                try {
                    if (selectedambience.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedambience.getAmbience()) {
                        SoundList.add(i);
                        AmbienceList.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.gui_showinformationdialog(Root, "Information", selectedmeditatable + " Has No Ambience", "Please Add Ambience To " + selectedmeditatable);
                    return false;
                }
            } else {
                Util.gui_showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
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
                if (SoundList.size() != selectedambience.getAmbience().size()) {return true;}
                List<SoundFile> ambiencelist = selectedambience.getAmbience();
                for (SoundFile x : SoundList) {
                    if (! ambiencelist.contains(x)) {return true;}
                }
                return false;
            } catch (NullPointerException ignored) {return false;}
        }

    // Dialog Methods
        public void advancedmode(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Switching To Advanced Mode?")) {save(null);}
            }
            this.close();
            if (selectedmeditatable != null && kujiin.xml.Options.ALLNAMES.contains(selectedmeditatable)) {
                new AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences(), selectedmeditatable).show();
            } else {new AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences()).show();}
        }
        public void save(ActionEvent actionEvent) {
            int index = MeditatableChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : SoundList) {
                    if (! selectedambience.ambienceexistsinActual(i)) {selectedambience.actual_add(i);}
                }
                ambiences.setmeditatableAmbience(index, selectedambience);
                ambiences.marshall();
                Util.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedmeditatable, "");
            } else {
                Util.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
        }
        public void closedialog(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedmeditatable, "Save Changes Before Closing?")) {save(null);}
                else {return;}
            }
            close();
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
        public Label TopLabel;
        private MainController Root;
        private Media Mediatopreview;
        private File Filetopreview;
        private MediaPlayer PreviewPlayer;

        public PreviewFile(MainController root, File filetopreview) {
            if (Util.audio_isValid(filetopreview)) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                fxmlLoader.setController(this);
                setOnHidden(event -> {
                    if (PreviewPlayer != null) {PreviewPlayer.dispose();}
                    close();
                });
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
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
                        PauseButton.setDisable(false);
                        StopButton.setDisable(false);
                    });
                    VolumeSlider.setValue(0.0);
                    VolumePercentage.setText("0%");
                } catch (IOException ignored) {}
            } else {
                Util.gui_showinformationdialog(Root, "Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
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
    public static class ExceptionDialog extends Stage {
        public TextArea StackTraceTextField;
        public Button CloseButton;
        public Button ContinueButton;
        public CheckBox NotifyMeCheckbox;
        public Label TopText;
        private MainController Root;

        public ExceptionDialog(MainController root, Exception exception) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExceptionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
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
    public static class ChangeProgramOptions extends Stage {
        public CheckBox TooltipsCheckBox;
        public CheckBox HelpDialogsCheckBox;
        public TextField AlertFileTextField;
        public Button AlertFileEditButton;
        public TextField FadeInValue;
        public TextField FadeOutValue;
        public TextField EntrainmentVolumePercentage;
        public TextField AmbienceVolumePercentage;
        public ChoiceBox<String> ProgramThemeChoiceBox;
        public Button AcceptButton;
        public Button CancelButton;
        public Button DeleteAllGoalsButton;
        public Button DeleteAllSessionsProgressButton;
        public Button DefaultsButton;
        public CheckBox ReferenceSwitch;
        public RadioButton ReferenceHTMLRadioButton;
        public RadioButton ReferenceTXTRadioButton;
        public CheckBox FullscreenCheckbox;
        public CheckBox RampSwitch;
        public Button AddNewThemeButton;
        private kujiin.xml.Options Options;
        private MainController Root;
        private MainController.ReferenceType tempreferencetype;

        public ChangeProgramOptions(MainController root) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeProgramOptions.fxml"));
                fxmlLoader.setController(this);
                Options = Root.getOptions();
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                setResizable(false);
                setTitle("Preferences");
                AlertFileTextField.setEditable(false);
                setuplisteners();
                setuptooltips();
                populatefromxml();
                referencetoggle();
                setOnCloseRequest(event -> {
                    if (valuesdifferentthanxml()) {
                        switch (Util.gui_getyesnocancelconfirmationdialog(Root, "Unsaved Changes", "You Have Unsaved Changes", "Save Changes Before Exiting?")) {
                            case YES: save(); break;
                            case NO: break;
                            case CANCEL: event.consume(); break;
                        }
                    }
                });
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
        }

    // Setup Methods
        public void populatefromxml() {
        // Program Options
            TooltipsCheckBox.setSelected(Root.getOptions().getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(Root.getOptions().getProgramOptions().getHelpdialogs());
        // Session Options
            alertfiletoggled();
        // Playback Options
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            EntrainmentVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getEntrainmentvolume() * 100).intValue()));
            AmbienceVolumePercentage.setText(String.valueOf(new Double(Options.getSessionOptions().getAmbiencevolume() * 100).intValue()));
        // Appearance Options
            ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(Root.getOptions().STYLE_THEMES_NAMES));
            try {
                int index = Root.getOptions().STYLE_THEMES_ACTUAL.indexOf(Root.getOptions().getAppearanceOptions().getThemefile());
                ProgramThemeChoiceBox.getSelectionModel().select(index);
            } catch (Exception ignored) {}
        }
        public void setuptooltips() {
            TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
            if (Root.getOptions().getProgramOptions().getTooltips()) {
                HelpDialogsCheckBox.setTooltip(new Tooltip(""));
                AlertFileTextField.setTooltip(new Tooltip("Alert File Is A Sound File Played In Between Different Session Parts"));
                AlertFileEditButton.setTooltip(new Tooltip("Edit Alert File"));
                RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
                FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
                FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
                EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
                AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
                DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
                DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
            } else {
                HelpDialogsCheckBox.setTooltip(null);
                AlertFileTextField.setTooltip(null);
                AlertFileEditButton.setTooltip(null);
                RampSwitch.setTooltip(null);
                FadeInValue.setTooltip(null);
                FadeOutValue.setTooltip(null);
                EntrainmentVolumePercentage.setTooltip(null);
                AmbienceVolumePercentage.setTooltip(null);
                DeleteAllGoalsButton.setTooltip(null);
                DeleteAllSessionsProgressButton.setTooltip(null);
            }
        }
        public void setuplisteners() {
            Util.custom_textfield_double(FadeInValue, 0.0, 60.0, 1, 1);
            Util.custom_textfield_double(FadeOutValue, 0.0, 60.0, 1, 1);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            Util.custom_textfield_integer(EntrainmentVolumePercentage, 1, 100, 5);
            ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
            ReferenceHTMLRadioButton.setOnAction(event1 -> HTMLTypeSelected());
            ReferenceTXTRadioButton.setOnAction(event1 -> TXTTypeSelected());
            ProgramThemeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {selectnewtheme();});
            FullscreenCheckbox.setOnMouseClicked(event -> setFullscreenOption());
            AlertFileTextField.setEditable(false);
        }

    // Alert File Methods
        public void editalertfile(ActionEvent actionEvent) {
            ChangeAlertFile changeAlertFile = new ChangeAlertFile(Root);
            changeAlertFile.showAndWait();
            alertfiletoggled();
        }
        public void alertfiletoggled() {
            boolean enabled = Root.getOptions().getSessionOptions().getAlertfunction();
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
        public void setFullscreenOption() {
            if (! FullscreenCheckbox.isDisabled()) {
                Options.getSessionOptions().setReferencefullscreen(FullscreenCheckbox.isSelected());
            }
        }
        public void HTMLTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(true);
            ReferenceTXTRadioButton.setSelected(false);
            tempreferencetype = MainController.ReferenceType.html;
        }
        public void TXTTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(false);
            ReferenceTXTRadioButton.setSelected(true);
            tempreferencetype = MainController.ReferenceType.txt;
        }

    // Appearance Methods
        public void addnewtheme(ActionEvent actionEvent) {

        }
        public void selectnewtheme() {
            int index = ProgramThemeChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Options.getAppearanceOptions().setThemefile(Root.getOptions().STYLE_THEMES_ACTUAL.get(index));
                getScene().getStylesheets().clear();
                getScene().getStylesheets().add(Options.getAppearanceOptions().getThemefile());
            }
        }

    // Button Actions
        public void save() {
            if (checkifvaluesValid()) {
                Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
                Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
                Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
                Options.getSessionOptions().setFadeoutduration(new Double(FadeInValue.getText()));
                Options.getSessionOptions().setFadeinduration(new Double(FadeOutValue.getText()));
                Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
                Options.getSessionOptions().setReferencetype(tempreferencetype);
                Options.getSessionOptions().setReferencefullscreen(FullscreenCheckbox.isSelected());
                Options.marshall();
            }
        }
        public void accept(ActionEvent actionEvent) {save(); close();}
        public void cancel(ActionEvent actionEvent) {close();}
        public boolean valuesdifferentthanxml() {
            try {
                if (Options.getProgramOptions().getTooltips() != TooltipsCheckBox.isSelected()) {return true;}
                if (Options.getProgramOptions().getHelpdialogs() != HelpDialogsCheckBox.isSelected()) {return true;}
                if (! Objects.equals(Options.getSessionOptions().getEntrainmentvolume() * 100, new Double(EntrainmentVolumePercentage.getText()))) {return true;}
                if (! Objects.equals(Options.getSessionOptions().getAmbiencevolume() * 100, new Double(AmbienceVolumePercentage.getText()))) {return true;}
                if (! Objects.equals(Options.getSessionOptions().getFadeinduration(), new Double(FadeInValue.getText()))) {return true;}
                if (! Objects.equals(Options.getSessionOptions().getFadeoutduration(), new Double(FadeOutValue.getText()))) {return true;}
                if (Options.getSessionOptions().getRampenabled() != RampSwitch.isSelected()) {return true;}
                if (Options.getSessionOptions().getReferenceoption() != ReferenceSwitch.isSelected()) {return true;}
                if (Options.getSessionOptions().getReferencetype() != tempreferencetype) {return true;}
                if (Options.getSessionOptions().getReferenceoption() != ReferenceSwitch.isSelected() && Options.getSessionOptions().getReferencefullscreen() != ReferenceSwitch.isSelected()) {return true;}
                // TODO Check Appearance Options Here
                return false;
            } catch (NumberFormatException | NullPointerException ignored) {return false;}
        }
        public boolean checkifvaluesValid() {
            Double entrainmentvolume = new Double(EntrainmentVolumePercentage.getText()) / 100;
            Double ambiencevolume = new Double(AmbienceVolumePercentage.getText()) / 100;
            boolean entrainmentgood = entrainmentvolume <= 100.0 && entrainmentvolume > 0.0;
            boolean ambiencegood = ambiencevolume <= 100.0 && ambiencevolume > 0.0;
            Util.gui_validate(EntrainmentVolumePercentage, entrainmentgood);
            Util.gui_validate(AmbienceVolumePercentage, ambiencegood);
            return entrainmentgood && ambiencegood;
        }
        public void resettodefaults(ActionEvent actionEvent) {
            if (Util.gui_getokcancelconfirmationdialog(Root, "Reset To Defaults", "Reset All Values To Defaults?", "You Will Lose Any Unsaved Changes")) {
                Options.resettodefaults();
                populatefromxml();
            }
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Util.gui_showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {
                    Util.gui_showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Util.gui_showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {
                    Util.gui_showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }

    }
    public static class SessionDetails extends Stage {
        public MainController Root;
        public BarChart<String, java.lang.Number> SessionBarChart;
        public CategoryAxis SessionCategoryAxis;
        public NumberAxis SessionNumbersAxis;
        public TextField DatePracticedTextField;
        public TextField SessionDurationTextField;
        public Button CloseButton;
        public Label GoalsCompletedTopLabel;
        public ListView<String> GoalsCompletedListView;

        public SessionDetails(MainController root, List<Meditatable> meditatablesinsession) {
            Root = root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionCompleteDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                SessionNumbersAxis.setLabel("Minutes");
                setTitle("Session Details");
                XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
                Duration totalsessionduration = new Duration(0);
                ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
                for (Meditatable i : meditatablesinsession) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getduration().toMinutes()));
                    totalsessionduration.add(i.getduration());
                    for (Goals.Goal x : i.getGoalsCompletedThisSession()) {
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
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
        }
        public SessionDetails(MainController root, kujiin.xml.Session session) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_Individual.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                SessionNumbersAxis.setLabel("Minutes");
                setTitle("Session Details");
                DatePracticedTextField.setText(session.getDate_Practiced());
                DatePracticedTextField.setEditable(false);
                XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i < 16; i++) {
                    int duration = session.getcutduration(i);
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
            } catch (IOException | NullPointerException e) {new ExceptionDialog(Root, e).showAndWait();}
        }

        public void closeDialog(ActionEvent actionEvent) {
            close();
        }

        class CompletedGoalsAtEndOfSessionBinding {
            private StringProperty cutname;
            private StringProperty practicedhours;
            private StringProperty goalhours;
            private StringProperty dateset;
            private IntegerProperty daysittooktocomplete;
            private StringProperty datecompleted;

            public CompletedGoalsAtEndOfSessionBinding(String cutname, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
                this.cutname = new SimpleStringProperty(cutname);
                this.practicedhours = new SimpleStringProperty(practicedhours);
                this.goalhours = new SimpleStringProperty(goalhours);
                this.dateset = new SimpleStringProperty(dateset);
                this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
                this.datecompleted = new SimpleStringProperty(datecompleted);
            }
        }
    }
    public static class AmbienceSong {
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
    public static class ChangeAllValuesDialog extends Stage {
            public Button AcceptButton;
            public Button CancelButton;
            public TextField MinutesTextField;
            public CheckBox PresessionCheckbox;
            public CheckBox PostsessionCheckBox;
            private Boolean accepted;
            private MainController Root;
            private int minutes;

            public ChangeAllValuesDialog(MainController root, String toptext) {
                try {
                    Root = root;
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAllValuesDialog.fxml"));
                    fxmlLoader.setController(this);
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                    setTitle(toptext);
                    setAccepted(false);
                    MinutesTextField.setText("0");
                    Util.custom_textfield_integer(MinutesTextField, 0, 600, 1);
                    MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        try {setMinutes(Integer.parseInt(MinutesTextField.getText()));}
                        catch (NumberFormatException ignored) {setMinutes(0);}
                    });
                } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
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
            public void acceptbuttonpressed(Event event) {setAccepted(true); this.close();}
            public void cancelbuttonpressed(Event event) {setAccepted(false); this.close();}
            public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
            public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
    }
    public static class ExportingSessionDialog extends Stage {
            public Button CancelButton;
            public ProgressBar TotalProgress;
            public Label StatusBar;
            public ProgressBar CurrentProgress;
            public Label TotalLabel;
            public Label CurrentLabel;
            private MainController Root;

            public ExportingSessionDialog(MainController root) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExportingSessionDialog.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
                setTitle("Exporting Session");
            }

            public void unbindproperties() {
                TotalProgress.progressProperty().unbind();
                CurrentProgress.progressProperty().unbind();
                StatusBar.textProperty().unbind();
                CurrentLabel.textProperty().unbind();
            }
        }
    public static class CutsMissingDialog  extends Stage {
            public Button AddMissingCutsButton;
            public ListView<Text> SessionListView;
            public Button CreateAnywayButton;
            public Button CancelCreationButton;
            private List<Cut> allcuts;
            private List<Cut> missingcuts;
            private Util.AnswerType result;
            private MainController Root;

            public CutsMissingDialog(MainController root, List<Cut> allcuts) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/CutsOutOfOrderOrMissing.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                    this.setOnCloseRequest(event -> dialogclosed());
                } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
                setTitle("Cuts Missing");
                this.allcuts = allcuts;
                populatelistview();
                Util.gui_showinformationdialog(Root, "Cuts Missing", "Due To The Nature Of Kuji-In, Each Cut Should Connect From RIN Up, Or The Later Cuts Might Lack The Energy They Need", "Use This Dialog To Connect Cuts, Or Cancel Without Creating");
            }

            public int getlastworkingcutindex() {
                int lastcutindex = 0;
                for (Cut i : allcuts) {
                    if (i.getduration().greaterThan(Duration.ZERO)) {lastcutindex = i.number;}
                }
                return lastcutindex;
            }
            public void populatelistview() {
                ObservableList<Text> sessionitems = FXCollections.observableArrayList();
                for (int i=0; i<getlastworkingcutindex(); i++) {
                    Text item = new Text();
                    StringBuilder currentcuttext = new StringBuilder();
                    Cut selectedcut = allcuts.get(i);
                    currentcuttext.append(selectedcut.number).append(". ").append(selectedcut.name);
                    if (selectedcut.getduration().greaterThan(Duration.ZERO)) {
                        currentcuttext.append(" (").append(Util.formatdurationtoStringSpelledOut(selectedcut.getduration(), SessionListView.getLayoutBounds().getWidth() - (currentcuttext.length() + 1)));
                        currentcuttext.append(")");
                    } else {
                        if (missingcuts == null) {missingcuts = new ArrayList<>();}
                        missingcuts.add(selectedcut);
                        currentcuttext.append(" (Missing Value!)");
                        item.setStyle("-fx-font-weight:bold; -fx-font-style: italic;");
                    }
                    item.setText(currentcuttext.toString());
                    sessionitems.add(item);
                }
                SessionListView.setItems(sessionitems);
            }
            public void addmissingcutstoSession(Event event) {
                if (missingcuts != null && missingcuts.size() > 0) {
                    CutInvocationDialog cutdurationdialog = new CutInvocationDialog(Root);
                    cutdurationdialog.showAndWait();
                    for (Cut i : missingcuts) {
                        if (cutdurationdialog.getDuration() != 0) {
                            i.setDuration(cutdurationdialog.getDuration());
                        }
                    }
                }
                setResult(Util.AnswerType.YES);
                this.close();
            }
            public void createSessionwithoutmissingcuts(Event event) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Session Not Well-Formed", "Really Create Anyway?")) {
                    setResult(Util.AnswerType.YES);
                    this.close();
                }
            }
            public void dialogclosed() {
                if (result == null && Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Close Dialog Without Creating", "This Will Return To The Creator")) {
                    setResult(Util.AnswerType.CANCEL);
                    this.close();
                }
            }
            public Util.AnswerType getResult() {
                return result;
            }
            public void setResult(Util.AnswerType result) {
                this.result = result;
            }
            public void cancelcreation(ActionEvent actionEvent) {
                setResult(Util.AnswerType.CANCEL);
                this.close();
            }
        }
    public static class CutInvocationDialog extends Stage {
            public Button CancelButton;
            public Button OKButton;
            public TextField MinutesTextField;
            private int duration;
            private MainController Root;

            public CutInvocationDialog(MainController root) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/CutInvocationDialog.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
                setTitle("Cut Invocation");
                MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {if (newValue.matches("\\d*")) {
                        MinutesTextField.setText(Integer.toString(Integer.parseInt(newValue)));}  else {
                        MinutesTextField.setText(oldValue);}}
                    catch (Exception e) {MinutesTextField.setText("");}
                });
                MinutesTextField.setText("0");
            }
            public int getDuration() {
                return duration;
            }
            public void setDuration(int duration) {
                this.duration = duration;
            }
            public void CancelButtonPressed(Event event) {
                setDuration(0);
                this.close();
            }
            public void OKButtonPressed(Event event) {
                try {
                    int value = Integer.parseInt(MinutesTextField.getText());
                    if (value != 0) {
                        setDuration(value);
                        this.close();
                    } else {
                        if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "Cut Invocation Value Is 0", "Continue With Zero Value (These Cuts Won't Be Included)" )) {
                            setDuration(0);
                            this.close();
                        }
                    }
                } catch (NumberFormatException e) {
                    Util.gui_showerrordialog(Root, "Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
            }
        }
    public static class SortSessionItems extends Stage {
            public TableView<SessionItem> SessionItemsTable;
            public TableColumn<SessionItem, Integer> NumberColumn;
            public TableColumn<SessionItem, String> NameColumn;
            public TableColumn<SessionItem, String> DurationColumn;
            public Button UpButton;
            public Button DownButton;
            public Button AcceptButton;
            public Button CancelButton;
            private List<Meditatable> sessionitems;
            private ObservableList<SessionItem> tableitems;
            private MainController Root;
            private Util.AnswerType result;

            public SortSessionItems(MainController Root, List<Meditatable> sessionitems) {
                this.sessionitems = sessionitems;
                this.Root = Root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SortSessionParts.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                    this.setOnCloseRequest(event -> dialogClosed());
                } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
                NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
                DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
                SessionItemsTable.setOnMouseClicked(event -> itemselected());
                tableitems = FXCollections.observableArrayList();
                UpButton.setDisable(true);
                DownButton.setDisable(true);
                populatetable();
            }

            public void itemselected() {
                int index = SessionItemsTable.getSelectionModel().getSelectedIndex();
                boolean validitemselected = index != -1;
                UpButton.setDisable(! validitemselected && index == 0);
                DownButton.setDisable(! validitemselected && index != SessionItemsTable.getItems().size() - 1);
            }
            public void populatetable() {
                SessionItemsTable.getItems().clear();
                tableitems.clear();
                int count = 1;
                for (Meditatable i : sessionitems) {
                    tableitems.add(new SessionItem(count, i.name, Util.formatdurationtoStringDecimalWithColons(i.getduration())));
                    count++;
                }
                SessionItemsTable.setItems(tableitems);
            }
            public void moveitemup(ActionEvent actionEvent) {
                int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
                if (selectedindex == -1) {return;}
                if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                    Util.gui_showinformationdialog(Root, "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                    return;
                }
                if (selectedindex == 0) {return;}
                Meditatable selecteditem = sessionitems.get(selectedindex);
                Meditatable oneitemup = sessionitems.get(selectedindex - 1);
                if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
                    if (selecteditem.number > oneitemup.number) {
                        Util.gui_showinformationdialog(Root, "Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                        return;
                    }
                }
                if (oneitemup instanceof Qi_Gong) {
                    Util.gui_showinformationdialog(Root, "Cannot Move", "Cannot Replace Presession", "Cannot Move");
                    return;
                }
                Collections.swap(sessionitems, selectedindex, selectedindex - 1);
                populatetable();
            }
            public void moveitemdown(ActionEvent actionEvent) {
                int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
                if (selectedindex == -1) {return;}
                if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                    Util.gui_showinformationdialog(Root, "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                    return;
                }
                if (selectedindex == tableitems.size() - 1) {return;}
                Meditatable selecteditem = sessionitems.get(selectedindex);
                Meditatable oneitemdown = sessionitems.get(selectedindex + 1);
                if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
                    if (selecteditem.number < oneitemdown.number) {
                        Util.gui_showinformationdialog(Root, "Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                        return;
                    }
                }
                if (oneitemdown instanceof Qi_Gong) {
                    Util.gui_showinformationdialog(Root, "Cannot Move", "Cannot Replace Postsession", "Cannot Move");
                    return;
                }
                Collections.swap(sessionitems, selectedindex, selectedindex + 1);
                populatetable();
            }
            public void cutcheck() {

            }
            public List<Meditatable> getorderedsessionitems() {
                return sessionitems;
            }
            public void accept(ActionEvent actionEvent) {
                close();
            }
            public void cancel(ActionEvent actionEvent) {
                sessionitems = null;
                close();
            }
            public void dialogClosed() {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Cancel Creation", "Cancel Creation", "This Will Return To The Creator Main Window")) {
                    setResult(Util.AnswerType.CANCEL);
                    this.close();
                }
            }

            public Util.AnswerType getResult() {
                return result;
            }
            public void setResult(Util.AnswerType result) {
                this.result = result;
            }

            class SessionItem {
                private IntegerProperty number;
                private StringProperty name;
                private StringProperty duration;

                public SessionItem(int number, String name, String duration) {
                    this.number = new SimpleIntegerProperty(number);
                    this.name = new SimpleStringProperty(name);
                    this.duration = new SimpleStringProperty(duration);
                }
            }
        }
    public static class AllMeditatablesGoalProgress extends Stage {
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
        private Meditatable selectedmeditatable;
        public MainController Root;
        private String setgoaltext = "Set Goal";
        private String goalpacingtext = "Current Goal Pacing";

        public AllMeditatablesGoalProgress(MainController Root, Meditatable selectedmeditatable) {
            this.Root = Root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AllMeditatablesGoalProgress.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
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
                if (selectedmeditatable != null) {GoalsTable.getSelectionModel().select(selectedmeditatable.number);}
                this.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        // TODO Check If Goals Set For All Meditatables, If Not Display Confirmation Dialog

                    }
                });
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
        }

        public void populatetable() {
            allgoalsdetails.clear();
            for (Meditatable i : Root.getSession().getAllMeditatablesincludingTotalforTracking()) {
                String practicedtime = Util.formatdurationtoStringSpelledOut(new Duration(i.getTotalMinutesPracticed(false)), null);
                if (practicedtime.equals("0 Minutes")) {
                    // TODO None Text Here! No Practiced Time Set
                }
                String currentgoaltime;
                String percentcompleted ;
                try {
                    currentgoaltime = Util.formatdurationtoStringSpelledOut(new Duration(i.getCurrentGoal().getGoal_Hours() * 3_600_000), null);
                    percentcompleted = String.valueOf(new Double((i.getTotalMinutesPracticed(false) / (i.getCurrentGoal().getGoal_Hours() * 60)) * 100).intValue()) + "%";
                } catch (NullPointerException ignored) {
                    currentgoaltime = "No Goal Set";
                    percentcompleted = "No Goal Set";
                }
                allgoalsdetails.add(new GoalProgressBinding(i.name, practicedtime, currentgoaltime, percentcompleted, i.getcompletedgoalcount()));
            }
            GoalsTable.setItems(allgoalsdetails);
        }
        public void newrowselected() {
            if (GoalsTable.getSelectionModel().getSelectedIndex() == -1) {selectedmeditatable = null;}
            else {selectedmeditatable = Root.getSession().getAllMeditatablesincludingTotalforTracking().get(GoalsTable.getSelectionModel().getSelectedIndex());}
            if (selectedmeditatable == null) {
                SetCurrentGoalButton.setDisable(true);
                ViewCompletedGoalsButton.setDisable(true);
            } else {
                SetCurrentGoalButton.setDisable(false);
                if (selectedmeditatable.getCurrentGoal() == null) {SetCurrentGoalButton.setText(setgoaltext);}
                else {SetCurrentGoalButton.setText(goalpacingtext);}
                ViewCompletedGoalsButton.setDisable(selectedmeditatable.getcompletedgoalcount() == 0);
            }
        }
        public void setcurrentgoal(ActionEvent actionEvent) {
            if (selectedmeditatable != null) {
                SimpleGoalSetDialog setDialog = new SimpleGoalSetDialog(Root, selectedmeditatable);
                setDialog.showAndWait();
                if (setDialog.shouldSetgoal()) {
                    selectedmeditatable.addGoal(new Goals.Goal(setDialog.getNewGoalHours(), selectedmeditatable));
                    populatetable();
                }
            }
        }
        public void viewcompletedgoals(ActionEvent actionEvent) {
            if (selectedmeditatable != null) {

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
    public static class SimpleGoalSetDialog extends Stage {
        public MainController Root;
        public Label TopLabel;
        public Spinner<Integer> HoursSpinner;
        public Spinner<Integer> MinutesSpinner;
        public TextField DecimalHoursTextField;
        public Label StatusBar;
        public Button AcceptButton;
        public Button CancelButton;
        private Meditatable SelectedMeditatable;
        private boolean setgoal = false;
        private int practicedminutes;

        public SimpleGoalSetDialog(MainController root, Meditatable selectedmeditatable) {
            try {
                Root = root;
                SelectedMeditatable = selectedmeditatable;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SetGoalDialog_Simple.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Set A New Goal");
                HoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                MinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> checkvalue());
                HoursSpinner.getValueFactory().setValue(SelectedMeditatable.getTotalMinutesPracticed(false) / 60);
                MinutesSpinner.getValueFactory().setValue(SelectedMeditatable.getTotalMinutesPracticed(false) % 60);
                Util.custom_spinner_integer(HoursSpinner, 0, Integer.MAX_VALUE, 1, false);
                Util.custom_spinner_integer(MinutesSpinner, 0, 59, 1, false);
                practicedminutes = SelectedMeditatable.getTotalMinutesPracticed(false);
                DecimalHoursTextField.setText(Util.convert_minstodecimalhours(practicedminutes, 1).toString());
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
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
                Util.gui_showinformationdialog(Root, "Cannot Accept", "Goal Is Less Than Or Equal To Practiced Minutes", "Goal Must Be Greater Than Practiced Minutes");
                return;
            }
            setgoal = true;
        }

    }
    public static class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField TotalPracticedTime;
        public Label TopLabel;
        private MainController Root;
        private Double practicedhours;
        private Double goalhours;
        private Double hoursleft;

        public GoalPacingDialog(MainController root, Meditatable meditatable) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                setTitle("Goal Pacing");
                practicedhours = (double) (meditatable.getTotalMinutesPracticed(false) / 60);
                goalhours = meditatable.getCurrentGoal().getGoal_Hours();
                GoalDuration.setText(Util.formatdurationtoStringSpelledOut(new Duration(goalhours * 3_600_000), GoalDuration.getLayoutBounds().getWidth()));
                TotalPracticedTime.setText(Util.formatdurationtoStringSpelledOut(new Duration(practicedhours * 3600000), TotalPracticedTime.getLayoutBounds().getWidth()));
                hoursleft = goalhours - practicedhours;
                GoalTimeLeft.setText(Util.formatdurationtoStringSpelledOut(new Duration(hoursleft * 3600000), GoalTimeLeft.getLayoutBounds().getWidth()));
                Util.custom_spinner_integer(PracticeDays, 1, Integer.MAX_VALUE, 1, false);
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> calculate());
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
                TopLabel.setText("Goal Pacing For " + meditatable.name + " Current Goal");
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
        }

        // Other Methods
        public void calculate() {
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = hoursleft.floatValue() / days.floatValue();
            int minsaday = Util.convert_decimalhourstominutes(hourstopractice.doubleValue());
            PracticeTimeADay.setText(Util.formatdurationtoStringSpelledOut(new Duration((minsaday * 60) * 1000), PracticeTimeADay.getLayoutBounds().getWidth()));
        }
    }
    public static class AllSessionsDetails extends Stage {
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
        private MainController Root;

        public AllSessionsDetails(MainController root) {
            Root = root;
            allsessionslist = Root.getSessions().getSession();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_All.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
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
            for (Meditatable i : Root.getSession().getAllMeditatablesincludingTotalforTracking()) {
                if (! (i instanceof Total)) {
                    series.getData().add(new XYChart.Data<>(i.getNameForChart(), Util.convert_minstodecimalhours(i.getTotalMinutesPracticed(false), 1)));
                    piecesofthepie.add(new PieChart.Data(i.getNameForChart(), Util.convert_minstodecimalhours(i.getTotalMinutesPracticed(false), 1)));
                }
                totalprogressrows.add(new TotalProgressRow(i.getNameForChart(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getTotalMinutesPracticed(false) * 60000))));
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
                                    if (i.getcutduration(j) <= Integer.parseInt(Filter_ThresholdMinutesTextField.getText())) {validsession = false;}
                                } catch (NumberFormatException | NullPointerException ignored) {validsession = i.getcutduration(j) > 0;}
                            } else {if (i.getcutduration(j) == 0) {validsession = false;}}
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
            int index = sessionsTableView.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                new SessionDetails(Root, filteredsessionlist.get(index)).showAndWait();
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
    public static class ExportDialog extends Stage {
        private File finalexportfile;
        private File tempentrainmenttextfile;
        private File tempambiencetextfile;
        private File tempentrainmentfile;
        private File tempambiencefile;
        private File finalentrainmentfile;
        private File finalambiencefile;


        public ExportDialog() {

        }


    }
    public static class PlayerUI extends Stage {
        public Button PlayButton;
        public Button PauseButton;
        public Button StopButton;
        public Label StatusBar;
        public RadioButton ReferenceHTMLButton;
        public RadioButton ReferenceTXTButton;
        public Slider EntrainmentVolume;
        public Label EntrainmentVolumePercentage;
        public Slider AmbienceVolume;
        public Label AmbienceVolumePercentage;
        public Label CurrentCutTopLabel;
        public Label CutCurrentLabel;
        public ProgressBar CurrentCutProgress;
        public Label CutTotalLabel;
        public Label TotalCurrentLabel;
        public ProgressBar TotalProgress;
        public Label TotalTotalLabel;
        public Label TotalSessionLabel;
        public Label GoalTopLabel;
        public ProgressBar GoalProgressBar;
        public ToggleButton ReferenceToggleButton;
        public Label GoalPercentageLabel;
        private This_Session Session;
        private MainController Root;
        public boolean displaynormaltime = true;

        // TODO Fix Set Multiple Goal Minutes (And Add Check If Long Enough Logic On Accepting)
        // TODO Select Button On Options -> ChangeAlertFileDialog Instead Of Just A File Chooser
        // TODO Display Short Cut Descriptions (Power/Responsibility... On The Player Widget While Playing)
        // TODO Create Goal Progress Similar To Session Details And Add To Session Details Dialog
        // TODO Confirmation -> Alert File On LONG Sessions (Deep In Trance)
        public PlayerUI(MainController root) {
            try {
                Root = root;
                Session = Root.getSession();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionPlayerDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions();
                Root.getOptions().setStyle(this);
                setTitle("Session Player");
                reset();
                boolean referenceoption = Root.getOptions().getSessionOptions().getReferenceoption();
                ReferenceType referenceType = Root.getOptions().getSessionOptions().getReferencetype();
                if (referenceoption && referenceType != null && Session.checkallreferencefilesforsession(referenceType, false)) {ReferenceToggleButton.setSelected(true);}
                else {ReferenceToggleButton.setSelected(false);}
                togglereference(null);
                ReferenceToggleButton.setSelected(Root.getOptions().getSessionOptions().getReferenceoption());
                setResizable(false);
                CutTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                TotalTotalLabel.setOnMouseClicked(event -> displaynormaltime = !displaynormaltime);
                setOnCloseRequest(event -> {
                    if (Session.getPlayerState() == PlayerState.PLAYING || Session.getPlayerState() == PlayerState.STOPPED || Session.getPlayerState() == PlayerState.PAUSED || Session.getPlayerState() == PlayerState.IDLE) {
                        if (Session.endsessionprematurely()) {close(); cleanupPlayer();} else {play(); event.consume();}
                    } else {
                        Util.gui_showtimedmessageonlabel(StatusBar, "Cannot Close Player During Fade Animation", 400);
                        new Timeline(new KeyFrame(Duration.millis(400), ae -> Session.getCurrentmeditatable().toggleplayerbuttons()));
                        event.consume();
                    }
                });
            } catch (Exception e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
        }

        // Button Actions
        public void play() {Session.play(this);}
        public void pause() {Session.pause();}
        public void stop() {Session.stop();}
        public void togglereference(ActionEvent actionEvent) {
            boolean buttontoggled = ReferenceToggleButton.isSelected();
            Root.getOptions().getSessionOptions().setReferenceoption(buttontoggled);
            ReferenceHTMLButton.setDisable(! buttontoggled);
            ReferenceTXTButton.setDisable(! buttontoggled);
            if (! buttontoggled) {
                ReferenceHTMLButton.setSelected(false);
                ReferenceTXTButton.setSelected(false);
                Root.getOptions().getSessionOptions().setReferencetype(null);
                Session.closereferencefile();
                Session.togglevolumebinding();
            } else {
                if (Root.getOptions().getSessionOptions().getReferencetype() == null) {Root.getOptions().getSessionOptions().setReferencetype(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);}
                switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                    case html:
                        ReferenceHTMLButton.setSelected(true);
                        htmlreferenceoptionselected(null);
                        break;
                    case txt:
                        ReferenceTXTButton.setSelected(true);
                        txtreferenceoptionselected(null);
                        break;
                }
                if (! Session.checkallreferencefilesforsession(Root.getOptions().getSessionOptions().getReferencetype(), true)) {
                    ReferenceToggleButton.setSelected(false);
                    togglereference(null);
                }
                if (Session.getPlayerState() == PlayerState.PLAYING) {
                    Session.displayreferencefile();
                    Session.togglevolumebinding();
                }
            }
        }
        public void htmlreferenceoptionselected(ActionEvent actionEvent) {
            if (ReferenceToggleButton.isSelected()) {
                ReferenceTXTButton.setSelected(! ReferenceHTMLButton.isSelected());
                if (ReferenceHTMLButton.isSelected()) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.html);}
                else {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.txt);}
            } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
        }
        public void txtreferenceoptionselected(ActionEvent actionEvent) {
            if (ReferenceToggleButton.isSelected()) {
                ReferenceHTMLButton.setSelected(! ReferenceTXTButton.isSelected());
                if (ReferenceTXTButton.isSelected()) {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.txt);}
                else {Root.getOptions().getSessionOptions().setReferencetype(ReferenceType.html);}
            } else {Root.getOptions().getSessionOptions().setReferencetype(null);}
        }
        public void cleanupPlayer() {}
        public void reset() {
            CutCurrentLabel.setText("--:--");
            CurrentCutProgress.setProgress(0.0);
            CutTotalLabel.setText("--:--");
            TotalCurrentLabel.setText("--:--");
            TotalProgress.setProgress(0.0);
            TotalTotalLabel.setText("--:--");
            EntrainmentVolume.setDisable(true);
            EntrainmentVolumePercentage.setText("0%");
            AmbienceVolume.setDisable(true);
            AmbienceVolumePercentage.setText("0%");
            // TODO Reset Goal UI Here
            PlayButton.setText("Start");
            PauseButton.setDisable(true);
            StopButton.setDisable(true);
        }

        // Dialogs
        public static class DisplayReference extends Stage {
            public ScrollPane ContentPane;
            public Slider EntrainmentVolumeSlider;
            public Label EntrainmentVolumePercentage;
            public Slider AmbienceVolumeSlider;
            public Label AmbienceVolumePercentage;
            public Button PlayButton;
            public Button PauseButton;
            public Button StopButton;
            public ProgressBar TotalProgress;
            public ProgressBar CurrentProgress;
            public Label CurrentName;
            public Label CurrentPercentage;
            public Label TotalPercentage;
            private MainController Root;
            private Meditatable currentmeditatable;
            private ReferenceType referenceType;
            private Boolean fullscreenoption;
            private Scene scene;

            public DisplayReference(MainController root, Meditatable currentmeditatable) {
                try {
                    Root = root;
                    this.currentmeditatable = currentmeditatable;
                    referenceType = Root.getOptions().getSessionOptions().getReferencetype();
                    fullscreenoption = Root.getOptions().getSessionOptions().getReferencefullscreen();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ReferenceDisplay.fxml"));
                    fxmlLoader.setController(this);
                    scene = new Scene(fxmlLoader.load());
                    setScene(scene);
                    Root.getOptions().setStyle(this);
//                this.setResizable(false);
                    setTitle(this.currentmeditatable.name + "'s Reference");
                    setsizing();
                    loadcontent();
                    AmbienceVolumeSlider.setValue(Root.getPlayer().AmbienceVolume.getValue());
                    AmbienceVolumePercentage.setText(Root.getPlayer().AmbienceVolumePercentage.getText());
                    EntrainmentVolumeSlider.setValue(Root.getPlayer().EntrainmentVolume.getValue());
                    EntrainmentVolumePercentage.setText(Root.getPlayer().EntrainmentVolumePercentage.getText());
                    setOnCloseRequest(event -> untoggleplayerreference());
                    if (Root.getSession().getCurrentindexofplayingelement() == 0) {
                        setFullScreenExitHint("Press F11 To Toggle Fullscreen, ESC To Hide Reference");
                    } else {setFullScreenExitHint("");}
                    addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        switch (event.getCode()) {
                            case ESCAPE:
                                // TODO Closing Reference Display On Escape Is Crashing The Whole App
//                                hide();
//                                untoggleplayerreference();
//                                break;
                            case F11:
                                if (Root.getSession().getPlayerState() == PlayerState.PLAYING) {
                                    boolean fullscreen = this.isFullScreen();
                                    fullscreenoption = !fullscreen;
                                    Root.getOptions().getSessionOptions().setReferencefullscreen(fullscreenoption);
                                    setsizing();
                                    if (!fullscreen) {setFullScreenExitHint("");}
                                    break;
                                }
                        }
                    });
                } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            }
            public DisplayReference(MainController root, String htmlcontent) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ReferencePreview.fxml"));
                fxmlLoader.setController(this);
                try {
                    scene = new Scene(fxmlLoader.load());
                    setScene(scene);
                    Root.getOptions().setStyle(this);
                    this.setResizable(false);
                } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
                setTitle("Reference File Preview");
                fullscreenoption = false;
                setsizing();
                WebView browser = new WebView();
                WebEngine webEngine = browser.getEngine();
                webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
                webEngine.loadContent(htmlcontent);
                ContentPane.setContent(browser);
            }

            public void setsizing() {
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                double height = primaryScreenBounds.getHeight();
                double width = primaryScreenBounds.getWidth();
                if (! fullscreenoption) {height -= 100; width -= 100;}
                this.setFullScreen(fullscreenoption);
                this.setHeight(height);
                this.setWidth(width);
                this.centerOnScreen();
                ContentPane.setFitToWidth(true);
                ContentPane.setFitToHeight(true);
                ContentPane.setStyle("-fx-background-color: #212526");
            }
            public void loadcontent() {
                File referencefile = currentmeditatable.getReferenceFile();
                if (referencefile != null) {
                    switch (referenceType) {
                        case txt:
                            StringBuilder sb = new StringBuilder();
                            try (FileInputStream fis = new FileInputStream(referencefile);
                                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                                while (bis.available() > 0) {
                                    sb.append((char) bis.read());
                                }
                            } catch (Exception e) {
                                new MainController.ExceptionDialog(Root, e).showAndWait();
                            }
                            TextArea ta = new TextArea();
                            ta.setText(sb.toString());
                            ta.setWrapText(true);
                            ContentPane.setContent(ta);
                            Root.getOptions().setStyle(this);
                            break;
                        case html:
                            WebView browser = new WebView();
                            WebEngine webEngine = browser.getEngine();
                            webEngine.load(referencefile.toURI().toString());
                            webEngine.setUserStyleSheetLocation(new File(kujiin.xml.Options.DIRECTORYSTYLES, "referencefile.css").toURI().toString());
                            ContentPane.setContent(browser);
                            break;
                        default:
                            break;
                    }
                } else {System.out.println("Reference File Is Null");}
            }
            public void untoggleplayerreference() {
                Root.getPlayer().ReferenceToggleButton.setSelected(false);
                Root.getPlayer().togglereference(null);
            }

            public void play(ActionEvent actionEvent) {Root.getSession().play(Root.getPlayer());}
            public void pause(ActionEvent actionEvent) {Root.getSession().pause();}
            public void stop(ActionEvent actionEvent) {Root.getSession().stop();}

        }


    }
    public static class SimpleTextDialogWithCancelButton extends Stage {
        public Button CancelButton;
        public Label Message;
        public Label TopTitle;
        private MainController Root;

        public SimpleTextDialogWithCancelButton(MainController root, String titletext, String toptitletext, String message) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleTextDialogWithCancelButton.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(titletext);
            Message.setText(message);
            TopTitle.setText(toptitletext);
        }
    }
    public static class LoadingDialog extends Stage {
        public Label Message;
        public ProgressIndicator Progress;

        public LoadingDialog(MainController Root, String titletext, String message) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/LoadingDialog.fxml"));
                fxmlLoader.setController(this);
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(Event::consume);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(titletext);
            Message.setText(message);
        }
    }

    // Enums
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }
    public enum ReferenceType {
        html, txt
    }
    public enum PlayerState {
        PLAYING, PAUSED, STOPPED, TRANSITIONING, IDLE, FADING_PLAY, FADING_RESUME, FADING_PAUSE, FADING_STOP
    }
}
