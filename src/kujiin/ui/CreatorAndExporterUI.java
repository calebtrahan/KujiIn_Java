package kujiin.ui;

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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.util.*;
import kujiin.xml.Preset;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

// TODO Get FFMPEG Working To Mix Audio Files Together
    // Not Supported Stream?
public class CreatorAndExporterUI {
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

    public CreatorAndExporterUI(MainController root) {
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
        RinSwitch = root.RinSwitch;
        RinTime = root.RinTime;
        KyoSwitch = root.KyoSwitch;
        KyoTime = root.KyoTime;
        TohSwitch = root.TohSwitch;
        TohTime = root.TohTime;
        ShaSwitch = root.ShaSwitch;
        ShaTime = root.ShaTime;
        KaiSwitch = root.KaiSwitch;
        KaiTime = root.KaiTime;
        JinSwitch = root.JinSwitch;
        JinTime = root.JinTime;
        RetsuSwitch = root.RetsuSwitch;
        RetsuTime = root.RetsuTime;
        ZaiSwitch = root.ZaiSwitch;
        ZaiTime = root.ZaiTime;
        ZenSwitch = root.ZenSwitch;
        ZenTime = root.ZenTime;
        PostSwitch = root.PostSwitch;
        PostTime = root.PostTime;
        EarthSwitch = root.EarthSwitch;
        EarthTime = root.EarthTime;
        AirSwitch = root.AirSwitch;
        AirTime = root.AirTime;
        FireSwitch = root.FireSwitch;
        FireTime = root.FireTime;
        WaterSwitch = root.WaterSwitch;
        WaterTime = root.WaterTime;
        VoidSwitch = root.VoidSwitch;
        VoidTime = root.VoidTime;
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
        if (session.getPlayerState() == PlayerUI.PlayerState.PLAYING ||
            session.getPlayerState() == PlayerUI.PlayerState.PAUSED ||
            session.getPlayerState() == PlayerUI.PlayerState.TRANSITIONING) {
            if (Util.gui_getokcancelconfirmationdialog(Root, "Stop Session", "In Order To Edit Session Values The Session Player Must Be Stopped And Reset", "Stop And Reset Session Player?")) {
                if (! session.stop().equals("Session Stopped")) {return;}
            } else {return;}
        }
        // TODO Check Exporter Here
        if (creatorState == CreatorState.NOT_CREATED) {
            if (! gettextfieldtimes()) {
                Util.gui_showerrordialog(Root, "Error Creating Session", "At Least One Cut Or Element's Value Must Not Be 0", "Cannot Create Session"); return;}
            boolean creationstate = session.create();
            ExportButton.setDisable(! creationstate);
            session.Root.PlayButton.setDisable(! creationstate);
            if (creationstate) setCreatorState(CreatorState.CREATED);
            else {setCreatorState(CreatorState.NOT_CREATED);}
        } else {setCreatorState(CreatorState.NOT_CREATED);}
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
//                    exportnextservice();
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
            TotalSessionTime.setText(Util.format_minstohrsandmins_short(totalsessiontime));
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
            Util.gui_showtimedmessageonlabel(StatusBar, "Session Values Changed, Ambience Unselected", 5000);
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
                Util.gui_showinformationdialog(Root, "Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            session.resetcreateditems();
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
            Util.gui_showerrordialog(Root, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
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
            Util.gui_showinformationdialog(Root, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
        } else {This_Session.deleteprevioussession();}
        return ! currentlyexporting;
    }

// Presets
    public void loadpreset() {
        if (Preset.openpreset() && Preset.validpreset()) {
            changevaluestopreset(Preset.getpresettimes());
        } else {
            Util.gui_showinformationdialog(Root, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void savepreset() {
        Preset.setpresettimes(getcreatorvalues());
        if (! Preset.validpreset()) {
            Util.gui_showinformationdialog(Root, "Information", "Cannot Save Preset", "All Values Are 0"); return;}
        if (Preset.savepreset()) {
            Util.gui_showtimedmessageonlabel(StatusBar, "Preset Successfully Saved", 4000);}
        else {
            Util.gui_showerrordialog(Root, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
    }

// Subclasses/Dialogs
    public static class ChangeAllValuesDialog extends Stage {
        public Button AcceptButton;
        public Button CancelButton;
        public TextField MinutesTextField;
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
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(toptext);
            setAccepted(false);
            MinutesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {if (newValue.matches("\\d*")) {
                    MinutesTextField.setText(Integer.toString(Integer.parseInt(newValue)));}  else {
                    MinutesTextField.setText(oldValue);}}
                catch (Exception e) {MinutesTextField.setText("");}
            });
            MinutesTextField.setText("0");
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
            try {return Integer.parseInt(MinutesTextField.getText());}
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
                this.setResizable(false);
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
    public static class CutsMissingDialog  extends Stage {
        public Button AddMissingCutsButton;
        public ListView<Text> SessionListView;
        public Button CreateAnywayButton;
        public Button CancelCreationButton;
        private ArrayList<Cut> allcuts;
        private ArrayList<Cut> missingcuts;
        private Util.AnswerType result;
        private MainController Root;

        public CutsMissingDialog(MainController root, ArrayList<Cut> allcuts) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutsOutOfOrderOrMissing.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> dialogclosed());
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Cuts Missing");
            this.allcuts = allcuts;
            populatelistview();
            Util.gui_showinformationdialog(Root, "Cuts Missing", "Due To The Nature Of Kuji-In, Each Cut Should Connect From RIN Up, Or The Later Cuts Might Lack The Energy They Need", "Use This Dialog To Connect Cuts, Or Cancel Without Creating");
        }

        public int getlastworkingcutindex() {
            int lastcutindex = 0;
            for (Cut i : allcuts) {
                if (i.getdurationinminutes() > 0) {lastcutindex = i.number;}
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
                if (selectedcut.getdurationinminutes() > 0) {
                    currentcuttext.append(" (").append(Util.format_minstohrsandmins_short(selectedcut.getdurationinminutes())).append(")");
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
                        Root.getSession().setDuration(i.number, cutdurationdialog.getDuration());
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SortSessionParts.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
                this.setOnCloseRequest(event -> dialogClosed());
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
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
            for (Object i : sessionitems) {
                Meditatable item = (Meditatable) i;
                tableitems.add(new SessionItem(count, item.name, Util.format_minstohrsandmins_abbreviated(item.getdurationinminutes())));
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

// Enums
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }
}
