package kujiin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.widgets.CreatorAndExporterWidget;
import kujiin.widgets.GoalsWidget;
import kujiin.widgets.PlayerWidget;
import kujiin.widgets.ProgressTrackerWidget;
import kujiin.xml.Options;
import kujiin.xml.Session;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    public Label CreatorStatusBar;
    public Label PlayerStatusBar;
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
    public CheckBox AmbienceSwitch;
    public TextField ApproximateEndTime;
    public Button ChangeValuesButton;
    public TextField TotalSessionTime;
    public ComboBox<String> GoalCutComboBox;
    public Label GoalTopLabel;
    private This_Session Session;
    private GoalsWidget Goals;
    private CreatorAndExporterWidget CreatorAndExporter;
    private PlayerWidget Player;
    private ProgressTrackerWidget ProgressTracker;
    private Options Options;

// Event Handlers
    public static final EventHandler<KeyEvent> noneditabletextfield = event -> Tools.showinformationdialog("Information", "Can't Enter Text", "This Text Field Can't Be Edited");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setProgressTracker(new ProgressTrackerWidget(this));
        setSession(new This_Session(this));
        setGoals(new GoalsWidget(this));
        setCreatorAndExporter(new CreatorAndExporterWidget(this));
        setPlayer(new PlayerWidget(this));
        setOptions(new Options());
        getOptions().unmarshall();
        sessionplayerswitch(null);
        CreatorStatusBar.setText("");
    }
    public Boolean cleanup() {
        return getPlayer().cleanup() && getGoals().cleanup() && getCreatorAndExporter().cleanup() && getProgressTracker().cleanup();
    }

// Getters And Setters
    public GoalsWidget getGoals() {
    return Goals;
}
    public void setGoals(GoalsWidget goals) {
        this.Goals = goals;
    }
    public This_Session getSession() {
        return Session;
    }
    public void setSession(This_Session session) {
        this.Session = session;
    }
    public CreatorAndExporterWidget getCreatorAndExporter() {
        return CreatorAndExporter;
    }
    public void setCreatorAndExporter(CreatorAndExporterWidget creatorAndExporter) {
        this.CreatorAndExporter = creatorAndExporter;
    }
    public PlayerWidget getPlayer() {
        return Player;
    }
    public void setPlayer(PlayerWidget player) {
        this.Player = player;
    }
    public ProgressTrackerWidget getProgressTracker() {
        return ProgressTracker;
    }
    public void setProgressTracker(ProgressTrackerWidget progressTracker) {
        this.ProgressTracker = progressTracker;
    }
    public Options getOptions() {
        return Options;
    }
    public void setOptions(Options options) {
        Options = options;
    }

