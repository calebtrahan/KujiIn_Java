package kujiin.ui.creation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.PreviewFile;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.Util;
import kujiin.xml.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static kujiin.util.Util.SUPPORTEDAUDIOFORMATS;

public class AddOrEditAmbience extends Stage {
    public TableView<AddOrEditAmbienceTableItem> AddOrEditAmbienceTable;
    public TableColumn<AddOrEditAmbienceTableItem, Integer> NumberColumn;
    public TableColumn<AddOrEditAmbienceTableItem, String> NameColumn;
    public TableColumn<AddOrEditAmbienceTableItem, String> DurationColumn;
    public MenuButton AddMenu;
    public MenuItem AddFromAmbienceDirectory;
    public MenuItem AddOpenFiles;
    public Button RemoveButton;
    public Button PreviewButton;
    public Button UpButton;
    public Button DownButton;
    public Label StatusBar;
    public Button AcceptButton;
    public Button CancelButton;
    public MenuItem QuickAddRepeatAmbience;
    public MenuItem QuickAddShuffleAmbience;
    private AvailableAmbiences availableAmbiences;
    private boolean accepted = false;
    private Ambience ambience;
    private Duration playbackitemduration;
    private SelectAvailableAmbience selectAvailableAmbience;
    private PreviewFile previewFile;
    private Session.PlaybackItem playbackItem;
    private PlaybackItemAmbience playbackItemAmbience;
    private Preferences preferences;

    public AddOrEditAmbience(Preferences preferences, Session.PlaybackItem playbackItem, AvailableAmbiences availableAmbiences) {
        try {
            this.preferences = preferences;
            this.playbackItem = playbackItem;
            this.availableAmbiences = availableAmbiences;
            playbackItemAmbience = availableAmbiences.getsessionpartAmbience(playbackItem.getAvailableambienceindex());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AddOrEditAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            ambience = playbackItem.getAmbience();
            if (ambience.getAmbience().isEmpty()) {setTitle("Add Ambience"); updatestatusbar();}
            else {setTitle("Edit Ambience"); populatetable();}
            playbackitemduration = new Duration(playbackItem.getDuration());
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }

// Button Actions
    public void addfromavailableambience() {
        if (playbackItemAmbience.hasAny()) {
            selectAvailableAmbience = new SelectAvailableAmbience(playbackItemAmbience);
            selectAvailableAmbience.showAndWait();
            if (selectAvailableAmbience.isAccepted() && ! selectAvailableAmbience.getAmbiencetoadd().isEmpty()) {
                for (SoundFile i : selectAvailableAmbience.getAmbiencetoadd()) {ambience.add(i);}
                populatetable();
            } else {syncbuttons();}
        } else {
            if (new ConfirmationDialog(preferences, "No Ambience Found", "No Available Ambience Found", "Add Ambience To " +
            playbackItem.getName() + "?").getResult()) {
                AmbienceEditor_Simple amb = new AmbienceEditor_Simple(availableAmbiences, preferences, playbackItem);
                amb.initOwner(this);
                amb.initModality(Modality.APPLICATION_MODAL);
                amb.showAndWait();
                addfromavailableambience();
            }
        }
    }
    public void addfromfiles() {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(this);
        List<File> discardedfiles = new ArrayList<>();
        List<File> filestoadd = new ArrayList<>();
        for (File i : files) {
            if (! SUPPORTEDAUDIOFORMATS.contains(i.getAbsolutePath().substring(i.getAbsolutePath().lastIndexOf(".")))) {discardedfiles.add(i);}
            else {filestoadd.add(i);}
        }
        if (! discardedfiles.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Skipped Adding " + discardedfiles.size() + " Files");
            alert.setHeaderText(discardedfiles.size() + " Files Were Invalid And Were Skipped");
            alert.setContentText("Supported Formats: " + SUPPORTEDAUDIOFORMATS.toString());
            alert.showAndWait();
        }
        // Calculate Duration And Add To Table Here
        if (! filestoadd.isEmpty()) {
            // Ask For Confirmation To Add To Available Ambience For PlaybackItem

            populatetable();
        }
    }
    public void quickaddrepeatambience() {
        Duration duration = Duration.ZERO;
        int indexcount = 0;
        while (duration.lessThan(playbackitemduration)) {
            try {ambience.add(playbackItemAmbience.getAmbience().get(indexcount));}
            catch (IndexOutOfBoundsException ignored) {indexcount = 0;}
        }
        populatetable();
    }
    public void quickaddshuffleambience() {
        List<SoundFile> listtoshuffle = playbackItemAmbience.getAmbience();
        Duration duration = Duration.ZERO;
        int indexcount = 0;
        while (duration.lessThan(playbackitemduration)) {
            try {listtoshuffle.add(playbackItemAmbience.getAmbience().get(indexcount));}
            catch (IndexOutOfBoundsException ignored) {indexcount = 0;}
        }
        Collections.shuffle(listtoshuffle);
        ambience.setAmbience(listtoshuffle);
        populatetable();
    }
    public void removefromtable() {
        int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1) {
            ambience.remove(selectedindex);
            populatetable();
        }
    }
    public void preview() {
        int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1) {
            previewFile = new PreviewFile(ambience.getAmbience().get(selectedindex).getFile());
            previewFile.initOwner(this);
            previewFile.initModality(Modality.APPLICATION_MODAL);
            previewFile.showAndWait();
        }
    }
    public void moveup() {
        if (AddOrEditAmbienceTable.getItems().size() > 1) {
            int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
            switch (selectedindex) {
                case -1: return;
                case 0:
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Cannot Move");
                    alert.setHeaderText("Cannot Move First Item In Table Up");
                    alert.initOwner(this);
                    alert.showAndWait();
                    break;
                default:
                    List<SoundFile> newambiencelist = ambience.getAmbience();
                    Collections.swap(newambiencelist, selectedindex, selectedindex -1);
                    ambience.setAmbience(newambiencelist);
                    populatetable();
                    break;
            }
        }
    }
    public void movedown() {
        if (AddOrEditAmbienceTable.getItems().size() > 1) {
            final int lastindex = AddOrEditAmbienceTable.getItems().size() - 1;
            int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
            if (selectedindex != -1) {
                if (selectedindex == lastindex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Cannot Move");
                    alert.setHeaderText("Cannot Move Last Item In Table Down");
                    alert.initOwner(this);
                    alert.showAndWait();
                } else {
                    List<SoundFile> newambiencelist = ambience.getAmbience();
                    Collections.swap(newambiencelist, selectedindex, selectedindex + 1);
                    ambience.setAmbience(newambiencelist);
                    populatetable();
                }
            }
        }
    }
    public void accept() {
        accepted = ! ambience.getAmbience().isEmpty();
        close();
    }
    public void cancel() {
        accepted = false;
    }

