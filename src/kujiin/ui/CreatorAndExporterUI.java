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
import kujiin.xml.Options;
import kujiin.xml.Preset;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
        StatusBar = root.CreatorStatusBar;
        exporterState = ExporterState.NOT_EXPORTED;
        creatorState = CreatorState.NOT_CREATED;
        setuptextfields();
        TotalSessionTime.setEditable(false);
        ApproximateEndTime.setEditable(false);
        exportservices = new ArrayList<>();
        updateuitimeline = new Timeline(new KeyFrame(Duration.millis(10000), ae -> updatecreatorui()));
        updateuitimeline.setCycleCount(Animation.INDEFINITE);
        updateuitimeline.play();
//        updatecreatorui();
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
    public void setSession(This_Session session) {this.session = session;}
    
// GUI
    public void setuptextfields() {
        if (Root.getOptions().getProgramOptions().getTooltips()) {
            TotalSessionTime.setTooltip(new Tooltip("Total Session Time (Not Including Presession + Postsession Ramp, And Alert File)"));
            ApproximateEndTime.setTooltip(new Tooltip("Approximate Finish Time For This Session (Assuming You Start Now)"));
            AmbienceSwitch.setTooltip(new Tooltip("Check This After You Set All Values To Check For And Enable Ambience For This Session"));
            ChangeAllValuesButton.setTooltip(new Tooltip("Change All Cut Values Simultaneously"));
            LoadPresetButton.setTooltip(new Tooltip("Load A Saved Preset"));
            SavePresetButton.setTooltip(new Tooltip("Save This Session As A Preset"));
            ExportButton.setTooltip(new Tooltip("Export This Session To .mp3 For Use Without The Program"));
        }
    }
    public void updatecreatorui() {
        if (textfieldsnotallzero()) {
            Integer totalsessiontime = 0;
            for (Integer i : session.getallsessionvalues()) {totalsessiontime += i;}
            int rampduration = Root.getOptions().getSessionOptions().getRampduration();
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
            Util.gui_showtimedmessageonlabel(StatusBar, "Session Values Changed, Ambience Unselected", 5000);
        }
    }
    public void setDisable(boolean disabled) {
        ChangeAllValuesButton.setDisable(disabled);
        LoadPresetButton.setDisable(disabled);
        SavePresetButton.setDisable(disabled);
        AmbienceSwitch.setDisable(disabled);
        TotalSessionTime.setDisable(disabled);
        ApproximateEndTime.setDisable(disabled);
        for (Meditatable i : Root.getSession().getAllMeditatables()) {
            i.setDisable(disabled);
        }
        if (disabled) {updateuitimeline.stop();}
        else {updateuitimeline.play();}
    }

// Creation
    public boolean textfieldsnotallzero() {
        try {
            for (Integer i : session.getallsessionvalues()) {if (i > 0) {return true;}}
            return  false;
        } catch (NullPointerException ignored) {return false;}
    }
    public boolean longsession() {
        for (Integer i : session.getallsessionvalues()) {
            if (i >= Options.DEFAULT_LONG_MEDITATABLE_DURATION) {return true;}
        }
        return false;
    }
    public void createsession() {
        // TODO Check Exporter Here
        if (! textfieldsnotallzero()) {
            Util.gui_showerrordialog(Root, "Error Creating Session", "At Least One Cut Or Element's Value Must Not Be 0", "Cannot Create Session");
            setCreatorState(CreatorState.NOT_CREATED);
            return;
        }
        if (longsession()) {
            if (! Root.getOptions().getSessionOptions().getAlertfunction()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Add Alert File", "I've Detected A Long Session. Long Sessions Can Make It Difficult To Hear " +
                        "The Subtle Transitions In Between Session Parts", "Add Alert File In Between Session Parts?")) {
                    new MainController.ChangeAlertFile(Root).showAndWait();
                }
            }
        }
        boolean creationstate = session.createsession();
        if (creationstate) setCreatorState(CreatorState.CREATED);
        else {setCreatorState(CreatorState.NOT_CREATED);}
        setDisable(getCreatorState() == CreatorState.NOT_CREATED);
    }

// Export
    public void toggleexport() {
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
    public void checkambience() {
        if (AmbienceSwitch.isSelected()) {
            if (textfieldsnotallzero()) {
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
            Integer min = changevaluesdialog.getMinutes();
            for (Cut i : session.getallCuts()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {session.getPostsession().changevalue(min);}
        }
    }
    public void changeallelementvalues() {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(Root, "Change All Element Values To: ");
        changevaluesdialog.showAndWait();
        if (changevaluesdialog.getAccepted()) {
            Integer min = changevaluesdialog.getMinutes();
            for (Element i : session.getallElements()) {i.changevalue(min);}
            if (changevaluesdialog.getincludepresession()) {session.getPresession().changevalue(min);}
            if (changevaluesdialog.getincludepostsession()) {session.getPostsession().changevalue(min);}
        }
    }
    public boolean cleanup() {
        boolean currentlyexporting = getExporterState() == ExporterState.WORKING;
        if (currentlyexporting) {
            Util.gui_showinformationdialog(Root, "Information", "Currently Exporting", "Wait For The Export To Finish Before Exiting");
        } else {This_Session.deleteprevioussession();}
        return ! currentlyexporting;
    }

// Presets
    public void changevaluestopreset(ArrayList<Double> presetvalues) {
        try {
            for (int i = 0; i < Root.getSession().getAllMeditatables().size(); i++) {
                 Root.getSession().getAllMeditatables().get(i).setDuration(presetvalues.get(i));
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            Util.gui_showerrordialog(Root, "Error", "Couldn't Change Creator Values To Preset", "Try Reloaded Preset");
        }
    }
    public void loadpreset() {
        File presetfile = Preset.openpreset();
        if (presetfile != null && Preset.validpreset()) {
            changevaluestopreset(Preset.getpresettimes());
        } else {if (presetfile != null) Util.gui_showinformationdialog(Root, "Invalid Preset File", "Invalid Preset File", "Cannot Load File");}
    }
    public void savepreset() {
        // TODO Saving Preset Is Broke!
        ArrayList<Double> creatorvalues = new ArrayList<>();
        for (Meditatable i : Root.getSession().getAllMeditatables()) {creatorvalues.add(i.getduration().toMinutes());}
        Preset.setpresettimes(creatorvalues);
        if (! Preset.validpreset()) {Util.gui_showinformationdialog(Root, "Information", "Cannot Save Preset", "All Values Are 0"); return;}
        if (Preset.savepreset()) {Util.gui_showtimedmessageonlabel(StatusBar, "Preset Successfully Saved", 4000);}
        else {Util.gui_showerrordialog(Root, "Error", "Couldn't Save Preset", "Your Preset Could Not Be Saved, Do You Have Write Access To That Directory?");}
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
        private int minutes;

        public ChangeAllValuesDialog(MainController root, String toptext) {
            try {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAllValuesDialog.fxml"));
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
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
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
        private List<Cut> allcuts;
        private List<Cut> missingcuts;
        private Util.AnswerType result;
        private MainController Root;

        public CutsMissingDialog(MainController root, List<Cut> allcuts) {
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

// Enums
    public enum ExporterState {
        NOT_EXPORTED, WORKING, COMPLETED, FAILED, CANCELLED
    }
    public enum CreatorState {
        NOT_CREATED, CREATED
    }

}
