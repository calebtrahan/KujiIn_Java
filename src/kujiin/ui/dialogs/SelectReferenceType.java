package kujiin.ui.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import kujiin.ui.MainController;
import kujiin.util.enums.ReferenceType;

import java.io.IOException;
import java.util.ArrayList;

public class SelectReferenceType extends Stage {
    public RadioButton HTMLRadioButton;
    public RadioButton TextRadioButton;
    public TextArea Description;
    public CheckBox FullScreenCheckbox;
    public Button AcceptButton;
    public Button CancelButton;
    private ArrayList<String> descriptions = new ArrayList<>();
    private boolean result = false;
    private MainController Root;

    public SelectReferenceType(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectReferenceType.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setTitle("Select Reference Type");
            Root.getOptions().setStyle(this);
            this.setResizable(false);
            setOnCloseRequest(event -> {});
            descriptions.add("Display HTML Variation Of Reference Files During Session Playback. This Is Stylized Code That Allows You To Color/Format Your Reference In A Way Plain Text Cannot");
            descriptions.add("Display Text Variation Of Reference Files During Session Playback. This Is Just Plain Text So It Won't Be Formatted Or Styled");
            HTMLRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(0)));
            HTMLRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
            HTMLRadioButton.setOnAction(event ->  htmlButtonselected());
            TextRadioButton.setOnMouseEntered(event -> Description.setText(descriptions.get(1)));
            TextRadioButton.setOnMouseExited(event -> setdescriptiontoselectedtype());
            TextRadioButton.setOnAction(event ->  textButtonselected());
            switch (Root.getOptions().getSessionOptions().getReferencetype()) {
                case html:
                    HTMLRadioButton.setSelected(true);
                    setdescriptiontoselectedtype();
                    break;
                case txt:
                    TextRadioButton.setSelected(true);
                    setdescriptiontoselectedtype();
                    break;
            }
        } catch (IOException ignored) {}
    }

    private void setdescriptiontoselectedtype() {
        if (HTMLRadioButton.isSelected()) {Description.setText(descriptions.get(0));}
        else if (TextRadioButton.isSelected()) {Description.setText(descriptions.get(1));}
    }
    private void htmlButtonselected() {
        TextRadioButton.setSelected(false);
        Description.setText(descriptions.get(0));
    }
    private void textButtonselected() {
        HTMLRadioButton.setSelected(false);
        Description.setText(descriptions.get(1));
    }
    public ReferenceType getReferenceType() {
        if (HTMLRadioButton.isSelected()) {return ReferenceType.html;}
        else if (TextRadioButton.isSelected()) {return ReferenceType.txt;}
        else {return null;}
    }
    public boolean getFullScreen() {return FullScreenCheckbox.isSelected();}
    public boolean getResult() {return result;}

    public void accept(ActionEvent actionEvent) {
        if (HTMLRadioButton.isSelected() || TextRadioButton.isSelected()) {result = true;  close();}
        else {
            new InformationDialog(Root.getOptions(), "Cannot Accept", "No Reference Type Selected", null);
            result = false;
        }
    }
}