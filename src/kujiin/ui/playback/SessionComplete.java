package kujiin.ui.playback;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import kujiin.ui.boilerplate.StyledStage;
import kujiin.util.Util;
import kujiin.xml.Session;

import java.io.IOException;

public class SessionComplete extends StyledStage {
    public Label TopLabel;
    public Label DurationCompletedLabel;
    public BarChart<String, Number> SessionBarChart;
    public CategoryAxis SessionCategoryAxis;
    public NumberAxis SessionNumbersAxis;
    public Button CloseButton;

    public SessionComplete(Session session, boolean sessioncomplete) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            String sessionsummary;
            if (sessioncomplete) {sessionsummary = "Session Completed";}
            else {sessionsummary = "Session Ended";}
            setTitle(sessionsummary);
            TopLabel.setText(sessionsummary);
            if (sessioncomplete) {DurationCompletedLabel.setText("You've Completed " + Util.formatdurationtoStringSpelledOut(session.getSessionDuration(), 1000.0));}
            else {DurationCompletedLabel.setText("You've Completed " + Util.formatdurationtoStringSpelledOut(new Duration(session.getElapsedTime()), 1000.0));}
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            for (Session.PlaybackItem i : session.getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getElapsedTime()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getDuration()));
            }
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            SessionBarChart.requestFocus();
            CloseButton.setOnAction(event -> close());
        } catch (IOException ignored) {}
    }
}
