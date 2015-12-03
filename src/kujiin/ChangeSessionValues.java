package kujiin;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kujiin.dialogs.ChangeAllValuesDialog;
import kujiin.util.lib.GuiUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class ChangeSessionValues extends Stage implements Initializable {
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
    public Label totalsessiontimeFormattedLabel;
    public Label approximatefinishtimeLabel;
    ArrayList<Integer> textfieldvalues = new ArrayList<>();
    private This_Session this_session;

    public ChangeSessionValues(This_Session this_session) {
        this.this_session = this_session;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/kujiin/assets/fxml/ChangeSessionValues.fxml"));
        fxmlLoader.setController(this);
        try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Edit This Session's Values");}
        catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        maketextfieldsnumericonly();
        setpreviousvalues();
    }

    public boolean getambienceenabled() {return AmbienceOptionCheckBox.isSelected();}

// Other Methods
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
    }
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
            for (Integer i : textfieldvalues) {if (i > 0) {not_all_zeros = true;}}
        } catch (NumberFormatException ignored) {}
        return not_all_zeros;
    }
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
    public void checkambience(ActionEvent actionEvent) {
        if (AmbienceOptionCheckBox.isSelected()) {
            if (gettextfieldvalues()) {
                this_session.checkifambienceisgood(textfieldvalues, this);
            } else {
                GuiUtils.showinformationdialog("Information", "All Cut Durations Are Zero", "Please Increase Cut(s) Durations Before Checking This");
                AmbienceOptionCheckBox.setSelected(false);
            }
        } else {this_session.setAmbienceenabled(false);}
        ambiencecheckboxswitch();
    }
    public void cancelsessioncreation(ActionEvent actionEvent) {this.close();}
    public void createsession(ActionEvent actionEvent) {
        if (gettextfieldvalues()) {
            if (Tools.sessionwellformednesschecks(textfieldvalues)) {
                this_session.setAmbienceenabled(AmbienceOptionCheckBox.isSelected());
                this_session.create(textfieldvalues);
                super.close();
            }
        }
        else {GuiUtils.showerrordialog("Error", "Cannot Edit Values", "All Valued For Cuts (Pre + Post Exluded) Must Be > 0");}
    }
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
    public void setpreviousvalues() {
        ArrayList<Cut> previousvalues = this_session.getallCuts();
        pretime.setText(Integer.toString(previousvalues.get(0).duration));
        rintime.setText(Integer.toString(previousvalues.get(1).duration));
        kyotime.setText(Integer.toString(previousvalues.get(2).duration));
        tohtime.setText(Integer.toString(previousvalues.get(3).duration));
        shatime.setText(Integer.toString(previousvalues.get(4).duration));
        kaitime.setText(Integer.toString(previousvalues.get(5).duration));
        jintime.setText(Integer.toString(previousvalues.get(6).duration));
        retsutime.setText(Integer.toString(previousvalues.get(7).duration));
        zaitime.setText(Integer.toString(previousvalues.get(8).duration));
        zentime.setText(Integer.toString(previousvalues.get(9).duration));
        posttime.setText(Integer.toString(previousvalues.get(10).duration));
        AmbienceOptionCheckBox.setSelected(this_session.getAmbienceenabled());
        ambiencecheckboxswitch();
    }
    public void ambiencecheckboxswitch() {
        if (AmbienceOptionCheckBox.isSelected()) {AmbienceOptionCheckBox.setText("Enabled");}
        else {AmbienceOptionCheckBox.setText("Disabled");}
    }

// Presets
    public void opensavedpreset(ActionEvent actionEvent) {
        System.out.println("This Isn't Done Yet");
    }
    public void savethissessionaspreset(ActionEvent actionEvent) {
        System.out.println("This Isn't Done Yet");
    }
}

