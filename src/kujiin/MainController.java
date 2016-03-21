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
import kujiin.widgets.PlayerWidget;
import kujiin.widgets.ProgressAndGoalsWidget;
import kujiin.xml.Options;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
// TODO Saving Preset Is Broke!
// TODO Refactor Ambience For Cut So It's Stored In XML And Files Can Be Anywhere On The File System

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
    public ComboBox<String> GoalCutComboBox;
    public Label GoalTopLabel;
    public Label LengthLabel;
    public Label CompletionLabel;
    public TextField GoalPracticedMinutes;
    public TextField GoalSetHours;
    public TextField GoalSetMinutes;
    public Label GoalStatusBar;
    public TextField GoalPracticedHours;
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
    public Scene Scene;
    public Stage Stage;
    public Button ResetCreatorButton;
    private This_Session Session;
    private CreatorAndExporterWidget CreatorAndExporter;
    private PlayerWidget Player;
    private ProgressAndGoalsWidget ProgressTracker;
    private Options Options;

// Event Handlers
    public final EventHandler<KeyEvent> NONEDITABLETEXTFIELD = event -> Tools.showinformationdialog(this, "Not Editable", "Non-Editable Text Field", "This Text Field Can't Be Edited");
    public final EventHandler<ActionEvent> CHECKBOXONOFFLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("ON");} else {a.setText("OFF");}};
    public final EventHandler<ActionEvent> CHECKBOXYESNOLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("YES");} else {a.setText("NO");}};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
        setProgressTracker(new ProgressAndGoalsWidget(this));
        setSession(new This_Session(this));
        setCreatorAndExporter(new CreatorAndExporterWidget(this));
        CreatorStatusBar.setText("");
    }
    public boolean cleanup() {
        return getCreatorAndExporter().cleanup() && getProgressTracker().cleanup();
    }

// Getters And Setters
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
    public ProgressAndGoalsWidget getProgressTracker() {
        return ProgressTracker;
    }
    public void setProgressTracker(ProgressAndGoalsWidget progressTracker) {
        this.ProgressTracker = progressTracker;
    }
    public Options getOptions() {
        return Options;
    }
    public void setOptions(Options options) {
        Options = options;
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
    getProgressTracker().updategoalsui();
    getProgressTracker().updateprogressui();
}
    public void editprogramsambience(ActionEvent actionEvent) {
        AdvancedAmbienceEditor sae = new AdvancedAmbienceEditor(this);
        sae.showAndWait();
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        EditReferenceFiles a = new EditReferenceFiles(this);
        a.showAndWait();
    }
    public void howtouseprogram(ActionEvent actionEvent) {Tools.howtouseprogram(this);}
    public void aboutthisprogram(ActionEvent actionEvent) {Tools.aboutthisprogram();}
    public void contactme(ActionEvent actionEvent) {Tools.contactme();}
    public void close(ActionEvent actionEvent) {
        if (cleanup()) {System.exit(0);}
    }

// Total Progress And Goals Widget
    public void updatetotalprogresswidget(ActionEvent actionEvent) {
        ProgressTracker.updateprogressui();}
    public void displaylistofsessions(Event event) {
        ProgressTracker.displaysessionlist();
    }
    public void showcutprogress(Event event) {
        ProgressTracker.displaydetailedcutprogress();}
    public void setnewgoal(Event event) {
        ProgressTracker.setnewgoal();}
    public void viewcurrentgoals(Event event) {
        ProgressTracker.opengoaleditor();}

