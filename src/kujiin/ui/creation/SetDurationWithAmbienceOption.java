package kujiin.ui.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetDurationWithAmbienceOption extends StyledStage {
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Spinner<Integer> SecondsSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    public CheckBox QuickAddAmbienceCheckbox;
    private boolean accepted = false;
    private boolean quickaddambience = false;
    private List<PlaybackItem> playbackItemList;
    private Preferences preferences;
    private AvailableAmbiences availableAmbiences;
    private int missingambiencecount = 0;

    public SetDurationWithAmbienceOption(Preferences preferences, AvailableAmbiences availableAmbiences, List<PlaybackItem> playbackItemList, boolean quickaddambienceoption) {
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
            for (PlaybackItem i : playbackItemList) {if (! availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {missingambiencecount++;}}
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

    // Button Actions
    public void accept() {
        double duration = getNewDuration().toMillis();
        for (PlaybackItem i : playbackItemList) {i.setExpectedDuration(duration);}
        if (QuickAddAmbienceCheckbox.isVisible()) {quickaddambience = QuickAddAmbienceCheckbox.isSelected();}
        accepted = true;
        close();
    }
    public void cancel() {
        accepted = false;
        for (PlaybackItem i : playbackItemList) {i.getAmbience().clearambience();}
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