package kujiin.ui.creation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.PlaybackItemAmbience;
import kujiin.xml.SoundFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SelectAvailableAmbience extends StyledStage implements Initializable {
    public TableView<AvailableAmbienceTableItem> AvailableAmbienceTable;
    public TableColumn<AvailableAmbienceTableItem, String> NameColumn;
    public TableColumn<AvailableAmbienceTableItem, String> DurationColumn;
    public Label StatusBar;
    public Button AddButton;
    public Button CancelButton;
    private List<SoundFile> ambiencetoadd = new ArrayList<>();
    private boolean accepted = false;
    private PlaybackItemAmbience AvailableAmbience;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AvailableAmbienceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellData -> cellData.getValue().duration);
        AddButton.setOnAction(event -> accept());
        CancelButton.setOnAction(event -> cancel());
    }
    public SelectAvailableAmbience(PlaybackItemAmbience availableAmbience) {
        try {
            AvailableAmbience = availableAmbience;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/SelectAvailableAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Select Ambience File(s) To Add");
            ObservableList<AvailableAmbienceTableItem> ambienceTableItems = FXCollections.observableArrayList();
            for (SoundFile i : AvailableAmbience.getAmbience()) {
                ambienceTableItems.add(new AvailableAmbienceTableItem(i.getName(), new Duration(i.getDuration())));
            }
            AvailableAmbienceTable.setItems(ambienceTableItems);
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public List<SoundFile> getAmbiencetoadd() {
        return ambiencetoadd;
    }

    // Button Actions
    public void accept() {
        if (! AvailableAmbienceTable.getSelectionModel().getSelectedItems().isEmpty()) {
            for (AvailableAmbienceTableItem i : AvailableAmbienceTable.getSelectionModel().getSelectedItems()) {
                ambiencetoadd.add(AvailableAmbience.getAmbience().get(AvailableAmbienceTable.getSelectionModel().getSelectedItems().indexOf(i)));
            }
            accepted = ! ambiencetoadd.isEmpty();
        }
        close();
    }
    public void cancel() {
        accepted = false;
        close();
    }

// Table Class
    class AvailableAmbienceTableItem {
        StringProperty name;
        StringProperty duration;

        public AvailableAmbienceTableItem(String name, Duration duration) {
            this.name = new SimpleStringProperty(name);
            this.duration = new SimpleStringProperty(Util.formatdurationtoStringSpelledOut(duration, DurationColumn.getWidth()));
        }

    }
}