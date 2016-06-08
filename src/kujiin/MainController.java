package kujiin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import kujiin.ui.CreatorAndExporterUI;
import kujiin.ui.PlayerUI;
import kujiin.ui.ProgressAndGoalsUI;
import kujiin.util.This_Session;
import kujiin.util.Util;
import kujiin.xml.Ambience;
import kujiin.xml.Ambiences;
import kujiin.xml.Options;
import kujiin.xml.SoundFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
// TODO Saving Preset Is Broke!
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
    private CreatorAndExporterUI CreatorAndExporter;
    private PlayerUI Player;
    private ProgressAndGoalsUI ProgressTracker;
    private Options Options;

// Event Handlers
    public final EventHandler<KeyEvent> NONEDITABLETEXTFIELD = event -> Util.gui_showinformationdialog(this, "Not Editable", "Non-Editable Text Field", "This Text Field Can't Be Edited");
    public final EventHandler<ActionEvent> CHECKBOXONOFFLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("ON");} else {a.setText("OFF");}};
    public final EventHandler<ActionEvent> CHECKBOXYESNOLISTENER = event -> {CheckBox a = (CheckBox) event.getSource(); if (a.isSelected()) {a.setText("YES");} else {a.setText("NO");}};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setOptions(new Options(this));
        getOptions().unmarshall();
        setProgressTracker(new ProgressAndGoalsUI(this));
        setSession(new This_Session(this));
        setCreatorAndExporter(new CreatorAndExporterUI(this));
        CreatorStatusBar.setText("");
    }
    public boolean cleanup() {
        getSession().getAmbiences().marshall();
        getSession().getEntrainments().marshall();
        getOptions().marshall();
        return getCreatorAndExporter().cleanup() && getProgressTracker().cleanup();
    }

// Getters And Setters
    public This_Session getSession() {
        return Session;
    }
    public void setSession(This_Session session) {
        this.Session = session;
    }
    public CreatorAndExporterUI getCreatorAndExporter() {
        return CreatorAndExporter;
    }
    public void setCreatorAndExporter(CreatorAndExporterUI creatorAndExporter) {
        this.CreatorAndExporter = creatorAndExporter;
    }
    public PlayerUI getPlayer() {
        return Player;
    }
    public void setPlayer(PlayerUI player) {
        this.Player = player;
    }
    public ProgressAndGoalsUI getProgressTracker() {
        return ProgressTracker;
    }
    public void setProgressTracker(ProgressAndGoalsUI progressTracker) {
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
    getProgressTracker().updaterootgoalsui();
    getProgressTracker().updateprogressui();
}
    public void editprogramsambience(ActionEvent actionEvent) {
        getStage().setIconified(true);
        AdvancedAmbienceEditor sae = new AdvancedAmbienceEditor(this, getSession().getAmbiences());
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
        Util.menu_howtouse(this);}
    public void aboutthisprogram(ActionEvent actionEvent) {
        Util.menu_aboutthisprogram();}
    public void contactme(ActionEvent actionEvent) {
        Util.menu_contactme();}
    public void close(ActionEvent actionEvent) {
        if (cleanup()) {System.exit(0);}
    }

// Total Progress And Goals UI
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

