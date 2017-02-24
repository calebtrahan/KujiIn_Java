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
import kujiin.ui.dialogs.alerts.ErrorDialog;
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
    private ObservableList<AmbienceSong> AmbienceList = FXCollections.observableArrayList();
    private ArrayList<SoundFile> SoundList = new ArrayList<>();
    private AmbienceSong selectedambiencesong;
    private Session.PlaybackItem selectedplaybackitem;
    private PreviewFile previewdialog;
    private AvailableAmbiences availableAmbiences;
    private PlaybackItemAmbience selectedplaybackitemambience;
    private Preferences preferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }
    public AmbienceEditor_Simple(AvailableAmbiences availableAmbiences, Preferences preferences) {
        try {
            this.availableAmbiences = availableAmbiences;
            this.preferences = preferences;
//            if (! Root.getStage().isIconified()) {Root.getStage().setIconified(true);}
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog());
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
            NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
        } catch (IOException ignored) {}
    }
    public AmbienceEditor_Simple(AvailableAmbiences availableAmbiences, Preferences preferences, Session.PlaybackItem playbackItem) {
        try {
//            if (! Root.getStage().isIconified()) {Root.getStage().setIconified(true);}
            this.availableAmbiences = availableAmbiences;
            this.preferences = preferences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Simple.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog());
            setOnShowing(event -> {
                SessionPartChoiceBox.getSelectionModel().select(playbackItem.getEntrainmentandavailableambienceindex());
                selectandloadsessionpart();
            });
            SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
            selectedplaybackitemambience = availableAmbiences.getsessionpartAmbience(playbackItem.getEntrainmentandavailableambienceindex());
        } catch (IOException ignored) {}
    }

// Table Methods
    public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
    public void selectandloadsessionpart() {
        int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            AmbienceList.clear();
            SoundList.clear();
            AmbienceTable.getItems().clear();
            calculatetotalduration();
        }
    }
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
    public void addandcalculateduration(SoundFile soundFile) {
        MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
        calculatedurationplayer.setOnReady(() -> {
            soundFile.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
            calculatedurationplayer.dispose();
            SoundList.add(soundFile);
            AmbienceSong tempsong = new AmbienceSong(soundFile);
            AmbienceList.add(tempsong);
            AmbienceTable.getItems().add(tempsong);
            selectedplaybackitemambience.add(soundFile);
            calculatetotalduration();
        });
    }
    public void remove() {
        int index = AmbienceTable.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            SoundFile soundFile = SoundList.get(index);
            selectedplaybackitemambience.remove(index);
            if (new ConfirmationDialog(preferences, "Confirmation", null, "Also Delete File " + soundFile.getName() + " From Hard Drive? This Cannot Be Undone", "Delete File", "Keep File").getResult()) {
                if (! soundFile.getFile().delete()) {
                    new ErrorDialog(preferences, "Couldn't Delete", null, "Couldn't Delete " + soundFile.getFile().getAbsolutePath() + " Check File Permissions");
                }
            }
            AmbienceTable.getItems().remove(index);
            AmbienceList.remove(index);
            SoundList.remove(index);
            calculatetotalduration();
        }
        else {
            new InformationDialog(preferences, "Information", "Nothing Selected", "Select A Table Item To Remove");}
    }
    public void preview() {
        if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
            if (previewdialog == null || !previewdialog.isShowing()) {
                previewdialog = new PreviewFile(selectedambiencesong.getFile());
                previewdialog.showAndWait();
            }
        }
    }
    public void populateactualambiencetable() {
        AmbienceList.clear();
        if (selectedplaybackitem != null) {
            PlaybackItemAmbience playbackItemAmbience = availableAmbiences.getsessionpartAmbience(selectedplaybackitem.getEntrainmentandavailableambienceindex());
            if (playbackItemAmbience.hasAny()) {
                for (SoundFile i : playbackItemAmbience.getAmbience()) {
                    SoundList.add(i);
                    AmbienceList.add(new AmbienceSong(i));
                }
                AmbienceTable.setItems(AmbienceList);
            }
        }
    }
    public void calculatetotalduration() {
        Duration totalselectedduration = Duration.ZERO;
        for (AmbienceSong i : AmbienceTable.getItems()) {
            totalselectedduration = totalselectedduration.add(Duration.millis(i.getDuration()));
        }
        TotalDuration.setText(Util.formatdurationtoStringSpelledOut(totalselectedduration, TotalDuration.getLayoutBounds().getWidth()));
    }
    public boolean unsavedchanges() {
        if (SessionPartChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
        try {
            List<SoundFile> ambiencelist = selectedplaybackitemambience.getAmbience();
            if (SoundList.size() != ambiencelist.size()) {return true;}
            for (SoundFile x : SoundList) {
                if (! ambiencelist.contains(x)) {return true;}
            }
            return false;
        } catch (NullPointerException ignored) {return false;}
    }

// Dialog Methods
    public void advancedmode() {
        if (unsavedchanges()) {
            if (new ConfirmationDialog(preferences, "Unsaved Changes", null, "You Have Unsaved Changes To " + selectedplaybackitem.getName(), "Save Changes", "Discard").getResult()) {save();}
        }
        this.close();
        if (selectedplaybackitem != null) {
            new AmbienceEditor_Advanced(availableAmbiences, preferences, selectedplaybackitem).show();
        } else {new AmbienceEditor_Advanced(availableAmbiences, preferences).show();}
    }
    public void save() {
        availableAmbiences.setsessionpartAmbience(selectedplaybackitem.getEntrainmentandavailableambienceindex(), selectedplaybackitemambience);
        availableAmbiences.marshall();
        new InformationDialog(preferences, "Saved", selectedplaybackitem.getName() + "Ambience Saved", null);
    }
    public void closedialog() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(preferences, this, "Unsaved Changes", null, "You Have Unsaved Changes To " + selectedplaybackitem.getName(), "Save", "Discard", "Cancel").getResult()) {
                case YES: save();
                case NO: close(); break;
            }
        } else {close();}
    }

}