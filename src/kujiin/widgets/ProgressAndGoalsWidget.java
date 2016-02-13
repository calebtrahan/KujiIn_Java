package kujiin.widgets;

import javafx.beans.property.*;
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
import kujiin.xml.Goals;
import kujiin.xml.Options;
import kujiin.xml.Session;
import kujiin.xml.Sessions;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// TODO Finish Tying Current And Completed Goals Together
// TODO Finish Making EditGoalsDialog (Also Add A Select A Different Cut Feature, And Make It So They Can Pass Cutindex Data Back And Forth)
public class ProgressAndGoalsWidget implements Widget {
    public static String[] GOALCUTNAMES = {"Presession", "Rin", "Kyo", "Toh", "Sha", "Kai", "Jin", "Retsu", "Zai", "Zen", "Postsession", "Total"};
    private ComboBox<String> CutSelectorComboBox;
    private Integer cutindex;
// Progress Tracker Fields
    private Sessions Sessions;
    private TextField TotalTimePracticed;
    private TextField NumberOfSessionsPracticed;
    private TextField AverageSessionDuration;
    private CheckBox PreAndPostOption;
    private Button SessionListButton;
// Goals Fields
    private Goals Goals;
    private Button NewGoalButton;
    private Button CurrentGoalsButton;
    private TextField PracticedHours;
    private TextField PracticedMinutes;
    private TextField GoalHours;
    private TextField GoalMinutes;
    private ProgressBar GoalProgress;
    private Label TopLabel;
    private Label StatusBar;

    public ProgressAndGoalsWidget(MainController mainController) {
        NewGoalButton = mainController.newgoalButton;
        CurrentGoalsButton = mainController.viewcurrrentgoalsButton;
        PracticedHours = mainController.GoalPracticedHours;
        PracticedHours.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        PracticedMinutes = mainController.GoalPracticedMinutes;
        PracticedMinutes.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        GoalHours = mainController.GoalSetHours;
        GoalHours.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        GoalMinutes = mainController.GoalSetMinutes;
        GoalMinutes.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        GoalProgress = mainController.goalsprogressbar;
        CutSelectorComboBox = mainController.GoalCutComboBox;
        TopLabel = mainController.GoalTopLabel;
        StatusBar = mainController.GoalStatusBar;
        TotalTimePracticed = mainController.TotalTimePracticed;
        NumberOfSessionsPracticed = mainController.NumberOfSessionsPracticed;
        AverageSessionDuration = mainController.AverageSessionDuration;
        PreAndPostOption = mainController.PrePostSwitch;
        SessionListButton = mainController.ListOfSessionsButton;
        Sessions = new Sessions();
        Goals = new Goals();
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
        TotalTimePracticed.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        NumberOfSessionsPracticed.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        AverageSessionDuration.setOnKeyTyped(MainController.NONEDITABLETEXTFIELD);
        ObservableList<String> cutnames = FXCollections.observableArrayList(GOALCUTNAMES);
        CutSelectorComboBox.setItems(cutnames);
        CutSelectorComboBox.setOnAction(this::cutselectionchanged);
        Service<Void> getgoals = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Goals.unmarshall();
                        return null;
                    }
                };
            }
        };
        getgoals.setOnSucceeded(event -> updateprogressui());
        getgoals.setOnFailed(event -> updateprogressui());
        getgoals.start();
        PreAndPostOption.setOnAction(this::cutselectionchanged);
        cutindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
    }

// Getters And Setters
    public Sessions getSessions() {return Sessions;}
    public Goals getGoal() {
        return Goals;
    }
    public Integer getCutindex() {
        return cutindex;
    }
    public void setCutindex(Integer cutindex) {
        this.cutindex = cutindex;
    }

