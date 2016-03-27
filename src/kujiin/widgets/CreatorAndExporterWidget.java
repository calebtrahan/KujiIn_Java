package kujiin.widgets;

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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.*;
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
public class CreatorAndExporterWidget {
    private Button ChangeAllValuesButton;
    private Button ExportButton;
    private Button LoadPresetButton;
    private Button SavePresetButton;
    private CheckBox AmbienceSwitch;
    private TextField TotalSessionTime;
    private TextField ApproximateEndTime;
    private ToggleButton PreSwitch;
    private TextField PreTime;
    private ToggleButton RinSwitch;
    private TextField RinTime;
    private ToggleButton KyoSwitch;
    private TextField KyoTime;
    private ToggleButton TohSwitch;
    private TextField TohTime;
    private ToggleButton ShaSwitch;
    private TextField ShaTime;
    private ToggleButton KaiSwitch;
    private TextField KaiTime;
    private ToggleButton JinSwitch;
    private TextField JinTime;
    private ToggleButton RetsuSwitch;
    private TextField RetsuTime;
    private ToggleButton ZaiSwitch;
    private TextField ZaiTime;
    private ToggleButton ZenSwitch;
    private TextField ZenTime;
    private ToggleButton PostSwitch;
    private TextField PostTime;
    private ToggleButton EarthSwitch;
    private TextField EarthTime;
    private ToggleButton AirSwitch;
    private TextField AirTime;
    private ToggleButton FireSwitch;
    private TextField FireTime;
    private ToggleButton WaterSwitch;
    private TextField WaterTime;
    private ToggleButton VoidSwitch;
    private TextField VoidTime;
    private Label StatusBar;
    private ExporterState exporterState;
    private CreatorState creatorState;
    private This_Session session;
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
        ChangeAllValuesButton = root.ChangeAllCutsButton;
        ExportButton = root.ExportButton;
        AmbienceSwitch = root.AmbienceSwitch;
        TotalSessionTime = root.TotalSessionTime;
        ApproximateEndTime = root.ApproximateEndTime;
        Preset = new Preset(root);
        PreSwitch = root.PreSwitch;
        PreTime = root.PreTime;
        PreSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(PreSwitch, PreTime));
        RinSwitch = root.RinSwitch;
        RinTime = root.RinTime;
        RinSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(RinSwitch, RinTime));
        KyoSwitch = root.KyoSwitch;
        KyoTime = root.KyoTime;
        KyoSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(KyoSwitch, KyoTime));
        TohSwitch = root.TohSwitch;
        TohTime = root.TohTime;
        TohSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(TohSwitch, TohTime));
        ShaSwitch = root.ShaSwitch;
        ShaTime = root.ShaTime;
        ShaSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(ShaSwitch, ShaTime));
        KaiSwitch = root.KaiSwitch;
        KaiTime = root.KaiTime;
        KaiSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(KaiSwitch, KaiTime));
        JinSwitch = root.JinSwitch;
        JinTime = root.JinTime;
        JinSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(JinSwitch, JinTime));
        RetsuSwitch = root.RetsuSwitch;
        RetsuTime = root.RetsuTime;
        RetsuSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(RetsuSwitch, RetsuTime));
        ZaiSwitch = root.ZaiSwitch;
        ZaiTime = root.ZaiTime;
        ZaiSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(ZaiSwitch, ZaiTime));
        ZenSwitch = root.ZenSwitch;
        ZenTime = root.ZenTime;
        ZenSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(ZenSwitch, ZenTime));
        PostSwitch = root.PostSwitch;
        PostTime = root.PostTime;
        PostSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(PostSwitch, PostTime));
        EarthSwitch = root.EarthSwitch;
        EarthTime = root.EarthTime;
        EarthSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(EarthSwitch, EarthTime));
        AirSwitch = root.AirSwitch;
        AirTime = root.AirTime;
        AirSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(AirSwitch, AirTime));
        FireSwitch = root.FireSwitch;
        FireTime = root.FireTime;
        FireSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(FireSwitch, FireTime));
        WaterSwitch = root.WaterSwitch;
        WaterTime = root.WaterTime;
        WaterSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(WaterSwitch, WaterTime));
        VoidSwitch = root.VoidSwitch;
        VoidTime = root.VoidTime;
        VoidSwitch.setOnAction(event -> Tools.valueboxandlabelpairswitch(VoidSwitch, VoidTime));
        session = root.getSession();
        StatusBar = root.CreatorStatusBar;
        exporterState = ExporterState.NOT_EXPORTED;
        creatorState = CreatorState.NOT_CREATED;
        setuptextfields();
        TotalSessionTime.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        ApproximateEndTime.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        exportservices = new ArrayList<>();
        updateuitimeline = new Timeline(new KeyFrame(Duration.millis(10000), ae -> updatecreatorui()));
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
                session.create(session.getallsessionvalues());
