package kujiin.ui.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.xml.PlaybackItem;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AdjustDuration extends StyledStage {
    public CheckBox RampOnlyCheckbox;
    public Spinner<Integer> HoursSpinner;
    public Spinner<Integer> MinutesSpinner;
    public Spinner<Integer> SecondsSpinner;
    public Button AcceptButton;
    public Button CancelButton;
    private Duration newduration;
    private boolean accepted = false;

    public AdjustDuration(PlaybackItem playbackItem) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AdjustDuration.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            if (playbackItem.getExpectedDuration() > 0.0) {setTitle("Edit " + playbackItem.getName() + " Duration");}
            else {setTitle("Set " + playbackItem.getName() + " Duration");}
            long millis = (long) playbackItem.getExpectedDuration();
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            millis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            millis -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, (int) hours));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) minutes));
            SecondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, (int) seconds));
            setListeners();
        } catch (IOException e) {e.printStackTrace();}
    }
    public AdjustDuration(int numberofitems) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AdjustDuration.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Set Duration For " + numberofitems + " Playback items");
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            SecondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));
            RampOnlyCheckbox.setVisible(false);
            setListeners();
        } catch (IOException e) {e.printStackTrace();}
    }
    public AdjustDuration(String titletext) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/AdjustDuration.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle(titletext);
            HoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0));
            MinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            SecondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));
            RampOnlyCheckbox.setVisible(false);
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
    public Duration getNewduration() {
        return newduration;
    }
    public boolean isAccepted() {
        return accepted;
    }

// Button Actions

    public void accept() {
        Duration tempduration = Duration.ZERO;
        tempduration = tempduration.add(Duration.hours(HoursSpinner.getValue()));
        tempduration = tempduration.add(Duration.minutes(MinutesSpinner.getValue()));
        tempduration = tempduration.add(Duration.seconds(SecondsSpinner.getValue()));
        if (tempduration.greaterThan(Duration.ZERO)) {
            newduration = tempduration;
            accepted = true;
            close();
        }
    }
    public void cancel() {
        accepted = false;
        close();
    }
    public void ramptoggled() {}

}