// Other Methods
    private void populatetable() {
        AddOrEditAmbienceTable.getItems().clear();
        ObservableList<AddOrEditAmbienceTableItem> items = FXCollections.observableArrayList();
        int count = 1;
        for (SoundFile i : ambience.getAmbience()) {
            items.add(new AddOrEditAmbienceTableItem(count, i.getName(), Util.formatdurationtoStringSpelledOut(new Duration(i.getDuration()), DurationColumn.getWidth())));
        }
        AddOrEditAmbienceTable.setItems(items);
        updatestatusbar();
        syncbuttons();
    }
    private void updatestatusbar() {
        AcceptButton.setDisable(! ambience.gettotalDuration().greaterThan(playbackitemduration));
        if (ambience.gettotalDuration().greaterThan(playbackitemduration)) {
            StatusBar.setText("Ambience Is Long Enough For Session Duration");
        } else {
            Duration leftoverduration;
            if (ambience.gettotalDuration().greaterThan(Duration.ZERO)) {leftoverduration = playbackitemduration.subtract(ambience.gettotalDuration());}
            else {leftoverduration = playbackitemduration;}
            StatusBar.setText("Still Need " + Util.formatdurationtoStringDecimalWithColons(leftoverduration) + " Of Ambience");
        }
    }
    private void syncbuttons() {
        int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
        boolean nothingselected = selectedindex == -1;
        RemoveButton.setDisable(nothingselected);
        PreviewButton.setDisable(nothingselected);
        UpButton.setDisable(nothingselected || selectedindex == 0);
        DownButton.setDisable(nothingselected || selectedindex == AddOrEditAmbienceTable.getItems().size() - 1);
    }

// Table Class
    class AddOrEditAmbienceTableItem {
        IntegerProperty number;
        StringProperty name;
        StringProperty duration;

        public AddOrEditAmbienceTableItem(int number, String name, String duration) {
            this.number = new SimpleIntegerProperty(number);
            this.name = new SimpleStringProperty(name);
            this.duration = new SimpleStringProperty(duration);
        }
    }
}