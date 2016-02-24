package kujiin.widgets;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.Cut;
import kujiin.MainController;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.Goals;
import kujiin.xml.Options;
import kujiin.xml.Preset;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

// TODO Get FFMPEG Working To Mix Audio Files Together
    // Not Supported Stream?
public class CreatorAndExporterWidget implements Widget {
    private Button ChangeAllValuesButton;
    private Button ExportButton;
    private Button LoadPresetButton;
    private Button SavePresetButton;
    private Button CreateButton;
    private CheckBox AmbienceSwitch;
    private TextField TotalSessionTime;
    private TextField ApproximateEndTime;
    private Label PreLabel;
    private Label RinLabel;
    private Label KyoLabel;
    private Label TohLabel;
    private Label ShaLabel;
    private Label KaiLabel;
    private Label JinLabel;
    private Label RetsuLabel;
    private Label ZaiLabel;
    private Label ZenLabel;
    private Label PostLabel;
    private Label LengthLabel;
    private Label CompletionLabel;
    private TextField PreTime;
    private TextField RinTime;
    private TextField KyoTime;
    private TextField TohTime;
    private TextField ShaTime;
    private TextField KaiTime;
    private TextField JinTime;
    private TextField RetsuTime;
    private TextField ZaiTime;
    private TextField ZenTime;
    private TextField PostTime;
    private Label StatusBar;
    private IntegerProperty PresessionValue = new SimpleIntegerProperty(0);
    private IntegerProperty RinValue = new SimpleIntegerProperty(0);
    private IntegerProperty KyoValue = new SimpleIntegerProperty(0);
    private IntegerProperty TohValue = new SimpleIntegerProperty(0);
    private IntegerProperty ShaValue = new SimpleIntegerProperty(0);
    private IntegerProperty KaiValue = new SimpleIntegerProperty(0);
    private IntegerProperty JinValue = new SimpleIntegerProperty(0);
    private IntegerProperty RetsuValue = new SimpleIntegerProperty(0);
    private IntegerProperty ZaiValue = new SimpleIntegerProperty(0);
    private IntegerProperty ZenValue = new SimpleIntegerProperty(0);
    private IntegerProperty PostsessionValue = new SimpleIntegerProperty(0);
    private ExporterState exporterState;
    private CreatorState creatorState;
    private This_Session session;
    private ArrayList<Integer> textfieldtimes = new ArrayList<>(11);
    private ArrayList<Service<Boolean>> exportservices;
    private Service<Boolean> currentexporterservice;
    private Integer exportserviceindex;
    private ExportingSessionDialog exportingSessionDialog;
    private Preset Preset;
    private Timeline updateuitimeline;
    private MainController Root;

