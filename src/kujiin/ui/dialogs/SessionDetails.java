package kujiin.ui.dialogs;

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
import kujiin.xml.FavoriteSession;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Session;

import java.io.IOException;

public class SessionDetails extends StyledStage {
    public BarChart<String, Number> SessionBarChart;
    public CategoryAxis SessionCategoryAxis;
    public NumberAxis SessionNumbersAxis;
    public Label DurationCompletedLabel;
    public Button CloseButton;

    public SessionDetails(Session session, String title, boolean sessionjustcompleted) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            if (title == null) {setTitle("Session Details");}
            else {setTitle(title);}
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            Duration highestduration = Duration.ZERO;
            for (PlaybackItem i : session.getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getPracticeTime()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getPracticeTime()));
                if (new Duration(i.getExpectedDuration()).greaterThan(highestduration)) {highestduration = new Duration(i.getExpectedDuration());}
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (sessionjustcompleted) {stringBuilder.append("You've Completed ");
            } else {stringBuilder.append("Session Duration: ");}
            stringBuilder.append(Util.formatdurationtoStringSpelledOut(totalsessionduration, Double.MAX_VALUE));
            DurationCompletedLabel.setText(stringBuilder.toString());
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            Duration finalTotalsessionduration = totalsessionduration;
            SessionBarChart.requestFocus();
            CloseButton.setOnAction(event -> close());
        } catch (IOException ignored) {}
    }
    public SessionDetails(FavoriteSession favoriteSession, String title, boolean sessionjustcompleted) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/playback/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            if (title == null) {setTitle("Session Details");}
            else {setTitle(title);}
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            Duration highestduration = Duration.ZERO;
            for (PlaybackItem i : favoriteSession.getSession().getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getPracticeTime()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getPracticeTime()));
                if (new Duration(i.getExpectedDuration()).greaterThan(highestduration)) {highestduration = new Duration(i.getExpectedDuration());}
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (sessionjustcompleted) {stringBuilder.append("You've Completed ");
            } else {stringBuilder.append("Session Duration: ");}
            stringBuilder.append(Util.formatdurationtoStringSpelledOut(totalsessionduration, Double.MAX_VALUE));
            DurationCompletedLabel.setText(stringBuilder.toString());
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            Duration finalTotalsessionduration = totalsessionduration;
            SessionBarChart.requestFocus();
            CloseButton.setOnAction(event -> close());
        } catch (IOException ignored) {}
    }

}