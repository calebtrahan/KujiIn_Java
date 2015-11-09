package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kujiin.Tools;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CutInvocationDialog extends Stage implements Initializable{
    public Button CancelButton;
    public Button OKButton;
    public TextField cutinvocationminutesTextField;
    private int cutinvocationduration;

    CutInvocationDialog(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/CutInvocationDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Creating Session");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tools.numericTextField(cutinvocationminutesTextField);
    }

    public int getCutinvocationduration() {
        return cutinvocationduration;
    }

    public void setCutinvocationduration(int cutinvocationduration) {
        this.cutinvocationduration = cutinvocationduration;
    }

    public void CancelButtonPressed(Event event) {
        setCutinvocationduration(0);
        this.close();
    }

    public void OKButtonPressed(Event event) {
        try {
            int value = Integer.parseInt(cutinvocationminutesTextField.getText());
            if (value != 0) {
                setCutinvocationduration(value);
                this.close();
            } else {
                Alert continuewithzerovalue = new Alert(Alert.AlertType.CONFIRMATION);
                continuewithzerovalue.setHeaderText("Cut Invocation Value Is Zero");
                continuewithzerovalue.setContentText("Continue With Zero Value (These Cuts Won't Be Included)");
                continuewithzerovalue.setTitle("Confirmation");
                Optional<ButtonType> a = continuewithzerovalue.showAndWait();
                if (a.isPresent() && a.get() == ButtonType.OK) {
                    setCutinvocationduration(0);
                    this.close();
                }
            }
        } catch (NumberFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Value Is Empty");
            error.setContentText("Enter A Numeric Value Then Press OK");
            error.showAndWait();
        }
    }
}
