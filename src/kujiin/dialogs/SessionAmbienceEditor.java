package kujiin.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SessionAmbienceEditor extends Stage implements Initializable {
    public Button RightArrow;
    public Button LeftArrow;
    public Label CutSelectionLabel;
    public Button NewAmbienceAddButton;
    public Button NewAmbienceRemoveButton;
    public Button NewAmbiencePreviewButton;
    public Slider PreviewSlider;
    public Label PreviewCurrentTime;
    public Label PreviewTotalTime;
    public TextField PreviewFileName;
    public Slider PreviewVolumeSlider;
    public Button PreviewStartButton;
    public Button CloseButton;
    public Button CurrentAmbienceRemoveButton;
    public Button CurrentAmbiencePreviewButton;
    public ChoiceBox<String> CutSelectionBox;
    public TableView<AmbienceSong> NewAmbienceTable;
    public TableColumn<AmbienceSong, String> New_NameColumn;
    public TableColumn<AmbienceSong, String> New_DurationColumn;
    public TableView<AmbienceSong> CurrentAmbienceTable;
    public TableColumn<AmbienceSong, String> Current_NameColumn;
    public TableColumn<AmbienceSong, String> Current_DurationColumn;
    private String selectedcutname = null;
    private Media previewmedia = null;
    private MediaPlayer previewmediaplayer = null;
    private ObservableList<AmbienceSong> new_songlist = FXCollections.observableArrayList();
    private ObservableList<AmbienceSong> current_songlist = FXCollections.observableArrayList();
    private AmbienceSong selected_new_ambiencesong;
    private AmbienceSong selected_current_ambiencesong;

    // TODO Continue Setting This Up
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        New_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        New_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        Current_NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        Current_DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        NewAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> newselectionchanged(newValue));
        CurrentAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> currentselectionchanged(newValue));
        ObservableList<String> allnames = FXCollections.observableArrayList();
        allnames.addAll(This_Session.allnames);
        CutSelectionBox.setItems(allnames);
        this.setOnCloseRequest(event -> close());
    }

    public SessionAmbienceEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SessionAmbienceEditor.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Session Ambience Editor");}
        catch (IOException e) {e.printStackTrace();}
        CutSelectionBox.setOnAction(event -> selectandloadcut());
    }

// Transfer Methods
    public void rightarrowpressed(ActionEvent actionEvent) {
        // Transfer To Current Cut
    }
    public void leftarrowpressed(ActionEvent actionEvent) {
        // Transfer To Temp Folder (And Add To TableView)
    }

// New Ambience Methods
    public void addfilestonewambience(ActionEvent actionEvent) {
        FileChooser a = new FileChooser();
        List<File> files = a.showOpenMultipleDialog(this);
        ArrayList<File> notvalidfilenames = new ArrayList<>();
        if (files != null) {
            for (File i : files) {
                if (i.getName().endsWith(".mp3") && Tools.getaudioduration(i) != 0.0) {new_songlist.add(new AmbienceSong(i.getName(), i));}
                else {notvalidfilenames.add(i);}
            }
        }
        if (new_songlist.size() > 0) {NewAmbienceTable.setItems(new_songlist);}
        if (notvalidfilenames.size() > 0) {
            StringBuilder c = new StringBuilder();
            for (File i : notvalidfilenames) {
                c.append(i.getName());
                if (i != notvalidfilenames.get(notvalidfilenames.size() - 1)) {c.append("\n");}
            }
            GuiUtils.showinformationdialog("Information", "The Files Weren't Added Because They Are Unsupported", c.toString());
        }
    }
    public void removefromnewambience(ActionEvent actionEvent) {
        int index = NewAmbienceTable.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            NewAmbienceTable.getItems().remove(index);
        } else {
            GuiUtils.showinformationdialog("Information", "Nothing Selected", "Select A Table Item To Remove");
        }
    }
    public void previewnewambience(ActionEvent actionEvent) {
        if (selected_new_ambiencesong != null && NewAmbienceTable.getItems().size() > 0) {
            previewmedia = new Media(selected_new_ambiencesong.getFile().toURI().toString());
            previewmediaplayer = new MediaPlayer(previewmedia);
            PreviewTotalTime.setText(selected_new_ambiencesong.getTotaldurationshort());
            PreviewFileName.setText(selected_new_ambiencesong.name.toString());
            PreviewCurrentTime.setText("00:00");
        }
    }
    public void newselectionchanged(AmbienceSong ambiencesong) {
        selected_new_ambiencesong = ambiencesong;
    }

// Current Ambience Methods
    public void selectandloadcut() {

    }
    public void removecurrentambience(ActionEvent actionEvent) {
    }
    public void previewcurrentambience(ActionEvent actionEvent) {

    }
    public void currentselectionchanged(AmbienceSong ambiencesong) {}

// Other Methods
    public void preview(ActionEvent actionEvent) {
        // TODO This Starts & Stops The File Passed Into The Preview Player
        if (previewmediaplayer != null && previewmedia != null) {
            switch (previewmediaplayer.getStatus()) {
                case PLAYING:
                    System.out.println("Playing");
                    previewmediaplayer.stop();
                    PreviewStartButton.setText("Start");
                default:
                    System.out.println("Default");
                    previewmediaplayer.play();
                    PreviewSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                        double position = PreviewSlider.getValue();
                        Duration seektothis = previewmediaplayer.getTotalDuration().multiply(position);
                        previewmediaplayer.seek(seektothis);
                    });
                    previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                        PreviewCurrentTime.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
                        updatePositionSlider(previewmediaplayer.getCurrentTime());
                    });
                    PreviewVolumeSlider.valueProperty().bindBidirectional(previewmediaplayer.volumeProperty());
                    PreviewStartButton.setText("Stop");
            }
        } else {GuiUtils.showinformationdialog("Information", "Nothing To Preview", "Select A Table Item And Press Preview");}
    }
    public void updatePositionSlider(Duration currenttime) {
        if (PreviewSlider.isValueChanging()) {return;}
        Duration total = previewmediaplayer.getTotalDuration();
        if (total == null || currenttime == null) {PreviewSlider.setValue(0);}
        else {PreviewSlider.setValue(currenttime.toMillis() / total.toMillis());}
    }
    public void closebuttonpressed(ActionEvent actionEvent) {close();}
    @Override
    public void close() {
        super.close();
        if (previewmediaplayer != null && previewmediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            previewmediaplayer.stop();
            previewmediaplayer.dispose();
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