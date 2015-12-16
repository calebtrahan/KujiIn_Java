package kujiin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.util.lib.FileUtils;
import kujiin.util.lib.GuiUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    public Slider VolumeSlider;
    private ObservableList<AmbienceSong> songListData = FXCollections.observableArrayList();
    private String selectedcutname = null;
    private Media previewmedia = null;
    private MediaPlayer previewmediaplayer = null;

    public EditAmbienceDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/EditAmbienceDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Edit Current Ambience");}
        catch (IOException e) {e.printStackTrace();}
    }

// Dialog Methods
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


// Preview Methods
    public void preview(Event event) {
        if (previewmedia != null && previewmediaplayer != null) {
            if (PreviewButton.getText().equals("Start")) {
                previewmediaplayer.play();
                VolumeSlider.valueProperty().unbind();
                VolumeSlider.valueProperty().bindBidirectional(previewmediaplayer.volumeProperty());
                PreviewSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                    double position = PreviewSlider.getValue();
                    Duration seektothis = previewmediaplayer.getTotalDuration().multiply(position);
                    previewmediaplayer.seek(seektothis);
                });
                previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    PreviewCurrentTimeLabel.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
                    updatePositionSlider(previewmediaplayer.getCurrentTime());
                });
                PreviewButton.setText(" Stop ");
            } else {
                previewmediaplayer.stop();
                PreviewButton.setText("Start");
            }
        } else {
            GuiUtils.showinformationdialog("Information", "Nothing To Preview", "Select A File To Preview");
        }
    }
    public void updatePositionSlider(Duration currenttime) {
        if (PreviewSlider.isValueChanging()) {return;}
        Duration total = previewmediaplayer.getTotalDuration();
        if (total == null || currenttime == null) {PreviewSlider.setValue(0);}
        else {PreviewSlider.setValue(currenttime.toMillis() / total.toMillis());}
    }

// Table View Methods
    public void removefromTable(Event event) {
        int index = AmbienceListTableView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            String filename = songListData.get(index).getName();
            if (GuiUtils.getanswerdialog("Confirmation", String.format("Remove '%s' From %s's Ambience?", filename, selectedcutname), "This Cannot Be Undone")) {
                if (songListData.get(index).getFile().delete()) {
                    AmbienceListTableView.getItems().remove(index);
                    songListData.remove(index);
                    populateCutTableView(null);
                } else {GuiUtils.showerrordialog("Error", "Couldn't Delete '" + filename + "'", "Check File Permissions");}
            }
        } else {GuiUtils.showinformationdialog("Information", "Nothing To Remove", "Select An Item To Remove");}
    }
    public void addToTable(Event event) {
        AddAmbienceDialog c = new AddAmbienceDialog(selectedcutname);
        c.showAndWait();
        getfiles();
    }
    public boolean getfiles() {
        if (selectedcutname != null) {
            File thisdirectory = new File(This_Session.directoryambience, selectedcutname);
            try {
                for (File i : thisdirectory.listFiles()) {
                    if (FileUtils.validaudiofile(i)) {songListData.add(new AmbienceSong(i.getName(), i));}
                }
                return true;
            } catch (NullPointerException e) {
                GuiUtils.showinformationdialog("Information", selectedcutname + " Has No Ambience", "Please Add Ambience To " + selectedcutname);
                return false;
            }
        } else {
            GuiUtils.showinformationdialog("Information", "No Cut Loaded", "Load A Cut's Ambience First");
            return false;
        }
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
        try {
            songListData.clear();
            AmbienceListTableView.getItems().clear();
            // Clear Old Data (Table + Observable Lists)
            int index = This_Session.allnames.indexOf(CutSelectChoiceBox.getValue());
            selectedcutname = This_Session.allnames.get(index);
            if (getfiles()) {
                AmbienceListTableView.getItems().addAll(songListData);
                CurrentlySelectedLabel.setText(String.format("Now Displaying: %s's Ambience", selectedcutname));
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {GuiUtils.showinformationdialog("Information", "No Cut Selected", "Select A Cut To Load");}
    }

// Subclass For Table
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
