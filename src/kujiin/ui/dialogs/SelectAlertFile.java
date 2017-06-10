package kujiin.ui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import kujiin.ui.MainController;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.AnswerDialog;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;
import kujiin.xml.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SelectAlertFile extends StyledStage {
    public Button HelpButton;
    public Button AcceptButton;
    public Button CancelButton;
    public CheckBox AlertFileToggleButton;
    public TextField alertfileTextField;
    public Button openFileButton;
    public Button PreviewButton;
    private File alertfile;
    private MainController Root;
    private boolean accepted = false;

    public SelectAlertFile(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/ChangeAlertDialog.fxml"));
            fxmlLoader.setController(this);
            setScene(new Scene(fxmlLoader.load()));
            setTitle("Alert File Editor");
            AlertFileToggleButton.setSelected(Root.getPreferences().getSessionOptions().getAlertfunction());
            String alertfilelocation = Root.getPreferences().getSessionOptions().getAlertfilelocation();
            if (alertfilelocation != null) {alertfile = new File(Root.getPreferences().getSessionOptions().getAlertfilelocation());}
            alertfiletoggled();
            HelpButton.setOnAction(event -> help());
            PreviewButton.setOnAction(event -> preview());
            AcceptButton.setOnAction(event -> accept());
            CancelButton.setOnAction(event -> cancel());
            AlertFileToggleButton.setOnAction(event -> alertfiletoggled());
            openFileButton.setOnAction(event -> openandtestnewfile());
        } catch (IOException ignored) {}
    }

// Getters And Setters
    public File getAlertfile() {
        return alertfile;
    }
    public boolean enableAlertFile() {
        return AlertFileToggleButton.isSelected();
    }
    public boolean isAccepted() {
        return accepted;
    }

// Button Actions
    public void accept() {
        if (AlertFileToggleButton.isSelected() && alertfile == null) {
            new InformationDialog(Root.getPreferences(), "No Alert File Selected", "No Alert File Selected And Alert Function Enabled", "Please Select An Alert File Or Turn Off Alert Function");
            return;
        }
        accepted = true;
        close();
    }
    public void cancel() {
        close();
    }
    public void openandtestnewfile() {
        File testfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
        if (fileisgood(testfile)) {alertfile = testfile;}
        alertfiletoggled();
    }
    public void preview() {
        if (alertfile != null && alertfile.exists()) {
            PreviewFile previewFile = new PreviewFile(alertfile);
            previewFile.showAndWait();
        }
    }
    public void alertfiletoggled() {
        if (AlertFileToggleButton.isSelected()) {AlertFileToggleButton.setText("ON");}
        else {AlertFileToggleButton.setText("OFF");}
        PreviewButton.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
        openFileButton.setDisable(! AlertFileToggleButton.isSelected());
        alertfileTextField.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
        if (alertfile != null && alertfile.exists()) {
            Double duration = Util.audio_getduration(alertfile);
            Duration alertfileduration = new Duration(duration * 1000);
            if (duration >= Preferences.SUGGESTED_ALERT_FILE_MAX_LENGTH && duration < Preferences.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                switch (new AnswerDialog(Root.getPreferences(), this, "Alert File Longer Than Suggested Duration", null,
                        String.format("Alert File Is %s Which Is Longer Than Suggested Duration: %s And May Break Immersion",
                                Util.formatdurationtoStringDecimalWithColons(alertfileduration),
                                Util.formatdurationtoStringDecimalWithColons(new Duration(Preferences.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000))),
                        "Use As Alert File", "Don't Use As Alert File", "Cancel"
                ).getResult()) {
                    case YES:
                        if (new ConfirmationDialog(Root.getPreferences(), "Really Use " + alertfile.getName() + " As Your Alert File?", null, "Really Use This As Your Alert File? This May Break Immersion", null, null).getResult()) {break;}
                        else {return;}
                    case CANCEL: return;
                }
            } else if (duration >= Preferences.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                new InformationDialog(Root.getPreferences(), "Cannot Add Alert File", null,
                        String.format("Alert File Is %s Which Is Too Long And Will Break Immersion", Util.formatdurationtoStringDecimalWithColons(alertfileduration)));
                return;
            }
            String durationtext = Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), alertfileTextField.getLayoutBounds().getWidth());
            String text = String.format("%s (%s)", alertfile.getName(), durationtext);
            alertfileTextField.setText(text);
        } else {
            if (alertfile != null) {alertfile = null; alertfiletoggled();}
            alertfileTextField.setText(Preferences.NO_ALERT_FILE_SELECTED_TEXT);
        }
    }
    public void help() {
        new InformationDialog(Root.getPreferences(), "What Is An Alert File?", "", "The 'alert file' is a short audible warning\nthat is played in between parts of the session\nto inform you it's time to player_transition to the next\npart of the session");
    }

