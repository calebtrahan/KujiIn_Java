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
import java.util.*;

public class MainController implements Initializable {
    public Label CreatorStatusBar;
    public Label PlayerStatusBar;
    public Button ExportButton;
    public Button PlayButton;
    public Button ListOfSessionsButton;
    public CheckBox ReferenceFilesOption;
    public ProgressBar goalsprogressbar;
    public Button newgoalButton;
    public Button viewcurrrentgoalsButton;
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
    public Label TotalSessionLabel;
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
    public Slider EntrainmentVolume;
    public Slider AmbienceVolume;
    public Label EntrainmentVolumePercentage;
    public Label AmbienceVolumePercentage;
    public Button CreateButton;
    public Label PreLabel;
    public Label RinLabel;
    public Label KyoLabel;
    public Label TohLabel;
    public Label ShaLabel;
    public Label KaiLabel;
    public Label JinLabel;
    public Label RetsuLabel;
    public Label ZaiLabel;
    public Label ZenLabel;
    public Label PostLabel;
    public Label LengthLabel;
    public Label CompletionLabel;
    public Label VolumeEntrainmentLabel;
    public Label VolumeAmbienceLabel;
    public Label VolumeTopLabel;
    public TextField GoalPracticedMinutes;
    public TextField GoalSetHours;
    public TextField GoalSetMinutes;
    public Label GoalStatusBar;
    public TextField GoalPracticedHours;
    public Scene Scene;
    private This_Session Session;
    private CreatorAndExporterWidget CreatorAndExporter;
    private PlayerWidget Player;
    private ProgressAndGoalsWidget ProgressTracker;
    private Options Options;