// Creator And Exporter Widget
    public void loadpreset(ActionEvent actionEvent) {
        CreatorAndExporter.loadpreset();}
    public void savepreset(ActionEvent actionEvent) {
        CreatorAndExporter.savepreset();}
    public void toggleexporter(ActionEvent actionEvent) {
        getCreatorAndExporter().toggleexport();
    }
    public void exportsession(Event event) {
        //        CreatorAndExporter.startexport();}
        Tools.showtimedmessage(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
    }
    public void ambienceswitch(ActionEvent actionEvent) {
        CreatorAndExporter.checkambience();}
    public void resetallcreatorvalues(ActionEvent actionEvent) {
    }
    public void changeallcutsvalues(ActionEvent actionEvent) {
        CreatorAndExporter.changeallcutvalues();}
    public void changeallelementsvalues(ActionEvent actionEvent) {
        CreatorAndExporter.changeallelementvalues();
    }

// Session Player Widget
    public void playthisession(ActionEvent actionEvent) {
        getCreatorAndExporter().togglecreator();
        if (getCreatorAndExporter().getCreatorState() == CreatorAndExporterWidget.CreatorState.CREATED) {
            if (getPlayer() != null && getPlayer().isShowing()) {return;}
            getStage().setIconified(true);
            setPlayer(new PlayerWidget(this));
            getPlayer().showAndWait();
            getStage().setIconified(false);
        }
    }

    // Dialogs
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
        private File htmldirectory = new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html");
        private File txtdirectory = new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt");
        private File selectedfile;
        private String selectedcut;
        private String selectedvariation;
        private MainController Root;

        public EditReferenceFiles(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference Files Editor");
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            cutnames = FXCollections.observableArrayList();
            variations = FXCollections.observableArrayList();
            cutnames.addAll(kujiin.xml.Options.CUTNAMES);
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
                Tools.showerrordialog(Root, "Error", "Couldn't Save", "Does The File Exist/Do You Have Access To It?");}
        }
        public void loadnewfile(ActionEvent actionEvent) {
            if (! CutNamesChoiceBox.getValue().equals("") && ! CutVariationsChoiceBox.getValue().equals("")) {
                if (unsavedchanges()) {
                    if (Tools.getanswerdialog(Root, "Confirmation", "Document Has Unsaved Changes", "Save These Changes Before Loading A Different File?")) {savefile(null);}
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
                    Tools.showinformationdialog(Root, "Information", "No Cut Selected", "Select A Cut To Load");}
                else {
                    Tools.showinformationdialog(Root, "Information", "No Variation Selected", "Select A Variation To Load");}
                SelectReferenceFileLabel.setText("Select A Cut Name And Variation And Press 'Load'");
            }
        }

        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && selectedvariation != null) {
                if (selectedvariation.equals("html")) {
                    PlayerWidget.DisplayReference dr = new PlayerWidget.DisplayReference(Root, MainTextArea.getText());
                    dr.showAndWait();
                } else {
                    Tools.showinformationdialog(Root, "Information", "Preview Is For Html Content Not Available For Text Only", "Cannot Open Preview");
                }
            }
        }
    }
    public static class AdvancedAmbienceEditor extends Stage implements Initializable {
        public Button RightArrow;
        public Button LeftArrow;
        public Label CutSelectionLabel;
        public ChoiceBox<String> CutOrElementSelectionBox;
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
        private ObservableList<AmbienceSong> temp_ambiencesonglist = FXCollections.observableArrayList();
        private AmbienceSong selected_temp_ambiencesong;
        private AmbienceSong selected_actual_ambiencesong;
        private String selectedcutorelementname;
        private File tempdirectory;
        private MainController Root;
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
            allnames.addAll(kujiin.xml.Options.CUTNAMES);
            CutOrElementSelectionBox.setItems(allnames);
            this.setOnCloseRequest(event -> close());
        }

        public AdvancedAmbienceEditor(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AdvancedAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            CutOrElementSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(MainController root, String cutname) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AdvancedAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            CutOrElementSelectionBox.setOnAction(event -> selectandloadcut());
            CutOrElementSelectionBox.getSelectionModel().select(kujiin.xml.Options.CUTNAMES.indexOf(cutname));
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }

    // Transfer Methods
        // TODO Add Check Duplicates Before Moving Over (Or Ask Allow Duplicates?)
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_temp_ambiencesong != null && selectedcutorelementname != null) {
                Service<Void> copyfile = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                File cutdirectory = new File(kujiin.xml.Options.DIRECTORYAMBIENCE, selectedcutorelementname);
                                File newfile = new File(cutdirectory, selected_temp_ambiencesong.name.getValue());
                                FileUtils.copyFile(selected_temp_ambiencesong.getFile(), newfile);
                                return null;
                            }
                        };
                    }
                };
                copyfile.setOnSucceeded(event -> selectandloadcut());
                copyfile.setOnFailed(event -> Tools.showerrordialog(Root, "Error", "Couldn't Copy File To " + selectedcutorelementname + "'s Ambience Directory", "Check File Permissions"));
                copyfile.start();
            } else {
                if (selected_temp_ambiencesong == null) {
                    Tools.showinformationdialog(Root, "Information", "Cannot Transfer", "Nothing Selected");
                } else {
                    Tools.showinformationdialog(Root, "Information", "Cannot Transfer", "No Cut Selected");
                }
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_actual_ambiencesong != null && selectedcutorelementname != null) {
                for (AmbienceSong i : Temp_Table.getItems()) {
                    if (selected_actual_ambiencesong.name.getValue().equals(i.name.getValue())) {
                        Tools.showinformationdialog(Root, "Information", "File Already Exists", "Select A Different File To Transfer");
                        return;
                    }
                }
                System.out.println(actual_ambiencesonglist.size());
                Service<Void> copyfile = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                File newfile = new File(tempdirectory, selected_actual_ambiencesong.name.getValue());
                                FileUtils.copyFile(selected_actual_ambiencesong.getFile(), newfile);
                                return null;
                            }
                        };
                    }
                };
                copyfile.setOnSucceeded(event -> {
                    File newfile = new File(tempdirectory, selected_actual_ambiencesong.name.getValue());
                    actual_ambiencesonglist.add(new AmbienceSong(newfile.getName(), newfile));
                    System.out.println(actual_ambiencesonglist.size());
                    Temp_Table.setItems(actual_ambiencesonglist);
                });
                copyfile.setOnFailed(event -> Tools.showerrordialog(Root, "Error", "Couldn't Copy File To Temp Directory", "Check File Permissions"));
                copyfile.start();
            }
        }

    // Temp Ambience Methods
        public void addtotempambience(ActionEvent actionEvent) {addto(Temp_Table, temp_ambiencesonglist);}
        public void removefromtempambience(ActionEvent actionEvent) {removefrom(Temp_Table);}
        public void previewtempambience(ActionEvent actionEvent) {preview(selected_temp_ambiencesong);}
        public void tempselectionchanged(AmbienceSong ambiencesong) {
            selected_temp_ambiencesong = ambiencesong;
        }

    // Actual Ambience Methods
        public void addtoactualambience(ActionEvent actionEvent) {addto(Actual_Table, actual_ambiencesonglist);}
        public void removeactualambience(ActionEvent actionEvent) {removefrom(Actual_Table);}
        public void previewactualambience(ActionEvent actionEvent) {preview(selected_actual_ambiencesong);}
        public void actualselectionchanged(AmbienceSong ambiencesong) {selected_actual_ambiencesong = ambiencesong;}

    // Table Methods
        public void selectandloadcut() {
            actual_ambiencesonglist.clear();
            Actual_Table.getItems().clear();
            int index = kujiin.xml.Options.CUTNAMES.indexOf(CutOrElementSelectionBox.getValue());
            selectedcutorelementname = kujiin.xml.Options.CUTNAMES.get(index);
            if (getcurrentambiencefiles()) {
                Actual_Table.setItems(actual_ambiencesonglist);
                CutSelectionLabel.setText(selectedcutorelementname + "'s Ambience");
            }
        }
        public void addto(TableView<AmbienceSong> table, ObservableList<AmbienceSong> songlist) {
            List<File> files = Tools.multipleopenfilechooser(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    if (Tools.validaudiofile(i) && Tools.getaudioduration(i) != 0.0) {
                        songlist.add(new AmbienceSong(i.getName(), i));}
                    else {notvalidfilenames.add(i);}
                }
                // TODO Show Dialog Here With Invalid File Names
            }
            if (songlist.size() > 0) {table.setItems(songlist);}
        }
        public void removefrom(TableView<AmbienceSong> table) {
            int index = table.getSelectionModel().getSelectedIndex();
            if (index != -1) {table.getItems().remove(index);}
            else {Tools.showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        public void preview(AmbienceSong selectedsong) {
            if (selectedsong != null && selectedsong.getFile() != null && selectedsong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(Root, selectedsong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }

        public boolean getcurrentambiencefiles() {
            if (selectedcutorelementname != null) {
                File thisdirectory = new File(kujiin.xml.Options.DIRECTORYAMBIENCE, selectedcutorelementname);
                try {
                    for (File i : thisdirectory.listFiles()) {
                        if (Tools.validaudiofile(i)) {
                            actual_ambiencesonglist.add(new AmbienceSong(i.getName(), i));}
                    }
                    return true;
                } catch (NullPointerException e) {
                    Tools.showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
                    return false;
                }
            } else {
                Tools.showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

        public void closebuttonpressed(ActionEvent actionEvent) {close();}
        @Override
        public void close() {
            try {FileUtils.cleanDirectory(tempdirectory);} catch (IOException ignored) {}
            super.close();
        }

        public void switchtosimple(ActionEvent actionEvent) {
            // TODO Check If Unsaved Changes Here?
            this.close();
            if (selected_temp_ambiencesong != null && kujiin.xml.Options.CUTNAMES.contains(selectedcutorelementname)) {
                new SimpleAmbienceEditor(Root, selectedcutorelementname).show();
            } else {new SimpleAmbienceEditor(Root).show();}
        }
    }
    public static class SimpleAmbienceEditor extends Stage implements Initializable {
        public TableView<AmbienceSong> AmbienceTable;
        public TableColumn<AmbienceSong, String> NameColumn;
        public TableColumn<AmbienceSong, String> DurationColumn;
        public ChoiceBox<String> CutOrElementChoiceBox;
        public Button SaveButton;
        public Button CloseButton;
        public Button AddButton;
        public Button RemoveButton;
        public Button PreviewButton;
        public TextField TotalDuration;
        public Button AdvancedButton;
        private MainController Root;
        private ObservableList<AmbienceSong> AmbienceList = FXCollections.observableArrayList();
        private AmbienceSong selectedambiencesong;
        private String selectedcutorelementname;
        private PreviewFile previewdialog;

        @Override
        public void initialize(URL location, ResourceBundle resources) {

        }

        public SimpleAmbienceEditor(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException ignored) {}
            CutOrElementChoiceBox.setOnAction(event -> selectandloadcut());
        }
        public SimpleAmbienceEditor(MainController root, String cutorelementname) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException ignored) {}
            setOnShowing(event -> {
                if (kujiin.xml.Options.CUTNAMES.contains(cutorelementname)) {
                    CutOrElementChoiceBox.getSelectionModel().select(cutorelementname);
                    selectandloadcut();
                }
            });
            CutOrElementChoiceBox.setOnAction(event -> selectandloadcut());
        }

    // Table Methods
        public void selectandloadcut() {
            AmbienceList.clear();
            AmbienceTable.getItems().clear();
            int index = kujiin.xml.Options.CUTNAMES.indexOf(CutOrElementChoiceBox.getValue());
            selectedcutorelementname = kujiin.xml.Options.CUTNAMES.get(index);
            if (getcurrentambiencefiles()) {
                AmbienceTable.setItems(AmbienceList);
            }
        }
        public void add() {
            List<File> filesselected = new FileChooser().showOpenMultipleDialog(Root.Scene.getWindow());
            List<File> notvalidfilenames = new ArrayList<>();
            if (filesselected == null || filesselected.size() == 0) {return;}
            for (File i : filesselected) {
                for (String x : Tools.SUPPORTEDAUDIOFORMATS) {
                    if (i.getName().endsWith(x)) {
                        if (Tools.getaudioduration(i) != 0.0) {AmbienceList.add(new AmbienceSong(i.getName(), i)); break;}
                    }
                }
                if (! i.equals(AmbienceList.get(AmbienceList.size() - 1).getFile())) {notvalidfilenames.add(i);}
            }
            if (notvalidfilenames.size() > 0) {
                Tools.showinformationdialog(Root, "Information", notvalidfilenames.size() + " Files Weren't Added Because They Are Unsupported", "");
            }
        }
        public void addfiles(ActionEvent actionEvent) {
            List<File> files = Tools.multipleopenfilechooser(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    if (Tools.validaudiofile(i) && Tools.getaudioduration(i) != 0.0) {
                        AmbienceList.add(new AmbienceSong(i.getName(), i));}
                    else {notvalidfilenames.add(i);}
                }
                // TODO Show Dialog Here With Invalid File Names
            }
            if (AmbienceList.size() > 0) {AmbienceTable.setItems(AmbienceList);}
        }
        public void remove(ActionEvent actionEvent) {
            int index = AmbienceTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {AmbienceTable.getItems().remove(index);}
            else {Tools.showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
        }
        public void preview(ActionEvent actionEvent) {
            if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
                if (previewdialog == null || !previewdialog.isShowing()) {
                    previewdialog = new PreviewFile(Root, selectedambiencesong.getFile());
                    previewdialog.showAndWait();
                }
            }
        }

        public boolean getcurrentambiencefiles() {
            if (selectedcutorelementname != null) {
                File thisdirectory = new File(kujiin.xml.Options.DIRECTORYAMBIENCE, selectedcutorelementname);
                try {
                    for (File i : thisdirectory.listFiles()) {
                        if (Tools.validaudiofile(i)) {
                            AmbienceList.add(new AmbienceSong(i.getName(), i));}
                    }
                    return true;
                } catch (NullPointerException e) {
                    Tools.showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
                    return false;
                }
            } else {
                Tools.showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

    // Dialog Methods
        public void advancedmode(ActionEvent actionEvent) {
            this.close();
            if (selectedcutorelementname != null && kujiin.xml.Options.CUTNAMES.contains(selectedcutorelementname)) {
                new AdvancedAmbienceEditor(Root, selectedcutorelementname).show();
            } else {new AdvancedAmbienceEditor(Root).show();}
        }
        public void save(ActionEvent actionEvent) {

        }
        public void closedialog(ActionEvent actionEvent) {
            // TODO Check For Unsaved Changes Here
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
            Filetopreview = filetopreview;
            if (Tools.validaudiofile(Filetopreview)) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(Root);
                } catch (IOException ignored) {}
            } else {Tools.showinformationdialog(Root, "Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
        }
        public PreviewFile(MainController root, Media mediatopreview) {
            Filetopreview = new File(mediatopreview.getSource());
            if (Tools.validaudiofile(Filetopreview)) {
                Root = root;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/PreviewAudioDialog.fxml"));
                fxmlLoader.setController(this);
                try {
                    Scene defaultscene = new Scene(fxmlLoader.load());
                    setScene(defaultscene);
                    Root.getOptions().setStyle(Root);
                } catch (IOException ignored) {}
                Mediatopreview = mediatopreview;
                PreviewPlayer = new MediaPlayer(Mediatopreview);
                TopLabel.setText("Loading");
                PreviewPlayer.setOnReady(() -> {
                    TopLabel.setText(Filetopreview.getName());
                    TotalTime.setText(Tools.formatlengthshort((int) PreviewPlayer.getTotalDuration().toSeconds()));
                    TotalTime.setText("00:00");
                });
            } else {Tools.showinformationdialog(Root, "Information", Filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
        }


        public void play(ActionEvent actionEvent) {
            if (PreviewPlayer != null && PreviewPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                PreviewPlayer.play();
                ProgressSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                    Duration seektothis = PreviewPlayer.getTotalDuration().multiply(ProgressSlider.getValue());
                    PreviewPlayer.seek(seektothis);
                });
                PreviewPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    CurrentTime.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
                    updatePositionSlider(PreviewPlayer.getCurrentTime());
                });
                VolumeSlider.valueChangingProperty().unbind();
                VolumeSlider.valueProperty().bindBidirectional(PreviewPlayer.volumeProperty());
                syncbuttons();
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
            if (PreviewPlayer.getStatus() == MediaPlayer.Status.PLAYING|| PreviewPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
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
                Root.getOptions().setStyle(Root);
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
    public static class ChangeProgramOptions extends Stage {
        public CheckBox TooltipsCheckBox;
        public CheckBox HelpDialogsCheckBox;
        public TextField AlertFileTextField;
        public Button AlertFileSelectButton;
        public TextField FadeInValue;
        public TextField FadeOutValue;
        public TextField EntrainmentVolumePercentage;
        public TextField AmbienceVolumePercentage;
        public ChoiceBox<String> ProgramThemeChoiceBox;
        public Button ApplyButton;
        public Button AcceptButton;
        public Button CancelButton;
        public Button DeleteAllGoalsButton;
        public Button DeleteAllSessionsProgressButton;
        public Button DefaultsButton;
        public ChoiceBox<String> RampDurationChoiceBox;
        public CheckBox AlertSwitch;
        public CheckBox ReferenceSwitch;
        public RadioButton ReferenceHTMLRadioButton;
        public RadioButton ReferenceTXTRadioButton;
        public CheckBox FullscreenCheckbox;
        public CheckBox RampSwitch;
        public Label ReferenceTopLabelDisplayType;
        public Label ReferenceTopLabelFullScreen;
        public Label RampTopLabel;
        private kujiin.xml.Options Options;
        private File AlertFile;
        private boolean valuechanged;
        private ObservableList<String> rampselections = FXCollections.observableArrayList(kujiin.xml.Options.RAMPDURATIONS);
        private MainController Root;
        private PlayerWidget.ReferenceType tempreferencetype;

        public ChangeProgramOptions(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeProgramOptions.fxml"));
            fxmlLoader.setController(this);
            Options = Root.getOptions();
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(Root);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Change Program Options");
            Tools.doubleTextField(FadeInValue, false);
            Tools.doubleTextField(FadeOutValue, false);
            Tools.doubleTextField(EntrainmentVolumePercentage, false);
            Tools.doubleTextField(AmbienceVolumePercentage, false);
            kujiin.xml.Options.STYLETHEMES.clear();
            try {
                for (File i : kujiin.xml.Options.DIRECTORYSTYLES.listFiles()) {
                    if (i.getName().endsWith(".css")) {kujiin.xml.Options.STYLETHEMES.add(i.getName().substring(0, i.getName().length() - 4));}
                }
                if (kujiin.xml.Options.STYLETHEMES.size() != 0) {
                    ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(kujiin.xml.Options.STYLETHEMES));
                }
            } catch (NullPointerException e) {ProgramThemeChoiceBox.getItems().clear(); ProgramThemeChoiceBox.setDisable(true);}
            TooltipsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            HelpDialogsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            FadeInValue.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            FadeOutValue.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            EntrainmentVolumePercentage.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            AmbienceVolumePercentage.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            ProgramThemeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                selectnewtheme();
                changedvalue();
            });
            AlertSwitch.setOnMouseClicked(event -> alertfiletoggle());
            AlertSwitch.setOnAction(Root.CHECKBOXONOFFLISTENER);
            ReferenceSwitch.setOnMouseClicked(event -> referencetoggle());
            ReferenceSwitch.setOnAction(Root.CHECKBOXONOFFLISTENER);
            RampSwitch.setOnMouseClicked(event -> ramptoggle());
            RampSwitch.setOnAction(Root.CHECKBOXONOFFLISTENER);
            ReferenceHTMLRadioButton.setOnAction(event1 -> HTMLTypeSelected());
            ReferenceTXTRadioButton.setOnAction(event1 -> TXTTypeSelected());
            FullscreenCheckbox.setOnAction(Root.CHECKBOXYESNOLISTENER);
            FullscreenCheckbox.setOnMouseClicked(event -> setFullscreenOption());
            AlertFileTextField.setEditable(false);
            AlertFileTextField.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
            loadoptionsfromxml();
            checkalertfile();
            referencetoggle();
            ramptoggle();
        }

    // Alert File Methods
        public void alertfiletoggle() {
            if (AlertSwitch.isSelected()) {
                if (Options.getSessionOptions().getAlertfilelocation() == null) {
                    AlertFile = getnewalertfile();
                    checkalertfile();
                }
            } else {
                if (Tools.getanswerdialog(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
                    AlertFile = null;
                    checkalertfile();
                } else {
                    AlertSwitch.setSelected(true);
                    alertfiletoggle();
                }
            }
        }
        public File getnewalertfile() {
            File newfile = Tools.singleopenfilechooser(getScene(), "Select A New Alert File", null);
            if (newfile != null) {
                if (Tools.validaudiofile(newfile)) {
                    double duration = Tools.getaudioduration(newfile);
                    if (duration > 10000) {
                        if (!Tools.getanswerdialog(Root, "Validation", "Alert File Is longer Than 10 Seconds",
                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                                        "Really Use It?", duration))) {newfile = null;}
                    }
                } else {
                    Tools.showinformationdialog(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Tools.supportedaudiotext());
                    newfile = null;
                }
            }
            return newfile;
        }
        public boolean checkalertfile() {
            boolean good;
            if (AlertFile != null && Tools.validaudiofile(AlertFile)) {
                good = true;
                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
                String audioduration = Tools.formatlengthshort((int) Tools.getaudioduration(AlertFile));
                AlertFileTextField.setText(String.format("%s (%s)", AlertFile.getName(), audioduration));
            } else {
                good = false;
                AlertFileTextField.setText("Alert Feature Disabled");
                Options.getSessionOptions().setAlertfilelocation(null);
            }
            Options.getSessionOptions().setAlertfunction(good);
            AlertFileSelectButton.setDisable(! good);
            AlertFileTextField.setDisable(! good);
            AlertSwitch.setSelected(good);
            return good;
        }
        public void openandtestnewfile(ActionEvent actionEvent) {
            AlertFile = getnewalertfile();
            checkalertfile();
        }

    // Reference Methods
        public void referencetoggle() {
            boolean enabled = ReferenceSwitch.isSelected();
            if (enabled) {ReferenceSwitch.setText("ON");}
            ReferenceHTMLRadioButton.setDisable(! enabled);
            ReferenceTXTRadioButton.setDisable(! enabled);
            FullscreenCheckbox.setDisable(! enabled);
            ReferenceTopLabelDisplayType.setDisable(! enabled);
            ReferenceTopLabelFullScreen.setDisable(! enabled);
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
            tempreferencetype = PlayerWidget.ReferenceType.html;
        }
        public void TXTTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(false);
            ReferenceTXTRadioButton.setSelected(true);
            tempreferencetype = PlayerWidget.ReferenceType.txt;
        }

    // Ramp Methods
        public void ramptoggle() {
            boolean enabled = RampSwitch.isSelected();
            if (enabled) {RampSwitch.setText("ON");}
            RampDurationChoiceBox.setDisable(! enabled);
            RampTopLabel.setDisable(! enabled);
        }

    // Button Actions
        public void selectnewtheme() {
            if (ProgramThemeChoiceBox.getSelectionModel().getSelectedIndex() != -1) {
                File cssfile = new File(kujiin.xml.Options.DIRECTORYSTYLES, ProgramThemeChoiceBox.getValue() + ".css");
                if (cssfile.exists()) {
                    Options.getAppearanceOptions().setThemefile(cssfile.toURI().toString());
                    getScene().getStylesheets().clear();
                    getScene().getStylesheets().add(Options.getAppearanceOptions().getThemefile());
                }
            }
        }
        public void loadoptionsfromxml() {
            TooltipsCheckBox.setSelected(Options.getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(Options.getProgramOptions().getHelpdialogs());
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            RampDurationChoiceBox.setItems(rampselections);
            if (Options.getSessionOptions().getRampduration() == 2) {RampDurationChoiceBox.getSelectionModel().select(0);}
            else if (Options.getSessionOptions().getRampduration() == 3) {RampDurationChoiceBox.getSelectionModel().select(1);}
            else if (Options.getSessionOptions().getRampduration() == 4) {RampDurationChoiceBox.getSelectionModel().select(2);}
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            EntrainmentVolumePercentage.setText(String.format("%.1f", Options.getSessionOptions().getEntrainmentvolume() * 100));
            AmbienceVolumePercentage.setText(String.format("%.1f", Options.getSessionOptions().getAmbiencevolume() * 100));
            AlertSwitch.setSelected(Options.getSessionOptions().getAlertfunction());
            try {AlertFile = new File(Options.getSessionOptions().getAlertfilelocation());}
            catch (NullPointerException ignored) {AlertFile = null;}
            checkalertfile();
        }
        public boolean apply(ActionEvent actionEvent) {
            try {
                if (checkvalues()) {
                    Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
                    Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
                    Options.getSessionOptions().setRampenabled(RampSwitch.isSelected());
                    if (RampSwitch.isSelected()) {
                        int index = RampDurationChoiceBox.getSelectionModel().getSelectedIndex();
                        if (index == 0) {Options.getSessionOptions().setRampduration(2);}
                        else if (index == 1) {Options.getSessionOptions().setRampduration(3);}
                        else if (index == 2) {Options.getSessionOptions().setRampduration(5);}
                    } else {Options.getSessionOptions().setRampduration(null);}
                    Options.getSessionOptions().setFadeoutduration(new Double(FadeInValue.getText()));
                    Options.getSessionOptions().setFadeinduration(new Double(FadeOutValue.getText()));
                    Options.getSessionOptions().setReferenceoption(ReferenceSwitch.isSelected());
                    Options.getSessionOptions().setReferencetype(tempreferencetype);
                    Options.getSessionOptions().setReferencefullscreen(FullscreenCheckbox.isSelected());
                    Options.getSessionOptions().setAlertfunction(AlertSwitch.isSelected());
                    if (AlertFile != null) {Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());}
                    else {Options.getSessionOptions().setAlertfilelocation(null);}
                    Options.marshall();
                    valuechanged = false;
                    ApplyButton.setDisable(true);
                    return true;
                } else {return false;}
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public void accept(ActionEvent actionEvent) {
            apply(null);
            close();
        }
        public void cancel(ActionEvent actionEvent) {
            close();
        }
        public void changedvalue() {
            ApplyButton.setDisable(false);
            valuechanged = true;
        }
        public boolean checkvalues() {
            Double entrainmentvolume = new Double(EntrainmentVolumePercentage.getText()) / 100;
            Double ambiencevolume = new Double(AmbienceVolumePercentage.getText()) / 100;
            boolean entrainmentgood = entrainmentvolume <= 100.0 && entrainmentvolume > 0.0;
            boolean ambiencegood = ambiencevolume <= 100.0 && ambiencevolume > 0.0;
            Tools.validate(EntrainmentVolumePercentage, entrainmentgood);
            Tools.validate(AmbienceVolumePercentage, ambiencegood);
            boolean alertfilegood = checkalertfile();
            if (AlertSwitch.isSelected()) {Tools.validate(AlertFileTextField, alertfilegood);}
            return entrainmentgood && ambiencegood && alertfilegood;
        }
        public void resettodefaults(ActionEvent actionEvent) {
            Options.resettodefaults();
            loadoptionsfromxml();
            valuechanged = true;
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (Tools.getanswerdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (Tools.getanswerdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }

    // Dialog Methods
        @Override
        public void close() {
            if (valuechanged) {
                if (! Tools.getanswerdialog(Root, "Confirmation", "You Have Unsaved Changes", "Exit Without Saving?")) {return;}
            }
            super.close();
        }
    }
    public static class AmbienceSong {
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
