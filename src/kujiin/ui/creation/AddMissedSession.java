package kujiin.ui.creation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.ui.dialogs.alerts.InformationDialog;
import kujiin.util.Util;
import kujiin.xml.Ambience;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Preferences;
import kujiin.xml.Session;

import java.io.IOException;
import java.time.LocalDate;

public class AddMissedSession extends StyledStage {
    private final Preferences preferences;
    public ListView<String> SessionListView;
    public DatePicker DateSelector;
    public Button AddSessionButton;
    public Button CancelButton;
    private boolean accepted = false;

    public AddMissedSession(Preferences preferences, Session session) {
        this.preferences = preferences;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/creation/MissedSessionOverview.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            ObservableList<String> items = FXCollections.observableArrayList();
            items.add("Session Items:");
            int count = 1;
            for (PlaybackItem i : session.getPlaybackItems()) {
                items.add("> " + count + ". " + i.getName() + " (" + i.getdurationasString() + ")");
                count++;
            }
            items.add("Total Session Time: " + Util.formatdurationtoStringSpelledOutShort(session.getExpectedSessionDuration(), true));
            SessionListView.setItems(items);
            AddSessionButton.setOnAction(event -> addsession());
            CancelButton.setOnAction(event -> cancel());
        } catch (IOException e) {e.printStackTrace();}
    }

    public LocalDate getDate() {return DateSelector.getValue();}
    public boolean isAccepted() {
        return accepted;
    }
    private void addsession() {
        if (DateSelector.getValue() != null) {
            accepted = true;
            close();
        } else {
            new InformationDialog(preferences, "Invalid Date", "Session Cannot Be Added Without A Valid Date Practiced", null);
        }
    }
    private void cancel() {
        accepted = false;
        close();
    }
}