// Creator And Exporter UI
    public void loadpreset(ActionEvent actionEvent) {CreatorAndExporter.loadpreset();}
    public void savepreset(ActionEvent actionEvent) {CreatorAndExporter.savepreset();}
    public void toggleexporter(ActionEvent actionEvent) {
        getCreatorAndExporter().toggleexport();
    }
    public void exportsession(Event event) {
        //        CreatorAndExporter.startexport();}
        Util.gui_showtimedmessageonlabel(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
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
        getCreatorAndExporter().createsession();
        if (getCreatorAndExporter().getCreatorState() == CreatorAndExporterUI.CreatorState.CREATED) {
            if (getPlayer() != null && getPlayer().isShowing()) {return;}
            getStage().setIconified(true);
            setPlayer(new PlayerUI(this));
            getPlayer().showAndWait();
            getStage().setIconified(false);
        }
    }

// Dialogs
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
        private String selectedcutorelement;
        private MainController Root;
        private PlayerUI.ReferenceType referenceType;
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
            ObservableList<String> cutorelementnames = FXCollections.observableArrayList();
            cutorelementnames.addAll(kujiin.xml.Options.ALLNAMES);
            userselectedindexes = new ArrayList<>();
            CutNamesChoiceBox.setItems(cutorelementnames);
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            CutNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
            HTMLVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(CutNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            setReferenceType(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION);
            HTMLVariation.setSelected(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION == PlayerUI.ReferenceType.html);
            TEXTVariation.setSelected(kujiin.xml.Options.DEFAULT_REFERENCE_TYPE_OPTION == PlayerUI.ReferenceType.txt);
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
        public PlayerUI.ReferenceType getReferenceType() {
            return referenceType;
        }
        public void setReferenceType(PlayerUI.ReferenceType referenceType) {
            this.referenceType = referenceType;
        }

    // Text Area Methods
        private boolean unsavedchanges() {
            try {
                return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
            } catch (Exception e) {e.printStackTrace(); return false;}
        }
        public void newcutorelementselected(ActionEvent actionEvent) {
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
            if (getReferenceType() != null && selectedcutorelement != null && selectedfile != null) {
                boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
                PreviewButton.setDisable(! hasvalidtext || getReferenceType() == PlayerUI.ReferenceType.txt);
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
                String text = selectedcutorelement + "'s Reference File (" + getReferenceType().toString() + " Variation) Has Been Saved";
                Util.gui_showinformationdialog(Root, "Changes Saved", text, "");
            } else {Util.gui_showerrordialog(Root, "Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
        }
        public void loadselectedfile() {
            if (CutNamesChoiceBox.getSelectionModel().getSelectedIndex() != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
                selectedcutorelement = CutNamesChoiceBox.getSelectionModel().getSelectedItem();
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
            if (getReferenceType() == null || selectedcutorelement == null) {selectedfile = null; return;}
            switch (getReferenceType()) {
                case html:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "html"), selectedcutorelement + ".html");
                    if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(Root, e);}}
                    break;
                case txt:
                    selectedfile = new File(new File(kujiin.xml.Options.DIRECTORYREFERENCE, "txt"), selectedcutorelement + ".txt");
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
            setReferenceType(PlayerUI.ReferenceType.html);
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
            setReferenceType(PlayerUI.ReferenceType.txt);
            selectnewfile();
            loadselectedfile();
        }
        public void preview(ActionEvent actionEvent) {
            if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && getReferenceType() == PlayerUI.ReferenceType.html) {
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
                this.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {

                    }
                });
            } catch (IOException e) {new ExceptionDialog(Root, e).showAndWait();}
            setTitle("Advanced Ambience Editor");
            CutOrElementSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public AdvancedAmbienceEditor(MainController root, Ambiences ambiences, String cutname) {
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
            CutOrElementSelectionBox.getSelectionModel().select(kujiin.xml.Options.ALLNAMES.indexOf(cutname));
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }

    // Transfer Methods
        // TODO Add Check Duplicates Before Moving Over (Or Ask Allow Duplicates?)
        public void rightarrowpressed(ActionEvent actionEvent) {
            // Transfer To Current Cut (use Task)
            if (selected_temp_ambiencesong != null && selectedcutorelementname != null) {
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
            if (selected_actual_ambiencesong != null && selectedcutorelementname != null) {
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
            String longtext = Util.format_minstohrsandmins_long((int) ((temptotalduration / 1000) / 60));
            if (longtext.length() < 20) {Temp_TotalDuration.setText(longtext);}
            else {Temp_TotalDuration.setText(Util.format_minstohrsandmins_short((int) ((temptotalduration / 1000) / 60)));}
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
            String longtext = Util.format_minstohrsandmins_long((int) ((actualtotalduration / 1000) / 60));
            if (longtext.length() < 20) {Actual_TotalDuration.setText(longtext);}
            else {Actual_TotalDuration.setText(Util.format_minstohrsandmins_short((int) ((actualtotalduration / 1000) / 60)));}
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
                    Util.gui_showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
                    return false;
                }
            } else {
                Util.gui_showinformationdialog(Root, "Information", "No Cut Loaded", "Load A Cut's Ambience First");
                return false;
            }
        }

    // Dialog Methods
        public boolean unsavedchanges() {
            if (CutOrElementSelectionBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
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
            int index = CutOrElementSelectionBox.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                for (SoundFile i : actual_soundfilelist) {
                    if (! selectedambience.ambienceexistsinActual(i)) {selectedambience.actual_add(i);}
                }
                ambiences.setcutorelementsAmbience(index, selectedambience);
                ambiences.marshall();
                Util.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedcutorelementname, "");
            } else {
                Util.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
        }
        public void closebuttonpressed(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedcutorelementname, "Save Changes Before Closing?")) {save(null);}
                else {return;}
            }
            close();
        }
        public void switchtosimple(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedcutorelementname, "Save Changes Before Switching To Simple Mode?")) {save(null);}
            }
            this.close();
            deletetempambiencefromdirectory();
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
                setTitle("Simple Ambience Editor");
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
                setTitle("Simple Ambience Editor");
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
                    Util.gui_showinformationdialog(Root, "Information", selectedcutorelementname + " Has No Ambience", "Please Add Ambience To " + selectedcutorelementname);
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
            TotalDuration.setText(Util.format_minstohrsandmins_long((int) ((totalselectedduration / 1000) / 60)));
        }
        public boolean unsavedchanges() {
            if (CutOrElementChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
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
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedcutorelementname, "Save Changes Before Switching To Advanced Mode?")) {save(null);}
            }
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
                Util.gui_showinformationdialog(Root, "Saved", "Ambience Saved To " + selectedcutorelementname, "");
            } else {
                Util.gui_showinformationdialog(Root, "Cannot Save", "No Cut Or Element Selected", "Cannot Save");}
        }
        public void closedialog(ActionEvent actionEvent) {
            if (unsavedchanges()) {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Save Changes", "You Have Unsaved Changes To " + selectedcutorelementname, "Save Changes Before Closing?")) {save(null);}
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
                        CurrentTime.setText(Util.format_secondsforplayerdisplay(0));
                        TotalTime.setText(Util.format_secondsforplayerdisplay((int) PreviewPlayer.getTotalDuration().toSeconds()));
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
                    CurrentTime.setText(Util.format_secondsforplayerdisplay((int) newValue.toSeconds()));
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
        private MainController Root;
        private PlayerUI.ReferenceType tempreferencetype;

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
            setTitle("Preferences");
            doubleTextField(FadeInValue, false);
            doubleTextField(FadeOutValue, false);
            doubleTextField(EntrainmentVolumePercentage, false);
            doubleTextField(AmbienceVolumePercentage, false);
            kujiin.xml.Options.STYLETHEMES.clear();

        // Add Listeners
            setuplisteners();
            setuptooltips();
            populatefromxml();
            referencetoggle();
            ramptoggle();
        }
    // Setup Methods
        public void populatefromxml() {
        // Program Options
            TooltipsCheckBox.setSelected(Root.getOptions().getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(Root.getOptions().getProgramOptions().getHelpdialogs());
        // Session Options
            AlertSwitch.setSelected(Root.getOptions().getSessionOptions().getAlertfunction());
            try {AlertFile = new File(Options.getSessionOptions().getAlertfilelocation());}
            catch (NullPointerException ignored) {AlertFile = null;}
            checkalertfile();
        // Playback Options
            RampSwitch.setSelected(Options.getSessionOptions().getRampenabled());
            FadeInValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeinduration()));
            FadeOutValue.setText(String.format("%.2f", Options.getSessionOptions().getFadeoutduration()));
            EntrainmentVolumePercentage.setText(String.format("%.1f", Options.getSessionOptions().getEntrainmentvolume() * 100));
            AmbienceVolumePercentage.setText(String.format("%.1f", Options.getSessionOptions().getAmbiencevolume() * 100));
        // Appearance Options
            try {
                for (File i : kujiin.xml.Options.DIRECTORYSTYLES.listFiles()) {
                    if (i.getName().endsWith(".css")) {kujiin.xml.Options.STYLETHEMES.add(i.getName().substring(0, i.getName().length() - 4));}
                }
                if (kujiin.xml.Options.STYLETHEMES.size() != 0) {
                    ProgramThemeChoiceBox.setItems(FXCollections.observableArrayList(kujiin.xml.Options.STYLETHEMES));}
            } catch (NullPointerException e) {ProgramThemeChoiceBox.getItems().clear(); ProgramThemeChoiceBox.setDisable(true);}
        }
        public void setuptooltips() {
            TooltipsCheckBox.setTooltip(new Tooltip("Display Messages Like These When Hovering Over Program Controls"));
            if (Root.getOptions().getProgramOptions().getTooltips()) {
                HelpDialogsCheckBox.setTooltip(new Tooltip(""));
                AlertSwitch.setTooltip(new Tooltip("Enable A Sound File Played In Between Different Session Parts"));
                RampSwitch.setTooltip(new Tooltip("Enable A Ramp In Between Session Parts To Smooth Mental Transition"));
                FadeInValue.setTooltip(new Tooltip("Seconds To Fade In Audio Into Session Part"));
                FadeOutValue.setTooltip(new Tooltip("Seconds To Fade Out Audio Out Of Session Part"));
                EntrainmentVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Entrainment (Changeable In Session)"));
                AmbienceVolumePercentage.setTooltip(new Tooltip("Default Volume Percentage For Ambience (Changeable In Session)"));
                DeleteAllGoalsButton.setTooltip(new Tooltip("Delete ALL Goals Past, Present And Completed (This CANNOT Be Undone)"));
                DeleteAllSessionsProgressButton.setTooltip((new Tooltip("Delete ALL Sessions Past, Present And Completed (This CANNOT Be Undone)")));
            } else {
                HelpDialogsCheckBox.setTooltip(null);
                AlertSwitch.setTooltip(null);
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
            TooltipsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {setuptooltips(); changedvalue();});
            HelpDialogsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            FadeInValue.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            FadeOutValue.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            EntrainmentVolumePercentage.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            AmbienceVolumePercentage.textProperty().addListener((observable, oldValue, newValue) -> {changedvalue();});
            ProgramThemeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {selectnewtheme(); changedvalue();});
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
        }

    // Alert File Methods
        public void alertfiletoggle() {
            if (AlertSwitch.isSelected()) {
                if (Options.getSessionOptions().getAlertfilelocation() == null) {
                    AlertFile = getnewalertfile();
                    checkalertfile();
                }
            } else {
                if (Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
                    AlertFile = null;
                    checkalertfile();
                } else {
                    AlertSwitch.setSelected(true);
                }
            }
        }
        public File getnewalertfile() {
            File newfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
            if (newfile != null) {
                if (Util.audio_isValid(newfile)) {
                    double duration = Util.audio_getduration(newfile);
                    if (duration > 10000) {
                        if (!Util.gui_getokcancelconfirmationdialog(Root, "Validation", "Alert File Is longer Than 10 Seconds",
                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                                        "Really Use It?", duration))) {newfile = null;}
                    }
                } else {
                    Util.gui_showinformationdialog(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Util.audio_getsupportedText());
                    newfile = null;
                }
            }
            return newfile;
        }
        public boolean checkalertfile() {
            boolean good;
            if (AlertFile != null && Util.audio_isValid(AlertFile)) {
                good = true;
                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
                String audioduration = Util.format_secondsforplayerdisplay((int) Util.audio_getduration(AlertFile));
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
            tempreferencetype = PlayerUI.ReferenceType.html;
        }
        public void TXTTypeSelected() {
            ReferenceHTMLRadioButton.setSelected(false);
            ReferenceTXTRadioButton.setSelected(true);
            tempreferencetype = PlayerUI.ReferenceType.txt;
        }

    // Ramp Methods
        public void ramptoggle() {
            boolean enabled = RampSwitch.isSelected();
            if (enabled) {RampSwitch.setText("ON");}
            else {RampSwitch.setText("OFF");}
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
        public void apply(ActionEvent actionEvent) {
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
                }
            } catch (Exception e) {e.printStackTrace();}
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
            Util.gui_validate(EntrainmentVolumePercentage, entrainmentgood);
            Util.gui_validate(AmbienceVolumePercentage, ambiencegood);
            boolean alertfilegood = checkalertfile();
            if (AlertSwitch.isSelected()) {Util.gui_validate(AlertFileTextField, alertfilegood);}
            return entrainmentgood && ambiencegood && alertfilegood;
        }
        public void resettodefaults(ActionEvent actionEvent) {
            Options.resettodefaults();
            valuechanged = true;
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
                if (! Util.gui_getokcancelconfirmationdialog(Root, "Confirmation", "You Have Unsaved Changes", "Exit Without Saving?")) {return;}
            }
            Root.setOptions(Options);
            Root.getOptions().marshall();
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
            this.length = new SimpleStringProperty(Util.format_secondsforplayerdisplay((int) (duration / 1000)));
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
