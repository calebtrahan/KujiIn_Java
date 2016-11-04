package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.util.enums.ReferenceType;
import kujiin.xml.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static kujiin.xml.Preferences.PROGRAM_ICON;

public class EditReferenceFiles extends Stage {
    public ChoiceBox<String> SessionPartNamesChoiceBox;
    public TextArea MainTextArea;
    public Button CloseButton;
    public Label StatusBar;
    public Button SaveButton;
    public Button PreviewButton;
    public RadioButton HTMLVariation;
    public RadioButton TEXTVariation;
    private File selectedfile;
    private SessionPart selectedsessionpart;
    private ArrayList<Integer> userselectedindexes;
    private ReferenceType referenceType;
    private MainController Root;

    public EditReferenceFiles(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/EditReferenceFiles.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            getIcons().clear();
            getIcons().add(PROGRAM_ICON);
            String themefile = Root.getPreferences().getUserInterfaceOptions().getThemefile();
            if (themefile != null) {getScene().getStylesheets().add(themefile);}
            setTitle("Reference Files Editor");
            ObservableList<String> sessionpartnames = FXCollections.observableArrayList();
            sessionpartnames.addAll(Preferences.ALLNAMES);
            userselectedindexes = new ArrayList<>();
            SessionPartNamesChoiceBox.setItems(sessionpartnames);
            MainTextArea.textProperty().addListener((observable, oldValue, newValue) -> {textchanged();});
            SessionPartNamesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {if (oldValue != null) userselectedindexes.add(oldValue.intValue());});
            HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
            TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
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
        } catch (IOException e) {new ExceptionDialog(Root.getPreferences(), e).showAndWait();}
    }

    // Text Area Methods
    private boolean unsavedchanges() {
        try {
            return ! MainTextArea.getText().equals(Util.file_getcontents(selectedfile));
        } catch (Exception e) {return false;}
    }
    public void newsessionpartselected() {
        HTMLVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
        TEXTVariation.setDisable(SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1);
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
        if (referenceType != null && selectedsessionpart != null && selectedfile != null) {
            boolean hasvalidtext = MainTextArea.getText() != null && MainTextArea.getText().length() > 0;
            PreviewButton.setDisable(! hasvalidtext || referenceType == ReferenceType.txt);
            SaveButton.setDisable(MainTextArea.getText() == null || Util.file_getcontents(selectedfile).equals(MainTextArea.getText().toCharArray()));
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
            Util.gui_showtimedmessageonlabel(StatusBar, "No SessionPart Selected", 3000);
        }
    }

    // Button Methods
    public void htmlselected() {
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
    public void textselected() {
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
    public void preview() {
        if (MainTextArea.getText().length() > 0 && HTMLVariation.isSelected() && referenceType == ReferenceType.html) {
            if (! Util.String_validhtml(MainTextArea.getText())) {
                if (! new ConfirmationDialog(Root.getPreferences(), "Confirmation", null, "Html Code In Text Area Is Not Valid HTML", "Preview Anyways", "Cancel").getResult()) {return;}
            }
            new DisplayReference(Root, MainTextArea.getText());
        }
    }

    // Utility Methods
    public void saveselectedfile() {
        if (Util.file_writecontents(selectedfile, MainTextArea.getText())) {
            String text = selectedsessionpart + "'s Reference File (" + referenceType.toString() + " Variation) Has Been Saved";
            new InformationDialog(Root.getPreferences(), "Changes Saved", text, "");
        } else {
            new ErrorDialog(Root.getPreferences(), "Error", "Couldn't Save To:\n" + selectedfile.getAbsolutePath(), "Check If You Have Write Access To File");}
    }
    public void loadselectedfile() {
        int index = SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex();
        if (index != -1 && (HTMLVariation.isSelected() || TEXTVariation.isSelected())) {
            selectedsessionpart = Root.getAllSessionParts(false).get(index);
            selectnewfile();
            String contents = Util.file_getcontents(selectedfile);
            MainTextArea.setText(contents);
            PreviewButton.setDisable(TEXTVariation.isSelected() || contents == null || contents.length() == 0);
            StatusBar.setTextFill(Color.BLACK);
            StatusBar.setText("");
            SaveButton.setDisable(true);
        } else {
            if (SessionPartNamesChoiceBox.getSelectionModel().getSelectedIndex() == -1) {
                new InformationDialog(Root.getPreferences(), "Information", "No SessionPart Selected", "Select A SessionPart To Load");}
            else {
                new InformationDialog(Root.getPreferences(), "Information", "No Variation Selected", "Select A Variation To Load");}
            PreviewButton.setDisable(true);
        }
    }
    public void selectnewfile() {
        if (referenceType == null || selectedsessionpart == null) {selectedfile = null; return;}
        switch (referenceType) {
            case html:
                selectedfile = new File(new File(Preferences.DIRECTORYREFERENCE, "html"), selectedsessionpart.getNameForReference() + ".html");
                if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(Root.getPreferences(), e);}}
                break;
            case txt:
                selectedfile = new File(new File(Preferences.DIRECTORYREFERENCE, "txt"), selectedsessionpart.getNameForReference() + ".txt");
                if (! selectedfile.exists()) {try {selectedfile.createNewFile();} catch (IOException e) {new ExceptionDialog(Root.getPreferences(), e);}}
                break;
        }
    }

    // Dialog Methods
    public void closewindow(Event event) {
        // Check If Unsaved Text
        this.close();
    }

}