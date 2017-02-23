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
import kujiin.ui.MainController;
import kujiin.util.Util;
import kujiin.xml.Session;

import java.io.IOException;
import java.util.stream.Collectors;

public class SessionDetails extends Stage {
    private MainController Root;
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

    public SessionDetails(Session session) {
        try {
            this.Root = Root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            setTitle("Session Details");
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            Duration highestduration = Duration.ZERO;
            ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
            for (Session.PlaybackItem i : session.getPlaybackItems()) {
                series.getData().add(new XYChart.Data<>(i.getName(), new Duration(i.getDuration()).toMinutes()));
                totalsessionduration = totalsessionduration.add(new Duration(i.getDuration()));
                if (new Duration(i.getDuration()).greaterThan(highestduration)) {highestduration = new Duration(i.getDuration());}
                completedgoalsitems.addAll(i.getGoalsCompletedThisSession().stream().map(x -> String.format("%s: %s Hours Completed (%s Current)", i.getName(), x.getDuration(), new Duration(i.getDuration()).toHours())).collect(Collectors.toList()));
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
//    public SessionDetails(kujiin.xml.Session session) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionDetails_Individual.fxml"));
//            fxmlLoader.setController(this);
//            Scene defaultscene = new Scene(fxmlLoader.load());
//            setScene(defaultscene);
//            setResizable(false);
//            SessionNumbersAxis.setLabel("Minutes");
//            setTitle("Session Details");
//            DatePracticedTextField.setText(session.getDate_Practiced().format(Util.dateFormat));
//            DatePracticedTextField.setEditable(false);
//            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
//            List<Integer> values = new ArrayList<>();
//            for (SessionItem i : Root.getSessionParts(0, 16)) {
//                int duration = (int) session.getduration(i).toMinutes();
//                values.add(duration);
//                String name;
//                if (i.index == 0) {name = "Pre";}
//                else if (i.index == 15) {name = "Post";}
//                else {name = Preferences.ALLNAMES.get(i.index);}
//                series.getData().add(new XYChart.Data<>(name, duration));
//            }
//            SessionBarChart.getData().add(series);
//            SessionBarChart.setLegendVisible(false);
//            Collections.sort(values);
//            SessionNumbersAxis.setUpperBound(values.get(values.size() - 1));
//            SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(session.gettotalsessionduration(), SessionDurationTextField.getLayoutBounds().getWidth()));
//            SessionDurationTextField.setEditable(false);
//            SessionBarChart.requestFocus();
//            CloseButton.setOnAction(event -> close());
//        } catch (IOException | NullPointerException ignored) {}
//    }

    @Override
    public void close() {
        super.close();
        if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}
    }

}