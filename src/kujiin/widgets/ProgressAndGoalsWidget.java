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
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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
    private MainController Root;

    public ProgressAndGoalsWidget(MainController root) {
        Root = root;
        NewGoalButton = root.newgoalButton;
        CurrentGoalsButton = root.viewcurrrentgoalsButton;
        PracticedHours = root.GoalPracticedHours;
        PracticedHours.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        PracticedMinutes = root.GoalPracticedMinutes;
        PracticedMinutes.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        GoalHours = root.GoalSetHours;
        GoalHours.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        GoalMinutes = root.GoalSetMinutes;
        GoalMinutes.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        GoalProgress = root.goalsprogressbar;
        CutSelectorComboBox = root.GoalCutComboBox;
        TopLabel = root.GoalTopLabel;
        StatusBar = root.GoalStatusBar;
        TotalTimePracticed = root.TotalTimePracticed;
        NumberOfSessionsPracticed = root.NumberOfSessionsPracticed;
        AverageSessionDuration = root.AverageSessionDuration;
        PreAndPostOption = root.PrePostSwitch;
        SessionListButton = root.ListOfSessionsButton;
        Sessions = new Sessions(Root);
        Goals = new Goals(Root);
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
        TotalTimePracticed.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        NumberOfSessionsPracticed.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
        AverageSessionDuration.setOnKeyTyped(Root.NONEDITABLETEXTFIELD);
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
        getgoals.setOnSucceeded(event -> updategoalsui());
        getgoals.setOnFailed(event -> updategoalsui());
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
        if (Sessions.getSession() != null) {new DisplayCutTotalsDialog(Root, Sessions.getSession());}
        else {Tools.showinformationdialog(Root, "Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");}
    }
    public void displaysessionlist() {
        if (Sessions.getSession() == null || Sessions.getSession().size() == 0) {
            Tools.showinformationdialog(Root, "Cannot Display", "Nothing To Display", "Need To Practice At Least One Session To Use This Feature");
        } else {new DisplaySessionListDialog(Root, Sessions.getSession()).showAndWait();}
    }
    public void setnewgoal() {
        if (cutindex == -1) {Tools.showinformationdialog(Root, "Information","No Cut Selected", "Select A Cut To Add A Goal To"); return;}
        SetANewGoalForSingleCut setANewGoalForSingleCutDialog = new SetANewGoalForSingleCut(cutindex, Root);
        setANewGoalForSingleCutDialog.showAndWait();
        if (setANewGoalForSingleCutDialog.isAccepted()) {
            try {
                Goals.add(cutindex, new Goals.Goal(setANewGoalForSingleCutDialog.getGoaldate(), setANewGoalForSingleCutDialog.getGoalhours(), GOALCUTNAMES[cutindex]));}
            catch (JAXBException ignored) {Tools.showerrordialog(Root, "Error", "Couldn't Add Goal", "Check File Permissions");}
        }
        updategoalsui();
    }
    public void opengoaleditor() {
        if (cutindex == -1) {Tools.showinformationdialog(Root, "Information", "No Cut Selected", "Please Select A Cut To Edit Its Goals"); return;}
        if (! Goals.goalsexist(cutindex, true)) {Tools.showinformationdialog(Root, "Information", "No Goals Exist For " + GOALCUTNAMES[cutindex], "Please Add A Goal For " + GOALCUTNAMES[cutindex]); return;}
        new EditGoalsDialog(Root, cutindex).showAndWait();
    }

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
            if (cutindex == -1) {PreAndPostOption.setDisable(true);}
            else {
                int averagesessionduration = Sessions.averagepracticetimeinminutes(cutindex, PreAndPostOption.isSelected());
                int totalminutespracticed = Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected());
                int numberofsessionspracticed = Sessions.cutsessionscount(cutindex);
                boolean sessionsgood = averagesessionduration != 0 || totalminutespracticed != 0 || numberofsessionspracticed != 0;
                String nonetext = "No Sessions";
                if (sessionsgood) {
                    String longtext = Tools.minutestoformattedhoursandmins(averagesessionduration);
                    if (longtext.toCharArray().length <= 14) {AverageSessionDuration.setText(longtext);}
                    else {AverageSessionDuration.setText(Tools.minstoformattedabbreviatedhoursandminutes(averagesessionduration));}
                }
                else {AverageSessionDuration.setText(nonetext);}
                if (sessionsgood) {
                    String longtext = Tools.minutestoformattedhoursandmins(totalminutespracticed);
                    if (longtext.toCharArray().length <= 14) {TotalTimePracticed.setText(longtext);}
                    else {TotalTimePracticed.setText(Tools.minstoformattedabbreviatedhoursandminutes(totalminutespracticed));}
                }
                else {TotalTimePracticed.setText(nonetext);}
                if (sessionsgood) {NumberOfSessionsPracticed.setText(Integer.toString(numberofsessionspracticed));}
                else {NumberOfSessionsPracticed.setText(nonetext);}
            }
        } catch (NullPointerException ignored) {}
    }
    public void selectcut(int cutindex) {
        CutSelectorComboBox.getSelectionModel().select(cutindex);
        setCutindex(cutindex);
    }

