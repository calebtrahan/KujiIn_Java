package kujiin.widgets;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import kujiin.MainController;
import kujiin.Tools;
import kujiin.interfaces.Widget;
import kujiin.xml.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProgressAndGoalsWidget implements Widget {
    public static String[] GOALCUTNAMES = {"Pre/Post", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Total"};
    private ComboBox<String> CutSelectorComboBox;
    private Integer cutindex;
// Progress Tracker Fields
    private Sessions Sessions;
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button DetailedCutProgressButton;
    private Button SessionListButton;
    private Button PrematureEndingsButton;
// Goals Fields
    private CurrentGoals CurrentGoals;
    private CompletedGoals CompletedGoals;
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private Button CompletedGoalsButton;
    private Label PracticedHours;
    private Label GoalHours;
    private ProgressBar GoalProgress;
    private Label TopLabel;

    public ProgressAndGoalsWidget(MainController mainController) {
        NewGoalButton = mainController.newgoalButton;
        CurrentGoalsButton = mainController.viewcurrrentgoalsButton;
        CompletedGoalsButton = mainController.viewcompletedgoalsButton;
        PracticedHours = mainController.goalscurrrentvalueLabel;
        GoalHours = mainController.goalssettimeLabel;
        GoalProgress = mainController.goalsprogressbar;
        CutSelectorComboBox = mainController.GoalCutComboBox;
        TopLabel = mainController.GoalTopLabel;
        TotalTimePracticed = mainController.TotalTimePracticed;
        NumberOfSessionsPracticed = mainController.NumberOfSessionsPracticed;
        AverageSessionDuration = mainController.AverageSessionDuration;
        PreAndPostOption = mainController.PrePostSwitch;
        DetailedCutProgressButton = mainController.ShowCutProgressButton;
        SessionListButton = mainController.ListOfSessionsButton;
        PrematureEndingsButton = mainController.PrematureEndingsButton;
        Sessions = new Sessions();
        CurrentGoals = new CurrentGoals();
        CompletedGoals = new CompletedGoals();
        Service<Void> getsessions = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Sessions.unmarshall();
                        return null;
                    }
                };
            }
        };
        getsessions.setOnRunning(event -> loading());
        getsessions.setOnFailed(event -> updateprogressui());
        getsessions.setOnSucceeded(event -> updateprogressui());
        getsessions.start();
        TotalTimePracticed.setOnKeyTyped(MainController.noneditabletextfield);
        NumberOfSessionsPracticed.setOnKeyTyped(MainController.noneditabletextfield);
        AverageSessionDuration.setOnKeyTyped(MainController.noneditabletextfield);
        ObservableList<String> cutnames = FXCollections.observableArrayList(GOALCUTNAMES);
        CutSelectorComboBox.setItems(cutnames);
        CutSelectorComboBox.setOnAction(this::cutselectionchanged);
        Service<Void> getcurrentgoals = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CurrentGoals.unmarshall();
                        return null;
                    }
                };
            }
        };
        Service<Void> getcompletedgoals = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CompletedGoals.unmarshall();
                        return null;
                    }
                };
            }
        };
        getcompletedgoals.setOnSucceeded(event -> updateprogressui());
        getcompletedgoals.setOnFailed(event -> updateprogressui());
        getcompletedgoals.start();
        getcurrentgoals.setOnSucceeded(event -> updateprogressui());
        getcurrentgoals.setOnFailed(event -> updateprogressui());
        getcurrentgoals.start();
    }

// Getters And Setters
    public Sessions getSessions() {return Sessions;}
    public kujiin.xml.CurrentGoals getCurrentGoals() {
        return CurrentGoals;
    }
    public kujiin.xml.CompletedGoals getCompletedGoals() {
        return CompletedGoals;
    }

