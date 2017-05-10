package kujiin.ui.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import kujiin.util.Util;
import kujiin.xml.PlaybackItem;
import kujiin.xml.Session;

import java.io.IOException;
import java.util.stream.Collectors;

public class SessionDetails extends Stage {
    public BarChart<String, Number> SessionBarChart;
    public CategoryAxis SessionCategoryAxis;
    public NumberAxis SessionNumbersAxis;
    public TextField DatePracticedTextField;
    public TextField SessionDurationTextField;
    public Button CloseButton;
    public Label GoalsCompletedTopLabel;
    public ListView<String> GoalsCompletedListView;
    public TextField MostProgressTextField;
    public TextField AverageDurationTextField;

    public SessionDetails(Session session, String title) {
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
            ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
            for (PlaybackItem i : session.getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getPracticeTime()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getPracticeTime()));
                if (new Duration(i.getExpectedDuration()).greaterThan(highestduration)) {highestduration = new Duration(i.getExpectedDuration());}
                completedgoalsitems.addAll(i.getGoalsCompletedThisSession().stream().map(x -> String.format("%s: %s Hours Completed (%s Current)", i.getName(), x.getDuration(), new Duration(i.getExpectedDuration()).toHours())).collect(Collectors.toList()));
            }
            GoalsCompletedTopLabel.setText("Goals Completed This Session");
            if (completedgoalsitems.size() > 0) {GoalsCompletedListView.setItems(completedgoalsitems);}
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            Duration finalTotalsessionduration = totalsessionduration;
            Duration finalHighestduration = highestduration;
            setOnShowing(event -> {
                SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(finalTotalsessionduration, SessionDurationTextField.getLayoutBounds().getWidth()));
                AverageDurationTextField.setText(Util.formatdurationtoStringSpelledOut(Duration.millis(finalHighestduration.toMillis() / session.getPlaybackItems().size()), AverageDurationTextField.getLayoutBounds().getWidth()));
                MostProgressTextField.setText(Util.formatdurationtoStringSpelledOut(finalHighestduration, MostProgressTextField.getLayoutBounds().getWidth()));
            });
            SessionBarChart.requestFocus();
            CloseButton.setOnAction(event -> close());
        } catch (IOException ignored) {}
    }

}