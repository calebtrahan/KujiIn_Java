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
import kujiin.xml.Ambience;
import kujiin.xml.Ambiences;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;
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
    public Button ResetCreatorButton;
    private Scene Scene;
    private Stage Stage;
    private This_Session Session;
    private CreatorAndExporterWidget CreatorAndExporter;
    private PlayerWidget Player;
    private ProgressAndGoalsWidget ProgressTracker;
    private Options Options;

// Event Handlers
    public final EventHandler<KeyEvent> NONEDITABLETEXTFIELD = event -> Tools.gui_showinformationdialog(this, "Not Editable", "Non-Editable Text Field", "This Text Field Can't Be Edited");
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
        getSession().getAmbiences().marshall();
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
        getStage().setIconified(true);
        AdvancedAmbienceEditor sae = new AdvancedAmbienceEditor(this, getSession().getAmbiences());
        sae.showAndWait();
        getStage().setIconified(false);
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        EditReferenceFiles a = new EditReferenceFiles(this);
        a.showAndWait();
    }
    public void howtouseprogram(ActionEvent actionEvent) {Tools.menu_howtouse(this);}
    public void aboutthisprogram(ActionEvent actionEvent) {Tools.menu_aboutthisprogram();}
    public void contactme(ActionEvent actionEvent) {Tools.menu_contactme();}
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
        Tools.gui_showtimedmessageonlabel(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
    }
    public void ambienceswitch(ActionEvent actionEvent) {
        CreatorAndExporter.checkambience();}
    public void resetallcreatorvalues(ActionEvent actionEvent) {
        Session.resetcreateditems();
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
        private ObservableList<String> cutorelementnames = FXCollections.observableArrayList();
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
                Root.getOptions().setStyle(this);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Reference Files Editor");
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            variations = FXCollections.observableArrayList();
            cutorelementnames.addAll(kujiin.xml.Options.ALLNAMES);
            variations.addAll(Arrays.asList("html", "txt"));
            CutNamesChoiceBox.setItems(cutorelementnames);
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
                Tools.gui_showtimedmessageonlabel(StatusBar, "Select A Cut And Variation And Press 'Load' First", 3000);
            }
        }

    // File Methods
        public void savefile(ActionEvent actionEvent) {
            if (Tools.file_writecontents(selectedfile, Tools.file_getcontents(selectedfile))) {
                String text = selectedcut + "'s Reference File (" + selectedvariation + " Variation) Has Been Saved";
                Tools.gui_showtimedmessageonlabel(StatusBar, text, 5000);
            } else {
                Tools.gui_showerrordialog(Root, "Error", "Couldn't Save", "Does The File Exist/Do You Have Access To It?");}
        }
        public void loadnewfile(ActionEvent actionEvent) {
            if (! CutNamesChoiceBox.getValue().equals("") && ! CutVariationsChoiceBox.getValue().equals("")) {
                if (unsavedchanges()) {
                    if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "Document Has Unsaved Changes", "Save These Changes Before Loading A Different File?")) {savefile(null);}
                    else {return;}
                }
                selectedcut = CutNamesChoiceBox.getValue();
                selectedvariation = CutVariationsChoiceBox.getValue();
                SelectReferenceFileLabel.setText(String.format("%s's Reference File (%s Variation)", selectedcut, selectedvariation));
                if (selectedvariation.equals("html")) {selectedfile = new File(htmldirectory, selectedcut + ".html");}
                else {selectedfile = new File(txtdirectory, selectedcut + ".txt");}
                MainTextArea.setText(Tools.file_getcontents(selectedfile));
            } else {
                if (CutNamesChoiceBox.getValue().equals("")) {
                    Tools.gui_showinformationdialog(Root, "Information", "No Cut Selected", "Select A Cut To Load");}
                else {
                    Tools.gui_showinformationdialog(Root, "Information", "No Variation Selected", "Select A Variation To Load");}
                SelectReferenceFileLabel.setText("Select A Cut Name And Variation And Press 'Load'");
            }
        }

        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && selectedvariation != null) {
                if (selectedvariation.equals("html")) {
                    PlayerWidget.DisplayReference dr = new PlayerWidget.DisplayReference(Root, MainTextArea.getText());
                    dr.showAndWait();
                } else {
                    Tools.gui_showinformationdialog(Root, "Information", "Preview Is For Html Content Not Available For Text Only", "Cannot Open Preview");
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
        private ArrayList<SoundFile> actual_soundfilelist;
        private ObservableList<AmbienceSong> temp_ambiencesonglist = FXCollections.observableArrayList();
        private ArrayList<SoundFile> temp_soundfilelist;
        private AmbienceSong selected_temp_ambiencesong;
        private AmbienceSong selected_actual_ambiencesong;
        private double temptotalduration;
        private double actualtotalduration;
        private String selectedcutorelementname;
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
            CutOrElementSelectionBox.setItems(allnames);
            this.setOnCloseRequest(event -> close());
        }

        public AdvancedAmbienceEditor(MainController root, Ambiences ambiences) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/AdvancedAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            CutOrElementSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(MainController root, Ambiences ambiences, String cutname) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AdvancedAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            CutOrElementSelectionBox.setOnAction(event -> selectandloadcut());
            CutOrElementSelectionBox.getSelectionModel().select(kujiin.xml.Options.ALLNAMES.indexOf(cutname));
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
                copyfile.setOnFailed(event -> Tools.gui_showerrordialog(Root, "Error", "Couldn't Copy File To " + selectedcutorelementname + "'s Ambience Directory", "Check File Permissions"));
                copyfile.start();
            } else {
                if (selected_temp_ambiencesong == null) {
                    Tools.gui_showinformationdialog(Root, "Information", "Cannot Transfer", "Nothing Selected");
                } else {
                    Tools.gui_showinformationdialog(Root, "Information", "Cannot Transfer", "No Cut Selected");
                }
            }
        }
        public void leftarrowpressed(ActionEvent actionEvent) {
            if (selected_actual_ambiencesong != null && selectedcutorelementname != null) {
                File newtempfile = new File(tempdirectory, selected_actual_ambiencesong.name.getValue());
                for (AmbienceSong i : Temp_Table.getItems()) {
                    if (selected_actual_ambiencesong.name.getValue().equals(i.name.getValue())) {
                        Tools.gui_showinformationdialog(Root, "Information", "File Already Exists", "Select A Different File To Transfer");
                        return;
                    }
                }
                Service<Void> copyfile = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                FileUtils.copyFile(selected_actual_ambiencesong.getFile(), newtempfile);
                                return null;
                            }
                        };
                    }
                };
                copyfile.setOnSucceeded(event -> {
                    temp_ambiencesonglist.add(new AmbienceSong(new SoundFile(newtempfile)));
                    Temp_Table.setItems(temp_ambiencesonglist);
                });
                copyfile.setOnFailed(event -> Tools.gui_showerrordialog(Root, "Error", "Couldn't Copy File To Temp Directory", "Check File Permissions"));
                copyfile.start();
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
            Temp_TotalDuration.setText(Tools.format_minstohrsandmins_long((int) ((temptotalduration / 1000) / 60)));
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
            Actual_TotalDuration.setText(Tools.format_minstohrsandmins_long((int) ((actualtotalduration / 1000) / 60)));
        }

    // Table Methods
        public void selectandloadcut() {
            int index = CutOrElementSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                selectedambience = ambiences.getcutorelementsAmbience(index);
                if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
                else {actual_ambiencesonglist.clear();}
                if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
                else {actual_soundfilelist.clear();}
                Actual_Table.getItems().clear();
                selectedcutorelementname = kujiin.xml.Options.ALLNAMES.get(index);
                if (populateactualambiencetable()) {
                    Actual_Table.setItems(actual_ambiencesonglist);
                    CutSelectionLabel.setText(selectedcutorelementname + "'s Ambience");
                }
                calculateactualtotalduration();
            }
        }
        private void addto(TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
            List<File> files = Tools.filechooser_multiple(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    SoundFile soundFile = new SoundFile(i);
                    if (soundFile.isValid()) {
                        addandcalculateduration(soundFile, table, soundfilelist, songlist);
                    } else {notvalidfilenames.add(i);}
                }
                if (notvalidfilenames.size() > 0) {Tools.gui_showinformationdialog(Root, "Files Couldn't Be Added", "These Files Couldn't Be Added", notvalidfilenames.toString());}
                // TODO Show Dialog Here With Invalid File Names
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
                if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                table.getItems().remove(index);
                soundfilelist.remove(index);
                songlist.remove(index);
                calculateactualtotalduration();
                calculatetemptotalduration();
            }
            else {Tools.gui_showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
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
            if (selectedcutorelementname != null) {
                try {
                    if (selectedambience.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedambience.getAmbience()) {
                        actual_soundfilelist.add(i);
                        actual_ambiencesonglist.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.gui_showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
                    return false;
                }
            } else {
                Tools.gui_showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

    // Dialog Methods
        public void save(ActionEvent actionEvent) {
            int index = CutOrElementSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : actual_soundfilelist) {
                    if (! selectedambience.ambienceexistsinActual(i)) {selectedambience.actual_add(i);}
                }
                ambiences.setcutorelementsAmbience(index, selectedambience);
                ambiences.marshall();
                Tools.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedcutorelementname, "");
            } else {Tools.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
        }
        public void closebuttonpressed(ActionEvent actionEvent) {
            try {FileUtils.cleanDirectory(tempdirectory);} catch (IOException ignored) {}
            close();
        }
        public void switchtosimple(ActionEvent actionEvent) {
            // TODO Check If Unsaved Changes Here?
            this.close();
            if (selected_temp_ambiencesong != null && kujiin.xml.Options.ALLNAMES.contains(selectedcutorelementname)) {
                new SimpleAmbienceEditor(Root, Root.getSession().getAmbiences(), selectedcutorelementname).show();
            } else {new SimpleAmbienceEditor(Root, Root.getSession().getAmbiences()).show();}
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
        private ObservableList<AmbienceSong> AmbienceList;
        private ArrayList<SoundFile> SoundList;
        private AmbienceSong selectedambiencesong;
        private String selectedcutorelementname;
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
            CutOrElementChoiceBox.setItems(allnames);
        }

        public SimpleAmbienceEditor(MainController root, Ambiences ambiences) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            CutOrElementChoiceBox.setOnAction(event -> selectandloadcut());
        }
        public SimpleAmbienceEditor(MainController root, Ambiences ambiences, String cutorelementname) {
            Root = root;
            this.ambiences = ambiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SimpleAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException ignored) {}
            setOnShowing(event -> {
                if (kujiin.xml.Options.ALLNAMES.contains(cutorelementname)) {
                    CutOrElementChoiceBox.getSelectionModel().select(cutorelementname);
                    selectandloadcut();
                }
            });
            CutOrElementChoiceBox.setOnAction(event -> selectandloadcut());
        }

    // Table Methods
        public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
        public void selectandloadcut() {
            int index = CutOrElementChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                selectedambience = ambiences.getcutorelementsAmbience(index);
                if (AmbienceList == null) {AmbienceList = FXCollections.observableArrayList();}
                else {AmbienceList.clear();}
                if (SoundList == null) {SoundList = new ArrayList<>();}
                else {SoundList.clear();}
                AmbienceTable.getItems().clear();
                selectedcutorelementname = kujiin.xml.Options.ALLNAMES.get(index);
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
                for (String x : Tools.SUPPORTEDAUDIOFORMATS) {
                    if (i.getName().endsWith(x)) {
                        if (Tools.audio_getduration(i) != 0.0) {AmbienceList.add(new AmbienceSong(new SoundFile(i))); break;}
                    }
                }
                if (! i.equals(AmbienceList.get(AmbienceList.size() - 1).getFile())) {notvalidfilenames.add(i);}
            }
            if (notvalidfilenames.size() > 0) {
                Tools.gui_showinformationdialog(Root, "Information", notvalidfilenames.size() + " Files Weren't Added Because They Are Unsupported", "");
            }
        }
        public void addfiles(ActionEvent actionEvent) {
            List<File> files = Tools.filechooser_multiple(getScene(), "Add Files", null);
            ArrayList<File> notvalidfilenames = new ArrayList<>();
            if (files != null) {
                for (File i : files) {
                    SoundFile soundFile = new SoundFile(i);
                    if (soundFile.isValid()) {
                        addandcalculateduration(soundFile);
                    }
                    else {notvalidfilenames.add(i);}
                }
                // TODO Show Dialog Here With Invalid File Names
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
                if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "Also Delete File " + soundFile.getName() + " From Hard Drive?", "This Cannot Be Undone")) {
                    soundFile.getFile().delete();
                }
                AmbienceTable.getItems().remove(index);
                AmbienceList.remove(index);
                SoundList.remove(index);
                calculatetotalduration();
            }
            else {Tools.gui_showinformationdialog(Root, "Information", "Nothing Selected", "Select A Table Item To Remove");}
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
            if (selectedcutorelementname != null) {
                try {
                    if (selectedambience.getAmbience() == null) {return false;}
                    for (SoundFile i : selectedambience.getAmbience()) {
                        SoundList.add(i);
                        AmbienceList.add(new AmbienceSong(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.gui_showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
                    return false;
                }
            } else {
                Tools.gui_showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }
        public void calculatetotalduration() {
            totalselectedduration = 0.0;
            for (AmbienceSong i : AmbienceTable.getItems()) {
                totalselectedduration += i.getDuration();
            }
            TotalDuration.setText(Tools.format_minstohrsandmins_long((int) ((totalselectedduration / 1000) / 60)));
        }

    // Dialog Methods
        public void advancedmode(ActionEvent actionEvent) {
            this.close();
            if (selectedcutorelementname != null && kujiin.xml.Options.ALLNAMES.contains(selectedcutorelementname)) {
                new AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences(), selectedcutorelementname).show();
            } else {new AdvancedAmbienceEditor(Root, Root.getSession().getAmbiences()).show();}
        }
        public void save(ActionEvent actionEvent) {
            int index = CutOrElementChoiceBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : SoundList) {
                    if (! selectedambience.ambienceexistsinActual(i)) {selectedambience.actual_add(i);}
                }
                ambiences.setcutorelementsAmbience(index, selectedambience);
                ambiences.marshall();
                Tools.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedcutorelementname, "");
            } else {Tools.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
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
            if (Tools.audio_isValid(filetopreview)) {
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
                        CurrentTime.setText(Tools.format_secondsforplayerdisplay(0));
                        TotalTime.setText(Tools.format_secondsforplayerdisplay((int) PreviewPlayer.getTotalDuration().toSeconds()));
                        PlayButton.setDisable(false);
                        PauseButton.setDisable(false);
                        StopButton.setDisable(false);
                    });
                    VolumeSlider.setValue(0.0);
                    VolumePercentage.setText("0%");
                } catch (IOException ignored) {}
            } else {Tools.gui_showinformationdialog(Root, "Information", filetopreview.getName() + " Is Not A Valid Audio File", "Cannot Preview");}
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
                    CurrentTime.setText(Tools.format_secondsforplayerdisplay((int) newValue.toSeconds()));
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
                Root.getOptions().setStyle(this);
                this.setResizable(false);
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Change Program Options");
            doubleTextField(FadeInValue, false);
            doubleTextField(FadeOutValue, false);
            doubleTextField(EntrainmentVolumePercentage, false);
            doubleTextField(AmbienceVolumePercentage, false);
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
                if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
                    AlertFile = null;
                    checkalertfile();
                } else {
                    AlertSwitch.setSelected(true);
                    alertfiletoggle();
                }
            }
        }
        public File getnewalertfile() {
            File newfile = Tools.filechooser_single(getScene(), "Select A New Alert File", null);
            if (newfile != null) {
                if (Tools.audio_isValid(newfile)) {
                    double duration = Tools.audio_getduration(newfile);
                    if (duration > 10000) {
                        if (!Tools.gui_getconfirmationdialog(Root, "Validation", "Alert File Is longer Than 10 Seconds",
                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                                        "Really Use It?", duration))) {newfile = null;}
                    }
                } else {
                    Tools.gui_showinformationdialog(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Tools.audio_getsupportedText());
                    newfile = null;
                }
            }
            return newfile;
        }
        public boolean checkalertfile() {
            boolean good;
            if (AlertFile != null && Tools.audio_isValid(AlertFile)) {
                good = true;
                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
                String audioduration = Tools.format_secondsforplayerdisplay((int) Tools.audio_getduration(AlertFile));
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
            Tools.gui_validate(EntrainmentVolumePercentage, entrainmentgood);
            Tools.gui_validate(AmbienceVolumePercentage, ambiencegood);
            boolean alertfilegood = checkalertfile();
            if (AlertSwitch.isSelected()) {Tools.gui_validate(AlertFileTextField, alertfilegood);}
            return entrainmentgood && ambiencegood && alertfilegood;
        }
        public void resettodefaults(ActionEvent actionEvent) {
            Options.resettodefaults();
            loadoptionsfromxml();
            valuechanged = true;
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.gui_showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.gui_showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (Tools.gui_getconfirmationdialog(Root, "Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.gui_showerrordialog(Root, "Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.gui_showinformationdialog(Root, "Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }

    // Dialog Methods
        public void doubleTextField(TextField txtfield, boolean setvalueatzero) {
            txtfield.textProperty().addListener((observable, oldValue, newValue) -> {
                try {if (newValue.matches("\\d+\\.\\d+")) {Double.parseDouble(newValue);}  else {txtfield.setText(oldValue);}}
                catch (Exception e) {txtfield.setText("");}});
            if (setvalueatzero) {txtfield.setText("0.00");}
        }
        @Override
        public void close() {
            if (valuechanged) {
                if (! Tools.gui_getconfirmationdialog(Root, "Confirmation", "You Have Unsaved Changes", "Exit Without Saving?")) {return;}
            }
            super.close();
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
            this.length = new SimpleStringProperty(Tools.format_secondsforplayerdisplay((int) (duration / 1000)));
        }

        public String getName() {
            return name.getValue();
        }
        public File getFile() {
            return file;
        }
        public double getDuration() {return duration;}
    }
}
