package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.util.table.AmbienceSong;
import kujiin.xml.Preferences;
import kujiin.xml.SoundFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static kujiin.xml.Preferences.PROGRAM_ICON;

public class AmbienceEditor_Advanced extends Stage implements Initializable {
    public Button RightArrow;
    public Button LeftArrow;
    public ChoiceBox<String> SessionPartSelectionBox;
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
    private SessionPart selectedsessionpart;
    private File tempdirectory;
    private PreviewFile previewdialog;
    private MainController Root;

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
        allnames.addAll(Preferences.ALLNAMES);
        SessionPartSelectionBox.setItems(allnames);
        Actual_TotalDuration.setEditable(false);
        Temp_TotalDuration.setEditable(false);
        LeftArrow.setOnAction(event -> leftarrowpressed());
        RightArrow.setOnAction(event -> rightarrowpressed());
        Actual_AddButton.setOnAction(event -> addtoactualambience());
        Actual_RemoveButton.setOnAction(event -> removeactualambience());
        Actual_PreviewButton.setOnAction(event -> previewactualambience());
        Temp_AddButton.setOnAction(event -> addtotempambience());
        Temp_RemoveButton.setOnAction(event -> removefromtempambience());
        Temp_PreviewButton.setOnAction(event -> previewtempambience());
        SaveButton.setOnAction(event -> save());
        CloseButton.setOnAction(event -> close());
        SwitchToSimpleEditor.setOnAction(event -> switchtosimple());
    }
    public AmbienceEditor_Advanced(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
            setResizable(false);
            setOnCloseRequest(event -> {
                if (unsavedchanges()) {
                    switch (new AnswerDialog(Root.getPreferences(), "Save Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save And Close", "Close", "Cancel").getResult()) {
                        case YES:
                            save();
                            break;
                        case CANCEL: event.consume();
                    }
                }
            });
            setTitle("Advanced Ambience Editor");
            SessionPartSelectionBox.setOnAction(event -> selectandloadsessionpart());
            tempdirectory = new File(Preferences.DIRECTORYTEMP, "AmbienceEditor");
        } catch (IOException e) {}
    }
    public AmbienceEditor_Advanced(MainController Root, SessionPart sessionPart) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Advanced.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
            setResizable(false);
            setTitle("Advanced Ambience Editor");
            SessionPartSelectionBox.setOnAction(event -> selectandloadsessionpart());
            SessionPartSelectionBox.getSelectionModel().select(sessionPart.number);
            tempdirectory = new File(Preferences.DIRECTORYTEMP, "AmbienceEditor");
        } catch (IOException e) {new ExceptionDialog(Root.getPreferences(), e).showAndWait();}
    }