// Button Actions
    public void cutselectionchanged(ActionEvent actionEvent) {
        try {
            cutindex = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            PreAndPostOption.setDisable(cutindex != 11);
            if (cutindex != 11) {PreAndPostOption.setSelected(false);}
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
    public void setnewgoal() {
        if (cutindex == -1) {Tools.showinformationdialog("Information","No Cut Selected", "Select A Cut To Add A Goal To"); return;}
        SetANewGoalDialog setANewGoalDialog = new SetANewGoalDialog(cutindex, this);
        setANewGoalDialog.showAndWait();
        if (setANewGoalDialog.isAccepted()) {
            try {
                Goals.add(cutindex, new Goals.Goal(setANewGoalDialog.getGoaldate(), setANewGoalDialog.getGoalhours()));}
            catch (JAXBException ignored) {Tools.showerrordialog("Error", "Couldn't Add Goal", "Check File Permissions");}
        }
        updategoalsui();
    }
    public void opengoaleditor() {
        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Edit Its Goals"); return;}
        if (! Goals.goalsexist(cutindex, true)) {Tools.showinformationdialog("Information", "No Goals Exist For " + GOALCUTNAMES[cutindex], "Please Add A Goal For " + GOALCUTNAMES[cutindex]); return;}
        new EditGoalsDialog(this).showAndWait();
    }
//    public void goalpacing() {
//        if (cutindex == -1) {Tools.showinformationdialog("Information", "No Cut Selected", "Please Select A Cut To Calculate Goal Pacing"); return;}
//        new GoalPacingDialog(Goals.getgoal(cutindex, 0), Goals.getTotalGoals(), Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected())).showAndWait();
//    }

// Widget Implementation
    public void loading() {
        AverageSessionDuration.setText("-");
        TotalTimePracticed.setText("-");
        NumberOfSessionsPracticed.setText("-");
    }
    @Override
    public void disable() {
        TotalTimePracticed.setDisable(true);
        NumberOfSessionsPracticed.setDisable(true);
        AverageSessionDuration.setDisable(true);
        PreAndPostOption.setDisable(true);
        SessionListButton.setDisable(true);
        NewGoalButton.setDisable(true);
        CurrentGoalsButton.setDisable(true);
        PracticedHours.setDisable(true);
        PracticedMinutes.setDisable(false);
        GoalHours.setDisable(false);
        GoalMinutes.setDisable(false);
        GoalProgress.setDisable(true);
    }
    @Override
    public void enable() {
        TotalTimePracticed.setDisable(false);
        NumberOfSessionsPracticed.setDisable(false);
        AverageSessionDuration.setDisable(false);
        PreAndPostOption.setDisable(false);
        SessionListButton.setDisable(false);
        NewGoalButton.setDisable(false);
        CurrentGoalsButton.setDisable(false);
        PracticedHours.setDisable(false);
        PracticedMinutes.setDisable(false);
        GoalHours.setDisable(false);
        GoalMinutes.setDisable(false);
        GoalProgress.setDisable(false);
    }
    @Override
    public void resetallvalues() {
        TotalTimePracticed.setText("No Sessions");
        NumberOfSessionsPracticed.setText("No Sessions");
        AverageSessionDuration.setText("No Sessions");
        PracticedHours.setText("-");
        PracticedMinutes.setText("-");
        GoalHours.setText("-");
        GoalMinutes.setText("-");
        GoalProgress.setProgress(0.0);
        if (cutindex != null && cutindex != -1) {
            TopLabel.setText(GOALCUTNAMES[cutindex]);
//            if (cutindex == 10) {TopLabel.setText("Set A Total Goal");}
//            if (cutindex == 0) {TopLabel.setText("Set A Goal For " + GOALCUTNAMES[0]);}
//            else {TopLabel.setText("Set A Goal For " + GOALCUTNAMES[cutindex]);}
        } else {TopLabel.setText("Select A Cut");}
    }
    @Override
    public boolean cleanup() {
        Goals.marshall();
        Sessions.marshall();
        return true;
    }

// Total Progress Specific Methods
    public void updateprogressui() {
    // Update Total Progress
        try {
            int averagesessionduration = Sessions.averagepracticetimeinminutes(cutindex, PreAndPostOption.isSelected());
            int totalminutespracticed = Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected());
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
        CutSelectorComboBox.getSelectionModel().select(cutindex);
        setCutindex(cutindex);
    }

// Goal Specific Methods
    public ArrayList<String> precreationgoalchecks(ArrayList<Integer> textfieldtimes) {
        ArrayList<String> notgoodcuts = new ArrayList<>();
        for (int i = 0; i < textfieldtimes.size(); i++) {
            Integer val = textfieldtimes.get(i);
            if (! checkifgoalsetandlongenough(i, val)) {
                notgoodcuts.add(GOALCUTNAMES[i]);
            }
        }
        return notgoodcuts;
    }
    public boolean checkifgoalsetandlongenough(int cut_index, int duration) {
        try {
            List<kujiin.xml.Goals.Goal> currentGoals = Goals.sort(Goals.getallcutgoals(cut_index, false));
            int currentpracticedminutes = Sessions.getpracticedtimeinminutesforallsessions(cut_index, PreAndPostOption.isSelected());
            currentpracticedminutes += duration;
            return currentpracticedminutes >= currentGoals.get(currentGoals.size() - 1).getGoal_Hours().intValue();
        } catch (Exception e) {return false;}
    }
    public void updategoalsui() {
        try {
            int practicedminutes = Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected());
            Double goal = Goals.getgoal(cutindex, 0, false).getGoal_Hours();
            Integer hours = practicedminutes / 60;
            Integer minutes = practicedminutes % 60;
            PracticedHours.setText(hours.toString());
            PracticedMinutes.setText(minutes.toString());
            if (goal != null) {
                Double progress = Tools.convertminutestodecimalhours(practicedminutes, 2) / goal;
                goal *= 60;
                Integer hrs = goal.intValue() / 60;
                Integer mins = goal.intValue() % 60;
                GoalHours.setText(hrs.toString());
                GoalMinutes.setText(mins.toString());
                GoalProgress.setProgress(progress);
            } else {
                GoalHours.setText("?");
                GoalMinutes.setText("?");
                GoalProgress.setProgress(0.0);
            }
            TopLabel.setText(GOALCUTNAMES[cutindex]);
//            if (cutname.equals(GOALCUTNAMES[10])) { TopLabel.setText("Total Goal");}
//            else if (cutname.equals(GOALCUTNAMES[0])) {TopLabel.setText("Pre + Post Goal");}
//            else {TopLabel.setText(cutname + "'s Current Goal");}
        } catch (NullPointerException ignored) {
            TopLabel.setText(GOALCUTNAMES[cutindex]);
//            if (cutindex != 11) {
//                TopLabel.setText(GOALCUTNAMES[cutindex] + "'s Current Goal");
//            } else {
//                TopLabel.setText(GOALCUTNAMES[cutindex] + " Session Time Current Goal");
//            }
            int practicedminutes = Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected());
            Integer hours = practicedminutes / 60;
            Integer minutes = practicedminutes % 60;
            PracticedHours.setText(hours.toString());
            PracticedMinutes.setText(minutes.toString());
            GoalMinutes.setText("?");
            GoalHours.setText("?");
            GoalProgress.setProgress(0.0);
            Tools.showtimedmessage(StatusBar, "No Current Goal Set For " + GOALCUTNAMES[cutindex], 4000);
        } catch (ArrayIndexOutOfBoundsException ignored) {resetallvalues();}
    }
    public List<kujiin.xml.Goals.Goal> getcurrentgoallist(boolean includecompleted) {
        if (cutindex != null) {return Goals.getallcutgoals(cutindex, includecompleted);}
        else {return null;}
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
    public static class EditGoalsDialog extends Stage {
        public TableView<CurrentGoalBinding> CurrentGoalTable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button CloseButton;
        public TableColumn<CurrentGoalBinding, Boolean> IsCompletedColumn;
        public TableColumn<CurrentGoalBinding, String> CompletionDateColumn;
        public Label TopLabel;
        public CheckBox ShowCompletedCheckBox;
        public Button AddGoalButton;
        public Button RemoveGoalButton;
        public ChoiceBox<String> CutSelectorComboBox;
        private ProgressAndGoalsWidget ProgressAndGoals;
        private List<kujiin.xml.Goals.Goal> CurrentGoalList;
        private kujiin.xml.Goals.Goal SelectedGoal;
        private Integer cutindex;

        public EditGoalsDialog(ProgressAndGoalsWidget progressandgoals) {
            ProgressAndGoals = progressandgoals;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayGoals.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Current Goals");}
            catch (IOException e) {e.printStackTrace();}
            CutSelectorComboBox.setItems(FXCollections.observableArrayList(GOALCUTNAMES));
            populatetable();
        }

    // Button Actions

    // Getters And Setters
        public Integer getCutindex() {
        return cutindex;
    }
        public void setCutindex(Integer cutindex) {
            this.cutindex = cutindex;
        }

    // Cut Selection Methods
        public boolean goalschanged() {
            List<kujiin.xml.Goals.Goal> goalsfromxml = ProgressAndGoals.getGoal().getallcutgoals(ProgressAndGoals.getCutindex(), true);
            return ! CurrentGoalList.containsAll(goalsfromxml) ||  goalsfromxml.size() != CurrentGoalList.size();
        }
        public void changecutselection(ActionEvent actionEvent) {
            if (getCutindex() != null) {
                if (goalschanged()) {
                    if (Tools.getanswerdialog("Confirmation", "You Have Made Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save These Changes Before Changing Cuts?")) {
                        savechanges();
                    }
                }
            }
            int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            if (index == -1) {return;}
            setCutindex(index);
            ProgressAndGoals.selectcut(getCutindex());
            CurrentGoalTable.getItems().clear();
            populatetable();
        }

    // Table Methods
        public void populatetable() {
            try {
                ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
                int count = 1;
                int cutindex = ProgressAndGoals.getCutindex();
                CurrentGoalList = new ArrayList<>();
                String name = ProgressAndGoalsWidget.GOALCUTNAMES[cutindex];
                if (cutindex != 11) {TopLabel.setText(name + "'s Goals");}
                else {TopLabel.setText(name + " Goals");}
                for (kujiin.xml.Goals.Goal i : ProgressAndGoals.getGoal().getallcutgoals(cutindex, ShowCompletedCheckBox.isSelected())) {
                    currentGoals.add(new CurrentGoalBinding(count, Double.toString(i.getGoal_Hours()), i.getDate_Set(),
                            i.getpercentagecompleted(ProgressAndGoals.getSessions().getpracticedtimeinminutesforallsessions(cutindex, false)),
                            i.getCompleted(), i.getDate_Completed()));
                    CurrentGoalList.add(i);
                    count++;
                }
                NumberColumn.setCellValueFactory(cellData -> cellData.getValue().goalid.asObject());
                NumberColumn.setStyle("-fx-alignment: CENTER;");
                GoalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
                GoalTimeColumn.setStyle("-fx-alignment: CENTER;");
                DueDateColumn.setCellValueFactory(cellData -> cellData.getValue().duedate);
                DueDateColumn.setStyle("-fx-alignment: CENTER;");
                PercentCompleteColumn.setCellValueFactory(cellData -> cellData.getValue().percentcomplete);
                PercentCompleteColumn.setStyle("-fx-alignment: CENTER;");
                IsCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().completed);
                IsCompletedColumn.setStyle("-fx-alignment: CENTER;");
                CompletionDateColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
                CompletionDateColumn.setStyle("-fx-alignment: CENTER;");
                CurrentGoalTable.setItems(currentGoals);
            } catch (ArrayIndexOutOfBoundsException ignored) {reset();}
        }
        public void tableselectionchanged(Event event) {
            int index = CurrentGoalTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {SelectedGoal = CurrentGoalList.get(index);}
            RemoveGoalButton.setDisable(index == -1);
        }
        public void completedgoalstoggle(ActionEvent actionEvent) {
            populatetable();
        }
        public void addgoal(ActionEvent actionEvent) {
            ProgressAndGoals.setnewgoal();
            populatetable();}
        public void removegoal(ActionEvent actionEvent) {
            if (SelectedGoal == null) {return;}
            if (! SelectedGoal.getCompleted() && Tools.getanswerdialog("Confirmation", "Remove This Goal?", "This Cannot Be Undone")) {
                CurrentGoalList.remove(CurrentGoalTable.getSelectionModel().getSelectedIndex());
                ProgressAndGoals.getGoal().update(CurrentGoalList, ProgressAndGoals.getCutindex());
            }
        }
        public void savechanges() {
            if (getCutindex() != null && getCutindex() != -1) {
                ProgressAndGoals.getGoal().update(CurrentGoalList, getCutindex());
            }
        }

    // Dialog Methods
        public void closeDialog(Event event) {this.close();}
        public void reset() {
            CurrentGoalTable.getItems().clear();
        }
        @Override
        public void close() {
            if (getCutindex() != null && getCutindex() != -1) {
                if (Tools.getanswerdialog("Confirmation", "Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save Changes Before Exiting?")) {
                    savechanges();
                }
                ProgressAndGoals.selectcut(getCutindex());
            }
            super.close();
        }
    }
    public static class GoalPacingDialog extends Stage implements Initializable {
        public Label SelectedGoalHours;
        public Button SelectADiffferentGoalButton;
        public Spinner<Integer> DaysSpinner;
        public Button CalculateButton;
        public Button CloseButton;
        private kujiin.xml.Goals.Goal currentGoal;
        private List<kujiin.xml.Goals.Goal> currentGoals;
        private double alreadypracticedhours;

        public GoalPacingDialog(kujiin.xml.Goals.Goal currentGoal, List<kujiin.xml.Goals.Goal> currentGoals, double alreadypracticedhours) {
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
        public kujiin.xml.Goals.Goal getCurrentGoal() {
            return currentGoal;
        }
        public void setCurrentGoal(kujiin.xml.Goals.Goal currentGoal) {
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
    public static class SelectGoalDialog extends Stage {
        public TableView<CurrentGoalBinding> currentgoaltable;
        public TableColumn<CurrentGoalBinding, Integer> NumberColumn;
        public TableColumn<CurrentGoalBinding, String> GoalTimeColumn;
        public TableColumn<CurrentGoalBinding, String> DueDateColumn;
        public TableColumn<CurrentGoalBinding, String> PercentCompleteColumn;
        public Button SelectButton;
        public Button CancelButton;
        public kujiin.xml.Goals.Goal selectedgoal;
        private List<kujiin.xml.Goals.Goal> currentGoalList;

        public SelectGoalDialog(List<kujiin.xml.Goals.Goal> currentGoalList, double currentpracticedhours) {
            this.currentGoalList = currentGoalList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {setScene(new Scene(fxmlLoader.load())); this.setTitle("Select A Goal");}
            catch (IOException e) {e.printStackTrace();}
            ObservableList<CurrentGoalBinding> currentGoals = FXCollections.observableArrayList();
//            currentGoals.addAll(currentGoalList.stream().map(i -> new CurrentGoalBinding(currentGoalList.indexOf(i) + 1, Double.toString(i.getGoal_Hours()), i.getDate_Set(), i.getpercentagecompleted(currentpracticedhours))).collect(Collectors.toList()));
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
        public kujiin.xml.Goals.Goal getSelectedgoal() {
            return selectedgoal;
        }
        public void setSelectedgoal(kujiin.xml.Goals.Goal selectedgoal) {
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
        private ProgressAndGoalsWidget progressAndGoalsWidget;
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
                List<kujiin.xml.Goals.Goal> currentgoals = progressAndGoalsWidget.getGoal().getallcutgoals(cutindex, false);
                System.out.println(currentgoals.size());
                int highestgoalminutes = Tools.convertdecimalhourstominutes(progressAndGoalsWidget.getGoal().getgoal(cutindex, currentgoals.size() - 1, false).getGoal_Hours());
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(highestgoalminutes / 60, Integer.MAX_VALUE, highestgoalminutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(highestgoalminutes % 60, 59, highestgoalminutes % 60));
            } catch (NullPointerException ignored) {
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
            }
            if (cutindex != 11) TopLabel.setText("New Goal For " + GOALCUTNAMES[cutindex]);
            else TopLabel.setText("New Total Goal");
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
        public void viewcurrentgoals(Event event) {progressAndGoalsWidget.opengoaleditor();}
    }
    public static class GoalCompleted extends Stage {
        public Label GoalHours;
        public Button CloseButton;
        public Label CurrentHoursLabel;

        public GoalCompleted(kujiin.xml.Goals.Goal currentGoal, Double currentpracticedhours) {
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
        private BooleanProperty completed;
        private StringProperty datecompleted;

        public CurrentGoalBinding(int id, String goalhours, String duedate, String percentcomplete, Boolean completed, String datecompleted) {
            this.goalid = new SimpleIntegerProperty(id);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.duedate = new SimpleStringProperty(duedate);
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
            this.completed = new SimpleBooleanProperty(completed);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }

}
