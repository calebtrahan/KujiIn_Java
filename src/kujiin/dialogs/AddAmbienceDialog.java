package kujiin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.This_Session;
import kujiin.Tools;
import kujiin.util.lib.GuiUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAmbienceDialog extends Stage implements Initializable {
    // TODO Get Slider Working With Preview (Line 124)
    // TODO Maybe Add Volume Slider?
    public TableView<AmbienceSong> AddAmbienceTable;
    public Button addFilesButton;
    public Button RemoveFilesButton;
    public Button PreviewButton;
    public Slider previewSlider;
    public Label previewCurrentTimeLabel;
    public Label previewTotalTimeLabel;
    public Button EditCurrentAmbienceButton;
    public Button CloseButton;
    public ChoiceBox<String> SelectCutChoiceBox;
    public Button ClearTableButton;
    public Button addambiencetocutButton;
    public Slider VolumeSlider;
    private ObservableList<AmbienceSong> songListData = FXCollections.observableArrayList();
    private ObservableList<String> cutnames = FXCollections.observableArrayList();
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    private Media previewmedia = null;
    private MediaPlayer previewmediaplayer = null;
    private boolean filesadded;

    public AddAmbienceDialog(String cutname) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AddAmbienceDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Add Ambience");}
        catch (IOException e) {e.printStackTrace();}
        if (cutname != null) {SelectCutChoiceBox.setValue(cutname);}
        filesadded = false;
    }
    public void addfilestotable(Event event) {
        FileChooser a = new FileChooser();
        List<File> files = a.showOpenMultipleDialog(this);
        ArrayList<File> notvalidfilenames = new ArrayList<>();
        if (files != null) {
            for (File i : files) {
                if (i.getName().endsWith(".mp3") && Tools.getaudioduration(i) != 0.0) {songListData.add(new AmbienceSong(i.getName(), i));}
                else {notvalidfilenames.add(i);}
            }
        }
        if (songListData.size() > 0) {AddAmbienceTable.setItems(songListData);}
        if (notvalidfilenames.size() > 0) {
            StringBuilder c = new StringBuilder();
            for (File i : notvalidfilenames) {
                c.append(i.getName());
                if (i != notvalidfilenames.get(notvalidfilenames.size() - 1)) {c.append("\n");}
            }
            GuiUtils.showinformationdialog("Information", "The Files Weren't Added Because They Are Unsupported", c.toString());
        }
    }
    public void removefilesfromtable(Event event) {
        int index = AddAmbienceTable.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            AddAmbienceTable.getItems().remove(index);
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("No Selection");
            a.setHeaderText("Nothing Selected");
            a.setContentText("Select An Item From The Table To Delete");
            a.showAndWait();
        }
    }
    public void selectionchanged(AmbienceSong ambienceSong) {
        File tempfile = ambienceSong.getFile();
        if (previewmediaplayer != null) {
            previewmediaplayer.stop();
            previewmediaplayer.dispose();
        }
        PreviewButton.setText("Preview");
        previewTotalTimeLabel.setText(ambienceSong.getTotaldurationshort());
        previewmedia = new Media(tempfile.toURI().toString());
        previewmediaplayer = new MediaPlayer(previewmedia);
    }
    public void previewselectedfile(Event event) {
        if (previewmedia != null && previewmediaplayer != null) {
            if (PreviewButton.getText().equals("Preview")) {
                previewmediaplayer.play();
                VolumeSlider.valueProperty().bindBidirectional(previewmediaplayer.volumeProperty());
                previewSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                    double position = previewSlider.getValue();
                    Duration seektothis = previewmediaplayer.getTotalDuration().multiply(position);
                    previewmediaplayer.seek(seektothis);
                });
                previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    previewCurrentTimeLabel.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
                    updatePositionSlider(previewmediaplayer.getCurrentTime());
                });
                PreviewButton.setText(" Stop ");
            } else {
                previewmediaplayer.stop();
                PreviewButton.setText("Preview");
            }
        } else {
            GuiUtils.showinformationdialog("Information", "Nothing To Preview", "Select An Item From The Table");
        }
    }
    public void updatePositionSlider(Duration currenttime) {
        if (previewSlider.isValueChanging()) {return;}
        Duration total = previewmediaplayer.getTotalDuration();
        if (total == null || currenttime == null) {previewSlider.setValue(0);}
        else {previewSlider.setValue(currenttime.toMillis() / total.toMillis());}
    }
    public void editcurrentambience(Event event) {
        EditAmbienceDialog editAmbienceDialog = new EditAmbienceDialog();
        editAmbienceDialog.showAndWait();
        close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cutnames.addAll(This_Session.allnames);
        SelectCutChoiceBox.setItems(cutnames);
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        AddAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionchanged(newValue));
        this.setOnCloseRequest(event -> close());
    }

    public void closebuttonpressed(Event event) {close();}
    public void cleartable(Event event) {
        if (GuiUtils.getanswerdialog("Confirmation", "Clear Table", "Remove All Items From This Table?")) {
            AddAmbienceTable.getSelectionModel().getTableView().getItems().clear();
            previewmedia = null;
            previewmediaplayer = null;
            songListData.clear();
        }
    }

    @Override
    public void close() {
        if (AddAmbienceTable.getSelectionModel().getTableView().getItems().size() > 0 && ! filesadded) {
            if (! GuiUtils.getanswerdialog("Confirmation", "Files Not Added", "Close Dialog Without Adding These Files")) {return;}
        }
        if (previewmediaplayer != null) {
            previewmediaplayer.stop();
            previewmediaplayer.dispose();
        }
        super.close();
    }

    public void AddAmbienceToCut(Event event) {
        int index = This_Session.allnames.indexOf(SelectCutChoiceBox.getValue());
        if (index != -1 && songListData.size() != 0) {
            String name = This_Session.allnames.get(index);
            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
            b.setTitle("Confirmation");
            b.setHeaderText("Add All Of This Ambience?");
                    b.setContentText("Really Add All Of These Files To " + name + "'s Ambience?");
            Optional<ButtonType> c = b.showAndWait();
            if (c.isPresent() && c.get() == ButtonType.OK) {
                ArrayList<File> filestoadd = new ArrayList<>();
                for (AmbienceSong i : songListData) {filestoadd.add(i.getFile());}
                Service<Void> copyservice = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                for (File i : filestoadd) {
                                    File filetocopy =  new File(This_Session.directoryambience, name + "/" + i.getName());
                                    FileUtils.copyFile(i, filetocopy);
                                    updateMessage(String.format("(%d/%d) -> Copying %s", filestoadd.indexOf(i) + 1, filestoadd.size(), filetocopy.getName()));
                                }
                                return null;
                }};}};
                Alert copyingfilesdialog = new Alert(Alert.AlertType.INFORMATION);
                copyingfilesdialog.setTitle("Copying Files");
                copyingfilesdialog.setHeaderText("Please Wait...");
                copyservice.setOnSucceeded(event1 -> {
                    filesadded = true;
                    System.out.println("Succeeded!");
                    copyingfilesdialog.contentTextProperty().unbind();
                    copyingfilesdialog.close();
                    GuiUtils.showinformationdialog("Information", "Success", "Files Added To " + name + "'s Ambience");
                    this.close();
                });
                copyservice.setOnFailed(event2 -> {
                    System.out.println("Failed!");
                    copyingfilesdialog.contentTextProperty().unbind();
                    copyingfilesdialog.close();
                    GuiUtils.showerrordialog("Error", "Failure", "Couldn't Add Files To " + name + "'s Ambience");
                    this.close();
                });
                copyingfilesdialog.contentTextProperty().bind(copyservice.messageProperty());
                copyingfilesdialog.show();
                copyservice.start();
            }
        } else {
            if (songListData.size() == 0) {GuiUtils.showinformationdialog("Information", "No Ambience To Add", "Add Some Ambience To The Table First");}
            else {GuiUtils.showinformationdialog("Information", "No Cut Selected", "Select A Cut To Add This Ambience To");}
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


        public File getFile() {
            return file;
        }

        public String getTotaldurationshort() {
            return totaldurationshort;
        }
    }
}
