package kujiin.widgets;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProgressTrackerWidget implements Widget {
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button DetailedCutProgressButton;
    private Button SessionListButton;
    private Button PrematureEndingsButton;
    private Sessions sessions;

    public ProgressTrackerWidget(MainController mainController) {
        TotalTimePracticed = mainController.TotalTimePracticed;
        NumberOfSessionsPracticed = mainController.NumberOfSessionsPracticed;
        AverageSessionDuration = mainController.AverageSessionDuration;
        PreAndPostOption = mainController.PrePostSwitch;
        DetailedCutProgressButton = mainController.ShowCutProgressButton;
        SessionListButton = mainController.ListOfSessionsButton;
        PrematureEndingsButton = mainController.PrematureEndingsButton;
        sessions = new Sessions();
        Service<Void> getsessions = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        sessions.unmarshall();
                        return null;
                    }
                };
            }
        };
        getsessions.setOnRunning(event -> loadingui());
        getsessions.setOnFailed(event -> updateui());
        getsessions.setOnSucceeded(event -> updateui());
        getsessions.start();
        TotalTimePracticed.setOnKeyTyped(MainController.noneditabletextfield);
        NumberOfSessionsPracticed.setOnKeyTyped(MainController.noneditabletextfield);
        AverageSessionDuration.setOnKeyTyped(MainController.noneditabletextfield);
    }

// Getters And Setters
    public Sessions getSessions() {return sessions;}

