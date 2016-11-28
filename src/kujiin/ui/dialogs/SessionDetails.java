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
import kujiin.ui.dialogs.boilerplate.ModalDialog;
import kujiin.util.SessionPart;
import kujiin.util.Util;
import kujiin.xml.Preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionDetails extends ModalDialog {
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

    public SessionDetails(MainController Root, Stage parent, boolean minimizeparent, List<SessionPart> itemsinsession) {
        super(Root, parent, minimizeparent);
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
            for (SessionPart i : itemsinsession) {
                series.getData().add(new XYChart.Data<>(i.getNameForChart(), i.getduration().toMinutes()));
                totalsessionduration = totalsessionduration.add(i.getduration());
                if (i.getduration().greaterThan(highestduration)) {highestduration = i.getduration();}
                completedgoalsitems.addAll(i.getGoalscompletedthissession().stream().map(x -> String.format("%s: %s Hours Completed (%s Current)", i.name, x.getDuration(), i.getduration().toHours())).collect(Collectors.toList()));
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
            CloseButton.setOnAction(event -> close());
        } catch (IOException ignored) {}
    }
    public SessionDetails(MainController Root, Stage parent, boolean minimizeparent, kujiin.xml.Session session) {
        super(Root, parent, minimizeparent);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../assets/fxml/SessionDetails_Individual.fxml"));
            fxmlLoader.setController(this);
            Scene defaultscene = new Scene(fxmlLoader.load());
            setScene(defaultscene);
            setResizable(false);
            SessionNumbersAxis.setLabel("Minutes");
            setTitle("Session Details");
            DatePracticedTextField.setText(session.getDate_Practiced().format(Util.dateFormat));
            DatePracticedTextField.setEditable(false);
            XYChart.Series<String, java.lang.Number> series = new XYChart.Series<>();
            List<Integer> values = new ArrayList<>();
            for (SessionPart i : Root.getSessionParts(0, 16)) {
                int duration = (int) session.getduration(i).toMinutes();
                values.add(duration);
                String name;
                if (i.number == 0) {name = "Pre";}
                else if (i.number == 15) {name = "Post";}
                else {name = Preferences.ALLNAMES.get(i.number);}
                series.getData().add(new XYChart.Data<>(name, duration));
            }
            SessionBarChart.getData().add(series);
            SessionBarChart.setLegendVisible(false);
            SessionNumbersAxis.setUpperBound(Util.list_getmaxintegervalue(values));
            SessionDurationTextField.setText(Util.formatdurationtoStringSpelledOut(session.gettotalsessionduration(), SessionDurationTextField.getLayoutBounds().getWidth()));
            SessionDurationTextField.setEditable(false);
            SessionBarChart.requestFocus();
            CloseButton.setOnAction(event -> close());
        } catch (IOException | NullPointerException ignored) {}
    }

    @Override
    public void close() {
        super.close();
        if (Root.getStage().isIconified()) {Root.getStage().setIconified(false);}
    }

}