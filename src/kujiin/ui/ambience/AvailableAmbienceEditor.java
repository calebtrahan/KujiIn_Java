package kujiin.ui.ambience;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;
import kujiin.ui.boilerplate.IconImageView;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.PreviewFile;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.PlaybackItemAmbience;
import kujiin.xml.Preferences;
import kujiin.xml.SoundFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvailableAmbienceEditor extends StyledStage {
    public ChoiceBox<String> PlaybackItemChoiceBox;
    public TableView<AmbienceSong> AmbienceTable;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public MenuButton AddMenu;
    public MenuItem AddFilesMenuItem;
    public MenuItem AddDirectoryMenuItem;
    public Button PreviewButton;
    public Button RemoveButton;
    public Button ClearButton;
    public HBox DurationBox;
    public TextField TotalDurationTextField;
    public Label StatusBar;
    public Button CloseButton;
    private AvailableAmbiences availableAmbiences;
    private PlaybackItemAmbience playbackitemambience;
    private Preferences preferences;
    private boolean working = false;
    private int workcompleted;

    public AvailableAmbienceEditor(Preferences preferences, AvailableAmbiences availableAmbiences) {
        try {
            this.preferences = preferences;
            this.availableAmbiences = availableAmbiences;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/ambience/AmbienceEditor.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Edit Available Ambience");
            String[] names = {"Qi-Gong", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Earth", "Air", "Fire", "Water", "Void"};
            ObservableList<String> allnames = FXCollections.observableArrayList(names);
            PlaybackItemChoiceBox.setItems(allnames);
            PlaybackItemChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (working) {
                    PlaybackItemChoiceBox.getSelectionModel().select(oldValue.intValue());
                    new InformationDialog(preferences, "Cannot Change Selected Playback Item", "Currently Working", null);
                } else { playbackitemchanged(); }
            });
            AddFilesMenuItem.setOnAction(event -> additems());
            AddDirectoryMenuItem.setOnAction(event -> adddirectory());
            PreviewButton.setOnAction(event -> preview());
            RemoveButton.setOnAction(event -> removeitems());
            ClearButton.setOnAction(event -> clearambience());
            AmbienceTable.setPlaceholder(new Label("Select A Playback Item"));
            AmbienceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            NameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
            DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().duration);
            AmbienceTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                boolean noneselected = AmbienceTable.getSelectionModel().getSelectedIndices().isEmpty();
                boolean multipleselected = AmbienceTable.getSelectionModel().getSelectedIndices().size() > 1;
                PreviewButton.setDisable(noneselected || multipleselected);
                RemoveButton.setDisable(noneselected);
            });
            AddMenu.setGraphic(new IconImageView(Preferences.ICON_ADD, 20.0));
            AddMenu.setDisable(true);
            AddMenu.setTooltip(new Tooltip("Add"));
            PreviewButton.setGraphic(new IconImageView(Preferences.ICON_PLAY, 20.0));
            PreviewButton.setTooltip(new Tooltip("Preview"));
            PreviewButton.setDisable(true);
            RemoveButton.setGraphic(new IconImageView(Preferences.ICON_REMOVE, 20.0));
            RemoveButton.setTooltip(new Tooltip("Add"));
            RemoveButton.setDisable(true);
            ClearButton.setGraphic(new IconImageView(Preferences.ICON_CLEARSESSION, 20.0));
            ClearButton.setTooltip(new Tooltip("Clear Ambience"));
            ClearButton.setDisable(true);
            CloseButton.setOnAction(event -> close());
            setOnCloseRequest(event -> {
                if (working) {event.consume();}
            });
        } catch (IOException ignored) {}
    }

// Utility Methods
    private void setWorking(boolean value) {
        working = value;
        PlaybackItemChoiceBox.setDisable(value);
        AddMenu.setDisable(value);
        PreviewButton.setDisable(value);
        RemoveButton.setDisable(value);
        ClearButton.setDisable(value);
        DurationBox.setDisable(value);
        CloseButton.setDisable(value);
    }
    private void playbackitemchanged() {
        int index = PlaybackItemChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            playbackitemambience = availableAmbiences.getsessionpartAmbience(index);
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
            AddMenu.setDisable(false);
            PreviewButton.setDisable(true);
            RemoveButton.setDisable(true);
            ClearButton.setDisable(true);
        } else {
            AddMenu.setDisable(true);
            PreviewButton.setDisable(true);
            RemoveButton.setDisable(true);
            ClearButton.setDisable(true);
        }
    }
    private void displayStatusBarMessage(String text, Duration timeoutduration) {
        StatusBar.setText(text);
        if (timeoutduration != null) { new Timeline(new KeyFrame(timeoutduration, ae -> StatusBar.setText(""))).play(); }
    }