// Button Actions
    public void cutselectionchanged(ActionEvent actionEvent) {
        try {
            cutindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            if (cutindex == -1) {resetallvalues();}
            else {
                updateprogressui();
                updategoalsui();
            }
        } catch (NullPointerException ignored) {resetallvalues();}
    }
    public void displaydetailedcutprogress() {
        if (Sessions.getSession() != null) {new DisplayCutTotalsDialog(Sessions.getSession());}
        else {Tools.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            Tools.showinformationdialog("Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {new DisplaySessionListDialog(null, Sessions.getSession()).showAndWait();}
    }
    public void displayprematureendings() {
        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Select A Cut"); return;}
        ArrayList<Session> prematuresessionlist = Sessions.getsessionswithprematureendings(cutindex);
        if (prematuresessionlist.size() > 0) {
            DisplayPrematureEndingsDialog a = new DisplayPrematureEndingsDialog(null, prematuresessionlist);
            a.showAndWait();
        } else {Tools.showinformationdialog("Cannot Display", "Nothing To Display", "No Sessions Ended Prematurely To Display");}
    }
    public void setnewgoal() {
        if (cutindex == -1) {Tools.showinformationdialog("Information","No Cut Selected", "Select A Cut To Add A Goal To"); return;}
        SetANewGoalDialog setANewGoalDialog = new SetANewGoalDialog(cutindex, this);
        setANewGoalDialog.showAndWait();
        if (setANewGoalDialog.isAccepted()) {
            try {CurrentGoals.add(cutindex, new CurrentGoals.CurrentGoal(setANewGoalDialog.getGoaldate(), setANewGoalDialog.getGoalhours()));}
            catch (JAXBException ignored) {Tools.showerrordialog("Error", "Couldn't Add Goal", "Check File Permissions");}
        }
        updategoalsui();
    }
    public void displaycurrentgoals() {
        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Display Current Goals"); return;}
        if (! CurrentGoals.goalsexist(cutindex)) {Tools.showinformationdialog("Information", "No Goals Exist For " + GOALCUTNAMES[cutindex], "Please Add A Goal For " + GOALCUTNAMES[cutindex]); return;}
        List<CurrentGoals.CurrentGoal> goalslist = CurrentGoals.getallcutgoals(cutindex);
        if (goalslist == null) {Tools.showinformationdialog("Information", "No Goals To Display", "Set A New Goal First"); return;}
        new DisplayCurrentGoalsDialog(goalslist, Tools.convertminutestodecimalhours(Sessions.getpracticedtimeinminutes(cutindex, PreAndPostOption.isSelected()))).showAndWait();
    }
    public void displaycompletedgoals() {
        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Display Completed Goals"); return;}
        if (! CompletedGoals.goalsexist(cutindex)) {Tools.showinformationdialog("Information", "No Goals Completed For " + GOALCUTNAMES[cutindex], "Complete A Goal For " + GOALCUTNAMES[cutindex]); return;}
        List<CompletedGoals.CompletedGoal> goalslist = CompletedGoals.getallcutgoals(cutindex);
        new DisplayCompletedGoalsDialog(goalslist).showAndWait();
    }
    public void goalpacing() {
        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Calculate Goal Pacing"); return;}
        new GoalPacingDialog(CurrentGoals.getgoal(cutindex, 0), CurrentGoals.getTotalGoals(), Sessions.getpracticedtimeinminutes(cutindex, PreAndPostOption.isSelected())).showAndWait();
    }

// Widget Implementation
    public void loading() {
        AverageSessionDuration.setText("Loading...");
        TotalTimePracticed.setText("Loading...");
        NumberOfSessionsPracticed.setText("Loading...");
    }
    @Override
    public void disable() {
        TotalTimePracticed.setDisable(true);
        NumberOfSessionsPracticed.setDisable(true);
        AverageSessionDuration.setDisable(true);
        PreAndPostOption.setDisable(true);
        DetailedCutProgressButton.setDisable(true);
        SessionListButton.setDisable(true);
        PrematureEndingsButton.setDisable(true);
        NewGoalButton.setDisable(true);
        CurrentGoalsButton.setDisable(true);
        CompletedGoalsButton.setDisable(true);
        PracticedHours.setDisable(true);
        GoalHours.setDisable(true);
        GoalProgress.setDisable(true);
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
        NewGoalButton.setDisable(false);
        CurrentGoalsButton.setDisable(false);
        CompletedGoalsButton.setDisable(false);
        PracticedHours.setDisable(false);
        GoalHours.setDisable(false);
        GoalProgress.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
        PracticedHours.setText("-   ");
        GoalHours.setText("  -");
        GoalProgress.setProgress(0.0);
        if (cutindex != null && cutindex != -1) {
            TopLabel.setText("Set A Goal For");
        } else {TopLabel.setText("Select A Cut");}
    }
    @Override
    public boolean cleanup() {
        CurrentGoals.marshall();
        CompletedGoals.marshall();
        Sessions.marshall();
        return true;
    }

// Total Progress Specific Methods
    public void updateprogressui() {
    // Update Total Progress
        try {
            int averagesessionduration = Sessions.averagepracticetimeinminutes(cutindex, PreAndPostOption.isSelected());
            int totalminutespracticed = Sessions.getpracticedtimeinminutes(cutindex, PreAndPostOption.isSelected());
            int numberofsessionspracticed = Sessions.cutsessionscount(cutindex);
            String nonetext = "No Sessions";
            if (averagesessionduration != 0) {AverageSessionDuration.setText(Tools.minutestoformattedhoursandmins(averagesessionduration));}
            else {AverageSessionDuration.setText(nonetext);}
            if (totalminutespracticed != 0) {TotalTimePracticed.setText(Tools.minutestoformattedhoursandmins(totalminutespracticed));}
            else {TotalTimePracticed.setText(nonetext);}
            if (numberofsessionspracticed != 0) {NumberOfSessionsPracticed.setText(Integer.toString(numberofsessionspracticed));}
            else {NumberOfSessionsPracticed.setText(nonetext);}
        } catch (NullPointerException ignored) {}
    }
    public void selectcut(int cutindex) {
        if (cutindex == 0 || cutindex == 10) {CutSelectorComboBox.getSelectionModel().select(0);}
        else {CutSelectorComboBox.getSelectionModel().select(cutindex);}
    }

// Goal Specific Methods
// Goal Specific Methods
    public void updategoalsui() {
        try {
            String cutname = GOALCUTNAMES[cutindex];
            if (! CurrentGoals.goalsexist(cutindex)) {
                resetallvalues();
                return;
            }
            Double practiced = Tools.convertminutestodecimalhours(Sessions.getpracticedtimeinminutes(cutindex, PreAndPostOption.isSelected()));
//            System.out.println("Goal: " + practiced);
            Double goal = CurrentGoals.getgoal(cutindex, 0).getGoal_Hours();
            PracticedHours.setText("Current: " + practiced.toString() + " hrs");
            GoalHours.setText("Goal: " + goal.toString() + " hrs");
            GoalProgress.setProgress(practiced / goal);
            if (cutname.equals(GOALCUTNAMES[10])) { TopLabel.setText("Total Goal");}
            else if (cutname.equals(GOALCUTNAMES[0])) {TopLabel.setText("Pre + Post Goal");}
            else {TopLabel.setText(cutname + "'s Current Goal");}
        } catch (NullPointerException ignored) {}
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
    public static class DisplayCompletedGoalsDialog extends Stage {
        public TableView<CompletedGoal> currentgoaltable;
        public TableColumn<CompletedGoal, Integer> NumberColumn;
        public TableColumn<CompletedGoal, String> GoalTimeColumn;
        public TableColumn<CompletedGoal, String> CompletedOnColumn;
        public Button CloseButton;

        public DisplayCompletedGoalsDialog(List<CompletedGoals.CompletedGoal> completedgoals) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCompletedGoals.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Completed Goals");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CompletedGoal> completedGoals = FXCollections.observableArrayList();
            completedGoals.addAll(completedgoals.stream().map(i -> new CompletedGoal(completedgoals.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Completed())).collect(Collectors.toList()));
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
            GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            CompletedOnColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
            currentgoaltable.setItems(completedGoals);
        }

        public void closeDialog(Event event) {this.close();}

        public class CompletedGoal {
            public IntegerProperty goalid;
            public StringProperty goalhours;
            public StringProperty datecompleted;

            public CompletedGoal(int goalid, String goalhours, String datecompleted) {
                this.goalid = new SimpleIntegerProperty(goalid);
                this.goalhours = new SimpleStringProperty(goalhours);
                this.datecompleted = new SimpleStringProperty(datecompleted);
            }
        }
    }
    public static class DisplayCurrentGoalsDialog extends Stage {
        public TableView<CurrentGoalBinding> currentgoaltable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button CloseButton;

        public DisplayCurrentGoalsDialog(List<CurrentGoals.CurrentGoal> currentGoalList, double currentpracticedhours) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCurrentGoals.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
            NumberColumn.setStyle("-fx-alignment: CENTER;");
            GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            GoalTimeColumn.setStyle("-fx-alignment: CENTER;");
            DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
            DueDateColumn.setStyle("-fx-alignment: CENTER;");
            PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
            PercentCompleteColumn.setStyle("-fx-alignment: CENTER;");
            currentgoaltable.setItems(currentGoals);
        }

        public void closeDialog(Event event) {this.close();}
    }
    public static class DisplayPrematureEndingsDialog extends Stage {

        public TableView<PrematureEnding> prematureendingTable;
        public TableColumn<PrematureEnding, String> DateColumn;
        public TableColumn<PrematureEnding, String> LastCutPracticedColumn;
        public TableColumn<PrematureEnding, String> ExpectedSessionColumn;
        public Button CloseButton;
        public TableColumn<PrematureEnding, String> ReasonColumn;
        public ArrayList<Session> sessionwithprematureendings;

        public DisplayPrematureEndingsDialog(Parent parent, ArrayList<Session> sessionwithprematureendings) {
            this.sessionwithprematureendings = sessionwithprematureendings;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayPrematureEndings.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Premature Endings");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<PrematureEnding> prematureEndings = FXCollections.observableArrayList();
            for (Session i : sessionwithprematureendings) {
                prematureEndings.add(new PrematureEnding(i.getDate_Practiced(), i.getLast_Cut_Practiced_Before_Premature_Ending(), i.getExpected_Session_List(), i.getPremature_Ending_Reason()));
            }
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().date);
            LastCutPracticedColumn.setCellValueFactory(cellData -> cellData.getValue().lastcutpracticed);
            ExpectedSessionColumn.setCellValueFactory(cellData -> cellData.getValue().expectedsessionlist);
            ReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reason);
            prematureendingTable.setItems(prematureEndings);
        }

        public void closeDialog(Event event) {this.close();}

        public class PrematureEnding {
            public StringProperty date;
            public StringProperty lastcutpracticed;
            public StringProperty expectedsessionlist;
            public StringProperty reason;

            public PrematureEnding(String date, String lastcutpracticed, String expectedsessionlist, String reason) {
                this.date = new SimpleStringProperty(date);
                this.lastcutpracticed = new SimpleStringProperty(lastcutpracticed);
                this.expectedsessionlist = new SimpleStringProperty(expectedsessionlist);
                this.reason = new SimpleStringProperty(reason);
            }
        }

    }
    public static class GoalPacingDialog extends Stage implements Initializable {
        public Label SelectedGoalHours;
        public Button SelectADiffferentGoalButton;
        public Spinner<Integer> DaysSpinner;
        public Button CalculateButton;
        public Button CloseButton;
        private CurrentGoals.CurrentGoal currentGoal;
        private List<CurrentGoals.CurrentGoal> currentGoals;
        private double alreadypracticedhours;

        public GoalPacingDialog(CurrentGoals.CurrentGoal currentGoal, List<CurrentGoals.CurrentGoal> currentGoals, double alreadypracticedhours) {
            this.currentGoal = currentGoal;
            this.currentGoals = currentGoals;
            this.alreadypracticedhours = alreadypracticedhours;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Goal Pacing");}
            catch (IOException e) {e.printStackTrace();}
            SelectedGoalHours.setText(currentGoal.getGoal_Hours() + " Hours");
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            DaysSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        }

        // Getters And Setters
        public CurrentGoals.CurrentGoal getCurrentGoal() {
            return currentGoal;
        }
        public void setCurrentGoal(CurrentGoals.CurrentGoal currentGoal) {
            this.currentGoal = currentGoal;
        }

        // Button Actions
        public void selectanewgoal(ActionEvent actionEvent) {
            SelectGoalDialog selectGoalDialog = new SelectGoalDialog(currentGoals, alreadypracticedhours);
            selectGoalDialog.showAndWait();
            setCurrentGoal(selectGoalDialog.getSelectedgoal());
            if (getCurrentGoal() == null) {return;}
            SelectedGoalHours.setText(getCurrentGoal().getGoal_Hours() + " Hours");
        }
        public void calculate(ActionEvent actionEvent) {
            if (getCurrentGoal() == null) {Tools.showerrordialog("Error", "No Goal Selected", "Please Select A Goal"); return;}
            if (DaysSpinner.getValue() == 0) {Tools.showerrordialog("Error", "Cannot Calculate", "Days Cannot Be 0"); return;}
            Double goalhours = getCurrentGoal().getGoal_Hours();
            Double days = (double) DaysSpinner.getValue();
            Float hourstopractice = goalhours.floatValue() / days.floatValue();
            int minsaday = Tools.convertdecimalhourstominutes(hourstopractice.doubleValue());
            String formattedgoalhours = Tools.minstoformattedlonghoursandminutes(Tools.convertdecimalhourstominutes(goalhours));
            Tools.showinformationdialog("Calculation", "To Reach " + formattedgoalhours + " In " + days.intValue() + " Days:",
                    "Practice For " + Tools.minstoformattedlonghoursandminutes(minsaday) + " A Day");
        }
        public void closedialog(ActionEvent actionEvent) {close();}

    }
    public static class PrematureEndingDialog extends Stage {
        public TextArea Reason;
        public Button AcceptButton;
        public Button CancelButton;
        private kujiin.xml.Options Options;

        public PrematureEndingDialog(Options options) {
            Options = options;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/PrematureEndingDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Ending Session Prematurely");
            } catch (IOException e) {e.printStackTrace();}
        }

        public String getReason() {
            if (! Reason.getText().isEmpty()) {return Reason.getText();}
            else {return null;}
        }
        public void accepted(ActionEvent actionEvent) {
            this.close();
        }
        public void rejected(ActionEvent actionEvent) {
            Options.getSessionOptions().setPrematureendings(! Tools.getanswerdialog("Disable Premature Endings", "Disable Premature Endings Dialog", "This Will Keep This Session From Displaying In The Future"));
            Reason.setText("");
            this.close();
        }
    }
    public static class SelectGoalDialog extends Stage {
        public TableView<CurrentGoalBinding> currentgoaltable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button SelectButton;
        public Button CancelButton;
        public CurrentGoals.CurrentGoal selectedgoal;
        private List<CurrentGoals.CurrentGoal> currentGoalList;

        public SelectGoalDialog(List<CurrentGoals.CurrentGoal> currentGoalList, double currentpracticedhours) {
            this.currentGoalList = currentGoalList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Goal");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
            NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
            NumberColumn.setStyle("-fx-alignment: CENTER;");
            GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            GoalTimeColumn.setStyle("-fx-alignment: CENTER;");
            DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
            DueDateColumn.setStyle("-fx-alignment: CENTER;");
            PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
            PercentCompleteColumn.setStyle("-fx-alignment: CENTER;");
            currentgoaltable.setItems(currentGoals);
        }

        // Getters And Setters
        public CurrentGoals.CurrentGoal getSelectedgoal() {
            return selectedgoal;
        }
        public void setSelectedgoal(CurrentGoals.CurrentGoal selectedgoal) {
            this.selectedgoal = selectedgoal;
        }

        // Button Actions
        public void closeDialog(ActionEvent actionEvent) {close();}
        public void selectgoal(ActionEvent actionEvent) {
            int index = currentgoaltable.getSelectionModel().getSelectedIndex();
            if (index == -1) {Tools.showinformationdialog("Information", "No Goal Selected", "Select A Goal"); return;}
            setSelectedgoal(currentGoalList.get(index));
            close();
        }
    }
    public static class SetANewGoalDialog extends Stage {
        private final ProgressAndGoalsWidget progressAndGoalsWidget;
        public Spinner<Integer> GoalHoursSpinner;
        public Spinner<Integer> GoalMinutesSpinner;
        public DatePicker GoalDatePicker;
        public Button CancelButton;
        public Button OKButton;
        public Button CurrentGoalsButton;
        public Label TopLabel;
        private LocalDate goaldate;
        private Double goalhours;

        public SetANewGoalDialog(int cutindex, ProgressAndGoalsWidget progressAndGoalsWidget) {
            this.progressAndGoalsWidget = progressAndGoalsWidget;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("New Goal");}
            catch (IOException e) {e.printStackTrace();}
            GoalHoursSpinner.setEditable(true);
            GoalMinutesSpinner.setEditable(true);
            GoalDatePicker.setValue(LocalDate.now());
            try {
                int minutes = Tools.convertdecimalhourstominutes(progressAndGoalsWidget.getCurrentGoals().getgoal(cutindex, 0).getGoal_Hours());
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minutes / 60, Integer.MAX_VALUE, minutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minutes % 60, 59, minutes % 60));
            } catch (NullPointerException ignored) {
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            }
            TopLabel.setText("Set A New Goal For " + GOALCUTNAMES[cutindex]);
        }

        // Getters And Setters
        public LocalDate getGoaldate() {
            return goaldate;
        }
        public void setGoaldate(LocalDate goaldate) {
            this.goaldate = goaldate;
        }
        public Double getGoalhours() {
            return goalhours;
        }
        public void setGoalhours(Double goalhours) {
            this.goalhours = goalhours;
        }
        public boolean isAccepted() {return getGoalhours() != null && getGoaldate() != null;}

        // Button Actions
        public void cancelgoalsetting(Event event) {this.close();}
        public void Accept(Event event) {
            if (GoalMinutesSpinner.getValue() > 59) {
                Tools.showinformationdialog("Information", "Minutes Cannot Be Greater Than 59", "Select A Value Less Than 59"); return;}
            boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
            if (dategood) {
                int hours = GoalHoursSpinner.getValue();
                int minutes = GoalMinutesSpinner.getValue();
                double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
                setGoalhours(newhours);
                setGoaldate(GoalDatePicker.getValue());
                super.close();
            } else {
                Tools.showinformationdialog("Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
                setGoalhours(null);
                setGoaldate(null);
            }
        }
        public void viewcurrentgoals(Event event) {progressAndGoalsWidget.displaycurrentgoals();}
    }
    public static class GoalCompleted extends Stage {
        public Label GoalHours;
        public Button CloseButton;
        public Label CurrentHoursLabel;

        public GoalCompleted(CurrentGoals.CurrentGoal currentGoal, Double currentpracticedhours) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalCompleted.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Goal Achieved");}
            catch (IOException e) {e.printStackTrace();}
            GoalHours.setText(currentGoal.getGoal_Hours().toString());
            CurrentHoursLabel.setText(currentpracticedhours.toString());
            CloseButton.setOnAction(event -> close());
        }
    }
    public static class CurrentGoalBinding {
        private IntegerProperty goalid;
        private StringProperty goalhours;
        private StringProperty duedate;
        private StringProperty percentcomplete;

        public CurrentGoalBinding(int id, String goalhours, String duedate, String percentcomplete) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.duedate = new SimpleStringProperty(duedate);
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
        }
    }

}
