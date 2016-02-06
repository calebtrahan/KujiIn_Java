package kujiin.widgets;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kujiin.Cut;
import kujiin.MainController;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.Options;
import kujiin.xml.Preset;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ResourceBundle;

// TODO Get FFMPEG Working To Mix Audio Files Together
    // Not Supported Stream?
public class CreatorAndExporterWidget implements Widget {
    private Button changeallvaluesbutton;
    private Button exportButton;
    private Button ExportButton;
    private Button loadpresetbutton;
    private Button savepresetbutton;
    private CheckBox AmbienceSwitch;
    private TextField TotalSessionTime;
    private TextField ApproximateEndTime;
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
    private This_Session session;
    private Label StatusBar;
    private ArrayList<Integer> textfieldtimes = new ArrayList<>(11);
    private ArrayList<Service<Boolean>> exportservices;
    private Service<Boolean> currentexporterservice;
    private Integer exportserviceindex;
    private ExportingSessionDialog exportingSessionDialog;
    private Preset Preset;

    public CreatorAndExporterWidget(MainController mainController) {
        exportButton = mainController.ExportButton;
        loadpresetbutton = mainController.LoadPresetButton;
        savepresetbutton = mainController.SavePresetButton;
        changeallvaluesbutton = mainController.ChangeValuesButton;
        ExportButton = mainController.ExportButton;
        AmbienceSwitch = mainController.AmbienceSwitch;
        TotalSessionTime = mainController.TotalSessionTime;
        ApproximateEndTime = mainController.ApproximateEndTime;
        Preset = new Preset();
        PreTime = mainController.PreTime;
        RinTime = mainController.RinTime;
        KyoTime = mainController.KyoTime;
        TohTime = mainController.TohTime;
        ShaTime = mainController.ShaTime;
        KaiTime = mainController.KaiTime;
        JinTime = mainController.JinTime;
        RetsuTime = mainController.RetsuTime;
        ZaiTime = mainController.ZaiTime;
        ZenTime = mainController.ZenTime;
        PostTime = mainController.PostTime;
        session = mainController.getSession();
        StatusBar = mainController.CreatorStatusBar;
        exporterState = ExporterState.IDLE;
        setuptextfields();
        textfieldtimes.addAll(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        TotalSessionTime.setOnKeyTyped(MainController.noneditabletextfield);
        ApproximateEndTime.setOnKeyTyped(MainController.noneditabletextfield);
        exportservices = new ArrayList<>();
        updatecreatorui();
    }

// Getters And Setters
    public ExporterState getExporterState() {
        return exporterState;
    }
    public void setExporterState(ExporterState exporterState) {
        this.exporterState = exporterState;
    }

// Creation
    public boolean createsession() {
        if (gettextfieldtimes()) {
            if (session.sessioncreationwellformednesschecks(textfieldtimes)) {
                session.setAmbienceenabled(AmbienceSwitch.isSelected());
                session.create(textfieldtimes);
                return true;
            } else {return false;}
        }
        else {Tools.showerrordialog("Error", "At Least One Cut's Value (Pre + Post Excluded) Must Be Greater Than 0", "Session Not Valid"); return false;}
    }

// Export
    public void startexport() {
        if (createsession()) {
            if (getExporterState() == ExporterState.IDLE) {
                if (checkforffmpeg()) {
                    if (session.getExportfile() == null) {
                        session.getnewexportsavefile();
                    } else {
                        // TODO Continue Fixing Logic Here
                        if (session.getExportfile().exists()) {
                            if (!Tools.getanswerdialog("Confirmation", "Overwrite Saved Exported Session?", "Saved Session: " + session.getExportfile().getAbsolutePath())) {
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
                    exportingSessionDialog = new ExportingSessionDialog(session);
                    exportingSessionDialog.show();
                    setExporterState(ExporterState.EXPORT_IN_PROGRESS);
                    exportnextservice();
                } else {
                    Tools.showerrordialog("Error", "Cannot Export. Missing FFMpeg", "Please Install FFMpeg To Use The Export Feature");
                    // TODO Open A Browser Showing How To Install FFMPEG
                }
            } else if (getExporterState() == ExporterState.EXPORT_IN_PROGRESS) {
                Tools.showtimedmessage(StatusBar, "Session Currently Being Exported", 3000);
            } else {
                if (Tools.getanswerdialog("Confirmation", "Session Already Exported", "Export Again?")) {
                    setExporterState(ExporterState.IDLE);
                    startexport();
                }
            }
        } else {Tools.showinformationdialog("Information", "Cannot Export", "No Cuts Selected");}
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
        setExporterState(ExporterState.IDLE);}
    public void exportcancelled() {
        System.out.println("Cancelled!");
        setExporterState(ExporterState.IDLE);}
    public void exportfinished() {
        System.out.println("Export Finished!");
        setExporterState(ExporterState.EXPORTED);
    }

// Other Methods
    public boolean checkforffmpeg() {
    boolean good = false;
    try {
        ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("ffmpeg", "-version"));
        final Process checkforffmpeg = processBuilder.start();
        checkforffmpeg.waitFor();
        good = checkforffmpeg.exitValue() == 0;
    } catch (IOException ignored) {} catch (InterruptedException e) {
        e.printStackTrace();
        good = false;
    }
    return good;
}
    public void setuptextfields() {
        Tools.integerTextField(PreTime);
        Tools.integerTextField(RinTime);
        Tools.integerTextField(KyoTime);
        Tools.integerTextField(TohTime);
        Tools.integerTextField(ShaTime);
        Tools.integerTextField(KaiTime);
        Tools.integerTextField(JinTime);
        Tools.integerTextField(RetsuTime);
        Tools.integerTextField(ZaiTime);
        Tools.integerTextField(ZenTime);
        Tools.integerTextField(PostTime);
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
        changeallvaluesbutton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
        loadpresetbutton.setTooltip(new Tooltip("Load A Saved Preset"));
        savepresetbutton.setTooltip(new Tooltip("Save This Session As A Preset"));
        exportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
    }
    public void updatecreatorui() {
        if (gettextfieldtimes()) {
            Integer totalsessiontime = 0;
            for (Integer i : textfieldtimes) {totalsessiontime += i;}
            TotalSessionTime.setText(Tools.minutestoformattedhoursandmins(totalsessiontime));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, totalsessiontime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            ApproximateEndTime.setText(sdf.format(cal.getTime()));
            Tools.showtimedmessage(StatusBar, "Turn Session Player On To Play This Session, Or Export By Pressing 'Export' Button", 5000);
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
                session.checkifambienceisgood(textfieldtimes, AmbienceSwitch);
            } else {
                Tools.showinformationdialog("Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceSwitch.setSelected(false);
            }
        } else {
            session.resetallcuts();
            session.setAmbienceenabled(false);
        }
    }
    public void changeallvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog();
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
            Tools.showerrordialog("Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
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
        changeallvaluesbutton.setDisable(true);
        AmbienceSwitch.setDisable(true);
        ApproximateEndTime.setDisable(true);
        TotalSessionTime.setDisable(true);
        PreTime.setDisable(true);
        RinTime.setDisable(true);
        KyoTime.setDisable(true);
        TohTime.setDisable(true);
        ShaTime.setDisable(true);
        KaiTime.setDisable(true);
        JinTime.setDisable(true);
        RetsuTime.setDisable(true);
        ZaiTime.setDisable(true);
        ZenTime.setDisable(true);
        PostTime.setDisable(true);
        StatusBar.setText("Creator Disabled While Session Player Enabled");
    }
    public void disablebuttons() {
        ExportButton.setDisable(true);
        loadpresetbutton.setDisable(true);
        savepresetbutton.setDisable(true);
    }
    @Override
    public void enable() {
        changeallvaluesbutton.setDisable(false);
        AmbienceSwitch.setDisable(false);
        ApproximateEndTime.setDisable(false);
        TotalSessionTime.setDisable(false);
        PreTime.setDisable(false);
        RinTime.setDisable(false);
        KyoTime.setDisable(false);
        TohTime.setDisable(false);
        ShaTime.setDisable(false);
        KaiTime.setDisable(false);
        JinTime.setDisable(false);
        RetsuTime.setDisable(false);
        ZaiTime.setDisable(false);
        ZenTime.setDisable(false);
        PostTime.setDisable(false);
        StatusBar.setText("");
    }
    public void enablebuttons() {
        ExportButton.setDisable(false);
        loadpresetbutton.setDisable(false);
        savepresetbutton.setDisable(false);
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
        boolean currentlyexporting = getExporterState() == ExporterState.EXPORT_IN_PROGRESS;
        if (currentlyexporting) {
            Tools.showinformationdialog("Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
        } else {This_Session.deleteprevioussession();}
        return ! currentlyexporting;
    }

// Presets
    public void loadpreset() {
        if (Preset.openpreset() && Preset.validpreset()) {
            changevaluestopreset(Preset.getpresettimes());
        } else {Tools.showinformationdialog("Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void savepreset() {
        Preset.setpresettimes(getcreatorvalues());
        if (! Preset.validpreset()) {Tools.showinformationdialog("Information", "Cannot Save Preset", "All Values Are 0"); return;}
        if (Preset.savepreset()) {Tools.showtimedmessage(StatusBar, "Preset Successfully Saved", 4000);}
        else {Tools.showerrordialog("Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
    }

// Subclasses/Dialogs
    public static class ChangeAllValuesDialog extends Stage implements Initializable {
        public Button AcceptButton;
        public Button CancelButton;
        public TextField changeAllValuesMinutesTextField;
        public CheckBox PresessionCheckbox;
        public CheckBox PostsessionCheckBox;
        private Boolean accepted;

        public ChangeAllValuesDialog() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Change All Values To:");}
            catch (IOException e) {e.printStackTrace();}
            setAccepted(false);
        }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            Tools.integerTextField(changeAllValuesMinutesTextField);
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

        public ExportingSessionDialog(This_Session thisSession) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ExportingSessionDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
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

        public SessionNotWellformedDialog(Parent parent, ArrayList<Integer> textfieldvalues, String cutsmissingtext, int lastcutindex) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionNotWellformedDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
            this.textfieldvalues = textfieldvalues;
            this.lastcutindex = lastcutindex;
            sessionmissingcutsLabel.setText(cutsmissingtext);
            populatelistview();
            explanationLabel.setText(("Your Practiced Cuts Do Not Connect! Due To The Nature Of The Kuji-In I Recommend " +
                    "Connecting All Cuts From RIN All The Way To Your Last Cut (") + Options.allnames.get(lastcutindex) +
                    ") Or Your This_Session Might Not Have The Energy It Needs");
            setCreatesession(false);
        }

        public void populatelistview() {
            ArrayList<String> items = new ArrayList<>();
            ObservableList<Text> sessionitems = FXCollections.observableArrayList();
            int count = 0;
            boolean thisitemmissing;
            for (int i = 0; i < textfieldvalues.size(); i++) {
                String name = Options.allnames.get(i);
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
            CutInvocationDialog cutdurationdialog = new CutInvocationDialog(null);
            cutdurationdialog.showAndWait();
            setInvocationduration(cutdurationdialog.getCutinvocationduration());
            setCreatesession(true);
            this.close();
        }

        public void createSessionwithoutmissingcuts(Event event) {
            if (Tools.getanswerdialog("Confirmation", "Session Not Well-Formed", "Really Create Anyway?")) {
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

        CutInvocationDialog(Parent parent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutInvocationDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating This_Session");}
            catch (IOException e) {e.printStackTrace();}
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            Tools.integerTextField(cutinvocationminutesTextField);
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
                    if (Tools.getanswerdialog("Confirmation", "Cut Invocation Value Is 0", "Continue With Zero Value (These Cuts Won't Be Included)" )) {
                        setCutinvocationduration(0);
                        this.close();
                    }
                }
            } catch (NumberFormatException e) {Tools.showerrordialog("Error", "Value Is Empty", "Enter A Numeric Value Then Press OK");}
        }
    }

// Enums
    public enum ExporterState {
        IDLE, EXPORT_IN_PROGRESS, EXPORTED
    }

}
