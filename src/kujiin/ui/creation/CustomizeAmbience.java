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
import kujiin.ui.dialogs.PreviewFile;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
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
    public MenuItem QuickAddShuffleAmbience;
    public MenuItem AddFromAmbienceDirectory;
    public MenuItem AddOpenFiles;
    public Button RemoveButton;
    public Button PreviewButton;
    public Button UpButton;
    public Button DownButton;
    public Label StatusBar;
    public Button AcceptButton;
    public Button CancelButton;
    private boolean accepted = false;
    private Ambience ambience;
    private Duration playbackitemduration;
    private PlaybackItem playbackItem;
    private Preferences preferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    public CustomizeAmbience(Preferences preferences, PlaybackItem playbackItem) {
        try {
            ambience = playbackItem.getAmbience();
            this.preferences = preferences;
            this.playbackItem = playbackItem;
            playbackitemduration = new Duration(playbackItem.getExpectedDuration());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AddOrEditAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            boolean hasavailableambience = playbackItem.getAmbience().hasAvailableAmbience();
            AddFromAmbienceDirectory.setDisable(! hasavailableambience);
            AddOrEditAmbienceTable.setPlaceholder(new Label("No Ambience For " + playbackItem.getName()));
            AddOrEditAmbienceTable.setOnMousePressed(event -> {
                int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
                if (selectedindex != -1) {
                    if (event.isSecondaryButtonDown() && event.getClickCount() == 1) {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem preview = new MenuItem("Preview");
                        preview.setOnAction(event1 -> preview());
                        MenuItem moveup = new MenuItem("Move Up");
                        moveup.setOnAction(event1 -> moveup());
                        MenuItem movedown = new MenuItem("Move Down");
                        movedown.setOnAction(event1 -> movedown());
                        MenuItem remove = new MenuItem("Remove");
                        remove.setOnAction(event1 -> removefromtable());
                        contextMenu.getItems().add(preview);
                        if (selectedindex < AddOrEditAmbienceTable.getItems().size() - 1) {contextMenu.getItems().add(movedown);}
                        if (selectedindex > 0) {contextMenu.getItems().add(moveup);}
                        contextMenu.getItems().add(remove);
                        AddOrEditAmbienceTable.setContextMenu(contextMenu);
                    }
                    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) { preview(); }
                }
            });
            if (ambience.getSessionAmbience() == null || ambience.getSessionAmbience().isEmpty()) {setTitle("Add Ambience"); updatestatusbar();}
            else {setTitle("Customize Ambience"); populatetable();}
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
        if (playbackItem.getAmbience().hasAvailableAmbience()) {
            SelectAvailableAmbience selectAvailableAmbience = new SelectAvailableAmbience(playbackItem);
            selectAvailableAmbience.initModality(Modality.APPLICATION_MODAL);
            selectAvailableAmbience.showAndWait();
            if (selectAvailableAmbience.isAccepted() && ! selectAvailableAmbience.getAmbiencetoadd().isEmpty()) {
                for (SoundFile i : selectAvailableAmbience.getAmbiencetoadd()) {ambience.addPreset(i);}
                populatetable();
            } else {syncbuttons();}
        } else {
            new InformationDialog(preferences, "No Ambience Found", "No Available Ambience Found For " +
                    playbackItem.getName(), "Add Ambience To Use This Feature");
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
        boolean clearambience = false;
        if (ambience.hasPresetAmbience()) {
            if (new ConfirmationDialog(preferences, "Confirmation", "Ambience Already Exists", "Clear Ambience Before Quick Add?").getResult()) {
                clearambience = true;
            }
        }
        ambience.quickadd_repeat(playbackItem, clearambience);
        populatetable();
        AcceptButton.requestFocus();
    }
    public void quickaddshuffleambience() {
        boolean clearambience = false;
        if (ambience.hasPresetAmbience()) {
            if (new ConfirmationDialog(preferences, "Confirmation", "Ambience Already Exists", "Clear Ambience Before Quick Add?").getResult()) {
                clearambience = true;
            }
        }
        ambience.quickadd_shuffle(playbackItem, clearambience);
        populatetable();
        AcceptButton.requestFocus();
    }
    public void removefromtable() {
        int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1) {
            ambience.removePreset(selectedindex);
            populatetable();
            AddMenu.requestFocus();
        }
    }
    public void preview() {
        int selectedindex = AddOrEditAmbienceTable.getSelectionModel().getSelectedIndex();
        if (selectedindex != -1) {
            PreviewFile previewFile = new PreviewFile(ambience.getSessionAmbience().get(selectedindex).getFile());
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
                    List<SoundFile> newambiencelist = ambience.getSessionAmbience();
                    Collections.swap(newambiencelist, selectedindex, selectedindex -1);
                    ambience.setSessionAmbience(newambiencelist);
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
                    List<SoundFile> newambiencelist = ambience.getSessionAmbience();
                    Collections.swap(newambiencelist, selectedindex, selectedindex + 1);
                    ambience.setSessionAmbience(newambiencelist);
                    populatetable();
                }
            }
        }
    }
    public void accept() {
        accepted = ambience.hasPresetAmbience();
        if (accepted) {playbackItem.setAmbience(ambience);}
        close();
    }

// Other Methods
    private void populatetable() {
        AddOrEditAmbienceTable.getItems().clear();
        if (ambience.hasPresetAmbience()) {
            ObservableList<AddOrEditAmbienceTableItem> items = FXCollections.observableArrayList();
            int count = 1;
            for (SoundFile i : ambience.getSessionAmbience()) {
                items.add(new AddOrEditAmbienceTableItem(count, i.getName(), Util.formatdurationtoStringDecimalWithColons(new Duration(i.getDuration()))));
                count++;
            }
            AddOrEditAmbienceTable.setItems(items);
        } else {AddOrEditAmbienceTable.setPlaceholder(new Label("No Ambience For " + playbackItem.getName()));}
        updatestatusbar();
        syncbuttons();
    }
    private void updatestatusbar() {
        AcceptButton.setDisable(! ambience.getPresetAmbienceDuration().greaterThan(playbackitemduration));
        if (ambience.getPresetAmbienceDuration().greaterThan(playbackitemduration)) {
            StatusBar.setText("");
        } else {
            Duration leftoverduration;
            if (ambience.getPresetAmbienceDuration().greaterThan(Duration.ZERO)) {leftoverduration = playbackitemduration.subtract(ambience.getPresetAmbienceDuration());}
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