// Transfer Methods
    public void rightarrowpressed() {
        // Transfer To Current Cut (use Task)
        if (selected_temp_ambiencesong != null && selectedsessionpart != null) {
            if (! Actual_Table.getItems().contains(selected_temp_ambiencesong)) {
                int tempindex = Temp_Table.getItems().indexOf(selected_actual_ambiencesong);
                actual_ambiencesonglist.add(temp_ambiencesonglist.get(tempindex));
                actual_soundfilelist.add(temp_soundfilelist.get(tempindex));
                Actual_Table.getItems().add(selected_temp_ambiencesong);
                calculateactualtotalduration();
            }
        } else {
            if (selected_temp_ambiencesong == null) {
                new InformationDialog(Root.getPreferences(), "Information", "Cannot Transfer", "Nothing Selected");}
            else {
                new InformationDialog(Root.getPreferences(), "Information", "Cannot Transfer", "No SessionPart Selected");}
        }
    }
    public void leftarrowpressed() {
        if (selected_actual_ambiencesong != null && selectedsessionpart != null) {
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
    public void addtotempambience() {
        if (temp_soundfilelist == null) {temp_soundfilelist = new ArrayList<>();}
        if (temp_ambiencesonglist == null) {temp_ambiencesonglist = FXCollections.observableArrayList();}
        addto(Temp_Table, temp_soundfilelist, temp_ambiencesonglist);}
    public void removefromtempambience() {removefrom(Temp_Table, temp_soundfilelist, temp_ambiencesonglist);}
    public void previewtempambience() {preview(selected_temp_ambiencesong);}
    public void tempselectionchanged(AmbienceSong ambiencesong) {
        selected_temp_ambiencesong = ambiencesong;
    }
    public void calculatetemptotalduration() {
        Duration temptotalduration = Duration.ZERO;
        for (AmbienceSong i : Temp_Table.getItems()) {
            temptotalduration = temptotalduration.add(Duration.millis(i.getDuration()));}
        Temp_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(temptotalduration, Temp_TotalDuration.getLayoutBounds().getWidth()));
    }
    public void deletetempambiencefromdirectory() {
        try {
            FileUtils.cleanDirectory(tempdirectory);} catch (IOException ignored) {}
    }

// Actual Ambience Methods
    public void addtoactualambience() {
        if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
        if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
        addto(Actual_Table, actual_soundfilelist, actual_ambiencesonglist);}
    public void removeactualambience() {removefrom(Actual_Table, actual_soundfilelist, actual_ambiencesonglist);}
    public void previewactualambience() {preview(selected_actual_ambiencesong);}
    public void actualselectionchanged(AmbienceSong ambiencesong) {selected_actual_ambiencesong = ambiencesong;}
    public void calculateactualtotalduration() {
        Duration actualtotalduration = Duration.ZERO;
        for (AmbienceSong i : Actual_Table.getItems()) {
            actualtotalduration = actualtotalduration.add(Duration.millis(i.getDuration()));}
        Actual_TotalDuration.setText(Util.formatdurationtoStringSpelledOut(actualtotalduration, Actual_TotalDuration.getLayoutBounds().getWidth()));
    }

// Table Methods
    public void selectandloadsessionpart() {
        int index = SessionPartSelectionBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            if (actual_ambiencesonglist == null) {actual_ambiencesonglist = FXCollections.observableArrayList();}
            else {actual_ambiencesonglist.clear();}
            if (actual_soundfilelist == null) {actual_soundfilelist = new ArrayList<>();}
            else {actual_soundfilelist.clear();}
            Actual_Table.getItems().clear();
            selectedsessionpart = Root.getAllSessionParts(false).get(index);
            if (populateactualambiencetable()) {Actual_Table.setItems(actual_ambiencesonglist);}
            calculateactualtotalduration();
        }
    }
    private void addto(TableView<AmbienceSong> table, ArrayList<SoundFile> soundfilelist, ObservableList<AmbienceSong> songlist) {
        List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
        if (files == null || files.isEmpty()) {return;}
        int notvalidfilecount = 0;
        int validfilecount = 0;
        for (File t : files) {
            if (! Util.audio_isValid(t)) {notvalidfilecount++;}
            else {validfilecount++;}
        }
        if (validfilecount > 0) {
            if (Util.list_hasduplicates(files)) {
                if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard").getResult()) {
                    files = Util.list_removeduplicates(files);
                }
            }
        }
        for (File i : files) {
            SoundFile soundFile = new SoundFile(i);
            addandcalculateduration(soundFile, table, soundfilelist, songlist);
        }
        if (notvalidfilecount > 0) {new InformationDialog(Root.getPreferences(), "Information", notvalidfilecount + " Files Were Not Valid And Weren't Added", "");}
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
            switch (new AnswerDialog(Root.getPreferences(), "Removing File", null, "Removing Ambience From Table. Also Delete File " + soundFile.getName() + " From Disk? (This Cannot Be Undone)",
                    "Remove And Delete File", "Remove But Keep File", "Cancel").getResult()) {
                case YES: soundFile.getFile().delete(); break;
                case CANCEL: return;
            }
            table.getItems().remove(index);
            soundfilelist.remove(index);
            songlist.remove(index);
            calculateactualtotalduration();
            calculatetemptotalduration();
        }
        else {new InformationDialog(Root.getPreferences(), "Information", "Nothing Selected", "Select A Table Item To Remove");}
    }
    private void preview(AmbienceSong selectedsong) {
        if (selectedsong != null && selectedsong.getFile() != null && selectedsong.getFile().exists()) {
            if (previewdialog == null || !previewdialog.isShowing()) {
                previewdialog = new PreviewFile(selectedsong.getFile(), Root, this);
                previewdialog.showAndWait();
            }
        }
    }
    private boolean populateactualambiencetable() {
        actual_ambiencesonglist.clear();
        if (selectedsessionpart != null) {
            try {
                if (selectedsessionpart.getAmbience() == null) {return false;}
                for (SoundFile i : selectedsessionpart.getAmbience().getAmbience()) {
                    actual_soundfilelist.add(i);
                    actual_ambiencesonglist.add(new AmbienceSong(i));
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                new InformationDialog(Root.getPreferences(), "Information", selectedsessionpart + " Has No Ambience", "Please Add Ambience To " + selectedsessionpart);
                return false;
            }
        } else {
            new InformationDialog(Root.getPreferences(), "Information", "No SessionPart Loaded", "Load A SessionPart's Ambience First");
            return false;
        }
    }

// Dialog Methods
    public boolean unsavedchanges() {
        if (SessionPartSelectionBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
        try {
            List<SoundFile> ambiencelist = selectedsessionpart.getAmbience().getAmbience();
            if (actual_soundfilelist.size() != ambiencelist.size()) {return true;}
            for (SoundFile x : actual_soundfilelist) {
                if (! ambiencelist.contains(x)) {return true;}
            }
            return false;
        } catch (NullPointerException ignored) {return false;}
    }
    public void save() {
        int index = SessionPartSelectionBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            actual_soundfilelist.stream().filter(i -> !selectedsessionpart.getAmbience().ambienceexistsinActual(i)).forEach(i -> selectedsessionpart.getAmbience().add(i));
            Root.getAmbiences().setsessionpartAmbience(selectedsessionpart, selectedsessionpart.getAmbience());
            Root.getAmbiences().marshall();
            new InformationDialog(Root.getPreferences(), "Saved", "Ambience Saved To " + selectedsessionpart, "");
        } else {
            new InformationDialog(Root.getPreferences(), "Cannot Save", "No SessionPart Selected", "Cannot Save");}
    }
    public void switchtosimple() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(Root.getPreferences(), "Switch To Simple Mode", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save Changes", "Switch Without Saving", "Cancel").getResult()) {
                case YES: save(); break;
                case CANCEL: return;
            }
        }
        this.close();
        deletetempambiencefromdirectory();
        if (selected_temp_ambiencesong != null && selectedsessionpart != null) {
            new AmbienceEditor_Simple(Root, selectedsessionpart).show();
        } else {new AmbienceEditor_Simple(Root).show();}
    }

}
