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
import kujiin.ui.dialogs.alerts.AnswerDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;
import kujiin.util.table.AmbienceSong;
import kujiin.xml.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AmbienceEditor_Simple extends Stage implements Initializable {
    public TableView<AmbienceSong> AmbienceTable;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public ChoiceBox<String> SessionPartChoiceBox;
    public Button SaveButton;
    public Button CloseButton;
    public Button AddButton;
    public Button RemoveButton;
    public Button PreviewButton;
    public TextField TotalDuration;
    public Button AdvancedButton;
    private AmbienceSong selectedambiencesong;
    private PreviewFile previewdialog;
    private AvailableAmbiences availableAmbiences;
    private PlaybackItemAmbience selectedplaybackitemambience;
    private Preferences preferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AmbienceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        AmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged(newValue));
        ObservableList<String> allnames = FXCollections.observableArrayList();
        allnames.addAll(Preferences.ALLNAMES);
        SessionPartChoiceBox.setItems(allnames);
        SaveButton.setOnAction(event -> save());
        CloseButton.setOnAction(event -> closedialog());
        AdvancedButton.setOnAction(event -> advancedmode());
        AddButton.setOnAction(event -> addfiles());
        RemoveButton.setOnAction(event -> remove());
        PreviewButton.setOnAction(event -> preview());
//        AmbienceTable.setRowFactory(param -> {
//            final TableRow<AmbienceSong> row = new TableRow<>();
//            final ContextMenu rowMenu = new ContextMenu();
//            MenuItem details = new MenuItem("Details");
//            details.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//
//                }
//            });
//            MenuItem previewitem = new MenuItem("Preview");
//            previewitem.setOnAction(event -> new PreviewFile(SoundList.getplaybackItemGoals(AmbienceTable.getSelectionModel().getSelectedIndex()).getFile()).showAndWait());
//            MenuItem removeItem = new MenuItem("Delete");
//            removeItem.setOnAction(event -> remove());
//            rowMenu.getItems().addAll(previewitem, removeItem);
//            return null;
//        });
    }
    public AmbienceEditor_Simple(AvailableAmbiences availableAmbiences, Preferences preferences) {
        try {
            this.availableAmbiences = availableAmbiences;
            this.preferences = preferences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/availableambience/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog());
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
            DurationColumn.setStyle("-fx-alignment: CENTER");
            AmbienceTable.setPlaceholder(new Label("Select A Playback Item To Edit Ambience"));
        } catch (IOException ignored) {}
    }
    public AmbienceEditor_Simple(AvailableAmbiences availableAmbiences, Preferences preferences, PlaybackItem playbackItem) {
        try {
//            if (! Root.getStage().isIconified()) {Root.getStage().setIconified(true);}
            this.availableAmbiences = availableAmbiences;
            this.preferences = preferences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/availableambience/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
            DurationColumn.setStyle("-fx-alignment: CENTER");
            setOnShown(event -> {
                SessionPartChoiceBox.getSelectionModel().select(playbackItem.getCreationindex());
                selectandloadsessionpart();
            });
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
            selectedplaybackitemambience = availableAmbiences.getsessionpartAmbience(playbackItem.getCreationindex());
        } catch (IOException ignored) {}
    }