// Utility Methods
    public boolean fileisgood(File testfile) {
        // Test If Valid Extension
        if (! Util.audio_isValid(testfile)) {
            new InformationDialog(Root.getPreferences(), "Information", "Invalid Audio Format", "Supported Audio Formats: " + Collections.singletonList(Util.SUPPORTEDAUDIOFORMATS).toString());
            return false;
        }
        Double duration = Util.audio_getduration(testfile);
        if (duration == 0.0) {
            new InformationDialog(Root.getPreferences(), "Invalid File", "Invalid Audio File", "Audio File Has Zero Length Or Is Corrupt. Cannot Use As Alert File"); return false;}
        else if (duration >= (Preferences.SUGGESTED_ALERT_FILE_MAX_LENGTH) && duration < (Preferences.ABSOLUTE_ALERT_FILE_MAX_LENGTH)) {
            String confirmationtext = String.format("%s Is %s Which Is Longer Than The Suggested Maximum Duration %s. This May Break Session Immersion", testfile.getName(),
                    Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(Preferences.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000), null));
            return new ConfirmationDialog(Root.getPreferences(), "Alert File Too Long", null, confirmationtext, "Use As Alert File", "Cancel").getResult();
        } else if (duration >= Preferences.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
            String errortext = String.format("%s Is Longer Than The Maximum Allowable Duration %s", Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(Preferences.ABSOLUTE_ALERT_FILE_MAX_LENGTH * 1000), null));
            new InformationDialog(Root.getPreferences(), "Invalid File", errortext, "Cannot Use As Alert File As It Will Break Immersion");
            return false;
        } else {return true;}
    }
    //        public void alertfiletoggle() {
//            if (AlertSwitch.isSelected()) {
//                if (Preferences.getSessionOptions().getAlertfilelocation() == null) {
//                    AlertFile = getnewalertfile();
//                    checkalertfile();
//                }
//            } else {
//                if (Util.dialog_OKCancelConfirmation(Root, "Confirmation", "This Will Disable The Audible Alert File Played In Between Cuts", "Really Disable This Feature?")) {
//                    AlertFile = null;
//                    checkalertfile();
//                } else {
//                    AlertSwitch.setSelected(true);
//                }
//            }
//        }
//        public File getnewalertfile() {
//            File newfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
//            if (newfile != null) {
//                if (Util.audio_isValid(newfile)) {
//                    double duration = Util.audio_getduration(newfile);
//                    if (duration > 10000) {
//                        if (!Util.dialog_OKCancelConfirmation(Root, "Validation", "Alert File Is longer Than 10 Seconds",
//                                String.format("This Alert File Is %s Seconds, And May Break Immersion, " +
//                                        "Really Use It?", duration))) {newfile = null;}
//                    }
//                } else {
//                    Util.dialog_displayInformation(Root, "Information", newfile.getName() + " Isn't A Valid Audio File", "Supported Audio Formats: " + Util.audio_getsupportedText());
//                    newfile = null;
//                }
//            }
//            return newfile;
//        }
//        public boolean checkalertfile() {
//            boolean good;
//            if (AlertFile != null && Util.audio_isValid(AlertFile)) {
//                good = true;
//                Preferences.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
//                String audioduration = Util.format_secondsforplayerdisplay((int) Util.audio_getduration(AlertFile));
//                AlertFileTextField.setText(String.format("%s (%s)", AlertFile.getName(), audioduration));
//            } else {
//                good = false;
//                AlertFileTextField.setText("Alert Feature Disabled");
//                Preferences.getSessionOptions().setAlertfilelocation(null);
//            }
//            Preferences.getSessionOptions().setAlertfunction(good);
//            AlertFileEditButton.gui_setDisable(! good);
//            AlertFileTextField.gui_setDisable(! good);
//            AlertSwitch.setSelected(good);
//            return good;
//        }

}