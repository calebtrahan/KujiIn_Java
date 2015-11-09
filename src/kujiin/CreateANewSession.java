package kujiin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.dialogs.ChangeAllValuesDialog;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreateANewSession extends Stage implements Initializable {
    public TextField pretime;
    public TextField rintime;
    public TextField kyotime;
    public TextField tohtime;
    public TextField shatime;
    public TextField kaitime;
    public TextField jintime;
    public TextField retsutime;
    public TextField zaitime;
    public TextField zentime;
    public TextField posttime;
    public CheckBox AmbienceOptionCheckBox;
    public Button CancelButton;
    public Button CreateSessionButton;
    public Button ChangeAllValuesButton;
    public Button opensavedpresetButton;
    public Button SaveAsPresetButton;
    public Label sessioncreatorstatusbar;
    public Label totalsessiontimeFormattedLabel;
    public Label approximatefinishtimeLabel;
    private Session thissession;
    private Service<Void> creationservice;
    ArrayList<Integer> textfieldvalues = new ArrayList<>();

    CreateANewSession(Parent parent, Session thissession) {
        this.thissession = thissession;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/kujiin/assets/fxml/CreateANewSession.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Session Creator:");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        maketextfieldsnumericonly();
    }

    // Set All Text Fields Numeric
    public void maketextfieldsnumericonly() {
        Tools.numericTextField(pretime);
        pretime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(rintime);
        rintime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(kyotime);
        kyotime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(tohtime);
        tohtime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(shatime);
        shatime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(kaitime);
        kaitime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(jintime);
        jintime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(retsutime);
        retsutime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(zaitime);
        zaitime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(zentime);
        zentime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        Tools.numericTextField(posttime);
        posttime.textProperty().addListener((observable, oldValue, newValue) -> {
            updatetotalsessiontime();
        });
        sessioncreatorstatusbar.textProperty().addListener((observable, oldValue, newValue) -> {
            new Timeline(new KeyFrame(Duration.millis(3000), ae -> sessioncreatorstatusbar.setText(""))).play();});
    }

    // Test If All TextFieldValues Are Zero
    public boolean gettextfieldvalues() {
        Boolean not_all_zeros = false;
        try {
            if (textfieldvalues != null) {textfieldvalues.clear();}
            textfieldvalues.add(Integer.parseInt(pretime.getText()));
            textfieldvalues.add(Integer.parseInt(rintime.getText()));
            textfieldvalues.add(Integer.parseInt(kyotime.getText()));
            textfieldvalues.add(Integer.parseInt(tohtime.getText()));
            textfieldvalues.add(Integer.parseInt(shatime.getText()));
            textfieldvalues.add(Integer.parseInt(kaitime.getText()));
            textfieldvalues.add(Integer.parseInt(jintime.getText()));
            textfieldvalues.add(Integer.parseInt(retsutime.getText()));
            textfieldvalues.add(Integer.parseInt(zaitime.getText()));
            textfieldvalues.add(Integer.parseInt(zentime.getText()));
            textfieldvalues.add(Integer.parseInt(posttime.getText()));
            for (Integer i : textfieldvalues) {
                if (i > 0) {
                    not_all_zeros = true;}
            }
        } catch (NumberFormatException ignored) {}
        return not_all_zeros;
    }

    // Updates The Labels Showing Total Time And End Time Whenever A TextField Value Is Changed
    public void updatetotalsessiontime() {
        if (gettextfieldvalues()) {
            Integer totalsessiontime = 0;
            for (Integer i : textfieldvalues) {totalsessiontime += i;}
            totalsessiontimeFormattedLabel.setText(Tools.minutestoformattedhoursandmins(totalsessiontime));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, totalsessiontime);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            approximatefinishtimeLabel.setText(sdf.format(cal.getTime()));
        }
    }

    // Checks Ambience When The Add Ambience Checkbox Is Ticked
    public void checkambience(ActionEvent actionEvent) {
        if (AmbienceOptionCheckBox.isSelected()) {
//            sessioncreatorstatusbar.setText("Checking Ambience...Please Wait");
            if (gettextfieldvalues()) {
                thissession.checkifambienceisgood(textfieldvalues, this);
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Cannot Calculate Ambience");
                a.setHeaderText("All Cut Durations Are Zero");
                a.setContentText("Please Set Your Session Durations Before Adding Ambience");
                a.showAndWait();
                AmbienceOptionCheckBox.setSelected(false);
            }
        } else {thissession.setAmbienceenabled(false);}
    }

    // Closes Threads And Creation Window When Clicking Cancel Button
    public void cancelsessioncreation(ActionEvent actionEvent) {this.close();}

    // Called When Create Button Is Pressed
    public void createsession(ActionEvent actionEvent) {
        if (thissession.getCreated()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Session Validation");
            alert.setHeaderText("Session Is Already Created");
            alert.setContentText("Really Overwrite Previous Session?");
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.CANCEL)) {return;}
        }
        if (gettextfieldvalues()) {
            if (Tools.sessionwellformednesschecks(textfieldvalues)) {
                thissession.setAmbienceenabled(AmbienceOptionCheckBox.isSelected());
                thissession.create(textfieldvalues, this);
                // TODO HERE!!
            }
        } else {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("Cannot Create Session");
            alert2.setHeaderText("At Least One Cut's Value Must Be > 0");
            alert2.setContentText("All Values For Cuts (Pre + Post Excluded) Are 0.");
            alert2.showAndWait();
        }
    }

    // Called When Open Saved Preset Button Is Pressed
    public void opensavedpreset(ActionEvent actionEvent) {
        System.out.println("This Isn't Done Yet");
    }

    // Called When Save As Preset Button Is Pressed
    public void savethissessionaspreset(ActionEvent actionEvent) {
        System.out.println("This Isn't Done Yet");
    }

    // Changes All TextFieldValues
    public void changeallvalues(Event event) {
        ChangeAllValuesDialog changevaluesdialog = new ChangeAllValuesDialog(null);
        changevaluesdialog.showAndWait();
        Integer min = changevaluesdialog.getminutes();
        if (min != 0) {
            String minutes = min.toString();
            rintime.setText(minutes);
            kyotime.setText(minutes);
            tohtime.setText(minutes);
            shatime.setText(minutes);
            kaitime.setText(minutes);
            jintime.setText(minutes);
            retsutime.setText(minutes);
            zaitime.setText(minutes);
            zentime.setText(minutes);
            if (changevaluesdialog.getcheckboxstate()) {
                pretime.setText(minutes);
                posttime.setText(minutes);
            }
        }
    }
}
