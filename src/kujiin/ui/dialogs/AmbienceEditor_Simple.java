package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.util.table.AmbienceSong;
import kujiin.xml.SoundFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AmbienceEditor_Simple extends Stage implements Initializable {
    public TableView<AmbienceSong> AmbienceTable;
    public TableColumn<AmbienceSong, String> NameColumn;
    public TableColumn<AmbienceSong, String> DurationColumn;
    public ChoiceBox<String> SessionPartChoiceBox;
    public Button SaveButton;
    public Button CloseButton;
    public Button AddButton;
    public Button RemoveButton;
    public Button PreviewButton;
    public TextField TotalDuration;
    public Button AdvancedButton;
    private ObservableList<AmbienceSong> AmbienceList;
    private ArrayList<SoundFile> SoundList;
    private AmbienceSong selectedambiencesong;
    private SessionPart selectedsessionpart;
    private PreviewFile previewdialog;
    private Duration totalselectedduration;
    private MainController Root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
        DurationColumn.setCellValueFactory(cellDate -> cellDate.getValue().length);
        AmbienceTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tableselectionchanged(newValue));
        ObservableList<String> allnames = FXCollections.observableArrayList();
        allnames.addAll(kujiin.xml.Options.ALLNAMES);
        SessionPartChoiceBox.setItems(allnames);
    }

    public AmbienceEditor_Simple(MainController Root) {
        this.Root = Root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Simple.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions().setStyle(this);
            this.setResizable(false);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog(null));
        } catch (IOException ignored) {}
        SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
        NameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
    }
    public AmbienceEditor_Simple(MainController Root, SessionPart sessionPart) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/AmbienceEditor_Simple.fxml"));
        fxmlLoader.setController(this);
        try {
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            Root.getOptions().setStyle(this);
            this.setResizable(false);
            setTitle("Simple Ambience Editor");
            setOnCloseRequest(event -> closedialog(null));
        } catch (IOException ignored) {}
        setOnShowing(event -> {
            SessionPartChoiceBox.getSelectionModel().select(sessionPart.number);
            selectandloadsessionpart();
        });
        SessionPartChoiceBox.setOnAction(event -> selectandloadsessionpart());
    }

    // Table Methods
    public void tableselectionchanged(AmbienceSong ambienceSong) {selectedambiencesong = ambienceSong;}
    public void selectandloadsessionpart() {
        int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            if (AmbienceList == null) {AmbienceList = FXCollections.observableArrayList();}
            else {AmbienceList.clear();}
            if (SoundList == null) {SoundList = new ArrayList<>();}
            else {SoundList.clear();}
            AmbienceTable.getItems().clear();
            selectedsessionpart = Root.getAllSessionParts(false).get(index);
            if (populateactualambiencetable()) {
                AmbienceTable.setItems(AmbienceList);
            }
            calculatetotalduration();
        }
    }
    public void addfiles(ActionEvent actionEvent) {
        List<File> files = Util.filechooser_multiple(getScene(), "Add Files", null);
        if (files == null || files.isEmpty()) {return;}
        int notvalidfilecount = 0;
        int validfilecount = 0;
        for (File t : files) {
            if (! Util.audio_isValid(t)) {notvalidfilecount++;}
            else {validfilecount++;}
        }
        if (validfilecount > 0) {
            if (Util.list_hasduplicates(files)) {
                if (! new ConfirmationDialog(Root.getOptions(), "Confirmation", "Duplicate Files Detected", "Include Duplicate Files?", "Include", "Discard").getResult()) {
                    files = Util.list_removeduplicates(files);
                }
            }
        }
        for (File i : files) {
            SoundFile soundFile = new SoundFile(i);
            addandcalculateduration(soundFile);
        }
        if (notvalidfilecount > 0) {new InformationDialog(Root.getOptions(), "Information", notvalidfilecount + " Files Were Not Valid And Weren't Added", "");}
    }
    public void addandcalculateduration(SoundFile soundFile) {
        MediaPlayer calculatedurationplayer = new MediaPlayer(new Media(soundFile.getFile().toURI().toString()));
        calculatedurationplayer.setOnReady(() -> {
            soundFile.setDuration(calculatedurationplayer.getTotalDuration().toMillis());
            calculatedurationplayer.dispose();
            SoundList.add(soundFile);
            AmbienceSong tempsong = new AmbienceSong(soundFile);
            AmbienceList.add(tempsong);
            AmbienceTable.getItems().add(tempsong);
            selectedsessionpart.getAmbience().add(soundFile);
            calculatetotalduration();
        });
    }
    public void remove(ActionEvent actionEvent) {
        int index = AmbienceTable.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            SoundFile soundFile = SoundList.get(index);
            selectedsessionpart.getAmbience().remove(soundFile);
            if (new ConfirmationDialog(Root.getOptions(), "Confirmation", null, "Also Delete File " + soundFile.getName() + " From Hard Drive? This Cannot Be Undone", "Delete File", "Keep File").getResult()) {
                if (! soundFile.getFile().delete()) {
                    new ErrorDialog(Root.getOptions(), "Couldn't Delete", null, "Couldn't Delete " + soundFile.getFile().getAbsolutePath() + " Check File Permissions");
                }
            }
            AmbienceTable.getItems().remove(index);
            AmbienceList.remove(index);
            SoundList.remove(index);
            calculatetotalduration();
        }
        else {
            new InformationDialog(Root.getOptions(), "Information", "Nothing Selected", "Select A Table Item To Remove");}
    }
    public void preview(ActionEvent actionEvent) {
        if (selectedambiencesong != null && selectedambiencesong.getFile() != null && selectedambiencesong.getFile().exists()) {
            if (previewdialog == null || !previewdialog.isShowing()) {
                previewdialog = new PreviewFile(selectedambiencesong.getFile(), Root);
                previewdialog.showAndWait();
            }
        }
    }
    public boolean populateactualambiencetable() {
        AmbienceList.clear();
        if (selectedsessionpart != null) {
            try {
                if (selectedsessionpart.getAmbience() == null) {return false;}
                for (SoundFile i : selectedsessionpart.getAmbience().getAmbience()) {
                    SoundList.add(i);
                    AmbienceList.add(new AmbienceSong(i));
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                new InformationDialog(Root.getOptions(), "Information", selectedsessionpart + " Has No Ambience", "Please Add Ambience To " + selectedsessionpart);
                return false;
            }
        } else {
            new InformationDialog(Root.getOptions(), "Information", "No SessionPart Loaded", "Load A SessionPart's Ambience First");
            return false;
        }
    }
    public void calculatetotalduration() {
        totalselectedduration = Duration.ZERO;
        for (AmbienceSong i : AmbienceTable.getItems()) {
            totalselectedduration = totalselectedduration.add(Duration.millis(i.getDuration()));
        }
        TotalDuration.setText(Util.formatdurationtoStringSpelledOut(totalselectedduration, TotalDuration.getLayoutBounds().getWidth()));
    }
    public boolean unsavedchanges() {
        if (SessionPartChoiceBox.getSelectionModel().getSelectedIndex() == -1) {return false;}
        try {
            List<SoundFile> ambiencelist = selectedsessionpart.getAmbience().getAmbience();
            if (SoundList.size() != ambiencelist.size()) {return true;}
            for (SoundFile x : SoundList) {
                if (! ambiencelist.contains(x)) {return true;}
            }
            return false;
        } catch (NullPointerException ignored) {return false;}
    }

    // Dialog Methods
    public void advancedmode(ActionEvent actionEvent) {
        if (unsavedchanges()) {
            if (new ConfirmationDialog(Root.getOptions(), "Unsaved Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save Changes", "Discard").getResult()) {save(null);}
        }
        this.close();
        if (selectedsessionpart != null) {
            new AmbienceEditor_Advanced(Root, selectedsessionpart).show();
        } else {new AmbienceEditor_Advanced(Root).show();}
    }
    public void save(ActionEvent actionEvent) {
        int index = SessionPartChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            for (SoundFile i : SoundList) {
                if (! selectedsessionpart.getAmbience().ambienceexistsinActual(i)) {
                    selectedsessionpart.getAmbience().add(i);}
            }
            Root.getAmbiences().setsessionpartAmbience(selectedsessionpart.number, selectedsessionpart.getAmbience());
            Root.getAmbiences().marshall();
            new InformationDialog(Root.getOptions(), "Saved", "Ambience Saved To " + selectedsessionpart, "");
        } else {
            new InformationDialog(Root.getOptions(), "Cannot Save", "No SessionPart Selected", "Cannot Save");}
    }
    public void closedialog(ActionEvent actionEvent) {
        if (unsavedchanges()) {
            switch (new AnswerDialog(Root.getOptions(), "Unsaved Changes", null, "You Have Unsaved Changes To " + selectedsessionpart, "Save", "Discard", "Cancel").getResult()) {
                case YES: save(null);
                case NO: close(); break;
            }
        } else {close();}
    }
}