// Button Methods
    public void addfiles() {
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
            if (! new ConfirmationDialog(preferences, "Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard").getResult()) {
                files = Util.list_removeduplicates(files);
            }
        }
    }
    for (File i : files) {
        SoundFile soundFile = new SoundFile(i);
        addandcalculateduration(soundFile);
    }
    if (notvalidfilecount > 0) {new InformationDialog(preferences, "Information", notvalidfilecount + " Files Were Not Valid And Weren't Added", "");}
}
    public void remove() {
        List<AmbienceSong> items = AmbienceTable.getSelectionModel().getSelectedItems();
        List<SoundFile> soundfiles = new ArrayList<>();
        for (AmbienceSong i : items) {soundfiles.add(i.getSoundFile());}
        if (items.size() > 1 && ! new ConfirmationDialog(preferences, "Delete " + items.size() + " Items", "Really Delete " + items.size() + " Items?", null).getResult()) {
            return;
        }
        if (items.size() == 0) {
            new InformationDialog(preferences, "Information", "Nothing Selected", "Select A Table Item To Remove");
            return;
        }
        boolean deletefiles = new ConfirmationDialog(preferences, "Delete Files", "Also Delete Files From Hard Drive?", "This Cannot Be Undone").getResult();
        for (SoundFile i : soundfiles) {
            selectedplaybackitemambience.remove(selectedplaybackitemambience.getAmbience().indexOf(i));
            if (deletefiles) {i.getFile().delete();}
        }
        populatetable();
    }
    public void preview() {
        if (AmbienceTable.getSelectionModel().getSelectedItems().size() > 1) {
            new InformationDialog(preferences, "Cannot Preview", "Cannot Preview Multiple Files", "Select One File To Preview");
            return;
        }
        if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
            if (previewdialog == null || !previewdialog.isShowing()) {
                previewdialog = new PreviewFile(selectedambiencesong.getFile());
                previewdialog.showAndWait();
            }
        }
    }
    public void advancedmode() {
        if (unsavedchanges()) {
            if (new ConfirmationDialog(preferences, "Unsaved Changes", null, "You Have Unsaved Changes To " + selectedplaybackitemambience.getName(), "Save Changes", "Discard").getResult()) {save();}
        }
        this.close();
        if (selectedplaybackitemambience != null) {
//            new AmbienceEditor_Advanced(availableAmbiences, preferences, selectedplaybackitem).show();
        } else {new AmbienceEditor_Advanced(availableAmbiences, preferences).show();}
    }
    public void save() {
        availableAmbiences.setsessionpartAmbience(AmbienceTable.getSelectionModel().getSelectedIndex(), selectedplaybackitemambience);
        availableAmbiences.marshall();
        new InformationDialog(preferences, "Saved", selectedplaybackitemambience.getName() + " Ambience Saved", null);
    }
    public void closedialog() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(preferences, this, "Unsaved Changes", "You Have Unsaved Changes To " + selectedplaybackitemambience.getName(), "Save Changes?","Save", "Discard", "Cancel").getResult()) {
                case YES: save();
                case NO: close();
            }
        } else {close();}
    }

// Table Methods
    private void populatetable() {
        AmbienceTable.getItems().clear();
        if (selectedplaybackitemambience != null && selectedplaybackitemambience.hasAny()) {
            ObservableList<AmbienceSong> ambienceSongs = FXCollections.observableArrayList();
            for (SoundFile i : selectedplaybackitemambience.getAmbience()) {
                ambienceSongs.add(new AmbienceSong(i));
            }
            AmbienceTable.setItems(ambienceSongs);
            calculatetotalduration();
        } else {AmbienceTable.setPlaceholder(new Label("No Ambience For " + selectedplaybackitemambience.getName()));}
    }
    private void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
    private void selectandloadsessionpart() {
        int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            selectedplaybackitemambience = availableAmbiences.getsessionpartAmbience(SessionPartChoiceBox.getSelectionModel().getSelectedIndex());
            populatetable();
        }
    }
    private void addandcalculateduration(SoundFile soundFile) {
        MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
        calculatedurationplayer.setOnReady(() -> {
            soundFile.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
            calculatedurationplayer.dispose();
            selectedplaybackitemambience.add(soundFile);
            populatetable();
        });
    }
    private void calculatetotalduration() {
        Duration totalselectedduration = Duration.ZERO;
        for (AmbienceSong i : AmbienceTable.getItems()) {
            totalselectedduration = totalselectedduration.add(Duration.millis(i.getDuration()));
        }
        TotalDuration.setText(Util.formatdurationtoStringDecimalWithColons(totalselectedduration));
    }
    private boolean unsavedchanges() {
        if (SessionPartChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
        try {
            List<SoundFile> ambiencelist = selectedplaybackitemambience.getAmbience();
            if (AmbienceTable.getItems().size() != ambiencelist.size()) {return true;}
            for (AmbienceSong x : AmbienceTable.getItems()) {
                if (! ambiencelist.contains(x.getSoundFile())) {return true;}
            }
            return false;
        } catch (NullPointerException ignored) {return false;}
    }
}