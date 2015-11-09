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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kujiin.Session;
import kujiin.Tools;
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
    private ObservableList<AmbienceSong> songListData = FXCollections.observableArrayList();
    private ObservableList<String> cutnames = FXCollections.observableArrayList();
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    private Media previewmedia = null;
    private MediaPlayer previewmediaplayer = null;

    public AddAmbienceDialog(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AddAmbienceDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Add Ambience");}
        catch (IOException e) {e.printStackTrace();}
    }

    public AddAmbienceDialog(Parent parent, String cutname) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/AddAmbienceDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Add Ambience");}
        catch (IOException e) {e.printStackTrace();}
        SelectCutChoiceBox.setValue(cutname);
    }

    public void addfilestotable(Event event) {
        FileChooser a = new FileChooser();
        List<File> files = a.showOpenMultipleDialog(this);
        ArrayList<File> notvalidfilenames = new ArrayList<>();
        if (files != null) {
            for (File i : files) {
                if (i.getName().endsWith(".mp3") && Tools.getaudioduration(i) != 0.0) {
                    songListData.add(new AmbienceSong(i.getName(), i));
                } else {
                    notvalidfilenames.add(i);
                }
            }
        }
        AddAmbienceTable.setItems(songListData);
        if (notvalidfilenames.size() > 0) {
            Alert b = new Alert(Alert.AlertType.WARNING);
            b.setTitle("Couldn't Add All Files");
            b.setHeaderText("These Files Couldn't Be Added:");
            StringBuilder c = new StringBuilder();
            for (File i : notvalidfilenames) {
                c.append(i.getName());
                if (i != notvalidfilenames.get(notvalidfilenames.size() - 1)) {
                    c.append("\n");
                }
            }
            b.setContentText(c.toString());
            b.showAndWait();
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
                previewmediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    previewCurrentTimeLabel.setText(Tools.formatlengthshort((int) newValue.toSeconds()));
//                    previewSlider.setValue(previewmediaplayer.getCurrentTime().toMillis() / previewmediaplayer.getTotalDuration().toMillis());
                });
                PreviewButton.setText(" Stop ");
            } else {
                previewmediaplayer.stop();
                PreviewButton.setText("Preview");
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("No Item Selected");
            a.setHeaderText("Nothing To Preview");
            a.setContentText("Select An Item From The Table To Preview");
            a.showAndWait();
        }
    }

    public void editcurrentambience(Event event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cutnames.addAll(Session.allnames);
        SelectCutChoiceBox.setItems(cutnames);
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        AddAmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionchanged(newValue));
        this.setOnCloseRequest(event -> close());
    }

    public void closebuttonpressed(Event event) {this.close();}

    public void cleartable(Event event) {
        AddAmbienceTable.getSelectionModel().getTableView().getItems().clear();
        previewmedia = null;
        previewmediaplayer = null;
        songListData.clear();
    }

    @Override
    public void close() {
        if (previewmediaplayer != null) {
            previewmediaplayer.stop();
            previewmediaplayer.dispose();
        }
        super.close();
    }

    public void AddAmbienceToCut(Event event) {
        int index = Session.allnames.indexOf(SelectCutChoiceBox.getValue());
        if (index != -1 && songListData.size() != 0) {
            String name = Session.allnames.get(index);
            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
            b.setTitle("Confirmation");
            b.setHeaderText("Add All Of This Ambience?");
                    b.setContentText("Really Add All Of These Files To " + name + "'s Ambience?");
            Optional<ButtonType> c = b.showAndWait();
            if (c.isPresent() && c.get() == ButtonType.OK) {
                ArrayList<File> filestoadd = new ArrayList<>();
                boolean addambiencestatus = true;
                for (AmbienceSong i : songListData) {
                    filestoadd.add(i.getFile());
                }
                for (File i : filestoadd) {
                    File destinationfile = new File(Session.directoryambience, name + "/" + i.getName());
                    try {
                        FileUtils.copyFile(i, destinationfile);
                    } catch (IOException e) {
                        addambiencestatus = false;
                        e.printStackTrace();
                    }
                }
                if (addambiencestatus) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Ambience Added");
                    a.setHeaderText("Success!");
                    a.setContentText("Your Selected Ambience Files Have Been Added To " + name + "'s Ambience");
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Ambience Added");
                    a.setHeaderText("Failure!");
                    a.setContentText("An Error Occured While Adding Files To " + name + "'s Ambience. Check File Permissions.");
                    a.showAndWait();
                }
            }
        } else {
            if (songListData.size() == 0) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Nothing To Add");
                a.setHeaderText("No Ambience To Add");
                a.setContentText("Add Some Ambience To The Table First.");
                a.showAndWait();
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("No Cut Selected");
                a.setHeaderText("Cannot Add Ambience");
                a.setContentText("Select A Cut To Add This Ambience To");
                a.showAndWait();
            }
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
