package kujiin.ui.creation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.AmbienceEditor_Simple;
import kujiin.ui.dialogs.PreviewFile;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.util.Util;
import kujiin.xml.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static kujiin.util.Util.SUPPORTEDAUDIOFORMATS;

public class CustomizeAmbience extends StyledStage implements Initializable {
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
    private PlaybackItem playbackItem;
    private PlaybackItemAmbience playbackItemAmbience;
    private Preferences preferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QuickAddRepeatAmbience.setOnAction(event -> quickaddrepeatambience());
        QuickAddShuffleAmbience.setOnAction(event -> quickaddshuffleambience());
        AddFromAmbienceDirectory.setOnAction(event -> addfromavailableambience());
        AddOpenFiles.setOnAction(event -> addfromfiles());
        RemoveButton.setOnAction(event -> removefromtable());
        PreviewButton.setOnAction(event -> preview());
        UpButton.setOnAction(event -> moveup());
        DownButton.setOnAction(event -> movedown());
        AcceptButton.setOnAction(event -> accept());
        CancelButton.setOnAction(event -> close());
        NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        AddOrEditAmbienceTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            RemoveButton.setDisable(newValue == null);
            PreviewButton.setDisable(newValue == null);
            if (newValue != null && AddOrEditAmbienceTable.getItems().size() > 1) {
                UpButton.setDisable(AddOrEditAmbienceTable.getItems().indexOf(newValue) == 0);
                DownButton.setDisable(AddOrEditAmbienceTable.getItems().indexOf(newValue) == AddOrEditAmbienceTable.getItems().size() - 1);
            } else {
                UpButton.setDisable(true);
                DownButton.setDisable(true);
            }
        });
    }
    public CustomizeAmbience(Preferences preferences, PlaybackItem playbackItem, AvailableAmbiences availableAmbiences) {
        try {
            ambience = new Ambience();
            if (playbackItem.getAmbience().getAmbience() != null) {for (SoundFile i : playbackItem.getAmbience().getAmbience()) {ambience.add(i);}}
            this.preferences = preferences;
            this.playbackItem = playbackItem;
            this.availableAmbiences = availableAmbiences;
            playbackItemAmbience = availableAmbiences.getsessionpartAmbience(playbackItem.getCreationindex());
            playbackitemduration = new Duration(playbackItem.getExpectedDuration());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AddOrEditAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            if (ambience.getAmbience() == null || ambience.getAmbience().isEmpty()) {setTitle("Add Ambience"); updatestatusbar();}
            else {setTitle("Customize Ambience"); populatetable();}
            AddOrEditAmbienceTable.setPlaceholder(new Label("No Ambience For " + playbackItem.getName()));
//            NumberColumn.prefWidthProperty().bind(AddOrEditAmbienceTable.widthProperty().multiply(1 / 5));
//            NameColumn.prefWidthProperty().bind(AddOrEditAmbienceTable.widthProperty().multiply(3 / 5));
//            DurationColumn.prefWidthProperty().bind(AddOrEditAmbienceTable.widthProperty().multiply(1 / 5));
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public PlaybackItem getPlaybackItem() {return playbackItem;}

// Button Actions
    public void addfromavailableambience() {
        if (playbackItemAmbience.hasAny()) {
            SelectAvailableAmbience selectAvailableAmbience = new SelectAvailableAmbience(playbackItemAmbience);
            selectAvailableAmbience.initModality(Modality.APPLICATION_MODAL);
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
        ambience.addavailableambience_repeat(playbackitemduration, playbackItemAmbience);
        populatetable();
    }
    public void quickaddshuffleambience() {
        ambience.addavailableambience_shuffle(playbackitemduration, playbackItemAmbience);
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
            PreviewFile previewFile = new PreviewFile(ambience.getAmbience().get(selectedindex).getFile());
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
        ambience.setEnabled(! ambience.getAmbience().isEmpty());
        playbackItem.getAmbience().setAmbience(ambience.getAmbience());
        accepted = ! ambience.getAmbience().isEmpty();
        close();
    }

// Other Methods
    private void populatetable() {
        AddOrEditAmbienceTable.getItems().clear();
        if (! ambience.getAmbience().isEmpty()) {
            ObservableList<AddOrEditAmbienceTableItem> items = FXCollections.observableArrayList();
            int count = 1;
            for (SoundFile i : ambience.getAmbience()) {
                items.add(new AddOrEditAmbienceTableItem(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getDuration()))));
                count++;
            }
            AddOrEditAmbienceTable.setItems(items);
        } else {AddOrEditAmbienceTable.setPlaceholder(new Label("No Ambience For " + playbackItem.getName()));}
        updatestatusbar();
        syncbuttons();
    }
    private void updatestatusbar() {
        AcceptButton.setDisable(! ambience.gettotalDuration().greaterThan(playbackitemduration));
        if (ambience.gettotalDuration().greaterThan(playbackitemduration)) {
            StatusBar.setText("");
        } else {
            Duration leftoverduration;
            if (ambience.gettotalDuration().greaterThan(Duration.ZERO)) {leftoverduration = playbackitemduration.subtract(ambience.gettotalDuration());}
            else {leftoverduration = playbackitemduration;}
            StatusBar.setText("Still Need " + Util.formatdurationtoStringDecimalWithColons(leftoverduration));
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