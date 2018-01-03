package kujiin.ui.dialogs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.*;
import kujiin.ui.reference.DisplayReference;
import kujiin.util.Util;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static kujiin.xml.Preferences.ALLNAMES;
import static kujiin.xml.Preferences.PROGRAM_ICON;

public class EditReferenceFiles extends StyledStage {
    public ChoiceBox<String> SessionPartNamesChoiceBox;
    public TextArea MainTextArea;
    public Button CloseButton;
    public Label StatusBar;
    public Button SaveButton;
    public Button PreviewButton;
    public RadioButton HTMLVariation;
    public RadioButton TEXTVariation;
    private File selectedfile;
    private ArrayList<Integer> userselectedindexes;
    private ReferenceType referenceType;
    private MainController Root;

    public EditReferenceFiles(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/reference/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            initModality(Modality.WINDOW_MODAL);
            setTitle("Reference Files Editor");
            ObservableList<String> sessionpartnames = FXCollections.observableArrayList();
            sessionpartnames.addAll(Preferences.ALLNAMES);
            userselectedindexes = new ArrayList<>();
            SessionPartNamesChoiceBox.setItems(sessionpartnames);
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> textchanged());
            SessionPartNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != null) userselectedindexes.add(oldValue.intValue());
                if (newValue != null) textchanged();
            });
            HTMLVariation.setDisable(getselectedindex() == -1);
            TEXTVariation.setDisable(getselectedindex() == -1);
            if (referenceType == null) {referenceType = Preferences.DEFAULT_REFERENCE_TYPE_OPTION;}
            HTMLVariation.setSelected(referenceType == ReferenceType.html);
            TEXTVariation.setSelected(referenceType == ReferenceType.txt);
            referenceType = Root.getPreferences().getSessionOptions().getReferencetype();
            PreviewButton.setDisable(true);
            SaveButton.setDisable(true);
            String referencename = referenceType.name();
            this.setOnCloseRequest(event -> {
                if (unsavedchanges()) {
                    switch (new AnswerDialog(Root.getPreferences(), "Confirmation", null, SessionPartNamesChoiceBox.getValue() + " " + referencename + " Variation Has Unsaved Changes",
                            "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                        case YES:
                            saveselectedfile();
                            break;
                        case NO:
                            break;
                        case CANCEL:
                            event.consume();
                    }
                }
            });
            SessionPartNamesChoiceBox.setOnAction(event -> newsessionpartselected());
            HTMLVariation.setOnAction(event -> htmlselected());
            TEXTVariation.setOnAction(event -> textselected());
            PreviewButton.setOnAction(event -> preview());
            SaveButton.setOnAction(event -> saveselectedfile());
            CloseButton.setOnAction(event -> close());
        } catch (IOException e) {new ExceptionDialog(e).showAndWait();}
    }

    // Text Area Methods
    private boolean unsavedchanges() {
        try {
            return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
        } catch (Exception e) {return false;}
    }
    private void newsessionpartselected() {
        HTMLVariation.setDisable(getselectedindex() == -1);
        TEXTVariation.setDisable(getselectedindex() == -1);
        if (userselectedindexes.size() > 0 && selectedfile != null && unsavedchanges()) {
            Util.AnswerType answerType = new AnswerDialog(Root.getPreferences(), "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                    "Save And Close", "Close Without Saving", "Cancel").getResult();
            switch (answerType) {
                case YES:
                    saveselectedfile();
                    break;
                case NO:
                    break;
                case CANCEL:
                    SessionPartNamesChoiceBox.getSelectionModel().select(userselectedindexes.get(userselectedindexes.size() - 1));
                    return;
            }
        }
        loadselectedfile();
    }
    private void textchanged() {
        if (referenceType != null && getselectedindex() != -1 && selectedfile != null) {
            boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
            PreviewButton.setDisable(! hasvalidtext || referenceType == ReferenceType.txt);
            SaveButton.setDisable(MainTextArea.getText() == null || Util.file_getcontents(selectedfile).equals(MainTextArea.getText()));
            switch (referenceType) {
                case html:
                    if (MainTextArea.getText() != null && Util.String_validhtml(MainTextArea.getText())) {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                    else {StatusBar.setTextFill(Color.RED); StatusBar.setText("Not Valid .html");}
                    break;
                case txt:
                    if (MainTextArea.getText() != null && MainTextArea.getText().length() == 0) {StatusBar.setTextFill(Color.RED); StatusBar.setText("No Text Entered");}
                    else {StatusBar.setTextFill(Color.BLACK); StatusBar.setText("");}
                    break;
            }
        } else {
            MainTextArea.clear();
            StatusBar.setTextFill(Color.RED);
            StatusBar.setText("No SessionPart Selected");
            new Timeline(new KeyFrame(Duration.millis(3000), ae -> StatusBar.setText(""))).play();
        }
    }

    // Button Methods
    private void htmlselected() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(Root.getPreferences(), "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                    "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                case YES:
                    saveselectedfile();
                    break;
                case NO:
                    break;
                case CANCEL:
                    HTMLVariation.setSelected(false);
                    TEXTVariation.setSelected(true);
                    return;
            }
        }
        // Test If Unsaved Changes Here
        TEXTVariation.setSelected(false);
        PreviewButton.setDisable(! HTMLVariation.isSelected());
        referenceType = ReferenceType.html;
        selectnewfile();
        loadselectedfile();
    }
    private void textselected() {
        if (unsavedchanges()) {
            switch (new AnswerDialog(Root.getPreferences(), "Confirmation", null, "Previous Reference File Has Unsaved Changes",
                    "Save And Close", "Close Without Saving", "Cancel").getResult()) {
                case YES:
                    saveselectedfile();
                    break;
                case NO:
                    break;
                case CANCEL:
                    HTMLVariation.setSelected(true);
                    TEXTVariation.setSelected(false);
                    return;
            }
        }
        // Test If Unsaved Changes Here
        HTMLVariation.setSelected(false);
        PreviewButton.setDisable(! HTMLVariation.isSelected());
        referenceType = ReferenceType.txt;
        selectnewfile();
        loadselectedfile();
    }
    private void preview() {
        if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && referenceType == ReferenceType.html) {
            if (! Util.String_validhtml(MainTextArea.getText())) {
                if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Html Code In Text Area Is Not Valid HTML", "Preview Anyways", "Cancel").getResult()) {return;}
            }
            new DisplayReference(MainTextArea.getText()).showAndWait();
        }
    }

    // Utility Methods
    private int getselectedindex() {
        return SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex();
    }
    private void saveselectedfile() {
        if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
            String text = ALLNAMES.get(getselectedindex()) + "'s Reference File (" + referenceType.toString() + " Variation) Has Been Saved";
            new InformationDialog(Root.getPreferences(), "Changes Saved", text, "");
        } else {
            new ErrorDialog(Root.getPreferences(), "Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
    }
    private void loadselectedfile() {
        int index = getselectedindex();
        if (index != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
//            selectedplaybackitem = Root.getAllSessionParts(false).getplaybackItemGoals(index);
            selectnewfile();
            String contents = Util.file_getcontents(selectedfile);
            MainTextArea.setText(contents);
            PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
            StatusBar.setTextFill(Color.BLACK);
            StatusBar.setText("");
            SaveButton.setDisable(true);
        } else {
            if (getselectedindex() == -1) {
                new InformationDialog(Root.getPreferences(), "Information", "No SessionPart Selected", "Select A SessionPart To Load");}
            else {
                new InformationDialog(Root.getPreferences(), "Information", "No Variation Selected", "Select A Variation To Load");}
            PreviewButton.setDisable(true);
        }
    }
    private void selectnewfile() {
        int index = getselectedindex();
        if (referenceType == null || index == -1) {selectedfile = null; return;}
        switch (referenceType) {
            case html:
                selectedfile = new File(new File(Preferences.DIRECTORYREFERENCE, "html"), ALLNAMES.get(index).toUpperCase() + ".html");
                if (! selectedfile.exists()) {try {if (! selectedfile.createNewFile()) throw new IOException();} catch (IOException e) {new ExceptionDialog(e);}}
                break;
            case txt:
                selectedfile = new File(new File(Preferences.DIRECTORYREFERENCE, "txt"), ALLNAMES.get(index).toUpperCase() + ".txt");
                if (! selectedfile.exists()) {try {if (! selectedfile.createNewFile()) throw new IOException();} catch (IOException e) {new ExceptionDialog(e);}}
                break;
        }
    }

}