//                disable();
                ExportButton.setDisable(false);
                session.Root.PlayButton.setDisable(false);
                setCreatorState(CreatorState.CREATED);
            }
        } else {setCreatorState(CreatorState.NOT_CREATED);}
    }
    public boolean creationchecks() {
        if (! gettextfieldtimes()) {Tools.showerrordialog(Root, "Error Creating Session", "At Least One Cut's Value Must Not Be 0", "Cannot Create Session"); return false;}
        if (! session.checksessionwellformedness(session.getcutsessionvalues(true))) {return false;}
        // TODO Refactor Goals Here
        ArrayList<Integer> notgoodongoals = session.Root.getProgressTracker().precreationgoalchecks(session.getcutsessionvalues(true));
        if (! notgoodongoals.isEmpty()) {
            StringBuilder notgoodtext = new StringBuilder();
            for (int i = 0; i < notgoodongoals.size(); i++) {
                if (i == 0 && notgoodongoals.size() > 1) {notgoodtext.append("\n");}
                notgoodtext.append(ProgressAndGoalsWidget.GOALCUTNAMES[notgoodongoals.get(i)]);
                if (notgoodongoals.size() > 1) {
                    if (i != notgoodtext.length() - 1) {notgoodtext.append(", ");}
                    if (i == notgoodongoals.size() / 2) {notgoodtext.append("\n");}
                }
            }
            if (Tools.getanswerdialog(Root, "Confirmation", "Goals Aren't Long Enough For " + notgoodtext.toString(), "Set Goals For These Cuts Before Creating This Session?")) {
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
            } else {return true;}
        } else {return true;}
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
//        if (creationchecks()) {
//            if (getExporterState() == ExporterState.NOT_EXPORTED) {
//                if (checkforffmpeg()) {
//                    if (session.exportfile() == null) {
//                        session.getnewexportsavefile();
//                    } else {
//                        // TODO Continue Fixing Logic Here
//                        if (session.getExportfile().exists()) {
//                            if (!Tools.getanswerdialog(Root, "Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
//                                session.getnewexportsavefile();
//                            }
//                        } else {session.getnewexportsavefile();}
//                    }
//                    if (session.getExportfile() == null) {Tools.showtimedmessage(StatusBar, "Export Session Cancelled", 3000); return;}
//                    exportserviceindex = 0;
//                    ArrayList<Cut> cutsinsession = session.getCutsinsession();
//                    for (Cut i : cutsinsession) {
//                        exportservices.add(i.getexportservice());
//                    }
//                    exportservices.add(session.getsessionexporter());
//                    exportingSessionDialog = new ExportingSessionDialog(Root);
//                    exportingSessionDialog.show();
//                    setExporterState(ExporterState.WORKING);
//                    exportnextservice();
//                } else {
//                    Tools.showerrordialog(Root, "Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
//                    // TODO Open A Browser Showing How To Install FFMPEG
//                }
//            } else if (getExporterState() == ExporterState.WORKING) {
//                Tools.showtimedmessage(StatusBar, "Session Currently Being Exported", 3000);
//            } else {
//                if (Tools.getanswerdialog(Root, "Confirmation", "Session Already Exported", "Export Again?")) {
//                    setExporterState(ExporterState.NOT_EXPORTED);
//                    startexport();
//                }
//            }
//        } else {Tools.showinformationdialog(Root, "Information", "Cannot Export", "No Cuts Selected");}
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
        PreTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(0, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {PreTime.setText("0"); Root.getSession().setDuration(0, 0); updatecreatorui();}
        });
        RinTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(1, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {RinTime.setText("0"); updatecreatorui();}
        });
        KyoTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(2, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {KyoTime.setText("0"); updatecreatorui();}
        });
        TohTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(3, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {TohTime.setText("0"); updatecreatorui();}
        });
        ShaTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(4, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {ShaTime.setText("0"); updatecreatorui();}
        });
        KaiTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(5, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {KaiTime.setText("0"); updatecreatorui();}
        });
        JinTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(6, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {JinTime.setText("0"); updatecreatorui();}
        });
        RetsuTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(7, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {RetsuTime.setText("0"); updatecreatorui();}
        });
        ZaiTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(8, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {ZaiTime.setText("0"); updatecreatorui();}
        });
        ZenTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(9, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {ZenTime.setText("0"); updatecreatorui();}
        });
        PostTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(10, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {PostTime.setText("0"); updatecreatorui();}
        });
        EarthTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(11, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {EarthTime.setText("0"); updatecreatorui();}
        });
        AirTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(12, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {AirTime.setText("0"); updatecreatorui();}
        });
        FireTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(13, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {FireTime.setText("0"); updatecreatorui();}
        });
        WaterTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(14, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {WaterTime.setText("0"); updatecreatorui();}
        });
        VoidTime.textProperty().addListener((observable, oldValue, newValue) -> {
            try {Root.getSession().setDuration(15, Integer.valueOf(newValue)); updatecreatorui();}
            catch (NumberFormatException ignored) {VoidTime.setText("0"); updatecreatorui();}
        });
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
            for (Integer i : session.getallsessionvalues()) {totalsessiontime += i;}
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
            session.resetcreateditems();
            Tools.showtimedmessage(StatusBar, "Session Values Changed, Ambience Unselected", 5000);
        }
    }
    public boolean gettextfieldtimes() {
        Boolean not_all_zeros = false;
        for (Integer i : session.getallsessionvalues()) {if (i > 0) {not_all_zeros = true;}}
        return  not_all_zeros;
    }
    public void checkambience() {
        if (AmbienceSwitch.isSelected()) {
            if (gettextfieldtimes()) {
                session.checkambience(AmbienceSwitch);
            } else {
                Tools.showinformationdialog(Root, "Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            session.resetcreateditems();
            session.setAmbienceenabled(false);
        }
    }
    public void changeallcutvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(Root, "Change All Cut Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getminutes();
            for (Cut i : session.getallCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {session.getPostsession().changevalue(min);}
        }
    }
    public void changeallelementvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(Root, "Change All Element Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getminutes();
            for (Element i : session.getallElements()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {session.getPostsession().changevalue(min);}
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

        public ChangeAllValuesDialog(MainController root, String toptext) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(toptext);
            setAccepted(false);
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            Tools.integerTextField(changeAllValuesMinutesTextField, true, true);
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
                Root.getOptions().setStyle(this);
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
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Session Not Well Formed");
            this.textfieldvalues = textfieldvalues;
            this.lastcutindex = lastcutindex;
            sessionmissingcutsLabel.setText(cutsmissingtext);
            populatelistview();
            explanationLabel.setText(("Your Practiced Cuts Do Not Connect! Due To The Nature Of The Kuji-In I Recommend " +
                    "Connecting All Cuts From RIN All The Way To Your Last Cut (") + Options.CUTNAMES.get(lastcutindex) +
                    ") Or Your This_Session Might Not Have The Energy It Needs");
            setCreatesession(false);
        }

        public void populatelistview() {
            ArrayList<String> items = new ArrayList<>();
            ObservableList<Text> sessionitems = FXCollections.observableArrayList();
            int count = 0;
            boolean thisitemmissing;
            for (int i = 0; i < textfieldvalues.size() - 1; i++) {
                // TODO IMPORTANT Index Out Of Bounds Here
                String name = Options.CUTNAMES.get(i);
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
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Cut Invocation");
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            Tools.integerTextField(cutinvocationminutesTextField, true, true);
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
    public static class SortSessionItems extends Stage {
        public TableView<SessionItem> SessionItemsTable;
        public TableColumn<SessionItem, Integer> NumberColumn;
        public TableColumn<SessionItem, String> NameColumn;
        public TableColumn<SessionItem, String> DurationColumn;
        public Button UpButton;
        public Button DownButton;
        public Button AcceptButton;
        public Button CancelButton;
        private List<Object> sessionitems;
        private ObservableList<SessionItem> tableitems;
        private MainController Root;

        public SortSessionItems(MainController Root, List<Object> sessionitems) {
            this.sessionitems = sessionitems;
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SortSessionParts.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
            SessionItemsTable.setOnMouseClicked(event -> itemselected());
            tableitems = FXCollections.observableArrayList();
            populatetable();
        }

        public void itemselected() {
            boolean validitemselected = SessionItemsTable.getSelectionModel().getSelectedIndex() != -1;
            UpButton.setDisable(! validitemselected);
            DownButton.setDisable(! validitemselected);
        }
        public void populatetable() {
            SessionItemsTable.getItems().clear();
            tableitems.clear();
            int count = 1;
            for (Object i : sessionitems) {
                Playable item = (Playable) i;
                tableitems.add(new SessionItem(count, item.name, Tools.minstoformattedabbreviatedhoursandminutes(item.getdurationinminutes())));
                count++;
            }
            SessionItemsTable.setItems(tableitems);
        }
        public void moveitemup(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Tools.showinformationdialog(Root, "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == 0) {return;}
            Playable selecteditem = (Playable) sessionitems.get(selectedindex);
            Playable oneitemup = (Playable) sessionitems.get(selectedindex - 1);
            if (selecteditem instanceof Cut && oneitemup instanceof Cut) {
                if (selecteditem.number > oneitemup.number) {
                    Tools.showinformationdialog(Root, "Cannot Move", selecteditem.name + " Cannot Be Moved Before " + oneitemup.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemup instanceof Qi_Gong) {
                Tools.showinformationdialog(Root, "Cannot Move", "Cannot Replace Presession", "Cannot Move");
                return;
            }
            Collections.swap(sessionitems, selectedindex, selectedindex - 1);
            populatetable();
        }
        public void moveitemdown(ActionEvent actionEvent) {
            int selectedindex = SessionItemsTable.getSelectionModel().getSelectedIndex();
            if (selectedindex == -1) {return;}
            if (tableitems.get(selectedindex).name.get().equals("Presession") || tableitems.get(selectedindex).name.get().equals("Postsession")) {
                Tools.showinformationdialog(Root, "Information", "Cannot Move", "Presession And Postsession Cannot Be Moved");
                return;
            }
            if (selectedindex == tableitems.size() - 1) {return;}
            Playable selecteditem = (Playable) sessionitems.get(selectedindex);
            Playable oneitemdown = (Playable) sessionitems.get(selectedindex + 1);
            if (selecteditem instanceof Cut && oneitemdown instanceof Cut) {
                if (selecteditem.number < oneitemdown.number) {
                    Tools.showinformationdialog(Root, "Cannot Move", selecteditem.name + " Cannot Be Moved After " + oneitemdown.name + ". Cuts Would Be Out Of Order", "Cannot Move");
                    return;
                }
            }
            if (oneitemdown instanceof Qi_Gong) {
                Tools.showinformationdialog(Root, "Cannot Move", "Cannot Replace Postsession", "Cannot Move");
                return;
            }
            Collections.swap(sessionitems, selectedindex, selectedindex + 1);
            populatetable();
        }
        public void cutcheck() {

        }
        public List<Object> getorderedsessionitems() {
            return sessionitems;
        }
        public void accept(ActionEvent actionEvent) {
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            sessionitems = null;
            close();
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

// Enums
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }
}