// Button Actions
    private void removeitems() {
        ObservableList<Integer> indexes = AmbienceTable.getSelectionModel().getSelectedIndices();
        if (playbackitemambience != null && ! indexes.isEmpty()) {
            if (indexes.size() > 1 && new ConfirmationDialog(preferences, "Confirmation", "Really Remove " + indexes.size() + " Items?", null).getResult()) {return;}
            for (Integer i : indexes) {
                playbackitemambience.remove(i);
                populateambiencetable();
            }
        }
    }
    private void additems() {
        if (playbackitemambience != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Ambience Files To Add");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Audio Files", Util.SUPPORTEDAUDIOFORMATS));
            List<File> files = fileChooser.showOpenMultipleDialog(this);
            if (files != null && ! files.isEmpty()) {
                List<File> qualifiedfiles = new ArrayList<>();
                for (File x : files) { if (Util.audio_isValid(x)) {qualifiedfiles.add(x);} }
                if (qualifiedfiles.isEmpty()) {return;}
                workcompleted = 0;
                setWorking(true);
                List<SoundFile> filestoadd = new ArrayList<>();
                for (File i : files) {
                    Media media = new Media(i.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        SoundFile x = new SoundFile(i);
                        x.setDuration(mediaPlayer.getTotalDuration().toMillis());
                        filestoadd.add(x);
                        workcompleted++;
                        displayStatusBarMessage("Adding Files " + (workcompleted + 1) + "/" + qualifiedfiles.size(), null);
                        if (workcompleted == qualifiedfiles.size()) {
                            ObservableList<Integer> indexes = AmbienceTable.getSelectionModel().getSelectedIndices();
                            if (! indexes.isEmpty()) { playbackitemambience.addmultiple(indexes.get(indexes.size() - 1), filestoadd); }
                            else {playbackitemambience.addmultiple(filestoadd);}
                            displayStatusBarMessage("Added " + qualifiedfiles.size() + " Files", Duration.seconds(2.0));
                            setWorking(false);
                            availableAmbiences.setsessionpartAmbience(PlaybackItemChoiceBox.getSelectionModel().getSelectedIndex(), playbackitemambience);
                            populateambiencetable();
                        }
                    });
                }
            }
        }
    }
    private void adddirectory() {
        if (playbackitemambience != null) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Ambience Files To Add");
            File directory = directoryChooser.showDialog(this);
            try {
                if (directory != null) {
                    File[] files;
                    try {files = directory.listFiles();} catch (NullPointerException ignored) {return;}
                    if (files == null) {return;}
                    List<File> qualifiedfiles = new ArrayList<>();
                    for (File x : files) { if (Util.audio_isValid(x)) {qualifiedfiles.add(x);} }
                    if (qualifiedfiles.isEmpty()) {return;}
                    workcompleted = 0;
                    setWorking(true);
                    for (File i : files) {
                        Media media = new Media(i.toURI().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setOnReady(() -> {
                            SoundFile x = new SoundFile(i);
                            x.setDuration(mediaPlayer.getTotalDuration().toMillis());
                            playbackitemambience.add(x);
                            populateambiencetable();
                            workcompleted++;
                            if (workcompleted == files.length) {
                                setWorking(false);
                                availableAmbiences.setsessionpartAmbience(PlaybackItemChoiceBox.getSelectionModel().getSelectedIndex(), playbackitemambience);
                            }
                        });
                    }
                }
            } catch (NullPointerException ignored) { }
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
    private void clearambience() {
        if (playbackitemambience != null && new ConfirmationDialog(preferences, "Confirmation","Really Clear " + playbackitemambience.getName() + " Ambience", "This Cannot Be Undone").getResult()) {
            playbackitemambience.clear();
            populateambiencetable();
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