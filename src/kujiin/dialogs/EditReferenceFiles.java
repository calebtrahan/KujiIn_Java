package kujiin.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import kujiin.This_Session;
import kujiin.util.lib.FileUtils;
import kujiin.util.lib.GuiUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;

public class EditReferenceFiles extends Stage implements Initializable {

    public ChoiceBox<String> CutNamesChoiceBox;
    public ChoiceBox<String> CutVariationsChoiceBox;
    public TextArea MainTextArea;
    public Button CloseButton;
    public Label SelectReferenceFileLabel;
    public Label StatusBar;
    public Button SaveButton;
    public Button LoadButton;
    private ObservableList<String> cutnames;
    private ObservableList<String> variations;
    private File htmldirectory = new File(This_Session.directoryreference, "html");
    private File txtdirectory = new File(This_Session.directoryreference, "txt");
    private File selectedfile;
    private String selectedcut;
    private String selectedvariation;

    public EditReferenceFiles() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/EditReferenceFiles.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
        catch (IOException e) {e.printStackTrace();}
        MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
        cutnames = FXCollections.observableArrayList();
        variations = FXCollections.observableArrayList();
        cutnames.addAll(This_Session.allnames);
        variations.addAll(Arrays.asList("html", "txt"));
        CutNamesChoiceBox.setItems(cutnames);
        CutVariationsChoiceBox.setItems(variations);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void closewindow(Event event) {
        // Check If Unsaved Text
        this.close();
    }

// Text Area Methods
    private boolean unsavedchanges() {
        if (selectedfile == null) {return false;}
        try {
            Scanner sc1 = new Scanner(selectedfile);
            Scanner sc2 = new Scanner(MainTextArea.getText());
            while (sc1.hasNext() && sc2.hasNext()) {
                if (! sc1.next().equals(sc2.next())) {return false;}
            }
            return true;
        } catch (FileNotFoundException | NullPointerException ignored) {return false;}
    }
    private void textchanged() {
    }
    private void cleartextarea() {MainTextArea.clear();}
    private void resetvariationsbox() {
        CutVariationsChoiceBox.getItems().clear();
        CutVariationsChoiceBox.setItems(variations);
    }

// File Methods
    public void savefile(ActionEvent actionEvent) {
        if (FileUtils.writeFileContents(selectedfile, FileUtils.getFileContents(selectedfile))) {
            String text = selectedcut + "'s Reference File (" + selectedvariation + " Variation) Has Been Saved";
            GuiUtils.showtimedmessage(StatusBar, text, 5000);
        } else {GuiUtils.showerrordialog("Error", "Couldn't Save", "Does The File Exist/Do You Have Access To It?");}
    }
    public void loadnewfile(ActionEvent actionEvent) {
        if (! CutNamesChoiceBox.getValue().equals("") && ! CutVariationsChoiceBox.getValue().equals("")) {
            if (unsavedchanges()) {
                if (GuiUtils.getanswerdialog("Confirmation", "Document Has Unsaved Changes", "Save These Changes Before Loading A Different File?")) {savefile(null);}
                else {return;}
            }
            selectedcut = CutNamesChoiceBox.getValue();
            selectedvariation = CutVariationsChoiceBox.getValue();
            SelectReferenceFileLabel.setText(String.format("%s's Reference File (%s Variation)", selectedcut, selectedvariation));
            if (selectedvariation.equals("html")) {selectedfile = new File(htmldirectory, selectedcut + ".html");}
            else {selectedfile = new File(txtdirectory, selectedcut + ".txt");}
            MainTextArea.setText(FileUtils.getFileContents(selectedfile));
        } else {
            if (CutNamesChoiceBox.getValue().equals("")) {GuiUtils.showinformationdialog("Information", "No Cut Selected", "Select A Cut To Load");}
            else {GuiUtils.showinformationdialog("Information", "No Variation Selected", "Select A Variation To Load");}
            SelectReferenceFileLabel.setText("Select A Cut Name And Variation And Press 'Load'");
        }
    }

}
