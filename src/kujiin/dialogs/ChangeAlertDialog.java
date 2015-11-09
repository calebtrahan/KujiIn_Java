package kujiin.dialogs;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kujiin.Session;
import kujiin.Tools;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ChangeAlertDialog extends Stage {
    public TextField alertfileTextField;
    public Button openFileButton;
    public Button AcceptButton;
    public Button CancelButton;
    private File newalertfile = null;
    private File alertfileactual = Session.alertfile;
    private Boolean alertfilechanged = null;

    public ChangeAlertDialog(Parent parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/ChangeAlertDialog.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select An Ambience Type Variation");}
        catch (IOException e) {e.printStackTrace();}
        if (alertfileactual.exists()) {this.setTitle("Change Alert File");}
        else {this.setTitle("Add A New Alert File");}
        alertfileTextField.setEditable(false);
    }

    public void openandtestnewfile(Event event) {
        FileChooser a = new FileChooser();
        File newfile = a.showOpenDialog(this);
        if (newfile != null) {
            if (newfile.toString().endsWith(".mp3")) {
                double duration = Tools.getaudioduration(newfile);
                if (duration > 10000) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Alert File Validation");
                    alert.setHeaderText("Alert File Longer Than 10 Seconds");
                    String msg = String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
                            "Really Use It?", duration);
                    alert.setContentText(msg);
                    Optional<ButtonType> result = alert.showAndWait();
                    if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                        alertfileTextField.setText(newfile.getName());
                        newalertfile = newfile;
                    }
                } else {
                    alertfileTextField.setText(newfile.getName());
                    newalertfile = newfile;
                }
            }
        }
    }

    public void commitchanges(Event event) {
        if (newalertfile != null) {
            if (alertfileactual.exists()) {                                                                             // Change Alert File
                File tempfile = new File(Session.sounddirectory, "AlertTemp.mp3");
                try {
                    // Make A Temp File Copy In Case It Fails
                    FileUtils.copyFile(alertfileactual, tempfile);
                    alertfileactual.delete();
                    FileUtils.copyFile(newalertfile, alertfileactual);
                    if (Tools.testAlertFile()) {tempfile.delete();}
                } catch (IOException e) {setAlertfilechanged(false);}
            } else {                                                                                                    // Set A New Alert File
                try {
                    FileUtils.copyFile(newalertfile, alertfileactual);
                } catch (IOException e) {setAlertfilechanged(false);}
            }
            setAlertfilechanged(Tools.testAlertFile());
            this.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Alert File Opened");
            alert.setHeaderText("No Alert File Opened");
            alert.setContentText("You Need To Open An Alert File First");
            alert.showAndWait();
        }
    }

    public void cancel(Event event) {
        setAlertfilechanged(false);
        this.close();
    }

    public Boolean getAlertfilechanged() {
        if (alertfilechanged != null) {
            return alertfilechanged;
        } else {
            return false;
        }
    }
    public void setAlertfilechanged(Boolean alertfilechanged) {this.alertfilechanged = alertfilechanged;}
}
