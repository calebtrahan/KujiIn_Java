package kujiin.ui.creation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.Preferences;
import kujiin.xml.Session;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class SetDurationAndAmbience extends StyledStage implements Initializable {
    public CheckBox RampOnlyCheckbox;
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Spinner<Integer> SecondsSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    public ToggleButton QuickAddAmbienceToggleButton;
    public ChoiceBox<String> AvailableAmbienceChoiceBox;
    public TextField QuickAddAmbienceStatusBar;
    private Duration newduration;
    private boolean accepted = false;
    private List<Session.PlaybackItem> playbackItemList;
    private Preferences preferences;
    private AvailableAmbiences availableAmbiences;
    private int missingambiencecount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> items = FXCollections.observableArrayList(Arrays.asList("Repeat", "Shuffle"));
        AvailableAmbienceChoiceBox.setItems(items);
    }
    public SetDurationAndAmbience(Preferences preferences, AvailableAmbiences availableAmbiences, List<Session.PlaybackItem> playbackItemList) {
        this.preferences = preferences;
        this.availableAmbiences = availableAmbiences;
        try {
            this.playbackItemList = playbackItemList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/SetDurationAndAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Set ");
            if (playbackItemList.size() > 1) {stringBuilder.append(playbackItemList.size()).append(" Durations");}
            else {stringBuilder.append(playbackItemList.get(0).getName()).append(" Duration");}
            stringBuilder.append(" And Ambience");
            setTitle(stringBuilder.toString());
            long hours = 0;
            long minutes = 0;
            long seconds = 0;
            if (playbackItemList.size() == 1) {
                long millis = (long) playbackItemList.get(0).getDuration();
                hours = TimeUnit.MILLISECONDS.toHours(millis);
                millis -= TimeUnit.HOURS.toMillis(hours);
                minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
                millis -= TimeUnit.MINUTES.toMillis(minutes);
                seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            }
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, (int) hours));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) minutes));
            SecondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) seconds));
            AvailableAmbienceChoiceBox.setDisable(true);
            QuickAddAmbienceStatusBar.setDisable(true);
            for (Session.PlaybackItem i : playbackItemList) {if (! availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {missingambiencecount++;}}
            setListeners();
        } catch (IOException e) {e.printStackTrace();}
    }
    private void setListeners() {
        HoursSpinner.setOnScroll(event -> {
            Integer value = HoursSpinner.getValue();
            if (event.getDeltaY() < 0) {value -= 1; } else {value += 1;}
            if (value >= 0) {HoursSpinner.getValueFactory().setValue(value);}
        });
        MinutesSpinner.setOnScroll(event -> {
            Integer value = MinutesSpinner.getValue();
            if (event.getDeltaY() < 0) {value -= 1; } else {value += 1;}
            if (value >= 0 && value < 60) {MinutesSpinner.getValueFactory().setValue(value);}
        });
        SecondsSpinner.setOnScroll(event -> {
            Integer value = SecondsSpinner.getValue();
            if (event.getDeltaY() < 0) {value -= 5; } else {value += 5;}
            if (value >= 0 && value < 60) {SecondsSpinner.getValueFactory().setValue(value);}
        });
        QuickAddAmbienceToggleButton.setOnAction(event -> {
            if (QuickAddAmbienceToggleButton.isSelected()) {
                if (missingambiencecount > 0 && ! new ConfirmationDialog(preferences, "Warning", "Cannot Add Ambience For All Playback Items", "Missing Ambience For " + missingambiencecount + " Items. Add Anyway?").getResult()) {
                    QuickAddAmbienceToggleButton.setSelected(false);
                }
            }
            AvailableAmbienceChoiceBox.setDisable(! QuickAddAmbienceToggleButton.isSelected());
            QuickAddAmbienceStatusBar.setDisable(! QuickAddAmbienceToggleButton.isSelected());
            if (QuickAddAmbienceToggleButton.isSelected()) {QuickAddAmbienceStatusBar.setText("");}
        });
        AvailableAmbienceChoiceBox.setOnAction(event -> {
            int addedambiencecount = 0;
            switch (AvailableAmbienceChoiceBox.getSelectionModel().getSelectedItem()) {
                case "Repeat":
                    for (Session.PlaybackItem i : playbackItemList) {
                        if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {
                            i.getAmbience().clearambience();
                            i.getAmbience().addavailableambience_repeat(new Duration(i.getDuration()), availableAmbiences.getsessionpartAmbience(i.getCreationindex()));
                            addedambiencecount++;
                        }
                    }
                    if (addedambiencecount == 0) {QuickAddAmbienceStatusBar.setText("");}
                    else {
                        if (missingambiencecount == 0) {QuickAddAmbienceStatusBar.setText("Added Repeat Ambience For " + addedambiencecount + " Items");}
                        else {QuickAddAmbienceStatusBar.setText("Added Repeat Ambience For " + addedambiencecount + "/" + playbackItemList.size() + " Items");}
                    }
                    break;
                case "Shuffle":
                    for (Session.PlaybackItem i : playbackItemList) {
                        if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {
                            i.getAmbience().clearambience();
                            i.getAmbience().addavailableambience_repeat(new Duration(i.getDuration()), availableAmbiences.getsessionpartAmbience(i.getCreationindex()));
                            addedambiencecount++;
                        }
                    }
                    if (addedambiencecount == 0) {QuickAddAmbienceStatusBar.setText("");}
                    else {
                        if (missingambiencecount == 0) {QuickAddAmbienceStatusBar.setText("Added Repeat Ambience For " + addedambiencecount + " Items");}
                        else {QuickAddAmbienceStatusBar.setText("Added Repeat Ambience For " + addedambiencecount + "/" + playbackItemList.size() + " Items");}
                    }
                    break;
            }
        });
    }
}