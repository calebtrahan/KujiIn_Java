package kujiin.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.MainController;
import kujiin.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SelectAlertFile extends Stage {
    public Button HelpButton;
    public Button AcceptButton;
    public Button CancelButton;
    public CheckBox AlertFileToggleButton;
    public TextField alertfileTextField;
    public Button openFileButton;
    public Button PreviewButton;
    private File alertfile;
    private MainController Root;

    public SelectAlertFile(MainController Root) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/ChangeAlertDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            Root.setScene(defaultscene);
            Root.getOptions().setStyle(this);
            setTitle("Alert File Editor");
            AlertFileToggleButton.setSelected(Root.getOptions().getSessionOptions().getAlertfunction());
            String alertfilelocation = Root.getOptions().getSessionOptions().getAlertfilelocation();
            if (alertfilelocation != null) {alertfile = new File(Root.getOptions().getSessionOptions().getAlertfilelocation());}
            alertfiletoggled(null);
        } catch (IOException e) {}
    }

    // Button Actions
    public void accept(ActionEvent actionEvent) {
        if (AlertFileToggleButton.isSelected() && alertfile == null) {
            Root.dialog_displayInformation("No Alert File Selected", "No Alert File Selected And Alert Function Enabled", "Please Select An Alert File Or Turn Off Alert Function");
            return;
        }
        Root.getOptions().getSessionOptions().setAlertfunction(AlertFileToggleButton.isSelected());
        if (alertfile != null) {Root.getOptions().getSessionOptions().setAlertfilelocation(alertfile.toURI().toString());}
        else {Root.getOptions().getSessionOptions().setAlertfilelocation(null);}
        Root.getOptions().marshall();
        close();
    }
    public void cancel(ActionEvent actionEvent) {
        close();
    }
    public void openandtestnewfile(ActionEvent actionEvent) {
        File testfile = Util.filechooser_single(getScene(), "Select A New Alert File", null);
        if (fileisgood(testfile)) {alertfile = testfile;}
        alertfiletoggled(null);
    }
    public void preview(ActionEvent actionEvent) {
        if (alertfile != null && alertfile.exists()) {
            PreviewFile previewFile = new PreviewFile(alertfile, Root);
            previewFile.showAndWait();
        }
    }
    public void alertfiletoggled(ActionEvent actionEvent) {
        if (AlertFileToggleButton.isSelected()) {AlertFileToggleButton.setText("ON");}
        else {AlertFileToggleButton.setText("OFF");}
        PreviewButton.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
        openFileButton.setDisable(! AlertFileToggleButton.isSelected());
        alertfileTextField.setDisable(! AlertFileToggleButton.isSelected() || alertfile == null);
        if (alertfile != null && alertfile.exists()) {
            Double duration = Util.audio_getduration(alertfile);
            Duration alertfileduration = new Duration(duration * 1000);
            if (duration >= kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH && duration < kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                switch (Root.dialog_getAnswer("Alert File Longer Than Suggested Duration", null,
                        String.format("Alert File Is %s Which Is Longer Than Suggested Duration: %s And May Break Immersion",
                                Util.formatdurationtoStringDecimalWithColons(alertfileduration),
                                Util.formatdurationtoStringDecimalWithColons(new Duration(kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000))),
                        "Use As Alert File", "Don't Use As Alert File", "Cancel"
                )) {
                    case YES:
                        if (Root.dialog_getConfirmation("Really Use " + alertfile.getName() + " As Your Alert File?", null, "Really Use This As Your Alert File? This May Break Immersion", null, null)) {break;}
                        else {return;}
                    case CANCEL: return;
                }
            } else if (duration >= kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
                Root.dialog_displayInformation("Cannot Add Alert File", null,
                        String.format("Alert File Is %s Which Is Too Long And Will Break Immersion", Util.formatdurationtoStringDecimalWithColons(alertfileduration)));
                return;
            }
            String durationtext = Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), alertfileTextField.getLayoutBounds().getWidth());
            String text = String.format("%s (%s)", alertfile.getName(), durationtext);
            alertfileTextField.setText(text);
        } else {
            if (alertfile != null) {alertfile = null; alertfiletoggled(null);}
            alertfileTextField.setText(kujiin.xml.Options.NO_ALERT_FILE_SELECTED_TEXT);
        }
    }
    public void help(ActionEvent actionEvent) {
        Root.dialog_displayInformation("What Is An Alert File?", "", "The 'alert file' is a short audible warning\nthat is played in between parts of the session\nto inform you it's time to player_transition to the next\npart of the session");
    }

    // Utility Methods
    public boolean fileisgood(File testfile) {
        // Test If Valid Extension
        if (! Util.audio_isValid(testfile)) {
            Root.dialog_displayInformation("Information", "Invalid Audio Format", "Supported Audio Formats: " + Collections.singletonList(Util.SUPPORTEDAUDIOFORMATS).toString());
            return false;
        }
        Double duration = Util.audio_getduration(testfile);
        if (duration == 0.0) {
            Root.dialog_displayInformation("Invalid File", "Invalid Audio File", "Audio File Has Zero Length Or Is Corrupt. Cannot Use As Alert File"); return false;}
        else if (duration >= (kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH) && duration < (kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH)) {
            String confirmationtext = String.format("%s Is %s Which Is Longer Than The Suggested Maximum Duration %s. This May Break Session Immersion", testfile.getName(),
                    Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(kujiin.xml.Options.SUGGESTED_ALERT_FILE_MAX_LENGTH * 1000), null));
            return Root.dialog_getConfirmation("Alert File Too Long", null, confirmationtext, "Use As Alert File", "Cancel");
        } else if (duration >= kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH) {
            String errortext = String.format("%s Is Longer Than The Maximum Allowable Duration %s", Util.formatdurationtoStringSpelledOut(new Duration(duration * 1000), null), Util.formatdurationtoStringSpelledOut(new Duration(kujiin.xml.Options.ABSOLUTE_ALERT_FILE_MAX_LENGTH * 1000), null));
            Root.dialog_displayInformation("Invalid File", errortext, "Cannot Use As Alert File As It Will Break Immersion");
            return false;
        } else {return true;}
    }

//        public void alertfiletoggle() {
//            if (AlertSwitch.isSelected()) {
//                if (Options.getSessionOptions().getAlertfilelocation() == null) {
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
//                Options.getSessionOptions().setAlertfilelocation(AlertFile.toURI().toString());
//                String audioduration = Util.format_secondsforplayerdisplay((int) Util.audio_getduration(AlertFile));
//                AlertFileTextField.setText(String.format("%s (%s)", AlertFile.getName(), audioduration));
//            } else {
//                good = false;
//                AlertFileTextField.setText("Alert Feature Disabled");
//                Options.getSessionOptions().setAlertfilelocation(null);
//            }
//            Options.getSessionOptions().setAlertfunction(good);
//            AlertFileEditButton.gui_setDisable(! good);
//            AlertFileTextField.gui_setDisable(! good);
//            AlertSwitch.setSelected(good);
//            return good;
//        }

}
