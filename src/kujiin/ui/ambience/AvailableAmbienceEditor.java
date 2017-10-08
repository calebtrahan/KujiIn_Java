package kujiin.ui.ambience;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.PreviewFile;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.PlaybackItemAmbience;
import kujiin.xml.Preferences;
import kujiin.xml.SoundFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AvailableAmbienceEditor extends StyledStage {
    public ListView<String> PlaybackItemsListView;
    public TableView<AmbienceSong> AmbienceTable;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public Button AddFilesButton;
    public Button PreviewButton;
    public Button RemoveButton;
    public TextField TotalDurationTextField;
    public Label TableTopLabel;
    private AvailableAmbiences availableAmbiences;
    private PlaybackItemAmbience playbackitemambience;
    private Preferences preferences;
    private boolean working = false;
    private int workcompleted;

    public AvailableAmbienceEditor(Preferences preferences, AvailableAmbiences availableAmbiences) {
        try {
            this.preferences = preferences;
            this.availableAmbiences = availableAmbiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/availableambience/AvailableAmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Edit Available Ambience");
            String[] names = {"Qi-Gong", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Earth", "Air", "Fire", "Water", "Void"};
            ObservableList<String> allnames = FXCollections.observableArrayList(names);
            PlaybackItemsListView.setItems(allnames);
            PlaybackItemsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (working) {
                    PlaybackItemsListView.getSelectionModel().select(oldValue.intValue());
                    new InformationDialog(preferences, "Cannot Change Selected Playback Item", "Currently Working", null);
                } else { playbackitemchanged(); }
            });
            AddFilesButton.setOnAction(event -> additems());
            PreviewButton.setOnAction(event -> preview());
            RemoveButton.setOnAction(event -> removeitem());
            AmbienceTable.setPlaceholder(new Label("Select A Playback Item"));
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().duration);
//            TableTopLabel.setText("");
            AmbienceTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                PreviewButton.setDisable(AmbienceTable.getSelectionModel().getSelectedIndex() == -1);
                RemoveButton.setDisable(AmbienceTable.getSelectionModel().getSelectedIndex() == -1);
            });
            AddFilesButton.setDisable(true);
            PreviewButton.setDisable(true);
            RemoveButton.setDisable(true);
        } catch (IOException ignored) {}
    }

// Utility Methods
    private void playbackitemchanged() {
        int index = PlaybackItemsListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            playbackitemambience = availableAmbiences.getsessionpartAmbience(index);
            TableTopLabel.setText("Available Ambience For " + playbackitemambience.getName());
            populateambiencetable();
        }
    }
    private void populateambiencetable() {
        if (playbackitemambience != null) {
            AmbienceTable.getItems().clear();
            ObservableList<AmbienceSong> ambiencesongs = FXCollections.observableArrayList();
            if (playbackitemambience.getAmbience() != null) {
                for (SoundFile i : playbackitemambience.getAmbience()) {
                    ambiencesongs.add(new AmbienceSong(i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getDuration()))));
                }
                AmbienceTable.setItems(ambiencesongs);
                TotalDurationTextField.setText(Util.formatdurationtoStringDecimalWithColons(playbackitemambience.gettotalduration()));
            } else {
                AmbienceTable.setPlaceholder(new Label("No Ambience For " + playbackitemambience.getName()));
                TotalDurationTextField.setText("-");
            }
            AddFilesButton.setDisable(false);
            PreviewButton.setDisable(true);
            RemoveButton.setDisable(true);
        } else {
            AddFilesButton.setDisable(true);
            PreviewButton.setDisable(true);
            RemoveButton.setDisable(true);
        }
    }

// Button Actions
    private void removeitem() {
        int index = AmbienceTable.getSelectionModel().getSelectedIndex();
        if (playbackitemambience != null && index != -1) {
            File soundfile = playbackitemambience.getAmbience().get(index).getFile();
            playbackitemambience.remove(index);
            if (new ConfirmationDialog(preferences, "Remove File?", "Remove " + soundfile.getName() + " From Hard Disk?", "This Cannot Be Undone").getResult()) {
                if (! soundfile.delete()) {
                    new ErrorDialog(preferences, "Couldn't Delete", soundfile.getName() + " Could Not Be Deleted", "Check File Permissions");
                }
            }
        }
    }
    private void additems() {
        if (working) {new InformationDialog(preferences, "Still Working", "Please Wait For Operation To Finish", null);}
        if (playbackitemambience != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Ambience Files To Add");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Audio Files", Util.SUPPORTEDAUDIOFORMATS));
            List<File> files = fileChooser.showOpenMultipleDialog(this);
            if (files != null && ! files.isEmpty()) {
                workcompleted = 0;
                working = true;
                for (File i : files) {
                    Media media = new Media(i.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        SoundFile x = new SoundFile(i);
                        x.setDuration(mediaPlayer.getTotalDuration().toMillis());
                        playbackitemambience.add(x);
                        populateambiencetable();
                        workcompleted++;
                        if (workcompleted == files.size()) {
                            working = false;
                            availableAmbiences.setsessionpartAmbience(PlaybackItemsListView.getSelectionModel().getSelectedIndex(), playbackitemambience);
                        }
                    });
                }
            }
        }
    }
    private void preview() {
        int index =  AmbienceTable.getSelectionModel().getSelectedIndex();
        if (playbackitemambience != null && index != -1) {
            File file = playbackitemambience.getAmbience().get(index).getFile();
            PreviewFile previewFile = new PreviewFile(file);
            previewFile.initModality(Modality.APPLICATION_MODAL);
            previewFile.showAndWait();
        }
    }


    class AmbienceSong {
        private StringProperty name;
        private StringProperty duration;

        public AmbienceSong(String name, String duration) {
            this.name = new SimpleStringProperty(name);
            this.duration = new SimpleStringProperty(duration);
        }
    }
}
