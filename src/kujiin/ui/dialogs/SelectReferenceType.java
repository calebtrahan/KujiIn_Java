package kujiin.ui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.ErrorDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.ui.dialogs.boilerplate.ModalDialog;
import kujiin.util.enums.ReferenceType;
import kujiin.util.sessionitems.SessionItem;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectReferenceType extends ModalDialog {
    public RadioButton HTMLRadioButton;
    public RadioButton TextRadioButton;
    public TextArea Description;
    public CheckBox FullScreenCheckbox;
    public Button AcceptButton;
    public Button CancelButton;
    private ArrayList<String> descriptions = new ArrayList<>();
    private boolean result = false;
    private MainController Root;
    private List<SessionItem> itemsinsession;

    public SelectReferenceType(MainController Root, Stage stage, boolean minimizeparent, List<SessionItem> itemsinsession) {
        super(Root, stage, minimizeparent);
        try {
            this.itemsinsession = itemsinsession;
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SelectReferenceType.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Select Reference Type");
            setResizable(false);
            setOnCloseRequest(event -> {});
            descriptions.add("Display HTML Variation Of Reference Files During Session Playback. This Is Stylized Code That Allows You To Color/Format Your Reference In A Way Plain Text Cannot");
            descriptions.add("Display Text Variation Of Reference Files During Session Playback. This Is Just Plain Text So It Won't Be Formatted Or Styled");
            HTMLRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(0)));
            HTMLRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
            HTMLRadioButton.setOnAction(event ->  htmlButtonselected());
            TextRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(1)));
            TextRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
            TextRadioButton.setOnAction(event ->  textButtonselected());
            AcceptButton.setOnAction(event -> accept());
            CancelButton.setOnAction(event -> close());
            if (Root.getPreferences().getSessionOptions().getReferencetype() == null) {Root.getPreferences().getSessionOptions().setReferencetype(Preferences.DEFAULT_REFERENCE_TYPE_OPTION);}
            switch (Root.getPreferences().getSessionOptions().getReferencetype()) {
                case html:
                    HTMLRadioButton.setSelected(true);
                    setdescriptiontoselectedtype();
                    break;
                case txt:
                    TextRadioButton.setSelected(true);
                    setdescriptiontoselectedtype();
                    break;
            }
            FullScreenCheckbox.setSelected(Root.getPreferences().getSessionOptions().getReferencefullscreen());
        } catch (IOException ignored) {}
    }

// Getters
    public ReferenceType getReferenceType() {
        if (HTMLRadioButton.isSelected()) {return ReferenceType.html;}
        else if (TextRadioButton.isSelected()) {return ReferenceType.txt;}
        else {return null;}
    }
    public boolean getFullScreen() {return FullScreenCheckbox.isSelected();}
    public boolean getResult() {return result;}

// Button Methods
    private void htmlButtonselected() {
    TextRadioButton.setSelected(false);
    Description.setText(descriptions.get(0));
}
    private void textButtonselected() {
        HTMLRadioButton.setSelected(false);
        Description.setText(descriptions.get(1));
    }
    public boolean checkreferencefiles() {
        int nonexisting = 0;
        int empty = 0;
        int invalid = 0;
//        for (SessionItem i : itemsinsession) {
//            if (! i.reference_exists(getReferenceType())) {nonexisting++;}
//            else if (i.reference_empty(getReferenceType())) {empty++;}
//            else if (i.reference_invalid(getReferenceType())) {invalid++;}
//        }
        if (nonexisting > 0) {
            new ErrorDialog(Root.getPreferences(), "Missing Reference Files", "Missing Reference Files For " + nonexisting + " Session Parts", "Cannot Enable Reference");
            return false;
        }
        if (empty > 0 || invalid > 0) {
            StringBuilder msg = new StringBuilder();
            if (empty > 0) {msg.append(empty).append(" Session Parts With Empty Reference Files");}
            if (empty > 0 && invalid > 0) {msg.append("\n");}
            if (invalid > 0) {msg.append(invalid).append(" Session Parts With Invalid .html Reference");}
            return new ConfirmationDialog(Root.getPreferences(), "Enable Reference Confirmation", "Reference Files Incomplete", msg.toString(), "Continue Anyway", "Cancel").getResult();
        } else {return true;}
    }
    public void accept() {
        if (HTMLRadioButton.isSelected() || TextRadioButton.isSelected()) {
            if (checkreferencefiles()) {result = true;  close();}
        }
        else {
            new InformationDialog(Root.getPreferences(), "Cannot Accept", "No Reference Type Selected", null);
            result = false;
        }
    }

// Utility Methods
    private void setdescriptiontoselectedtype() {
        if (HTMLRadioButton.isSelected()) {Description.setText(descriptions.get(0));}
        else if (TextRadioButton.isSelected()) {Description.setText(descriptions.get(1));}
    }
}