    public CreatorAndExporterWidget(MainController root) {
        Root = root;
        LoadPresetButton = root.LoadPresetButton;
        SavePresetButton = root.SavePresetButton;
        ChangeAllValuesButton = root.ChangeValuesButton;
        CreateButton = root.CreateButton;
        ExportButton = root.ExportButton;
        AmbienceSwitch = root.AmbienceSwitch;
        TotalSessionTime = root.TotalSessionTime;
        ApproximateEndTime = root.ApproximateEndTime;
        Preset = new Preset(root);
        PreLabel = root.PreLabel;
        PreTime = root.PreTime;
        RinLabel = root.RinLabel;
        RinTime = root.RinTime;
        KyoLabel = root.KyoLabel;
        KyoTime = root.KyoTime;
        TohLabel = root.TohLabel;
        TohTime = root.TohTime;
        ShaLabel = root.ShaLabel;
        ShaTime = root.ShaTime;
        KaiLabel = root.KaiLabel;
        KaiTime = root.KaiTime;
        JinLabel = root.JinLabel;
        JinTime = root.JinTime;
        RetsuLabel = root.RetsuLabel;
        RetsuTime = root.RetsuTime;
        ZaiLabel = root.ZaiLabel;
        ZaiTime = root.ZaiTime;
        ZenLabel = root.ZenLabel;
        ZenTime = root.ZenTime;
        PostLabel = root.PostLabel;
        PostTime = root.PostTime;
        LengthLabel = root.LengthLabel;
        CompletionLabel = root.CompletionLabel;
        session = root.getSession();
        StatusBar = root.CreatorStatusBar;
        exporterState = ExporterState.NOT_EXPORTED;
        creatorState = CreatorState.NOT_CREATED;
        setuptextfields();
        textfieldtimes.addAll(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        PreLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        RinLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        KyoLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        TohLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        ShaLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        KaiLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        JinLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        RetsuLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        ZaiLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        ZenLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        PostLabel.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        TotalSessionTime.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        ApproximateEndTime.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        exportservices = new ArrayList<>();
        updateuitimeline = new Timeline(new KeyFrame(Duration.millis(60000), ae -> updatecreatorui()));
        updateuitimeline.setCycleCount(Animation.INDEFINITE);
        updatecreatorui();
    }

// Getters And Setters
    public ExporterState getExporterState() {
        return exporterState;
    }
    public void setExporterState(ExporterState exporterState) {
        this.exporterState = exporterState;
    }
    public CreatorState getCreatorState() {
        return creatorState;
    }
    public void setCreatorState(CreatorState creatorState) {
        this.creatorState = creatorState;
    }

// Creation
    public void togglecreator() {
        if (session.getPlayerState() == PlayerWidget.PlayerState.PLAYING ||
            session.getPlayerState() == PlayerWidget.PlayerState.PAUSED ||
            session.getPlayerState() == PlayerWidget.PlayerState.TRANSITIONING) {
            if (Tools.getanswerdialog(Root, "Stop Session", "In Order To Edit Session Values The Session Player Must Be Stopped And Reset", "Stop And Reset Session Player?")) {
                if (! session.stop().equals("Session Stopped")) {return;}
            } else {return;}
        }
        // TODO Check Exporter Here
        if (creatorState == CreatorState.NOT_CREATED) {
            if (creationchecks()) {
                session.setAmbienceenabled(AmbienceSwitch.isSelected());
                session.create(textfieldtimes);
                disable();
                ExportButton.setDisable(false);
                CreateButton.setText("Edit");
                session.Root.getPlayer().onOffSwitch.setDisable(false);
                session.Root.getPlayer().StatusBar.setText("Player Disabled Until Session Is Created Or Loaded");
                setCreatorState(CreatorState.CREATED);
                Tools.showinformationdialog(Root, "Success", "Session Successfully Created", "You Can Now Play Or Export This Session");
            }
        } else {
            enable();
            ExportButton.setDisable(true);
            CreateButton.setText("Create");
            session.Root.getPlayer().onOffSwitch.setSelected(false);
            session.Root.getPlayer().statusSwitch();
            session.Root.getPlayer().onOffSwitch.setDisable(true);
            session.Root.getPlayer().onOffSwitch.setTooltip(new Tooltip("Create A Session To Enable The Session Player"));
            setCreatorState(CreatorState.NOT_CREATED);
        }
    }
    public boolean creationchecks() {
        if (! gettextfieldtimes()) {Tools.showerrordialog(Root, "Error Creating Session", "At Least One Cut's Value Must Not Be 0", "Cannot Create Session"); return false;}
        if (! session.checksessionwellformedness(textfieldtimes)) {return false;}
        ArrayList<Integer> notgoodongoals = session.Root.getProgressTracker().precreationgoalchecks(textfieldtimes);
        if (! notgoodongoals.isEmpty()) {
            StringBuilder notgoodtext = new StringBuilder();
            for (int i = 0; i < notgoodongoals.size(); i++) {
                notgoodtext.append(ProgressAndGoalsWidget.GOALCUTNAMES[i]);
                if (i != notgoodtext.length() - 1) {notgoodtext.append(", ");}
                if (i == notgoodongoals.size() / 2) {notgoodtext.append("\n");}
            }
            if (Tools.getanswerdialog(Root, "Confirmation", "Goals Aren't Long Enough For \n" + notgoodtext.toString(), "Set Goals For These Cuts Before Creating This Session?")) {
                ProgressAndGoalsWidget.SetANewGoalForMultipleCuts s = new ProgressAndGoalsWidget.SetANewGoalForMultipleCuts(Root, notgoodongoals, Tools.getmaxvalue(notgoodongoals));
                s.showAndWait();
                if (s.isAccepted()) {
                    List<Integer> cutindexes = s.getSelectedCutIndexes();
                    Double goalhours = s.getGoalhours();
                    LocalDate goaldate = s.getGoaldate();
                    boolean goalssetsuccessfully = true;
                    for (Integer i : cutindexes) {
                        try {
                            Root.getProgressTracker().getGoal().add(i, new Goals.Goal(goaldate, goalhours, ProgressAndGoalsWidget.GOALCUTNAMES[i]));}
                        catch (JAXBException ignored) {goalssetsuccessfully = false; Tools.showerrordialog(Root, "Error", "Couldn't Add Goal For " + ProgressAndGoalsWidget.GOALCUTNAMES[i], "Check File Permissions");}
                    }
                    if (goalssetsuccessfully) {Tools.showinformationdialog(Root, "Information", "Goals For " + notgoodtext.toString() + "Set Successfully", "Session Will Now Be Created");}
                }
                return true;
            }
        } else {return true;}
        return false;
    }

// Export
    public void toggleexport() {
        if (exporterState == ExporterState.NOT_EXPORTED) {

        } else if (exporterState == ExporterState.WORKING) {

        } else if (exporterState == ExporterState.FAILED) {

        } else if (exporterState == ExporterState.COMPLETED) {

        } else if (exporterState == ExporterState.CANCELLED) {

        } else {

        }
    }
    public void startexport() {
        if (creationchecks()) {
            if (getExporterState() == ExporterState.NOT_EXPORTED) {
                if (checkforffmpeg()) {
                    if (session.getExportfile() == null) {
                        session.getnewexportsavefile();
                    } else {
                        // TODO Continue Fixing Logic Here
                        if (session.getExportfile().exists()) {
                            if (!Tools.getanswerdialog(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
                                session.getnewexportsavefile();
                            }
                        } else {session.getnewexportsavefile();}
                    }
                    if (session.getExportfile() == null) {Tools.showtimedmessage(StatusBar, "Export Session Cancelled", 3000); return;}
                    exportserviceindex = 0;
                    ArrayList<Cut> cutsinsession = session.getCutsinsession();
                    for (Cut i : cutsinsession) {
                        exportservices.add(i.getcutexportservice());
                    }
                    exportservices.add(session.getsessionexporter());
                    exportingSessionDialog = new ExportingSessionDialog(Root);
                    exportingSessionDialog.show();
                    setExporterState(ExporterState.WORKING);
                    exportnextservice();
                } else {
                    Tools.showerrordialog(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
                    // TODO Open A Browser Showing How To Install FFMPEG
                }
            } else if (getExporterState() == ExporterState.WORKING) {
                Tools.showtimedmessage(StatusBar, "Session Currently Being Exported", 3000);
            } else {
                if (Tools.getanswerdialog(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
                    setExporterState(ExporterState.NOT_EXPORTED);
                    startexport();
                }
            }
        } else {Tools.showinformationdialog(Root, "Information", "Cannot Export", "No Cuts Selected");}
    }
    private void exportnextservice() {
//        System.out.println("Starting Next Export Service");
        exportingSessionDialog.TotalProgress.setProgress((double) exportserviceindex / exportservices.size());
        try {
            currentexporterservice = exportservices.get(exportserviceindex);
            currentexporterservice.setOnRunning(event -> {
                exportingSessionDialog.CurrentProgress.progressProperty().bind(currentexporterservice.progressProperty());
                exportingSessionDialog.StatusBar.textProperty().bind(currentexporterservice.messageProperty());
                exportingSessionDialog.CurrentLabel.textProperty().bind(currentexporterservice.titleProperty());
            });
            currentexporterservice.setOnSucceeded(event -> {exportingSessionDialog.unbindproperties(); exportserviceindex++; exportnextservice();});
            currentexporterservice.setOnCancelled(event -> exportcancelled());
            currentexporterservice.setOnFailed(event -> exportfailed());
            currentexporterservice.start();
        } catch (ArrayIndexOutOfBoundsException ignored) {exportfinished();}
    }
    public void exportfailed() {
        System.out.println(currentexporterservice.getException().getMessage());
        System.out.println("Failed!");
        setExporterState(ExporterState.FAILED);}
    public void exportcancelled() {
        System.out.println("Cancelled!");
        setExporterState(ExporterState.CANCELLED);}
    public void exportfinished() {
        System.out.println("Export Finished!");
        setExporterState(ExporterState.COMPLETED);
    }

// Other Methods
    public boolean checkforffmpeg() {
    boolean good = false;
    try {
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("ffmpeg", "-version"));
        final Process checkforffmpeg = processBuilder.start();
        checkforffmpeg.waitFor();
        good = checkforffmpeg.exitValue() == 0;
    } catch (IOException | InterruptedException ignored) {good = false;}
    return good;
}
    public void setuptextfields() {
        Tools.integerTextField(PreTime, true);
        Tools.integerTextField(RinTime, true);
        Tools.integerTextField(KyoTime, true);
        Tools.integerTextField(TohTime, true);
        Tools.integerTextField(ShaTime, true);
        Tools.integerTextField(KaiTime, true);
        Tools.integerTextField(JinTime, true);
        Tools.integerTextField(RetsuTime, true);
        Tools.integerTextField(ZaiTime, true);
        Tools.integerTextField(ZenTime, true);
        Tools.integerTextField(PostTime, true);
        PreTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {PresessionValue.set(Integer.valueOf(newValue)); textfieldtimes.set(0, PresessionValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {PreTime.setText("0"); PresessionValue.set(0); textfieldtimes.set(0, 0); updatecreatorui();}
        });
        RinTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {RinValue.set(Integer.valueOf(newValue)); textfieldtimes.set(1, RinValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {RinTime.setText("0"); RinValue.set(0); textfieldtimes.set(1, 0); updatecreatorui();}
        });
        KyoTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {KyoValue.set(Integer.valueOf(newValue)); textfieldtimes.set(2, KyoValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {KyoTime.setText("0"); KyoValue.set(0); textfieldtimes.set(2, 0); updatecreatorui();}
        });
        TohTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {TohValue.set(Integer.valueOf(newValue)); textfieldtimes.set(3, TohValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {TohTime.setText("0"); TohValue.set(0); textfieldtimes.set(3, 0); updatecreatorui();}
        });
        ShaTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {ShaValue.set(Integer.valueOf(newValue)); textfieldtimes.set(4, ShaValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {ShaTime.setText("0"); ShaValue.set(0); textfieldtimes.set(4, 0); updatecreatorui();}
        });
        KaiTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {KaiValue.set(Integer.valueOf(newValue)); textfieldtimes.set(5, KaiValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {KaiTime.setText("0"); KaiValue.set(0); textfieldtimes.set(5, 0); updatecreatorui();}
        });
        JinTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {JinValue.set(Integer.valueOf(newValue)); textfieldtimes.set(6, JinValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {JinTime.setText("0"); JinValue.set(0); textfieldtimes.set(6, 0); updatecreatorui();}
        });
        RetsuTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {RetsuValue.set(Integer.valueOf(newValue)); textfieldtimes.set(7, RetsuValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {RetsuTime.setText("0"); RetsuValue.set(0); textfieldtimes.set(7, 0); updatecreatorui();}
        });
        ZaiTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {ZaiValue.set(Integer.valueOf(newValue)); textfieldtimes.set(8, ZaiValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {ZaiTime.setText("0"); ZaiValue.set(0); textfieldtimes.set(8, 0); updatecreatorui();}
        });
        ZenTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {ZenValue.set(Integer.valueOf(newValue)); textfieldtimes.set(9, ZenValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {ZenTime.setText("0"); ZenValue.set(0); textfieldtimes.set(9, 0); updatecreatorui();}
        });
        PostTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {PostsessionValue.set(Integer.valueOf(newValue)); textfieldtimes.set(10, PostsessionValue.get()); updatecreatorui();}
            catch (NumberFormatException ignored) {PostTime.setText("0"); PostsessionValue.set(0); textfieldtimes.set(10, 0); updatecreatorui();}
        });
        PreTime.setTooltip(new Tooltip("Minutes To Meditate Before Practicing Selected Cut(s). There Is A 2 Minute Ramp Into Whichever First Cut You Are Working On"));
        RinTime.setTooltip(new Tooltip("Minutes You Want To Practice RIN"));
        KyoTime.setTooltip(new Tooltip("Minutes You Want To Practice KYO"));
        TohTime.setTooltip(new Tooltip("Minutes You Want To Practice TOH"));
        ShaTime.setTooltip(new Tooltip("Minutes You Want To Practice SHA"));
        KaiTime.setTooltip(new Tooltip("Minutes You Want To Practice KAI"));
        JinTime.setTooltip(new Tooltip("Minutes You Want To Practice JIN"));
        RetsuTime.setTooltip(new Tooltip("Minutes ou Want To Practice RETSU"));
        ZaiTime.setTooltip(new Tooltip("Minutes You Want To Practice ZAI"));
        ZenTime.setTooltip(new Tooltip("Minutes You Want To Practice ZEN"));
        PostTime.setTooltip(new Tooltip("Minutes To Meditate After Practicing Selected Cut(s). There Is A 2 Minute Ramp Out Of The Last Cut You Are Working On"));
        TotalSessionTime.setTooltip(new Tooltip("Total Session Time (Not Including Presession + Postsession Ramp, And Alert File)"));
        ApproximateEndTime.setTooltip(new Tooltip("Approximate Finish Time For This Session (Assuming You Start Now)"));
        AmbienceSwitch.setTooltip(new Tooltip("Check This After You Set All Values To Check For And Enable Ambience For This Session"));
        ChangeAllValuesButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
        LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
        SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
        ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
    }
    public void updatecreatorui() {
        if (gettextfieldtimes()) {
            Integer totalsessiontime = 0;
            for (Integer i : textfieldtimes) {totalsessiontime += i;}
            int rampduration = Root.getOptions().getSessionOptions().getRampduration();
            totalsessiontime += rampduration * 2;
            if (rampduration > 0) {TotalSessionTime.setTooltip(new Tooltip("Duration Includes A Ramp Of " + rampduration + "Mins. On Both Presession And Postsession"));}
            else {TotalSessionTime.setTooltip(null);}
            TotalSessionTime.setText(Tools.minutestoformattedhoursandmins(totalsessiontime));
            ApproximateEndTime.setTooltip(new Tooltip("Time You Finish Will Vary Depending On When You Start Playback"));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, totalsessiontime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
        } else {
            TotalSessionTime.setText("-");
            ApproximateEndTime.setText("-");
        }
        if (AmbienceSwitch.isSelected()) {
            AmbienceSwitch.setSelected(false);
            session.resetallcuts();
            Tools.showtimedmessage(StatusBar, "Session Values Changed, Ambience Unselected", 5000);
        }
    }
    public boolean gettextfieldtimes() {
        Boolean not_all_zeros = false;
        for (Integer i : textfieldtimes) {if (i > 0) {not_all_zeros = true;}}
        return  not_all_zeros;
    }
    public void checkambience() {
        if (AmbienceSwitch.isSelected()) {
            if (gettextfieldtimes()) {
                session.checkambience(textfieldtimes, AmbienceSwitch);
            } else {
                Tools.showinformationdialog(Root, "Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            session.resetallcuts();
            session.setAmbienceenabled(false);
        }
    }
    public void changeallvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(Root);
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getminutes();
            String minutes = min.toString();
            RinTime.setText(minutes);
            KyoTime.setText(minutes);
            TohTime.setText(minutes);
            ShaTime.setText(minutes);
            KaiTime.setText(minutes);
            JinTime.setText(minutes);
            RetsuTime.setText(minutes);
            ZaiTime.setText(minutes);
            ZenTime.setText(minutes);
            if (changevaluesdialog.getincludepresession()) {PreTime.setText(minutes);}
            if (changevaluesdialog.getincludepostsession()) {PostTime.setText(minutes);}
        }
    }
    public void changevaluestopreset(ArrayList<Integer> presetvalues) {
        try {
            PreTime.setText(presetvalues.get(0).toString());
            RinTime.setText(presetvalues.get(1).toString());
            KyoTime.setText(presetvalues.get(2).toString());
            TohTime.setText(presetvalues.get(3).toString());
            ShaTime.setText(presetvalues.get(4).toString());
            KaiTime.setText(presetvalues.get(5).toString());
            JinTime.setText(presetvalues.get(6).toString());
            RetsuTime.setText(presetvalues.get(7).toString());
            ZaiTime.setText(presetvalues.get(8).toString());
            ZenTime.setText(presetvalues.get(9).toString());
            PostTime.setText(presetvalues.get(10).toString());
        } catch (ArrayIndexOutOfBoundsException ignored) {
            Tools.showerrordialog(Root, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }
    public ArrayList<Integer> getcreatorvalues() {
        return new ArrayList<>(Arrays.asList(
            Integer.parseInt(PreTime.getText()), Integer.parseInt(RinTime.getText()), Integer.parseInt(KyoTime.getText()),
            Integer.parseInt(TohTime.getText()), Integer.parseInt(ShaTime.getText()), Integer.parseInt(KaiTime.getText()),
            Integer.parseInt(JinTime.getText()), Integer.parseInt(RetsuTime.getText()), Integer.parseInt(ZaiTime.getText()),
            Integer.parseInt(ZenTime.getText()), Integer.parseInt(PostTime.getText())
        ));
    }

// Widget Implementation
    @Override
    public void disable() {
        updateuitimeline.stop();
        ChangeAllValuesButton.setDisable(true);
        AmbienceSwitch.setDisable(true);
        ApproximateEndTime.setDisable(true);
        TotalSessionTime.setDisable(true);
        PreLabel.setDisable(true);
        PreTime.setDisable(true);
        RinLabel.setDisable(true);
        RinTime.setDisable(true);
        KyoLabel.setDisable(true);
        KyoTime.setDisable(true);
        TohLabel.setDisable(true);
        TohTime.setDisable(true);
        ShaLabel.setDisable(true);
        ShaTime.setDisable(true);
        KaiLabel.setDisable(true);
        KaiTime.setDisable(true);
        JinLabel.setDisable(true);
        JinTime.setDisable(true);
        RetsuLabel.setDisable(true);
        RetsuTime.setDisable(true);
        ZaiLabel.setDisable(true);
        ZaiTime.setDisable(true);
        ZenLabel.setDisable(true);
        ZenTime.setDisable(true);
        PostLabel.setDisable(true);
        PostTime.setDisable(true);
        LengthLabel.setDisable(true);
        CompletionLabel.setDisable(true);
        LoadPresetButton.setDisable(true);
        SavePresetButton.setDisable(true);
        StatusBar.setText("Creator Disabled While Session Player Enabled");
    }
    @Override
    public void enable() {
        updateuitimeline.play();
        ChangeAllValuesButton.setDisable(false);
        AmbienceSwitch.setDisable(false);
        ApproximateEndTime.setDisable(false);
        TotalSessionTime.setDisable(false);
        PreLabel.setDisable(false);
        PreTime.setDisable(false);
        RinLabel.setDisable(false);
        RinTime.setDisable(false);
        KyoLabel.setDisable(false);
        KyoTime.setDisable(false);
        TohLabel.setDisable(false);
        TohTime.setDisable(false);
        ShaLabel.setDisable(false);
        ShaTime.setDisable(false);
        KaiLabel.setDisable(false);
        KaiTime.setDisable(false);
        JinLabel.setDisable(false);
        JinTime.setDisable(false);
        RetsuLabel.setDisable(false);
        RetsuTime.setDisable(false);
        ZaiLabel.setDisable(false);
        ZaiTime.setDisable(false);
        ZenLabel.setDisable(false);
        ZenTime.setDisable(false);
        PostLabel.setDisable(false);
        PostTime.setDisable(false);
        LengthLabel.setDisable(false);
        CompletionLabel.setDisable(false);
        LoadPresetButton.setDisable(false);
        SavePresetButton.setDisable(false);
        StatusBar.setText("");
    }
    @Override
    public void resetallvalues() {
        AmbienceSwitch.setText("No Session Created");
        TotalSessionTime.setText("No Session Created");
        PreTime.setText("-");
        RinTime.setText("-");
        KyoTime.setText("-");
        TohTime.setText("-");
        ShaTime.setText("-");
        KaiTime.setText("-");
        JinTime.setText("-");
        RetsuTime.setText("-");
        ZaiTime.setText("-");
        ZenTime.setText("-");
        PostTime.setText("-");
    }
    @Override
    public boolean cleanup() {
        boolean currentlyexporting = getExporterState() == ExporterState.WORKING;
        if (currentlyexporting) {
            Tools.showinformationdialog(Root, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
        } else {This_Session.deleteprevioussession();}
        return ! currentlyexporting;
    }

// Presets
    public void loadpreset() {
        if (Preset.openpreset() && Preset.validpreset()) {
            changevaluestopreset(Preset.getpresettimes());
        } else {Tools.showinformationdialog(Root, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void savepreset() {
        Preset.setpresettimes(getcreatorvalues());
        if (! Preset.validpreset()) {Tools.showinformationdialog(Root, "Information", "Cannot Save Preset", "All Values Are 0"); return;}
        if (Preset.savepreset()) {Tools.showtimedmessage(StatusBar, "Preset Successfully Saved", 4000);}
        else {Tools.showerrordialog(Root, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
    }

// Subclasses/Dialogs
    public static class ChangeAllValuesDialog extends Stage implements Initializable {
        public Button AcceptButton;
        public Button CancelButton;
        public TextField changeAllValuesMinutesTextField;
        public CheckBox PresessionCheckbox;
        public CheckBox PostsessionCheckBox;
        private Boolean accepted;
        private MainController Root;

        public ChangeAllValuesDialog(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Change All Values To: ");
            setAccepted(false);
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            Tools.integerTextField(changeAllValuesMinutesTextField, true);
        }
    // Getters And Setters
        public Boolean getAccepted() {
        return accepted;
    }
        public void setAccepted(Boolean accepted) {
            this.accepted = accepted;
        }

    // Button Actions
        public void acceptbuttonpressed(Event event) {setAccepted(true); this.close();}
        public void cancelbuttonpressed(Event event) {this.close();}
        public boolean getincludepresession() {return PresessionCheckbox.isSelected();}
        public boolean getincludepostsession() {return PostsessionCheckBox.isSelected();}
        public Integer getminutes() {
            try {return Integer.parseInt(changeAllValuesMinutesTextField.getText());}
            catch (NumberFormatException e) {return 0;}
        }

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Exporting Session");
        }

        public void unbindproperties() {
            TotalProgress.progressProperty().unbind();
            CurrentProgress.progressProperty().unbind();
            StatusBar.textProperty().unbind();
            CurrentLabel.textProperty().unbind();
        }
    }
    public static class SessionNotWellformedDialog extends Stage {
        public Button returntoCreatorButton;
        public Button addmissingCutsButton;
        public ListView<Text> sessionlistview;
        public Label sessionmissingcutsLabel;
        public Button CreateAnywayButton;
        public Label explanationLabel;
        private ArrayList<Integer> textfieldvalues;
        private int lastcutindex;
        private int invocationduration;
        private boolean createsession;
        private MainController Root;

        public SessionNotWellformedDialog(MainController root, ArrayList<Integer> textfieldvalues, String cutsmissingtext, int lastcutindex) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionNotWellformedDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Session Not Well Formed");
            this.textfieldvalues = textfieldvalues;
            this.lastcutindex = lastcutindex;
            sessionmissingcutsLabel.setText(cutsmissingtext);
            populatelistview();
            explanationLabel.setText(("Your Practiced Cuts Do Not Connect! Due To The Nature Of The Kuji-In I Recommend " +
                    "Connecting All Cuts From RIN All The Way To Your Last Cut (") + Options.ALLNAMES.get(lastcutindex) +
                    ") Or Your This_Session Might Not Have The Energy It Needs");
            setCreatesession(false);
        }

        public void populatelistview() {
            ArrayList<String> items = new ArrayList<>();
            ObservableList<Text> sessionitems = FXCollections.observableArrayList();
            int count = 0;
            boolean thisitemmissing;
            for (int i = 0; i < textfieldvalues.size(); i++) {
                String name = Options.ALLNAMES.get(i);
                String minutes;
                if (i <= lastcutindex || i == textfieldvalues.size() - 1) {
                    thisitemmissing = false;
                    if (i == 0 || i == 10) {
                        if (textfieldvalues.get(i) == 0) {
                            minutes = "Ramp Only";
                        } else {
                            String time = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));
                            minutes = String.format("%s + Ramp", time);
                        }
                    } else {
                        if (textfieldvalues.get(i) == 0) {
                            thisitemmissing = true;
                            minutes = " Missing Value! ";
                        }
                        else {minutes = Tools.minutestoformattedhoursandmins(textfieldvalues.get(i));}
                    }
                    String txt = String.format("%d: %s (%s )", count + 1, name, minutes);
                    Text item = new Text();
                    item.setText(txt);
                    if (thisitemmissing) item.setStyle("-fx-font-weight:bold; -fx-font-style: italic;");
                    sessionitems.add(item);
                    count++;
                }
            }
            sessionlistview.setItems(sessionitems);
        }

        public void returntoCreator(Event event) {this.close();}

        public void addmissingcutstoSession(Event event) {
            CutInvocationDialog cutdurationdialog = new CutInvocationDialog(Root);
            cutdurationdialog.showAndWait();
            setInvocationduration(cutdurationdialog.getCutinvocationduration());
            setCreatesession(true);
            this.close();
        }

        public void createSessionwithoutmissingcuts(Event event) {
            if (Tools.getanswerdialog(Root, "Confirmation", "Session Not Well-Formed", "Really Create Anyway?")) {
                setCreatesession(true);
                this.close();
            }
        }

        public int getInvocationduration() {
            return invocationduration;
        }

        public void setInvocationduration(int invocationduration) {
            this.invocationduration = invocationduration;
        }

        public boolean isCreatesession() {
            return createsession;
        }

        public void setCreatesession(boolean createsession) {this.createsession = createsession;}
    }
    public static class CutInvocationDialog extends Stage implements Initializable{
        public Button CancelButton;
        public Button OKButton;
        public TextField cutinvocationminutesTextField;
        private int cutinvocationduration;
        private MainController Root;

        public CutInvocationDialog(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Cut Invocation");
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            Tools.integerTextField(cutinvocationminutesTextField, true);
        }

        public int getCutinvocationduration() {
            return cutinvocationduration;
        }

        public void setCutinvocationduration(int cutinvocationduration) {
            this.cutinvocationduration = cutinvocationduration;
        }

        public void CancelButtonPressed(Event event) {
            setCutinvocationduration(0);
            this.close();
        }

        public void OKButtonPressed(Event event) {
            try {
                int value = Integer.parseInt(cutinvocationminutesTextField.getText());
                if (value != 0) {
                    setCutinvocationduration(value);
                    this.close();
                } else {
                    if (Tools.getanswerdialog(Root, "Confirmation", "Cut Invocation Value Is 0", "Continue With Zero Value (These Cuts Won't Be Included)" )) {
                        setCutinvocationduration(0);
                        this.close();
                    }
                }
            } catch (NumberFormatException e) {Tools.showerrordialog(Root, "Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
        }
    }

// Enums
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }
}
