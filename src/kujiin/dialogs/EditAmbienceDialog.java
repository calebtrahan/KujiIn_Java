package kujiin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import kujiin.This_Session;
import kujiin.Tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditAmbienceDialog extends Stage implements Initializable {
    public TableView<AmbienceSong> AmbienceListTableView;
    public Label CurrentlySelectedLabel;
    public ChoiceBox<String> CutSelectChoiceBox;
    public Button LoadCutsAmbienceButton;
    public Button AddButton;
    public Button RemoveButton;
    public Button PreviewButton;
    public Label PreviewCurrentTimeLabel;
    public Slider PreviewSlider;
    public Label PreviewTotalTimeLabel;
    public Button CloseButton;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public Label PreviewNameLabel;
    private ObservableList<AmbienceSong> songListData = FXCollections.observableArrayList();
    private String selectedcutname = null;
    private Media previewmedia = null;
    private MediaPlayer previewmediaplayer = null;

    public EditAmbienceDialog(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/EditAmbienceDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Edit Current Ambience");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> allnames = FXCollections.observableArrayList();
        allnames.addAll(This_Session.allnames);
        CutSelectChoiceBox.setItems(allnames);
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        AmbienceListTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionchanged(newValue));
        this.setOnCloseRequest(event -> close());
    }

    public void closewindow(Event event) {this.close();}

    public void preview(Event event) {
        if (previewmedia != null && previewmediaplayer != null) {
            if (PreviewButton.getText().equals("Start")) {
                previewmediaplayer.play();
                previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    PreviewCurrentTimeLabel.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
//                    previewSlider.setValue(previewmediaplayer.getCurrentTime().toMillis() / previewmediaplayer.getTotalDuration().toMillis());
                });
                PreviewButton.setText(" Stop ");
            } else {
                previewmediaplayer.stop();
                PreviewButton.setText("Start");
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("No Item Selected");
            a.setHeaderText("Nothing To Preview");
            a.setContentText("Select An Item From The Table To Preview");
            a.showAndWait();
        }
    }

    public void removefromTable(Event event) {
        int index = AmbienceListTableView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
            b.setTitle("Removal Confirmation");
            b.setHeaderText("Remove This Audio File?");
            String filename = songListData.get(index).getName();
            b.setContentText(String.format("Really Remove %s From %s's Ambience?", filename, selectedcutname));
            Optional<ButtonType> c = b.showAndWait();
            if (c.get() == ButtonType.OK) {
                songListData.get(index).getFile().delete();
                AmbienceListTableView.getItems().remove(index);
                songListData.remove(index);
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Nothing To Remove");
            a.setHeaderText("Nothing To Remove");
            a.setContentText("Select An Item From The Table To Remove");
            a.showAndWait();
        }
    }

    @Override
    public void close() {
        super.close();
        if (previewmediaplayer != null && previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            previewmediaplayer.stop();
            previewmediaplayer.dispose();
        }
    }

    public void addToTable(Event event) {
        if (selectedcutname != null) {
            AddAmbienceDialog c = new AddAmbienceDialog(null, selectedcutname);
            c.showAndWait();
            getfiles();
        } else {
            AddAmbienceDialog b = new AddAmbienceDialog(null);
            b.showAndWait();
            getfiles();
        }
    }

    public boolean getfiles() {
        boolean success;
        if (selectedcutname != null) {
            File thisdirectory = new File(This_Session.directoryambience, selectedcutname);
            try {
                for (File i : thisdirectory.listFiles()) {
                    // Test Here If A Valid Audio Ambient File
                    songListData.add(new AmbienceSong(i.getName(), i));
                }
                success = true;
            } catch (NullPointerException e) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("No Ambience");
                a.setHeaderText("Cannot Load Ambience");
                a.setContentText(selectedcutname + " Does Not Contain Any Ambience. Please Add Ambience By " +
                        "Clicking The 'Add Ambience' Button");
                a.showAndWait();
                success = false;
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Nothing Loaded");
            a.setHeaderText("No Cut's Ambience Loaded");
            a.setContentText("Please Load A Cut Before Using This Feature");
            a.showAndWait();
            success = false;
        }
        return success;
    }

    public void selectionchanged(AmbienceSong ambienceSong) {
        try {
            File tempfile = ambienceSong.getFile();
            if (previewmediaplayer != null) {
                previewmediaplayer.stop();
                previewmediaplayer.dispose();
            }
            PreviewNameLabel.setText(ambienceSong.getName());
            PreviewButton.setText("Start");
            PreviewTotalTimeLabel.setText(ambienceSong.getTotaldurationshort());
            previewmedia = new Media(tempfile.toURI().toString());
            previewmediaplayer = new MediaPlayer(previewmedia);
        } catch (NullPointerException ignored) {}
    }

    public void populateCutTableView(Event event) {
        songListData.clear();
        AmbienceListTableView.getItems().clear();
        // Clear Old Data (Table + Observable Lists)
        int index = This_Session.allnames.indexOf(CutSelectChoiceBox.getValue());
        if (index != 1) {
            selectedcutname = This_Session.allnames.get(index);
            if (getfiles()) {
                AmbienceListTableView.getItems().addAll(songListData);
                CurrentlySelectedLabel.setText(String.format("Now Displaying: %s's Ambience", selectedcutname));
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("No Selection");
            a.setHeaderText("Cannot Load Ambience");
            a.setContentText("Select A Cut From The Drop Down Menu");
            a.showAndWait();
        }
    }

    class AmbienceSong {
        private StringProperty name;
        private StringProperty length;
        private File file;
        private String totaldurationshort;

        public AmbienceSong(String name, File file) {
            this.name = new SimpleStringProperty(name);
            this.file = file;
            double duration = Tools.getaudioduration(file);
            totaldurationshort = Tools.formatlengthshort((int) duration);
            this.length = new SimpleStringProperty(totaldurationshort);
        }

        public String getName() {
            return name.getValue();
        }

        public File getFile() {
            return file;
        }

        public String getTotaldurationshort() {
            return totaldurationshort;
        }
    }
}