// Button Actions
    public void displaydetailedcutprogress() {
        if (sessions.getSession() != null) {new DisplayCutTotalsDialog(sessions.getSession());}
        else {
            Tools.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (sessions.getSession() == null || sessions.getSession().size() == 0) {
            Tools.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {new DisplaySessionListDialog(null, sessions.getSession()).showAndWait();}
    }
    public void displayprematureendings() {
        ArrayList<Session> prematuresessionlist = sessions.getsessionswithprematureendings();
        if (prematuresessionlist.size() > 0) {
            GoalsWidget.DisplayPrematureEndingsDialog a = new GoalsWidget.DisplayPrematureEndingsDialog(null, prematuresessionlist);
            a.showAndWait();
        } else {
            Tools.showinformationdialog("Cannot Display", "Nothing To Display", "No Premature Endings To Display");}
    }

// Widget Implementation
    @Override
    public void disable() {
        TotalTimePracticed.setDisable(true);
        NumberOfSessionsPracticed.setDisable(true);
        AverageSessionDuration.setDisable(true);
        PreAndPostOption.setDisable(true);
        DetailedCutProgressButton.setDisable(true);
        SessionListButton.setDisable(true);
        PrematureEndingsButton.setDisable(true);
    }
    @Override
    public void enable() {
        TotalTimePracticed.setDisable(false);
        NumberOfSessionsPracticed.setDisable(false);
        AverageSessionDuration.setDisable(false);
        PreAndPostOption.setDisable(false);
        DetailedCutProgressButton.setDisable(false);
        SessionListButton.setDisable(false);
        PrematureEndingsButton.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        TotalTimePracticed.setText("No Sessions Practiced");
        NumberOfSessionsPracticed.setText("0");
        AverageSessionDuration.setText("No Sessions Practiced");
    }

// Other Methods
    public void loadingui() {
        AverageSessionDuration.setText("Loading...");
        TotalTimePracticed.setText("Loading...");
        NumberOfSessionsPracticed.setText("Loading...");
    }
    public void updateui() {
        sessions.deletenonvalidsessions();
        int averagesessionduration = sessions.averagepracticetimeinminutes(PreAndPostOption.isSelected());
        int totalminutespracticed = sessions.totalpracticetimeinminutes(PreAndPostOption.isSelected());
        int numberofsessionspracticed = sessions.sessionscount();
        String nonetext = "No Sessions Practiced";
        if (averagesessionduration != 0) {AverageSessionDuration.setText(Tools.minutestoformattedhoursandmins(averagesessionduration));}
        else {AverageSessionDuration.setText(nonetext);}
        if (totalminutespracticed != 0) {TotalTimePracticed.setText(Tools.minutestoformattedhoursandmins(totalminutespracticed));}
        else {TotalTimePracticed.setText(nonetext);}
        if (numberofsessionspracticed != 0) {NumberOfSessionsPracticed.setText(Integer.toString(numberofsessionspracticed));}
        else {NumberOfSessionsPracticed.setText(nonetext);}
    }

// Subclasses/Dialogs
    public static class DisplaySessionListDialog extends Stage {

        public TableView<SessionRow> sessionsTableView;
        public TableColumn<SessionRow, String> DateColumn;
        public TableColumn<SessionRow, Integer> RinColumn;
        public TableColumn<SessionRow, Integer> KyoColumn;
        public TableColumn<SessionRow, Integer> TohColumn;
        public TableColumn<SessionRow, Integer> ShaColumn;
        public TableColumn<SessionRow, Integer> KaiColumn;
        public TableColumn<SessionRow, Integer> JinColumn;
        public TableColumn<SessionRow, Integer> RetsuColumn;
        public TableColumn<SessionRow, Integer> ZaiColumn;
        public TableColumn<SessionRow, Integer> ZenColumn;
        public TableColumn<SessionRow, Integer> TotalColumn;
        public Button CloseButton;
        private ObservableList<SessionRow> sessionlist = FXCollections.observableArrayList();


        public DisplaySessionListDialog(Parent parent, List<Session> sessionlist) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplaySessionList.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
            catch (IOException e) {e.printStackTrace();}
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            RinColumn.setCellValueFactory(cellData -> cellData.getValue().rin.asObject());
            KyoColumn.setCellValueFactory(cellData -> cellData.getValue().kyo.asObject());
            TohColumn.setCellValueFactory(cellData -> cellData.getValue().toh.asObject());
            ShaColumn.setCellValueFactory(cellData -> cellData.getValue().sha.asObject());
            KaiColumn.setCellValueFactory(cellData -> cellData.getValue().kai.asObject());
            JinColumn.setCellValueFactory(cellData -> cellData.getValue().jin.asObject());
            RetsuColumn.setCellValueFactory(cellData -> cellData.getValue().retsu.asObject());
            ZaiColumn.setCellValueFactory(cellData -> cellData.getValue().zai.asObject());
            ZenColumn.setCellValueFactory(cellData -> cellData.getValue().zen.asObject());
            TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total.asObject());
            ArrayList<SessionRow> sessionRows = new ArrayList<>();
            int count = 1;
            for (Session i : sessionlist) {
                sessionRows.add(new SessionRow(count, i.getDate_Practiced(), i.getPresession_Duration(), i.getRin_Duration(),
                        i.getKyo_Duration(), i.getToh_Duration(), i.getSha_Duration(), i.getKai_Duration(), i.getJin_Duration(),
                        i.getRetsu_Duration(), i.getZai_Duration(), i.getZen_Duration(), i.getPostsession_Duration(),
                        i.getTotal_Session_Duration()));
                count++;
            }
            ObservableList<SessionRow> rowlist = FXCollections.observableArrayList();
            rowlist.addAll(sessionRows);
            sessionsTableView.setItems(rowlist);
        }

        public void closeDialog(Event event) {this.close();}

        public class SessionRow {
            public IntegerProperty id;
            public StringProperty datepracticed;
            public IntegerProperty presession;
            public IntegerProperty rin;
            public IntegerProperty kyo;
            public IntegerProperty toh;
            public IntegerProperty sha;
            public IntegerProperty kai;
            public IntegerProperty jin;
            public IntegerProperty retsu;
            public IntegerProperty zai;
            public IntegerProperty zen;
            public IntegerProperty postsession;
            public IntegerProperty total;

            public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu, int zai, int zen, int postsession, int total) {
                this.id = new SimpleIntegerProperty(id);
                this.datepracticed = new SimpleStringProperty(datepracticed);
                this.presession = new SimpleIntegerProperty(presession);
                this.rin = new SimpleIntegerProperty(rin);
                this.kyo = new SimpleIntegerProperty(kyo);
                this.toh = new SimpleIntegerProperty(toh);
                this.sha = new SimpleIntegerProperty(sha);
                this.kai = new SimpleIntegerProperty(kai);
                this.jin = new SimpleIntegerProperty(jin);
                this.retsu = new SimpleIntegerProperty(retsu);
                this.zai = new SimpleIntegerProperty(zai);
                this.zen = new SimpleIntegerProperty(zen);
                this.postsession = new SimpleIntegerProperty(postsession);
                this.total = new SimpleIntegerProperty(total);
            }

            public String toString() {
                return String.format("%s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s > %s",
                        id.getValue(), datepracticed.getValue(), presession.getValue(), rin.getValue(), kyo.getValue(),
                        toh.getValue(), sha.getValue(), kai.getValue(), jin.getValue(), retsu.getValue(), zai.getValue(),
                        zen.getValue(), postsession.getValue(), total.getValue());
            }

            public StringProperty getDatepracticed() {
                return datepracticed;
            }
        }
    }
    public static class DisplayCutTotalsDialog extends Stage {
        public TableView<TotalProgressRow> progresstable;
        public TableColumn<TotalProgressRow, String> NameColumn;
        public TableColumn<TotalProgressRow, String> ProgressColumn;
        public TableColumn<TotalProgressRow, Integer> NumberColumn;
        private List<Session> allsessions;

        public DisplayCutTotalsDialog(List<Session> allsessions) {
            this.allsessions = allsessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCutTotalsDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
            catch (IOException e) {e.printStackTrace();}
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().number.asObject());
            NameColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            ProgressColumn.setCellValueFactory(cellData -> cellData.getValue().formattedduration);
            populatetable();
        }

        public void populatetable() {
            ArrayList<TotalProgressRow> allrows = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                int durationinmins = 0;
                for (Session x : allsessions) {durationinmins += x.getcutduration(i);}
                String duration;
                if (durationinmins > 0) {duration = Tools.minutestoformattedhoursandmins(durationinmins);}
                else {duration = "-";}
                allrows.add(new TotalProgressRow(i, Options.allnames.get(i), duration));
            }
            progresstable.getItems().addAll(allrows);
        }

        public class TotalProgressRow {
            private IntegerProperty number;
            private StringProperty name;
            private StringProperty formattedduration;

            public TotalProgressRow(Integer id, String name, String formattedduration) {
                this.number = new SimpleIntegerProperty(id);
                this.name = new SimpleStringProperty(name);
                this.formattedduration = new SimpleStringProperty(formattedduration);
            }
        }
    }

}