// Top Menu Actions
    public void changealertfile(ActionEvent actionEvent) {
        ChangeAlertDialog a = new ChangeAlertDialog(null);
        a.showAndWait();
        if (a.getAlertfilechanged()) {
            Tools.showtimedmessage(PlayerStatusBar, "Alert File Changed Successfully", 5000);
        } else {
            Tools.showtimedmessage(PlayerStatusBar, "Changing The Alert File Failed", 5000);
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
    public void updatetotalprogresswidget(ActionEvent actionEvent) {
        ProgressTracker.updateui();}
    public void displaylistofsessions(Event event) {
        ProgressTracker.displaysessionlist();}
    public void displayprematureendings(Event event) {
        ProgressTracker.displayprematureendings();}
    public void showcutprogress(Event event) {
        ProgressTracker.displaydetailedcutprogress();}

// Created Session Widget
    public boolean getsessioninformation() {
        try {
            boolean cutsinsession = Session.getCutsinsession().size() != 0;
            if (cutsinsession) {
                Session session = new Session();
                ArrayList<Integer> cuttimes = new ArrayList<>(11);
                for (String i : kujiin.xml.Options.allnames) {
                    Integer duration = 0;
                    for (Cut x : this.Session.getCutsinsession()) {if (x.name.equals(i)) {duration = x.getdurationinminutes();}}
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
            }
            // Set Ambience Enabled And This_Session Total Time
//            if (Session.getAmbienceenabled()) {AmbienceEnabledTextField.setText("Yes"); AmbienceEnabledTextField.setDisable(false);}
//            else {AmbienceEnabledTextField.setText("No"); AmbienceEnabledTextField.setDisable(true);}
//            TotalSessionTimeTextField.setText(Session.gettotalsessionduration());
//            TotalSessionTimeTextField.setDisable(! cutsinsession);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    public void loadpreset(ActionEvent actionEvent) {
        CreatorAndExporter.loadpreset();}
    public void savepreset(ActionEvent actionEvent) {
        CreatorAndExporter.saveaspreset(ProgressTracker.getSessions().getsession(ProgressTracker.getSessions().sessionscount() - 1));}
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
//            ChangeSessionValues createsession = new ChangeSessionValues(Session);
//            createsession.showAndWait();
//            if (getsessioninformation()) {creatorState = CreatorState.CREATED;}
//            else {
//                creatorState = CreatorState.NOT_CREATED;
//            }
//        } else {
//            PlayerStatusBar.setText("Session Creation In Progress");
//        }
    }
    public void exportsession(Event event) {
        CreatorAndExporter.exportsession();
    }
    public void settextfieldvalue(TextField textField, Integer value) {
        if (value > 0) {textField.setDisable(false); textField.setText(Integer.toString(value));}
        else {textField.setText("-"); textField.setDisable(true);}
    }
    public void ambienceswitch(ActionEvent actionEvent) {
        CreatorAndExporter.checkambience();}
    public void changeallcreatorvalues(ActionEvent actionEvent) {
        CreatorAndExporter.changeallvalues();}

// Session Player Widget
    public void sessionplayerswitch(ActionEvent actionEvent) {
        Player.statusSwitch();
        if (Player.isEnabled()) {
            CreatorAndExporter.disable();
            CreatorAndExporter.disablebuttons();
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setOnCloseRequest(event -> ProgressTracker.getSessions().deletenonvalidsessions());
        } else {
            CreatorAndExporter.enable();
            CreatorAndExporter.enablebuttons();
        }
    }
    public void playsession(Event event) {
        Player.play(ProgressTracker.getSessions());}
    public void pausesession(Event event) {
        Player.pause();}
    public void stopsession(Event event) {
        Player.stop(ProgressTracker.getSessions());}
    public void setReferenceOption(ActionEvent actionEvent) {
        Player.displayreferencefile();}
    public void adjustvolume(ActionEvent actionEvent) {
        Player.adjustvolume();}

// Goals Widget
    public void setnewgoal(Event event) {
        Goals.setnewgoal();}
    public void getgoalpacing(Event event) {
        Goals.goalpacing();}
    public void viewcurrentgoals(Event event) {
        Goals.displaycurrentgoals();}
    public void viewcompletedgoals(Event event) {
        Goals.displaycompletedgoals();}

    // Menu Tools/Dialogs
    public static class ChangeAlertDialog extends Stage {
        public TextField alertfileTextField;
        public Button openFileButton;
        public Button AcceptButton;
        public Button CancelButton;
        private File newalertfile = null;
        private File alertfileactual = kujiin.xml.Options.alertfile;
        private Boolean alertfilechanged = null;

        public ChangeAlertDialog(Parent parent) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAlertDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
            catch (IOException e) {new MainController.ExceptionDialog(e.getClass().getName(), e.getMessage());}
            if (alertfileactual.exists()) {this.setTitle("Change Alert File");}
            else {this.setTitle("Add A New Alert File");}
            alertfileTextField.setEditable(false);
        }

        public void openandtestnewfile(Event event) {
            FileChooser a = new FileChooser();
            File newfile = a.showOpenDialog(this);
            if (newfile != null) {
                if (newfile.toString().endsWith(".mp3")) {
                    double duration = Tools.getaudioduration(newfile);
                    if (duration > 10000) {
                        if (Tools.getanswerdialog("Validation", "Alert File Is longer Than 10 Seconds",
                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                                "Really Use It?", duration))) {
                            alertfileTextField.setText(newfile.getName());
                            newalertfile = newfile;
                        }
                    } else {
                        alertfileTextField.setText(newfile.getName());
                        newalertfile = newfile;
                    }
                }
            }
        }

        public void commitchanges(Event event) {
            if (newalertfile != null) {
                if (alertfileactual.exists()) {                                                                             // Change Alert File
                    File tempfile = new File(kujiin.xml.Options.sounddirectory, "AlertTemp.mp3");
                    try {
                        // Make A Temp File Copy In Case It Fails
                        FileUtils.copyFile(alertfileactual, tempfile);
                        alertfileactual.delete();
                        FileUtils.copyFile(newalertfile, alertfileactual);
                        if (Tools.testAlertFile()) {tempfile.delete();}
                    } catch (IOException e) {setAlertfilechanged(false);}
                } else {                                                                                                    // Set A New Alert File
                    try {
                        FileUtils.copyFile(newalertfile, alertfileactual);
                    } catch (IOException e) {setAlertfilechanged(false);}
                }
                setAlertfilechanged(Tools.testAlertFile());
                this.close();
            } else {Tools.showerrordialog("Error", "No Alert File Opened", "Open An Alert File First");}
        }

        public void cancel(Event event) {
            setAlertfilechanged(false);
            this.close();
        }

        public Boolean getAlertfilechanged() {
            if (alertfilechanged != null) {
                return alertfilechanged;
            } else {
                return false;
            }
        }
        public void setAlertfilechanged(Boolean alertfilechanged) {this.alertfilechanged = alertfilechanged;}
    }
    public static class EditReferenceFiles extends Stage {

        public ChoiceBox<String> CutNamesChoiceBox;
        public ChoiceBox<String> CutVariationsChoiceBox;
        public TextArea MainTextArea;
        public Button CloseButton;
        public Label SelectReferenceFileLabel;
        public Label StatusBar;
        public Button SaveButton;
        public Button LoadButton;
        public Button PreviewButton;
        private ObservableList<String> cutnames;
        private ObservableList<String> variations;
        private File htmldirectory = new File(kujiin.xml.Options.directoryreference, "html");
        private File txtdirectory = new File(kujiin.xml.Options.directoryreference, "txt");
        private File selectedfile;
        private String selectedcut;
        private String selectedvariation;

        public EditReferenceFiles() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
            catch (IOException e) {new MainController.ExceptionDialog(e.getClass().getName(), e.getMessage());}
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            cutnames = FXCollections.observableArrayList();
            variations = FXCollections.observableArrayList();
            cutnames.addAll(kujiin.xml.Options.allnames);
            variations.addAll(Arrays.asList("html", "txt"));
            CutNamesChoiceBox.setItems(cutnames);
            CutVariationsChoiceBox.setItems(variations);
        }

        public void closewindow(Event event) {
            // Check If Unsaved Text
            this.close();
        }

    // Text Area Methods
        private boolean unsavedchanges() {
            if (selectedfile == null) {return false;}
            try {
                Scanner sc1 = new Scanner(selectedfile);
                Scanner sc2 = new Scanner(MainTextArea.getText());
                while (sc1.hasNext() && sc2.hasNext()) {if (! sc1.next().equals(sc2.next())) {return false;}}
                return true;
            } catch (FileNotFoundException | NullPointerException ignored) {return false;}
        }
        private void textchanged() {
            if (selectedvariation != null && selectedcut != null) {
                if (selectedvariation.equals("html")) {
                    StatusBar.setTextFill(Color.BLACK);
                    if (MainTextArea.getText().matches(".*\\<[^>]+>.*")) {StatusBar.setText("Valid .html File");}
                    else {StatusBar.setText("Not Valid .html");}
                } else {
                    StatusBar.setTextFill(Color.RED);
                    if (MainTextArea.getText().length() == 0) {StatusBar.setText("No Text Entered");}
                    else {StatusBar.setText("");}
                }
            } else {
                MainTextArea.clear();
                StatusBar.setTextFill(Color.RED);
                Tools.showtimedmessage(StatusBar, "Select A Cut And Variation And Press 'Load' First", 3000);
            }
        }

    // File Methods
        public void savefile(ActionEvent actionEvent) {
            if (Tools.writeFileContents(selectedfile, Tools.getFileContents(selectedfile))) {
                String text = selectedcut + "'s Reference File (" + selectedvariation + " Variation) Has Been Saved";
                Tools.showtimedmessage(StatusBar, text, 5000);
            } else {
                Tools.showerrordialog("Error", "Couldn't Save", "Does The File Exist/Do You Have Access To It?");}
        }
        public void loadnewfile(ActionEvent actionEvent) {
            if (! CutNamesChoiceBox.getValue().equals("") && ! CutVariationsChoiceBox.getValue().equals("")) {
                if (unsavedchanges()) {
                    if (Tools.getanswerdialog("Confirmation", "Document Has Unsaved Changes", "Save These Changes Before Loading A Different File?")) {savefile(null);}
                    else {return;}
                }
                selectedcut = CutNamesChoiceBox.getValue();
                selectedvariation = CutVariationsChoiceBox.getValue();
                SelectReferenceFileLabel.setText(String.format("%s's Reference File (%s Variation)", selectedcut, selectedvariation));
                if (selectedvariation.equals("html")) {selectedfile = new File(htmldirectory, selectedcut + ".html");}
                else {selectedfile = new File(txtdirectory, selectedcut + ".txt");}
                MainTextArea.setText(Tools.getFileContents(selectedfile));
            } else {
                if (CutNamesChoiceBox.getValue().equals("")) {
                    Tools.showinformationdialog("Information", "No Cut Selected", "Select A Cut To Load");}
                else {
                    Tools.showinformationdialog("Information", "No Variation Selected", "Select A Variation To Load");}
                SelectReferenceFileLabel.setText("Select A Cut Name And Variation And Press 'Load'");
            }
        }

        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && selectedvariation != null) {
                if (selectedvariation.equals("html")) {
                    PlayerWidget.DisplayReference dr = new PlayerWidget.DisplayReference(MainTextArea.getText());
                    dr.showAndWait();
                } else {
                    Tools.showinformationdialog("Information", "Preview Is For Html Content Not Available For Text Only", "Cannot Open Preview");
                }
            }
        }
    }
    public static class SessionAmbienceEditor extends Stage implements Initializable {
        public Button RightArrow;
        public Button LeftArrow;
        public Label CutSelectionLabel;
        public Button NewAmbienceAddButton;
        public Button NewAmbienceRemoveButton;
        public Button NewAmbiencePreviewButton;
        public Slider PreviewSlider;
        public Label PreviewCurrentTime;
        public Label PreviewTotalTime;
        public TextField PreviewFileName;
        public Slider PreviewVolumeSlider;
        public Button PreviewStartButton;
        public Button CloseButton;
        public Button CurrentAmbienceRemoveButton;
        public Button CurrentAmbiencePreviewButton;
        public ChoiceBox<String> CutSelectionBox;
        public TableView<AmbienceSong> NewAmbienceTable;
        public TableColumn<AmbienceSong, String> New_NameColumn;
        public TableColumn<AmbienceSong, String> New_DurationColumn;
        public TableView<AmbienceSong> CurrentAmbienceTable;
        public TableColumn<AmbienceSong, String> Current_NameColumn;
        public TableColumn<AmbienceSong, String> Current_DurationColumn;
        public TextField Current_TotalDuration;
        private String selectedcutname = null;
        private Media previewmedia = null;
        private MediaPlayer previewmediaplayer = null;
        private ObservableList<AmbienceSong> new_songlist = FXCollections.observableArrayList();
        private ObservableList<AmbienceSong> current_songlist = FXCollections.observableArrayList();
        private AmbienceSong selected_new_ambiencesong;
        private AmbienceSong selected_current_ambiencesong;
        private File tempdirectory;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            New_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            New_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            Current_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            Current_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
            NewAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> newselectionchanged(newValue));
            CurrentAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> currentselectionchanged(newValue));
            ObservableList<String> allnames = FXCollections.observableArrayList();
            allnames.addAll(kujiin.xml.Options.allnames);
            CutSelectionBox.setItems(allnames);
            this.setOnCloseRequest(event -> close());
        }

        public SessionAmbienceEditor() {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Session Ambience Editor");}
            catch (IOException e) {e.printStackTrace();}
            CutSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.directorytemp, "AmbienceEditor");
        }
        public SessionAmbienceEditor(String cutname) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Session Ambience Editor");}
            catch (IOException e) {e.printStackTrace();}
            CutSelectionBox.setOnAction(event -> selectandloadcut());
            CutSelectionBox.getSelectionModel().select(kujiin.xml.Options.allnames.indexOf(cutname));
            tempdirectory = new File(kujiin.xml.Options.directorytemp, "AmbienceEditor");
        }

    // Transfer Methods
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_new_ambiencesong != null && selectedcutname != null) {
                Service<Void> copyfile = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                File cutdirectory = new File(kujiin.xml.Options.directoryambience, selectedcutname);
                                File newfile = new File(cutdirectory, selected_new_ambiencesong.name.getValue());
                                FileUtils.copyFile(selected_new_ambiencesong.getFile(), newfile);
                                return null;
                            }
                        };
                    }
                };
                copyfile.setOnSucceeded(event -> selectandloadcut());
                copyfile.setOnFailed(event -> Tools.showerrordialog("Error", "Couldn't Copy File To " + selectedcutname + "'s Ambience Directory", "Check File Permissions"));
                copyfile.start();
            } else {
                if (selected_new_ambiencesong == null) {
                    Tools.showinformationdialog("Information", "Cannot Transfer", "Nothing Selected");
                } else {
                    Tools.showinformationdialog("Information", "Cannot Transfer", "No Cut Selected");
                }
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_current_ambiencesong != null && selectedcutname != null) {
                for (AmbienceSong i : NewAmbienceTable.getItems()) {
                    if (selected_current_ambiencesong.name.getValue().equals(i.name.getValue())) {
                        Tools.showinformationdialog("Information", "File Already Exists", "Select A Different File To Transfer");
                        return;
                    }
                }
                System.out.println(new_songlist.size());
                Service<Void> copyfile = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                File newfile = new File(tempdirectory, selected_current_ambiencesong.name.getValue());
                                FileUtils.copyFile(selected_current_ambiencesong.getFile(), newfile);
                                return null;
                            }
                        };
                    }
                };
                copyfile.setOnSucceeded(event -> {
                    File newfile = new File(tempdirectory, selected_current_ambiencesong.name.getValue());
                    new_songlist.add(new AmbienceSong(newfile.getName(), newfile));
                    System.out.println(new_songlist.size());
                    NewAmbienceTable.setItems(new_songlist);
                });
                copyfile.setOnFailed(event -> Tools.showerrordialog("Error", "Couldn't Copy File To Temp Directory", "Check File Permissions"));
                copyfile.start();
            }
        }

    // New Ambience Methods
        public void addfilestonewambience(ActionEvent actionEvent) {
            FileChooser a = new FileChooser();
            List<File> files = a.showOpenMultipleDialog(this);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    if (i.getName().endsWith(".mp3") && Tools.getaudioduration(i) != 0.0) {new_songlist.add(new AmbienceSong(i.getName(), i));}
                    else {notvalidfilenames.add(i);}
                }
            }
            if (new_songlist.size() > 0) {NewAmbienceTable.setItems(new_songlist);}
            if (notvalidfilenames.size() > 0) {
                StringBuilder c = new StringBuilder();
                for (File i : notvalidfilenames) {
                    c.append(i.getName());
                    if (i != notvalidfilenames.get(notvalidfilenames.size() - 1)) {c.append("\n");}
                }
                Tools.showinformationdialog("Information", "The Files Weren't Added Because They Are Unsupported", c.toString());
            }
        }
        public void removefromnewambience(ActionEvent actionEvent) {
            int index = NewAmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                NewAmbienceTable.getItems().remove(index);
            } else {
                Tools.showinformationdialog("Information", "Nothing Selected", "Select A Table Item To Remove");
            }
        }
        public void previewnewambience(ActionEvent actionEvent) {
            if (selected_new_ambiencesong != null && NewAmbienceTable.getItems().size() > 0) {
                if (previewmediaplayer != null && previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    previewmediaplayer.stop();
                    previewmediaplayer.dispose();
                }
                previewmedia = new Media(selected_new_ambiencesong.getFile().toURI().toString());
                previewmediaplayer = new MediaPlayer(previewmedia);
                PreviewTotalTime.setText(selected_new_ambiencesong.getTotaldurationshort());
                PreviewFileName.setText(selected_new_ambiencesong.name.getValue());
                PreviewCurrentTime.setText("00:00");
            }
        }
        public void newselectionchanged(AmbienceSong ambiencesong) {
            selected_new_ambiencesong = ambiencesong;
        }

    // Current Ambience Methods
        public void selectandloadcut() {
            if (selected_current_ambiencesong != null) {
                for (AmbienceSong i : CurrentAmbienceTable.getItems()) {
                    if (i.name.getValue().equals(PreviewFileName.getText())) {resetpreviewplayer();}
                }
            }
            current_songlist.clear();
            CurrentAmbienceTable.getItems().clear();
            int index = kujiin.xml.Options.allnames.indexOf(CutSelectionBox.getValue());
            selectedcutname = kujiin.xml.Options.allnames.get(index);
            if (getcurrentambiencefiles()) {
                CurrentAmbienceTable.getItems().addAll(current_songlist);
                CutSelectionLabel.setText(selectedcutname + "'s Ambience");
            }
        }
        public void removecurrentambience(ActionEvent actionEvent) {
            int index = CurrentAmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                String filename = current_songlist.get(index).getName();
                if (Tools.getanswerdialog("Confirmation", String.format("Remove '%s' From %s's Ambience?", filename, selectedcutname), "This Cannot Be Undone")) {
                    if (current_songlist.get(index).getFile().delete()) {
                        CurrentAmbienceTable.getItems().remove(index);
                        current_songlist.remove(index);
                        selectandloadcut();
                    } else {
                        Tools.showerrordialog("Error", "Couldn't Delete '" + filename + "'", "Check File Permissions");}
                }
            } else {
                Tools.showinformationdialog("Information", "Nothing To Remove", "Select An Item To Remove");}
        }
        public void previewcurrentambience(ActionEvent actionEvent) {
            if (selected_current_ambiencesong != null && CurrentAmbienceTable.getItems().size() > 0) {
                if (previewmediaplayer != null && previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    previewmediaplayer.stop();
                    previewmediaplayer.dispose();
                }
                previewmedia = new Media(selected_current_ambiencesong.getFile().toURI().toString());
                previewmediaplayer = new MediaPlayer(previewmedia);
                PreviewTotalTime.setText(selected_current_ambiencesong.getTotaldurationshort());
                PreviewFileName.setText(selected_current_ambiencesong.name.getValue());
                PreviewCurrentTime.setText("00:00");
            }
        }
        public void currentselectionchanged(AmbienceSong ambiencesong) {selected_current_ambiencesong = ambiencesong;}
        public boolean getcurrentambiencefiles() {
            if (selectedcutname != null) {
                File thisdirectory = new File(kujiin.xml.Options.directoryambience, selectedcutname);
                try {
                    for (File i : thisdirectory.listFiles()) {
                        if (Tools.validaudiofile(i)) {current_songlist.add(new AmbienceSong(i.getName(), i));}
                    }
                    return true;
                } catch (NullPointerException e) {
                    Tools.showinformationdialog("Information", selectedcutname + " Has No Ambience", "Please Add Ambience To " + selectedcutname);
                    return false;
                }
            } else {
                Tools.showinformationdialog("Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

    // Other Methods
        public void preview(ActionEvent actionEvent) {
            if (previewmediaplayer != null && previewmedia != null) {
                if (previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    previewmediaplayer.stop();
                    PreviewStartButton.setText("Start");
                } else {
                    previewmediaplayer.play();
                    PreviewSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                        double position = PreviewSlider.getValue();
                        Duration seektothis = previewmediaplayer.getTotalDuration().multiply(position);
                        previewmediaplayer.seek(seektothis);
                    });
                    previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                        PreviewCurrentTime.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
                        updatePositionSlider(previewmediaplayer.getCurrentTime());
                    });
                    PreviewVolumeSlider.valueChangingProperty().unbind();
                    PreviewVolumeSlider.valueProperty().bindBidirectional(previewmediaplayer.volumeProperty());
                    PreviewStartButton.setText("Stop");
                }
            } else {
                Tools.showinformationdialog("Information", "Nothing To Preview", "Select A Table Item And Press Preview");}
        }
        public void updatePositionSlider(Duration currenttime) {
            if (PreviewSlider.isValueChanging()) {return;}
            Duration total = previewmediaplayer.getTotalDuration();
            if (total == null || currenttime == null) {PreviewSlider.setValue(0);}
            else {PreviewSlider.setValue(currenttime.toMillis() / total.toMillis());}
        }
        public void closebuttonpressed(ActionEvent actionEvent) {close();}
        @Override
        public void close() {
            super.close();
            try {
                FileUtils.cleanDirectory(tempdirectory);} catch (IOException ignored) {}
            // Clear Out Temp Directory
            if (previewmediaplayer != null && previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
                previewmediaplayer.stop();
                previewmediaplayer.dispose();
            }
        }
        public void resetpreviewplayer() {
            if (previewmediaplayer != null) {previewmediaplayer.stop(); previewmediaplayer.dispose();}
            PreviewFileName.setText("No File Selected");
            PreviewTotalTime.setText("--:--");
            PreviewCurrentTime.setText("--:--");
            PreviewSlider.setValue(0);
            PreviewVolumeSlider.setValue(0);
            PreviewStartButton.setText("Start");
        }

        class AmbienceSong {
            private StringProperty name;
            private StringProperty length;
            private File file;
            private String totaldurationshort;

            public AmbienceSong(String name, File file) {
                this.name = new SimpleStringProperty(name);
                this.file = file;
                double duration = Tools.getaudioduration(file);
                totaldurationshort = Tools.formatlengthshort((int) duration);
                this.length = new SimpleStringProperty(totaldurationshort);
            }

            public String getName() {
                return name.getValue();
            }

            public File getFile() {
                return file;
            }

            public String getTotaldurationshort() {
                return totaldurationshort;
            }
        }

    }
    public static class ExceptionDialog extends Stage {
        public TextArea StackTraceTextField;
        public Button CloseButton;
        public Button ContinueButton;
        public CheckBox NotifyMeCheckbox;
        public Label TopText;

        public ExceptionDialog(String exceptionname, String stacktrace) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExceptionDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Error Occured");}
            catch (IOException e) {e.printStackTrace();}
            TopText.setText(exceptionname + " Occured");
            StackTraceTextField.setText(stacktrace);
        }

        public void exit(ActionEvent actionEvent) {
            if (NotifyMeCheckbox.isSelected()) {
                Tools.sendstacktracetodeveloper(StackTraceTextField.getText());
            }
            this.close();
            System.exit(1);
        }
        public void continueprogram(ActionEvent actionEvent) {
            if (NotifyMeCheckbox.isSelected()) {
                Tools.sendstacktracetodeveloper(StackTraceTextField.getText());
            }
            this.close();
        }

    }
//    public static class SimpleTextDialogWithCancelButton extends Stage {
//        public Button CancelButton;
//        public Label Message;
//
//        public SimpleTextDialogWithCancelButton(Parent parent, String titletext, String message) {
//             FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SimpleTextDialogWithCancelButton.fxml"));
//             fxmlLoader.setController(this);
//             try {setScene(new Scene(parent, fxmlLoader.load())); this.setTitle(titletext);}
//             catch (IOException e) {e.printStackTrace();}
//             Message.setText(message);
//        }
//    }
}
