package kujiin.ui.creation;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.ConfirmationDialog;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetDurationWithAmbienceOption extends StyledStage {
    private final Preferences preferences;
    private Duration minduration = Duration.minutes(1.0);
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Spinner<Integer> SecondsSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    public CheckBox QuickAddAmbienceCheckbox;
    public ChoiceBox<String> QuickAddAmbienceChoiceBox;
    private boolean accepted = false;
    private boolean quickaddambience = false;
    private List<PlaybackItem> playbackItemList;
    private int missingambiencecount = 0;

    public SetDurationWithAmbienceOption(Preferences preferences, AvailableAmbiences availableAmbiences, List<PlaybackItem> playbackItemList, boolean quickaddambienceoption) {
        this.preferences = preferences;
        try {
            this.playbackItemList = playbackItemList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/SetDurationAndAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            StringBuilder stringBuilder = new StringBuilder();
            if (playbackItemList.size() == 1 && playbackItemList.get(0).getExpectedDuration() > 0.0) { stringBuilder.append("Edit "); }
            else { stringBuilder.append("Set "); }
            if (playbackItemList.size() > 1) {stringBuilder.append(playbackItemList.size()).append(" Durations");}
            else {stringBuilder.append(playbackItemList.get(0).getName()).append(" Duration");}
            setTitle(stringBuilder.toString());
            long hours = 0;
            long minutes = 0;
            long seconds = 0;
            if (playbackItemList.size() == 1) {
                long millis = (long) playbackItemList.get(0).getExpectedDuration();
                hours = TimeUnit.MILLISECONDS.toHours(millis);
                millis -= TimeUnit.HOURS.toMillis(hours);
                minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
                millis -= TimeUnit.MINUTES.toMillis(minutes);
                seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            }
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, (int) hours, preferences.getCreationOptions().getScrollincrement()));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) minutes, preferences.getCreationOptions().getScrollincrement()));
            SecondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) seconds, preferences.getCreationOptions().getScrollincrement()));
            QuickAddAmbienceCheckbox.setVisible(quickaddambienceoption);
            QuickAddAmbienceChoiceBox.setVisible(quickaddambienceoption);
            setScrollListeners();
            boolean hassomeambience = false;
            for (PlaybackItem i : playbackItemList) {
                if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {hassomeambience = true;}
                else {missingambiencecount++;}
            }
            QuickAddAmbienceCheckbox.setDisable(! hassomeambience);
            QuickAddAmbienceChoiceBox.setDisable(true);
            if (! hassomeambience) {QuickAddAmbienceCheckbox.setTooltip(new Tooltip("Cannot Add As There Is No Ambience For Any Playback Items"));}
            QuickAddAmbienceCheckbox.selectedProperty().addListener(observable -> {
                QuickAddAmbienceChoiceBox.setDisable(! QuickAddAmbienceCheckbox.isSelected());
                if (QuickAddAmbienceCheckbox.isSelected() && missingambiencecount > 0) {
                    if (! new ConfirmationDialog(preferences, "Missing Ambience", "Missing Ambience For " + missingambiencecount + " Playback Items", "Add Partial Ambience?").getResult()) {
                        QuickAddAmbienceCheckbox.setSelected(false);
                    }
                }
            });
            QuickAddAmbienceChoiceBox.setItems(FXCollections.observableArrayList("Repeat", "Shuffle"));
            QuickAddAmbienceChoiceBox.getSelectionModel().select(1);
        } catch (IOException e) {e.printStackTrace();}
    }
    private void setScrollListeners() {
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
    }

// Getters And Setters
    public boolean isAccepted() {
        return accepted;
    }
    public List<PlaybackItem> getPlaybackItemList() {
        return playbackItemList;
    }
    public boolean isQuickaddambience() {
        return quickaddambience;
    }
    public int getQuickAddAmbienceType() {return QuickAddAmbienceChoiceBox.getSelectionModel().getSelectedIndex();}
    public double getDuration() {return getNewDuration().toMillis();}

// Button Actions
    public void accept() {
        double expectedduration = getNewDuration().toMillis();
        if (getNewDuration().lessThan(minduration)) {
            new InformationDialog(preferences, "Cannot Add", "Cannot Add Item(s)", "Duration Is Less Than 1 Minute");
            return;
        }
        double fadedurations = 0.0;
    // Fade Animations
        for (PlaybackItem i : playbackItemList) {i.setExpectedDuration(expectedduration);}
        if (QuickAddAmbienceCheckbox.isVisible()) {quickaddambience = QuickAddAmbienceCheckbox.isSelected();}
        accepted = true;
        close();
    }
    public void cancel() {
        accepted = false;
        for (PlaybackItem i : playbackItemList) {i.getAmbience().clearambience();}
        close();
    }

// Presets
    public void setfiveminutes() {
        HoursSpinner.getValueFactory().setValue(0);
        MinutesSpinner.getValueFactory().setValue(5);
        SecondsSpinner.getValueFactory().setValue(0);
    }
    public void settenminutes() {
        HoursSpinner.getValueFactory().setValue(0);
        MinutesSpinner.getValueFactory().setValue(10);
        SecondsSpinner.getValueFactory().setValue(0);
    }
    public void setfifteenminutes() {
        HoursSpinner.getValueFactory().setValue(0);
        MinutesSpinner.getValueFactory().setValue(15);
        SecondsSpinner.getValueFactory().setValue(0);
    }
    public void setthirtyminutes() {
        HoursSpinner.getValueFactory().setValue(0);
        MinutesSpinner.getValueFactory().setValue(30);
        SecondsSpinner.getValueFactory().setValue(0);
    }
    public void setonehour() {
        HoursSpinner.getValueFactory().setValue(1);
        MinutesSpinner.getValueFactory().setValue(0);
        SecondsSpinner.getValueFactory().setValue(0);
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