// Goal Specific Methods
    public ArrayList<Integer> precreationgoalchecks(ArrayList<Integer> textfieldtimes) {
        ArrayList<Integer> notgoodcuts = new ArrayList<>();
        for (int i = 0; i < textfieldtimes.size(); i++) {
            Integer val = textfieldtimes.get(i);
            if (val != 0 && ! checkifgoalsetandlongenough(i, val)) {
                notgoodcuts.add(i);
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
//            Goals.sortallcompletedgoals();
            NewGoalButton.setDisable(cutindex == -1);
            CurrentGoalsButton.setDisable(cutindex == -1);
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
        } catch (NullPointerException ignored) {
            if (cutindex != -1) {
                TopLabel.setText(GOALCUTNAMES[cutindex]);
                Tools.showtimedmessage(StatusBar, "No Current Goal Set (" + Goals.getcompletedgoalcount(cutindex) + " Completed)", 4000);
                int practicedminutes = Sessions.getpracticedtimeinminutesforallsessions(cutindex, PreAndPostOption.isSelected());
                Integer hours = practicedminutes / 60;
                Integer minutes = practicedminutes % 60;
                PracticedHours.setText(hours.toString());
                PracticedMinutes.setText(minutes.toString());
                GoalMinutes.setText("?");
                GoalHours.setText("?");
            } else {
                PracticedHours.setText("-");
                PracticedMinutes.setText("-");
                GoalMinutes.setText("-");
                GoalHours.setText("-");
            }
            NewGoalButton.setDisable(cutindex == -1);
            CurrentGoalsButton.setDisable(cutindex == -1);
            GoalProgress.setProgress(0.0);
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
        public TableColumn<SessionRow, String> RinColumn;
        public TableColumn<SessionRow, String> KyoColumn;
        public TableColumn<SessionRow, String> TohColumn;
        public TableColumn<SessionRow, String> ShaColumn;
        public TableColumn<SessionRow, String> KaiColumn;
        public TableColumn<SessionRow, String> JinColumn;
        public TableColumn<SessionRow, String> RetsuColumn;
        public TableColumn<SessionRow, String> ZaiColumn;
        public TableColumn<SessionRow, String> ZenColumn;
        public TableColumn<SessionRow, String> TotalColumn;
        public Button CloseButton;
        private ObservableList<SessionRow> sessionlist = FXCollections.observableArrayList();
        private MainController Root;


        public DisplaySessionListDialog(MainController root, List<Session> sessionlist) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplaySessionList.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Session List");
            DateColumn.setCellValueFactory(cellData -> cellData.getValue().datepracticed);
            RinColumn.setCellValueFactory(cellData -> cellData.getValue().rin);
            KyoColumn.setCellValueFactory(cellData -> cellData.getValue().kyo);
            TohColumn.setCellValueFactory(cellData -> cellData.getValue().toh);
            ShaColumn.setCellValueFactory(cellData -> cellData.getValue().sha);
            KaiColumn.setCellValueFactory(cellData -> cellData.getValue().kai);
            JinColumn.setCellValueFactory(cellData -> cellData.getValue().jin);
            RetsuColumn.setCellValueFactory(cellData -> cellData.getValue().retsu);
            ZaiColumn.setCellValueFactory(cellData -> cellData.getValue().zai);
            ZenColumn.setCellValueFactory(cellData -> cellData.getValue().zen);
            TotalColumn.setCellValueFactory(cellData -> cellData.getValue().total);
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
            public StringProperty presession;
            public StringProperty rin;
            public StringProperty kyo;
            public StringProperty toh;
            public StringProperty sha;
            public StringProperty kai;
            public StringProperty jin;
            public StringProperty retsu;
            public StringProperty zai;
            public StringProperty zen;
            public StringProperty postsession;
            public StringProperty total;

            public SessionRow(int id, String datepracticed, int presession, int rin, int kyo, int toh, int sha, int kai, int jin, int retsu, int zai, int zen, int postsession, int total) {
                this.id = new SimpleIntegerProperty(id);
                this.datepracticed = new SimpleStringProperty(datepracticed);
                this.presession = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(presession));
                this.rin = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(rin));
                this.kyo = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(kyo));
                this.toh = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(toh));
                this.sha = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(sha));
                this.kai = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(kai));
                this.jin = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(jin));
                this.retsu = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(retsu));
                this.zai = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(zai));
                this.zen = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(zen));
                this.postsession = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(postsession));
                this.total = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(total));
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
        private MainController Root;
        private List<Session> allsessions;

        public DisplayCutTotalsDialog(MainController root, List<Session> allsessions) {
            Root = root;
            this.allsessions = allsessions;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayCutTotalsDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Cut Totals");
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
                allrows.add(new TotalProgressRow(i, Options.ALLNAMES.get(i), duration));
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
        public Button GoalPacingButton;
        private MainController Root;
        private ProgressAndGoalsWidget ProgressAndGoals;
        private List<kujiin.xml.Goals.Goal> CurrentGoalList;
        private kujiin.xml.Goals.Goal SelectedGoal;
        private Integer cutindex;

        public EditGoalsDialog(MainController root, int cutindex) {
            Root = root;
            ProgressAndGoals = Root.getProgressTracker();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DisplayGoals.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Edit Goals");
            CutSelectorComboBox.setItems(FXCollections.observableArrayList(GOALCUTNAMES));
            populatetable();
            if (cutindex == -1) {TopLabel.setText("Select A Cut");}
            else {CutSelectorComboBox.getSelectionModel().select(cutindex);}
        }

    // Getters And Setters
        public Integer getCutindex() {
        return cutindex;
    }
        public void setCutindex(Integer cutindex) {
            this.cutindex = cutindex;
        }
        public kujiin.xml.Goals.Goal getselectedgoal() {return SelectedGoal;}

    // Cut Selection Methods
        public boolean goalschanged() {
            List<kujiin.xml.Goals.Goal> goalsfromxml = ProgressAndGoals.getGoal().getallcutgoals(ProgressAndGoals.getCutindex(), true);
            System.out.println(CurrentGoalList.containsAll(goalsfromxml));
            System.out.println(goalsfromxml.size() + " Compared To: " + CurrentGoalList.size());
            return ! CurrentGoalList.containsAll(goalsfromxml) ||  goalsfromxml.size() != CurrentGoalList.size();
        }
        public void changecutselection(ActionEvent actionEvent) {
            if (getCutindex() != null) {
                if (goalschanged()) {
                    if (Tools.getanswerdialog(Root, "Confirmation", "You Have Made Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save These Changes Before Changing Cuts?")) {
                        savechanges();
                    }
                }
            }
            int index = CutSelectorComboBox.getSelectionModel().getSelectedIndex();
            if (index == -1) {return;}
            setCutindex(index);
            ProgressAndGoals.selectcut(getCutindex());
            CurrentGoalTable.getItems().clear();
            RemoveGoalButton.setDisable(true);
            GoalPacingButton.setDisable(true);
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
            GoalPacingButton.setDisable(index == -1 || SelectedGoal == null || SelectedGoal.getCompleted());
        }
        public void completedgoalstoggle(ActionEvent actionEvent) {
            populatetable();
        }
        public void addgoal(ActionEvent actionEvent) {
            ProgressAndGoals.setnewgoal();
            populatetable();}
        public void removegoal(ActionEvent actionEvent) {
            if (SelectedGoal == null) {return;}
            if (! SelectedGoal.getCompleted() && Tools.getanswerdialog(Root, "Confirmation", "Remove This Goal?", "This Cannot Be Undone")) {
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
                if (Tools.getanswerdialog(Root, "Confirmation", "Unsaved Changes To " + GOALCUTNAMES[getCutindex()], "Save Changes Before Exiting?")) {
                    savechanges();
                }
                ProgressAndGoals.selectcut(getCutindex());
            }
            super.close();
        }

    // Goal pacing
        public void goalpacing(ActionEvent actionEvent) {
            if (getselectedgoal() != null && CurrentGoalList != null && getCutindex() != null) {
                new GoalPacingDialog(Root, getselectedgoal(), CurrentGoalList, Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(cutindex, false)).showAndWait();
            }
        }
    }
    public static class GoalPacingDialog extends Stage {
        public Spinner<Integer> PracticeDays;
        public TextField PracticeTimeADay;
        public TextField GoalDuration;
        public TextField GoalDueDate;
        public TextField GoalDaysTillDue;
        public Button ExtendDueDateButton;
        public Button CloseButton;
        public TextField GoalTimeLeft;
        public TextField PracticeTimeDaysAWeek;
        public TextField TotalPracticedTime;
        private MainController Root;
        private Goals.Goal CurrentGoal;
        private List<Goals.Goal> CurrentGoals;

        public GoalPacingDialog(MainController root, kujiin.xml.Goals.Goal currentGoal, List<kujiin.xml.Goals.Goal> currentGoals, int cutindex) {
            Root = root;
            CurrentGoal = currentGoal;
            CurrentGoals = currentGoals;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalPacingDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Goal Pacing");
            int practicedminutes = Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(cutindex, false);
            int goalminutes = Tools.convertdecimalhourstominutes(currentGoal.getGoal_Hours());
            GoalDuration.setText(Tools.minstoformattedabbreviatedhoursandminutes(goalminutes));
            GoalDueDate.setText(CurrentGoal.getDate_Due());
            TotalPracticedTime.setText(Tools.minstoformattedabbreviatedhoursandminutes(practicedminutes));
            int minutesleft = goalminutes - practicedminutes;
            GoalTimeLeft.setText(Tools.minstoformattedabbreviatedhoursandminutes(minutesleft));
            LocalDate datedue = Tools.converttolocaldate(CurrentGoal.getDate_Due());
            int daystilldue = Period.between(LocalDate.now(), datedue).getDays();
            System.out.println("Days Till Due " + daystilldue);
            if (daystilldue >= 0) {
                GoalDaysTillDue.setText(String.format("%s Days", daystilldue));
                PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, daystilldue, daystilldue));
                calculate();
                PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> {
                    calculate();
                });
            } else {
                Tools.showinformationdialog(Root, "Goal Is Overdue", "Cannot Calculate Goal Pacing For A Goal That Is Past Due", "Set A New Due Date To Use This Feature");
                if (! extendduedate(null)) {close();}
                else {
                    datedue = Tools.converttolocaldate(CurrentGoal.getDate_Due());
                    daystilldue = Period.between(LocalDate.now(), datedue).getDays();
                    GoalDaysTillDue.setText(String.format("%s Days", daystilldue));
                    PracticeDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, daystilldue, daystilldue));
                    calculate();
                    PracticeDays.valueProperty().addListener((observable, oldValue, newValue) -> {
                        calculate();
                    });
                }
            }
        }

    // Button Actions
        public void closedialog(ActionEvent actionEvent) {close();}
        public boolean extendduedate(ActionEvent actionEvent) {
            DatePickerDialog dpd = new DatePickerDialog(Root, "Select A New Due Date", "Select A New Due Date", LocalDate.now());
            dpd.showAndWait();
            if (dpd.getDate() != null) {
                if (Tools.getanswerdialog(Root, "Confirmation", "This Will Postpone This Goal's Due Date To " + dpd.getDate().toString(), "Really Postpone?")) {
                    CurrentGoal.setDate_Due(Tools.convertfromlocaldatetostring(dpd.getDate()));
                    return true;
                } else {Tools.showinformationdialog(Root, "Information", "Extend Due Date Cancelled", "This Goal's Due Date Was Not Extended"); return false;}
            } else {return false;}
        }

    // Other Methods
        public void selectanewgoal(ActionEvent actionEvent) {
//            SelectGoalDialog selectGoalDialog = new SelectGoalDialog(Root, currentGoals, alreadypracticedhours);
//            selectGoalDialog.showAndWait();
//            setCurrentGoal(selectGoalDialog.getSelectedgoal());
//            if (getCurrentGoal() == null) {return;}
//            SelectedGoalHours.setText(getCurrentGoal().getGoal_Hours() + " Hours");
        }
        public void calculate() {
            System.out.println("Calculating");
            Double goalhours = CurrentGoal.getGoal_Hours();
            Double days = (double) PracticeDays.getValue();
            Float hourstopractice = goalhours.floatValue() / days.floatValue();
            int minsaday = Tools.convertdecimalhourstominutes(hourstopractice.doubleValue());
            String formattedgoalhours = Tools.minstoformattedabbreviatedhoursandminutes(minsaday);
            PracticeTimeADay.setText(formattedgoalhours);
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
        public kujiin.xml.Goals.Goal selectedgoal;
        private MainController Root;
        private List<kujiin.xml.Goals.Goal> currentGoalList;

        public SelectGoalDialog(MainController root, List<kujiin.xml.Goals.Goal> currentGoalList, double currentpracticedhours) {
            Root = root;
            this.currentGoalList = currentGoalList;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SelectGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Select Goal");
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
            if (index == -1) {Tools.showinformationdialog(Root, "Information", "No Goal Selected", "Select A Goal"); return;}
            setSelectedgoal(currentGoalList.get(index));
            close();
        }
    }
    public static class SetANewGoalForSingleCut extends Stage {
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
        private MainController Root;
        private int practicedminutes;
        private int goalminutes;

        public SetANewGoalForSingleCut(int cutindex, MainController root) {
            Root = root;
            progressAndGoalsWidget = Root.getProgressTracker();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetNewGoalDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Set A New Goal");
            GoalHoursSpinner.setEditable(true);
            GoalMinutesSpinner.setEditable(true);
            GoalDatePicker.setValue(LocalDate.now());
            practicedminutes = Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(cutindex, false);
            try {
                List<kujiin.xml.Goals.Goal> currentgoals = progressAndGoalsWidget.getGoal().getallcutgoals(cutindex, false);
                System.out.println(currentgoals.size());
                goalminutes = Tools.convertdecimalhourstominutes(progressAndGoalsWidget.getGoal().getgoal(cutindex, currentgoals.size() - 1, false).getGoal_Hours());
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(goalminutes / 60, Integer.MAX_VALUE, goalminutes / 60));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, goalminutes % 60));
            } catch (NullPointerException ignored) {
                goalminutes = 0;
                int hr = practicedminutes / 60;
                int min = practicedminutes % 60;
                GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(hr, Integer.MAX_VALUE, hr));
                GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, min));
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
            int thisminutes = (GoalHoursSpinner.getValue() * 60) + GoalMinutesSpinner.getValue();
            if (thisminutes <= practicedminutes) {
                Tools.showinformationdialog(Root, "Cannot Set Goal", "Goal Time Must Be Higher Than Practiced Time " + Tools.minstoformattedlonghoursandminutes(practicedminutes), "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            if (thisminutes > goalminutes) {
                Tools.showinformationdialog(Root, "Cannot Set Goal", "Goal Time Must Be Higher Than Practiced Time " + Tools.minstoformattedlonghoursandminutes(practicedminutes), "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            if (! GoalDatePicker.getValue().isAfter(LocalDate.now())) {
                Tools.showinformationdialog(Root, "Cannot Set Goal",  "Due Date Must Be After Today", "Cannot Set Goal");
                setGoalhours(null);
                setGoaldate(null);
                return;
            }
            int hours = GoalHoursSpinner.getValue();
            int minutes = GoalMinutesSpinner.getValue();
            double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
            setGoalhours(newhours);
            setGoaldate(GoalDatePicker.getValue());
            super.close();
        }
        public void viewcurrentgoals(Event event) {progressAndGoalsWidget.opengoaleditor();}
    }
    public static class SetANewGoalForMultipleCuts extends Stage {
        public ToggleButton Presession;
        public ToggleButton RIN;
        public ToggleButton KYO;
        public ToggleButton TOH;
        public ToggleButton SHA;
        public ToggleButton KAI;
        public ToggleButton JIN;
        public ToggleButton RETSU;
        public ToggleButton ZAI;
        public ToggleButton ZEN;
        public ToggleButton Postsession;
        private ProgressAndGoalsWidget progressAndGoalsWidget;
        private MainController Root;
        public Spinner<Integer> GoalHoursSpinner;
        public Spinner<Integer> GoalMinutesSpinner;
        public DatePicker GoalDatePicker;
        public Button CancelButton;
        public Button OKButton;
        public Button CurrentGoalsButton;
        public Label TopLabel;
        private LocalDate goaldate;
        private Double goalhours;

        public SetANewGoalForMultipleCuts(MainController root, ArrayList<Integer> cutindexes, int expectedminutes) {
            Root = root;
            progressAndGoalsWidget = Root.getProgressTracker();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/SetMultipleGoalsDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Set A Goal For Multiple Cuts");
            GoalHoursSpinner.setEditable(true);
            GoalMinutesSpinner.setEditable(true);
            GoalDatePicker.setValue(LocalDate.now());
            GoalHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(expectedminutes / 60, Integer.MAX_VALUE, expectedminutes / 60));
            GoalMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(expectedminutes % 60, 59, expectedminutes % 60));
            for (Integer i : cutindexes) {select(i, true);}
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
        public boolean isAccepted() {return getGoalhours() != null && getGoaldate() != null && ! getSelectedCutIndexes().isEmpty();}
        public List<Integer> getSelectedCutIndexes() {
            List<Integer> selectedcuts = new ArrayList<>();
            for (int i = 0; i<=10; i++) {if (getselected(i)) {selectedcuts.add(i);}}
            return selectedcuts;
        }
        public void select(int index, boolean value) {
            if (index == 0) {Presession.setSelected(value);}
            if (index == 1) {RIN.setSelected(value);}
            if (index == 2) {KYO.setSelected(value);}
            if (index == 3) {TOH.setSelected(value);}
            if (index == 4) {SHA.setSelected(value);}
            if (index == 5) {KAI.setSelected(value);}
            if (index == 6) {JIN.setSelected(value);}
            if (index == 7) {RETSU.setSelected(value);}
            if (index == 8) {ZAI.setSelected(value);}
            if (index == 9) {ZEN.setSelected(value);}
            if (index == 10) {Postsession.setSelected(value);}
        }
        public boolean getselected(int index) {
            if (index == 0) {return Presession.isSelected();}
            if (index == 1) {return RIN.isSelected();}
            if (index == 2) {return KYO.isSelected();}
            if (index == 3) {return TOH.isSelected();}
            if (index == 4) {return SHA.isSelected();}
            if (index == 5) {return KAI.isSelected();}
            if (index == 6) {return JIN.isSelected();}
            if (index == 7) {return RETSU.isSelected();}
            if (index == 8) {return ZAI.isSelected();}
            if (index == 9) {return ZEN.isSelected();}
            if (index == 10) {return Postsession.isSelected();}
            return false;
        }

    // Button Actions
        public void viewcurrentgoals(Event event) {
            progressAndGoalsWidget.opengoaleditor();
        }
        public void Accept(Event event) {
            if (getSelectedCutIndexes().isEmpty()) {Tools.showinformationdialog(Root, "Information", "Cannot Add Goal", "No Cuts Selected"); return;}
            if (GoalMinutesSpinner.getValue() > 59) {
                Tools.showinformationdialog(Root, "Information", "Minutes Cannot Be Greater Than 59", "Select A Value Less Than 59"); return;}
            boolean dategood = GoalDatePicker.getValue().isAfter(LocalDate.now());
            if (dategood) {
                int hours = GoalHoursSpinner.getValue();
                int minutes = GoalMinutesSpinner.getValue();
                double newhours = Tools.hoursandminutestoformatteddecimalhours(hours, minutes);
                setGoalhours(newhours);
                setGoaldate(GoalDatePicker.getValue());
                super.close();
            } else {
                Tools.showinformationdialog(Root, "Cannot Set Goal", "Cannot Set Goal", "Due Date Must Be After Today");
                setGoalhours(null);
                setGoaldate(null);
            }
        }
        public void cancelgoalsetting(Event event) {
            this.close();
        }

    }
    public static class SingleGoalCompletedDialog extends Stage {
        public Label GoalHours;
        public Button CloseButton;
        public Label CurrentHoursLabel;
        public Label TopLabel;
        private MainController Root;

        public SingleGoalCompletedDialog(MainController root, kujiin.xml.Goals.Goal currentGoal, Double currentpracticedhours) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalCompleted.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle("Goal Achieved");
            TopLabel.setText(currentGoal.getCutName() + " Goal Achieved");
            GoalHours.setText(currentGoal.getGoal_Hours().toString());
            CurrentHoursLabel.setText("Practiced Hours: " + currentpracticedhours.toString());
            CloseButton.setOnAction(event -> close());
        }
    }
    public static class MultipleGoalsCompletedDialog extends Stage implements Initializable {
        public Label TopLabel;
        public TableView<CompletedGoalsAtEndOfSessionBinding> GoalsCompletedTable;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> CutNameColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> CurrentHoursColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> GoalHoursColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> DateSetColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, Integer> DaysTakenColumn;
        public TableColumn<CompletedGoalsAtEndOfSessionBinding, String> DateCompletedColumn;
        public Button CloseButton;
        private MainController Root;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            CutNameColumn.setCellValueFactory(cellData -> cellData.getValue().cutname);
            GoalHoursColumn.setCellValueFactory(cellData -> cellData.getValue().goalhours);
            DateSetColumn.setCellValueFactory(cellData -> cellData.getValue().dateset);
            DaysTakenColumn.setCellValueFactory(cellData -> cellData.getValue().daysittooktocomplete.asObject());
            DateCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().datecompleted);
            CurrentHoursColumn.setCellValueFactory(cellData -> cellData.getValue().practicedhours);
        }

        public MultipleGoalsCompletedDialog(MainController root, List<kujiin.xml.Goals.Goal> completedgoals) {
            Root = root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/GoalsCompleted.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(completedgoals.size() + " Goals Achieved");
            TopLabel.setText("You Completed " + completedgoals.size() + " Goals This Session");
            ObservableList<CompletedGoalsAtEndOfSessionBinding> newcompletedgoals = FXCollections.observableArrayList();
            for (kujiin.xml.Goals.Goal i : completedgoals) {
                String cutname = i.getCutName();
                int cutindex = new ArrayList<>(Arrays.asList(ProgressAndGoalsWidget.GOALCUTNAMES)).indexOf(cutname);
                String practicedhours = Double.toString(Tools.convertminutestodecimalhours(Root.getProgressTracker().getSessions().getpracticedtimeinminutesforallsessions(cutindex, false), 2));
                String goalhours = i.getGoal_Hours().toString();
                String dateset = i.getDate_Set();
                Integer daystaken = (int) ChronoUnit.DAYS.between(Tools.converttolocaldate(i.getDate_Set()), Tools.converttolocaldate(i.getDate_Due()));
                String datecompleted = i.getDate_Completed();
                newcompletedgoals.add(new CompletedGoalsAtEndOfSessionBinding(cutname, practicedhours, goalhours, dateset, daystaken, datecompleted));
            }
            GoalsCompletedTable.setItems(newcompletedgoals);
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
            this.goalhours = new SimpleStringProperty(Tools.minstoformattedabbreviatedhoursandminutes(Tools.convertdecimalhourstominutes(new Double(goalhours))));
            this.duedate = new SimpleStringProperty(Tools.checkifdateoverdue(duedate));
            this.percentcomplete = new SimpleStringProperty(percentcomplete);
            this.completed = new SimpleBooleanProperty(completed);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }
    public static class CompletedGoalsAtEndOfSessionBinding {
        private StringProperty cutname;
        private StringProperty practicedhours;
        private StringProperty goalhours;
        private StringProperty dateset;
        private IntegerProperty daysittooktocomplete;
        private StringProperty datecompleted;

        public CompletedGoalsAtEndOfSessionBinding(String cutname, String practicedhours, String goalhours, String dateset, int daysittooktocomplete, String datecompleted) {
            this.cutname = new SimpleStringProperty(cutname);
            this.practicedhours = new SimpleStringProperty(practicedhours);
            this.goalhours = new SimpleStringProperty(goalhours);
            this.dateset = new SimpleStringProperty(dateset);
            this.daysittooktocomplete = new SimpleIntegerProperty(daysittooktocomplete);
            this.datecompleted = new SimpleStringProperty(datecompleted);
        }
    }
    public static class DatePickerDialog extends Stage {
        public Label TopLabel;
        public DatePicker Date;
        public Button AcceptButton;
        public Button CloseButton;
        private LocalDate date;
        private MainController Root;
        private LocalDate MustBeAfterDate;

        public DatePickerDialog(MainController root, String titletext, String TopLabelText, LocalDate minimumdate) {
            Root = root;
            MustBeAfterDate = minimumdate;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../assets/fxml/DatePickerDialog.fxml"));
            fxmlLoader.setController(this);
            try {
                Scene defaultscene = new Scene(fxmlLoader.load());
                setScene(defaultscene);
                Root.getOptions().setStyle(defaultscene);
            } catch (IOException e) {new MainController.ExceptionDialog(Root, e).showAndWait();}
            setTitle(titletext);
            TopLabel.setText(TopLabelText);
            Date.setValue(LocalDate.now());
        }

        public LocalDate getDate() {
            return date;
        }
        public void setDate(LocalDate date) {
            this.date = date;
        }
        public void accept(ActionEvent actionEvent) {
            if (Date.getValue().isAfter(MustBeAfterDate) || Date.getValue().isEqual(MustBeAfterDate)) {
                setDate(Date.getValue());
                close();
            } else {Tools.showinformationdialog(Root, "Information", "Goal Date Must Be After " + Tools.convertfromlocaldatetostring(MustBeAfterDate), "Select A Later Date");}
        }
        public void cancel(ActionEvent actionEvent) {
            close();
        }
    }

}
