package kujiin.ui.creation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.Preferences;
import kujiin.xml.Session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SetDurationAndAmbience extends StyledStage {
    public CheckBox RampOnlyCheckbox;
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Spinner<Integer> SecondsSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    public ToggleButton QuickAddAmbienceToggleButton;
    public ChoiceBox<String> AvavilableAmbienceChoiceBox;
    public TextField QuickAddAmbienceStatusBar;
    private boolean accepted = false;
    private List<Session.PlaybackItem> playbackItemList;
    private Preferences preferences;
    private AvailableAmbiences availableAmbiences;
    private int missingambiencecount = 0;

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
            AvavilableAmbienceChoiceBox.setDisable(true);
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
                if (getNewDuration().lessThanOrEqualTo(Duration.ZERO) && ! RampOnlyCheckbox.isSelected()) {
                    new InformationDialog(preferences, "Information", "Cannot Add Ambience With Zero Duration", "Add Duration Or Select Ramp Only To Add Ambience");
                    QuickAddAmbienceToggleButton.setSelected(false);
                    return;
                }
                if (missingambiencecount > 0 && ! new ConfirmationDialog(preferences, "Warning", "Cannot Add Ambience For All Playback Items", "Missing Ambience For " + missingambiencecount + " Items. Add Anyway?").getResult()) {
                    QuickAddAmbienceToggleButton.setSelected(false);
                    return;
                }
            }
            AvavilableAmbienceChoiceBox.setDisable(! QuickAddAmbienceToggleButton.isSelected());
            QuickAddAmbienceStatusBar.setDisable(! QuickAddAmbienceToggleButton.isSelected());
            if (QuickAddAmbienceToggleButton.isSelected()) {QuickAddAmbienceStatusBar.setText("");}
        });
        AvavilableAmbienceChoiceBox.setOnAction(event -> {
            int addedambiencecount = 0;
            if (AvavilableAmbienceChoiceBox.getSelectionModel().getSelectedIndex() != -1) {
                String ambiencetype;
                if (AvavilableAmbienceChoiceBox.getSelectionModel().getSelectedIndex() == 0) {ambiencetype = "Repeat";}
                else {ambiencetype = "Shuffle";}
                for (Session.PlaybackItem i : playbackItemList) {
                    if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {
                        Duration duration;
                        if (RampOnlyCheckbox.isSelected()) {duration = Duration.minutes(1);}
                        else {duration = getNewDuration();}
                        i.getAmbience().clearambience();
                        if (Objects.equals(ambiencetype, "Repeat")) {i.getAmbience().addavailableambience_repeat(duration, availableAmbiences.getsessionpartAmbience(i.getCreationindex()));}
                        else {i.getAmbience().addavailableambience_shuffle(duration, availableAmbiences.getsessionpartAmbience(i.getCreationindex()));}
                        addedambiencecount++;
                        i.getAmbience().setEnabled(true);
                    } else {i.getAmbience().setEnabled(false);}
                }
                if (addedambiencecount == 0) {QuickAddAmbienceStatusBar.setText("");}
                else {
                    if (missingambiencecount == 0) {QuickAddAmbienceStatusBar.setText("Added " + ambiencetype + " Ambience For " + addedambiencecount + " Items");}
                    else {QuickAddAmbienceStatusBar.setText("Added " + ambiencetype + " Ambience For " + addedambiencecount + "/" + playbackItemList.size() + " Items");}
                }
            }
        });
        RampOnlyCheckbox.setOnAction(event -> {
            HoursSpinner.setDisable(RampOnlyCheckbox.isSelected());
            MinutesSpinner.setDisable(RampOnlyCheckbox.isSelected());
            SecondsSpinner.setDisable(RampOnlyCheckbox.isSelected());
        });
        ObservableList<String> items = FXCollections.observableArrayList(Arrays.asList("Repeat", "Shuffle"));
        AvavilableAmbienceChoiceBox.setItems(items);
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public List<Session.PlaybackItem> getPlaybackItemList() {
        return playbackItemList;
    }

// Button Actions
    public void accept() {
        double duration = getNewDuration().toMillis();
        for (Session.PlaybackItem i : playbackItemList) {i.setDuration(duration);}
        accepted = true;
        close();
    }
    public void cancel() {
        accepted = false;
        for (Session.PlaybackItem i : playbackItemList) {i.getAmbience().clearambience();}
        close();
    }

// Utility Methods
    private Duration getNewDuration() {
        Duration tempduration = Duration.ZERO;
        tempduration = tempduration.add(Duration.hours(HoursSpinner.getValue()));
        tempduration = tempduration.add(Duration.minutes(MinutesSpinner.getValue()));
        tempduration = tempduration.add(Duration.seconds(SecondsSpinner.getValue()));
        return tempduration;
    }
}