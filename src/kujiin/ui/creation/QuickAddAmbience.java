package kujiin.ui.creation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.xml.AvailableAmbiences;
import kujiin.xml.Preferences;
import kujiin.xml.Session;

import java.io.IOException;
import java.util.List;

public class QuickAddAmbience extends StyledStage {
    public RadioButton RepeatRadioButton;
    public RadioButton ShuffleRadioButton;
    public Button AcceptButton;
    public Button CancelButton;
    private List<Session.PlaybackItem> playbackItemList;
    private AvailableAmbiences availableAmbiences;
    private boolean accepted = false;
    private Preferences preferences;
    private QuickAmbienceType quickAmbienceType;

    public QuickAddAmbience(Preferences preferences, AvailableAmbiences availableAmbiences, List<Session.PlaybackItem> playbackItemList) {
        this.preferences = preferences;
        try {
            this.availableAmbiences = availableAmbiences;
            this.playbackItemList = playbackItemList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/QuickAddAmbience.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            setTitle("Quick Add Ambience");
        } catch (IOException e) {e.printStackTrace();}
    }

// Getters And Setters
    public List<Session.PlaybackItem> getPlaybackItemList() {return playbackItemList;}
    public boolean isAccepted() {
        return accepted;
    }

// Button Methods
    public void repeatbuttonselected() {
        RepeatRadioButton.setSelected(true);
        ShuffleRadioButton.setSelected(false);
        quickAmbienceType = QuickAmbienceType.Repeat;
    }
    public void shufflebuttonselected() {
        RepeatRadioButton.setSelected(false);
        ShuffleRadioButton.setSelected(true);
        quickAmbienceType = QuickAmbienceType.Shuffle;
    }
    public void addambience() {
        int missingambiencecount = 0;
        for (Session.PlaybackItem i : playbackItemList) {
            if (availableAmbiences.getsessionpartAmbience(i.getCreationindex()).hasAny()) {
                Duration duration;
                if (i.isRampOnly()) {duration = Duration.minutes(1);}
                else {duration = Duration.millis(i.getDuration());}
                i.getAmbience().clearambience();
                if (quickAmbienceType == QuickAmbienceType.Repeat) {i.getAmbience().addavailableambience_repeat(duration, availableAmbiences.getsessionpartAmbience(i.getCreationindex()));}
                else if (quickAmbienceType == QuickAmbienceType.Shuffle) {i.getAmbience().addavailableambience_shuffle(duration, availableAmbiences.getsessionpartAmbience(i.getCreationindex()));}
                i.getAmbience().setEnabled(true);
            } else {i.getAmbience().setEnabled(false); missingambiencecount++;}
        }
        if (missingambiencecount > 0) {
            new InformationDialog(preferences, "Missing Ambience", "Missing Ambience For " + missingambiencecount + " Playback Items",
                    "Added " + quickAmbienceType.toString() + " Ambience For " + (playbackItemList.size() - missingambiencecount) + " Playback Items");
        }
        accepted = true;
        close();
    }

    enum QuickAmbienceType {
        Repeat, Shuffle
    }
}