    // TODO Check if Goals For Each Cut Are >= Set Practice Time (+ Already Practiced Time)
        // If Not, Ask To Set New Goals
        // Maybe Make A New Goal Setter To Set All Goals For All Cuts On One Dialog

// Event Handlers
    public static final EventHandler<KeyEvent> NONEDITABLETEXTFIELD = event -> Tools.showinformationdialog("Information", "Can't Enter Text", "This Text Field Can't Be Edited");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setProgressTracker(new ProgressAndGoalsWidget(this));
        setSession(new This_Session(this));
        setCreatorAndExporter(new CreatorAndExporterWidget(this));
        setPlayer(new PlayerWidget(this));
        setOptions(new Options());
        getOptions().unmarshall();
        sessionplayerswitch(null);
        CreatorStatusBar.setText("");
    }
    public boolean cleanup() {
        return getPlayer().cleanup() && getCreatorAndExporter().cleanup() && getProgressTracker().cleanup();
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

// Top Menu Actions
    public void editprogramsambience(ActionEvent actionEvent) {
        SessionAmbienceEditor sae = new SessionAmbienceEditor(this);
        sae.showAndWait();
    }
    public void editreferencefiles(ActionEvent actionEvent) {
        EditReferenceFiles a = new EditReferenceFiles(this);
        a.showAndWait();
    }
    public void howtouseprogram(ActionEvent actionEvent) {Tools.howtouseprogram();}
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
    public void togglecreator(ActionEvent actionEvent) {
        getCreatorAndExporter().togglecreator();
    }
    public void toggleexporter(ActionEvent actionEvent) {
        getCreatorAndExporter().toggleexport();
    }
    public void exportsession(Event event) {
        //        CreatorAndExporter.startexport();}
        Tools.showtimedmessage(CreatorStatusBar, "Exporter Is Broken. FFMPEG Is Being A Pain In The Ass", 3000);
    }
    public void ambienceswitch(ActionEvent actionEvent) {
        CreatorAndExporter.checkambience();}
    public void changeallcreatorvalues(ActionEvent actionEvent) {
        CreatorAndExporter.changeallvalues();}

// Session Player Widget
    public void sessionplayerswitch(ActionEvent actionEvent) {
        Player.statusSwitch();
    }
    public void playsession(Event event) {
        Player.play();}
    public void pausesession(Event event) {
        Player.pause();}
    public void stopsession(Event event) {
        Player.stop();}
    public void setReferenceOption(ActionEvent actionEvent) {
        Player.displayreferencefile();}
    public void changesessionoptions(ActionEvent actionEvent) {
        new ChangeProgramOptions(this).showAndWait();
        Options.marshall();
        getProgressTracker().updategoalsui();
        getProgressTracker().updateprogressui();
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
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {e.printStackTrace();}
            setTitle("Reference Files Editor");
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            cutnames = FXCollections.observableArrayList();
            variations = FXCollections.observableArrayList();
            cutnames.addAll(kujiin.xml.Options.ALLNAMES);
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
                    PlayerWidget.DisplayReference dr = new PlayerWidget.DisplayReference(Root, MainTextArea.getText());
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
        private MainController Root;

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
            allnames.addAll(kujiin.xml.Options.ALLNAMES);
            CutSelectionBox.setItems(allnames);
            this.setOnCloseRequest(event -> close());
        }

        public SessionAmbienceEditor(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {e.printStackTrace();}
            setTitle("Session Ambience Editor");
            CutSelectionBox.setOnAction(event -> selectandloadcut());
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
        }
        public SessionAmbienceEditor(MainController root, String cutname) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {e.printStackTrace();}
            setTitle("Session Ambience Editor");
            CutSelectionBox.setOnAction(event -> selectandloadcut());
            CutSelectionBox.getSelectionModel().select(kujiin.xml.Options.ALLNAMES.indexOf(cutname));
            tempdirectory = new File(kujiin.xml.Options.DIRECTORYTEMP, "AmbienceEditor");
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
                                File cutdirectory = new File(kujiin.xml.Options.DIRECTORYAMBIENCE, selectedcutname);
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
            int index = kujiin.xml.Options.ALLNAMES.indexOf(CutSelectionBox.getValue());
            selectedcutname = kujiin.xml.Options.ALLNAMES.get(index);
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
                File thisdirectory = new File(kujiin.xml.Options.DIRECTORYAMBIENCE, selectedcutname);
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
        private MainController Root;

        public ExceptionDialog(MainController root, String exceptionname, String stacktrace) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ExceptionDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {e.printStackTrace();}
            setTitle("Program Error Occured");
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
        public CheckBox RampCheckbox;
        public ChoiceBox<String> RampDurationChoiceBox;
        private kujiin.xml.Options Options;
        private File AlertFile;
        private boolean valuechanged;
        private ObservableList<String> rampselections = FXCollections.observableArrayList(kujiin.xml.Options.RAMPDURATIONS);
        private MainController Root;

        public ChangeProgramOptions(MainController root) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeProgramOptions.fxml"));
            fxmlLoader.setController(this);
            Options = Root.getOptions();
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {e.printStackTrace();}
            setTitle("Change Program Options");
            Tools.integerTextField(FadeInValue);
            Tools.integerTextField(FadeOutValue);
            Tools.integerTextField(EntrainmentVolumePercentage);
            Tools.integerTextField(AmbienceVolumePercentage);
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
            loadoptionsfromxml();
            checkalertfile();
        }

    // Button Actions
        public boolean checkalertfile() {
            String oldalertfilelocation = Options.getSessionOptions().getAlertfilelocation();
            if (oldalertfilelocation != null) {
                AlertFile = new File(oldalertfilelocation);
                if (AlertFile.exists()) {
                    if (Tools.validaudiofile(AlertFile)) {
                        String audioduration = Tools.formatlengthshort((int) Tools.getaudioduration(AlertFile));
                        AlertFileTextField.setText(String.format("%s (%s)", AlertFile.getName(), audioduration));
                    } else {Tools.showinformationdialog("Information", "Old Alert File Is Unsupported", "Please Select A New One");}
                } else {
                    AlertFile = null;
                    AlertFileTextField.setText("Select A New Alert File...");
                }
            }
            return AlertFile != null;
        }
        public void loadoptionsfromxml() {
            TooltipsCheckBox.setSelected(Options.getProgramOptions().getTooltips());
            HelpDialogsCheckBox.setSelected(Options.getProgramOptions().getHelpdialogs());
            RampCheckbox.setSelected(Options.getSessionOptions().getRampenabled());
            RampDurationChoiceBox.setItems(rampselections);
            if (Options.getSessionOptions().getRampduration() == 2) {RampDurationChoiceBox.getSelectionModel().select(0);}
            else if (Options.getSessionOptions().getRampduration() == 3) {RampDurationChoiceBox.getSelectionModel().select(1);}
            else if (Options.getSessionOptions().getRampduration() == 4) {RampDurationChoiceBox.getSelectionModel().select(2);}
            FadeInValue.setText(Options.getSessionOptions().getFadeinduration().toString());
            FadeOutValue.setText(Options.getSessionOptions().getFadeoutduration().toString());
            EntrainmentVolumePercentage.setText(Options.getSessionOptions().getEntrainmentvolume().toString());
            AmbienceVolumePercentage.setText(Options.getSessionOptions().getAmbiencevolume().toString());
        }
        public void apply(ActionEvent actionEvent) {
            Options.getSessionOptions().setEntrainmentvolume(new Double(EntrainmentVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setAmbiencevolume(new Double(AmbienceVolumePercentage.getText()) / 100);
            Options.getSessionOptions().setRampenabled(RampCheckbox.isSelected());
            int index = RampDurationChoiceBox.getSelectionModel().getSelectedIndex();
            if (index == 0) {Options.getSessionOptions().setRampduration(2);}
            else if (index == 1) {Options.getSessionOptions().setRampduration(3);}
            else if (index == 2) {Options.getSessionOptions().setRampduration(5);}
            Options.getSessionOptions().setFadeoutduration(new Double(FadeInValue.getText()));
            Options.getSessionOptions().setFadeinduration(new Double(FadeOutValue.getText()));
            if (AlertFile != null) {
                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
            }
            Options.marshall();
            valuechanged = false;
            ApplyButton.setDisable(true);
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
        public void openandtestnewfile(ActionEvent actionEvent) {
            File newfile = new FileChooser().showOpenDialog(this);
            if (newfile != null) {
                if (Tools.validaudiofile(newfile)) {
                    double duration = Tools.getaudioduration(newfile);
                    if (duration > 10000) {
                        if (! Tools.getanswerdialog("Validation", "Alert File Is longer Than 10 Seconds",
                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                                        "Really Use It?", duration))) {return;}
                    }
                    if (! newfile.getAbsolutePath().equals(kujiin.xml.Options.ALERTFILE.getAbsolutePath()) &&
                            Tools.getanswerdialog("Validation", "Alert File Isn't In Program Directory", "Copy To Program Directory?")) {
                        if (kujiin.xml.Options.ALERTFILE.exists()) {
                            if (!Tools.getanswerdialog("Overwrite", "Old Alert File Already Exists", "Overwrite?")) {return;}
                        }
                        try {
                            kujiin.xml.Options.ALERTFILE.delete();
                            FileUtils.copyFile(newfile, kujiin.xml.Options.ALERTFILE);
                        } catch (IOException e) {Tools.showerrordialog("Error", "Couldn't Copy File", "Check Program Directory Permissions");}
                    } else {
                        AlertFileTextField.setText(String.format("%s (%s)", newfile.getName(), Tools.formatlengthshort((int) duration)));
                        AlertFile = newfile;
                    }
                } else {
                    Tools.showinformationdialog("Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Tools.supportedaudiotext());
                }
            }
        }
        public void resettodefaults(ActionEvent actionEvent) {
            Options.resettodefaults();
            loadoptionsfromxml();
            valuechanged = true;
        }
        public void deleteallsessions(ActionEvent actionEvent) {
            if (Tools.getanswerdialog("Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.showerrordialog("Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.showinformationdialog("Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }
        public void deleteallgoals(ActionEvent actionEvent) {
            if (Tools.getanswerdialog("Confirmation", "This Will Permanently And Irreversible Delete All Sessions Progress And Reset The Progress Tracker", "Really Delete?")) {
                if (! kujiin.xml.Options.SESSIONSXMLFILE.delete()) {
                    Tools.showerrordialog("Error", "Couldn't Delete Sessions File", "Check File Permissions For This File");
                } else {Tools.showinformationdialog("Success", "Successfully Delete Sessions And Reset All Progress", "");}
            }
        }

        @Override
        public void close() {
            if (valuechanged) {
                if (! Tools.getanswerdialog("Confirmation", "You Have Unsaved Changes", "Exit Without Saving?")) {return;}
            }
            super.close();
        }

    }

}
