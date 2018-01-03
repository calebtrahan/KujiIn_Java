package kujiin.ui.playback;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Session;

import java.io.IOException;

public class SessionComplete extends StyledStage {
    private final Session session;
    public Label TopLabel;
    public Label DurationCompletedLabel;
    public BarChart<String, Number> SessionBarChart;
    public CheckBox AddSessionNotesCheckbox;
    public TextArea SessionNotesTextArea;
    public Button ReturnToPlayerButton;
    public Button ReturnToCreatorButton;
    public Button ExitProgramButton;
    private SessionCompleteDirections sessionCompleteDirections;

    public SessionComplete(Session session, boolean sessioncomplete) {
        this.session = session;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            String sessionsummary;
            if (sessioncomplete) {sessionsummary = "Session Completed";}
            else {sessionsummary = "Session Ended";}
            setTitle(sessionsummary);
            TopLabel.setText(sessionsummary);
            String tooltiptext = "You've Completed " + Util.formatdurationtoStringSpelledOut(session.getSessionPracticedTime(), 1000.0);
            String text = "You've Completed " + Util.formatdurationtoStringDecimalWithColons(session.getSessionPracticedTime());
            DurationCompletedLabel.setText(text);
            DurationCompletedLabel.setTooltip(new Tooltip(tooltiptext));
            if (! sessioncomplete) {
                AddSessionNotesCheckbox.setVisible(false);
                SessionNotesTextArea.setVisible(false);
            } else {
                AddSessionNotesCheckbox.setSelected(false);
                SessionNotesTextArea.setDisable(true);
                AddSessionNotesCheckbox.selectedProperty().addListener(observable -> SessionNotesTextArea.setDisable(! AddSessionNotesCheckbox.isSelected()));
            }
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            for (PlaybackItem i : session.getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getPracticeTime()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getExpectedDuration()));
            }
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            SessionBarChart.requestFocus();
            ExitProgramButton.setOnAction(event -> {sessionCompleteDirections = SessionCompleteDirections.EXITPROGRAM; close();});
            ReturnToPlayerButton.setOnAction(event -> {sessionCompleteDirections = SessionCompleteDirections.KEEPPLAYEROPEN; close();});
            ReturnToCreatorButton.setOnAction(event -> {sessionCompleteDirections = SessionCompleteDirections.CLOSEPLAYER; close();});
        } catch (IOException ignored) {ignored.printStackTrace();}
    }

// Getters And Setters
    public boolean needtosetNotes() {
        return AddSessionNotesCheckbox.isSelected() && ! SessionNotesTextArea.getText().isEmpty();
    }
    public String getNotes() {
        return session.getNotes();
    }
    public SessionCompleteDirections getSessionCompleteDirections() {
        return sessionCompleteDirections;
    }

    // Button Actions
    @Override
    public void close() {
        if (sessionCompleteDirections == null) {sessionCompleteDirections = SessionCompleteDirections.KEEPPLAYEROPEN;}
        if (needtosetNotes()) {session.setNotes(SessionNotesTextArea.getText());}
        super.close();
    }


    enum SessionCompleteDirections {
        EXITPROGRAM, KEEPPLAYEROPEN, CLOSEPLAYER
    }
}