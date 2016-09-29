package kujiin.ui.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.xml.Options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public SessionDetails(Options options, List<SessionPart> itemsinsession) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionCompleteDialog.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            options.setStyle(this);
            this.setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            setTitle("Session Details");
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            Duration totalsessionduration = new Duration(0);
            Duration highestduration = Duration.ZERO;
            ObservableList<String> completedgoalsitems = FXCollections.observableArrayList();
            for (SessionPart i : itemsinsession) {
                series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getduration().toMinutes()));
                totalsessionduration = totalsessionduration.add(i.getduration());
                if (i.getduration().greaterThan(highestduration)) {highestduration = i.getduration();}
                completedgoalsitems.addAll(i.getGoalscompletedthissession().stream().map(x -> String.format("%s: %s Hours Completed (%s Current)", i.name, x.getGoal_Hours(), i.getduration().toHours())).collect(Collectors.toList()));
            }
            if (completedgoalsitems.size() > 0) {
                GoalsCompletedTopLabel.setText(completedgoalsitems.size() + " Goals Completed This Session");
                GoalsCompletedListView.setItems(completedgoalsitems);
            }
            else {GoalsCompletedTopLabel.setText("No Goals Completed This Session");}
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(totalsessionduration, SessionDurationTextField.getLayoutBounds().getWidth()));
            SessionDurationTextField.setEditable(false);
            AverageDurationTextField.setText(Util.formatdurationtoStringSpelledOut(Duration.millis(highestduration.toMillis() / itemsinsession.size()), AverageDurationTextField.getLayoutBounds().getWidth()));
            MostProgressTextField.setText(Util.formatdurationtoStringSpelledOut(highestduration, MostProgressTextField.getLayoutBounds().getWidth()));
            SessionBarChart.requestFocus();
        } catch (IOException e) {}
    }
    public SessionDetails(Options options, kujiin.xml.Session session) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assets/fxml/SessionDetails_Individual.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            options.setStyle(this);
            this.setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            setTitle("Session Details");
            DatePracticedTextField.setText(session.getDate_Practiced());
            DatePracticedTextField.setEditable(false);
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                int duration = session.getsessionpartduration(i);
                values.add(duration);
                String name;
                if (i == 0) {name = "Pre";}
                else if (i == 15) {name = "Post";}
                else {name = kujiin.xml.Options.ALLNAMES.get(i);}
                series.getData().add(new XYChart.Data<>(name, duration));
            }
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            SessionNumbersAxis.setUpperBound(Util.list_getmaxintegervalue(values));
            SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(new Duration((session.getTotal_Session_Duration() * 60) * 1000), SessionDurationTextField.getLayoutBounds().getWidth()));
            SessionDurationTextField.setEditable(false);
            SessionBarChart.requestFocus();
        } catch (IOException | NullPointerException e) {}
    }

    public void closeDialog(ActionEvent actionEvent) {
        close();
    }

    class CompletedGoalsAtEndOfSessionBinding {
        private StringProperty sessionpartname;
        private StringProperty practicedhours;
        private StringProperty goalhours;
        private StringProperty dateset;
        private IntegerProperty daysittooktocomplete;

        public CompletedGoalsAtEndOfSessionBinding(String sessionpartname, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
            this.sessionpartname = new SimpleStringProperty(sessionpartname);
            this.practicedhours = new SimpleStringProperty(practicedhours);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.dateset = new SimpleStringProperty(dateset);
            this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
        }
    }
}