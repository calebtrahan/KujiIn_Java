package kujiin.ui.availableambience;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.PlaybackItemAmbience;
import kujiin.xml.SoundFile;

import java.io.IOException;

public class AvailableAmbienceEditor extends StyledStage {
    public ListView<String> PlaybackItemsListView;
    public TableView<AmbienceSong> AmbienceTable;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public Button AddFilesButton;
    public Button PreviewButton;
    public Button RemoveButton;
    public TextField TotalDurationTextField;
    private AvailableAmbiences availableAmbiences;
    private PlaybackItemAmbience playbackitemambience;
    private final String[] names = {"Qi-Gong", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Earth", "Air", "Fire", "Water", "Void"};

    public AvailableAmbienceEditor(AvailableAmbiences availableAmbiences) {
        try {
            this.availableAmbiences = availableAmbiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/availableambience/AvailableAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Available Ambience Editor");
            ObservableList<String> allnames = FXCollections.observableArrayList(names);
            PlaybackItemsListView.setItems(allnames);
            PlaybackItemsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> playbackitemchanged());
        } catch (IOException e) {}
    }



    private void playbackitemchanged() {
        int index = PlaybackItemsListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            playbackitemambience = availableAmbiences.getsessionpartAmbience(index);
            populateambiencetable();
        }
    }
    private void populateambiencetable() {
        if (playbackitemambience != null) {
            ObservableList<AmbienceSong> ambiencesongs = FXCollections.observableArrayList();
            for (SoundFile i : playbackitemambience.getAmbience()) {
                ambiencesongs.add(new AmbienceSong(i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getDuration()))));
            }
            AmbienceTable.setItems(ambiencesongs);
        }
    }

// Button Actions
    private void removeitem() {}
    private void additems() {}
    private void preview() {}


    class AmbienceSong {
        private StringProperty name;
        private StringProperty duration;

        public AmbienceSong(String name, String duration) {
            this.name = new SimpleStringProperty(name);
            this.duration = new SimpleStringProperty(duration);
        }
    }
}
